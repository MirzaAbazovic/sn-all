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
public class AbmMessageReceivedAction implements Action<OrderProvisionStates, OrderProvisionEvents> {
    private final AppointmentClientService appointmentClientService;
    private final ResourceOrderService resourceOrderService;
    private final Notificator notificator;

    CustomOrderMapper customOrderMapper;

    private ResourceOrder resourceOrder;


    private final Map<String, BiConsumer<Object, StateContext<OrderProvisionStates, OrderProvisionEvents>>> processingMap = Map.of(
        Variables.LINE_ID.getVariableName(), (lineId, context) -> processLineId((String) lineId, context),
        Variables.BINDING_DELIVERY_DATE.getVariableName(), (bindingDeliveryDate, context) -> processBindingDeliveryDate((String) bindingDeliveryDate, context)
    );

    public AbmMessageReceivedAction(AppointmentClientService appointmentClientService,
                                    Notificator notificator,
                                    ResourceOrderService resourceOrderService,
                                    CustomOrderMapper customOrderMapper) {
        this.appointmentClientService = appointmentClientService;
        this.notificator = notificator;
        this.resourceOrderService = resourceOrderService;
        this.customOrderMapper = customOrderMapper;
    }

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.debug("ABM message received. Process vars: {}", context.getStateMachine().getExtendedState().getVariables());
        var resourceOrderId = context.getExtendedState().get(Variables.RESOURCE_ORDER_ID.getVariableName(), String.class);
        resourceOrder = resourceOrderService.findById(resourceOrderId);

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


    private void processLineId(String lineId, StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.debug("Processing lineId: {}", lineId);

        ResourceCharacteristic lineIdCharacteristic = ResourceCharacteristic.builder()
            .name("lineId")
            .value(lineId)
            .build();

        resourceOrder.getResourceOrderItems().get(0).getResource().addResourceCharacteristic(lineIdCharacteristic);
        // todo: Test this
    }

    private void processBindingDeliveryDate(String bindingDeliveryDate, StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        OffsetDateTime appointmentDate = LocalDate.parse(bindingDeliveryDate).atStartOfDay().atOffset(ZoneOffset.UTC);

        var appointment = AppointmentCreate.builder()
            .category("delivery")
            .externalId(resourceOrder.getId())
            .validFor(TimePeriod.builder()
                .startDateTime(appointmentDate)
                .endDateTime(appointmentDate)
                .build())
            .build();

        log.info("Creating appointment: {}", appointment);

        var createdAppointment = appointmentClientService.createAppointment(appointment);


        log.info("Appointment created: {}", appointment);

        // TODO: save appointment reference in resource order
        // todo: Change this to be other apopintment
        resourceOrder.setExpectedCompletionDate(createdAppointment.getValidFor().getStartDateTime());
    }

    private void notifyListeners(ResourceOrder resourceOrder) {
        ResourceOrderAttributeValueChangeEventPayload payload = ResourceOrderAttributeValueChangeEventPayload.builder()
            // todo: Map entity to dto
            .resourceOrder(customOrderMapper.mapToResourceOrderTmf(resourceOrder))
            .build();

        ResourceOrderAttributeValueChangeEvent event = ResourceOrderAttributeValueChangeEvent.builder()
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

}

