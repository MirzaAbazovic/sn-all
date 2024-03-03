/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2004 09:53:43
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.sql.Date;
import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.EndstelleLtgDatenDAO;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;


/**
 *
 */
public class EndstelleLtgDatenDAOImpl extends HurricanHibernateDaoImpl implements EndstelleLtgDatenDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public EndstelleLtgDaten findByEsId(Long esId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(EndstelleLtgDaten.class.getName());
        hql.append(" ltg where ltg.endstelleId=? and ltg.gueltigVon<=? and ltg.gueltigBis>?");

        Date now = DateTools.getActualSQLDate();
        List result = find(hql.toString(), esId, now, now);
        if ((result != null) && (!result.isEmpty())) {
            return (EndstelleLtgDaten) result.get(result.size() - 1);
        }

        return null;
    }

    @Override
    public EndstelleLtgDaten update4History(EndstelleLtgDaten obj4History, Serializable id, java.util.Date gueltigBis) {
        return update4History(obj4History, new EndstelleLtgDaten(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


