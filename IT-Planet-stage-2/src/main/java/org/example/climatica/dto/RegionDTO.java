package org.example.climatica.dto;

import lombok.Data;

@Data
public class RegionDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Long regionTypeId;
}
