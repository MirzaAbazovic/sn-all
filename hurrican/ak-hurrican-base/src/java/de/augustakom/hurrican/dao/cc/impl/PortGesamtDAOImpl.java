/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2005 09:12:35
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.PortGesamtDAO;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.PortGesamt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungFreigabeInfo;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;

/**
 * Hibernate DAO-Implementierung von <code>PortGesamtDAO</code>.
 *
 *
 */
public class PortGesamtDAOImpl extends Hibernate4DAOImpl implements PortGesamtDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deletePortGesamt() {
        sessionFactory.getCurrentSession().createSQLQuery("delete from T_PORT_GESAMT").executeUpdate();
    }

    @Override
    public Date selectPortGesamtDate() {
        StringBuilder hql = new StringBuilder("select max(pg.datum) from ");
        hql.append(PortGesamt.class.getName()).append(" pg");

        @SuppressWarnings("unchecked")
        List<Date> result = find(hql.toString());
        return ((result != null) && (result.size() == 1) && (result.get(0) instanceof Date)) ? result.get(0) : null;
    }


    @Override
    public List<PhysikFreigebenView> createPhysikFreigabeView(Date freigabeDatum, Boolean onlyKlaerfaelle) {
        StringBuilder sql = new StringBuilder("SELECT ad.AUFTRAG_ID, rang.BEMERKUNG, rang.RANGIER_ID, es.ID as ES_ID, ");
        sql.append(" auft.KUNDE__NO, pd.ANSCHLUSSART, ast.STATUS_TEXT, nl.TEXT as NIEDERLASSUNG, ");
        sql.append(" rfi.INFO, ad.PRODAK_ORDER__NO, vs.VERLAUF_STATUS, ad.STATUS_ID as AUFTRAG_STATUS_ID, ");
        sql.append(" cb.KUENDBESTAETIGUNG_CARRIER, pg.STATUS, rfi.IN_BEARBEITUNG as IN_BEARBEITUNG, ");
        sql.append(" hvt_gr.ORTSTEIL as HVT ");
        sql.append(" FROM T_auftrag auft ");
        sql.append(" INNER JOIN t_auftrag_daten ad ON auft.ID=ad.AUFTRAG_ID ");
        sql.append(" INNER JOIN t_auftrag_technik at ON ad.AUFTRAG_ID=at.AUFTRAG_ID ");
        sql.append(" INNER JOIN t_auftrag_status ast ON ad.STATUS_ID=ast.ID ");
        sql.append(" INNER JOIN t_produkt pd ON ad.PROD_ID=pd.PROD_ID ");
        sql.append(" INNER JOIN t_endstelle es ON at.AT_2_ES_ID=es.ES_GRUPPE ");
        sql.append(" INNER JOIN t_hvt_standort hvt_st ON es.HVT_ID_STANDORT=hvt_st.HVT_ID_STANDORT ");
        sql.append(" INNER JOIN t_hvt_gruppe hvt_gr ON hvt_st.HVT_GRUPPE_ID=hvt_gr.HVT_GRUPPE_ID ");
        sql.append(" RIGHT JOIN t_rangierung rang ON (es.RANGIER_ID=rang.RANGIER_ID");
        sql.append(" OR es.RANGIER_ID_ADDITIONAL=rang.RANGIER_ID) ");
        sql.append(" LEFT JOIN t_rangierung_freigabe_info rfi ON (rang.RANGIER_ID=rfi.RANGIER_ID ");
        sql.append(" AND (rfi.AUFTRAG_ID is null OR auft.ID=rfi.AUFTRAG_ID)) ");
        sql.append(" LEFT JOIN t_verlauf vlf ON auft.ID=vlf.AUFTRAG_ID ");
        sql.append(" LEFT JOIN t_verlauf_status vs ON vlf.VERLAUF_STATUS_ID=vs.ID ");
        sql.append(" LEFT JOIN t_carrierbestellung  cb ON es.CB_2_ES_ID=cb.CB_2_ES_ID ");
        sql.append(" LEFT JOIN t_niederlassung nl ON at.NIEDERLASSUNG_ID=nl.ID ");
        sql.append(" LEFT JOIN t_equipment equ ON rang.EQ_IN_ID=equ.EQ_ID ");
        sql.append(" left join T_HW_SWITCH sw on equ.SWITCH=sw.ID ");
        sql.append(" LEFT JOIN t_port_gesamt pg ON equ.HW_EQN=pg.PORT AND sw.NAME=pg.SWITCH ");
        sql.append(" WHERE (at.GUELTIG_BIS=? OR at.GUELTIG_BIS is null) ");
        sql.append(" AND (ad.GUELTIG_BIS=? OR ad.GUELTIG_BIS is null) ");
        sql.append(" AND rang.ES_ID=? AND rang.FREIGABE_AB<=? AND rang.GUELTIG_BIS=? ");
        sql.append(" AND (ad.status_id is null OR ad.STATUS_ID<?) ");
        if ((onlyKlaerfaelle != null) && onlyKlaerfaelle) {
            sql.append(" AND (rfi.INFO is Not Null) ");
        }
        sql.append(" AND (vlf.ID is null OR vlf.ID in (SELECT MAX(v.id) from t_verlauf v ");
        sql.append(" WHERE v.auftrag_id = auft.id GROUP BY v.auftrag_id)) ");
        sql.append(" AND (cb.CB_ID is null OR cb.CB_ID in (SELECT MAX(cb2.CB_ID) from t_carrierbestellung cb2 ");
        sql.append(" WHERE cb2.CB_2_ES_ID=es.CB_2_ES_ID GROUP BY cb2.CB_2_ES_ID))");
        sql.append(" ORDER BY rang.RANGIER_ID, ad.AUFTRAG_ID");

        Date end = DateTools.getHurricanEndDate();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { end, end, Rangierung.RANGIERUNG_NOT_ACTIVE, freigabeDatum, end, AuftragStatus.KONSOLIDIERT },
                new Type[] {new DateType(), new DateType(), new LongType(), new DateType(), new DateType(), new LongType()});

        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<PhysikFreigebenView> retVal = new ArrayList<PhysikFreigebenView>();
            for (Object[] values : result) {
                int columnIndex = 0;
                PhysikFreigebenView view = new PhysikFreigebenView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRangierungBemerkung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangierId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundenNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setTechProduct(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setClarifyInfo(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBaStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setCbKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setPortGesamtStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setInBearbeitung(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setHvt(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public void deleteRangierungFreigabeInfoById(final Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        RangierungFreigabeInfo key = (RangierungFreigabeInfo) session.get(RangierungFreigabeInfo.class, id);
        session.delete(key);
        session.flush();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
