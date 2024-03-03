package de.bitconex.adlatus.wholebuy.provision.service.party;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.dto.PersonDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.party.PartyClientService;
import de.bitconex.tmf.rom.model.ResourceOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PartyResolver {
    private static final String ORGANIZATION = "Organization";
    private static final String INDIVIDUAL = "Individual";
    private static final String ORDER_MANAGEMENT_CONTACT_PERSON = "OrderManagementContactPerson";
    private static final String ORDER_TECHNICIAN = "OrderTechnician";
    private static final String ORDER_END_CUSTOMER = "OrderEndCustomer";
    private final PartyClientService partyService;

    public PartyResolver(PartyClientService partyClientService) {
        this.partyService = partyClientService;
    }

    public PersonDTO resolveOrderManagementContact(ResourceOrder order, TelecomInterfaceType type) {
        var party = order.getRelatedParty().stream().filter(p -> ORDER_MANAGEMENT_CONTACT_PERSON.equals(p.getRole())).findFirst().orElse(null);
        // TODO handle if party is not found
        PersonDTO personDTO = null;
        String atReferredType = party.getAtReferredType();
        if (INDIVIDUAL.equals(atReferredType)) {
            var individual = partyService.getIndividual(party.getHref());
            var contactMedium = individual.getContactMedium().stream().findFirst().orElse(null);
            personDTO = PersonDTO.builder()
                .salutation(resolveSalutation(individual.getTitle(), type))
                .firstName(individual.getGivenName())
                .lastName(individual.getFamilyName())
                .phone(contactMedium.getCharacteristic().getPhoneNumber())
                .email(contactMedium.getCharacteristic().getEmailAddress())
                .build();
        } else {
            log.error("Unknown party type: {}", party.getAtReferredType());
        }
        return personDTO;
    }

    public PersonDTO resolveTechnicianContact(ResourceOrder order, TelecomInterfaceType type) {
        var party = order.getRelatedParty().stream().filter(p -> ORDER_TECHNICIAN.equals(p.getRole())).findFirst().orElse(null);
        PersonDTO personDTO = null;

        String atReferredType = party.getAtReferredType();
        if (INDIVIDUAL.equals(atReferredType)) {
            var individual = partyService.getIndividual(party.getHref());
            var contactMedium = individual.getContactMedium().stream().findFirst().orElse(null);
            personDTO =  PersonDTO.builder()
                .salutation(resolveSalutation(individual.getTitle(), type))
                .firstName(individual.getGivenName())
                .lastName(individual.getFamilyName())
                .phone(contactMedium.getCharacteristic().getPhoneNumber())
                .email(contactMedium.getCharacteristic().getEmailAddress())
                .build();
        } else {
            log.error("Unknown party type: {}", party.getAtReferredType());
        }

        return personDTO;
    }

    public PersonDTO resolveEndCustomer(ResourceOrder order, TelecomInterfaceType type) {
        var party = order.getRelatedParty().stream().filter(p -> ORDER_END_CUSTOMER.equals(p.getRole())).findFirst().orElse(null);
        PersonDTO personDTO = null;

        String atReferredType = party.getAtReferredType();
        if (ORGANIZATION.equals(atReferredType)) {
            var org = partyService.getOrganization(party.getId());
            // TODO map to Technician
        } else if (INDIVIDUAL.equals(atReferredType)) {
            var individual = partyService.getIndividual(party.getHref());
            personDTO =  PersonDTO.builder()
                .salutation(resolveSalutation(individual.getTitle(), type))
                .firstName(individual.getGivenName())
                .lastName(individual.getFamilyName())
                .build();
        } else {
            log.error("Unknown party type: {}", party.getAtReferredType());
        }

        return personDTO;
    }

    public int resolveSalutation(String salutation, TelecomInterfaceType type) {
        // TODO resolve salutation based on salutation string and interface type
        return 1;
    }
}
