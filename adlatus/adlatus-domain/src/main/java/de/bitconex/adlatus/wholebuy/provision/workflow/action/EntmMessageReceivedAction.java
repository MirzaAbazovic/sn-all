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
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class EntmMessageReceivedAction implements Action<OrderProvisionStates, OrderProvisionEvents> {
    private Notificator notificator;
    private final AppointmentClientService appointmentClientService;
    private ResourceOrderService resourceOrderService;
    CustomOrderMapper customOrderMapper;

    ResourceOrder resourceOrder;

    private final Map<String, BiConsumer<Object, StateContext<OrderProvisionStates, OrderProvisionEvents>>> processingMap = Map.of(
        Variables.PAYMENT_DATE.getVariableName(), (paymentDate, context) -> processPaymentDate((String) paymentDate, context)
    );

    public EntmMessageReceivedAction(AppointmentClientService appointmentClientService, ResourceOrderService resourceOrderService, Notificator notificator, CustomOrderMapper customOrderMapper) {
        this.appointmentClientService = appointmentClientService;
        this.resourceOrderService = resourceOrderService;
        this.notificator = notificator;
        this.customOrderMapper = customOrderMapper;
    }

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.debug("ENTM message received. Process vars: {}", context.getStateMachine().getExtendedState().getVariables());

        var resourceOrderId = context.getExtendedState().get(Variables.RESOURCE_ORDER_ID.getVariableName(), String.class);

        this.resourceOrder = resourceOrderService.findById(resourceOrderId);

        var stateVariables = context.getExtendedState().getVariables();

        for (var entry : stateVariables.entrySet()) {
            String variableName = entry.getKey().toString();
            var variableValue = entry.getValue();

            if (processingMap.containsKey(variableName)) {
                processingMap.get(variableName).accept(variableValue, context);
            }
        }

        resourceOrderService.save(resourceOrder);
        notifyListeners(customOrderMapper.mapToResourceOrderTmf(resourceOrder));
    }

    private void notifyListeners(de.bitconex.tmf.rom.model.ResourceOrder resourceOrder) {
        ResourceOrderAttributeValueChangeEventPayload payload = ResourceOrderAttributeValueChangeEventPayload.builder()
            .resourceOrder(resourceOrder)
            .build();
        ResourceOrderAttributeValueChangeEvent event = ResourceOrderAttributeValueChangeEvent.
            builder()
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

    private void processPaymentDate(String paymentDate, StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.info("Processing paymentDate: {}", paymentDate);

        var appointment = AppointmentCreate.builder()
            .category("payment")
            .externalId(resourceOrder.getId())
            .validFor(TimePeriod.builder()
                .startDateTime(LocalDate.parse(paymentDate).atStartOfDay().atOffset(ZoneOffset.UTC))
                .endDateTime(LocalDate.parse(paymentDate).atStartOfDay().atOffset(ZoneOffset.UTC))
                .build())
            .build();

        log.debug("Creating appointment: {}", appointment);

        var createdAppointment = appointmentClientService.createAppointment(appointment);

        log.debug("Appointment created: {}", appointment);

        ResourceCharacteristic paymentDate1 = ResourceCharacteristic.builder()
            .name("paymentDate")
            .value(createdAppointment.getHref())
            .build();

        resourceOrder.getResourceOrderItems().get(0).getResource().addResourceCharacteristic(paymentDate1);
    }
}

