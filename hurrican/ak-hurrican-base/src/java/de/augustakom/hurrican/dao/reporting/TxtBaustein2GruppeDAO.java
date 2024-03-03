/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2007 10:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;

public interface TxtBaustein2GruppeDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Funktion loescht alle Datensaetze anhand der Bausteingruppen-Id
     *
     * @param bausteinGruppeId der zu loeschenden Txt-Baustein-Gruppe
     *
     */
    public void deleteByBausteinGruppeId(Long bausteinGruppeId);

}
