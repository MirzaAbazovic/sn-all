/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 14:34:08
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import java.time.*;

/**
 *
 */
public class ModifyPortRes {

    public ModifyPortRes(LocalDate executionDate, String lineId, boolean portChanged) {
        super();
        this.executionDate = executionDate;
        this.lineId = lineId;
        this.portChanged = portChanged;
    }

    public LocalDate executionDate;
    public String lineId;
    public boolean portChanged;

}


