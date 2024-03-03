/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2017 11:43:13
 */

package de.mnet.hurrican.wholesale.dao.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.wholesale.dao.WholesaleAuditDAO;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;


/**
 * Hibernate DAO-Implementierung von {@link WholesaleAuditDAO}.
 */
@Repository
public class WholesaleAuditDAOImpl extends Hibernate4DAOImpl implements WholesaleAuditDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;


    @Override
    public WholesaleAudit findById(Long id) throws FindException {
        return null;
    }


    public List<WholesaleAudit> findByVorabstimmungsId(String vorabstimmungsId) throws FindException {
        DetachedCriteria criteria = DetachedCriteria.forClass(WholesaleAudit.class);
        criteria.add(Restrictions.eq("vorabstimmungsId", vorabstimmungsId));
       return  (List<WholesaleAudit>) criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
