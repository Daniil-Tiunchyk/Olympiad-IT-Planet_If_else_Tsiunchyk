package org.example.climatica.weather_forecast;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.climatica.weather_forecast.dto.CreateWeatherForecastDto;
import org.example.climatica.weather_forecast.dto.UpdateWeatherForecastDto;
import org.example.climatica.weather_forecast.dto.WeatherForecastDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/region/weather/forecast")
public class WeatherForecastController {

    private final WeatherForecastService weatherForecastService;

    public WeatherForecastController(WeatherForecastService weatherForecastService) {
        this.weatherForecastService = weatherForecastService;
    }

    @Operation(summary = "Get weather forecast by ID", responses = {
            @ApiResponse(description = "Forecast found", responseCode = "200", content = @Content(schema = @Schema(implementation = WeatherForecastDto.class))),
            @ApiResponse(description = "Invalid forecast ID", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forecast not found", responseCode = "404")
    })
    @GetMapping("/{forecastId}")
    public ResponseEntity<WeatherForecastDto> getForecast(@PathVariable Long forecastId) {
        if (forecastId == null || forecastId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        WeatherForecastDto forecast = weatherForecastService.getForecastById(forecastId);
        return forecast != null ? ResponseEntity.ok(forecast) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Update a weather forecast", responses = {
            @ApiResponse(description = "Forecast updated", responseCode = "200", content = @Content(schema = @Schema(implementation = WeatherForecastDto.class))),
            @ApiResponse(description = "Invalid input data", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forecast not found", responseCode = "404")
    })
    @PutMapping("/{forecastId}")
    public ResponseEntity<WeatherForecastDto> updateForecast(@PathVariable Long forecastId, @RequestBody UpdateWeatherForecastDto updateDto) {
        if (forecastId == null || forecastId <= 0 || updateDto.getDateTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        WeatherForecastDto updatedForecast = weatherForecastService.updateForecast(forecastId, updateDto);
        return updatedForecast != null ? ResponseEntity.ok(updatedForecast) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Add a weather forecast", responses = {
            @ApiResponse(description = "Forecast created", responseCode = "200", content = @Content(schema = @Schema(implementation = WeatherForecastDto.class))),
            @ApiResponse(description = "Invalid input data", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Region not found", responseCode = "404")
    })
    @PostMapping("/")
    public ResponseEntity<WeatherForecastDto> createForecast(@RequestBody CreateWeatherForecastDto createDto) {
        if (createDto.getRegionId() == null || createDto.getRegionId() <= 0 || createDto.getDateTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        WeatherForecastDto newForecast = weatherForecastService.createForecast(createDto);
        return newForecast != null ? ResponseEntity.status(HttpStatus.CREATED).body(newForecast) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a weather forecast", responses = {
            @ApiResponse(description = "Forecast deleted", responseCode = "200"),
            @ApiResponse(description = "Invalid forecast ID", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forecast not found", responseCode = "404")
    })
    @DeleteMapping("/{forecastId}")
    public ResponseEntity<Void> deleteForecast(@PathVariable Long forecastId) {
        if (forecastId == null || forecastId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        boolean deleted = weatherForecastService.deleteForecast(forecastId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
