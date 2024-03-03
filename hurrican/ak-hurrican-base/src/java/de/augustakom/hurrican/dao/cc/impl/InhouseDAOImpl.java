/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2004 08:24:24
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.InhouseDAO;
import de.augustakom.hurrican.model.cc.Inhouse;


/**
 * Hibernate DAO-Implementierung von <code>InhouseDAO</code>.
 *
 *
 */
public class InhouseDAOImpl extends HurricanHibernateDaoImpl implements InhouseDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Inhouse findByEsId(Long esId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Inhouse.class.getName());
        hql.append(" i where i.endstelleId=? and i.gueltigVon<=? and i.gueltigBis>?");

        Date now = DateTools.getActualSQLDate();
        @SuppressWarnings("unchecked")
        List<Inhouse> result = find(hql.toString(), new Object[] { esId, now, now });

        return ((result != null) && (!result.isEmpty())) ? result.get(result.size() - 1) : null;
    }

    @Override
    public Inhouse update4History(Inhouse obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new Inhouse(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


