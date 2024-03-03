package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@JsonPropertyOrder({
        de.bitconex.tmf.rom.model.Quantity.JSON_PROPERTY_AMOUNT,
        de.bitconex.tmf.rom.model.Quantity.JSON_PROPERTY_UNITS
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class Quantity implements Serializable {
    public static final String JSON_PROPERTY_AMOUNT = "amount";
    private Float amount = 1.0f;

    public static final String JSON_PROPERTY_UNITS = "units";
    private String units;
}
