package org.example.climatica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long regionId;
    private String regionName;
    private float temperature;
    private float humidity;
    private float windSpeed;
    private String weatherCondition;
    private float precipitationAmount;
    private LocalDateTime measurementDateTime;
    @ElementCollection
    private List<Long> weatherForecast;
}
