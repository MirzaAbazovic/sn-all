package de.mnet.wbci.model;

import java.time.*;
import java.util.*;

public enum Portierungszeitfenster {
    ZF1("6:00 - 8:00 Uhr", true),
    ZF2("6:00 - 12:00 Uhr", true),
    ZF3("Frei waehlbar", false);

    private final String description;
    private final boolean supported;

    private Portierungszeitfenster(String description, boolean supported) {
        this.description = description;
        this.supported = supported;
    }

    /**
     * Filters non supported instances.
     *
     * @return
     */
    public static Portierungszeitfenster[] supported() {
        List<Portierungszeitfenster> supported = new ArrayList<>();

        for (Portierungszeitfenster portierungszeitfenster : values()) {
            if (portierungszeitfenster.isSupported()) {
                supported.add(portierungszeitfenster);
            }
        }

        return supported.toArray(new Portierungszeitfenster[supported.size()]);
    }

    /**
     * Sets local time from to given date value and returns local date time.
     * @param date
     * @return
     */
    public LocalDateTime timeFrom(LocalDate date) {
        if (this.equals(ZF1) || this.equals(ZF2)) {
            return LocalDateTime.of(date, LocalTime.of(6, 0));
        } else {
            return LocalDateTime.of(date, LocalTime.MIDNIGHT);
        }
    }

    /**
     * Sets local time to to given date value and returns local date time.
     * @param date
     * @return
     */
    public LocalDateTime timeTo(LocalDate date) {
        if (this.equals(ZF1)) {
            return LocalDateTime.of(date, LocalTime.of(8, 0));
        } else if (this.equals(ZF2)) {
            return LocalDateTime.of(date, LocalTime.of(12, 0));
        } else {
            return LocalDateTime.of(date, LocalTime.MIDNIGHT);
        }
    }

    public boolean isSupported() {
        return supported;
    }

    public String getDescription() {
        return description;
    }
}
