package de.bitconex.adlatus.wholebuy.provision.service.appointment;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class TimeSlotResolver {
    public int resolveTimeSlot(OffsetDateTime date, TelecomInterfaceType type) {
        // TODO resolve time slot using date and interface type
        return 9;
    }
}
