/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 15:52:04
 */
package de.mnet.hurrican.scheduler.model;

import java.io.*;

import de.augustakom.common.model.AbstractObservable;


/**
 * Abstrakte Basisklasse fuer Scheduler-Modelle, die eine ID benoetigen.
 *
 *
 */
public abstract class BaseSchedulerModel extends AbstractObservable implements Serializable {

    private static final long serialVersionUID = 3192622858341857713L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}


