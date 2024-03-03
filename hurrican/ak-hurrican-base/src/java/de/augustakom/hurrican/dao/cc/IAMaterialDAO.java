/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 15:58:16
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;


/**
 * Interface fuer die Verwaltung von Materialentnahmen fuer Innenauftraege/Budgets.
 *
 *
 */
public interface IAMaterialDAO extends ByExampleDAO, StoreDAO, FindDAO, FindAllDAO {

    /**
     * Ermittelt alle (nicht entfernten) Artikel zu der Materialentnahme mit der ID <code>matEntId</code>
     *
     * @param matEntId
     * @return Liste mit Objekten des Typs <code>IAMaterialEntnahmeArtikel</code>
     *
     */
    public List<IAMaterialEntnahmeArtikel> findArtikel4MatEntnahme(Long matEntId);

    /**
     * Sucht nach einem Material ueber die (eindeutige) Materialnummer.
     *
     * @param matNr Materialnummer
     * @return Objekt vom Typ <code>IAMaterial</code>.
     *
     */
    public IAMaterial findMaterial(String matNr);

    /**
     * Entfernt alle(!) Eintraege der Material-Referenzliste.
     *
     *
     */
    public void deleteMaterials();

}


