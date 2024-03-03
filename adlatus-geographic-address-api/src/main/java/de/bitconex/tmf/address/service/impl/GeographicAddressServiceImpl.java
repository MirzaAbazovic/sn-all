package de.bitconex.tmf.address.service.impl;

import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.address.repository.GeographicAddressRepository;
import de.bitconex.tmf.address.service.GeographicAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeographicAddressServiceImpl implements GeographicAddressService {
    GeographicAddressRepository repository;

    public GeographicAddressServiceImpl(GeographicAddressRepository repository) {
        this.repository = repository;
    }

    @Override
    public GeographicAddress getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<GeographicAddress> getAll() {
        return repository.findAll();
    }

    @Override
    public GeographicAddress add(GeographicAddress geographicAddress) {
        return repository.save(geographicAddress);
    }

    @Override
    public List<GeographicAddress> addAll(List<GeographicAddress> geographicAddresses) {
        return repository.saveAll(geographicAddresses);
    }
}
