/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 11:21:24
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import com.google.common.collect.Iterables;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wita.dao.WitaConfigDao;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaConfig;
import de.mnet.wita.model.WitaSendCount;
import de.mnet.wita.model.WitaSendLimit;

/**
 * Hibernate Implementierung von {@link WitaConfigDao}.
 */
public class WitaConfigDaoImpl extends Hibernate4DAOImpl implements WitaConfigDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public WitaSendLimit findWitaSendLimit(final String geschaeftsfallTyp, final KollokationsTyp kollokationsTyp,
            final String ituCarrierCode) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(WitaSendLimit.class)
                .add(Restrictions.eq(WitaSendLimit.GESCHAEFTSFALLTYP, geschaeftsfallTyp));
        if (kollokationsTyp != null) {
            criteria.add(Restrictions.eq(WitaSendLimit.KOLLOKATIONSTYP, kollokationsTyp));
        }
        if (ituCarrierCode != null) {
            criteria.add(Restrictions.eq(WitaSendLimit.ITU_CARRIER_CODE, ituCarrierCode));
        }
        criteria.setMaxResults(1);
        return (WitaSendLimit) Iterables.getFirst(criteria.list(), null);
    }

    @Override
    public Long getWitaSentCount(String geschaeftsfallTyp, KollokationsTyp kollokationsTyp, String ituCarrierCode) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WitaSendCount.class)
                .setProjection(Projections.rowCount())
                .add(Restrictions.eq(WitaSendCount.GESCHAEFTSFALLTYP, geschaeftsfallTyp));
        if (kollokationsTyp != null) {
            criteria.add(Restrictions.eq(WitaSendCount.KOLLOKATIONSTYP, kollokationsTyp));
        }
        if (ituCarrierCode != null) {
            criteria.add(Restrictions.eq(WitaSendCount.ITU_CARRIER_CODE, ituCarrierCode));
        }
        @SuppressWarnings("unchecked")
        List<Long> witaSentCounts = criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return Iterables.getFirst(witaSentCounts, 0L);
    }

    @Override
    public WitaConfig getWitaDefaultVersion() {
        return findWitaConfig(WitaConfig.DEFAULT_WITA_VERSION);
    }

    /**
     * Liefert die WitaConfig zu dem Key zurueck, oder null wenn keins gefunden wird
     *
     * @param key
     * @return WitaConfig
     */
    @Override
    public WitaConfig findWitaConfig(final String key) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(WitaConfig.class)
                .add(Restrictions.eq("key", key))
                .setMaxResults(1);
        WitaConfig witaConfig = (WitaConfig) Iterables.getOnlyElement(criteria.list(), null);

        if (witaConfig == null) {
            throw new WitaConfigException(String.format("Die WITA/WBCI Konfiguration '%s' ist nicht bekannt! " +
                    "Die Konfiguration muss in der Config-Tabelle (T_WITA_CONFIG) mit '%s' als key eingetragen werden.", key, key));
        }

        return witaConfig;
    }

    @Override
    public String getWitaConfigValue(String key) {
        WitaConfig witaConfig = findWitaConfig(key);
        return witaConfig.getValue();
    }

    @Override
    public void resetWitaSentCount(GeschaeftsfallTyp geschaeftsfallTyp, KollokationsTyp kollokationsTyp) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + WitaSendCount.class.getName() + " sc " +
                "where sc." + WitaSendCount.GESCHAEFTSFALLTYP + " = :gfTyp and sc." + WitaSendCount.KOLLOKATIONSTYP + " = :kTyp");
        query.setParameter("gfTyp", geschaeftsfallTyp.name());
        query.setParameter("kTyp", kollokationsTyp);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
