package org.example.climatica.weather;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.example.climatica.exception.InvalidParametersException;
import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.WeatherData;
import org.example.climatica.weather.dto.WeatherDataCreateRequest;
import org.example.climatica.weather.dto.WeatherDataResponse;
import org.example.climatica.weather.dto.WeatherUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
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
            WeatherDataResponse weatherDataResponse = convertToResponse(weatherData);
            return ResponseEntity.ok(weatherDataResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Search weather information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data list retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request due to invalid input parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    @GetMapping("/search")
    public ResponseEntity<?> searchWeather(
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String weatherCondition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            List<WeatherData> weatherDataList = weatherService.searchWeather(startDateTime, endDateTime, regionId, weatherCondition, page, size);
            return ResponseEntity.ok(weatherDataList);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use ISO-8601 format.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
            //} catch (AuthenticationException e) {
            //    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add weather data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region or forecast ID not found")
            })
    @PostMapping
    public ResponseEntity<?> addWeather(@RequestBody WeatherDataCreateRequest request) {
        try {
            validateWeatherData(request);
            WeatherData weatherData = weatherService.convertToEntity(request);
            WeatherData createdWeatherData = weatherService.addWeather(weatherData);
            WeatherDataResponse response = convertToResponse(createdWeatherData);
            return ResponseEntity.ok(response);
        } catch (InvalidParametersException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public WeatherDataResponse convertToResponse(WeatherData weatherData) {
        WeatherDataResponse response = new WeatherDataResponse();
        response.setId(weatherData.getId());
        if (weatherData.getRegion() != null) {
            response.setRegionName(weatherData.getRegion().getName());
        } else {
            response.setRegionName("Unknown Region");
        }
        response.setTemperature(weatherData.getTemperature());
        response.setHumidity(weatherData.getHumidity());
        response.setWindSpeed(weatherData.getWindSpeed());
        response.setWeatherCondition(weatherData.getWeatherCondition().toString());
        response.setPrecipitationAmount(weatherData.getPrecipitationAmount());
        response.setMeasurementDateTime(weatherData.getMeasurementDateTime());
        response.setWeatherForecast(weatherData.getWeatherForecast());
        return response;
    }

    private void validateWeatherData(WeatherDataCreateRequest request) throws InvalidParametersException {
        if (request.getRegionId() == null || request.getRegionId() <= 0 ||
                request.getMeasurementDateTime() == null || request.getWindSpeed() < 0 ||
                request.getPrecipitationAmount() < 0 ||
                !Arrays.asList("CLEAR", "CLOUDY", "RAIN", "SNOW", "FOG", "STORM").contains(request.getWeatherCondition())) {
            throw new InvalidParametersException("Invalid parameters provided");
        }
    }

    @Operation(summary = "Update weather information for a specific region",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data updated successfully", content = @Content(schema = @Schema(implementation = WeatherDataResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    @PutMapping("/{regionId}")
    public ResponseEntity<?> updateWeatherAndRegion(@PathVariable Long regionId, @RequestBody WeatherUpdateDTO dto) {
        try {
            WeatherData updatedWeatherData = weatherService.updateWeatherAndRegion(regionId, dto);
            return ResponseEntity.ok(updatedWeatherData);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid weather condition");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{regionId}")
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
