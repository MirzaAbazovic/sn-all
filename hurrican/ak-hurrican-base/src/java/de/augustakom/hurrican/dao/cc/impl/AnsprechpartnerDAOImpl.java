/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 11:58:39
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AnsprechpartnerDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;


/**
 * Hibernate DAO-Implementierung von {@link AnsprechpartnerDAO}.
 *
 *
 */
public class AnsprechpartnerDAOImpl extends Hibernate4DAOImpl implements AnsprechpartnerDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Ansprechpartner> findAnsprechpartner(final Ansprechpartner.Typ type, final Long auftragId, final boolean preferred) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Ansprechpartner.class);
        if (type != null) {
            criteria.add(Restrictions.eq(Ansprechpartner.TYPE_REF_ID, type.refId()));
        }
        criteria.add(Restrictions.eq(Ansprechpartner.AUFTRAG_ID, auftragId));
        if (preferred) {
            criteria.add(Restrictions.eq(Ansprechpartner.PREFERRED, Boolean.TRUE));
        }
        @SuppressWarnings("unchecked")
        List<Ansprechpartner> list = criteria.list();
        return list;
    }

    @Override
    public void deleteAnsprechpartner(Ansprechpartner ansprechpartner) {
        sessionFactory.getCurrentSession().delete(ansprechpartner);
    }

    @Override
    public List<Ansprechpartner> findAnsprechpartnerForAddress(final CCAddress address) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Ansprechpartner.class);
        criteria.add(Restrictions.eq(Ansprechpartner.ADDRESS, address));
        @SuppressWarnings("unchecked")
        List<Ansprechpartner> list = criteria.list();
        return list;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CCAnsprechpartnerView> findAnsprechpartnerByKundeNoAndBuendelNo(final Long kundeNo, final Integer buendelNr) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CCAnsprechpartnerView.class);
        criteria.add(Restrictions.eq("kundeNo", kundeNo));
        if (buendelNr != null) {
            criteria.add(Restrictions.eq("buendelNr", buendelNr));
        }
        return criteria.list();
    }

}
