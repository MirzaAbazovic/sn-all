/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2010 16:25:46
 */

package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.service.base.exceptions.FindException;

public interface MailDAO extends StoreDAO, FindDAO {

    List<Mail> findAllPendingMails() throws FindException;

}
