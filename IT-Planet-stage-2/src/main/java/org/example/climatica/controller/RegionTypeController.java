package org.example.climatica.controller;

import org.example.climatica.model.RegionType;
import org.example.climatica.service.RegionTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
            @ApiResponse(description = "Invalid data", responseCode = "400")
    })
    @PostMapping
    public ResponseEntity<?> addRegionType(@RequestBody RegionType regionType) {
        if (regionType.getType() == null || regionType.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Type cannot be null or empty");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(regionTypeService.saveRegionType(regionType));
    }

    @Operation(summary = "Update an existing region type", responses = {
            @ApiResponse(description = "Region type updated", responseCode = "200", content = @Content(schema = @Schema(implementation = RegionType.class))),
            @ApiResponse(description = "Region type not found", responseCode = "404"),
            @ApiResponse(description = "Invalid data", responseCode = "400")
    })
    @PutMapping("/{typeId}")
    public ResponseEntity<?> updateRegionType(@PathVariable Long typeId, @RequestBody RegionType regionType) {
        if (typeId == null || typeId <= 0 || regionType.getType() == null || regionType.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }
        return regionTypeService.updateRegionType(typeId, regionType)
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
