package de.bitconex.adlatus.wholebuy.provision.service.order;

import de.bitconex.tmf.rom.model.ResourceOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdditionalServicesResolver {
    public List<String> resolveAdditionalServices(ResourceOrder order) {
        //TODO resolve additional services using order
        return List.of("Express-Entstörung Wholesale", "Search Call", "Courtesy Call");
    }
}
