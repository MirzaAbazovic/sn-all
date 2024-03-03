/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 14:35:33
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.hibernate.HibernateInClauseHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;


/**
 * Hibernate DAO-Implementierung von <code>TechLeistungDAO</code>.
 *
 *
 */
public class TechLeistungDAOImpl extends Hibernate4DAOImpl implements TechLeistungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Produkt2TechLeistung> findProdukt2TechLs(final Long prodId, String techLsTyp, final Boolean defaults) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Produkt2TechLeistung.class).createAlias("techLeistung", "tl");
        crit.setFetchMode("techLeistung", FetchMode.JOIN);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "prodId", prodId);
        if(techLsTyp != null) {
            CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "tl.typ", techLsTyp);
        }
        if (defaults != null) {
            CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "defaultLeistung", defaults);
        }
        return crit.list();
    }

    @Override
    public List<TechLeistung> findTechLeistungen(final boolean onlyActual) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(TechLeistung.class);
        if (onlyActual) {
            Date now = new Date();
            CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
            CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        }
        crit.addOrder(Order.asc("typ"));
        crit.addOrder(Order.asc("externLeistungNo"));
        crit.addOrder(Order.asc("name"));
        crit.addOrder(Order.asc("gueltigVon"));

        return crit.list();
    }

    @Override
    public List<TechLeistung> findTechLeistungen4Auftrag(final Long auftragId, final String lsTyp, final boolean onlyActive) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("select tl from ");
        hql.append(Auftrag2TechLeistung.class.getName()).append(" atl, ");
        hql.append(TechLeistung.class.getName()).append(" tl ");
        hql.append("where atl.auftragId= :aId and atl.techLeistungId=tl.id ");
        if (StringUtils.isNotBlank(lsTyp)) {
            hql.append("and tl.typ= :typ ");
        }
        if (onlyActive) {
            hql.append("and (atl.aktivBis is null or atl.aktivBis >= :endDate)");
        }

        Query query = session.createQuery(hql.toString());
        query.setLong("aId", auftragId);
        if (StringUtils.isNotBlank(lsTyp)) {
            query.setString("typ", lsTyp);
        }
        if (onlyActive) {
            query.setDate("endDate", DateTools.getHurricanEndDate());
        }

        @SuppressWarnings("unchecked")
        List<TechLeistung> result = query.list();
        return result;
    }


    @Override
    public Map<Long, List<TechLeistung>> findTechLeistungen4Auftraege(final List<Long> auftragIds, final String lsTyp, final boolean onlyActive) {
        Session session = sessionFactory.getCurrentSession();
        // since hibernate will just get the techLeistung ids itself and then issue single queries, we better
        // get the ids ourselves and issue a single query getting all techLeistungen at once
        StringBuilder hql = new StringBuilder("select atl.auftragId, tl.id from ");
        hql.append(Auftrag2TechLeistung.class.getName()).append(" atl, ");
        hql.append(TechLeistung.class.getName()).append(" tl ");
        hql.append("where atl.techLeistungId = tl.id ");
        hql.append("and %s ");
        if (StringUtils.isNotBlank(lsTyp)) {
            hql.append("and tl.typ = :typ ");
        }
        if (onlyActive) {
            hql.append("and (atl.aktivBis is null or atl.aktivBis >= :endDate)");
        }

        Map<String, Object> defParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(lsTyp)) {
            defParams.put("typ", lsTyp);
        }
        if (onlyActive) {
            defParams.put("endDate", DateTools.getHurricanEndDate());
        }

        List<Object[]> auftragToTlIds = HibernateInClauseHelper.list(getSessionFactory(), hql.toString(),
                defParams, "atl.auftragId", auftragIds);
        List<Long> techLeistungIds = new ArrayList<Long>();
        for (Object[] ids : auftragToTlIds) {
            Long techLeistungId = (Long) ids[1];
            techLeistungIds.add(techLeistungId);
        }

        List<TechLeistung> techLeistungen = HibernateInClauseHelper.list(getSessionFactory(),
                "from " + TechLeistung.class.getName() + " tl where %s", null, "tl.id", techLeistungIds);
        Map<Long, TechLeistung> techLeistungenMap = new HashMap<Long, TechLeistung>();
        for (TechLeistung techLeistung : techLeistungen) {
            techLeistungenMap.put(techLeistung.getId(), techLeistung);
        }

        Map<Long, List<TechLeistung>> result = new HashMap<Long, List<TechLeistung>>();
        for (Object[] ids : auftragToTlIds) {
            Long auftragId = (Long) ids[0];
            Long techLeistungId = (Long) ids[1];
            TechLeistung techLeistung = techLeistungenMap.get(techLeistungId);
            List<TechLeistung> list = result.get(auftragId);
            if (list == null) {
                list = new ArrayList<TechLeistung>();
                result.put(auftragId, list);
            }
            list.add(techLeistung);
        }
        return result;
    }

    @Override
    public List<TechLeistung> findTechLeistungen(final Long externLeistungNo) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();

        Criteria crit = session.createCriteria(TechLeistung.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "externLeistungNo", externLeistungNo);
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "gueltigVon", now);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER, "gueltigBis", now);
        crit.addOrder(Order.desc("id"));

        return crit.list();
    }

    @Override
    public List<TechLeistung> findTechLeistungen4Verlauf(final Long verlaufId) {
        final StringBuilder hql = new StringBuilder("select atl.techLeistungId from ");
        hql.append(Auftrag2TechLeistung.class.getName()).append(" atl ");
        hql.append("where (atl.verlaufIdReal= :vId or atl.verlaufIdKuend= :vId) ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("vId", verlaufId);

        @SuppressWarnings("unchecked")
        List<Long> result = query.list();
        if (result != null) {
            List<TechLeistung> retVal = new ArrayList<TechLeistung>();
            for (Long techLeistungId : result) {
                retVal.add(findById(techLeistungId, TechLeistung.class));
            }
            return retVal;
        }
        return null;
    }

    @Override
    public void deleteProdukt2TechLeistung(final Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Produkt2TechLeistung.class.getName());
        hql.append(" p2tl where p2tl.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, id);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


