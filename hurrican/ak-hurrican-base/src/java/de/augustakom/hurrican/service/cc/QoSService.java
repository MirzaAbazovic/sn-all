/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 13:29:20
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragQoS;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von Quality-of-Service (QoS) Daten.
 *
 *
 */
public interface QoSService extends ICCService {

    /**
     * Erstellt zu dem angegebenen Auftrag QoS-Eintraege mit entsprechenden Default-Werten.
     *
     * @param auftragId  Auftrag, zu dem QoS-Eintraege angelegt werden sollen
     * @param gueltigVon (optional) Datum, ab dem die QoS-Daten aktiv sein sollen. (Bei Angabe von 'null' wird das
     *                   aktuelle Datum verwendet.)
     * @param sessionId  Session-ID des Users
     * @return die angelegten QoS-Eintraege
     * @throws StoreException wenn bei der Anlage der QoS-Eintraege ein Fehler auftritt oder der Auftrag noch gueltige
     *                        QoS-Eintraege besitzt.
     *
     */
    public List<AuftragQoS> addDefaultQoS2Auftrag(Long auftragId, Date gueltigVon, Long sessionId)
            throws StoreException;

    /**
     * Speichert das angegebene QoS-Objekt ab. <br>
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId Session-ID des Users
     * @throws StoreException wenn beim Speichern ein Fehler auftritt oder wenn die QoS-Regeln zum Auftrag verletzt
     *                        sind
     *
     */
    public AuftragQoS saveAuftragQoS(AuftragQoS toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt alle QoS-Eintraege zu dem Auftrag mit der ID <code>auftragId</code>.
     *
     * @param auftragId ID des Auftrags, dessen QoS-Objekte ermittelt werden sollen.
     * @param onlyAct   Flag, ob alle (false) oder nur die aktuellen (true) QoS-Eintraege ermittelt werden sollen.
     * @return je nach Flag onlyAct alle (false) oder nur die aktuellen (true) QoS-Eintraege zum Auftrag
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
    public List<AuftragQoS> findQoS4Auftrag(Long auftragId, boolean onlyAct) throws FindException;

}


