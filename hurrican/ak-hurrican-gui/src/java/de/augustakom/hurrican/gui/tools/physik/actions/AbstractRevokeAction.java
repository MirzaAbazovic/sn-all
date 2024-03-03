/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2010 10:59:42
 */
package de.augustakom.hurrican.gui.tools.physik.actions;

import de.augustakom.hurrican.gui.auftrag.actions.AbstractAuftragAction;
import de.augustakom.hurrican.model.cc.AuftragDaten;


/**
 * Ermittelt vom aktuell geoeffneten Auftrags-Frame das AuftragDaten Objekt.
 *
 *
 */
public abstract class AbstractRevokeAction extends AbstractAuftragAction {

    public AuftragDaten findActiveAuftragDaten() {
        return findModelByType(AuftragDaten.class);
    }

}


