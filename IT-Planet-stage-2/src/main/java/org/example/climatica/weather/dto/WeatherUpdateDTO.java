package org.example.climatica.weather.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WeatherUpdateDTO {
    private String regionName;
    private Float temperature;
    private Float humidity;
    private Float windSpeed;
    private String weatherCondition;
    private Float precipitationAmount;
    private LocalDateTime measurementDateTime;
    private List<Long> weatherForecast;
}
