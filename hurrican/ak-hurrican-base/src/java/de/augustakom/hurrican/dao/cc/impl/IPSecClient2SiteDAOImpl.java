/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2009 11:49:56
 */

package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.IPSecClient2SiteDAO;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 *
 */
public class IPSecClient2SiteDAOImpl extends Hibernate4DAOImpl implements IPSecClient2SiteDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<IPSecClient2SiteToken> findAllClient2SiteTokens() {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecClient2SiteToken.class);
        @SuppressWarnings("unchecked")
        List<IPSecClient2SiteToken> result = crit.list();
        return result;
    }

    @Override
    public List<IPSecClient2SiteToken> findAllClient2SiteTokens(final String serialNumber) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecClient2SiteToken.class);
        CriteriaHelper.addExpression(crit,
                CriteriaHelper.LIKE, IPSecClient2SiteToken.SERIAL_NUMBER,
                WildcardTools.replaceWildcards(serialNumber));

        @SuppressWarnings("unchecked")
        List<IPSecClient2SiteToken> result = crit.list();
        return result;
    }

    @Override
    public List<IPSecClient2SiteToken> findFreeClient2SiteTokens() throws FindException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecClient2SiteToken.class);
        crit.add(Restrictions.isNull(IPSecClient2SiteToken.AUFTRAG_ID));
        @SuppressWarnings("unchecked")
        List<IPSecClient2SiteToken> result = crit.list();
        return result;
    }

    @Override
    public List<IPSecClient2SiteToken> findFreeClient2SiteTokens(final String serialNumber)
            throws FindException {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecClient2SiteToken.class);
        crit.add(Restrictions.isNull(IPSecClient2SiteToken.AUFTRAG_ID));
        CriteriaHelper.addExpression(crit,
                CriteriaHelper.LIKE, IPSecClient2SiteToken.SERIAL_NUMBER,
                WildcardTools.replaceWildcards(serialNumber));

        @SuppressWarnings("unchecked")
        List<IPSecClient2SiteToken> result = crit.list();
        return result;
    }

    @Override
    public List<IPSecClient2SiteToken> findClient2SiteTokens(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(IPSecClient2SiteToken.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, IPSecClient2SiteToken.AUFTRAG_ID, auftragId);
        @SuppressWarnings("unchecked")
        List<IPSecClient2SiteToken> result = crit.list();
        return result;
    }

    @Override
    public void deleteClient2SiteToken(IPSecClient2SiteToken token) throws StoreException {
        sessionFactory.getCurrentSession().delete(token);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
