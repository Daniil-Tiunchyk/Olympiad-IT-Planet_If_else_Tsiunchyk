package org.example.climatica.weather.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WeatherDataCreateRequest {
    private Long regionId;
    private float temperature;
    private float humidity;
    private float windSpeed;
    private String weatherCondition;
    private float precipitationAmount;
    private LocalDateTime measurementDateTime;
    private List<Long> weatherForecast;
}
