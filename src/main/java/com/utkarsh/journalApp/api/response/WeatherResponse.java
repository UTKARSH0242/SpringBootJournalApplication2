package com.utkarsh.journalApp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class WeatherResponse {
    public Current current;

    @Getter
    @Setter
    public class Current {
//        @JsonProperty("observation_time")
//        public String observationTime;
//        public int temperature;
//        @JsonProperty("weather_code")
//        public int weatherCode;
//        @JsonProperty("weather_icons")
//        public ArrayList<String> weatherIcons;
//        @JsonProperty("weather_descriptions")
//        public ArrayList<String> weatherDescriptions;
//        public Astro astro;
//        @JsonProperty("air_quality")
//        public AirQuality airQuality;
//        @JsonProperty("wind_speed")
//        public int windSpeed;
//        @JsonProperty("wind_degree")
//        public int windDegree;
//        @JsonProperty("wind_dir")
//        public String windDir;
//        public int pressure;
//        public double precip;
//        public int humidity;
//        public int cloudcover;
        public int feelslike;
//        @JsonProperty("uv_index")
//        public int uvIndex;
//        public int visibility;
//        @JsonProperty("is_day")
//        public String isDay;
    }

}