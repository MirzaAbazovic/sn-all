/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2009 13:39:57
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Interface fuer Funktionen, die zur Steuerung externer Partner dienen.
 *
 *
 */
public interface ExtServiceProviderService extends ICCService {

    /**
     * Liefert alle externen Partner
     *
     * @return Liste mit den gesuchten Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<ExtServiceProvider> findAllServiceProvider() throws FindException;

    /**
     * Liefert einen bestimmten ServiceProvider
     *
     * @param id Id des ServiceProvider
     * @return Das gesuchte Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public ExtServiceProvider findById(Long id) throws FindException;

    /**
     * Funktion informiert einen externen Partner ueber einen neuen Auftrag per Mail.
     *
     * @param verlaufId   Id des Bauauftrags
     * @param attachments optional email-Anhange wird
     * @param sessionId   Id der Session, um den Benutzer zu identifizieren
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public void sendAuftragEmail(Long verlaufId, List<Pair<byte[], String>> attachments, Long sessionId) throws FindException;

    /**
     * Funktion informiert einen externen Partner ueber die Stornierung eines Auftrags per Mail.
     *
     * @param verlaufId Id des Bauauftrags
     * @param sessionId Id der Session, um den Benutzer zu identifizieren
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public void sendStornoEmail(Long verlaufId, Long sessionId) throws FindException;
}
