/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 13:15:53
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wita.activiti.BusinessKeyUtils;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.model.IoArchive;

/**
 * Hibernate DAO Implementierung von {@link IoArchiveDao}.
 */
public class IoArchiveDaoImpl extends Hibernate4DAOImpl implements IoArchiveDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<IoArchive> findIoArchivesForExtOrderNo(String extOrderNo) {
        return findIoArchivesForExtOrderNos(ImmutableSet.of(extOrderNo));
    }

    @Override
    public List<IoArchive> findIoArchivesForExtOrderNos(final Collection<String> extOrderNos) {
        if (extOrderNos.isEmpty()) {
            return Lists.newArrayList();
        }

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(IoArchive.class);

        Set<String> searchExtOrderNos = Sets.newHashSet(extOrderNos);
        for (String extOrderNo : extOrderNos) {
            searchExtOrderNos.add(BusinessKeyUtils.getKueDtBusinesskey(extOrderNo));
        }

        criteria.add(Restrictions.in(IoArchive.WITA_EXT_ORDER_NO, searchExtOrderNos.toArray()));
        criteria.addOrder(Order.asc(IoArchive.REQUEST_TIMESTAMP));
        @SuppressWarnings("unchecked")
        List<IoArchive> result = criteria.list();
        return result;
    }

    @Override
    public List<IoArchive> findIoArchivesForVertragsnummer(final String vertragsnummer) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(IoArchive.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, IoArchive.WITA_VERTRAGSNUMMER, vertragsnummer);
        criteria.addOrder(Order.asc(IoArchive.REQUEST_TIMESTAMP));
        @SuppressWarnings("unchecked")
        List<IoArchive> result = criteria.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
