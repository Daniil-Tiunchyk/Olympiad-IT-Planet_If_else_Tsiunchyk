package org.example.climatica.region;

import org.example.climatica.model.Region;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public Optional<Region> findRegionById(Long id) {
        return regionRepository.findById(id);
    }

    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    public boolean existsByLatitudeAndLongitude(Double latitude, Double longitude) {
        return regionRepository.findByLatitudeAndLongitude(latitude, longitude).isPresent();
    }

    public Optional<Region> updateRegion(Long id, Region newRegion) {
        return regionRepository.findById(id).map(region -> {
            region.setAccountId(region.getAccountId());
            region.setName(newRegion.getName());
            region.setLatitude(newRegion.getLatitude());
            region.setLongitude(newRegion.getLongitude());
            region.setRegionType(newRegion.getRegionType());
            return regionRepository.save(region);
        });
    }

    @Transactional
    public boolean deleteRegion(Long id) {
        return regionRepository.findById(id).map(region -> {
            regionRepository.delete(region);
            return true;
        }).orElse(false);
    }
}
