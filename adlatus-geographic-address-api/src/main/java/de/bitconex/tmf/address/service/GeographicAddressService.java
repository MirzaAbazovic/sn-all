package de.bitconex.tmf.address.service;

import de.bitconex.tmf.address.model.GeographicAddress;

import java.util.List;


public interface GeographicAddressService {
    GeographicAddress getById(String id);
    List<GeographicAddress> getAll();
    GeographicAddress add(GeographicAddress geographicAddress);
    List<GeographicAddress> addAll(List<GeographicAddress> geographicAddresses);
}
