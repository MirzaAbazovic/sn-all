/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2015
 */
package de.augustakom.hurrican.model.cc.view;

import java.time.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.billing.view.TimeSlotViewBuilder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;

/**
 *
 */
public class TimeSlotHolderBuilder extends EntityBuilder<TimeSlotHolderBuilder, TimeSlotHolder> {
    private TimeSlotViewBuilder timeSlotViewBuilder;
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster;
    private LocalDate talRealisierungsDay;


    public TimeSlotHolderBuilder withTimeSlotViewBuilder(TimeSlotViewBuilder timeSlotViewBuilder) {
        this.timeSlotViewBuilder = timeSlotViewBuilder;
        return this;
    }

    public TimeSlotHolderBuilder withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
        return this;
    }

    public TimeSlotHolderBuilder withTalRealisierungsDay(LocalDate talRealisierungsDay) {
        this.talRealisierungsDay = talRealisierungsDay;
        return this;
    }
}
