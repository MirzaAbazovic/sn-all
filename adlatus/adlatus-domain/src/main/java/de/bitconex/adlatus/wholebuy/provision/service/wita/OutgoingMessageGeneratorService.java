package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.common.templating.TemplateEngine;
import de.bitconex.adlatus.wholebuy.provision.dto.LocationDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.PersonDTO;
import de.bitconex.adlatus.wholebuy.provision.service.agreement.AgreementResolver;
import de.bitconex.adlatus.wholebuy.provision.service.appointment.TimeSlotResolver;
import de.bitconex.adlatus.wholebuy.provision.service.location.AddressResolver;
import de.bitconex.adlatus.wholebuy.provision.service.order.AdditionalServicesResolver;
import de.bitconex.adlatus.wholebuy.provision.service.party.PartyResolver;
import de.bitconex.tmf.rom.model.ResourceOrder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// TODO should be renamed to WitaOutgoingMessageGeneratorService.
//  There should be a service (e.g. OutgoingMessageGenerator) which resolves the interface type and then calls the appropriate generator for that interface.
//  All places that call this generator should call the above mentioned service instead.
//  Current approach is just for MVP and will definitely be refactored.
@Service
public class OutgoingMessageGeneratorService implements MessageGenerator {
    private final TemplateEngine templateEngine;
    private final AgreementResolver agreementResolver;
    private final PartyResolver partyResolver;
    private final AdditionalServicesResolver additionalServicesResolver;
    private final TimeSlotResolver timeSlotResolver;
    private final AddressResolver addressResolver;

    public OutgoingMessageGeneratorService(
            TemplateEngine templateEngine,
            AgreementResolver agreementResolver,
            PartyResolver partyResolver,
            AdditionalServicesResolver additionalServicesResolver,
            TimeSlotResolver timeSlotResolver,
            AddressResolver addressResolver
    ) {
        this.templateEngine = templateEngine;
        this.agreementResolver = agreementResolver;
        this.partyResolver = partyResolver;
        this.additionalServicesResolver = additionalServicesResolver;
        this.timeSlotResolver = timeSlotResolver;
        this.addressResolver = addressResolver;
    }

    @Override
    public String generate(ResourceOrder order) {
        // todo: this should be retryable - if this fails anywhere, we always have to resolve all other info
        // todo: small state machine
        var dataModel = new HashMap<String, Object>();
        var resourceOrder = new HashMap<String, Object>();
        var agreement = new HashMap<String, String>();
        var messageInfo = new HashMap<String, Object>();

        messageInfo.put("timestamp", OffsetDateTime.now());

        var telecomInterface = agreementResolver.resolveInterface(order);
        agreement.put("majorInterfaceVersion", telecomInterface.getMajorVersion());
        agreement.put("minorInterfaceVersion", telecomInterface.getMinorVersion());

        var contract = agreementResolver.resolveContract(order);
        agreement.put("customerNumber", contract.getCustomerNumber());
        agreement.put("agreementNumber", contract.getAgreementNumber());

        var product = agreementResolver.resolveProduct(order);
        agreement.put("productId", product.getProductId());

        var productType = agreementResolver.resolveProductType(order);
        agreement.put("productType", productType);

        var orderType = agreementResolver.resolveOrderType(order);
        agreement.put("orderType", orderType);

        var businessCase = agreementResolver.resolveBusinessCase(order);
        agreement.put("businessCase", businessCase);

        PersonDTO orderManagementContactPerson = partyResolver.resolveOrderManagementContact(order, telecomInterface.getType());
        resourceOrder.put("orderManagementContactPerson", orderManagementContactPerson);

        PersonDTO technicianContact = partyResolver.resolveTechnicianContact(order, telecomInterface.getType());
        resourceOrder.put("technicianContact", technicianContact);

        PersonDTO endCustomer = partyResolver.resolveEndCustomer(order, telecomInterface.getType());
        resourceOrder.put("endCustomer", endCustomer);

        LocationDTO location = addressResolver.resolveAddress(order, telecomInterface.getType());
        resourceOrder.put("endCustomerLocation", location);

        List<String> additionalServices = additionalServicesResolver.resolveAdditionalServices(order);
        resourceOrder.put("additionalServices", additionalServices);
        resourceOrder.put("id", order.getId());

        var requestedCompletionDate = order.getRequestedCompletionDate();
        resourceOrder.put("requestedCompletionDate", Date.from(requestedCompletionDate.toInstant()));

        var timeWindow = timeSlotResolver.resolveTimeSlot(requestedCompletionDate, telecomInterface.getType());
        resourceOrder.put("requestedCompletionTime", timeWindow);

        dataModel.put("messageInfo", messageInfo);
        dataModel.put("order", resourceOrder);
        dataModel.put("agreement", agreement);

        return templateEngine.process(dataModel, telecomInterface.getType().getTemplateName());
    }
}
