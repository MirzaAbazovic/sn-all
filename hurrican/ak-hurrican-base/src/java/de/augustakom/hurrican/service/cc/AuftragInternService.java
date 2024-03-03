/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2009 17:02:52
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von internen Arbeitsauftraegen.
 *
 *
 */
public interface AuftragInternService extends ICCService {

    /**
     * Ermittelt einen internen Arbeitsauftrag ueber die Auftrags-ID
     *
     * @param auftragId Auftrags-ID
     * @return Objekt vom Typ <code>AuftragIntern</code> oder <code>null</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public AuftragIntern findByAuftragId(Long auftragId) throws FindException;

    /**
     * Speichert das angegebene Objekt ab.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Flag, ob eine Historisierung erzeugt werden soll (true) oder nicht (false).
     * @return das gespeicherte AuftragIntern-Objekt. Bei Historisierung wird ein neues Objekt zurueck gegeben, bei
     * 'einfacher' Speicherung Objekt 'toSave'.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    public AuftragIntern saveAuftragIntern(AuftragIntern toSave, boolean makeHistory) throws StoreException;

}


