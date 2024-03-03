/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 09:40:45
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.temp.MassenbenachrichtigungDaten;
import de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service fuer Massenbenachrichtigungen
 */
public interface MassenbenachrichtigungService extends ICCService {

    /**
     * Sucht die fuer Massenbenachrichtigungen relevanten Daten der gegebenen Auftraege heraus und stoesst die
     * Benachrichtigungen an.
     *
     * @param auftragsIds Liste von Hurrican-Auftrags-IDs
     */
    void triggerMassenbenachrichtigung(List<MassenbenachrichtigungDaten> auftragsDatenList, String template,
            boolean fax, boolean email, String ticketNr, String text, Date from, Date till, Long sessionId);

    /**
     * Ermittelt Auftrags/Kundendaten fuer die gegebenen AuftragDaten. Nicht gesetzt werden Port-Daten und Daten, die
     * durch Benutzereingaben gesetzt werden muessen.
     *
     * @param auftragDaten
     * @return Eine Instanz con TServiceExp, die Auftrags/Kundendaten aus Hurrican und Taifun enthaelt.
     * @throws ServiceNotFoundException Wenn ein Fehler aufgetreten ist
     * @throws FindException            Wenn ein Fehler aufgetreten ist
     */
    TServiceExp createFromAuftrag(AuftragDaten auftragDaten) throws ServiceNotFoundException, FindException;

}
