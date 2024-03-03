/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2005 14:30:57
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Dialer;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer EWSD-Funktionalitaeten (z.B. Import/Exports von/zur EWSD oder fuer Tools fuer die Abteilung
 * EWSD).
 *
 *
 */
public interface EWSDService extends ICCService {

    /**
     * Sucht nach allen Dialer-Eintraegen.
     *
     * @return Liste mit Objekten des Typs <code>Dialer</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<Dialer> findDialer() throws FindException;

    /**
     * Speichert das angegebene Dialer-Modell.
     *
     * @param toStore zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    public void saveDialer(Dialer toStore) throws StoreException;

    /**
     * Liest Daten aus von der EWSD breitgestelltem File Daten aus und speichert diese in der T_PORT Tabelle
     *
     * @param importFiles Liste der zu importierenden Dateien
     * @throws StoreException
     */
    public void importEWSDFiles(List<String> importFiles) throws StoreException;

    /**
     * Löscht den gesamten Inhalt der Tabelle T_PORT_GESAMT
     *
     * @throws DeleteException
     */
    public void deletePortGesamt() throws DeleteException;

    /**
     * Ermittelt das letzte Einspieldatum
     *
     * @throws FindException
     */
    public Date selectPortGesamtDate() throws FindException;

}
