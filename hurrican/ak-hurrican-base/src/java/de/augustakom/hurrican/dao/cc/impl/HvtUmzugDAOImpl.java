/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.HvtUmzugDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;

/**
 * Hibernate DAO-Implementierung von {@link de.augustakom.hurrican.dao.cc.HvtUmzugDAO}
 */
public class HvtUmzugDAOImpl extends Hibernate4DAOImpl implements HvtUmzugDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public List<Pair<Long, String>> findAuftraegeAndEsTypForHvtUmzug(Long hvtUmzugId) {
        final StringBuilder hql = new StringBuilder("select ad.auftragId, e.endstelleTyp from ");
        hql.append(HvtUmzug.class.getName()).append(" h, ");
        hql.append(GeoId2TechLocation.class.getName()).append(" geo2tl, ");
        hql.append(GeoId.class.getName()).append(" geoid, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append(" where h.id= :hvtUmzugId and h.hvtStandort=geo2tl.hvtIdStandort ");
        hql.append(" and geo2tl.geoId=geoid.id and h.kvzNr=geoid.kvz ");
        hql.append(" and geoid.id=e.geoId ");
        hql.append(" and e.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append(" and at.auftragId=ad.auftragId and ad.statusId< :gekuendigt ");
        hql.append(" and ad.statusId not in (:storno, :absage) ");
        hql.append(" and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and ad.gueltigVon<= :now and ad.gueltigBis> :now ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("hvtUmzugId", hvtUmzugId);
        q.setLong("gekuendigt", AuftragStatus.AUFTRAG_GEKUENDIGT);
        q.setLong("storno", AuftragStatus.STORNO);
        q.setLong("absage", AuftragStatus.ABSAGE);
        q.setDate("now", new Date());

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<Pair<Long, String>> retVal = new ArrayList<>();
            for (Object[] values : result) {
                Long auftragId = ObjectTools.getLongSilent(values, 0);
                String esTyp = ObjectTools.getStringSilent(values, 1);

                retVal.add(Pair.create(auftragId, esTyp));
            }
            return retVal;
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HvtUmzug> findHvtUmzuegeWithStatus(final HvtUmzugStatus... status) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(HvtUmzug.class);
        criteria.add(Restrictions.in("status", status));
        return (List<HvtUmzug>) criteria.list();
    }

    @Override
    public Set<Long> findAffectedStandorte4UmzugWithoutStatus(HvtUmzugStatus... status) {
        final StringBuilder hql = new StringBuilder("select distinct h.hvtStandort from ");
        hql.append(HvtUmzug.class.getName()).append(" h ");
        hql.append(" where h.status not in (:status) ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setParameterList("status", status);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (result != null) {
            return result.stream().collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    @Override
    public void deleteHvtUmzugDetail(@Nonnull HvtUmzugDetail toDelete) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(toDelete);
    }

    @Override
    public Set<Long> findKvz4HvtUmzugWithStatusUmgezogen(HVTStandort standort, String kvzNr) {
        final StringBuilder hql = new StringBuilder("select h.id from ");
        hql.append(HvtUmzug.class.getName()).append(" h ");
        hql.append("where h.kvzNr = :kvzNr ");
        hql.append("and h.hvtStandort = :standort ");
        hql.append("and (h.status = :statusAusgefuehrt or h.status = :statusBeendet)");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setString("kvzNr", kvzNr);
        q.setLong("standort", standort.getId());
        q.setParameter("statusAusgefuehrt", HvtUmzugStatus.AUSGEFUEHRT);
        q.setParameter("statusBeendet", HvtUmzugStatus.BEENDET);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (result != null) {
            return result.stream().collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}
