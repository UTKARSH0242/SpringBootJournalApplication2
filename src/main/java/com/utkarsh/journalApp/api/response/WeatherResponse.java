package com.utkarsh.journalApp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class WeatherResponse {
    public Current current;
    private String locationName;

    @Getter
    @Setter
    public static class Current {
        private int temperature;
        @JsonProperty("weather_descriptions")
        private java.util.List<String> weather_descriptions;
        private int feelslike;
    }

}