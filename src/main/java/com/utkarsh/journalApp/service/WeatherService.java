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
    private static final String API = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city) {

        String finalUrl = API.replace("CITY", city).replace("API_KEY", apiKey);
        ResponseEntity<WeatherResponse> weatherResponse = restTemplate.exchange(finalUrl, HttpMethod.POST, null, WeatherResponse.class);
        return weatherResponse.getBody();
    }
}