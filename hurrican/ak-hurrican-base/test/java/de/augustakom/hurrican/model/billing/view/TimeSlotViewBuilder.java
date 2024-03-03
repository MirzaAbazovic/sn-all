/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.model.billing.view;

import java.time.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class TimeSlotViewBuilder extends EntityBuilder<TimeSlotViewBuilder, TimeSlotView> {

    private Date date = null;
    private Date daytimeFrom = null;
    private Date daytimeTo = null;
    private Long weekday = null;
    private Long areaNo = null;

    public TimeSlotViewBuilder withDate(LocalDate date) {
        this.date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withDate(int year, int month, int day) {
        this.date = Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withDaytimeFrom(LocalTime daytimeFrom) {
        this.daytimeFrom = Date.from(daytimeFrom.atDate(LocalDate.now()).
                atZone(ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withDaytimeFrom(int fromHours, int fromMinutes) {
        this.daytimeFrom = Date.from(ZonedDateTime.of(1900, 1, 1, fromHours, fromMinutes, 0, 0, ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withDaytimeTo(LocalTime daytimeTo) {
        this.daytimeTo = Date.from(daytimeTo.atDate(LocalDate.now()).
                atZone(ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withDaytimeTo(int toHours, int toMinutes) {
        this.daytimeTo = Date.from(ZonedDateTime.of(1900, 1, 1, toHours, toMinutes, 0, 0, ZoneId.systemDefault()).toInstant());
        return this;
    }

    public TimeSlotViewBuilder withWeekday(Long weekday) {
        this.weekday = weekday;
        return this;
    }

}
