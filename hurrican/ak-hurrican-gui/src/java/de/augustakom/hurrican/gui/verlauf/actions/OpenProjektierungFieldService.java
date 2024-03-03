/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 14:14:16
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Action, um die Projektierungen fuer FieldService anzuzeigen.
 *
 *
 */
public class OpenProjektierungFieldService extends OpenProjektierungFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenProjektierungFrameAction#getAbteilung4Projektierung()
     */
    @Override
    protected Long getAbteilung4Projektierung() {
        return Abteilung.FIELD_SERVICE;
    }


}


