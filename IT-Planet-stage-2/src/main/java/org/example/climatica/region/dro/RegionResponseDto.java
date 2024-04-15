package org.example.climatica.region.dro;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.climatica.model.Region;

@Data
@NoArgsConstructor
public class RegionResponseDto {
    private Long id;
    private String name;
    private String parentRegion;
    private Double latitude;
    private Double longitude;
    private Long regionType;

    public RegionResponseDto(Region region) {
        this.id = region.getId();
        this.name = region.getName();
        this.parentRegion = region.getParentRegion();
        this.latitude = region.getLatitude();
        this.longitude = region.getLongitude();
        this.regionType = region.getRegionType();
    }
}
