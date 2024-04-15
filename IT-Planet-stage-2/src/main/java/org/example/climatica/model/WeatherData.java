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

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    private float temperature;
    private float humidity;
    private float windSpeed;
    private WeatherCondition weatherCondition;
    private float precipitationAmount;
    private LocalDateTime measurementDateTime;

    @ElementCollection
    private List<Long> weatherForecast;
}

