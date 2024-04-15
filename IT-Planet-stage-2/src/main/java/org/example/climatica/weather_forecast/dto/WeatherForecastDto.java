package org.example.climatica.weather_forecast.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherForecastDto {
    private Long id;
    private LocalDateTime dateTime;
    private Float temperature;
    private String weatherCondition;
    private Long regionId;
}
