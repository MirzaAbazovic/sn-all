/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:55:18
 */

package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;


public interface AuftragHousingDAO extends FindDAO, FindAllDAO, StoreDAO {

    /**
     * Ermittelt das AuftragHousing-Objekt zu einem Auftrag. Es wird nur der zuletzt gueltige Datensatz ermittelt.
     *
     * @param auftragId
     * @return AuftragHousing
     */
    public AuftragHousing findAuftragHousing(Long auftragId);

    /**
     * Ermittelt das HousingBuilding-Objekt für einen Auftrags-ID.
     *
     * @param Integer auftragsId
     * @return HousingBuilding
     */
    public HousingBuilding findHousingBuilding4Auftrag(Long auftragId);

}
