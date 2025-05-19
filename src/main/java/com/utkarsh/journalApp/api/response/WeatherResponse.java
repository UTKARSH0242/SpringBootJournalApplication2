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
        public int feelslike;
    }

}