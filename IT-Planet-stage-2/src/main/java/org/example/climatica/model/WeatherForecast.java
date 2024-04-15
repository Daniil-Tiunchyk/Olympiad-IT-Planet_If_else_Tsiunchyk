package org.example.climatica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;
    private Float temperature;

    @Enumerated(EnumType.STRING)
    private WeatherCondition weatherCondition;

    private Long regionId;
}
