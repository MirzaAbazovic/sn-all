/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 13:15:38
 */
package de.mnet.wita.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wita.activiti.BusinessKeyUtils;
import de.mnet.wita.model.IoArchive;

/**
 * DAO Interface fuer die Verwaltung von {@link IoArchive} Objekten.
 */
public interface IoArchiveDao extends FindDAO, StoreDAO, ByExampleDAO {

    /**
     * @return List of IoArchive entries corresponding to the extOrderNo sorted by request_timestamp ascending (is aware
     * of {@link BusinessKeyUtils#KUEDT_SUFFIX})
     */
    List<IoArchive> findIoArchivesForExtOrderNo(String extOrderNo);

    /**
     * @return List of IoArchive entries corresponding to the extOrderNos sorted by request_timestamp ascending (is
     * aware of {@link BusinessKeyUtils#KUEDT_SUFFIX})
     */
    List<IoArchive> findIoArchivesForExtOrderNos(Collection<String> extOrderNos);

    /**
     * @return List of IoArchive entries corresponding to the vertragsnummer sorted by request_timestamp ascending (is
     * aware of {@link BusinessKeyUtils#KUEDT_SUFFIX})
     */
    List<IoArchive> findIoArchivesForVertragsnummer(String vertragsnummer);

}
