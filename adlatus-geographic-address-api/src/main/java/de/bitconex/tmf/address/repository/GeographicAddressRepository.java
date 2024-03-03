package de.bitconex.tmf.address.repository;

import de.bitconex.tmf.address.model.GeographicAddress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicAddressRepository extends MongoRepository<GeographicAddress, String> {
}
