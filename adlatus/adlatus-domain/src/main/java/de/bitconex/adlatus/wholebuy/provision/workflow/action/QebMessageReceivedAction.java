package de.bitconex.adlatus.wholebuy.provision.workflow.action;

import de.bitconex.adlatus.wholebuy.provision.service.order.ResourceOrderService;
import de.bitconex.adlatus.wholebuy.provision.adapter.party.PartyClientService;
import de.bitconex.adlatus.wholebuy.provision.service.order.mapper.CustomOrderMapper;
import de.bitconex.adlatus.wholebuy.provision.adapter.party.mapper.WitaContactPersonMapper;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionEvents;
import de.bitconex.adlatus.wholebuy.provision.workflow.OrderProvisionStates;
import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.bitconex.hub.eventing.NotificationRequest;
import de.bitconex.hub.eventing.Notificator;
import de.bitconex.tmf.pm.model.IndividualCreate;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderAttributeValueChangeEvent;
import de.bitconex.tmf.rom.model.ResourceOrderAttributeValueChangeEventPayload;
import de.telekom.wholesale.oss.v15.complex.AnsprechpartnerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Slf4j
public class QebMessageReceivedAction implements Action<OrderProvisionStates, OrderProvisionEvents> {

    @Autowired
    private WitaContactPersonMapper witaContactPersonMapper;

    @Autowired
    private PartyClientService partyClientService;

    @Autowired
    private Notificator notificator;

    @Autowired
    private ResourceOrderService resourceOrderService;

    @Autowired
    private CustomOrderMapper customOrderMapper;

    @Override
    public void execute(StateContext<OrderProvisionStates, OrderProvisionEvents> context) {
        log.info("QEB message received. Process vars: {}", context.getStateMachine().getExtendedState().getVariables());

        var contactPerson = (AnsprechpartnerType) context.getExtendedState().getVariables().get(Variables.CONTACT_PERSON.getVariableName());

        IndividualCreate individualCreate = witaContactPersonMapper.mapToIndividualCreate(contactPerson);
        var individual = partyClientService.createIndividual(individualCreate);
        log.info("Created individual: {}", individual);

        var resourceOrderId = context.getExtendedState().get(Variables.RESOURCE_ORDER_ID.getVariableName(), String.class);

        // TODO: Enumeration for role and referredType
        // todo: extract method to resolve relatedparty - see ABM
        var ro = resourceOrderService.findById(resourceOrderId);
        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty relatedParty = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty.builder()
            .name(individual.getFullName())
            .href(individual.getHref())
            .id(individual.getId())
            .role("telekomContactPerson")
            .atReferredType("Individual")
            .build();
        ro.addRelatedParty(relatedParty);
        resourceOrderService.save(ro);

        // todo: Map to TMF model
        notifyListeners(customOrderMapper.mapToResourceOrderTmf(ro));
    }


    private void notifyListeners(ResourceOrder resourceOrder) {
        ResourceOrderAttributeValueChangeEventPayload payload = ResourceOrderAttributeValueChangeEventPayload.builder()
            .resourceOrder(resourceOrder)
            .build();
        ResourceOrderAttributeValueChangeEvent event = ResourceOrderAttributeValueChangeEvent.builder()
            .eventType("ResourceOrderAttributeValueChangeEvent")
            .eventId(UUID.randomUUID().toString())
            .timeOcurred(OffsetDateTime.now())
            .eventTime(OffsetDateTime.now())
            .correlationId(resourceOrder.getId())
            .event(payload)
            .build();


        NotificationRequest<ResourceOrderAttributeValueChangeEvent> notificationRequest = new NotificationRequest<>();
        notificationRequest.setEvent(event);

        notificator.notifyListener(notificationRequest);
    }
}

