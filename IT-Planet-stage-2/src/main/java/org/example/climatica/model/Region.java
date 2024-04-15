package org.example.climatica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long regionType;
    private Long accountId;
    private String name;
    private String parentRegion;
    private Double latitude;
    private Double longitude;
}
