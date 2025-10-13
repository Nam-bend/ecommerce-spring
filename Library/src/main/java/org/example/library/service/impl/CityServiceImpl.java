package org.example.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.library.entity.City;
import org.example.library.repository.CityRepository;
import org.example.library.service.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<City> getAll() {
        return cityRepository.findAll();
    }
}
