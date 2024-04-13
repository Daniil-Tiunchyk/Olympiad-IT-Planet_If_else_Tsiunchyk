package org.example.climatica.service;

import org.example.climatica.model.Region;
import org.example.climatica.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Region> findRegionById(Long id) {
        return regionRepository.findById(id);
    }

    @Transactional
    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    @Transactional
    public Optional<Region> updateRegion(Long id, Region newRegion) {
        return regionRepository.findById(id).map(region -> {
            region.setName(newRegion.getName());
            region.setLatitude(newRegion.getLatitude());
            region.setLongitude(newRegion.getLongitude());
            region.setType(newRegion.getType());
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
