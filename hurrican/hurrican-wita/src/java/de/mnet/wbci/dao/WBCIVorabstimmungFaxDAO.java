/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.2017
 */
package de.mnet.wbci.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.mnet.wbci.model.RequestTyp;

public interface WBCIVorabstimmungFaxDAO extends StoreDAO, FindAllDAO {
    void delete(Collection<WBCIVorabstimmungFax> ids);

    WBCIVorabstimmungFax findByVorabstimmungsID(String id);

    List<WBCIVorabstimmungFax> findAll(long auftragsId, RequestTyp requestTyp);
    List<WBCIVorabstimmungFax> findAll(long auftragsId);
}