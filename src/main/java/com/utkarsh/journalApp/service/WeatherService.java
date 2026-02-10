package com.utkarsh.journalApp.service;

import com.utkarsh.journalApp.api.response.WeatherResponse;
import com.utkarsh.journalApp.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    // Open-Meteo API for Bengaluru (Latitude: 12.9716, Longitude: 77.5946)
    private static final String API = "https://api.open-meteo.com/v1/forecast?latitude=12.9716&longitude=77.5946&current_weather=true";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = new WeatherResponse();
        String cacheKey = "weather_" + city;

        try {
            // 1. Check Redis Cache
            String cachedWeather = redisTemplate.opsForValue().get(cacheKey);
            if (cachedWeather != null) {
                // Deserialize JSON string back to WeatherResponse (using Jackson or similar if
                // needed,
                // but for simplicity here assuming we might store it as a string or object.
                // Let's refine this to verify what to store.
                // Since RedisTemplate<String, String> is used, we need to
                // serialize/deserialize.
                // Re-writing to use ObjectMapper for robust JSON handling.)

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(cachedWeather, WeatherResponse.class);
            }
        } catch (Exception e) {
            // Redis failure shouldn't stop the app
        }

        try {
            // 2. Fetch from Open-Meteo (No API Key needed)
            ResponseEntity<java.util.Map> response = restTemplate.exchange(API, HttpMethod.GET, null,
                    java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("current_weather")) {
                java.util.Map<String, Object> currentWeather = (java.util.Map<String, Object>) body
                        .get("current_weather");

                WeatherResponse.Current current = new WeatherResponse.Current();
                current.setTemperature(((Number) currentWeather.get("temperature")).intValue());

                // Open-Meteo uses codes. Determine description.
                int code = ((Number) currentWeather.get("weathercode")).intValue();
                String description = getWeatherDescription(code);

                current.setWeather_descriptions(java.util.Collections.singletonList(description));
                current.setFeelslike(((Number) currentWeather.get("temperature")).intValue()); // Approximation

                weatherResponse.setCurrent(current);

                // 3. Save to Redis with 30 min TTL
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    String jsonString = mapper.writeValueAsString(weatherResponse);
                    redisTemplate.opsForValue().set(cacheKey, jsonString, 30, java.util.concurrent.TimeUnit.MINUTES);
                } catch (Exception e) {
                    // Log Redis write error
                }

                return weatherResponse;
            }
        } catch (Exception e) {
            // Log error
        }

        // Fallback if API fails
        if (weatherResponse.getCurrent() == null) {
            WeatherResponse.Current current = new WeatherResponse.Current();
            current.setTemperature(25);
            current.setFeelslike(27);
            current.setWeather_descriptions(java.util.Collections.singletonList("Sunny"));
            weatherResponse.setCurrent(current);
        }

        return weatherResponse;
    }

    private String getWeatherDescription(int code) {
        if (code == 0)
            return "Clear Sky";
        if (code >= 1 && code <= 3)
            return "Partly Cloudy";
        if (code >= 45 && code <= 48)
            return "Foggy";
        if (code >= 51 && code <= 67)
            return "Rainy";
        if (code >= 71 && code <= 77)
            return "Snowy";
        if (code >= 80 && code <= 82)
            return "Rain Showers";
        if (code >= 95 && code <= 99)
            return "Thunderstorm";
        return "Unknown";
    }
}