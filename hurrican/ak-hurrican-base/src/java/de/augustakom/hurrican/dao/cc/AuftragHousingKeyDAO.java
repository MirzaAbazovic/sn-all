/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2011 09:00:20
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;


/**
 * DAO-Interface fuer AuftragHousingKeys.
 */
public interface AuftragHousingKeyDAO extends FindDAO, FindAllDAO, StoreDAO, DeleteDAO, ByExampleDAO {

    /**
     * Ermittelt alle Transponder zu der übergebenden Auftrags-ID
     *
     * @param auftragId
     * @return
     */
    public List<AuftragHousingKeyView> findAuftragHousingKeys(Long auftragId);

    /**
     * Loescht die Transponder-Gruppe mit der angegebenen {@code transponderGroupId}
     *
     * @param transponderGroupId
     */
    public void deleteTransponderGroup(Long transponderGroupId);

}
