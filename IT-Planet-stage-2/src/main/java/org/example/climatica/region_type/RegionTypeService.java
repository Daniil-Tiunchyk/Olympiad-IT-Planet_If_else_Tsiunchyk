package org.example.climatica.region_type;

import org.example.climatica.model.RegionType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegionTypeService {
    private final RegionTypeRepository regionTypeRepository;

    public RegionTypeService(RegionTypeRepository regionTypeRepository) {
        this.regionTypeRepository = regionTypeRepository;
    }

    public Optional<RegionType> findRegionTypeById(Long id) {
        return regionTypeRepository.findById(id);
    }

    public RegionType saveRegionType(RegionType regionType) {
        return regionTypeRepository.save(regionType);
    }

    public Optional<RegionType> findByType(String type) {
        return regionTypeRepository.findByType(type);
    }

    public Optional<RegionType> updateRegionType(Long id, RegionTypeDto dto) {
        return regionTypeRepository.findById(id).map(type -> {
            type.setType(dto.getType());
            return regionTypeRepository.save(type);
        });
    }

    public boolean deleteRegionType(Long id) {
        return regionTypeRepository.findById(id).map(type -> {
            regionTypeRepository.delete(type);
            return true;
        }).orElse(false);
    }
}
