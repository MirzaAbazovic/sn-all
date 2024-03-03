/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 08:17:51
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Ermittlung/Verwaltung von Reference-Daten.
 *
 *
 */
public interface ReferenceService extends ICCService {

    /**
     * Laedt alle Reference-Objekte.
     *
     * @return Liste mit Objekten des Typs <code>Reference</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Reference> findReferences() throws FindException;

    /**
     * Ermittelt alle Referenz-Typen zu einem bestimmten Typ. <br>
     *
     * @param type        Typ der zu ermittelnden Refernzen (Konstante in Modell Reference)
     * @param onlyVisible Flag, ob nur Referenzen mit Flag 'GUI_VISIBLE' ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Reference</code>.
     * @throws FindException
     *
     */
    List<Reference> findReferencesByType(String type, Boolean onlyVisible) throws FindException;

    /**
     * Ermittelt alle Referenz-Typen zu einem bestimmten Typ. <br> Die Methode wird in einer neuen Transaction
     * ausgefuehrt!
     *
     * @param type        Typ der zu ermittelnden Refernzen (Konstante in Modell Reference)
     * @param onlyVisible Flag, ob nur Referenzen mit Flag 'GUI_VISIBLE' ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Reference</code>.
     * @throws FindException
     */
    List<Reference> findReferencesByTypeTxNew(String type, Boolean onlyVisible) throws FindException;

    /**
     * Ermittelt die Referenz mit der angegebenen ID.
     *
     * @param refId entspricht {@link Reference#id}
     * @return Referenz zur gesuchten ID.
     * @throws FindException
     *
     */
    Reference findReference(Long refId) throws FindException;

    /**
     * Ermittelt einen Reference-Eintrag ueber den angegebenen Typ und den erwarteten Integer-Wert.
     *
     * @param type     gesuchter Reference-Typ ({@link Reference#type}.
     * @param intValue erwarteter Integer-Wert ({@link Reference#intValue}.
     * @return Objekt vom Typ <code>Reference</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Reference findReference(String type, Integer intValue) throws FindException;

    /**
     * Ermittelt einen Reference-Eintrag ueber den angegebenen Typ und den erwarteten String-Wert.
     *
     * @param type     gesuchter Reference-Typ.
     * @param strValue erwarteter String-Wert.
     * @return Objekt vom Typ <code>Reference</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Reference findReference(String type, String strValue) throws FindException;

    /**
     * Speichert <code>Reference</code>-Eintrag
     *
     * @param toSave zu speichernder <code>Reference</code>-Eintrag
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     *
     */
    void saveReference(Reference toSave) throws StoreException;

}


