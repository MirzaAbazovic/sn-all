/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.12.14
 */
package de.mnet.wbci.service;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;

/**
 * Hilfs-Service, um beim WBCI Automatismus bestimmte Aktionen in eigenen Transaktionen laufen zu lassen.
 */
public interface WbciAutomationTxHelperService {

    /**
     * Versucht die Kuendigung des angegebenen Auftrags (identifiziert durch Taifun-Auftragsnummer {@code orderNoOrig})
     * rueckgaengig zu machen. <br/>
     * Neben dem TAI Auftrag werden auch die zugehoerigen Hurrican-Auftraege wieder auf 'in Betrieb' genommen sowie
     * evtl. vorhandene WITA Kuendigungen storniert.
     * @param strAufErlm
     * @param orderNoOrig
     * @param user
     * @param sessionId
     */
    void undoCancellation(ErledigtmeldungStornoAuf strAufErlm, Long orderNoOrig, AKUser user, Long sessionId);
    
}
