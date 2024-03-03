/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 11:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.service.base.exceptions.FindException;

public interface TxtBausteinDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO, DeleteDAO {

    /**
     * Liefert alle gueltigen Text-Bausteine
     *
     * @return Liste mit Txt-Bausteinen
     * @throws FindException
     *
     */
    public List<TxtBaustein> findAllValidTxtBausteine();

    /**
     * Liefert alle gueltigen Txt-Bausteine die einer best. Gruppe zugeordnet sind.
     *
     * @param gruppeId Id der gesuchten Txt-Baustein-Gruppe
     * @return Liste mit Txt-Bauteinen
     *
     */
    public List<TxtBaustein> findAllValid4TxtBausteinGruppe(Long gruppeId);

    /**
     * Liefert für jeden Txt-Baustein den in der Historisierung neuesten Datensatz
     *
     * @return Liste mit Txt-Bausteinen
     *
     */
    public List<TxtBaustein> findAllNewTxtBausteine();
}
