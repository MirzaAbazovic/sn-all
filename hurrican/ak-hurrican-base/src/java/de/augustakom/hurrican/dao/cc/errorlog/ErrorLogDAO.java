package de.augustakom.hurrican.dao.cc.errorlog;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.ErrorLogEntry;

/**
 * DAO for {@link de.augustakom.hurrican.model.cc.ErrorLogEntry} entity and T_ERROR_LOG table
 *
 */
public interface ErrorLogDAO extends FindDAO, ByExampleDAO, StoreDAO {
    List<ErrorLogEntry> findByService(String service);
}
