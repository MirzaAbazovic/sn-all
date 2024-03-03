/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 16:40:51
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;

/**
 * Interface zur Definition der DAO-Methoden, um die Konfiguration von Regular Expressions zu verwalten.
 *
 *
 */
public interface CfgRegularExpressionDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach einer Regular Expression. Id oder Name kann {@code null} sein, mindestens einer der beiden Werte muss
     * aber gesetzt sein.
     *
     * @param refId         Die ID des Objektes, fuer das die Regular Expression genutzt wird
     * @param refName       Der Name des Objektes, fuer das die Regular Expression genutzt wird
     * @param refClass      Die Klasse des Objektes, fuer das die Regular Expression genutzt wird
     * @param requestedInfo Die Information, die mit der regular Expression gesucht werden soll
     * @return die Regular Expression, oder {@code null}
     */
    public CfgRegularExpression findRegularExpression(Long refId, String refName,
            Class<?> refClass, CfgRegularExpression.Info requestedInfo);

}


