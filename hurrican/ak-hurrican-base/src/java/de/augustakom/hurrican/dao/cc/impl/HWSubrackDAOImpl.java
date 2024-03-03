/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2009 16:25:51
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HWSubrackDAO;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;


/**
 * Hibernate DAO-Implementierung fuer HWSubrack-Objekte.
 *
 *
 */
public class HWSubrackDAOImpl extends Hibernate4DAOImpl implements HWSubrackDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<HWSubrack> findSubracksForRack(final Long rackId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(HWSubrack.class);
        criteria.add(Restrictions.eq(HWSubrack.RACK_ID, rackId));

        @SuppressWarnings("unchecked")
        List<HWSubrack> result = criteria.list();
        return result;
    }

    @Override
    public List<HWSubrack> findSubracksForStandort(final Long hvtIdStandort) {
        Session session = sessionFactory.getCurrentSession();
        // Get Racks...
        Criteria criteria = session.createCriteria(HWRack.class);
        criteria.add(Restrictions.eq(HWRack.HVT_STANDORT_ID, hvtIdStandort));
        @SuppressWarnings("unchecked")
        List<HWRack> racks = criteria.list();
        if (!racks.isEmpty()) {
            List<Long> rackIds = new ArrayList<Long>(racks.size());
            for (HWRack hwRack : racks) {
                rackIds.add(hwRack.getId());
            }
            // ... and subracks ...
            criteria = session.createCriteria(HWSubrack.class);
            criteria.add(Restrictions.in(HWSubrack.RACK_ID, rackIds));
            @SuppressWarnings("unchecked")
            List<HWSubrack> result = criteria.list();
            return result;
        }
        else {
            return new ArrayList<HWSubrack>();
        }
    }

    @Override
    public List<HWSubrackTyp> findSubrackTypes(final String rackType) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(HWSubrackTyp.class);
        if (rackType != null) {
            criteria.add(Restrictions.eq(HWSubrackTyp.RACK_TYP, rackType));
        }

        @SuppressWarnings("unchecked")
        List<HWSubrackTyp> result = criteria.list();
        return result;
    }

    @Nonnull
    @Override
    public List<HWSubrack> findHwSubracksByRackIdAndModNumber(Long rackId, String modNumber) {
        DetachedCriteria criteria = DetachedCriteria.forClass(HWSubrack.class);
        criteria.add(Property.forName(HWSubrack.RACK_ID).eq(rackId));
        criteria.add(Property.forName(HWSubrack.MOD_NUMBER).eq(modNumber));
        @SuppressWarnings("unchecked")
        List<HWSubrack> result = criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


