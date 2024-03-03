/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2005 16:33:14
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.VerlaufViewDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.VerlaufAmRlView;
import de.augustakom.hurrican.model.cc.view.VerlaufDispoRLView;
import de.augustakom.hurrican.model.cc.view.VerlaufEXTView;
import de.augustakom.hurrican.model.cc.view.VerlaufFieldServiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufStConnectView;
import de.augustakom.hurrican.model.cc.view.VerlaufStOnlineView;
import de.augustakom.hurrican.model.cc.view.VerlaufStVoiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;


/**
 * Hibernate DAO-Implementierung von <code>VerlaufViewDAO</code>.
 *
 *
 */
public class VerlaufViewDAOImpl extends Hibernate4DAOImpl implements VerlaufViewDAO {

    private static final Logger LOGGER = Logger.getLogger(VerlaufViewDAOImpl.class);
    /*
     * Sub-Select ermittelt den CPS-Tx Status der letzten CPS-Tx zu dem Taifun-Auftrag
     */
    private static final String SUB_SELECT_MAX_CPS_TX =
            "(select TX_STATE as CPS_TX_STATE from T_CPS_TX cps where cps.VERLAUF_ID=v.ID " +
                    "  and cps.ID=(select max(ID) from T_CPS_TX cpstmp where cpstmp.VERLAUF_ID=v.ID)) as CPS_TX_STATE";

    private static final String SUB_SELECT_LAST_CB_TAL_TIMESLOT =
            "(select TAL_REAL_TIMESLOT as CB_TAL_REAL_TIMESLOT from T_CARRIERBESTELLUNG cbest "+
                    " where cbest.CB_2_ES_ID=esb.CB_2_ES_ID " +
                    " and cbest.CB_ID=(select max(CB_ID) from T_CARRIERBESTELLUNG cbtmp where cbtmp.CB_2_ES_ID=esb.CB_2_ES_ID))" +
                    " as CB_TAL_REAL_TIMESLOT";

    private static final String SUB_SELECT_SUBORDERS =
            " (select count(*) from T_VERLAUF_SUB_ORDERS sub WHERE sub.VERLAUF_ID = v.ID) as hasSubOrders ";
    private static final String SUB_SELECT_BA_HINWEISE =
            " (select listagg(tl.BA_HINWEIS, ',') within group (order by tl.BA_HINWEIS) from T_TECH_LEISTUNG tl " +
                    " join T_AUFTRAG_2_TECH_LS at2tl on tl.ID = at2tl.TECH_LS_ID " +
                    " where at2tl.AUFTRAG_ID = v.AUFTRAG_ID and " +
                    " (v.REALISIERUNGSTERMIN is null or v.REALISIERUNGSTERMIN <= NVL(at2tl.AKTIV_BIS, v.REALISIERUNGSTERMIN))) as BA_HINWEISE ";
    private static final String SUB_SELECT_VERLAUF_AT_TECHNIK_ABT_COUNT =
            " (SELECT COUNT(*) FROM T_VERLAUF_ABTEILUNG vas WHERE vas.VERLAUF_ID = v.ID"
                    + " AND vas.DATUM_ERLEDIGT IS NULL AND vas.ABTEILUNG_ID NOT IN (" + Abteilung.AM + "," + Abteilung.DISPO
                    + "))" + " AS VERLAUF_AT_TECHNIK_ABT_COUNT";
    private static final String DECODE_KREUZUNG =
            " DECODE(phu.AENDERUNGSTYP, 5002, 1, 5003, 1, 5005, 1, 0) as kreuzung, "; // Wandel analog-ISDN, Wandel ISDN-analog, DSL-Kreuzung
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<VerlaufUniversalView> findBasWithUniversalQuery(Long abteilungId, Date realisierungFrom, Date realisierungTo) {
        return find4DispoOrNP(abteilungId, realisierungFrom, realisierungTo, null, true);
    }

    @Override
    public List<VerlaufUniversalView> find4DispoOrNP(Long abteilungId, java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        return find4DispoOrNP(abteilungId, realisierungFrom, realisierungTo, VerlaufStatus.STATUS_IM_UMLAUF, false);
    }

