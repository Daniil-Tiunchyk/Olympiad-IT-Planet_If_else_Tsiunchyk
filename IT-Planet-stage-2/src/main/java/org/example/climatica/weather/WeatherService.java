package org.example.climatica.weather;

import org.example.climatica.exception.InvalidParametersException;
import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.Region;
import org.example.climatica.model.WeatherCondition;
import org.example.climatica.model.WeatherData;
import org.example.climatica.region.RegionRepository;
import org.example.climatica.weather.dto.WeatherDataCreateRequest;
import org.example.climatica.weather.dto.WeatherUpdateDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final RegionRepository regionRepository;

    public WeatherService(WeatherRepository weatherRepository, RegionRepository regionRepository) {
        this.weatherRepository = weatherRepository;
        this.regionRepository = regionRepository;
    }

    public WeatherData getWeatherByRegionId(Long regionId) throws UnauthorizedException, NotFoundException {
        return weatherRepository.findByRegionId(regionId)
                .orElseThrow(() -> new NotFoundException("Region not found"));
    }

    public List<WeatherData> searchWeather(String startDateTime, String endDateTime, Long regionId, String weatherCondition, int page, int size) {
        if (regionId != null && regionId <= 0)
            throw new IllegalArgumentException("Invalid regionId. It must be greater than 0.");
        if (weatherCondition != null && !Arrays.asList("CLEAR", "CLOUDY", "RAIN", "SNOW", "FOG", "STORM").contains(weatherCondition))
            throw new IllegalArgumentException("Invalid weather condition.");

        LocalDateTime start = startDateTime != null ? LocalDateTime.parse(startDateTime) : LocalDateTime.MIN;
        LocalDateTime end = endDateTime != null ? LocalDateTime.parse(endDateTime) : LocalDateTime.MAX;
        Pageable pageable = PageRequest.of(page, size);

        return weatherRepository.findByRegionIdAndMeasurementDateTimeBetweenAndWeatherCondition(regionId, start, end, WeatherCondition.valueOf(weatherCondition), pageable).getContent();
    }

    public WeatherData addWeather(WeatherData weatherData) throws InvalidParametersException, UnauthorizedException, NotFoundException {
        if (weatherData.getRegion() == null) {
            throw new NotFoundException("Region is required");
        }
        return weatherRepository.save(weatherData);
    }


    public WeatherData updateWeatherAndRegion(Long regionId, WeatherUpdateDTO dto) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new NotFoundException("Region with ID " + regionId + " not found"));

        region.setName(dto.getRegionName());
        regionRepository.save(region);

        WeatherData existingWeather = weatherRepository.findByRegionId(regionId)
                .orElseThrow(() -> new NotFoundException("Weather data for region ID " + regionId + " not found"));
        updateRegionName(regionId, dto.getRegionName());
        existingWeather.setTemperature(dto.getTemperature());
        existingWeather.setHumidity(dto.getHumidity());
        existingWeather.setWindSpeed(dto.getWindSpeed());
        existingWeather.setWeatherCondition(WeatherCondition.valueOf(dto.getWeatherCondition()));
        existingWeather.setPrecipitationAmount(dto.getPrecipitationAmount());
        existingWeather.setMeasurementDateTime(dto.getMeasurementDateTime());
        return weatherRepository.save(existingWeather);
    }

    public void updateRegionName(Long regionId, String newName) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new NotFoundException("Region with ID " + regionId + " not found"));
        region.setName(newName);
        regionRepository.save(region);
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

    WeatherData convertToEntity(WeatherDataCreateRequest request) throws NotFoundException {
        WeatherData weatherData = new WeatherData();

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new NotFoundException("Region with ID: " + request.getRegionId() + " not found"));
        weatherData.setRegion(region);

        weatherData.setTemperature(request.getTemperature());
        weatherData.setHumidity(request.getHumidity());
        weatherData.setWindSpeed(request.getWindSpeed());
        weatherData.setWeatherCondition(WeatherCondition.valueOf(request.getWeatherCondition()));
        weatherData.setPrecipitationAmount(request.getPrecipitationAmount());
        weatherData.setMeasurementDateTime(request.getMeasurementDateTime());
        weatherData.setWeatherForecast(request.getWeatherForecast());

        return weatherData;
    }
}
