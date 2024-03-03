/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2008 11:48:54
 */
package de.augustakom.hurrican.service.cc.impl.command.eg;

import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Command-Klasse fuer Endgeraete-Commands.
 *
 *
 */
public abstract class AbstractEGCommand extends AbstractServiceCommand {

    /**
     * Key, um die Auftrags-ID zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID = "auftrag.id";
    /**
     * Key, um das zuzufuegende Endgeraet zu uebergeben.
     */
    public static final String KEY_EG_TO_ADD = "eg.to.add";

    /**
     * Gibt die Auftrags-ID zurueck.
     */
    protected Long getAuftragId() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID);
    }

    /**
     * Gibt das Endgeraet zurueck, das dem Auftrag hinzugefuegt werden soll.
     */
    protected EG getEGToAdd() {
        return (EG) getPreparedValue(KEY_EG_TO_ADD);
    }

}


