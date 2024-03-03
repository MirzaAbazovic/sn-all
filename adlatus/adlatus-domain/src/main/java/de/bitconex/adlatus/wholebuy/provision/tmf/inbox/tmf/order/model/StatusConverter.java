package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getValue();
    }

    @Override
    public Status convertToEntityAttribute(String s) {
        return Status.fromValue(s);
    }
}
