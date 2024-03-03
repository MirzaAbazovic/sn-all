/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2004 09:22:40
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.HVTGruppe;


/**
 * DAO-Interface fuer Objekte des Typs <code>HVTGruppe</code>.
 *
 *
 */
public interface HVTGruppeDAO extends FindDAO, ByExampleDAO, FindAllDAO, StoreDAO {

    /**
     * Sucht nach einer HVT-Gruppe, die einem best. HVT-Standort zugeordnet ist.
     *
     * @param standortId ID des HVT-Standorts, dessen HVT-Gruppe gesucht wird.
     * @return HVTGruppe oder <code>null</code>
     */
    public HVTGruppe findHVTGruppe4Standort(Long standortId);

    /**
     * Sucht nach einer HVT-Gruppe, ueber die gruppenID.
     *
     * @param gruppenId ID der HVT-Gruppe.
     * @return HVTGruppe oder <code>null</code>
     */
    public HVTGruppe findHVTGruppeById(Long gruppenId);
}


