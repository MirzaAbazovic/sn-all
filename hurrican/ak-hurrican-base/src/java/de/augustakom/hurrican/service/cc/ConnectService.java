/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:30:37
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Definition fuer die Bearbeitung von Connect-Daten.
 */
public interface ConnectService extends ICCService {

    void saveAuftragConnect(AuftragConnect toSave) throws StoreException;

    /**
     * @param auftrag der Auftrag zu dem der zugehörige Connect-Auftrag gesucht wird
     * @return den gefundenen Connect-Auftrag oder null, falls keiner existiert
     * @throws FindException falls mehr als ein Connect-Auftrag gefunden wurde
     */
    AuftragConnect findAuftragConnectByAuftrag(CCAuftragModel auftrag) throws FindException;


    /**
     * @param toSave zu speichernedes EndstelleConnect-Objekt
     * @throws StoreException falls Fehler beim Speichern aufgetreten ist
     */
    void saveEndstelleConnect(EndstelleConnect toSave) throws StoreException;

    /**
     * @param endstelle die Endstelle zu dem der zugehörige Endstelle-Connect gesucht wird
     * @return die gefundene Endstelle-Connect oder null, falls keine existiert
     * @throws FindException falls mehr als eine Endstelle-Connect gefunden wurde
     */
    EndstelleConnect findEndstelleConnectByEndstelle(Endstelle endstelle) throws FindException;
}
