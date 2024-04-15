package org.example.climatica.region.dro;

import lombok.Data;

@Data
public class RegionDTO {
    private Long regionType;
    private String name;
    private String parentRegion;
    private Double latitude;
    private Double longitude;
}
