/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.14
 */
package de.augustakom.hurrican.model.cc.view;

import javax.annotation.*;

/**
 * Marker-Interface fuer Objekte, die einen 'TimeSlotHolder' kennen.
 */
public interface TimeSlotAware {

    @Nonnull TimeSlotHolder getTimeSlot();
}
