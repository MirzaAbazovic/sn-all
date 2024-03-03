package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@JsonPropertyOrder({
        de.bitconex.tmf.rom.model.TimePeriod.JSON_PROPERTY_END_DATE_TIME,
        de.bitconex.tmf.rom.model.TimePeriod.JSON_PROPERTY_START_DATE_TIME
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class TimePeriod implements Serializable {
    public static final String JSON_PROPERTY_END_DATE_TIME = "endDateTime";
    private OffsetDateTime endDateTime;

    public static final String JSON_PROPERTY_START_DATE_TIME = "startDateTime";
    private OffsetDateTime startDateTime;
}
