package org.example.climatica.region_type;

import org.example.climatica.model.Account;
import org.example.climatica.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionTypeRepository extends JpaRepository<RegionType, Long> {
    Optional<RegionType> findByType(String type);

}
