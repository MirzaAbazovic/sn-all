/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 08:18:43
 */
package de.augustakom.hurrican.model.shared.iface;

import com.google.common.base.Function;

/**
 * Dieses Interface markiert ein Modell, das die ID eines CC-Auftrags (Hurrican) enthaelt.
 *
 *
 */
public interface CCAuftragModel {
    String AUFTRAG_ID = "auftragId";

    Function<CCAuftragModel, Long> GET_AUFTRAG_ID = CCAuftragModel::getAuftragId;

    /**
     * Uebergibt die Auftrags-ID aus dem CC-System.
     *
     * @param auftragId
     */
    void setAuftragId(Long auftragId);

    /**
     * Gibt die Auftrags-ID des CC-Systems zurueck.
     *
     * @return
     */
    Long getAuftragId();

}
