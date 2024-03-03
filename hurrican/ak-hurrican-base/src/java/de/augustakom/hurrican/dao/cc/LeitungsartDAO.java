/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 09:46:20
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.Leitungsart;


/**
 * DAO-Interface fuer Objekte des Typs <code>Leitungsart</code>
 *
 *
 */
public interface LeitungsartDAO extends FindAllDAO, FindDAO, ByExampleDAO {

    /**
     * Sucht nach einer Leitungsart ueber eine Endstellen-ID.
     *
     * @param esId ID der Endstelle
     * @return Instanz von <code>Leitungsart</code>.
     */
    public Leitungsart findByEsId(Long esId);

}


