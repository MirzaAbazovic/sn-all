/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 10:55:32
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Interface fuer die Verwaltung von SIP InterTrunk Daten.
 */
public interface SIPInterTrunkService extends ICCService {

    /**
     * Speichert das angegebene SIP InterTrunk Objekt ab.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId ID der aktuellen User-Session
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveSIPInterTrunk(AuftragSIPInterTrunk toSave, Long sessionId) throws StoreException;

    /**
     * Loescht das angegebene SIP InterTrunk Objekt.
     *
     * @param toDelete zu loeschendes Objekt
     * @throws StoreException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteSIPInterTrunk(AuftragSIPInterTrunk toDelete) throws StoreException;

    /**
     * Ermittelt alle SIP InterTrunk Daten zu dem angegebenen Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen SIP InterTrunk Daten ermittelt werden sollen
     * @return Liste mit {@link AuftragSIPInterTrunk} Objekten.
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     */
    public List<AuftragSIPInterTrunk> findSIPInterTrunks4Order(Long auftragId) throws FindException;

}


