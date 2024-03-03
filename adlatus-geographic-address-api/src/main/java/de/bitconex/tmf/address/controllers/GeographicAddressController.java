package de.bitconex.tmf.address.controllers;


import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.address.model.GeographicSubAddress;
import de.bitconex.tmf.server.GeographicAddressApi;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import de.bitconex.tmf.address.service.GeographicAddressService;

import java.util.List;

@Controller
public class GeographicAddressController implements GeographicAddressApi {
    private final GeographicAddressService geographicAddressService;

    public GeographicAddressController(GeographicAddressService geographicAddressService) {
        this.geographicAddressService = geographicAddressService;
    }

    @Override
    public ResponseEntity<List<GeographicAddress>> listGeographicAddress(@Valid String s, @Valid Integer integer, @Valid Integer integer1) {
        return ResponseEntity.ok(geographicAddressService.getAll());
    }

    @Override
    public ResponseEntity<List<GeographicSubAddress>> listGeographicSubAddress(String s, @Valid String s1, @Valid Integer integer, @Valid Integer integer1) {
        return null;
    }

    @Override
    public ResponseEntity<GeographicAddress> retrieveGeographicAddress(String s, @Valid String s1) {
        return ResponseEntity.ok(geographicAddressService.getById(s));
    }

    @Override
    public ResponseEntity<GeographicSubAddress> retrieveGeographicSubAddress(String s, String s1, @Valid String s2) {
        return null;
    }
}
