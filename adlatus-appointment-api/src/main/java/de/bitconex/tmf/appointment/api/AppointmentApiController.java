package de.bitconex.tmf.appointment.api;

import de.bitconex.hub.eventing.NotificationRequest;
import de.bitconex.hub.eventing.NotificationResponse;
import de.bitconex.hub.eventing.Notificator;
import de.bitconex.tmf.appointment.model.*;
import de.bitconex.tmf.appointment.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("appointment")
public class AppointmentApiController implements AppointmentApi {
    //TODO use lombok @Sl4j
    private static final Logger log = LoggerFactory.getLogger(AppointmentApiController.class);

    private final AppointmentService appointmentService;
    private final Notificator notificator;

    public AppointmentApiController(AppointmentService appointmentService, Notificator notificator) {
        this.appointmentService = appointmentService;
        this.notificator = notificator;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentCreate appointmentCreate) {
        notify(appointmentCreate);
        Appointment createdAppointment = appointmentService.createAppointment(appointmentCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    private void notify(AppointmentCreate appointmentCreate) {
        NotificationRequest<AppointmentCreateEvent> notification = new NotificationRequest<>();
        AppointmentCreateEvent appointmentCreateEvent = new AppointmentCreateEvent();
        AppointmentCreateEventPayload appointmentCreateEventPayload = new AppointmentCreateEventPayload();
        //TODO make models with Lombok @Data and @Builder to build them more easily
        Appointment appointment = new Appointment();
        appointment.setExternalId(appointmentCreate.getExternalId());
        appointment.setCategory(appointmentCreate.getCategory());
        appointment.description(appointmentCreate.getDescription());
        //... other prop.
        appointmentCreateEventPayload.appointment(appointment);
        appointmentCreateEvent.event(appointmentCreateEventPayload);
        notification.setEvent(appointmentCreateEvent);
        NotificationResponse notificationResponse = notificator.notifyListener(notification);
        log.info("notification result: {}", notificationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        Appointment appointment = appointmentService.getAppointmentById(id, Optional.empty());

        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> listAppointment(
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit
    ) {
        List<Appointment> appointments = appointmentService.getAllAppointments(fields, offset, limit);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Appointment> patchAppointment(@PathVariable String id, @RequestBody AppointmentUpdate appointmentUpdate) {
        Appointment appointment = appointmentService.updateAppointment(id, appointmentUpdate);

        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> retrieveAppointment(
            @PathVariable String id,
            @RequestParam(required = false) Optional<String> fields
    ) {
        Appointment appointment = appointmentService.getAppointmentById(id, fields);

        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(appointment);
    }
}
