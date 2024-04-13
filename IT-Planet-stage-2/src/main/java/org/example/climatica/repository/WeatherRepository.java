package org.example.climatica.repository;

import org.example.climatica.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
}
