/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 17:19:24
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.DSLAMProfileDAO;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Produkt2DSLAMProfile;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * Hibernate DAO-Implementierung von <code>DSLAMProfileDAO</code>.
 *
 *
 */
public class DSLAMProfileDAOImpl extends Hibernate4DAOImpl implements DSLAMProfileDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type) {
        if (type.equals(Auftrag2DSLAMProfile.class)) {
            return getByExampleDAO().queryByExample(example, type, new String[] { AbstractCCHistoryModel.GUELTIG_BIS }, null);
        }
        else {
            return getByExampleDAO().queryByExample(example, type);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DSLAMProfile> findDSLAMProfiles4Produkt(Long prodId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Produkt2DSLAMProfile.class.getName());
        hql.append(" p2d where p2d.prodId=?");

        List<Produkt2DSLAMProfile> p2dslamProf = find(hql.toString(), new Object[] { prodId });
        if (CollectionTools.isNotEmpty(p2dslamProf)) {
            final List<Long> dslamProfileIds = new ArrayList<>();
            for (Produkt2DSLAMProfile p2d : p2dslamProf) {
                dslamProfileIds.add(p2d.getDslamProfileId());
            }

            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(DSLAMProfile.class);
            criteria.add(Restrictions.in(AbstractCCIDModel.ID, dslamProfileIds));
            return criteria.list();
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DSLAMProfile> findDSLAMProfiles4Auftrag(Long auftragId) {
        DetachedCriteria a2pCriteria = DetachedCriteria.forClass(Auftrag2DSLAMProfile.class)
                .add(Property.forName(Auftrag2DSLAMProfile.AUFTRAG_ID).eq(auftragId))
                .setProjection(Projections.property(Auftrag2DSLAMProfile.DSLAM_PROFILE_ID));

        DetachedCriteria criteria = DetachedCriteria.forClass(DSLAMProfile.class);
        criteria.add(Property.forName(DSLAMProfile.ID).in(a2pCriteria));
        return criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DSLAMProfile> findDSLAMProfiles4BaugruppenTyp(final HWBaugruppenTyp hwBaugruppenTyp) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(DSLAMProfile.class);
        criteria.add(Restrictions.eq(DSLAMProfile.GUELTIG, Boolean.TRUE));
        criteria.add(Restrictions.eq(DSLAMProfile.BAUGRUPPEN_TYP_ID, hwBaugruppenTyp.getId()));
        criteria.addOrder(Order.asc(DSLAMProfile.DOWNSTREAM));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DSLAMProfile> findByParams(final DSLAMProfile fromParams) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(DSLAMProfile.class);
        criteria.add(Restrictions.eq(DSLAMProfile.DOWNSTREAM, fromParams.getBandwidth().getDownstream()));
        criteria.add(Restrictions.eq(DSLAMProfile.UPSTREAM, fromParams.getBandwidth().getUpstream()));
        if (!fromParams.getL2PowersafeEnabled()) {
            criteria.add(Restrictions.or(
                    Restrictions.eq(DSLAMProfile.L2POWERSAFE_ENABLED, fromParams.getL2PowersafeEnabled()),
                    Restrictions.isNull(DSLAMProfile.L2POWERSAFE_ENABLED)));
        }
        else {
            criteria.add(Restrictions.eq(DSLAMProfile.L2POWERSAFE_ENABLED, fromParams.getL2PowersafeEnabled()));
        }
        if (!fromParams.getForceADSL1()) {
            criteria.add(Restrictions.or(
                    Restrictions.eq(DSLAMProfile.FORCE_ADSL1, fromParams.getForceADSL1()),
                    Restrictions.isNull(DSLAMProfile.FORCE_ADSL1)));
        }
        else {
            criteria.add(Restrictions.eq(DSLAMProfile.FORCE_ADSL1, fromParams.getForceADSL1()));
        }
        criteria.add(Restrictions.eq(DSLAMProfile.FASTPATH, fromParams.getFastpath()));
        if (fromParams.getTmDown() != null) {
            criteria.add(Restrictions.eq(DSLAMProfile.TM_DOWN, fromParams.getTmDown()));
        }
        else {
            criteria.add(Restrictions.isNull(DSLAMProfile.TM_DOWN));
        }
        if (fromParams.getTmUp() != null) {
            criteria.add(Restrictions.eq(DSLAMProfile.TM_UP, fromParams.getTmUp()));
        }
        else {
            criteria.add(Restrictions.isNull(DSLAMProfile.TM_UP));
        }
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DSLAMProfile> findDSLAMProfiles(final Long baugruppenTyp, final Boolean fastpath, final Collection<String> uetvsAllowed) {
        Session session = sessionFactory.getCurrentSession();
        // @formatter:off
        Criteria criteria = session.createCriteria(DSLAMProfile.class);
        criteria.add(Restrictions.or(
                    Restrictions.eq(DSLAMProfile.BAUGRUPPEN_TYP_ID, baugruppenTyp),
                    Restrictions.isNull(DSLAMProfile.BAUGRUPPEN_TYP_ID)))
                .add(Restrictions.eq(DSLAMProfile.FASTPATH, fastpath))
                .add((CollectionTools.isNotEmpty(uetvsAllowed))
                        ? Restrictions.in(DSLAMProfile.UETV, uetvsAllowed)
                        : Restrictions.isNull(DSLAMProfile.UETV))
                .add(Restrictions.eq(DSLAMProfile.ENABLED_FOR_AUTOCHANGE, Boolean.TRUE))
                .add(Restrictions.eq(DSLAMProfile.GUELTIG, Boolean.TRUE))
                .addOrder(Order.asc(DSLAMProfile.BAUGRUPPEN_TYP_ID));
        // @formatter:on
        return criteria.list();
    }

    @Override
    public void deleteAuftrag2DSLAMProfileById(Long id) {
        sessionFactory.getCurrentSession().delete(findById(id, Auftrag2DSLAMProfile.class));
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


