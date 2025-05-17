package com.utkarsh.journalApp.service;

import com.utkarsh.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
    private static final String API_KEY = "ea814d33c2ab270b5ef17f34e000dceb";
    private static final String API_URL = "https://api.weatherstack.com/current?access_key=" + API_KEY + "&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse  getWeather(String city) {
        String finalUrl = API_URL.replace("CITY", city);
        ResponseEntity<WeatherResponse> weatherResponse = restTemplate.exchange(finalUrl, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body = weatherResponse.getBody();
        return body;
    }
}