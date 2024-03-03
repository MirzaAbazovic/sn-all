/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:55:18
 */

package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;


public interface AuftragMVSDAO extends FindDAO, FindAllDAO, StoreDAO {

    /**
     * Ermittelt den MVS Auftrag zur gegebenen Klasse und AuftragsId.
     */
    <T extends AuftragMVS> T find4Auftrag(Long auftragId, Class<T> clazz);

    /**
     * Ermittelt alle Domains, die fuer die aktuellen {@link AuftragMVS} gespeichert wurden.
     *
     * @return
     */
    Collection<String> findAllUsedDomains();

    /**
     * Ermittelt alle Subdomains, die fuer den angegebenen {@link AuftragMVSEnterprise} gespeichert wurden.
     *
     * @return
     */
    Collection<String> findAllUsedSubdomains(AuftragMVSEnterprise mvsEnterprise);

} // end
