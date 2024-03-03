package de.bitconex.adlatus.wholebuy.provision.resolver;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.service.appointment.TimeSlotResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeSlotResolverTest {
    private TimeSlotResolver timeSlotResolver;

    @BeforeEach
    void setUp() {
        timeSlotResolver = new TimeSlotResolver();
    }

    @Test
    void resolveTimeSlot_ReturnsExpectedTimeSlot() {
        OffsetDateTime date = OffsetDateTime.now();
        TelecomInterfaceType type = TelecomInterfaceType.WITA14;

        int result = timeSlotResolver.resolveTimeSlot(date, type);

        assertThat(result).isEqualTo(9); // currently hardcoded
    }
}