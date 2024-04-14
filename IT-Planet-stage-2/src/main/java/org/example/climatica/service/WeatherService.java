package org.example.climatica.service;

import org.example.climatica.exception.InvalidParametersException;
import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.WeatherData;
import org.example.climatica.repository.WeatherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public WeatherData getWeatherByRegionId(Long regionId) throws UnauthorizedException, NotFoundException {
        return weatherRepository.findById(regionId)
                .orElseThrow(() -> new NotFoundException("Region not found"));
    }

    public List<WeatherData> searchWeather(String startDateTime, String endDateTime, Long regionId, String weatherCondition, int form, int size) {
        //todo:    return weatherRepository.searchWeather(startDateTime, endDateTime, regionId, weatherCondition, form, size);
        return null;
    }

    public WeatherData addWeather(WeatherData weatherData) throws InvalidParametersException, UnauthorizedException, NotFoundException {
        return weatherRepository.save(weatherData);
    }

    public WeatherData updateWeather(Long regionId, WeatherData weatherData) throws UnauthorizedException, NotFoundException {
        WeatherData existingWeather = getWeatherByRegionId(regionId);
        //todo: existingWeather.updateWithNewData(weatherData); // Метод обновления данных в модели
        return weatherRepository.save(existingWeather);
    }

    public void deleteWeatherByRegionId(Long regionId) throws UnauthorizedException, NotFoundException {
        WeatherData weather = getWeatherByRegionId(regionId);
        weatherRepository.delete(weather);
    }

    public WeatherData addWeather(Long regionId, Long weatherId, WeatherData weatherData) throws UnauthorizedException, NotFoundException {
        WeatherData existingWeather = getWeatherByRegionId(regionId);
        //todo: existingWeather.mergeWith(weatherData); // Метод для комбинирования данных
        return weatherRepository.save(existingWeather);
    }

    public void deleteWeather(Long regionId, Long weatherId) throws UnauthorizedException, NotFoundException {
        WeatherData weather = getWeatherByRegionId(weatherId);
        weatherRepository.delete(weather);
    }
}
