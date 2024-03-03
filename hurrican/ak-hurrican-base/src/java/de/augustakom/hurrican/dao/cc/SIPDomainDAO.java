/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 15:45:21
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;

/**
 * DAO-Interface fuer die Verwaltung von <code>SIP Domaenen</code>.
 */
public interface SIPDomainDAO extends ByExampleDAO, StoreDAO {

    /**
     * Loescht die {@code Produkt2SIPDomain} Entität.
     */
    void deleteProdukt2SIPDomain(Produkt2SIPDomain toDelete);

    /**
     * Sucht via Example Objekt und berücksichtigt Referenzen. Nur Produktkonfigurationen (Produkt und Switch
     * konfiguriert)
     */

    List<Produkt2SIPDomain> querySIPDomain4Produkt(Produkt2SIPDomain example);

    /**
     * Sucht via Example Objekt und berücksichtigt Referenzen. Nur Endgeraetekonfigurationen (Switch und Endgeraet
     * konfiguriert)
     */
    List<EGType2SIPDomain> querySIPDomain4Eg(EGType2SIPDomain example);
}


