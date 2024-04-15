package org.example.climatica.weather_forecast;

import org.example.climatica.weather_forecast.dto.WeatherForecastDto;
import org.example.climatica.model.WeatherForecast;
import org.example.climatica.weather_forecast.dto.CreateWeatherForecastDto;
import org.example.climatica.weather_forecast.dto.UpdateWeatherForecastDto;
import org.springframework.stereotype.Service;

@Service
public class WeatherForecastService {

    private final WeatherForecastRepository repository;

    public WeatherForecastService(WeatherForecastRepository repository) {
        this.repository = repository;
    }

    public WeatherForecastDto getForecastById(Long id) {
        return repository.findById(id).map(this::convertToDto).orElse(null);
    }

    public WeatherForecastDto updateForecast(Long id, UpdateWeatherForecastDto updateDto) {
        return repository.findById(id).map(forecast -> {
            forecast.setTemperature(updateDto.getTemperature());
            forecast.setWeatherCondition(updateDto.getWeatherCondition());
            forecast.setDateTime(updateDto.getDateTime());
            return convertToDto(repository.save(forecast));
        }).orElse(null);
    }

    public WeatherForecastDto createForecast(CreateWeatherForecastDto createDto) {
        WeatherForecast forecast = new WeatherForecast();
        forecast.setRegionId(createDto.getRegionId());
        forecast.setTemperature(createDto.getTemperature());
        forecast.setWeatherCondition(createDto.getWeatherCondition());
        forecast.setDateTime(createDto.getDateTime());
        return convertToDto(repository.save(forecast));
    }

    public boolean deleteForecast(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private WeatherForecastDto convertToDto(WeatherForecast forecast) {
        WeatherForecastDto dto = new WeatherForecastDto();
        dto.setId(forecast.getId());
        dto.setTemperature(forecast.getTemperature());
        dto.setWeatherCondition(forecast.getWeatherCondition());
        dto.setDateTime(forecast.getDateTime());
        dto.setRegionId(forecast.getRegionId());
        return dto;
    }
}
