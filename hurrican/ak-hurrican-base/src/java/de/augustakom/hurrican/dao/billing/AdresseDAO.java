/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 10:48:12
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.Adresse;


/**
 * DAO-Definition zur Verwaltung von Objekten vom Typ <code>de.augustakom.hurrican.model.billing.Adresse</code>
 *
 *
 */
public interface AdresseDAO extends FindDAO {

    /**
     * Sucht nach den Adressen anhand der AdresseNo.
     *
     * @param aNos
     * @return
     */
    public List<Adresse> findByANos(List<Long> aNos);

}


