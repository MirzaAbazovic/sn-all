package de.bitconex.tmf.appointment.service.impl;

import de.bitconex.tmf.appointment.mapper.AppointmentMapper;
import de.bitconex.tmf.appointment.mapper.AppointmentMapperImpl;
import de.bitconex.tmf.appointment.model.Appointment;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import de.bitconex.tmf.appointment.model.AppointmentUpdate;
import de.bitconex.tmf.appointment.repository.AppointmentRepository;
import de.bitconex.tmf.appointment.service.AppointmentService;
import de.bitconex.tmf.appointment.util.QueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentMapper appointmentMapper = new AppointmentMapperImpl();

    private final AppointmentRepository appointmentRepository;

    private final MongoTemplate mongoTemplate;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, MongoTemplate mongoTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Appointment createAppointment(AppointmentCreate appointmentCreate) {
        Appointment appointment = appointmentMapper.toAppointment(appointmentCreate);

        appointment.setId(UUID.randomUUID().toString());
        appointment.setCreationDate(OffsetDateTime.now());
        appointment.setLastUpdate(OffsetDateTime.now());
        appointment.setHref(String.format("appointment/%s", appointment.getId()));

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(String id, Optional<String> fields) {
        // TODO: Implement fields logic
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointment> getAllAppointments(String fields, Integer offset, Integer limit) {
        try {
            Query query = QueryUtil.createQueryWithIncludedFields(fields, Appointment.class);

            int page = offset != null && offset >= 0 ? offset : 0;
            int size = limit != null && limit > 0 ? limit : 10;

            query.with(PageRequest.of(page, size));

            return mongoTemplate.find(query, Appointment.class);
        } catch (Exception exception) {
            logger.error(exception.getLocalizedMessage(), exception);
            return List.of();
        }
    }

    @Override
    public Appointment updateAppointment(String id, AppointmentUpdate appointmentUpdate) {
        Appointment appointment = this.getAppointmentById(id, Optional.empty());

        if (appointment == null) {
            return null;
        }

        if (appointmentUpdate.getCategory() != null) {
            appointment.setCategory(appointment.getCategory());
        }

        appointment.setLastUpdate(OffsetDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }
}
