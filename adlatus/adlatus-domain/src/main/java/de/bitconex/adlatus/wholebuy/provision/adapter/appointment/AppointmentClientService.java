package de.bitconex.adlatus.wholebuy.provision.adapter.appointment;

import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;

import java.util.List;

public interface AppointmentClientService {
    Appointment createAppointment(AppointmentCreate appointment);

    List<Appointment> getAppointments();
}
