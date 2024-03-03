/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2012 15:56:37
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.shared.view.TvFeedView;

/**
 * DAO-Interface fuer Objekte des Typs <code>de.augustakom.hurrican.model.cc.AuftragTvGeoId</code>
 */
public interface AuftragTvDAO extends FindDAO {

    /**
     * Sucht alle {@link TvFeedView} fuer die gegebene List an {@code auftragIds}
     *
     * @param auftragIds
     * @return
     */
    public List<TvFeedView> findTvFeed4Auftraege(List<Long> auftragIds, List<Integer> buendelNrs);
}