    private List<VerlaufUniversalView> find4DispoOrNP(Long abteilungId, java.util.Date realisierungFrom, java.util.Date realisierungTo, Long verlaufStatusId, boolean withBearbeiter) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.PORTIERUNGSART, v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, ");
        sql.append(" pa.TEXT as PORT_ART_TEXT, va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.DATUM_AN, va.REALISIERUNGSDATUM, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, va.WIEDERVORLAGE, va.BEARBEITER, ");
        sql.append(" a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, ad.STATUS_ID, bva.NAME as ANLASS, t.TDN, p.ANSCHLUSSART, ");
        sql.append(" esa.ID as ES_ID_A, esb.ID as ES_ID_B, esb.ORT as ES_ORT_B, esb.ENDSTELLE as ES_B, asa.ANSCHLUSSART as HVT_ANSCHLUSS, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, at.PROJECT_RESPONSIBLE as PROJEKTVERANTWORTLICHER, vs.VERLAUF_STATUS, ");
        sql.append(" hvt.CLUSTER_ID, vpn.VPN_NR, ");
        sql.append(DECODE_KREUZUNG);
        sql.append(SUB_SELECT_BA_HINWEISE);
        sql.append(",");
        sql.append(SUB_SELECT_MAX_CPS_TX);
        sql.append(",");
        sql.append(SUB_SELECT_LAST_CB_TAL_TIMESLOT);
        sql.append(",");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", dpo.DPO_CHASSIS_IDENTIFIER, ");
        sql.append(" rack.GERAETEBEZ ");
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_PORTIERUNGSART pa on v.PORTIERUNGSART=pa.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ANSCHLUSSART asa on asa.ID=esb.ANSCHLUSSART ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_HVT_STANDORT hvt on esb.HVT_ID_STANDORT=hvt.HVT_ID_STANDORT ");
        sql.append(" left join T_PHYSIKUEBERNAHME phu on v.ID=phu.VERLAUF_ID ");
        sql.append(" left join T_RANGIERUNG r on (r.rangier_id = esb.RANGIER_ID) ");
        sql.append(" left join T_EQUIPMENT eqIn on (r.EQ_IN_ID = eqIn.EQ_ID) ");
        sql.append(" left join T_HW_BAUGRUPPE bg on (eqIn.HW_BAUGRUPPEN_ID = bg.ID) ");
        sql.append(" left join T_HW_RACK_DPO dpo on (bg.RACK_ID = dpo.RACK_ID) ");
        sql.append(" left join T_HW_RACK rack on (bg.RACK_ID = rack.ID) ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null");
            if (verlaufStatusId != null) {
                sql.append(" and va.VERLAUF_STATUS_ID=").append(verlaufStatusId);
            }
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<> ").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<> ").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }

        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and ad.GUELTIG_VON <=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");

        sql.append(" and va.ABTEILUNG_ID=").append(abteilungId);
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc ");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufUniversalView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufUniversalView view = new VerlaufUniversalView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setPortierungsartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setDatumAnAbteilung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));

                if (withBearbeiter) {
                    view.setBearbeiterAbteilung(ObjectTools.getStringSilent(values, columnIndex++));
                } else {
                    columnIndex++;
                }

                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleOrtB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProjectResponsibleId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtClusterId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKreuzung(ObjectTools.getBooleanSilent(values, columnIndex++));
                setBaHinweise(values, view, columnIndex++);
                view.setCpsTxState(ObjectTools.getLongSilent(values, columnIndex++));
                view.getTimeSlot().setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.getNullSafe(
                        ObjectTools.getStringSilent(values, columnIndex++)));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setDpoChassis(ObjectTools.getStringSilent(values, columnIndex++));
                view.setGeraeteBez(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    private void setBaHinweise(Object[] values, AbstractVerlaufView view, int columnIndex) {
        String baHinweiseStr = ObjectTools.getStringSilent(values, columnIndex);
        if (StringUtils.isNotBlank(baHinweiseStr)) {
            // doubletten entfernen
            Set<String> baHinweiseSet = Sets.newHashSet();
            StringBuilder sb = new StringBuilder();
            for (String baHinweis : baHinweiseStr.split(",")) {
                if (baHinweiseSet.add(baHinweis)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(baHinweis);
                }
            }
            view.setBaHinweise(sb.toString());
        }
    }

    /**
     * Fügt an das SQL-Statement des übergebenen {@link StringBuilder}s die notwendigen Erweiterungen für die
     * übergebenen Realisierungstermine hinzu (<= und >=). Dabei wird der gegebene Alias für den Join der
     * VerlaufAbteilung beachtet. Dazu werden auch die entsprechenden Parameter für das SQL-Statement gesetzt
     */
    private void appendSqlAndSetParams4Realisierung(StringBuilder sql, String aliasVerlaufAbteilung,
            List<Object> params, List<Type> types, java.util.Date realisierungFrom, java.util.Date realisierungTo) {

        if (realisierungFrom != null) {
            sql.append(" and ").append(aliasVerlaufAbteilung).append(".REALISIERUNGSDATUM >= ?");
            params.add(DateUtils.truncate(realisierungFrom, Calendar.DATE));
            types.add(new DateType());
        }
        if (realisierungTo != null) {
            sql.append(" and ").append(aliasVerlaufAbteilung).append(".REALISIERUNGSDATUM <= ?");
            params.add(DateUtils.truncate(realisierungTo, Calendar.DATE));
            types.add(new DateType());
        }
    }

    @Override
    public List<VerlaufDispoRLView> findRL4DispoOrNP(Long abteilungId, java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.OBSERVE_PROCESS, va.ID as VA_ID, va.VERLAUF_STATUS_ID, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, va.WIEDERVORLAGE, ");
        sql.append(" va.REALISIERUNGSDATUM, va.BEARBEITER as VERTEILER, a.KUNDE__NO, ad.PROD_ID, ad.INBETRIEBNAHME, ");
        sql.append(" ad.PRODAK_ORDER__NO, ad.BEARBEITER, bva.NAME as ANLASS, t.TDN, p.ANSCHLUSSART, ");
        sql.append(" esa.ID as ES_ID_A, esb.ID as ES_ID_B, esb.ORT as ES_B_ORT, esb.ENDSTELLE as ES_B, asa.ANSCHLUSSART as HVT_ANSCHLUSS, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, v.NOT_POSSIBLE, at.PROJECT_RESPONSIBLE, vs.VERLAUF_STATUS, ");
        sql.append(" hvt.CLUSTER_ID, ");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(",");
        sql.append(SUB_SELECT_LAST_CB_TAL_TIMESLOT);
        sql.append(",");
        sql.append(SUB_SELECT_VERLAUF_AT_TECHNIK_ABT_COUNT);
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ANSCHLUSSART asa on asa.ID=esb.ANSCHLUSSART ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_HVT_STANDORT hvt on esb.HVT_ID_STANDORT=hvt.HVT_ID_STANDORT ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null and va.VERLAUF_STATUS_ID=").append(VerlaufStatus.STATUS_IN_BEARBEITUNG);
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }

        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" and va.ABTEILUNG_ID=").append(abteilungId);
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufDispoRLView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufDispoRLView view = new VerlaufDispoRLView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerteiler(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiterAm(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleOrtB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufNotPossible(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setProjectResponsibleId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtClusterId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.getTimeSlot().setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.getNullSafe(
                        ObjectTools.getStringSilent(values, columnIndex++)));
                view.setErledigt((ObjectTools.getIntegerSilent(values, columnIndex++) <= 0) ? Boolean.TRUE : Boolean.FALSE);
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufStConnectView> find4STConnect(java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.OBSERVE_PROCESS, va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.WIEDERVORLAGE, ");
        sql.append(" va.BEARBEITER, vs.VERLAUF_STATUS, a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, bva.NAME as ANLASS, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, ");
        sql.append(" t.TDN, p.ANSCHLUSSART, s.SCHNITTSTELLE, larta.NAME as LEITUNG_A, lartb.NAME as LEITUNG_B, ");
        sql.append(" vpn.VPN_NR, esa.ID as ES_ID_A, esb.ID as ES_ID_B, va.REALISIERUNGSDATUM, ");
        sql.append(" dp.NAME as DSLAM_PROFILE, va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, ");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", ");
        sql.append(SUB_SELECT_MAX_CPS_TX);
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ES_LTG_DATEN ltga on ltga.ES_ID=esa.ID ");
        sql.append(" left join T_ES_LTG_DATEN ltgb on ltgb.ES_ID=esb.ID ");
        sql.append(" left join T_LEITUNGSART larta on larta.ID=ltga.LEITUNGSART ");
        sql.append(" left join T_LEITUNGSART lartb on lartb.ID=ltgb.LEITUNGSART ");
        sql.append(" left join T_SCHNITTSTELLE s on (s.ID=ltga.SCHNITTSTELLE_ID or s.ID=ltgb.SCHNITTSTELLE_ID) ");
        sql.append(" left join T_AUFTRAG_2_DSLAMPROFILE a2dp on (a.ID=a2dp.AUFTRAG_ID and a2dp.GUELTIG_BIS=?) ");
        sql.append(" left join T_DSLAM_PROFILE dp on a2dp.DSLAM_PROFILE_ID=dp.ID ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();
        params.add(DateTools.getHurricanEndDate());
        types.add(new DateType());

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null ");
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }
        sql.append(" and va.ABTEILUNG_ID=").append(Abteilung.ST_CONNECT);

        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (ltga.GUELTIG_VON is null or ltga.GUELTIG_VON<=?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltga.GUELTIG_BIS is null or ltga.GUELTIG_BIS>?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltgb.GUELTIG_VON is null or ltgb.GUELTIG_VON<=?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltgb.GUELTIG_BIS is null or ltgb.GUELTIG_BIS>?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufStConnectView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufStConnectView view = new VerlaufStConnectView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBearbeiterSDH(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSchnittstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setDslamProfile(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setCpsTxState(ObjectTools.getLongSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufStVoiceView> find4STVoice(java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, ");
        sql.append(" v.PORTIERUNGSART, pa.TEXT as PORT_ART_TEXT, va.WIEDERVORLAGE, ");
        sql.append(" va.ID as VA_ID, va.BEARBEITER, va.VERLAUF_STATUS_ID, a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, ");
        sql.append(" bva.NAME as ANLASS, t.TDN, p.ANSCHLUSSART, esa.ID as ES_ID_A, esb.ID as ES_ID_B, ");
        sql.append(" cb.BEREITSTELLUNG_AM, c.TEXT as CARRIER_NAME, ");
        sql.append(" swa.NAME as SWITCH_A, swb.NAME as SWITCH_B, voip.ID as VOIP_ID, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, va.REALISIERUNGSDATUM, vs.VERLAUF_STATUS, ");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", ");
        sql.append(SUB_SELECT_MAX_CPS_TX);
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_PORTIERUNGSART pa on v.PORTIERUNGSART=pa.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_CARRIERBESTELLUNG cb on cb.CB_2_ES_ID=esb.CB_2_ES_ID ");
        sql.append(" left join T_CARRIER c on c.ID=cb.CARRIER_ID ");
        sql.append(" left join T_RANGIERUNG ra on ra.RANGIER_ID=esa.RANGIER_ID ");
        sql.append(" left join T_RANGIERUNG rb on rb.RANGIER_ID=esb.RANGIER_ID ");
        sql.append(" left join T_HVT_STANDORT hsa on hsa.HVT_ID_STANDORT=ra.HVT_ID_STANDORT ");
        sql.append(" left join T_HVT_STANDORT hsb on hsb.HVT_ID_STANDORT=rb.HVT_ID_STANDORT ");
        sql.append(" left join T_HVT_GRUPPE hvtga on hvtga.HVT_GRUPPE_ID=hsa.HVT_GRUPPE_ID ");
        sql.append(" left join T_HW_SWITCH swa on hvtga.SWITCH=swa.ID ");
        sql.append(" left join T_HVT_GRUPPE hvtgb on hvtgb.HVT_GRUPPE_ID=hsb.HVT_GRUPPE_ID ");
        sql.append(" left join T_HW_SWITCH swb on hvtgb.SWITCH=swb.ID ");
        sql.append(" left join T_AUFTRAG_VOIP voip on voip.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null ");
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }
        sql.append(" and va.ABTEILUNG_ID=").append(Abteilung.ST_VOICE);
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? and ad.GUELTIG_VON <=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (voip.GUELTIG_BIS is null or voip.GUELTIG_BIS>?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufStVoiceView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufStVoiceView view = new VerlaufStVoiceView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setPortierungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiterSTVoice(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setCarrierBereitstellung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSwitchEsA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSwitchEsB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVoIP((ObjectTools.getIntegerSilent(values, columnIndex++) != null) ? Boolean.TRUE : Boolean.FALSE);
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setCpsTxState(ObjectTools.getLongSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufStOnlineView> find4STOnline(Long abteilungId, java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, ");
        sql.append(" va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.REALISIERUNGSDATUM, va.WIEDERVORLAGE, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, ");
        sql.append(" a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, bva.NAME as ANLASS, t.TDN, ");
        sql.append(" p.ANSCHLUSSART, vpn.VPN_NR, va.BEARBEITER, vs.VERLAUF_STATUS, ");
        sql.append(" s.SCHNITTSTELLE, dp.NAME as DSLAM_PROFILE, esb.ID as ES_ID_B, esa.ID as ES_ID_A, ");
        sql.append(" larta.NAME as LEITUNG_A, lartb.NAME as LEITUNG_B, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, v.PORTIERUNGSART, pa.TEXT as PORT_ART_TEXT, ");
        sql.append(DECODE_KREUZUNG);
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", ");
        sql.append(SUB_SELECT_MAX_CPS_TX);
        sql.append(", ");
        sql.append(SUB_SELECT_LAST_CB_TAL_TIMESLOT);
        sql.append(", ");
        sql.append(" rack.GERAETEBEZ ");
        sql.append(", ");
        sql.append(SUB_SELECT_BA_HINWEISE);
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_PORTIERUNGSART pa on v.PORTIERUNGSART=pa.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ES_LTG_DATEN ltga on ltga.ES_ID=esa.ID ");
        sql.append(" left join T_ES_LTG_DATEN ltgb on ltgb.ES_ID=esb.ID ");
        sql.append(" left join T_LEITUNGSART larta on larta.ID=ltga.LEITUNGSART ");
        sql.append(" left join T_LEITUNGSART lartb on lartb.ID=ltgb.LEITUNGSART ");
        sql.append(" left join T_SCHNITTSTELLE s on s.ID=ltgb.SCHNITTSTELLE_ID ");
        sql.append(" left join T_AUFTRAG_2_DSLAMPROFILE a2dp on (a.ID=a2dp.AUFTRAG_ID and a2dp.GUELTIG_BIS=?) ");
        sql.append(" left join T_DSLAM_PROFILE dp on a2dp.DSLAM_PROFILE_ID=dp.ID ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_PHYSIKUEBERNAHME phu on v.ID=phu.VERLAUF_ID ");
        sql.append(" left join T_RANGIERUNG r on (r.rangier_id = esb.RANGIER_ID) ");
        sql.append(" left join T_EQUIPMENT eqIn on (r.EQ_IN_ID = eqIn.EQ_ID) ");
        sql.append(" left join T_HW_BAUGRUPPE bg on (eqIn.HW_BAUGRUPPEN_ID = bg.ID) ");
        sql.append(" left join T_HW_RACK rack on (bg.RACK_ID = rack.ID) ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();
        params.add(DateTools.getHurricanEndDate());
        types.add(new DateType());

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null and va.VERLAUF_STATUS_ID<>").append(VerlaufStatus.STATUS_ERLEDIGT);
            sql.append(" and va.VERLAUF_STATUS_ID<>").append(VerlaufStatus.STATUS_ERLEDIGT_SYSTEM);
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }

        sql.append(" and va.ABTEILUNG_ID=").append(abteilungId);
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufStOnlineView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufStOnlineView view = new VerlaufStOnlineView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiterIPS(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSchnittstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setDslamProfile(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setLeitungA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPortierungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPortierungsartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKreuzung(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setCpsTxState(ObjectTools.getLongSilent(values, columnIndex++));
                view.getTimeSlot().setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.getNullSafe(
                        ObjectTools.getStringSilent(values, columnIndex++)));
                view.setGeraeteBez(ObjectTools.getStringSilent(values, columnIndex++));
                setBaHinweise(values, view, columnIndex++);

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufEXTView> find4EXTERN(Long abteilungId, java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.PORTIERUNGSART, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, ");
        sql.append(" pa.TEXT as PORT_ART_TEXT, va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.REALISIERUNGSDATUM, va.WIEDERVORLAGE, ");
        sql.append(" vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, ");
        sql.append(" vs.VERLAUF_STATUS, a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, ad.STATUS_ID, ");
        sql.append(" bva.NAME as ANLASS, t.TDN, esa.ID as ES_ID_A, esb.ID as ES_ID_B, esb.ORT as ES_B_ORT, ");
        sql.append(" cb.BEREITSTELLUNG_AM, c.TEXT as CARRIER_NAME, p.ANSCHLUSSART, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, ext.id as EXT_SERVICE_PROVIDER_ID, ext.name as EXT_SERVICE_PROVIDER_NAME, ");
        sql.append(" hvt.CLUSTER_ID, ");
        sql.append(DECODE_KREUZUNG);
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", ");
        sql.append(SUB_SELECT_LAST_CB_TAL_TIMESLOT);
        sql.append(", ");
        sql.append(" rack.GERAETEBEZ ");
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_PORTIERUNGSART pa on v.PORTIERUNGSART=pa.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_CARRIERBESTELLUNG cb on cb.CB_2_ES_ID=esb.CB_2_ES_ID ");
        sql.append(" left join T_CARRIER c on c.ID=cb.CARRIER_ID ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_EXT_SERVICE_PROVIDER ext on ext.ID=va.EXT_SERVICE_PROVIDER_ID ");
        sql.append(" left join T_HVT_STANDORT hvt on esb.HVT_ID_STANDORT=hvt.HVT_ID_STANDORT ");
        sql.append(" left join T_PHYSIKUEBERNAHME phu on v.ID=phu.VERLAUF_ID ");
        sql.append(" left join T_RANGIERUNG r on (r.rangier_id = esb.RANGIER_ID) ");
        sql.append(" left join T_EQUIPMENT eqIn on (r.EQ_IN_ID = eqIn.EQ_ID) ");
        sql.append(" left join T_HW_BAUGRUPPE bg on (eqIn.HW_BAUGRUPPEN_ID = bg.ID) ");
        sql.append(" left join T_HW_RACK rack on (bg.RACK_ID = rack.ID) ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG='0') ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT='1' and va.DATUM_ERLEDIGT is null ");
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }

        sql.append(" and va.ABTEILUNG_ID=").append(abteilungId);
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufEXTView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufEXTView view = new VerlaufEXTView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleOrtB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCarrierBereitstellung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setExtServiceProviderId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setExtServiceProviderName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtClusterId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKreuzung(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.getTimeSlot().setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.getNullSafe(
                        ObjectTools.getStringSilent(values, columnIndex++)));
                view.setGeraeteBez(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufFieldServiceView> find4FieldService(java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.PORTIERUNGSART, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, ");
        sql.append(" pa.TEXT as PORT_ART_TEXT, va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.BEARBEITER as SCTUSER, ");
        sql.append(" va.WIEDERVORLAGE, vas.STATUS as VERLAUF_ABTEILUNG_STATUS, va.VERLAUF_ABTEILUNG_STATUS_ID, ");
        sql.append(" vs.VERLAUF_STATUS, a.KUNDE__NO, ad.PROD_ID, ad.PRODAK_ORDER__NO, va.REALISIERUNGSDATUM, ");
        sql.append(" bva.NAME as ANLASS, t.TDN, s.SCHNITTSTELLE, larta.NAME as LEITUNG_A, lartb.NAME as LEITUNG_B, ");
        sql.append(" esa.ID as ES_ID_A, esb.ID as ES_ID_B, esb.ORT as ES_B_ORT, esb.ENDSTELLE as ES_B, ");
        sql.append(" cb.BEREITSTELLUNG_AM, c.TEXT as CARRIER_NAME, p.ANSCHLUSSART as PRODUKTANSCHLUSS, ");
        sql.append(" aart.ANSCHLUSSART as ANSCHLUSSART, vpn.VPN_NR, va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, ");
        sql.append(" hvt.CLUSTER_ID, ");
        sql.append(DECODE_KREUZUNG);
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(", dpo.DPO_CHASSIS_IDENTIFIER, ");
        sql.append(SUB_SELECT_LAST_CB_TAL_TIMESLOT);
        sql.append(", ");
        sql.append(" rack.GERAETEBEZ ");
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_VERLAUF_ABTEILUNG_STATUS vas on vas.ID=va.VERLAUF_ABTEILUNG_STATUS_ID ");
        sql.append(" left join T_PORTIERUNGSART pa on v.PORTIERUNGSART=pa.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ANSCHLUSSART aart on esb.ANSCHLUSSART=aart.ID ");
        sql.append(" left join T_CARRIERBESTELLUNG cb on cb.CB_2_ES_ID=esb.CB_2_ES_ID ");
        sql.append(" left join T_CARRIER c on c.ID=cb.CARRIER_ID ");
        sql.append(" left join T_ES_LTG_DATEN ltga on ltga.ES_ID=esa.ID ");
        sql.append(" left join T_ES_LTG_DATEN ltgb on ltgb.ES_ID=esb.ID ");
        sql.append(" left join T_LEITUNGSART larta on larta.ID=ltga.LEITUNGSART ");
        sql.append(" left join T_LEITUNGSART lartb on lartb.ID=ltgb.LEITUNGSART ");
        sql.append(" left join T_SCHNITTSTELLE s on (s.ID=ltga.SCHNITTSTELLE_ID or s.ID=ltgb.SCHNITTSTELLE_ID) ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_HVT_STANDORT hvt on esb.HVT_ID_STANDORT=hvt.HVT_ID_STANDORT ");
        sql.append(" left join T_PHYSIKUEBERNAHME phu on v.ID=phu.VERLAUF_ID ");
        sql.append(" left join T_RANGIERUNG r on (r.rangier_id = esb.RANGIER_ID) ");
        sql.append(" left join T_EQUIPMENT eqIn on (r.EQ_IN_ID = eqIn.EQ_ID) ");
        sql.append(" left join T_HW_BAUGRUPPE bg on (eqIn.HW_BAUGRUPPEN_ID = bg.ID) ");
        sql.append(" left join T_HW_RACK_DPO dpo on (bg.RACK_ID = dpo.RACK_ID) ");
        sql.append(" left join T_HW_RACK rack on (bg.RACK_ID = rack.ID) ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null ");
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.VERLAUF_STORNIERT);
            sql.append(" and v.VERLAUF_STATUS_ID<>").append(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        }

        sql.append(" and va.ABTEILUNG_ID=").append(Abteilung.FIELD_SERVICE);
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (ltga.GUELTIG_VON is null or ltga.GUELTIG_VON<=?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltga.GUELTIG_BIS is null or ltga.GUELTIG_BIS>?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltgb.GUELTIG_VON is null or ltgb.GUELTIG_VON <=?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (ltgb.GUELTIG_BIS is null or ltgb.GUELTIG_BIS>?) ");
        params.add(now);
        types.add(new DateType());

        sql.append(" and (esa.ES_TYP is null or esa.ES_TYP='A') and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufFieldServiceView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufFieldServiceView view = new VerlaufFieldServiceView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setGesamtrealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsartId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPortierungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiter(ObjectTools.getStringSilent(values, columnIndex++));
                view.setWiedervorlage(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufAbteilungStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSchnittstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleOrtB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCarrierBereitstellung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnschlussArt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtClusterId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKreuzung(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setDpoChassis(ObjectTools.getStringSilent(values, columnIndex++));
                view.getTimeSlot().setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster.getNullSafe(
                        ObjectTools.getStringSilent(values, columnIndex++)));
                view.setGeraeteBez(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<VerlaufAmRlView> findRL4Am(java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, v.REALISIERUNGSTERMIN, ");
        sql.append(" v.VERSCHOBEN, v.ANLASS as ANLASS_ID, v.INSTALLATION_REF_ID, v.OBSERVE_PROCESS, va.ID as VA_ID, ");
        sql.append(" va.VERLAUF_STATUS_ID, va.BEARBEITER, a.KUNDE__NO, ad.PROD_ID, ");
        sql.append(" ad.INBETRIEBNAHME, ad.PRODAK_ORDER__NO, ad.BUENDEL_NR, ");
        sql.append(" bva.NAME as ANLASS, t.TDN, vpn.VPN_NR, p.ANSCHLUSSART, esa.ID as ES_ID_A, esb.ID as ES_ID_B, ");
        sql.append(" asa.ANSCHLUSSART as HVT_ANSCHLUSS, ad.BEARBEITER as BEARBEITER_SCV, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, pg.AM_RESPONSIBILITY, v.NOT_POSSIBLE, vs.VERLAUF_STATUS, ");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID ");
        sql.append(" inner join T_BA_VERL_ANLASS bva on bva.ID=v.ANLASS ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append(" left join T_ANSCHLUSSART asa on asa.ID=esb.ANSCHLUSSART ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();

        sql.append(" where (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=0) ");
        sql.append(" and va.ABTEILUNG_ID=").append(Abteilung.AM);

        if (onlyActiveVerlaeufe(realisierungFrom, realisierungTo)) {
            sql.append(" and v.AKT=1 and va.DATUM_ERLEDIGT is null and v.VERLAUF_STATUS_ID in (?,?) and va.VERLAUF_STATUS_ID<? ");
            params.add(VerlaufStatus.RUECKLAEUFER_AM);
            params.add(VerlaufStatus.KUENDIGUNG_RL_AM);
            params.add(VerlaufStatus.STATUS_ERLEDIGT);
            types.add(new LongType());
            types.add(new LongType());
            types.add(new LongType());
        }
        else {
            appendSqlAndSetParams4Realisierung(sql, "va", params, types, realisierungFrom, realisierungTo);
            sql.append(" and v.VERLAUF_STATUS_ID not in (?,?) ");
            params.add(VerlaufStatus.VERLAUF_STORNIERT);
            params.add(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
            types.add(new LongType());
            types.add(new LongType());
        }

        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());

        sql.append(" and (esb.ES_TYP is null or esb.ES_TYP='B') ");
        sql.append(" order by v.REALISIERUNGSTERMIN asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<VerlaufAmRlView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                VerlaufAmRlView view = new VerlaufAmRlView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVerschoben(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnlassId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInstallationRefId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiter(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBuendelNr(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEsIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setHvtAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBearbeiterAm(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAmResponsibility(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufNotPossible(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<ProjektierungsView> findProjektierungen(Long abteilungId, boolean ruecklaeufer) {
        StringBuilder sql = new StringBuilder("select v.ID as VID, v.AUFTRAG_ID, ");
        sql.append(" v.OBSERVE_PROCESS, va.ID as VA_ID, va.VERLAUF_STATUS_ID, va.BEARBEITER, ");
        sql.append(" vs.VERLAUF_STATUS, a.KUNDE__NO, ad.BUENDEL_NR, ad.PROD_ID, ad.PRODAK_ORDER__NO, ad.VORGABE_SCV, ");
        sql.append(" ad.BEARBEITER as AUFTRAG_BEARBEITER, t.TDN, vpn.VPN_NR, p.ANSCHLUSSART, va.REALISIERUNGSDATUM, ");
        sql.append(" va.NIEDERLASSUNG_ID, n.TEXT as NIEDERLASSUNG, pg.AM_RESPONSIBILITY, v.NOT_POSSIBLE, ");
        sql.append(" at.PREVENT_CPS_PROV as PREVENT_CPS_PROV_ORDER, at.PROJECT_RESPONSIBLE as PROJECTRESPONSIBLE, ");
        sql.append(SUB_SELECT_BA_HINWEISE);
        sql.append(",");
        sql.append(SUB_SELECT_SUBORDERS);
        sql.append(",");
        sql.append(SUB_SELECT_VERLAUF_AT_TECHNIK_ABT_COUNT);
        sql.append(", ");
        sql.append(" rack.GERAETEBEZ ");
        sql.append(" from T_VERLAUF v ");
        sql.append(" inner join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append(" inner join T_VERLAUF_STATUS vs on vs.ID=va.VERLAUF_STATUS_ID ");
        sql.append(" inner join T_AUFTRAG a on a.ID=v.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID ");
        sql.append(" left join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_ENDSTELLE esb on (at.AT_2_ES_ID=esb.ES_GRUPPE and esb.ES_TYP=?) ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append(" left join T_NIEDERLASSUNG n on n.ID=va.NIEDERLASSUNG_ID ");
        sql.append(" left join T_RANGIERUNG r on (r.rangier_id = esb.RANGIER_ID) ");
        sql.append(" left join T_EQUIPMENT eqIn on (r.EQ_IN_ID = eqIn.EQ_ID) ");
        sql.append(" left join T_HW_BAUGRUPPE bg on (eqIn.HW_BAUGRUPPEN_ID = bg.ID) ");
        sql.append(" left join T_HW_RACK rack on (bg.RACK_ID = rack.ID) ");
        sql.append(" where v.AKT=1 and v.PROJEKTIERUNG=1 and va.DATUM_ERLEDIGT is null ");
        if (Abteilung.isDispoOrNP(abteilungId)) {
            sql.append(" and va.ABTEILUNG_ID in (").append(Abteilung.DISPO).append(",").append(Abteilung.NP).append(")");
        }
        else {
            sql.append(" and va.ABTEILUNG_ID=").append(abteilungId);
        }
        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and (at.GUELTIG_VON is null or at.GUELTIG_VON<=?) ");
        sql.append(" and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?) ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();
        params.add(Endstelle.ENDSTELLEN_TYP_B);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new StringType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        if (NumberTools.isIn(abteilungId, Abteilung.getDispoAndNP())) {
            sql.append(" and va.ABTEILUNG_ID=? ");
            params.add(abteilungId);
            types.add(new LongType());

            if (ruecklaeufer) {
                sql.append(" and va.VERLAUF_STATUS_ID=? and v.VERLAUF_STATUS_ID>=? and v.VERLAUF_STATUS_ID<=? ");
                params.add(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                params.add(VerlaufStatus.BEI_ZENTRALER_DISPO);
                params.add(VerlaufStatus.RUECKLAEUFER_ZENTRALE_DISPO);
                types.add(new LongType());
                types.add(new LongType());
                types.add(new LongType());
            }
            else {
                sql.append(" and va.VERLAUF_STATUS_ID=? and v.VERLAUF_STATUS_ID>=? ");
                params.add(VerlaufStatus.STATUS_IM_UMLAUF);
                params.add(VerlaufStatus.BEI_ZENTRALER_DISPO);
                types.add(new LongType());
                types.add(new LongType());
            }
        }
        else if (NumberTools.equal(abteilungId, Abteilung.AM)) {
            sql.append(" and va.VERLAUF_STATUS_ID<? and v.VERLAUF_STATUS_ID=? ");
            params.add(VerlaufStatus.STATUS_ERLEDIGT);
            params.add(VerlaufStatus.RUECKLAEUFER_AM);
            types.add(new LongType());
            types.add(new LongType());
        }
        else {
            sql.append(" and va.VERLAUF_STATUS_ID<? and (v.VERLAUF_STATUS_ID>=? or v.VERLAUF_STATUS_ID<=?) ");
            params.add(VerlaufStatus.STATUS_ERLEDIGT);
            params.add(VerlaufStatus.BEI_TECHNIK);
            params.add(VerlaufStatus.RUECKLAEUFER_ZENTRALE_DISPO);
            types.add(new LongType());
            types.add(new LongType());
            types.add(new LongType());
        }
        sql.append(" order by ad.VORGABE_SCV asc, a.KUNDE__NO asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
            LOGGER.debug("params: " + params.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<ProjektierungsView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                ProjektierungsView view = new ProjektierungsView();
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setObserveProcess(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVerlaufAbtId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufAbtStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBearbeiter(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBuendelNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVorgabeAm(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBearbeiterAm(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setNiederlassungId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAmResponsibility(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVerlaufNotPossible(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPreventCPSProvOrder(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setProjectResponsibleId(ObjectTools.getLongSilent(values, columnIndex++));
                setBaHinweise(values, view, columnIndex++);
                view.setHasSubOrders(ObjectTools.getLongSilent(values, columnIndex++) > 0);
                view.setErledigt((ObjectTools.getIntegerSilent(values, columnIndex++) <= 0) ? Boolean.TRUE : Boolean.FALSE);
                view.setGeraeteBez(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<AbstractBauauftragView> findBAVerlaufViews4KundeInShortTerm(Long kundeNo,
            java.util.Date minDate, java.util.Date maxDate) {
        StringBuilder sql = new StringBuilder("select a.KUNDE__NO, a.ID as AUFTRAG_ID, t.TDN, ");
        sql.append("p.ANSCHLUSSART, v.REALISIERUNGSTERMIN, va.NAME as ANLASS, ");
        sql.append("v.ID as VERLAUF_ID, s.STATUS_TEXT, vs.VERLAUF_STATUS ");
        sql.append("from T_AUFTRAG a ");
        sql.append("inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append("inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append("inner join T_VERLAUF v on a.ID=v.AUFTRAG_ID ");
        sql.append("inner join T_BA_VERL_ANLASS va on v.ANLASS=va.ID ");
        sql.append("inner join T_AUFTRAG_STATUS s on ad.STATUS_ID=s.ID ");
        sql.append("inner join T_VERLAUF_STATUS vs on v.VERLAUF_STATUS_ID=vs.ID ");
        sql.append("left join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append("left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append("where a.KUNDE__NO=? and ad.STATUS_ID>=? and ad.STATUS_ID<? ");
        sql.append("and (v.PROJEKTIERUNG=? or v.PROJEKTIERUNG is null) and v.VERLAUF_STATUS_ID not in (?,?) ");
        sql.append("and v.REALISIERUNGSTERMIN between ? and ? ");
        sql.append("and ad.GUELTIG_BIS=? and (at.GUELTIG_BIS is null or at.GUELTIG_BIS=?)");

        java.util.Date endDate = DateTools.getHurricanEndDate();
        Object[] params = new Object[] { kundeNo, AuftragStatus.TECHNISCHE_REALISIERUNG,
                AuftragStatus.AUFTRAG_GEKUENDIGT, Boolean.FALSE, VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT,
                VerlaufStatus.VERLAUF_STORNIERT, minDate, maxDate, endDate, endDate };

        Type[] types = new Type[] { new LongType(), new LongType(), new LongType(),
                new BooleanType(), new LongType(), new LongType(),
                new DateType(), new DateType(), new DateType(), new DateType()};

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params, types);
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AbstractBauauftragView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AbstractBauauftragView view = new AbstractBauauftragView();
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVerlaufStatus(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }

            return retVal;
        }
        return Collections.emptyList();
    }

    /**
     * Gibt zurück, ob nur aktive Verläufe angezeigt werden sollen oder nicht
     */
    private boolean onlyActiveVerlaeufe(java.util.Date realisierungFrom, java.util.Date realisierungTo) {
        return (realisierungFrom == null) && (realisierungTo == null);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
