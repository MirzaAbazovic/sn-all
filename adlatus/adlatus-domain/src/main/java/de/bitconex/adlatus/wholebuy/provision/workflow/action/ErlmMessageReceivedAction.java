package de.bitconex.adlatus.wholebuy.provision.workflow.action;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceCharacteristic;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder;
import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.adapter.appointment.AppointmentClientService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.hub.eventing.NotificationRequest;
import de.bitconex.hub.eventing.Notificator;
import de.bitconex.tmf.appointment.model.AppointmentCreate;
import de.bitconex.tmf.appointment.model.TimePeriod;
import de.bitconex.tmf.rom.model.ResourceOrderAttributeValueChangeEvent;
import de.bitconex.tmf.rom.model.ResourceOrderAttributeValueChangeEventPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class ErlmMessageReceivedAction implements Action<OrderProvisionStates, OrderProvisionEvents> {
    private final Notificator notificator;
    private final AppointmentClientService appointmentClientService;
    private final CustomOrderMapper customOrderMapper;
    private final ResourceOrderService resourceOrderService;
    ResourceOrder resourceOrder;

    private final Map<String, BiConsumer<Object, StateContext<OrderProvisionStates, OrderProvisionEvents>>> processingMap = Map.of(
        Variables.COMPLETION_DATE.getVariableName(), (paymentDate, context) -> processCompletionDate((String) paymentDate, context)
    );

    public ErlmMessageReceivedAction(Notificator notificator, AppointmentClientService appointmentClientService, ResourceOrderService resourceOrderService, CustomOrderMapper customOrderMapper) {
        this.notificator = notificator;
        this.appointmentClientService = appointmentClientService;
        this.resourceOrderService = resourceOrderService;
        this.customOrderMapper = customOrderMapper;
    }

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.debug("ERLM message received. Process vars: {}", context.getStateMachine().getExtendedState().getVariables());

        var resourceOrderId = context.getExtendedState().get(Variables.RESOURCE_ORDER_ID.getVariableName(), String.class);

        this.resourceOrder = resourceOrderService.findById(resourceOrderId);

        if (resourceOrder == null) {
            log.error("Resource order with id {} not found", resourceOrderId);
            throw new EntityNotFoundException("Resource order with id " + resourceOrderId + " not found");
        }

        var stateVariables = context.getExtendedState().getVariables();

        for (var entry : stateVariables.entrySet()) {
            String variableName = entry.getKey().toString();
            var variableValue = entry.getValue();

            if (processingMap.containsKey(variableName)) {
                processingMap.get(variableName).accept(variableValue, context);
            }
        }

        resourceOrderService.save(resourceOrder);

        notifyListeners(resourceOrder);
    }

    private void notifyListeners(ResourceOrder resourceOrder) {
        ResourceOrderAttributeValueChangeEventPayload payload = ResourceOrderAttributeValueChangeEventPayload
            .builder()
            .resourceOrder(customOrderMapper.mapToResourceOrderTmf(resourceOrder))
            .build();

        ResourceOrderAttributeValueChangeEvent event = ResourceOrderAttributeValueChangeEvent
            .builder()
            .title("ResourceOrderAttributeValueChangeEvent")
            .eventTime(OffsetDateTime.now())
            .timeOcurred(OffsetDateTime.now())
            .eventType("ResourceOrderAttributeValueChangeEvent")
            .correlationId(resourceOrder.getId())
            .event(payload)
            .build();

        NotificationRequest<ResourceOrderAttributeValueChangeEvent> notification = new NotificationRequest<>();
        notification.setEvent(event);
        notificator.notifyListener(notification);
    }

    private void processCompletionDate(String xmlGregorianCalendar, StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.info("Processing completionDate: {}", xmlGregorianCalendar);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()    // DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm").withZone(ZoneOffset.UTC)
            .append(DateTimeFormatter.ISO_OFFSET_DATE)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter();
        OffsetDateTime appointmentDate = OffsetDateTime.parse(xmlGregorianCalendar, formatter);

        var appointment = AppointmentCreate.builder()
            .category("completionDate")
            .externalId(resourceOrder.getId())
            .validFor(TimePeriod.builder()
                .startDateTime(appointmentDate)
                .endDateTime(appointmentDate)
//                        .startDateTime(LocalDate.parse(xmlGregorianCalendar.toString()).atStartOfDay().atOffset(ZoneOffset.UTC))
//                        .endDateTime(LocalDate.parse(xmlGregorianCalendar.toString()).atStartOfDay().atOffset(ZoneOffset.UTC))
                .build())
            .build();
        var appointmentResult = appointmentClientService.createAppointment(appointment);

        resourceOrder.setCompletionDate(appointmentResult.getValidFor().getStartDateTime());

        ResourceCharacteristic paymentDate1 = ResourceCharacteristic.builder()
            .name("completionDate")
            .value(appointmentResult.getHref())
            .build();

        resourceOrder.getResourceOrderItems().get(0).getResource().addResourceCharacteristic(paymentDate1);
    }
}

