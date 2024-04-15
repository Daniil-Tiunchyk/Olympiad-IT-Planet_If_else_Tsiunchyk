package org.example.climatica.region;

import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.Region;
import org.example.climatica.model.WeatherData;
import org.example.climatica.region.dro.RegionDTO;
import org.example.climatica.region.dro.RegionResponseDto;
import org.example.climatica.region.dro.RegionUpdateDTO;
import org.example.climatica.weather.WeatherService;
import org.example.climatica.weather.dto.WeatherDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/region")
@Tag(name = "Region Controller", description = "API for managing regions")
public class RegionController {
    private final RegionService regionService;
    private final WeatherService weatherService;

    public RegionController(RegionService regionService, WeatherService weatherService) {
        this.regionService = regionService;
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get a region by ID", responses = {
            @ApiResponse(description = "Region found", responseCode = "200", content = @Content(schema = @Schema(implementation = Region.class))),
            @ApiResponse(description = "Region not found", responseCode = "404"),
            @ApiResponse(description = "Invalid region ID", responseCode = "400")
    })
    @GetMapping("/{regionId}")
    public ResponseEntity<?> getRegion(@PathVariable Long regionId) {
        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().body("Invalid region ID");
        }
        return regionService.findRegionById(regionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addRegion(@RequestBody RegionDTO regionDto, @CookieValue(value = "accountId", defaultValue = "1") Long accountId) {
        if (regionDto.getLatitude() == null || regionDto.getLongitude() == null || regionDto.getName() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (regionService.existsByLatitudeAndLongitude(regionDto.getLatitude(), regionDto.getLongitude())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Region with the same latitude and longitude already exists");
        }

        Region region = new Region();
        region.setName(regionDto.getName());
        region.setParentRegion(regionDto.getParentRegion());
        region.setLatitude(regionDto.getLatitude());
        region.setLongitude(regionDto.getLongitude());
        region.setRegionType(regionDto.getRegionType());
        region.setAccountId(accountId);
        region = regionService.saveRegion(region);

        RegionResponseDto responseDto = new RegionResponseDto(region);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Update an existing region", responses = {
            @ApiResponse(description = "Region updated", responseCode = "200", content = @Content(schema = @Schema(implementation = Region.class))),
            @ApiResponse(description = "Region not found", responseCode = "404"),
            @ApiResponse(description = "Invalid data", responseCode = "400")
    })
    @PutMapping("/{regionId}")
    public ResponseEntity<?> updateRegion(@PathVariable Long regionId, @RequestBody RegionUpdateDTO regionUpdateDTO) {
        if (regionId == null || regionUpdateDTO.getName() == null || regionUpdateDTO.getLatitude() == null || regionUpdateDTO.getLongitude() == null) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        Region region = new Region();
        region.setName(regionUpdateDTO.getName());
        region.setParentRegion(regionUpdateDTO.getParentRegion());
        region.setLatitude(regionUpdateDTO.getLatitude());
        region.setLongitude(regionUpdateDTO.getLongitude());

        return regionService.updateRegion(regionId, region)
                .map(updatedRegion -> ResponseEntity.ok(updatedRegion))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a region", responses = {
            @ApiResponse(description = "Region deleted", responseCode = "200"),
            @ApiResponse(description = "Region not found", responseCode = "404"),
            @ApiResponse(description = "Invalid region ID", responseCode = "400")
    })
    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> deleteRegion(@PathVariable Long regionId) {
        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().body("Invalid region ID");
        }
        return regionService.deleteRegion(regionId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Add weather data to a region",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather data added successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request due to invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region or weather not found")
            })
    @PostMapping("/{regionId}/weather/{weatherId}")
    public ResponseEntity<?> addWeatherToRegion(@PathVariable long regionId, @PathVariable long weatherId) {
        try {
            if (regionId <= 0 || weatherId <= 0) {
                return ResponseEntity.badRequest().body("Invalid region ID or weather ID");
            }

            WeatherData weatherData = weatherService.addWeatherToRegion(regionId, weatherId);
            WeatherDataResponse response = convertToResponse(weatherData);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private WeatherDataResponse convertToResponse(WeatherData weatherData) {
        WeatherDataResponse response = new WeatherDataResponse();
        response.setId(weatherData.getRegion().getId());
        response.setRegionName(weatherData.getRegion().getName());
        response.setTemperature(weatherData.getTemperature());
        response.setHumidity(weatherData.getHumidity());
        response.setWindSpeed(weatherData.getWindSpeed());
        response.setWeatherCondition(weatherData.getWeatherCondition().toString());
        response.setPrecipitationAmount(weatherData.getPrecipitationAmount());
        response.setMeasurementDateTime(LocalDateTime.parse(weatherData.getMeasurementDateTime().toString()));
        response.setWeatherForecast(new ArrayList<>(weatherData.getWeatherForecast()));
        return response;
    }

    @Operation(summary = "Delete weather from a region",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request due to invalid regionId"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    @DeleteMapping("/{regionId}/weather/{weatherId}")
    public ResponseEntity<?> deleteWeatherFromRegion(@PathVariable long regionId, @PathVariable long weatherId) {
        try {
            if (regionId <= 0) {
                return ResponseEntity.badRequest().body("Invalid region ID");
            }

            Region region = weatherService.deleteWeatherFromRegion(regionId, weatherId);
            RegionResponseDto response = convertToRegionResponse(region);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private RegionResponseDto convertToRegionResponse(Region region) {
        RegionResponseDto response = new RegionResponseDto();
        response.setId(region.getId());
        response.setName(region.getName());
        response.setParentRegion(region.getParentRegion());
        response.setLatitude(region.getLatitude());
        response.setLongitude(region.getLongitude());
        return response;
    }
}
