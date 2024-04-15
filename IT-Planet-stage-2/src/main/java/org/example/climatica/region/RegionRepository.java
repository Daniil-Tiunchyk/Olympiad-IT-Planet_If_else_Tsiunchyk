package org.example.climatica.region;

import org.example.climatica.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByLatitudeAndLongitude(Double latitude, Double longitude);
}