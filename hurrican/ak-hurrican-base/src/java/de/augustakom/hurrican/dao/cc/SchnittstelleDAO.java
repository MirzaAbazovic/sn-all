/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2004 07:51:22
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.Produkt2Schnittstelle;
import de.augustakom.hurrican.model.cc.Schnittstelle;


/**
 * DAO-Definition fuer die Verwaltung der Schnittstellen.
 *
 *
 */
public interface SchnittstelleDAO extends FindAllDAO, FindDAO {

    /**
     * Sucht nach allen Produkt-Schnittstellenmappings, die einem best. Produkt zugeordnet sind.
     *
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten des Typs <code>Produkt2Schnittstelle</code>
     */
    public List<?> findSchnittstellenMappings4Produkt(Long produktId);

    /**
     * Sucht nach allen (Hardware-)Schnittstellen, die einem best. Produkt zugeordnet sind.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten des Typs <code>Schnittstelle</code>
     */
    public List<Schnittstelle> findSchnittstellen4Produkt(Long produktId);

    /**
     * Sucht nach einer Schnittstelle ueber eine Endstellen-ID.
     *
     * @param esId ID der Endstelle
     * @return Instanz von <code>Schnittstelle</code>.
     */
    public Schnittstelle findByEsId(Long esId);

    /**
     * Entfernt die Mapping-Eintraege zwischen Produkt und Schnittstelle fuer das angegebene Produkt.
     *
     * @param produktId ID des Produkts, dessen Schnittstellen-Mapping geloescht werden soll.
     */
    public void deleteSchnittstellen4Produkt(Long produktId);

    /**
     * Speichert die Mappings zwischen Produkt und Schnittstelle ab.
     *
     * @param produkt2Schnittstelle Liste mit Objekten des Typs <code>Produkt2Schnittstelle</code>.
     */
    public void saveSchnittstellen4Produkt(List<Produkt2Schnittstelle> produkt2Schnittstelle);
}


