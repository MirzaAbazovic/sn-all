package de.bitconex.adlatus.common.model.converter;

import de.bitconex.adlatus.common.model.WitaProductOutbox;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WitaStatusConverter implements AttributeConverter<WitaProductOutbox.Status, String> {
    @Override
    public String convertToDatabaseColumn(WitaProductOutbox.Status status) {
        return status.getValue();
    }

    @Override
    public WitaProductOutbox.Status convertToEntityAttribute(String s) {
        return WitaProductOutbox.Status.fromValue(s);
    }
}
