/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 09:46:52
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.LeitungsartDAO;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Leitungsart;


/**
 * Hibernate DAO-Implementierung von LeitungsartDAO.
 *
 *
 */
public class LeitungsartDAOImpl extends Hibernate4FindDAOImpl implements LeitungsartDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Leitungsart findByEsId(Long esId) {
        StringBuilder hql = new StringBuilder("select l.id, l.name from ");
        hql.append(Leitungsart.class.getName()).append(" l, ");
        hql.append(EndstelleLtgDaten.class.getName()).append(" ltg ");
        hql.append(" where ltg.leitungsartId=l.id and ltg.endstelleId=? ");
        hql.append(" and ltg.gueltigVon<=? and ltg.gueltigBis>?");

        Date now = new Date();
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameters(new Object[] { esId, now, now }, new Type[] {new LongType(), new DateType(), new DateType()});
        List<Object[]> result = query.list();
        if ((result != null) && (result.size() == 1)) {
            Object[] values = result.get(0);
            Leitungsart lart = new Leitungsart();
            lart.setId(ObjectTools.getLongSilent(values, 0));
            lart.setName(ObjectTools.getStringSilent(values, 1));
            return lart;
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


