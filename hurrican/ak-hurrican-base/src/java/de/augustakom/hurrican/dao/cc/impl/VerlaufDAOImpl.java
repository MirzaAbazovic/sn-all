/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 16:09:47
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;


/**
 * Hibernate DAO-Implementierung von <code>VerlaufDAO</code>.
 */
public class VerlaufDAOImpl extends Hibernate4DAOImpl implements VerlaufDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public boolean hasActiveVerlauf(Long auftragId) {
        StringBuilder hql = new StringBuilder("select count(v.id) from ");
        hql.append(Verlauf.class.getName()).append(" v where (v.auftragId=? or ? in elements(v.subAuftragsIds)) and v.akt=?");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), new Object[] { auftragId, auftragId, Boolean.TRUE });
        if (result != null && !result.isEmpty()) {
            Long count = result.get(0);
            if (count != null && count > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Verlauf> findActive(Long auftragId, boolean projektierung) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Verlauf.class.getName()).append(" v where (v.auftragId=? or ? in elements(v.subAuftragsIds)) and v.akt=1 ");
        if (projektierung) {
            hql.append(" and v.projektierung=1");
        }
        else {
            hql.append(" and (v.projektierung is null or v.projektierung=0)");
        }

        @SuppressWarnings("unchecked")
        List<Verlauf> result = find(hql.toString(), new Object[] { auftragId, auftragId });
        return result;
    }

    @Override
    public Verlauf findLast4Auftrag(Long auftragId, boolean projektierung) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Verlauf.class.getName()).append(" v where (v.auftragId=? or ? in elements(v.subAuftragsIds)) ");
        if (projektierung) {
            hql.append(" and v.projektierung=1");
        }
        else {
            hql.append(" and (v.projektierung is null or v.projektierung=0)");
        }
        hql.append(" order by v.id desc");

        @SuppressWarnings("unchecked")
        List<Verlauf> result = find(hql.toString(), new Object[] { auftragId, auftragId });
        return (result != null && !result.isEmpty()) ? result.get(0) : null;
    }

    @Override
    public List<Verlauf> findByAuftragId(Long auftragId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Verlauf.class.getName()).append(" v where v.auftragId=? order by v.id desc");

        @SuppressWarnings("unchecked")
        List<Verlauf> result = find(hql.toString(), new Object[] { auftragId });
        StringBuilder hql2 = new StringBuilder("from ");
        hql2.append(Verlauf.class.getName()).append(" v where ? in elements(v.subAuftragsIds) order by v.id desc");

        @SuppressWarnings("unchecked")
        List<Verlauf> result2 = find(hql2.toString(), new Object[] { auftragId });
        Set<Verlauf> resultSet = Sets.newHashSet(result);
        resultSet.addAll(result2);
        List<Verlauf> sortedList = Lists.newArrayList(resultSet);
        Collections.sort(sortedList, Ordering.natural().onResultOf(AbstractCCIDModel.GET_ID).reverse());
        return sortedList;
    }

    @Override
    public List<Verlauf> findByWorkforceOrderId(String workforceOrderId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Verlauf.class.getName()).append(" v where v.workforceOrderId=? order by v.id desc");

        return find(hql.toString(), new Object[] { workforceOrderId });
    }

    @Override
    public List<SimpleVerlaufView> findSimpleVerlaufViews(final Long auftragId) {
        final String sql = "SELECT DISTINCT " +
                " v.ID AS VERLAUF_ID, " +
                " v.PROJEKTIERUNG, " +
                " v.REALISIERUNGSTERMIN, " +
                " va.NAME, " +
                " vs.VERLAUF_STATUS " +
                "FROM T_VERLAUF v " +
                " JOIN T_BA_VERL_ANLASS va " +
                "   ON V.ANLASS = VA.ID " +
                " JOIN T_VERLAUF_STATUS vs " +
                "   ON V.VERLAUF_STATUS_ID = VS.ID " +
                " LEFT JOIN T_VERLAUF_SUB_ORDERS so " +
                "   ON V.ID = SO.VERLAUF_ID " +
                "WHERE (v.AUFTRAG_ID = :auftragId or SO.AUFTRAG_ID = :auftragId) " +
                "ORDER BY v.ID DESC";

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sq = session.createSQLQuery(sql);
        sq.addScalar("VERLAUF_ID", new IntegerType());
        sq.addScalar("PROJEKTIERUNG", new BooleanType());
        sq.addScalar("REALISIERUNGSTERMIN", new DateType());
        sq.addScalar("NAME", new StringType());
        sq.addScalar("VERLAUF_STATUS", new StringType());
        sq.setLong("auftragId", auftragId);
        @SuppressWarnings("unchecked")
        List<Object[]> list = sq.list();

        List<SimpleVerlaufView> result = new ArrayList<>();
        for (Object[] values : list) {
            SimpleVerlaufView view = new SimpleVerlaufView();
            view.setId(ObjectTools.getLongSilent(values, 0));
            view.setProjektierung(ObjectTools.getBooleanSilent(values, 1));
            view.setRealisierungstermin(ObjectTools.getDateSilent(values, 2));
            view.setAnlass(ObjectTools.getStringSilent(values, 3));
            view.setStatus(ObjectTools.getStringSilent(values, 4));
            result.add(view);
        }
        return result;
    }


    // Referenz: ExportCommandJob.executeInternal()
    // ToDo: Unterauftraege (subAuftragsIds, T_VERLAUF_SUB_ORDERS)
    // implementieren, ExportCommand ggf. anpassen
    @Override
    public List<Long> findFinishedOrderByDate(Date date) {
        StringBuilder hql = new StringBuilder("select v.auftragId from ");
        hql.append(Verlauf.class.getName()).append(" v, ");
        hql.append(VerlaufAbteilung.class.getName()).append(" va ");
        hql.append(" where v.id=va.verlaufId and v.verlaufStatusId = ? ");
        hql.append(" and va.abteilungId=? and (v.notPossible is null or v.notPossible=?) ");
        if (date != null) {
            hql.append(" and va.ausgetragenAm >= ?");
        }
        else {
            hql.append(" and va.ausgetragenAm is not null");
        }

        List<Object> params = new ArrayList<>();
        params.add(VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
        params.add(Abteilung.AM);
        params.add(Boolean.FALSE);
        if (date != null) {
            params.add(date);
        }

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), params.toArray());
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }

    @Override
    public List<Verlauf> findFinishedVerlaufByDate(Date date) {
        StringBuilder hql = new StringBuilder("select distinct v.id, v.auftragId,  ");
        hql.append(" v.anlass, v.verlaufStatusId, v.projektierung, v.realisierungstermin ,  ");
        hql.append(" v.akt from ");
        hql.append(Verlauf.class.getName()).append(" v, ");
        hql.append(VerlaufAbteilung.class.getName()).append(" va, ");
        hql.append(AuftragDaten.class.getName()).append(" ad ");
        hql.append(" where v.id=va.verlaufId and v.auftragId=ad.auftragId ");
        hql.append(" and ad.gueltigBis = ? ");
        hql.append(" and v.verlaufStatusId in (?,?) ");
        hql.append(" and va.abteilungId=? ");
        hql.append(" and (v.notPossible is null or v.notPossible=?) ");
        hql.append(" and v.projektierung=? ");
        hql.append(" and ad.statusId>=? ");
        if (date != null) {
            hql.append(" and va.ausgetragenAm >= ?");
        }
        else {
            hql.append(" and va.ausgetragenAm is not null");
        }

        List<Object> params = new ArrayList<>();
        params.add(DateTools.getHurricanEndDate());
        params.add(VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
        params.add(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
        params.add(Abteilung.AM);
        params.add(Boolean.FALSE);
        params.add(Boolean.FALSE);
        params.add(AuftragStatus.TECHNISCHE_REALISIERUNG);
        if (date != null) {
            params.add(date);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(), params.toArray());
        if (result != null && !result.isEmpty()) {
            List<Verlauf> retVal = new ArrayList<>();
            for (Object[] values : result) {
                Verlauf view = new Verlauf();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setAuftragId(ObjectTools.getLongSilent(values, 1));
                view.setAnlass(ObjectTools.getLongSilent(values, 2));
                view.setVerlaufStatusId(ObjectTools.getLongSilent(values, 3));
                view.setProjektierung(ObjectTools.getBooleanSilent(values, 4));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, 5));
                view.setAkt(ObjectTools.getBooleanSilent(values, 6));
                retVal.add(view);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


