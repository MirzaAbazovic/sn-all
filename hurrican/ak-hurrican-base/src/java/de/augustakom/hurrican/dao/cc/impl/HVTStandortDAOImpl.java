/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 10:01:01
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.math.*;
import java.sql.Date;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.HVTStandortDAO;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTClusterView;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;


/**
 * Hibernate DAO-Implementierung von HVTStandortDAO.
 *
 *
 */
public class HVTStandortDAOImpl extends Hibernate4DAOImpl implements HVTStandortDAO, StoreDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<HVTStandort> findHVTStandort4DtagAsb(String onkz, Integer dtagAsb, Long standortTypRefId) {
        String queryString = "select s from HVTStandort s, HVTGruppe g where s.hvtGruppeId = g.id and " +
                "g.onkz = ? and mod(s.asb, 1000) = ? and s.standortTypRefId = ?";
        @SuppressWarnings("unchecked")
        List<HVTStandort> standorte = find(queryString, onkz, dtagAsb, standortTypRefId);
        return standorte;
    }

    @Override
    public List<HVTStandort> findHVTStandorte4Gruppe(Long hvtGruppeId) {
        @SuppressWarnings("unchecked")
        List<HVTStandort> result = find("from " + HVTStandort.class.getName() +
                " hvt where hvt.hvtGruppeId=? ", hvtGruppeId);
        return result;
    }

    @Override
    public List<HVTStandort> findActiveHVTStandorte4Gruppe(Long hvtGruppeId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(HVTStandort.class.getName());
        hql.append(" std where std.hvtGruppeId=? and std.gueltigVon<=? and std.gueltigBis>?");

        Date now = DateTools.getActualSQLDate();
        @SuppressWarnings("unchecked")
        List<HVTStandort> result = find(hql.toString(), new Object[] { hvtGruppeId, now, now });
        return result;
    }

    @Override
    public List<HVTGruppeStdView> findHVTViews(HVTQuery query) {
        List<Object> params = new ArrayList<Object>();

        StringBuilder hql = new StringBuilder("select std.asb, std.id, g.id, g.onkz, g.ortsteil, ");
        hql.append("std.standortTypRefId, std.gesicherteRealisierung from ");
        hql.append(HVTStandort.class.getName()).append(" std, ");
        hql.append(HVTGruppe.class.getName());
        hql.append(" g where std.hvtGruppeId=g.id");
        if (StringUtils.isNotBlank(query.getOnkz())) {
            hql.append(" and lower(g.onkz) like ?");
            params.add(WildcardTools.replaceWildcards(query.getOnkz()).toLowerCase());
        }
        if (query.getAsb() != null) {
            hql.append(" and std.asb = ?");
            params.add(query.getAsb());
        }
        if (StringUtils.isNotBlank(query.getOrtsteil())) {
            hql.append(" and lower(g.ortsteil) like ?");
            params.add(WildcardTools.replaceWildcards(query.getOrtsteil()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getOrt())) {
            hql.append(" and lower(g.ort) like ?");
            params.add(WildcardTools.replaceWildcards(query.getOrt()).toLowerCase());
        }
        if (query.getStandortTypRefId() != null) {
            hql.append(" and std.standortTypRefId = ?");
            params.add(query.getStandortTypRefId());
        }
        if (query.getNiederlassungId() != null) {
            hql.append(" and g.niederlassungId = ?");
            params.add(query.getNiederlassungId());
        }
        if (query.getHvtIdStandort() != null) {
            hql.append(" and std.id = ?");
            params.add(query.getHvtIdStandort());
        }
        if (query.getGesicherteRealisierung() != null) {
            hql.append(" and std.gesicherteRealisierung = ?");
            params.add(query.getGesicherteRealisierung());
        }

        hql.append(" order by g.ortsteil");

        @SuppressWarnings("unchecked")
        List<Object[]> result = find(hql.toString(), query.getMaxResultSize(), params.toArray());
        if (result != null) {
            List<HVTGruppeStdView> retVal = new ArrayList<HVTGruppeStdView>();
            for (Object[] values : result) {
                HVTGruppeStdView view = new HVTGruppeStdView();
                view.setAsb(ObjectTools.getIntegerSilent(values, 0));
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, 1));
                view.setHvtIdGruppe(ObjectTools.getLongSilent(values, 2));
                view.setOnkz(ObjectTools.getStringSilent(values, 3));
                view.setOrtsteil(ObjectTools.getStringSilent(values, 4));
                view.setStandortTypRefId(ObjectTools.getLongSilent(values, 5));
                view.setGesicherteRealisierung(ObjectTools.getStringSilent(values, 6));
                retVal.add(view);
            }

            return retVal;
        }

        return null;
    }

    public Pair<List<HVTStandort>, List<HVTGruppe>> findHVTStandorteAndGruppen(String onkz, Integer asb, String ortsteil,
            String ort, Long standortTypRefId, String clusterId ) {

        final List<Object> params = new ArrayList<>();
        final List<Type> types = new ArrayList<>();
        final String baseQuery = String.format("select std, g from %s std, %s g where std.hvtGruppeId = g.id ",
                HVTStandort.class.getName(), HVTGruppe.class.getName());
        final StringBuilder query = new StringBuilder(baseQuery);
        if (StringUtils.isNotBlank(onkz)) {
            query.append(" and lower(g.onkz) like ?");
            params.add(WildcardTools.replaceWildcards(onkz).toLowerCase());
            types.add(StringType.INSTANCE);
        }
        if (asb != null) {
            query.append(" and std.asb = ?");
            params.add(asb);
            types.add(IntegerType.INSTANCE);
        }
        if (StringUtils.isNotBlank(ortsteil)) {
            query.append(" and lower(g.ortsteil) like ?");
            params.add(WildcardTools.replaceWildcards(ortsteil).toLowerCase());
            types.add(StringType.INSTANCE);
        }
        if (StringUtils.isNotBlank(ort)) {
            query.append(" and lower(g.ort) like ?");
            params.add(WildcardTools.replaceWildcards(ort).toLowerCase());
            types.add(StringType.INSTANCE);
        }
        if (standortTypRefId != null) {
            query.append(" and std.standortTypRefId = ?");
            params.add(standortTypRefId);
            types.add(LongType.INSTANCE);
        }
        if (StringUtils.isNotEmpty(clusterId)) {
            query.append(" and lower(std.clusterId) like ?");
            params.add(WildcardTools.replaceWildcards(clusterId).toLowerCase());
            types.add(StringType.INSTANCE);
        }

        final Session session = sessionFactory.getCurrentSession();
        final Query q = session.createQuery(query.toString());
        q.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        q.setCacheable(true);
        final List<Object[]> result = q.list();

        final List<HVTStandort> standorts = new ArrayList<>();
        final List<HVTGruppe> groups = new ArrayList<>();
        result.stream().forEach(objects -> {
            if (objects[0] != null && !standorts.contains(objects[0])) {
                standorts.add((HVTStandort)objects[0]);
            }
            if (objects[1] != null && !groups.contains(objects[1])) {
                groups.add((HVTGruppe)objects[1]);
            }
        });

        // now search for possible orphan groups
        final List<Object> paramsGruppen = new ArrayList<>();
        final List<Type> typesGruppen = new ArrayList<>();
        final String baseQueryGruppen = String.format("select g from %s g where g.id is not null ",
                HVTGruppe.class.getName());
        final StringBuilder queryGruppen = new StringBuilder(baseQueryGruppen);

        if (StringUtils.isNotBlank(onkz)) {
            queryGruppen.append(" and lower(g.onkz) like ?");
            paramsGruppen.add(WildcardTools.replaceWildcards(onkz).toLowerCase());
            typesGruppen.add(StringType.INSTANCE);
        }
        if (StringUtils.isNotBlank(ortsteil)) {
            queryGruppen.append(" and lower(g.ortsteil) like ?");
            paramsGruppen.add(WildcardTools.replaceWildcards(ortsteil).toLowerCase());
            typesGruppen.add(StringType.INSTANCE);
        }
        if (StringUtils.isNotBlank(ort)) {
            queryGruppen.append(" and lower(g.ort) like ?");
            paramsGruppen.add(WildcardTools.replaceWildcards(ort).toLowerCase());
            typesGruppen.add(StringType.INSTANCE);
        }
        final Query qGruppen = session.createQuery(queryGruppen.toString());
        qGruppen.setParameters(paramsGruppen.toArray(), typesGruppen.toArray(new Type[typesGruppen.size()]));
        qGruppen.setCacheable(true);
        final List<Object> resultGruppen = qGruppen.list();
        resultGruppen.stream().forEach(objects -> {
            if (objects != null && !groups.contains(objects)) {
                groups.add((HVTGruppe) objects);
            }
        });

        return new Pair<>(standorts, groups);
    }

    @Override
    public List<HVTClusterView> findHVTClusterViews(final List<Long> hvtStandortIds) {
        final StringBuilder hql = new StringBuilder("SELECT std.id, std.clusterId, grp.onkz, n.areaNo FROM ");
        hql.append(HVTStandort.class.getName()).append(" std, ");
        hql.append(HVTGruppe.class.getName()).append(" grp, ");
        hql.append(Niederlassung.class.getName()).append(" n ");
        hql.append("WHERE std.hvtGruppeId=grp.id AND grp.niederlassungId=n.id ");
        hql.append("AND std.id IN (:hvtStandortIds)");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setParameterList("hvtStandortIds", hvtStandortIds);
        q.setCacheable(true);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<HVTClusterView> retVal = new ArrayList<HVTClusterView>();
            for (Object[] values : result) {
                HVTClusterView view = new HVTClusterView();
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, 0));
                view.setClusterId(ObjectTools.getStringSilent(values, 1));
                view.setOnkz(ObjectTools.getStringSilent(values, 2));
                view.setAreaNo(ObjectTools.getLongSilent(values, 3));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public HVTStandort findHVTStandort(String onkz, Integer asb) {
        StringBuilder sql = new StringBuilder();
        sql.append("select hs.HVT_ID_STANDORT from T_HVT_STANDORT hs ");
        sql.append("inner join T_HVT_GRUPPE hg on hg.HVT_GRUPPE_ID=hs.HVT_GRUPPE_ID ");
        sql.append("where hg.ONKZ=:onkz and hs.ASB=:asb ");
        sql.append("and hs.GUELTIG_BIS>=:now");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameter("onkz",onkz);
        sqlQuery.setInteger("asb", asb);
        sqlQuery.setDate("now", new java.util.Date());

        List<BigDecimal> result = sqlQuery.list();
        if (result != null) {
            if (result.size() == 1) {
                BigDecimal value = result.get(0);
                return findById(value.longValue(), HVTStandort.class);
            }
            else if (result.size() > 1) {
                throw new IncorrectResultSizeDataAccessException(
                        "HVT-Standort konnte nicht eindeutig ermittelt werden!!", 1, result.size());
            }
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


