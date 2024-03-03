package de.bitconex.tmf.appointment.repository;

import de.bitconex.tmf.appointment.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
}
