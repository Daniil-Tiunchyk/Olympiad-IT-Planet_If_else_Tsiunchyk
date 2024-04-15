package org.example.climatica.weather;

import org.example.climatica.model.WeatherCondition;
import org.example.climatica.model.WeatherData;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByRegionId(Long regionId);

    Page<WeatherData> findByRegionIdAndMeasurementDateTimeBetweenAndWeatherCondition(
            Long regionId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            WeatherCondition weatherCondition,
            Pageable pageable
    );

}
