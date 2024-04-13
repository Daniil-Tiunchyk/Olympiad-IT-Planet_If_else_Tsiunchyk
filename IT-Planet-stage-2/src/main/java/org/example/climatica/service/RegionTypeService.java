package org.example.climatica.service;

import org.example.climatica.model.RegionType;
import org.example.climatica.repository.RegionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegionTypeService {
    private final RegionTypeRepository regionTypeRepository;

    public RegionTypeService(RegionTypeRepository regionTypeRepository) {
        this.regionTypeRepository = regionTypeRepository;
    }

    @Transactional(readOnly = true)
    public Optional<RegionType> findRegionTypeById(Long id) {
        return regionTypeRepository.findById(id);
    }

    @Transactional
    public RegionType saveRegionType(RegionType regionType) {
        return regionTypeRepository.save(regionType);
    }

    @Transactional
    public Optional<RegionType> updateRegionType(Long id, RegionType newRegionType) {
        return regionTypeRepository.findById(id).map(type -> {
            type.setType(newRegionType.getType());
            return regionTypeRepository.save(type);
        });
    }

    @Transactional
    public boolean deleteRegionType(Long id) {
        return regionTypeRepository.findById(id).map(type -> {
            regionTypeRepository.delete(type);
            return true;
        }).orElse(false);
    }
}
