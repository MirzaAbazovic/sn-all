/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2010 11:55:16
 */
package de.augustakom.hurrican.gui.auftrag.actions;


/**
 * Action, um einen Bauauftrag an die zentrale Dispo zu uebergeben.
 */
public class CreateBAForZentraleDispoAction extends CreateBAAction {

    @Override
    public boolean getBaZentraleDispo() {
        return true;
    }

}


