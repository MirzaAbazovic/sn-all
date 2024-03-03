/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2007 10:37:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;

public interface TxtBausteinGruppeDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO, DeleteDAO {

    /**
     * Funktion liefert alle TextBaustein-Gruppen, die einem best. Report zugeordnet sind.
     *
     * @param reportId Report-Id, fuer die Txt-Baustein-Gruppen gesucht werden
     * @return Liste mit Txt-Baustein-Gruppen
     *
     */
    public List<TxtBausteinGruppe> findAll4Report(Long reportId);

    /**
     * Funktion liefert alle TextBaustein-Gruppen denen ein bestimmmter Text-Baustein zugeordnet ist.
     *
     * @param bausteinOrigId Id des Text-Bausteins der geprueft werden soll.
     * @return Liste mit Txt-Baustein-Gruppen
     *
     */
    public List<TxtBausteinGruppe> findAll4TxtBaustein(Long bausteinOrigId);
}
