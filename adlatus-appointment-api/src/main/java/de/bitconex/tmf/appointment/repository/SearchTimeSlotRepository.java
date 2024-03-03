package de.bitconex.tmf.appointment.repository;

import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchTimeSlotRepository extends MongoRepository<SearchTimeSlot, String> {
}
