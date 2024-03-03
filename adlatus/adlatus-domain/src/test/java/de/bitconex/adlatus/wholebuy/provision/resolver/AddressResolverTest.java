package de.bitconex.adlatus.wholebuy.provision.resolver;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.dto.LocationDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.geoaddress.GeographicAddressClientService;
import de.bitconex.adlatus.wholebuy.provision.service.location.AddressResolver;
import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.rom.model.RelatedPlaceRefOrValue;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderItem;
import de.bitconex.tmf.rom.model.ResourceRefOrValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressResolverTest {
    public static final String ADRESS_HREF = "someHref";
    private GeographicAddressClientService geographicAddressClientService;
    private AddressResolver addressResolver;

    @BeforeEach
    void setUp() {
        geographicAddressClientService = mock(GeographicAddressClientService.class);
        addressResolver = new AddressResolver(geographicAddressClientService);
    }

    @Test
    void testResolveAddress_ValidOrder() {
        ResourceOrder order = ResourceOrder.builder()
            .orderItem(
                List.of(
                    ResourceOrderItem.builder()
                        .resource(
                            ResourceRefOrValue.builder()
                                .place(
                                    RelatedPlaceRefOrValue.builder()
                                        .href(ADRESS_HREF)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
            )
            .build();

        GeographicAddress address = GeographicAddress.builder()
            .city("someCity")
            .postcode("somePostcode")
            .streetName("someStreetName")
            .streetNr("someStreetNr")
            .country("Deutschland")
            .build();

        when(geographicAddressClientService.getAddress(ADRESS_HREF)).thenReturn(address);

        LocationDTO location = addressResolver.resolveAddress(order, TelecomInterfaceType.WITA14);

        assertThat(location.getCity()).isEqualTo(address.getCity());
        assertThat(location.getZipCode()).isEqualTo(address.getPostcode());
        assertThat(location.getStreet()).isEqualTo(address.getStreetName());
        assertThat(location.getHouseNumber()).isEqualTo(address.getStreetNr());
        assertThat(location.getCountry()).isEqualTo(LocationDTO.Country.fromString(address.getCountry()));
    }

    @Test
    void testResolveAddress_NoOrderItem() {
        ResourceOrder order = new ResourceOrder();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> addressResolver.resolveAddress(order, TelecomInterfaceType.WITA14));

        assertThat(ex.getMessage()).isEqualTo("No orderItem found in order");
    }

    @Test
    void testResolveAddress_NoResource() {
        ResourceOrder order = new ResourceOrder();
        order.setOrderItem(List.of(ResourceOrderItem.builder().build()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> addressResolver.resolveAddress(order, TelecomInterfaceType.WITA14));

        assertThat(ex.getMessage()).isEqualTo("No resource found in order");
    }

    @Test
    void testResolveAddress_NoPlace() {
        ResourceOrder order = new ResourceOrder();
        order.setOrderItem(List.of(ResourceOrderItem.builder().resource(ResourceRefOrValue.builder().build()).build()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> addressResolver.resolveAddress(order, TelecomInterfaceType.WITA14));

        assertThat(ex.getMessage()).isEqualTo("No place found in order");
    }
}