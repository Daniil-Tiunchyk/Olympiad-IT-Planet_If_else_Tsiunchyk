package org.example.climatica.region;

import org.example.climatica.exception.NotFoundException;
import org.example.climatica.exception.UnauthorizedException;
import org.example.climatica.model.Region;
import org.example.climatica.model.WeatherData;
import org.example.climatica.region.dro.RegionDTO;
import org.example.climatica.region.dro.RegionResponseDto;
import org.example.climatica.weather.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<?> updateRegion(@PathVariable Long regionId, @RequestBody Region region) {
        if (regionId == null || region.getName() == null || region.getLatitude() == null || region.getLongitude() == null) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        return regionService.updateRegion(regionId, region)
                .map(ResponseEntity::ok)
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
    public ResponseEntity<WeatherData> addWeatherToRegion(
            @PathVariable Long regionId,
            @PathVariable Long weatherId,
            @RequestBody WeatherData weatherData) {

        if (regionId == null || regionId <= 0 || !isValidWeatherData(weatherData)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            WeatherData createdWeatherData = weatherService.addWeather(regionId, weatherId, weatherData);
            return ResponseEntity.ok(createdWeatherData);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isValidWeatherData(WeatherData weatherData) {
        //todo: Проверка данных погоды (даты, температуры, условий и т.д.)
        return true;
    }

    @Operation(summary = "Delete weather from a region",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request due to invalid regionId"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Region not found")
            })
    @DeleteMapping("/{regionId}/weather/{weatherId}")
    public ResponseEntity<Region> deleteWeatherFromRegion(
            @PathVariable Long regionId,
            @PathVariable Long weatherId) {

        if (regionId == null || regionId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            //todo: Yet another todo
            weatherService.deleteWeather(regionId, weatherId);
            return ResponseEntity.ok(new Region());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
