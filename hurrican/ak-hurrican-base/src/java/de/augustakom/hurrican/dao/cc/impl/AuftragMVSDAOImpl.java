/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:56:35
 */

package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragMVSDAO;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;

public class AuftragMVSDAOImpl extends Hibernate4DAOImpl implements AuftragMVSDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T extends AuftragMVS> T find4Auftrag(final Long auftragId, final Class<T> clazz) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(clazz);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragId", auftragId);
        @SuppressWarnings("unchecked")
        List<T> result = criteria.list();
        return Iterables.getOnlyElement(result, null);
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.AuftragMVSDAO#findAllUsedDomains()
     */
    @Override
    public Collection<String> findAllUsedDomains() {
        Session session = sessionFactory.getCurrentSession();
        final String property = "domain";
        Criteria criteria = session.createCriteria(AuftragMVSEnterprise.class);
        criteria.setProjection(Projections.distinct(Projections.property(property)));
        criteria.add(Restrictions.isNotNull(property));
        @SuppressWarnings("unchecked")
        Collection<String> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.AuftragMVSDAO#findAllUsedSubdomains(AuftragMVSEnterprise)
     */
    @Override
    public Collection<String> findAllUsedSubdomains(final AuftragMVSEnterprise mvsEnterprise) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(AuftragMVSSite.class);
        criteria.setProjection(Projections.property("subdomain"));
        criteria.add(Restrictions.eq("parent", mvsEnterprise));
        @SuppressWarnings("unchecked")
        Collection<String> result = criteria.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
} // end
