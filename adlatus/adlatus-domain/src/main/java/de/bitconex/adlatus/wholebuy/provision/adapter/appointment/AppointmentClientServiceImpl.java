package de.bitconex.adlatus.wholebuy.provision.adapter.appointment;

import de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging;
import de.bitconex.tmf.appointment.client.ApiClient;
import de.bitconex.tmf.appointment.client.api.AppointmentApi;
import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RestRequestLogging
public class AppointmentClientServiceImpl implements AppointmentClientService {
    private final AppointmentApi appointmentApi;
    
    public AppointmentClientServiceImpl(@Value("${appointment.api.basepath}") String appointmentApiBasePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(appointmentApiBasePath);
        appointmentApi = apiClient.buildClient(AppointmentApi.class);
    }

    @Override
    public Appointment createAppointment(AppointmentCreate appointment) {
        return appointmentApi.createAppointment(appointment);
    }

    @Override
    public List<Appointment> getAppointments() {
        return appointmentApi.listAppointment(null);
    }
}
