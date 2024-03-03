/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2008 16:51:39
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.util.*;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.MonitorDAO;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;

/**
 * DAO-Implementierung fuer den Ressourcen-Monitor <code>HvtToolDAO</code>.
 *
 *
 */
public class MonitorDAOImpl extends Hibernate4DAOImpl implements MonitorDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(RSMonitorConfig.class.getName());
        hql.append(" m where m.id = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RsmPortUsage> findPortUsages(final Long hvtIdStandort, final String kvzNummer, final Long physikTypId, final Long physikTypIdAdd, final int monthCount) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(RsmPortUsage.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, RsmPortUsage.HVT_ID_STANDORT, hvtIdStandort);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, RsmPortUsage.KVZ_NUMMER, kvzNummer);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, RsmPortUsage.PHYSIK_TYP_ID, physikTypId);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, RsmPortUsage.PHYSIK_TYP_ID_ADD, physikTypIdAdd);
        crit.addOrder(Order.desc(RsmPortUsage.YEAR));
        crit.addOrder(Order.desc(RsmPortUsage.MONTH));
        crit.setMaxResults(monthCount);

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Rangierung> findAktRangierung4Phsyiktyp(final Long hvtIdStandort, final Long physikTypId) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Criteria criteria = session.createCriteria(Rangierung.class);
        criteria.add(Restrictions.eq("hvtIdStandort", hvtIdStandort));
        criteria.add(Restrictions.eq("physikTypId", physikTypId));
        criteria.add(Restrictions.le("gueltigVon", now));
        criteria.add(Restrictions.gt("gueltigBis", now));
         return criteria.list();
    }

    @Override
    public void deleteRsmRangCount() {
        sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM T_RSM_RANG_COUNT").executeUpdate();
    }

    @Override
    public List<RsmRangCountView> findAllRsmRangCount() {
        return findRsmRangCount(new ResourcenMonitorQuery());
    }

    @Override
    public List<RsmRangCountView> findRsmRangCount(final ResourcenMonitorQuery filter) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder();
        hql.append("select rang.hvtStandortId, rang.kvzNummer, rang.physiktyp, rang.physiktypAdd, rang.belegt, ");
        hql.append("rang.frei, rang.freigabebereit, rang.defekt, rang.imAufbau, ");
        hql.append("rang.vorhanden, rang.portReach, rang.averageUsage, niederlassung.name, ort.clusterId ");
        hql.append("from ");
        hql.append(RsmRangCount.class.getName());
        hql.append(" rang, ");
        hql.append(HVTStandort.class.getName());
        hql.append(" ort, ");
        hql.append(HVTGruppe.class.getName());
        hql.append(" gruppe, ");
        hql.append(Niederlassung.class.getName());
        hql.append(" niederlassung ");
        hql.append("WHERE rang.vorhanden > 0 ");
        hql.append("and rang.hvtStandortId = ort.id ");
        hql.append("and ort.hvtGruppeId = gruppe.id ");
        hql.append("and gruppe.niederlassungId = niederlassung.id ");
        if (filter.getNiederlassungId() != null) {
            hql.append("and gruppe.niederlassungId = :nid ");
        }
        if (!filter.getStandortTypen().isEmpty()) {
            hql.append("and ort.standortTypRefId in (:types) ");
        }
        if (!Strings.isNullOrEmpty(filter.getCluster())) {
            hql.append("and ort.clusterId = :cid ");
        }
        hql.append("ORDER BY rang.id");

        Query query = session.createQuery(hql.toString());

        if (filter.getNiederlassungId() != null) {
            query.setLong("nid", filter.getNiederlassungId());
        }
        if (!filter.getStandortTypen().isEmpty()) {
            query.setParameterList("types", filter.getStandortTypen());
        }
        if (!Strings.isNullOrEmpty(filter.getCluster())) {
            query.setString("cid", filter.getCluster());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.list();
        if (CollectionTools.isNotEmpty(result)) {
            ImmutableList.Builder<RsmRangCountView> retVal = ImmutableList.builder();
            for (Object[] values : result) {
                int columnIndex = 0;
                RsmRangCountView view = new RsmRangCountView();
                view.setHvtStandortId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKvzNummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPhysiktyp(ObjectTools.getLongSilent(values, columnIndex++));
                view.setPhysiktypAdd(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBelegt(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setFrei(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setFreigabebereit(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setDefekt(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setImAufbau(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setVorhanden(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setPortReach(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setAverageUsage(ObjectTools.getFloatSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCluster(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal.build();
        }
        return ImmutableList.of();
    }

    @Override
    public void deleteHVTBelegung() {
        sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM T_RSM_EQ_COUNT").executeUpdate();
    }

    @Override
    public void deleteUevtView() {
        sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM T_RSM_EQ_VIEW").executeUpdate();
    }

    @Override
    public List<UevtCuDAView> findUevtCuDAViews() {
        return findUevtCuDAViews(new ResourcenMonitorQuery());
    }

    @Override
    public List<UevtCuDAView> findUevtCuDAViews(ResourcenMonitorQuery filter) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        StringBuilder sql = new StringBuilder("select eq_view.HVT_ID_STANDORT, UEVT_ID, UEVT, CUDA_PHYSIK, ");
        sql.append("sum(CUDA_FREI) as CUDA_FREI, sum(CUDA_VORBEREITET) as CUDA_VORBEREITET, ");
        sql.append("sum(CUDA_RANGIERT) as CUDA_RANGIERT, HVT_NAME, eq_view.ONKZ, eq_view.ASB, CLUSTER_ID ");
        sql.append("from T_RSM_EQ_VIEW eq_view ");
        sql.append("join T_HVT_STANDORT ort on ORT.HVT_ID_STANDORT = eq_view.HVT_ID_STANDORT ");
        sql.append("join t_hvt_gruppe gruppe on ORT.HVT_GRUPPE_ID = GRUPPE.HVT_GRUPPE_ID ");
        sql.append("where 1=1 ");

        if (filter.getNiederlassungId() != null) {
            sql.append("and GRUPPE.NIEDERLASSUNG_ID = ? ");
            params.add(filter.getNiederlassungId());
            types.add(new LongType());
        }
        if (!filter.getStandortTypen().isEmpty()) {
            List<String> standortTypenPlaceholder = Lists.newArrayList();
            for (Long standort : filter.getStandortTypen()) {
                standortTypenPlaceholder.add("?");
                params.add(standort);
                types.add(new LongType());
            }
            sql.append("and ORT.STANDORT_TYP_REF_ID in (");
            sql.append(Joiner.on(',').join(standortTypenPlaceholder));
            sql.append(") ");
        }
        if (!Strings.isNullOrEmpty(filter.getCluster())) {
            sql.append("and ort.cluster_id = ? ");
            params.add(filter.getCluster());
            types.add(new StringType());
        }

        sql.append("group by eq_view.HVT_ID_STANDORT, UEVT, CUDA_PHYSIK, HVT_NAME, eq_view.ONKZ, eq_view.ASB, SCHWELLWERT, UEVT_ID, CLUSTER_ID ");
        sql.append("order by eq_view.HVT_ID_STANDORT ");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<UevtCuDAView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0 ;
                UevtCuDAView view = new UevtCuDAView();
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, columnIndex++));
                view.setUevtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setUevt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCudaPhysik(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCudaFrei(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setCudaVorbereitet(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setCudaRangiert(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setHvtName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setOnkz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAsb(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setCluster(ObjectTools.getStringSilent(values, columnIndex++));

                views.add(view);
            }
        }

        sql = new StringBuilder("SELECT sum(c.BELEGT) as SUM_BELEGT, sum(c.FREI) as SUM_FREI from ");
        sql.append(" T_RSM_EQ_COUNT c where c.HVT_ID_STANDORT=? and c.UEVT=? and c.CUDA_PHYSIK=? ");
        for (UevtCuDAView view : views) {
            sqlQuery = session.createSQLQuery(sql.toString());
            sqlQuery.setParameters(new Object[] { view.getHvtIdStandort(), StringUtils.trimToNull(view.getUevt()),
                    StringUtils.trimToNull(view.getCudaPhysik()) },
                    new Type[] {new LongType(), new StringType(), new StringType()});
            List<Object[]> cudaCounts = sqlQuery.list();
            if ((cudaCounts != null) && (cudaCounts.size() == 1)) {
                Object[] counts = cudaCounts.get(0);
                view.setCudaBelegt(ObjectTools.getIntegerSilent(counts, 0));
                view.setCudaFreigegeben(ObjectTools.getIntegerSilent(counts, 1));
            }
        }

        return views;
    }

    @Override
    public void store(List<HVTBelegungView> toStore) {
        if ((toStore != null) && (!toStore.isEmpty())) {
            StringBuilder sql = new StringBuilder("INSERT INTO T_RSM_EQ_COUNT (UEVT, HVT_ID_STANDORT, ");
            sql.append(" CUDA_PHYSIK, RANG_LEISTE1, RANG_SS_TYPE, BELEGT, FREI) VALUES (?,?,?,?,?,?,?)");

            for (HVTBelegungView view : toStore) {
                List<Object> values = new ArrayList<>();
                values.add(view.getUevt());
                values.add(view.getHvtIdStandort());
                values.add(view.getCudaPhysik());
                values.add(view.getRangLeiste1());
                values.add(view.getRangSSType());
                values.add(view.getBelegt());
                values.add(view.getFrei());

                sessionFactory.getCurrentSession().createSQLQuery(sql.toString()).setParameters(values.toArray(),
                        new Type[] {new StringType(), new LongType(),
                                new StringType(), new StringType(),
                                new StringType(), new IntegerType(),
                                new IntegerType()}).executeUpdate();
            }
        }
    }

    @Override
    public void storeUevtCuDAViews(List<UevtCuDAView> toStore) {
        if ((toStore != null) && (!toStore.isEmpty())) {
            StringBuilder sql = new StringBuilder("INSERT INTO T_RSM_EQ_VIEW (");
            sql.append("HVT_ID_STANDORT, UEVT_ID, UEVT, CUDA_PHYSIK, CARRIER, CUDA_FREI, CUDA_VORBEREITET, ");
            sql.append("CUDA_RANGIERT, HVT_NAME, ONKZ, ASB, RANG_SS_TYPE) ");
            sql.append("values (?,?,?,?,?,?,?,?,?,?,?,?)");

            for (UevtCuDAView view : toStore) {
                List<Object> values = new ArrayList<>();
                values.add(view.getHvtIdStandort());
                values.add(view.getUevtId());
                values.add(view.getUevt());
                values.add(view.getCudaPhysik());
                values.add(view.getCarrier());
                values.add(view.getCudaFrei());
                values.add(view.getCudaVorbereitet());
                values.add(view.getCudaRangiert());
                values.add(view.getHvtName());
                values.add(view.getOnkz());
                values.add(view.getAsb());
                values.add(view.getRangSSType()); // //

                sessionFactory.getCurrentSession().createSQLQuery(sql.toString()).setParameters(values.toArray(),
                        new Type[] { new LongType(), new LongType(),
                                new StringType(), new StringType(),
                                new StringType(), new IntegerType(),
                                new IntegerType(), new IntegerType(),
                                new StringType(), new StringType(),
                                new IntegerType(), new StringType()}).executeUpdate();
            }
        }
    }

    @Override
    public List<HVTBelegungView> findHVTBelegungGrouped(String uevt, Long hvtIdStd) {
        StringBuilder sql = new StringBuilder("select HVT_ID_STANDORT, UEVT, CUDA_PHYSIK, RANG_LEISTE1, ");
        sql.append("RANG_SS_TYPE, sum(BELEGT) as BELEGT, sum(FREI) as FREI from T_RSM_EQ_COUNT ");
        sql.append("where UEVT=? and HVT_ID_STANDORT=? ");
        sql.append("group by HVT_ID_STANDORT, UEVT, CUDA_PHYSIK, RANG_LEISTE1, RANG_SS_TYPE ");
        sql.append("order by RANG_LEISTE1");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { uevt, hvtIdStd }, new Type[] { new StringType(), new LongType()});
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<HVTBelegungView> views = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                HVTBelegungView view = new HVTBelegungView();
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, columnIndex++));
                view.setUevt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCudaPhysik(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangLeiste1(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangSSType(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBelegt(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setFrei(ObjectTools.getIntegerSilent(values, columnIndex++));
                views.add(view);
            }

            return views;
        }

        return null;
    }

    @Override
    public List<UevtCuDAView> findViewsGroupedByRangSSType(String uevt, String cudaPhysik, Long hvtIdStandort) {
        StringBuilder sql = new StringBuilder();
        sql.append("select UEVT, CUDA_PHYSIK, RANG_SS_TYPE, sum(CUDA_RANGIERT) as CUDA_RANGIERT, ");
        sql.append("sum(CUDA_FREI) as CUDA_FREI, HVT_ID_STANDORT ");
        sql.append("from T_RSM_EQ_VIEW where UEVT=? and CUDA_PHYSIK=? and HVT_ID_STANDORT=? ");
        sql.append("group by RANG_SS_TYPE, UEVT, CUDA_PHYSIK, HVT_ID_STANDORT");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { uevt, cudaPhysik, hvtIdStandort }, new Type[] { new StringType(), new StringType(), new LongType() });
        List<Object[]> result = sqlQuery.list();
        List<UevtCuDAView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                UevtCuDAView view = new UevtCuDAView();
                view.setUevt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCudaPhysik(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangSSType(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCudaRangiert(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setCudaFrei(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, columnIndex++));

                views.add(view);
            }
        }

        sql = new StringBuilder("SELECT sum(BELEGT) as SUM_BELEGT from ");
        sql.append(" T_RSM_EQ_COUNT where HVT_ID_STANDORT=? and UEVT=? and CUDA_PHYSIK=? ");
        sql.append(" and RANG_SS_TYPE=? group by UEVT, HVT_ID_STANDORT, CUDA_PHYSIK, RANG_SS_TYPE");
        for (UevtCuDAView view : views) {
            sqlQuery = session.createSQLQuery(sql.toString());
            sqlQuery.setParameters(new Object[] { view.getHvtIdStandort(), view.getUevt(), view.getCudaPhysik(), view.getRangSSType() },
                    new Type[] {new LongType(), new StringType(), new StringType(), new StringType()});
            Object sum = sqlQuery.uniqueResult();
            if (sum != null) {
                view.setCudaBelegt(((BigDecimal)sum).intValue());
            }
        }

        return views;
    }

    @Override
    public int sumPortUsage(Date begin, Date end, Long hvtIdStandort, String kvzNummer, Long physikTypId, Long physikTypIdAdd, boolean forCancel) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        params.add(hvtIdStandort);
        types.add(new LongType());
        params.add(physikTypId);
        types.add(new LongType());

        StringBuilder sql = new StringBuilder("select count(ad.auftrag_id) ");
        sql.append("from T_AUFTRAG_DATEN ad ");
        sql.append("inner join T_AUFTRAG_TECHNIK atech on AD.AUFTRAG_ID=ATECH.AUFTRAG_ID ");
        sql.append("inner join T_ENDSTELLE es on ATECH.AT_2_ES_ID=ES.ES_GRUPPE ");

        if (!forCancel) {
            sql.append("inner join T_RANGIERUNG r on ES.RANGIER_ID=R.RANGIER_ID ");
            if (physikTypIdAdd != null) {
                sql.append("inner join T_RANGIERUNG radd on ES.RANGIER_ID_ADDITIONAL=RADD.RANGIER_ID ");
            }
            if (StringUtils.isNotBlank(kvzNummer)) {
                sql.append("inner join T_EQUIPMENT eq on r.EQ_OUT_ID=eq.EQ_ID ");
            }
            sql.append("where R.GUELTIG_BIS>SYSDATE ");
            sql.append("and ATECH.GUELTIG_BIS>SYSDATE ");
            sql.append("and AD.GUELTIG_BIS>SYSDATE ");
            sql.append("and R.HVT_ID_STANDORT=?");
            sql.append("and R.PHYSIK_TYP=? ");
            if (physikTypIdAdd != null) {
                sql.append("and RADD.GUELTIG_BIS>SYSDATE ");
                sql.append("and RADD.PHYSIK_TYP=? ");
                params.add(physikTypIdAdd);
                types.add(new LongType());
            }
            if (StringUtils.isNotBlank(kvzNummer)) {
                sql.append("and eq.KVZ_NUMMER=? ");
                params.add(kvzNummer);
                types.add(new StringType());
            }
            sql.append("and (AD.INBETRIEBNAHME between ? and ? or (AD.INBETRIEBNAHME is null and AD.VORGABE_SCV between ? and ?))");
            params.add(begin);
            params.add(end);
            params.add(begin);
            params.add(end);
            types.add(new DateType());
            types.add(new DateType());
            types.add(new DateType());
            types.add(new DateType());

        }
        else {
            sql.append("inner join T_RANGIERUNG r on (ES.RANGIER_ID=R.RANGIER_ID and R.ES_ID<>ES.ID) ");
            if (physikTypIdAdd != null) {
                sql.append("inner join T_RANGIERUNG radd on (ES.RANGIER_ID_ADDITIONAL=RADD.RANGIER_ID and RADD.ES_ID<>ES.ID) ");
            }
            if (StringUtils.isNotBlank(kvzNummer)) {
                sql.append("inner join T_EQUIPMENT eq on r.EQ_OUT_ID=eq.EQ_ID ");
            }
            sql.append("where R.GUELTIG_BIS>SYSDATE ");
            sql.append("and ATECH.GUELTIG_BIS>SYSDATE ");
            sql.append("and AD.GUELTIG_BIS>SYSDATE ");
            sql.append("and R.HVT_ID_STANDORT=? ");
            sql.append("and R.PHYSIK_TYP=?");
            if (physikTypIdAdd != null) {
                sql.append("and RADD.GUELTIG_BIS>SYSDATE ");
                sql.append("and RADD.PHYSIK_TYP=? ");
                params.add(physikTypIdAdd);
                types.add(new LongType());
            }
            if (StringUtils.isNotBlank(kvzNummer)) {
                sql.append("and eq.KVZ_NUMMER=? ");
                params.add(kvzNummer);
                types.add(new StringType());
            }
            sql.append("and AD.KUENDIGUNG between ? and ? ");
            params.add(begin);
            params.add(end);
            types.add(new DateType());
            types.add(new DateType());

            sql.append("and ad.status_id>=?");
            params.add(AuftragStatus.KUENDIGUNG);
            types.add(new LongType());
        }
        sql.append("and ad.status_id not in (?,?,?)");
        params.add(AuftragStatus.STORNO);
        params.add(AuftragStatus.ABSAGE);
        params.add(AuftragStatus.KONSOLIDIERT);
        types.add(new LongType());
        types.add(new LongType());
        types.add(new LongType());

        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
            .setParameters(params.toArray(), types.toArray(new Type[types.size()])).uniqueResult()).intValue();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
