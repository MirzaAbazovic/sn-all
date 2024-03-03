/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:35:33
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * DAO-Definition fuer Objekte des Typs <code>KundeNbz</code>
 *
 *
 */
public interface KundeNbzDAO extends FindDAO, ByExampleDAO {

    /**
     * Speichert eine Kunde-Nutzerbezeichnung Zuordnung
     *
     * @param kundeNbz KundeNbz-Objekt
     * @throws StoreException
     */
    public void saveKundeNbz(KundeNbz kundeNbz) throws StoreException;

    /**
     * Sucht nach allen KundeNo, die der übergebenen Nutzerbezeichnung zugeordnet sind
     *
     * @param nbz Nutzerbezeichnung
     * @throws FindException
     */
    public List<KundeNbz> findKundeNbzByNbz(String nbz) throws FindException;

    /**
     * Sucht nach einer Kunde-Nutzerbezeichnung Zuordnung in Abhängigkeit der kundeNo
     *
     * @param kundeNo Kundennummer
     * @throws FindException
     */
    public KundeNbz findKundeNbzByNo(Long kundeNo) throws FindException;
}


