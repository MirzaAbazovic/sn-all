/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 15:46:47
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.SIPDomainDAO;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;

/**
 * Hibernate DAO-Implementierung von <code>SIP Domaenen</code>.
 */
public class SIPDomainDAOImpl extends Hibernate4DAOImpl implements SIPDomainDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteProdukt2SIPDomain(Produkt2SIPDomain toDelete) {
        sessionFactory.getCurrentSession().delete(toDelete);
    }

    @Override
    public List<Produkt2SIPDomain> querySIPDomain4Produkt(Produkt2SIPDomain example) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Produkt2SIPDomain.class);

        // Preconditions
        criteria.add(Restrictions.isNotNull(Produkt2SIPDomain.TYPE_PROD_ID));
        criteria.add(Restrictions.isNotNull(Produkt2SIPDomain.TYPE_HWSWITCH_ID));

        if (example.getProdId() != null) {
            criteria.add(Restrictions.eq(Produkt2SIPDomain.TYPE_PROD_ID, example.getProdId()));
        }
        if ((example.getHwSwitch() != null) && (example.getHwSwitch().getId() != null)) {
            criteria.add(Restrictions.eq(Produkt2SIPDomain.TYPE_HWSWITCH_ID, example.getHwSwitch().getId()));
        }
        if ((example.getSipDomainRef() != null) && (example.getSipDomainRef().getId() != null)) {
            criteria.add(Restrictions.eq(Produkt2SIPDomain.TYPE_SIPDOMAINREF_ID, example.getSipDomainRef().getId()));
        }
        if (example.getDefaultDomain() != null) {
            criteria.add(Restrictions.eq(Produkt2SIPDomain.TYPE_DEFAULT_DOMAIN, example.getDefaultDomain()));
        }
        //noinspection unchecked
        return criteria.list();
    }

    @Override
    public List<EGType2SIPDomain> querySIPDomain4Eg(EGType2SIPDomain example) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EGType2SIPDomain.class);

        // Preconditions
        criteria.add(Restrictions.isNotNull(EGType2SIPDomain.TYPE_EGTYPE_ID));
        criteria.add(Restrictions.isNotNull(EGType2SIPDomain.TYPE_HWSWITCH_ID));

        if (example.getEgType() != null && (example.getEgType().getId() != null)) {
            criteria.add(Restrictions.eq(EGType2SIPDomain.TYPE_EGTYPE_ID, example.getEgType().getId()));
        }
        if ((example.getHwSwitch() != null) && (example.getHwSwitch().getId() != null)) {
            criteria.add(Restrictions.eq(EGType2SIPDomain.TYPE_HWSWITCH_ID, example.getHwSwitch().getId()));
        }
        if ((example.getSipDomainRef() != null) && (example.getSipDomainRef().getId() != null)) {
            criteria.add(Restrictions.eq(EGType2SIPDomain.TYPE_SIPDOMAINREF_ID, example.getSipDomainRef().getId()));
        }
        //noinspection unchecked
        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
