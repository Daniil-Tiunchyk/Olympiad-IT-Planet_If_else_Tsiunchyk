package org.example.climatica.weather_forecast.dto;

import lombok.Data;
import org.example.climatica.model.WeatherCondition;

import java.time.LocalDateTime;

@Data
public class UpdateWeatherForecastDto {
    private Float temperature;
    private WeatherCondition weatherCondition;
    private LocalDateTime dateTime;
}
