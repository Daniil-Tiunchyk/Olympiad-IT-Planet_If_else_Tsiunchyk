package org.example.climatica.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.climatica.exception.InvalidParametersException;
import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.WeatherData;
import org.example.climatica.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/region/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get weather information by region ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid region ID"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    @GetMapping("/{regionId}")
    public ResponseEntity<?> getWeatherByRegionId(@PathVariable Long regionId) {
        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().body("Invalid region ID");
        }
        try {
            WeatherData weatherData = weatherService.getWeatherByRegionId(regionId);
            if (weatherData == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(weatherData);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Search weather information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data list retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request due to invalid input parameters")
            })
    @GetMapping("/search")
    public ResponseEntity<?> searchWeather(
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String weatherCondition,
            @RequestParam(defaultValue = "0") int form,
            @RequestParam(defaultValue = "10") int size) {

        // Здесь должна быть логика валидации
        List<WeatherData> weatherDataList = weatherService.searchWeather(startDateTime, endDateTime, regionId, weatherCondition, form, size);
        return ResponseEntity.ok(weatherDataList);
    }

    @Operation(summary = "Add weather data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region or forecast ID not found")
            })
    @PostMapping
    public ResponseEntity<?> addWeather(@RequestBody WeatherData weatherData) {
        try {
            WeatherData createdWeatherData = weatherService.addWeather(weatherData);
            return ResponseEntity.ok(createdWeatherData);
        } catch (InvalidParametersException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{regionId}")
    @Operation(summary = "Update weather information for a specific region",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data updated successfully", content = @Content(schema = @Schema(implementation = WeatherData.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    public ResponseEntity<?> updateWeather(@PathVariable Long regionId, @RequestBody WeatherData newWeatherData) {
        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().body("Invalid region ID");
        }
        try {
            WeatherData updatedWeatherData = weatherService.updateWeather(regionId, newWeatherData);
            return ResponseEntity.ok(updatedWeatherData);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/region/weather/{regionId}")
    @Operation(summary = "Delete weather information by region ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid region ID"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    public ResponseEntity<Void> deleteWeatherByRegion(@PathVariable Long regionId) {
        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            weatherService.deleteWeatherByRegionId(regionId);
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
