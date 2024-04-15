package org.example.climatica.region_type;

import org.example.climatica.model.RegionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@RestController
@RequestMapping("/region/types")
@Tag(name = "RegionType Controller", description = "API for managing region types")
public class RegionTypeController {

    private final RegionTypeService regionTypeService;

    public RegionTypeController(RegionTypeService regionTypeService) {
        this.regionTypeService = regionTypeService;
    }

    @Operation(summary = "Get a region type by ID", responses = {
            @ApiResponse(description = "Region type found", responseCode = "200", content = @Content(schema = @Schema(implementation = RegionType.class))),
            @ApiResponse(description = "Region type not found", responseCode = "404"),
            @ApiResponse(description = "Invalid type ID", responseCode = "400")
    })
    @GetMapping("/{typeId}")
    public ResponseEntity<?> getRegionType(@PathVariable Long typeId) {
        if (typeId == null || typeId <= 0) {
            return ResponseEntity.badRequest().body("Invalid type ID");
        }
        return regionTypeService.findRegionTypeById(typeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add a new region type", responses = {
            @ApiResponse(description = "Region type created", responseCode = "201", content = @Content(schema = @Schema(implementation = RegionType.class))),
            @ApiResponse(description = "Invalid data", responseCode = "400"),
            @ApiResponse(description = "Type already exists", responseCode = "409")
    })
    @PostMapping
    public ResponseEntity<?> addRegionType(@RequestBody RegionTypeDto regionTypeDto) {
        if (regionTypeDto.getType() == null || regionTypeDto.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Type cannot be null or empty");
        }

        Optional<RegionType> existingRegionType = regionTypeService.findByType(regionTypeDto.getType());
        if (existingRegionType.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Region type with this 'type' already exists");
        }

        RegionType regionType = convertToEntity(regionTypeDto);
        RegionType savedRegionType = regionTypeService.saveRegionType(regionType);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRegionType);
    }

    private RegionType convertToEntity(RegionTypeDto dto) {
        RegionType regionType = new RegionType();
        regionType.setType(dto.getType());
        return regionType;
    }

    @Operation(summary = "Update an existing region type", responses = {
            @ApiResponse(description = "Region type updated", responseCode = "200", content = @Content(schema = @Schema(implementation = RegionType.class))),
            @ApiResponse(description = "Invalid data", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Region type not found", responseCode = "404"),
            @ApiResponse(description = "Type already exists", responseCode = "409")
    })
    @PutMapping("/{typeId}")
    public ResponseEntity<?> updateRegionType(@PathVariable Long typeId, @RequestBody RegionTypeDto regionTypeDto) {
        if (typeId == null || typeId <= 0 || regionTypeDto.getType() == null || regionTypeDto.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        Optional<RegionType> existingType = regionTypeService.findByType(regionTypeDto.getType());
        if (existingType.isPresent() && !existingType.get().getId().equals(typeId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Type already exists");
        }

        return regionTypeService.updateRegionType(typeId, regionTypeDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a region type", responses = {
            @ApiResponse(description = "Region type deleted", responseCode = "200"),
            @ApiResponse(description = "Region type not found", responseCode = "404"),
            @ApiResponse(description = "Invalid type ID", responseCode = "400")
    })
    @DeleteMapping("/{typeId}")
    public ResponseEntity<?> deleteRegionType(@PathVariable Long typeId) {
        if (typeId == null || typeId <= 0) {
            return ResponseEntity.badRequest().body("Invalid type ID");
        }
        return regionTypeService.deleteRegionType(typeId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
