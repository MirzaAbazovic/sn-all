/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 14:50:19
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ArrayTools;
import de.augustakom.hurrican.dao.cc.LockDAO;
import de.augustakom.hurrican.model.cc.Lock;


/**
 *
 */
public class LockDAOImpl extends Hibernate4DAOImpl implements LockDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Lock> findByAuftragId(Long auftragId, boolean onlyActive) {
        StringBuilder hql = new StringBuilder();
        Object[] params = new Object[] { auftragId };
        hql.append("from ");
        hql.append(Lock.class.getName());
        hql.append(" s where s.auftragId=?");
        if (onlyActive) {
            hql.append(" and s.lockStateRefId=?");
            params = ArrayTools.add(params, Lock.REF_ID_LOCK_STATE_ACTIVE);
        }
        hql.append(" order by s.id, s.createdAt");
        @SuppressWarnings("unchecked")
        List<Lock> result = find(hql.toString(), params);
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
