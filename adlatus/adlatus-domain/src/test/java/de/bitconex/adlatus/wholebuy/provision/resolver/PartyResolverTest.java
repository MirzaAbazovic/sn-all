package de.bitconex.adlatus.wholebuy.provision.resolver;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.dto.PersonDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.party.PartyClientService;
import de.bitconex.adlatus.wholebuy.provision.service.party.PartyResolver;
import de.bitconex.tmf.pm.model.ContactMedium;
import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.pm.model.MediumCharacteristic;
import de.bitconex.tmf.rom.model.ResourceOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartyResolverTest {
    public static final String INDIVIDIUAL_HREF = "href";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String SALUTATION_AS_STRING = "Mr.";
    public static final String EMAIL = "john@doe.com";
    public static final String PHONE = "123456789";
    private PartyClientService partyClientService;
    private PartyResolver partyResolver;

    @BeforeEach
    void setUp() {
        partyClientService = mock(PartyClientService.class);
        partyResolver = new PartyResolver(partyClientService);
    }

    @Test
    void testResolveOrderManagementContact() {
        ResourceOrder order = ResourceOrder.builder()
            .relatedParty(
                List.of(
                    de.bitconex.tmf.rom.model.RelatedParty.builder()
                        .role("OrderManagementContactPerson")
                        .atReferredType("Individual")
                        .href(INDIVIDIUAL_HREF)
                        .build()
                )
            )
            .build();

        TelecomInterfaceType type = TelecomInterfaceType.WITA14;

        when(partyClientService.getIndividual(INDIVIDIUAL_HREF)).thenReturn(getIndividual());

        PersonDTO actualPersonDTO = partyResolver.resolveOrderManagementContact(order, type);

        assertThat(actualPersonDTO.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualPersonDTO.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actualPersonDTO.getSalutation()).isEqualTo(1); // this is currently hardcoded
        assertThat(actualPersonDTO.getPhone()).isEqualTo(PHONE);
        assertThat(actualPersonDTO.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void testResolveTechnicianContact() {
        ResourceOrder order = ResourceOrder.builder()
            .relatedParty(
                List.of(
                    de.bitconex.tmf.rom.model.RelatedParty.builder()
                        .role("OrderTechnician")
                        .atReferredType("Individual")
                        .href(INDIVIDIUAL_HREF)
                        .build()
                )
            )
            .build();

        TelecomInterfaceType type = TelecomInterfaceType.WITA14;

        when(partyClientService.getIndividual(INDIVIDIUAL_HREF)).thenReturn(getIndividual());

        PersonDTO actualPersonDTO = partyResolver.resolveTechnicianContact(order, type);

        assertThat(actualPersonDTO.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualPersonDTO.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actualPersonDTO.getSalutation()).isEqualTo(1); // this is currently hardcoded
        assertThat(actualPersonDTO.getPhone()).isEqualTo(PHONE);
        assertThat(actualPersonDTO.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void testResolveEndCustomer() {
        // todo: add test case for organization once we add this as well
        ResourceOrder order = ResourceOrder.builder()
            .relatedParty(
                List.of(
                    de.bitconex.tmf.rom.model.RelatedParty.builder()
                        .role("OrderEndCustomer")
                        .atReferredType("Individual")
                        .href(INDIVIDIUAL_HREF)
                        .build()
                )
            )
            .build();

        TelecomInterfaceType type = TelecomInterfaceType.WITA14;

        when(partyClientService.getIndividual(INDIVIDIUAL_HREF)).thenReturn(getIndividual());

        PersonDTO actualPersonDTO = partyResolver.resolveEndCustomer(order, type);

        assertThat(actualPersonDTO.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualPersonDTO.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actualPersonDTO.getSalutation()).isEqualTo(1); // this is currently hardcoded
        assertThat(actualPersonDTO.getPhone()).isNull();
        assertThat(actualPersonDTO.getEmail()).isNull();
    }

    @Test
    void testResolveSalutation() {
        assertThat(partyResolver.resolveSalutation("Herr", TelecomInterfaceType.WITA14)).isEqualTo(1);
        assertThat(partyResolver.resolveSalutation("Frau", TelecomInterfaceType.WITA14)).isEqualTo(1);
        assertThat(partyResolver.resolveSalutation("Firma", TelecomInterfaceType.WITA14)).isEqualTo(1);
        assertThat(partyResolver.resolveSalutation("Herr", TelecomInterfaceType.WITA15)).isEqualTo(1);
        assertThat(partyResolver.resolveSalutation("Frau", TelecomInterfaceType.WITA15)).isEqualTo(1);
        assertThat(partyResolver.resolveSalutation("Firma", TelecomInterfaceType.WITA15)).isEqualTo(1);
    }


    Individual getIndividual() {
        return Individual.builder()
            .givenName(FIRST_NAME)
            .familyName(LAST_NAME)
            .title(SALUTATION_AS_STRING)
            .contactMedium(List.of(ContactMedium.builder()
                .characteristic(MediumCharacteristic.builder()
                    .emailAddress(EMAIL)
                    .phoneNumber(PHONE)
                    .build())
                .build()))
            .build();
    }
}