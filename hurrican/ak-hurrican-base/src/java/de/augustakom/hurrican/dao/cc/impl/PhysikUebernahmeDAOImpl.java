/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 08:02:59
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.PhysikUebernahmeDAO;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;


/**
 * Hibernate DAO-Implementierung von <code>PhysikUebernahmeDAO</code>.
 *
 *
 */
public class PhysikUebernahmeDAOImpl extends Hibernate4DAOImpl implements PhysikUebernahmeDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Long getMaxVorgangId() {
        StringBuilder hql = new StringBuilder("select max(pu.vorgang) from ");
        hql.append(PhysikUebernahme.class.getName()).append(" pu");

        List<Long> result = find(hql.toString());
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public PhysikUebernahme findLast4AuftragA(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(PhysikUebernahme.class);
        criteria.add(Restrictions.eq("auftragIdA", auftragId));
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(1);

        List tmp = criteria.list();
        return ((tmp != null) && (!tmp.isEmpty())) ? (PhysikUebernahme) tmp.get(0) : null;
    }

    @Override
    public PhysikUebernahme findByVerlaufId(Long auftragId, Long verlaufId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(PhysikUebernahme.class.getName());
        hql.append(" pu where pu.auftragIdA=? and pu.verlaufId=?");

        List<PhysikUebernahme> result = find(hql.toString(), auftragId, verlaufId);
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public PhysikaenderungsTyp findPhysikaenderungsTyp(Long auftragId, Long verlaufId) {
        PhysikUebernahme pu = findByVerlaufId(auftragId, verlaufId);
        if ((pu != null) && (pu.getAenderungstyp() != null)) {
            StringBuilder hql = new StringBuilder("from ");
            hql.append(PhysikaenderungsTyp.class.getName());
            hql.append(" pt where pt.id=?");

            List<PhysikaenderungsTyp> result = find(hql.toString(), pu.getAenderungstyp());
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


