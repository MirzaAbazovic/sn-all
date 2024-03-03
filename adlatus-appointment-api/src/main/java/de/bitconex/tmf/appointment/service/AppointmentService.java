package de.bitconex.tmf.appointment.service;

import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import de.bitconex.tmf.appointment.model.AppointmentUpdate;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment createAppointment(AppointmentCreate appointmentCreate);

    Appointment getAppointmentById(String id, Optional<String> fields);

    List<Appointment> getAllAppointments(String fields, Integer offset, Integer limit);

    Appointment updateAppointment(String id, AppointmentUpdate appointmentUpdate);

    void deleteAppointment(String id);
}
