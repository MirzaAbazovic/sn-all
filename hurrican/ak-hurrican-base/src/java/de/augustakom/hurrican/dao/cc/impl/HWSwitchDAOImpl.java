/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 15:05:57
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HWSwitchDAO;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Hibernate Implementierung von {@link HWSwitchDAO}.
 *
 *
 * @since Release 10
 */
@Repository
public class HWSwitchDAOImpl extends Hibernate4DAOImpl implements HWSwitchDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public HWSwitch findSwitchByName(final String name) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(HWSwitch.class);
        criteria.add(Restrictions.eq("name", name));
        @SuppressWarnings("unchecked")
        List<HWSwitch> result = criteria.list();
        if (result.isEmpty()) {
            return null;
        }
        else if (result.size() > 1) {
            throw new IllegalStateException(String.format("Fuer den Namen '%s' wurden mehrere Switches gefunden!", name));
        }
        return result.get(0);
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.HWSwitchDAO#findAllSwitches()
     */
    @Override
    public List<HWSwitch> findAllSwitches() {
        return findAll(HWSwitch.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HWSwitch> findSwitchesByType(final HWSwitchType... types) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(HWSwitch.class);
        criteria.add(Restrictions.in("type", types));
        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
