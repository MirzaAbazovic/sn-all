/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2012 16:04:01
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.sql.Date;
import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragTvDAO;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.shared.view.TvFeedView;

/**
 * Hibernate DAO-Implementierung, um Objekte des Typs <code>AuftragTvGeoId</code> zu verwalten.
 */
public class AuftragTvDAOImpl extends HurricanHibernateDaoImpl implements AuftragTvDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<TvFeedView> findTvFeed4Auftraege(final List<Long> versorgendeAuftraegeIds, final List<Integer> buendelNrs) {
        List<TvFeedView> retVal = new ArrayList<>();
        if (CollectionUtils.isEmpty(versorgendeAuftraegeIds)) {
            return retVal;
        }

        final List<Long> statiToExclude = Lists.newArrayList(AuftragStatus.ABSAGE, AuftragStatus.STORNO);
        final Date now = DateTools.getActualSQLDate();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  es.GEO_ID as geoId, ");
        sql.append("  ad.GUELTIG_VON as geoIdGueltigVon, ");
        sql.append("  ad.GUELTIG_BIS as geoIdGueltigBis, ");
        sql.append("  ad.AUFTRAG_ID as auftragsId, ");
        sql.append("  ast.STATUS_TEXT as auftragsStatus, ");
        sql.append("  ad.PRODAK_ORDER__NO as billingAuftragsId, ");
        sql.append("  hvtg.ORTSTEIL as techStandortName, ");
        sql.append("  hwr.GERAETEBEZ as geraetebezeichnung, ");
        sql.append("  rang.ONT_ID as ontBezeichner, ");
        sql.append("  eq.HW_EQN  as portBezeichner ");
        sql.append("FROM t_auftrag_daten ad ");
        sql.append("  LEFT JOIN t_auftrag_status ast ON ad.STATUS_ID = ast.ID ");
        sql.append("  LEFT JOIN t_auftrag_technik at ON ad.AUFTRAG_ID = at.AUFTRAG_ID ");
        sql.append("  LEFT JOIN t_endstelle es ON at.AT_2_ES_ID = es.ES_GRUPPE ");
        sql.append("  LEFT JOIN t_hvt_standort hvts ON es.HVT_ID_STANDORT = HVTS.HVT_ID_STANDORT ");
        sql.append("  LEFT JOIN t_hvt_gruppe hvtg ON HVTS.HVT_GRUPPE_ID = hvtg.HVT_GRUPPE_ID ");
        sql.append("  LEFT JOIN t_rangierung rang ON es.RANGIER_ID = rang.RANGIER_ID ");
        sql.append("  LEFT JOIN t_equipment eq ON rang.EQ_IN_ID = eq.EQ_ID ");
        sql.append("  LEFT JOIN t_hw_baugruppe hwb ON eq.HW_BAUGRUPPEN_ID = HWB.ID ");
        sql.append("  LEFT JOIN t_hw_rack hwr ON HWB.RACK_ID = hwr.ID ");
        sql.append("WHERE ");
        sql.append("  ( ");
        sql.append("  ad.AUFTRAG_ID IN (:versorgendeAuftraegeIds) ");
        if (CollectionUtils.isNotEmpty(buendelNrs)) {
            sql.append(" or ad.BUENDEL_NR IN (:buendelNrs) ");
        }
        sql.append("  ) ");
        sql.append("  AND ad.GUELTIG_VON <= :now ");
        sql.append("  AND ad.GUELTIG_BIS > :now ");
        sql.append("  AND at.GUELTIG_VON <= :now ");
        sql.append("  AND at.GUELTIG_BIS > :now ");
        sql.append("  AND (rang.GUELTIG_VON IS NULL OR rang.GUELTIG_VON <= :now) ");
        sql.append("  AND (rang.GUELTIG_BIS IS NULL OR rang.GUELTIG_BIS > :now) ");
        sql.append("  AND es.ES_TYP = 'B' ");
        sql.append("AND ad.STATUS_ID < :auftragGekuendigt ");
        sql.append(" AND ad.STATUS_ID NOT IN (:statiToExclude)");
        sql.append("ORDER BY es.GEO_ID ");

        // query
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        // add parameters
        sqlQuery.setParameterList("versorgendeAuftraegeIds", versorgendeAuftraegeIds);
        if (CollectionUtils.isNotEmpty(buendelNrs)) {
            sqlQuery.setParameterList("buendelNrs", buendelNrs);
        }
        sqlQuery.setDate("now", now);
        sqlQuery.setLong("auftragGekuendigt", AuftragStatus.AUFTRAG_GEKUENDIGT);
        sqlQuery.setParameterList("statiToExclude", statiToExclude);

        List<TvFeedView> results = sqlQuery
                .addScalar("geoId", LongType.INSTANCE)
                .addScalar("geoIdGueltigVon")
                .addScalar("geoIdGueltigBis")
                .addScalar("auftragsId", LongType.INSTANCE)
                .addScalar("auftragsStatus")
                .addScalar("billingAuftragsId", LongType.INSTANCE)
                .addScalar("techStandortName")
                .addScalar("geraetebezeichnung")
                .addScalar("ontBezeichner")
                .addScalar("portBezeichner")
                .setResultTransformer(Transformers.aliasToBean(TvFeedView.class)).list();

        return results;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

