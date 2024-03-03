/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2008 10:22:05
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDNDAO;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.VoipDnPlan;

/**
 * Hibernate DAO-Implementierung von <code>AuftragVoIPDNDAO</code>.
 *
 *
 */
public class AuftragVoIPDNDAOImpl extends Hibernate4DAOImpl implements AuftragVoIPDNDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<AuftragVoIPDN> findAuftragVoIPDN(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(AuftragVoIPDN.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragId", auftragId);
        List<AuftragVoIPDN> results = criteria.list();
        return results;
    }

    @Override
    public AuftragVoIPDN findByAuftragIDDN(final Long auftragId, final Long dnNoOrig) {
        if(auftragId == null && dnNoOrig == null) {
            // there is no point in executing the query since it will never return just 1 result (does a select *)
            // so we can just return null straight away
            return null;
        }

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(AuftragVoIPDN.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragId", auftragId);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "dnNoOrig", dnNoOrig);

        List<AuftragVoIPDN> results = criteria.list();
        if ((results == null) || (results.isEmpty()) || (results.size() > 1)) {
            return null;
        }
        else {
            return results.get(0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AuftragVoIPDN2EGPort> findAuftragVoIpdn2EgPorts(final Long auftragVoipDnId, final Date validAt) {
        final DetachedCriteria criteria = DetachedCriteria.forClass(AuftragVoIPDN2EGPort.class)
                .add(Property.forName("auftragVoipDnId").eq(auftragVoipDnId))
                .add(Property.forName("validFrom").le(validAt))
                .add(Property.forName("validTo").gt(validAt));
        return (List<AuftragVoIPDN2EGPort>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public void deleteVoipDnPlan(@Nonnull final VoipDnPlan voipDnPlan) {
        sessionFactory.getCurrentSession().delete(voipDnPlan);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
