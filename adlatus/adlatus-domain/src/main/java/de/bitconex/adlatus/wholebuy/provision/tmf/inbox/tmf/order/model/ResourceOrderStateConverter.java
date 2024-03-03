package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ResourceOrderStateConverter implements AttributeConverter<ResourceOrder.ResourceOrderState, String> {
    @Override
    public String convertToDatabaseColumn(ResourceOrder.ResourceOrderState resourceOrderState) {
        return resourceOrderState.getValue();
    }

    @Override
    public ResourceOrder.ResourceOrderState convertToEntityAttribute(String s) {
        return ResourceOrder.ResourceOrderState.fromValue(s);
    }
}
