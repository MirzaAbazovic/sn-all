package de.bitconex.adlatus.wholebuy.provision.service.location;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.dto.LocationDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.geoaddress.GeographicAddressClientService;
import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.rom.model.ResourceOrder;
import org.springframework.stereotype.Component;

@Component
public class AddressResolver {
    GeographicAddressClientService geographicAddressClientService;
    public AddressResolver(GeographicAddressClientService geographicAddressClientService) {
        this.geographicAddressClientService = geographicAddressClientService;
    }
    public LocationDTO resolveAddress(ResourceOrder order, TelecomInterfaceType type) {
        var orderItems = order.getOrderItem();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("No orderItem found in order");
        }

        if (orderItems.getFirst().getResource() == null) {
            throw new RuntimeException("No resource found in order");
        }

        var place = orderItems.getFirst().getResource().getPlace();

        if (place == null) {
            throw new RuntimeException("No place found in order");
        }

        GeographicAddress address = geographicAddressClientService.getAddress(place.getHref());

        LocationDTO location = LocationDTO.builder()
                .city(address.getCity())
                .zipCode(address.getPostcode())
                .street(address.getStreetName())
                .houseNumber(address.getStreetNr())
                .country(LocationDTO.Country.fromString(address.getCountry()))
                .build();

        return location;
    }
}
