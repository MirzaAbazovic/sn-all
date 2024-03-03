package de.bitconex.tmf.appointment.mapper;

import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import org.mapstruct.Mapper;

@Mapper
public interface AppointmentMapper {
    Appointment toAppointment(AppointmentCreate appointmentCreate);

    AppointmentCreate toAppointmentCreate(Appointment appointment);
}
