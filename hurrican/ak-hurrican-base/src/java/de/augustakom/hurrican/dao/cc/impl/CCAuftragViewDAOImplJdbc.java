/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2004 08:06:03
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.sql.Date;
import java.util.*;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ObjectType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungQuery;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;


/**
 * JDBC DAO-Implementierung von <code>CCAuftragViewDAO</code>
 *
 *
 */
public class CCAuftragViewDAOImplJdbc extends Hibernate4FindDAOImpl implements CCAuftragViewDAO {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragViewDAOImplJdbc.class);
    private static final int STRATEGY_BY_AUFTRAG_NO_ORIG = 0;
    private static final int STRATEGY_BY_BUENDEL_NO = 1;
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<AuftragDatenView> findAuftragDatenViews(AuftragDatenQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        boolean query4Account = StringUtils.isNotBlank(query.getIntAccount());

        StringBuilder sql = new StringBuilder();
        sql.append("select a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.PRODAK_ORDER__NO, ad.BESTELLNR, ");
        sql.append("ad.INBETRIEBNAHME, ad.KUENDIGUNG, ad.LBZ_KUNDE, ad.STATUS_ID, ");
        sql.append("t.TDN, p.PROD_ID, p.ANSCHLUSSART, p.PROD_NAME_PATTERN, ast.STATUS_TEXT, at.PROJECT_RESPONSIBLE ");
        if (query4Account) {
            sql.append(", acc.ACCOUNT ");
        }
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on a.ID = at.AUFTRAG_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID = t.ID ");
        sql.append(" left join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID = ast.ID ");
        if (query4Account) {
            sql.append(" left join T_INT_ACCOUNT acc on at.INT_ACCOUNT_ID=acc.ID ");
        }
        sql.append(" where at.GUELTIG_VON<=? and at.GUELTIG_BIS>? and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        if (query4Account) {
            sql.append(" and acc.GUELTIG_VON<=? and acc.GUELTIG_BIS>? ");
            CollectionUtils.addAll(params, new Object[] { now, now });
            types.add(new DateType());
            types.add(new DateType());
        }

        if (query.getKundeNo() != null) {
            sql.append(" and a.KUNDE__NO=? ");
            params.add(query.getKundeNo());
            types.add(new LongType());
        }
        if (query.getAuftragId() != null) {
            sql.append(" and a.ID=?");
            params.add(query.getAuftragId());
            types.add(new LongType());
        }
        if (query.getAuftragNoOrig() != null) {
            sql.append(" and ad.PRODAK_ORDER__NO=?");
            params.add(query.getAuftragNoOrig());
            types.add(new LongType());
        }
        if (query.getAuftragStatusId() != null) {
            sql.append(" and ad.STATUS_ID=? and rownum <= 5000");
            params.add(query.getAuftragStatusId());
            types.add(new LongType());
        }
        if (StringUtils.isNotBlank(query.getBestellNr())) {
            if (WildcardTools.containsWildcard(query.getBestellNr())) {
                sql.append(" and lower(ad.BESTELLNR) like ?");
                params.add(WildcardTools.replaceWildcards(query.getBestellNr().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(ad.BESTELLNR) = ?");
                params.add(query.getBestellNr().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getLbzKunde())) {
            if (WildcardTools.containsWildcard(query.getLbzKunde())) {
                sql.append(" and lower(ad.LBZ_KUNDE) like ?");
                params.add(WildcardTools.replaceWildcards(query.getLbzKunde().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(ad.LBZ_KUNDE) = ?");
                params.add(query.getLbzKunde().toLowerCase());
                types.add(new StringType());
            }
        }
        if (query.getAuftragStatusMin() != null) {
            sql.append(" and ad.STATUS_ID>=?");
            params.add(query.getAuftragStatusMin());
            types.add(new LongType());
        }
        if (query.getAuftragStatusMax() != null) {
            sql.append(" and ad.STATUS_ID<?");
            params.add(query.getAuftragStatusMax());
            types.add(new LongType());
        }
        if (query.getProdId() != null) {
            sql.append(" and p.PROD_ID = ?");
            params.add(query.getProdId());
            types.add(new LongType());
        }
        if (query.getIsVierDraht() != null) {
            sql.append(" and p.VIER_DRAHT = ?");
            params.add(query.getIsVierDraht());
            types.add(new BooleanType());
        }
        if (query.getProduktGruppeId() != null) {
            sql.append(" and p.PRODUKTGRUPPE_ID=? ");
            params.add(query.getProduktGruppeId());
            types.add(new LongType());
        }
        if (query4Account) {
            sql.append(" and acc.ACCOUNT=? ");
            params.add(query.getIntAccount());
            types.add(new StringType());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragDatenView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragDatenView view = new AuftragDatenView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBestellNr(ObjectTools.getStringSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setLbzKunde(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdNamePattern(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProjectResponsibleUserId(ObjectTools.getLongSilent(values, columnIndex++));
                if (query4Account) {
                    view.setIntAccount(ObjectTools.getStringSilent(values, columnIndex++));
                }

                retVal.add(view);
            }

            return retVal;
        }

        return Collections.<AuftragDatenView>emptyList();
    }

    @Override
    public List<AuftragRealisierungView> findRealisierungViews(AuftragRealisierungQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        params.add(Endstelle.ENDSTELLEN_TYP_A);
        types.add(new StringType());
        params.add(Endstelle.ENDSTELLEN_TYP_B);
        types.add(new StringType());
        params.add(VerlaufStatus.VERLAUF_STORNIERT);
        types.add(new LongType());
        params.add(VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
        types.add(new LongType());
        params.add(0);
        types.add(new IntegerType());

        StringBuilder sql = new StringBuilder(
                "select a.ID, a.KUNDE__NO, ad.STATUS_ID, ad.VORGABE_SCV, ad.PRODAK_ORDER__NO, ");
        sql.append("ad.VORGABE_KUNDE, ad.INBETRIEBNAHME, ad.KUENDIGUNG, ad.BEARBEITER, ast.STATUS_TEXT,  ");
        sql.append("p.PROD_ID, p.ANSCHLUSSART, p.PROD_NAME_PATTERN, t.TDN, v.REALISIERUNGSTERMIN, ");
        sql.append("esa.ENDSTELLE as ES_A_ENDSTELLE, esa.ORT as ES_A_ORT, ");
        sql.append("esb.ENDSTELLE as ES_B_ENDSTELLE, esb.ORT as ES_B_ORT ");
        sql.append("from T_AUFTRAG a ");
        sql.append("inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append("inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append("inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append("inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID ");
        sql.append("inner join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append("left join T_ENDSTELLE esa on at.AT_2_ES_ID=esa.ES_GRUPPE ");
        sql.append("left join T_ENDSTELLE esb on at.AT_2_ES_ID=esb.ES_GRUPPE ");
        sql.append("left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append("left join T_VERLAUF_SUB_ORDERS vso on vso.AUFTRAG_ID=a.ID ");
        sql.append("left join T_VERLAUF v on v.AUFTRAG_ID=a.ID or v.ID=vso.VERLAUF_ID ");
        sql.append("where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append("and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append("and ((esa.ES_TYP=? and esb.ES_TYP=?) OR (esa.ES_TYP is null and esb.ES_TYP is null)) ");
        sql.append("and (v.VERLAUF_STATUS_ID is null or v.VERLAUF_STATUS_ID not in (?,?)) ");
        sql.append("and (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=?) ");
        sql.append("and ((0=1) ");
        if (BooleanTools.nullToFalse(query.getInbetriebnahme())) {
            sql.append("or ((ad.INBETRIEBNAHME >= ? and ad.INBETRIEBNAHME <= ?) ");
            sql.append("or (ad.INBETRIEBNAHME is null and (ad.VORGABE_SCV >= ? and ad.VORGABE_SCV <= ?))) ");
            CollectionUtils.addAll(params, new Object[] { query.getInbetriebnahmeFrom(), query.getInbetriebnahmeTo(),
                    query.getInbetriebnahmeFrom(), query.getInbetriebnahmeTo() });
            types.add(new DateType());
            types.add(new DateType());
            types.add(new DateType());
            types.add(new DateType());
        }
        if (BooleanTools.nullToFalse(query.getKuendigung())) {
            sql.append("or (ad.KUENDIGUNG >= ? and ad.KUENDIGUNG <= ?)");
            CollectionUtils.addAll(params, new Object[] { query.getInbetriebnahmeFrom(), query.getInbetriebnahmeTo() });
            types.add(new DateType());
            types.add(new DateType());
        }
        if (BooleanTools.nullToFalse(query.getRealisierung())) {
            sql.append("or (v.REALISIERUNGSTERMIN >= ? and v.REALISIERUNGSTERMIN <= ?) ");
            CollectionUtils.addAll(params, new Object[] { query.getInbetriebnahmeFrom(), query.getInbetriebnahmeTo() });
            types.add(new DateType());
            types.add(new DateType());
        }
        sql.append(") ");
        if (query.getKundeNo() != null) {
            sql.append("and a.KUNDE__NO=? ");
            params.add(query.getKundeNo());
            types.add(new LongType());
        }
        if (query.getProduktGruppeId() != null) {
            sql.append("and pg.ID=? ");
            params.add(query.getProduktGruppeId());
            types.add(new LongType());
        }
        if (query.getNiederlassungId() != null) {
            sql.append("and at.NIEDERLASSUNG_ID=? ");
            params.add(query.getNiederlassungId());
            types.add(new LongType());
        }
        sql.append("order by ad.INBETRIEBNAHME asc, a.KUNDE__NO asc, a.ID asc, ");
        sql.append("ad.BUENDEL_NR asc, ad.BUENDEL_NR_HERKUNFT asc");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragRealisierungView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragRealisierungView view = new AuftragRealisierungView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVorgabeSCV(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVorgabeKunde(ObjectTools.getDateSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBearbeiter(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setEsA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsAOrt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsBOrt(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.<AuftragRealisierungView>emptyList();
    }

    @Override
    public List<AuftragCarrierView> findAuftragCarrierViews(AuftragCarrierQuery query) {
        Map<String, Object> params = Maps.newHashMap();
        Date now = DateTools.getActualSQLDate();
        params.put("now", now);

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select c.LBZ, c.VTRNR, a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.BESTELLNR, ad.PRODAK_ORDER__NO, ");
        baseSql.append(" ad.LBZ_KUNDE, t.TDN, e.ES_TYP, ast.STATUS_TEXT, p.ANSCHLUSSART, l.LEITUNGSNUMMER as OTHER_LBZ, ");
        baseSql.append(" cbv.CARRIER_REF_NR ");
        baseSql.append(" from T_AUFTRAG a ");
        baseSql.append(" inner join T_AUFTRAG_TECHNIK at on a.ID = at.AUFTRAG_ID ");
        baseSql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        baseSql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID = ast.id ");
        baseSql.append(" inner join T_ENDSTELLE e on e.ES_GRUPPE=AT.AT_2_ES_ID ");
        baseSql.append(" left join T_CARRIERBESTELLUNG c on c.CB_2_ES_ID = e.CB_2_ES_ID ");
        baseSql.append(" left join T_CB_VORGANG cbv on %s "); // Join Bedingung wird als String-Parameter gesetzt!
        baseSql.append(" left join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        baseSql.append(" left join T_TDN t ON at.TDN_ID = t.ID ");
        baseSql.append(" left join T_LEITUNGSNUMMER l ON a.ID = l.AUFTRAG_ID ");
        baseSql.append(" where ad.GUELTIG_VON<=:now and ad.GUELTIG_BIS>:now ");
        baseSql.append(" and at.GUELTIG_VON<=:now and at.GUELTIG_BIS>:now ");

        if (StringUtils.isNotBlank(query.getLbz())) {
            if (WildcardTools.containsWildcard(query.getLbz())) {
                baseSql.append(" and (lower(c.LBZ) like :lbz or (lower(l.LEITUNGSNUMMER) like :lbz and E.ES_TYP=:typ)) ");
                String lbz = WildcardTools.replaceWildcards(query.getLbz().toLowerCase());
                params.put("lbz", lbz);
                params.put("typ", Endstelle.ENDSTELLEN_TYP_B);
            }
            else {
                baseSql.append(" and (lower(c.LBZ) = :lbz or (lower(l.LEITUNGSNUMMER) = :lbz and E.ES_TYP=:typ)) ");
                String lbz = query.getLbz().toLowerCase();
                params.put("lbz", lbz);
                params.put("typ", Endstelle.ENDSTELLEN_TYP_B);
            }
        }
        if (StringUtils.isNotBlank(query.getVtrNr())) {
            if (WildcardTools.containsWildcard(query.getVtrNr())) {
                baseSql.append(" and lower(c.VTRNR) like :vtrnr ");
                params.put("vtrnr", WildcardTools.replaceWildcards(query.getVtrNr().toLowerCase()));
            }
            else {
                baseSql.append(" and lower(c.VTRNR) = :vtrnr ");
                params.put("vtrnr", query.getVtrNr().toLowerCase());
            }
        }
        if (StringUtils.isNotBlank(query.getCarrierRefNr())) {
            if (WildcardTools.containsWildcard(query.getCarrierRefNr())) {
                baseSql.append(" and cbv.CARRIER_REF_NR like :carrier ");
                params.put("carrier", WildcardTools.replaceWildcards(query.getCarrierRefNr()));
            }
            else {
                baseSql.append(" and cbv.CARRIER_REF_NR = :carrier ");
                params.put("carrier", query.getCarrierRefNr());
            }
        }

        // zwei SQL-Statements, die sich lediglich im Join auf den T_CB_VORGANG unterscheiden.
        // die beiden SQLs werden per union verbunden; dies ist bedeutend schneller, als wenn die beiden
        // Bedingungen im Join per OR verbunden werden!
        String firstSql = String.format(baseSql.toString(), " cbv.CB_ID = c.CB_ID ");
        // folgende Bedingung ist fuer REX-MK Vorgaenge notwendig!
        String secondSql = String.format(baseSql.toString(), " cbv.AUFTRAG_ID = a.ID and cbv.CB_ID IS NULL and e.ES_TYP = 'B' ");

        String sql = StringUtils.join(new String[] { firstSql, secondSql }, " union ");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql);
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            sqlQuery.setParameter(param.getKey(), param.getValue());
        }

        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragCarrierView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragCarrierView view = new AuftragCarrierView();
                view.setLbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVtrNr(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBestellNr(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setLbzKunde(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEsTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setOtherLbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCarrierRefNr((ObjectTools.getStringSilent(values, columnIndex++)));

                retVal.add(view);
            }

            return retVal;
        }

        return Collections.<AuftragCarrierView>emptyList();
    }

    @Override
    public List<WbciRequestCarrierView> findWbciRequestCarrierViews(String vorabstimmungsId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select gf.VORABSTIMMUNGSID, req.AENDERUNGS_ID, gf.TYP as GESCHAEFTSFALLTYP, ");
        sql.append(" gf.ABGEBENDEREKP, gf.AUFNEHMENDEREKP, gf.KUNDENWUNSCHTERMIN, gf.STATUS, ");
        sql.append(" gf.AUFTRAG_ID, gf.BILLING_ORDER__NO ");
        sql.append(" from T_WBCI_REQUEST req ");
        sql.append(" inner join T_WBCI_GESCHAEFTSFALL gf on req.GESCHAEFTSFALL_ID=gf.ID ");
        sql.append(" where (lower(gf.VORABSTIMMUNGSID) like :vaId or lower(req.AENDERUNGS_ID) like :vaId)");
        sql.append(" order by gf.VORABSTIMMUNGSID desc");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setString("vaId", WildcardTools.replaceWildcards(vorabstimmungsId.toLowerCase()));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<WbciRequestCarrierView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                WbciRequestCarrierView view = new WbciRequestCarrierView();
                view.setVorabstimmungsId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAenderungsId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setGeschaeftsfallTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAbgebenderEkp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAufnehmenderEkp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundenwunschtermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setStatus(getWbciGeschaeftsfallStatusSilent(ObjectTools.getStringSilent(values, columnIndex++)));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBillingOrderNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                retVal.add(view);
            }

            return retVal;
        }

        return Collections.<WbciRequestCarrierView>emptyList();  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Checks to see if a Status is set for the geschaeftsfall. If no status is set, null is returned. Otherwise the
     * status is mapped to a user-friendly format for displaying in the GUI.
     * @param status
     * @return
     */
    private String getWbciGeschaeftsfallStatusSilent(String status) {
        if (status != null) {
            return WbciGeschaeftsfallStatus.valueOf(status).getDescription();
        }
        return null;
    }

    @Override
    public List<AuftragEndstelleView> findAuftragEndstelleViews(AuftragEndstelleQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now, DateTools.getHurricanEndDate(), now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select e.ID as ES_ID, e.ENDSTELLE, e.NAME as ES_NAME, e.GEO_ID, e.ORT, e.ES_TYP, e.RANGIER_ID, ");
        sql.append(" a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.PROD_ID, ad.VORGABE_SCV, ad.INBETRIEBNAHME, ");
        sql.append(" ad.KUENDIGUNG, ad.PRODAK_ORDER__NO, ad.STATUS_ID, t.TDN, ast.STATUS_TEXT, p.ANSCHLUSSART, ad.GUELTIG_VON, ad.GUELTIG_BIS, ");
        sql.append(" dp.NAME as PROFILE_NAME, lart.NAME as LTG_ART_NAME, at.PROJECT_RESPONSIBLE AS PROJECT_RESPONSIBLE ");
        sql.append(" from T_ENDSTELLE e ");
        sql.append(" inner join T_AUFTRAG_TECHNIK_2_ENDSTELLE at2e on e.ES_GRUPPE = at2e.ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at2e.ID = at.AT_2_ES_ID ");
        sql.append(" inner join T_AUFTRAG a on at.AUFTRAG_ID = a.ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        sql.append(" left join T_AUFTRAG_STATUS ast on ad.STATUS_ID = ast.id ");
        sql.append(" left join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        sql.append(" left join T_TDN t ON at.TDN_ID = t.ID ");
        sql.append(" left join T_AUFTRAG_2_DSLAMPROFILE a2dp on a.ID=a2dp.AUFTRAG_ID ");
        sql.append(" left join T_DSLAM_PROFILE dp on a2dp.DSLAM_PROFILE_ID=dp.ID ");
        sql.append(" left join T_ES_LTG_DATEN eltg on e.ID = eltg.ES_ID ");
        sql.append(" left join T_LEITUNGSART lart on eltg.LEITUNGSART = lart.ID ");
        sql.append(" where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and (a2dp.GUELTIG_BIS is null or a2dp.GUELTIG_BIS=?) ");
        sql.append(" and (eltg.GUELTIG_BIS is null or eltg.GUELTIG_BIS>?) ");
        // Query-Parameter
        if (query.getEndstelleId() != null) {
            // bei Suche ueber ES_ID nur Auftraege, die nicht konsolidiert sind!
            sql.append(" and e.ID=? and ad.STATUS_ID not in (?) ");
            params.add(query.getEndstelleId());
            types.add(new LongType());
            params.add(AuftragStatus.KONSOLIDIERT);
            types.add(new LongType());
        }
        if (StringUtils.isNotBlank(query.getEndstelle())) {
            if (WildcardTools.containsWildcard(query.getEndstelle())) {
                sql.append(" and lower(e.ENDSTELLE) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEndstelle().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(e.ENDSTELLE) = ? ");
                params.add(query.getEndstelle().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getEndstelleOrt())) {
            if (WildcardTools.containsWildcard(query.getEndstelleOrt())) {
                sql.append(" and lower(e.ORT) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEndstelleOrt().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(e.ORT) = ? ");
                params.add(query.getEndstelleOrt().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getVbz())) {
            if (WildcardTools.containsWildcard(query.getVbz())) {
                sql.append(" and lower(t.TDN) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getVbz().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(t.TDN) = ? ");
                params.add(query.getVbz().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getLtgArt())) {
            if (WildcardTools.containsWildcard((query.getLtgArt()))) {
                sql.append(" and lower(lart.NAME) like ? ");
                params.add(WildcardTools.replaceWildcards((query.getLtgArt().toLowerCase())));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(lart.NAME) = ? ");
                params.add(query.getLtgArt().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getEndstelleTyp())) {
            sql.append(" and lower(e.ES_TYP) = ? ");
            params.add(query.getEndstelleTyp().toLowerCase());
            types.add(new StringType());
        }
        if (query.getRangierId() != null) {
            sql.append(" and e.RANGIER_ID = ?");
            params.add(query.getRangierId());
            types.add(new LongType());
        }
        if (CollectionUtils.isNotEmpty(query.getKundeNos())) {
            sql.append(" and a.KUNDE__NO in (");
            int x = 0;
            for (Long kNo : query.getKundeNos()) {
                if (x > 0) {
                    sql.append(",").append(kNo);
                }
                else {
                    sql.append(kNo);
                }
                x++;
            }
            sql.append(") ");
        }
        // GroupBy-Kriterium
        sql.append(" group by e.ID, e.ENDSTELLE, e.NAME, e.GEO_ID, e.ORT, e.ES_TYP, e.RANGIER_ID, ");
        sql.append(" a.ID, a.KUNDE__NO, ad.PROD_ID, ad.VORGABE_SCV, ad.INBETRIEBNAHME,  ad.KUENDIGUNG, ");
        sql.append(" ad.PRODAK_ORDER__NO, ad.STATUS_ID, t.TDN, ast.STATUS_TEXT,  p.ANSCHLUSSART, ad.GUELTIG_VON, ");
        sql.append(" ad.GUELTIG_BIS, dp.NAME, lart.NAME, at.PROJECT_RESPONSIBLE ");
        sql.append(" order by AUFTRAG_ID");
        // Order-Kriterium
        if (query.isOrderByKundeNo()) {
            sql.append(", a.KUNDE__NO");
        }
        else if (query.isOrderByAuftragNoOrig()) {
            sql.append(", ad.PRODAK_ORDER__NO");
        }
        else if (query.isOrderByAuftragId()) {
            sql.append(", a.ID");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                AuftragEndstelleView view = new AuftragEndstelleViewMapper().mapView(values);

                view.setDslamProfile(ObjectTools.getStringSilent(values, 20));
                view.setLtgArt(ObjectTools.getStringSilent(values, 21));
                view.setProjectResponsibleUserId(ObjectTools.getLongSilent(values, 22));

                views.add(view);
            }
        }
        return views;
    }

    @Override
    public List<AuftragEndstelleView> findAuftragEndstelleViews4VPN(Long vpnId, List<Long> kNos) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        params.add(Endstelle.ENDSTELLEN_TYP_B);
        types.add(new StringType());

        StringBuilder sql = new StringBuilder();
        sql.append("select e.ID as ES_ID, e.ENDSTELLE, e.NAME as ES_NAME, e.GEO_ID, e.ORT, e.ES_TYP, e.RANGIER_ID, ");
        sql.append("a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.PROD_ID, ad.VORGABE_SCV, ad.INBETRIEBNAHME, ");
        sql.append("ad.KUENDIGUNG, ad.PRODAK_ORDER__NO, ad.STATUS_ID, t.TDN, ast.STATUS_TEXT, ");
        sql.append("p.ANSCHLUSSART, ad.GUELTIG_VON, ad.GUELTIG_BIS ");
        sql.append("from T_AUFTRAG a ");
        sql.append("inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append("inner join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append("inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append("inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append("left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append("left join T_ENDSTELLE e on at.AT_2_ES_ID=e.ES_GRUPPE ");
        sql.append("where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append("and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append("and (e.ES_TYP is null or e.ES_TYP=?) ");
        if (vpnId != null) {
            sql.append("and at.VPN_ID=? ");
            params.add(vpnId);
            types.add(new LongType());
        }
        else {
            sql.append("and at.VPN_ID is null ");
        }

        if ((kNos != null) && (!kNos.isEmpty())) {
            sql.append(" and a.KUNDE__NO in (");
            int x = 0;
            for (Long kNo : kNos) {
                if (x > 0) {
                    sql.append(",").append(kNo);
                }
                else {
                    sql.append(kNo);
                }
                x++;
            }
            sql.append(") ");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                views.add(new AuftragEndstelleViewMapper().mapView(values));
            }
        }
        return views;
    }

    @Override
    public List<AuftragEquipmentView> findAuftragEquipmentViews(AuftragEquipmentQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        params.add(now);
        types.add(new DateType());
        params.add(now);
        types.add(new DateType());
        params.add(Endstelle.ENDSTELLEN_TYP_B);
        types.add(new StringType());
        params.add(Endstelle.ENDSTELLEN_TYP_A);
        types.add(new StringType());
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(DateTools.getHurricanEndDate());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder("select eq.EQ_ID, eq.HVT_ID_STANDORT, hg.ORTSTEIL, ");
        sql.append("sw.NAME, eq.HW_EQN, eq.RANG_BUCHT, eq.RANG_LEISTE1, eq.RANG_STIFT1, r.RANGIER_ID, ");
        sql.append("e.ID as ENDSTELLE_ID, e.ENDSTELLE, e.NAME as ES_NAME, e.ES_TYP, t.TDN, a.ID as AUFTRAG_ID, a.KUNDE__NO, ");
        sql.append("ast.STATUS_TEXT, ad.STATUS_ID, ad.INBETRIEBNAHME, ad.KUENDIGUNG, p.ANSCHLUSSART, ");
        sql.append("dp.name as PROFILE_NAME, rack.GERAETEBEZ, rack.MANAGEMENTBEZ, vpn.VPN_NR ");
        sql.append("from T_EQUIPMENT eq ");
        sql.append("left join T_RANGIERUNG r on (eq.EQ_ID=r.EQ_IN_ID or eq.EQ_ID=r.EQ_OUT_ID) and ");
        sql.append("  ((r.GUELTIG_VON is null or r.GUELTIG_VON<=?) and (r.GUELTIG_BIS is null or r.GUELTIG_BIS>?)) ");
        sql.append("left join T_ENDSTELLE e on (e.RANGIER_ID=r.RANGIER_ID or e.RANGIER_ID_ADDITIONAL=r.RANGIER_ID) ");
        sql.append("left join T_AUFTRAG_TECHNIK_2_ENDSTELLE a2e on e.ES_GRUPPE=a2e.ID ");
        sql.append("left join T_AUFTRAG_TECHNIK at on a2e.ID=at.AT_2_ES_ID ");
        sql.append("left join T_HW_SWITCH sw on (eq.SWITCH=sw.ID or at.HW_SWITCH=sw.ID) ");
        sql.append("left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append("left join T_AUFTRAG a on at.AUFTRAG_ID=a.ID ");
        sql.append("left join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append("left join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append("left join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append("left join T_HVT_STANDORT hs on hs.HVT_ID_STANDORT=eq.HVT_ID_STANDORT ");
        sql.append("left join T_HVT_GRUPPE hg on hg.HVT_GRUPPE_ID=hs.HVT_GRUPPE_ID ");
        sql.append("left join T_AUFTRAG_2_DSLAMPROFILE a2dp on a.ID=a2dp.AUFTRAG_ID ");
        sql.append("left join T_DSLAM_PROFILE dp on a2dp.DSLAM_PROFILE_ID=dp.ID ");
        sql.append("left join T_HW_BAUGRUPPE bg on eq.HW_BAUGRUPPEN_ID=bg.ID ");
        sql.append("left join T_HW_SUBRACK sr on bg.SUBRACK_ID=sr.ID ");
        sql.append("left join T_HW_RACK rack on bg.RACK_ID=rack.ID or sr.RACK_ID=rack.id ");
        sql.append("left join T_VPN vpn on at.VPN_ID=vpn.VPN_ID ");
        sql.append("where (e.ES_TYP is null or e.ES_TYP=? or e.ES_TYP=?) ");
        sql.append("and ((at.GUELTIG_VON is null or at.GUELTIG_VON<=?) and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?)) ");
        sql.append("and ((ad.GUELTIG_VON is null or ad.GUELTIG_VON<=?) and (ad.GUELTIG_BIS is null or ad.GUELTIG_BIS>?)) ");
        sql.append("and ((r.GUELTIG_VON is null or r.GUELTIG_VON<=?) and (r.GUELTIG_BIS is null or r.GUELTIG_BIS>?))");
        sql.append("and (a2dp.GUELTIG_BIS is null or a2dp.GUELTIG_BIS=?)");
        if (query.getHvtIdStandort() != null) {
            sql.append("and eq.HVT_ID_STANDORT=? ");
            params.add(query.getHvtIdStandort());
            types.add(new LongType());
        }
        if (StringUtils.isNotBlank(query.getEqBucht())) {
            sql.append("and eq.RANG_BUCHT=? ");
            params.add(query.getEqBucht());
            types.add(new StringType());
        }
        if (StringUtils.isNotBlank(query.getEqLeiste1())) {
            sql.append("and eq.RANG_LEISTE1=? ");
            params.add(query.getEqLeiste1());
            types.add(new StringType());
        }
        if (StringUtils.isNotBlank(query.getEqStift1())) {
            sql.append("and eq.RANG_STIFT1=? ");
            params.add(query.getEqStift1());
            types.add(new StringType());
        }
        if (StringUtils.isNotBlank(query.getEqSwitch())) {
            if (WildcardTools.containsWildcard(query.getEqSwitch())) {
                sql.append(" and lower(sw.NAME) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEqSwitch().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(sw.NAME) = ? ");
                params.add(query.getEqSwitch().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getEqHwEqn())) {
            if (WildcardTools.containsWildcard(query.getEqHwEqn())) {
                sql.append(" and lower(eq.HW_EQN) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEqHwEqn().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(eq.HW_EQN) = ? ");
                params.add(query.getEqHwEqn().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getEqGeraetebezeichnung())) {
            if (WildcardTools.containsWildcard(query.getEqGeraetebezeichnung())) {
                sql.append(" and lower(rack.GERAETEBEZ) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEqGeraetebezeichnung().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(rack.GERAETEBEZ) = ? ");
                params.add(query.getEqGeraetebezeichnung().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getEqMgmtbezeichnung())) {
            if (WildcardTools.containsWildcard(query.getEqMgmtbezeichnung())) {
                sql.append(" and lower(rack.MANAGEMENTBEZ) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getEqMgmtbezeichnung().toLowerCase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and lower(rack.MANAGEMENTBEZ) = ? ");
                params.add(query.getEqMgmtbezeichnung().toLowerCase());
                types.add(new StringType());
            }
        }
        if (query.isOnlyActive()) {
            sql.append("and ((ast.ID is null) or ast.ID>=? and ast.ID<?) ");
            params.add(AuftragStatus.TECHNISCHE_REALISIERUNG);
            types.add(new LongType());
            params.add(AuftragStatus.KUENDIGUNG);
            types.add(new LongType());
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("SQL: " + sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragEquipmentView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragEquipmentView view = new AuftragEquipmentView();
                view.setEquipmentId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, columnIndex++));
                view.setHvtOrtsteil(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEqSwitch(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEqHwEqn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEqBucht(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEqLeiste1(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEqStift1(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangierId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setKuendigung(ObjectTools.getObjectSilent(values, columnIndex++, Date.class));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setDslamProfile(ObjectTools.getStringSilent(values, columnIndex++));
                view.setGeraeteBezeichung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setMgmtBezeichnung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.<AuftragEquipmentView>emptyList();
    }

    /* Basis Select-Statements fuer die Abfrage von AuftragEndstellen-Views. */
    private StringBuilder getSelect4AESViews() {
        StringBuilder sql = new StringBuilder();
        sql.append("select e.ID as ES_ID, e.ENDSTELLE, e.NAME as ES_NAME, e.GEO_ID, e.ORT, e.ES_TYP, e.RANGIER_ID, ");
        sql.append(" a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.PROD_ID, ad.VORGABE_SCV, ad.INBETRIEBNAHME, ");
        sql.append(" ad.KUENDIGUNG, ad.PRODAK_ORDER__NO, ad.STATUS_ID, t.TDN, ast.STATUS_TEXT, ");
        sql.append(" p.ANSCHLUSSART, ad.GUELTIG_VON, ad.GUELTIG_BIS ");
        sql.append(" from t_auftrag_daten ad ");

        return sql;
    }

    @Override
    public List<AuftragEndstelleView> findAESViews4Uebernahme(Long geoId, List<Long> physikTypIds) {
        StringBuilder sql = getSelect4AESViews();
        sql.append(" inner join t_auftrag a on ad.auftrag_id=a.id ");
        sql.append(" inner join t_auftrag_status ast on ad.status_id=ast.id ");
        sql.append(" inner join t_auftrag_technik at on a.id=at.auftrag_id ");
        sql.append(" inner join t_tdn t on at.tdn_id=t.id ");
        sql.append(" inner join t_produkt p on ad.prod_id=p.prod_id ");
        sql.append(" inner join t_produkt_2_physiktyp p2p on p.prod_id=p2p.prod_id ");
        sql.append(" inner join t_endstelle e on e.es_gruppe = at.at_2_es_id ");
        sql.append(" inner join t_rangierung r on e.rangier_id=r.rangier_id ");
        sql.append(" where at.gueltig_von<=? and at.gueltig_bis>? ");
        sql.append(" and ad.gueltig_von<=? and ad.gueltig_bis>? ");
        sql.append(" and r.gueltig_von<=? and r.gueltig_bis>? ");
        sql.append(" and e.geo_id=? and p2p.physiktyp in (");
        for (int i = 0; i < physikTypIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(") and (r.es_id is null or r.es_id=?) and ad.status_id>? and ad.status_id<?");
        sql.append(" group by e.ID, e.ENDSTELLE, e.NAME, e.GEO_ID, e.ORT, ");
        sql.append(" e.ES_TYP, e.RANGIER_ID, a.ID, a.KUNDE__NO, ad.PROD_ID, ");
        sql.append(" ad.VORGABE_SCV, ad.INBETRIEBNAHME, ad.KUENDIGUNG, ad.PRODAK_ORDER__NO, ");
        sql.append(" ad.STATUS_ID, t.TDN, ast.STATUS_TEXT, p.ANSCHLUSSART, ad.GUELTIG_VON, ad.GUELTIG_BIS ");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        params.add(geoId);
        types.add(new LongType());
        for (Long pt : physikTypIds) {
            params.add(pt);
            types.add(new LongType());
        }
        params.add(Rangierung.RANGIERUNG_NOT_ACTIVE);
        types.add(new LongType());
        params.add(AuftragStatus.KUENDIGUNG);
        types.add(new LongType());
        params.add(AuftragStatus.KONSOLIDIERT);
        types.add(new LongType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                views.add(new AuftragEndstelleViewMapper().mapView(values));
            }
        }
        return views;
    }

    @Override
    public List<AuftragEndstelleView> findAESViews4KundeAndPG(Long kundeNo, Long produktGruppeId,
            boolean produktGruppeExclusive) {
        StringBuilder sql = getSelect4AESViews();
        sql.append(" inner join t_auftrag a on ad.auftrag_id=a.id ");
        sql.append(" inner join t_auftrag_status ast on ad.status_id=ast.id ");
        sql.append(" inner join t_auftrag_technik at on a.id=at.auftrag_id ");
        sql.append(" inner join t_tdn t on at.tdn_id=t.id ");
        sql.append(" inner join t_produkt p on ad.prod_id=p.prod_id ");
        sql.append(" inner join t_endstelle e on e.es_gruppe = at.at_2_es_id ");
        sql.append(" inner join t_rangierung r on e.rangier_id=r.rangier_id ");
        sql.append(" where at.gueltig_von<=? and at.gueltig_bis>? ");
        sql.append(" and ad.gueltig_von<=? and ad.gueltig_bis>? ");
        sql.append(" and r.gueltig_von<=? and r.gueltig_bis>? ");
        sql.append(" and (r.es_id is null or r.es_id=?) and ad.status_id>? and ad.status_id<?");
        sql.append(" and a.kunde__no=?");
        if (produktGruppeExclusive) {
            sql.append(" and p.produktgruppe_id<>? ");
        }
        else {
            sql.append(" and p.produktgruppe_id=? ");
        }

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        params.add(Rangierung.RANGIERUNG_NOT_ACTIVE);
        types.add(new LongType());
        params.add(AuftragStatus.KUENDIGUNG);
        types.add(new LongType());
        params.add(AuftragStatus.KONSOLIDIERT);
        types.add(new LongType());
        params.add(kundeNo);
        types.add(new LongType());
        params.add(produktGruppeId);
        types.add(new LongType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                views.add(new AuftragEndstelleViewMapper().mapView(values));
            }
        }
        return views;
    }

    @Override
    public List<AuftragEndstelleView> findAESViews4Wandel(Long geoId, Long kundeNo, Boolean analog2isdn) {
        StringBuilder sql = getSelect4AESViews();
        sql.append(" inner join t_auftrag a on ad.auftrag_id=a.id ");
        sql.append(" inner join t_auftrag_status ast on ad.status_id=ast.id ");
        sql.append(" inner join t_auftrag_technik at on a.id=at.auftrag_id ");
        sql.append(" inner join t_tdn t on at.tdn_id=t.id ");
        sql.append(" inner join t_produkt p on ad.prod_id=p.prod_id ");
        sql.append(" inner join t_endstelle e on e.es_gruppe = at.at_2_es_id ");
        sql.append(" inner join t_rangierung r on e.rangier_id=r.rangier_id ");
        sql.append(" where at.gueltig_von<=? and at.gueltig_bis>? ");
        sql.append(" and ad.gueltig_von<=? and ad.gueltig_bis>? ");
        sql.append(" and r.gueltig_von<=? and r.gueltig_bis>? ");
        sql.append(" and r.es_id is not null ");
        if (analog2isdn != null) {
            sql.append(" and r.physik_typ=? ");
        }
        sql.append(" and ((ad.status_id>? and ad.status_id<?) or ad.status_id=?) and e.geo_id=?");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        if (BooleanTools.nullToFalse(analog2isdn)) {
            params.add(PhysikTyp.PHYSIKTYP_AB);
            types.add(new LongType());
        }
        else if (analog2isdn != null) {
            params.add(PhysikTyp.PHYSIKTYP_UK0);
            types.add(new LongType());
        }
        params.add(AuftragStatus.KUENDIGUNG);
        types.add(new LongType());
        params.add(AuftragStatus.KONSOLIDIERT);
        types.add(new LongType());
        params.add(AuftragStatus.ABSAGE);
        types.add(new LongType());
        params.add(geoId);
        types.add(new LongType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                views.add(new AuftragEndstelleViewMapper().mapView(values));
            }
        }
        return views;
    }

    @Override
    public List<AuftragEndstelleView> findAESViews4TalPortAenderung(Long geoId, String esTyp) {
        StringBuilder sql = getSelect4AESViews();
        sql.append(" inner join T_AUFTRAG a on ad.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append(" inner join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_ENDSTELLE e on e.ES_GRUPPE=at.AT_2_ES_ID ");
        sql.append(" inner join T_CARRIERBESTELLUNG cb on e.CB_2_ES_ID=cb.CB_2_ES_ID ");
        sql.append(" where at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and e.GEO_ID=? and e.ES_TYP=? and ");
        sql.append(" ad.STATUS_ID>=? and ad.STATUS_ID<?");
        sql.append(" and cb.CARRIER_ID=? and cb.LBZ is not null");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        params.add(geoId);
        types.add(new LongType());
        params.add(esTyp);
        types.add(new StringType());
        params.add(AuftragStatus.KUENDIGUNG);
        types.add(new LongType());
        params.add(AuftragStatus.KONSOLIDIERT);
        types.add(new LongType());
        params.add(Carrier.ID_DTAG);
        types.add(new LongType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL: " + sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragEndstelleView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                views.add(new AuftragEndstelleViewMapper().mapView(values));
            }
        }
        return views;
    }

    @Override
    public List<CCAuftragProduktVbzView> findAuftragProduktVbzViews(CCAuftragProduktVbzQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select a.ID, ad.PRODAK_ORDER__NO, ad.STATUS_ID, ast.STATUS_TEXT, p.ANSCHLUSSART, t.TDN ");
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append(" inner join T_PRODUKT p on p.PROD_ID=ad.PROD_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append(" inner join T_TDN t on t.ID=at.TDN_ID ");
        sql.append(" where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        if (query.getKundeNo() != null) {
            sql.append(" and a.KUNDE__NO=? ");
            params.add(query.getKundeNo());
            types.add(new LongType());
        }
        if (query.getNurAuftraege4VPN() != null) {
            sql.append(" and p.VPN_PHYSIK=? ");
            params.add(query.getNurAuftraege4VPN());
            types.add(new BooleanType());
        }
        if (query.getProduktId() != null) {
            sql.append(" and ad.PROD_ID=? ");
            params.add(query.getProduktId());
            types.add(new LongType());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CCAuftragProduktVbzView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                CCAuftragProduktVbzView view = new CCAuftragProduktVbzView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return Collections.<CCAuftragProduktVbzView>emptyList();
    }

    @Override
    public List<AuftragIntAccountView> findAuftragAccountViews(AuftragIntAccountQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("select acc.ID as ACC_ID, acc.LI_NR, acc.ACCOUNT, acc.PASSWORT, acc.RUFNUMMER, ");
        sql.append(" a.ID as AUFTRAG_ID, ad.PRODAK_ORDER__NO, ad.PROD_ID, at.VPN_ID, p.ANSCHLUSSART, t.TDN ");
        sql.append(" from T_INT_ACCOUNT acc ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on acc.ID=at.INT_ACCOUNT_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" inner join T_AUFTRAG a on at.AUFTRAG_ID=a.ID ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" where acc.GUELTIG_VON<=? and acc.GUELTIG_BIS>? ");
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and p.PRODUKTGRUPPE_ID=? and a.KUNDE__NO=? and acc.LI_NR=? ");
        sql.append(" and ad.STATUS_ID>=?");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Date now = DateTools.getActualSQLDate();

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] {now, now, now, now, now, now,
                        query.getProduktGruppeId(), query.getKundeNo(), query.getIntAccountTyp(), AuftragStatus.KUENDIGUNG},
                new Type[] {new DateType(), new DateType(), new DateType(), new DateType(), new DateType(), new DateType(),
                        new LongType(), new LongType(), new IntegerType(), new LongType()});
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragIntAccountView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragIntAccountView view = new AuftragIntAccountView();
                view.setAccountId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAccountLiNr(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setAccount(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAccountPasswort(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAccountRufnummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVpnId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.<AuftragIntAccountView>emptyList();
    }

    @Override
    public List<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrig(Long auftragNoOrig) {
        return findAuftragIdAndTDN(new Object[] { auftragNoOrig }, STRATEGY_BY_AUFTRAG_NO_ORIG);
    }

    @Override
    public List<CCAuftragIDsView> findAuftragIdAndVbz4BuendelNr(Integer buendelNr, String buendelNrHerkunft) {
        return findAuftragIdAndTDN(new Object[] { buendelNr, buendelNrHerkunft }, STRATEGY_BY_BUENDEL_NO);
    }

    /* Sucht nach Auftrag-IDs und der VerbindungsBezeichnung. Die Suchstrategie kann differieren. */
    private List<CCAuftragIDsView> findAuftragIdAndTDN(Object[] searchParams, int strategy) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, searchParams);
        for (Object searchParam : searchParams) {
            if (searchParam instanceof String) {
                types.add(new StringType());
            } else if (searchParam instanceof Boolean) {
                types.add(new BooleanType());
            } else if (searchParam instanceof Integer) {
                types.add(new IntegerType());
            } else if (searchParam instanceof Long) {
                types.add(new LongType());
            } else if (searchParam instanceof Date) {
                types.add(new DateType());
            } else {
                types.add(new ObjectType());
            }
        }
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select ad.AUFTRAG_ID, ad.ID as AD_ID, ad.STATUS_ID, ad.PRODAK_ORDER__NO, at.ID as AT_ID, t.TDN, ");
        sql.append(" ast.STATUS_TEXT, p.ANSCHLUSSART from T_AUFTRAG_DATEN ad ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on ad.AUFTRAG_ID=at.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_PRODUKT p on ad.PROD_ID=p.PROD_ID");
        if (strategy == STRATEGY_BY_AUFTRAG_NO_ORIG) {
            sql.append(" where ad.PRODAK_ORDER__NO=? ");
        }
        else if (strategy == STRATEGY_BY_BUENDEL_NO) {
            sql.append(" where ad.BUENDEL_NR=? and ad.BUENDEL_NR_HERKUNFT=? ");
        }

        sql.append(" and (ad.GUELTIG_VON is null or ad.GUELTIG_VON<=?) and (ad.GUELTIG_BIS is null or ad.GUELTIG_BIS>?) ");
        sql.append(" and (at.GUELTIG_VON is null or at.GUELTIG_VON<=?) and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?) ");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CCAuftragIDsView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                CCAuftragIDsView view = new CCAuftragIDsView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragDatenId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragTechnikId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.<CCAuftragIDsView>emptyList();
    }

    @Override
    public List<CCKundeAuftragView> findAuftragViews4Kunde(Long kundeNo, boolean orderByBuendel,
            boolean excludeInvalid, boolean excludeKonsolidiert) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        params.add(kundeNo);
        types.add(new LongType());
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select a.ID, a.KUNDE__NO, ad.PRODAK_ORDER__NO, t.TDN, ad.INBETRIEBNAHME, ad.KUENDIGUNG, ");
        sql.append(" ad.BUENDEL_NR, p.PROD_ID, p.BRAUCHT_BUENDEL, p.ANSCHLUSSART, p.PROD_NAME_PATTERN, ");
        sql.append(" p.PRODUKTGRUPPE_ID, p.EXPORT_KDP_M, v.VPN_ID, v.VPN_NR, e1.ID as ES_ID_B, ");
        sql.append(" e1.ENDSTELLE as ENDSTELLE_B, e2.ID as ES_ID_A, e2.ENDSTELLE as ENDSTELLE_A, ");
        sql.append(" status.STATUS_TEXT, status.ID as STATUS_ID, ad.BEMERKUNGEN as AD_BEMERKUNG ");
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        sql.append(" left join T_AUFTRAG_STATUS status on ad.STATUS_ID = status.ID ");
        sql.append(" left join T_AUFTRAG_TECHNIK at on a.ID = at.AUFTRAG_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID = t.ID ");
        sql.append(" left join T_VPN v on at.VPN_ID = v.VPN_ID ");
        sql.append(" left join T_ENDSTELLE e1 on at.AT_2_ES_ID = e1.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE e2 on at.AT_2_ES_ID = e2.ES_GRUPPE ");
        sql.append(" where a.KUNDE__NO = ? ");
        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and (e1.ES_TYP is null or e1.ES_TYP='B') ");
        sql.append(" and (e2.ES_TYP is null or e2.ES_TYP='A') ");
        if (excludeInvalid) {
            sql.append(" and ad.STATUS_ID NOT IN (?,?,?) and ad.STATUS_ID<?");
            CollectionUtils.addAll(params, new Object[] { AuftragStatus.UNDEFINIERT, AuftragStatus.STORNO,
                    AuftragStatus.ABSAGE, AuftragStatus.KUENDIGUNG });
            types.add(new LongType());
            types.add(new LongType());
            types.add(new LongType());
            types.add(new LongType());
        }
        if (excludeKonsolidiert) {
            sql.append(" and ad.STATUS_ID NOT IN (?) ");
            params.add(AuftragStatus.KONSOLIDIERT);
            types.add(new LongType());
        }
        if (orderByBuendel) {
            sql.append(" order by ad.BUENDEL_NR, ad.BUENDEL_NR_HERKUNFT, a.ID");
        }
        else {
            sql.append(" order by a.ID");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CCKundeAuftragView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                CCKundeAuftragView view = new CCKundeAuftragView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBuendelNr(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBrauchtBuendel(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktNamePattern(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktGruppeId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setExportKdpM(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVpnId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragBemerkung(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }

            return retVal;
        }
        return Collections.<CCKundeAuftragView>emptyList();
    }

    @Override
    public List<Map<String, Object>> findActiveAuftraege4AM(Long kundeNo, Long taifunOrderNoOrig) {
        Map<String, Object> params = new HashMap<>();
        Date now = DateTools.getActualSQLDate();
        params.put("kundeNo", kundeNo);
        params.put("now", now);

        StringBuilder sql = new StringBuilder();
        sql.append("select ad.PRODAK_ORDER__NO as PRODAK_ORDER__NO, ad.AUFTRAG_ID as CC_AUFTRAG_ID, ");
        sql.append(" ad.BUENDEL_NR as BUENDEL_NR, ad.BUENDEL_NR_HERKUNFT as BUENDEL_NR_HERKUNFT, ");
        sql.append(" ad.STATUS_ID as AUFTRAG_STATUS_ID, p.PROD_ID as PROD_ID, p.AKTIONS_ID as AKTIONS_ID, ");
        sql.append(" p.ANSCHLUSSART as ANSCHLUSSART, count(ad.PRODAK_ORDER__NO) as ANZAHL");
        sql.append(" from T_AUFTRAG_DATEN ad ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append(" inner join T_AUFTRAG a on ad.AUFTRAG_ID=a.id");
        sql.append(" where ad.STATUS_ID < 9000 and ad.STATUS_ID <> 3400 and ad.STATUS_ID <> 1150 ");
        sql.append(" and p.AUFTRAGSERSTELLUNG=0 and ad.PRODAK_ORDER__NO is not null ");
        sql.append(" and a.KUNDE__NO = :kundeNo ");
        sql.append(" and (ad.GUELTIG_VON is null or ad.GUELTIG_VON <= :now) and (ad.GUELTIG_BIS is null or ad.GUELTIG_BIS > :now) ");
        if (taifunOrderNoOrig != null) {
            sql.append(" and ad.PRODAK_ORDER__NO = :taifunOrderNoOrig ");
            params.put("taifunOrderNoOrig", taifunOrderNoOrig);
        }
        sql.append(" group by ad.PRODAK_ORDER__NO, ad.AUFTRAG_ID, ad.BUENDEL_NR, ad.BUENDEL_NR_HERKUNFT, ");
        sql.append(" ad.STATUS_ID, p.PROD_ID, p.AKTIONS_ID, p.ANSCHLUSSART  ");
        sql.append(" having (p.AKTIONS_ID=1 or p.AKTIONS_ID=2)");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        for (Map.Entry<String, Object> param : params.entrySet()) {
            sqlQuery.setParameter(param.getKey(), param.getValue());
        }

        List<Object[]> result = sqlQuery.list();
        List<Map<String, Object>> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                Map<String, Object> view = new HashMap<>();
                view.put(CCAuftragViewDAO.AM_KEY_PRODAK_ORDER__NO, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_CC_AUFTRAG_ID, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_BUENDEL_NR, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_BUENDEL_NR_HERKUNFT, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_CC_AUFTRAG_STATUS_ID, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_PROD_ID, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_AKTIONS_ID, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_ANSCHLUSSART, ObjectTools.getLongSilent(values, columnIndex++));
                view.put(CCAuftragViewDAO.AM_KEY_ANZAHL, ObjectTools.getLongSilent(values, columnIndex++));

                views.add(view);
            }
        }

        return views;
    }

    @Override
    public List<AuftragVorlaufView> findAuftragsVorlauf() {
        StringBuilder sql = new StringBuilder("select a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.PRODAK_ORDER__NO, ");
        sql.append(" ad.VORGABE_SCV, ad.VORGABE_KUNDE, ad.BEARBEITER, t.TDN, p.ANSCHLUSSART as PRODUKT, ");
        sql.append(" e.ENDSTELLE, e.ORT, e.PLZ, ans.ANSCHLUSSART, bav.NAME as ANLASS, ltg.NAME as LEITUNGSART, ");
        sql.append(" schn.SCHNITTSTELLE, v.REALISIERUNGSTERMIN ");
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append(" inner join T_PRODUKT p on p.PROD_ID=ad.PROD_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_TDN t on t.ID=at.TDN_ID ");
        sql.append("left join T_VERLAUF_SUB_ORDERS vso on vso.AUFTRAG_ID=a.ID ");
        sql.append("left join T_VERLAUF v on v.AUFTRAG_ID=a.ID or v.ID=vso.VERLAUF_ID ");
        sql.append(" left join T_BA_VERL_ANLASS bav on bav.ID=v.ANLASS ");
        sql.append(" left join T_ENDSTELLE e on at.AT_2_ES_ID=e.ES_GRUPPE ");
        sql.append(" left join T_ANSCHLUSSART ans on ans.ID=e.ANSCHLUSSART ");
        sql.append(" left join T_ES_LTG_DATEN esltg on esltg.ES_ID=e.ID ");
        sql.append(" left join T_LEITUNGSART ltg on ltg.ID=esltg.LEITUNGSART ");
        sql.append(" left join T_SCHNITTSTELLE schn on schn.ID=esltg.SCHNITTSTELLE_ID ");
        sql.append(" where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and v.AKT=? and (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=?) ");
        sql.append(" and e.ES_TYP=? and ad.STATUS_ID not in (?,?,?) ");
        sql.append(" and (ad.VORGABE_SCV>=? or v.REALISIERUNGSTERMIN>=?) ");
        sql.append(" and (esltg.GUELTIG_VON is null or esltg.GUELTIG_VON<=?) ");
        sql.append(" and (esltg.GUELTIG_BIS is null or esltg.GUELTIG_BIS>?) ");
        sql.append(" order by ad.VORGABE_SCV, v.REALISIERUNGSTERMIN, a.ID");

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Date[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        CollectionUtils.addAll(params, new Integer[] { 1, 0 });
        types.add(new IntegerType());
        types.add(new IntegerType());
        params.add(Endstelle.ENDSTELLEN_TYP_B);
        types.add(new StringType());
        CollectionUtils.addAll(params, new Long[] {
                AuftragStatus.STORNO, AuftragStatus.ABSAGE, AuftragStatus.KONSOLIDIERT });
        types.add(new LongType());
        types.add(new LongType());
        types.add(new LongType());
        CollectionUtils.addAll(params, new Date[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();

        if (result != null) {
            List<AuftragVorlaufView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragVorlaufView view = new AuftragVorlaufView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVorgabeSCV(ObjectTools.getDateSilent(values, columnIndex++));
                view.setVorgabeKunde(ObjectTools.getDateSilent(values, columnIndex++));
                view.setUebernommenVon(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleOrt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstellePLZ(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHvtSchaltung(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setLeitungsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setSchnittstelle(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealisierungstermin(ObjectTools.getDateSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return Collections.<AuftragVorlaufView>emptyList();
    }

    @Override
    public List<IncompleteAuftragView> findIncomplete(final java.util.Date gueltigVon) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.ID as AUFTRAG_ID, ad.PRODAK_ORDER__NO, t.TDN, a.KUNDE__NO, p.ANSCHLUSSART as PRODUKTNAME, ");
        sql.append("pg.PRODUKTGRUPPE, ba4a.NAME as AUFTRAGSART, ad.VORGABE_SCV, ad.BEARBEITER, ba4v.NAME as BA_ANLASS, ");
        sql.append("v.REALISIERUNGSTERMIN as BA_REALISIERUNGSTERMIN, va.DATUM_AN as BA_AN_DISPO, ");
        sql.append("cb.BESTELLT_AM as CB_BESTELLT_AM, cb.ZURUECK_AM as CB_ZURUECK_AM, cb.LBZ as CB_LBZ, ");
        sql.append("cb.KUENDIGUNG_AN_CARRIER as CB_KUENDIGUNG_AN, cb.KUENDBESTAETIGUNG_CARRIER as CB_KUENDIGUNG_ZURUECK, ");
        sql.append("ni.TEXT as NIEDERLASSUNG ");
        sql.append("from T_AUFTRAG a ");
        sql.append("inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID ");
        sql.append("inner join T_PRODUKT p on ad.PROD_ID=p.PROD_ID ");
        sql.append("inner join T_PRODUKTGRUPPE pg on p.PRODUKTGRUPPE_ID=pg.ID ");
        sql.append("left join T_AUFTRAG_TECHNIK at on a.ID=at.AUFTRAG_ID ");
        sql.append("left join T_BA_VERL_ANLASS ba4a on ba4a.ID=at.AUFTRAGSART ");
        sql.append("left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append("left join T_ENDSTELLE e on at.AT_2_ES_ID=e.ES_GRUPPE ");
        sql.append("left join T_CARRIERBESTELLUNG cb on e.CB_2_ES_ID=cb.CB_2_ES_ID ");
        sql.append("left join T_CARRIER c on c.ID=cb.CARRIER_ID ");
        sql.append("left join T_VERLAUF_SUB_ORDERS vso on vso.AUFTRAG_ID=a.ID ");
        sql.append("left join T_VERLAUF v on v.AUFTRAG_ID=a.ID or v.ID=vso.VERLAUF_ID ");
        sql.append("left join T_BA_VERL_ANLASS ba4v on v.ANLASS=ba4v.ID ");
        sql.append("left join T_VERLAUF_ABTEILUNG va on v.ID=va.VERLAUF_ID ");
        sql.append("left join T_NIEDERLASSUNG ni on at.NIEDERLASSUNG_ID=ni.ID ");
        sql.append("where ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append("and ad.STATUS_ID NOT IN (?,?,?,?) and pg.ID<>? ");
        sql.append("and (at.GUELTIG_VON is null or at.GUELTIG_VON<=?) and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?) ");
        sql.append("and (e.ES_TYP is null or e.ES_TYP=?) ");
        sql.append("and (c.TEXT is null or c.TEXT=?) ");
        sql.append("and (v.ID is null or v.ID in (select max(vtmp.ID) from T_VERLAUF vtmp where vtmp.AUFTRAG_ID=ad.AUFTRAG_ID)) ");
        sql.append("and (v.PROJEKTIERUNG is null or v.PROJEKTIERUNG=?) ");
        sql.append("and (va.ABTEILUNG_ID is null or va.ABTEILUNG_ID in (?,?)) ");
        if (gueltigVon != null) {
            sql.append("and (ad.gueltig_von >= ?)");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        java.sql.Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new java.sql.Date[] { now, now });
        types.add(new DateType());
        types.add(new DateType());

        CollectionUtils.addAll(params, new Long[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE,
                AuftragStatus.IN_BETRIEB, AuftragStatus.KONSOLIDIERT });
        types.add(new LongType());
        types.add(new LongType());
        types.add(new LongType());
        types.add(new LongType());
        params.add(ProduktGruppe.AK_ONLINE);
        types.add(new LongType());
        CollectionUtils.addAll(params, new java.sql.Date[] { now, now });
        types.add(new DateType());
        types.add(new DateType());
        CollectionUtils.addAll(params, new Object[] { Endstelle.ENDSTELLEN_TYP_B, "DTAG", 0 });
        types.add(new StringType());
        types.add(new StringType());
        types.add(new IntegerType());
        params.add(Abteilung.DISPO);
        types.add(new LongType());
        params.add(Abteilung.NP);
        types.add(new LongType());

        if (gueltigVon != null) {
            params.add(gueltigVon);
            types.add(new DateType());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();

        List<IncompleteAuftragView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                IncompleteAuftragView view = new IncompleteAuftragView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktGruppe(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragsart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVorgabeSCV(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBearbeiter(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBaAnlass(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBaRealTermin(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBaAnDispo(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCbBestelltAm(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCbZurueckAm(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCbLbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setCbKuendigungAm(ObjectTools.getDateSilent(values, columnIndex++));
                view.setCbKuendigungZurueck(ObjectTools.getDateSilent(values, columnIndex++));
                view.setNiederlassung(ObjectTools.getStringSilent(values, columnIndex++));

                views.add(view);
            }
        }

        return views;
    }

    @Override
    public List<CCKundeAuftragView> findKundeAuftragViews4Address(Long addressId) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        params.add(addressId);
        types.add(new LongType());
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now });
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select a.ID, a.KUNDE__NO, ad.PRODAK_ORDER__NO, t.TDN, ad.INBETRIEBNAHME, ad.KUENDIGUNG, ");
        sql.append(" ad.BUENDEL_NR, p.PROD_ID, p.BRAUCHT_BUENDEL, p.ANSCHLUSSART, p.PROD_NAME_PATTERN, ");
        sql.append(" p.PRODUKTGRUPPE_ID, p.EXPORT_KDP_M, v.VPN_ID, v.VPN_NR, e1.ID as ES_ID_B, ");
        sql.append(" e1.ENDSTELLE as ENDSTELLE_B, e2.ID as ES_ID_A, e2.ENDSTELLE as ENDSTELLE_A, ");
        sql.append(" status.STATUS_TEXT, status.ID as STATUS_ID, ad.BEMERKUNGEN as AD_BEMERKUNG ");
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        sql.append(" inner join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        sql.append(" left join T_AUFTRAG_STATUS status on ad.STATUS_ID = status.ID ");
        sql.append(" left join T_AUFTRAG_TECHNIK at on a.ID = at.AUFTRAG_ID ");
        sql.append(" left join T_TDN t on at.TDN_ID = t.ID ");
        sql.append(" left join T_VPN v on at.VPN_ID = v.VPN_ID ");
        sql.append(" left join T_ENDSTELLE e1 on at.AT_2_ES_ID = e1.ES_GRUPPE ");
        sql.append(" left join T_ENDSTELLE e2 on at.AT_2_ES_ID = e2.ES_GRUPPE ");
        sql.append(" left join T_ANSPRECHPARTNER asp on a.ID = asp.AUFTRAG_ID ");
        sql.append(" where asp.ADDRESS_ID = ? ");
        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and (e1.ES_TYP is null or e1.ES_TYP='B') ");
        sql.append(" and (e2.ES_TYP is null or e2.ES_TYP='A') ");
        sql.append(" order by a.ID");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CCKundeAuftragView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                CCKundeAuftragView view = new CCKundeAuftragView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
                view.setBuendelNr(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setBrauchtBuendel(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktNamePattern(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktGruppeId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setExportKdpM(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setVpnId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVpnNr(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleIdA(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleA(ObjectTools.getStringSilent(values, columnIndex++));
                view.setEndstelleIdB(ObjectTools.getLongSilent(values, columnIndex++));
                view.setEndstelleB(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragBemerkung(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }

            return retVal;
        }
        return Collections.<CCKundeAuftragView>emptyList();
    }

    @Override
    public List<CCAuftragIDsView> findAufragIdAndVbz4AuftragIds(Collection<Long> auftragIds) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        Date now = DateTools.getActualSQLDate();
        params.add(now);
        params.add(now);
        params.add(now);
        params.add(now);
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());
        types.add(new DateType());

        StringBuilder sql = new StringBuilder();
        sql.append("select ad.AUFTRAG_ID, ad.ID as AD_ID, ad.STATUS_ID, ad.PRODAK_ORDER__NO, at.ID as AT_ID, t.TDN, ");
        sql.append(" ast.STATUS_TEXT, p.ANSCHLUSSART from T_AUFTRAG_DATEN ad ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on ad.AUFTRAG_ID=at.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID=ast.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID=t.ID ");
        sql.append(" left join T_PRODUKT p on ad.PROD_ID=p.PROD_ID");
        sql.append(" where ad.AUFTRAG_ID in (").append(StringUtils.join(auftragIds, ",")).append(")");
        sql.append(" and (ad.GUELTIG_VON is null or ad.GUELTIG_VON<=?) and (ad.GUELTIG_BIS is null or ad.GUELTIG_BIS>?) ");
        sql.append(" and (at.GUELTIG_VON is null or at.GUELTIG_VON<=?) and (at.GUELTIG_BIS is null or at.GUELTIG_BIS>?) ");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sql.toString());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CCAuftragIDsView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                CCAuftragIDsView view = new CCAuftragIDsView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragDatenId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragTechnikId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProduktName(ObjectTools.getStringSilent(values, columnIndex++));

                retVal.add(view);
            }
            return retVal;
        }
        return Collections.<CCAuftragIDsView>emptyList();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /* Mapper fuer Objekte des Typs <code>AuftragEndstelleView</code>. */
    private static class AuftragEndstelleViewMapper {
        public AuftragEndstelleView mapView(Object[] values) {
            int columnIndex = 0;
            AuftragEndstelleView view = new AuftragEndstelleView();
            view.setEndstelleId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setEndstelle(ObjectTools.getStringSilent(values, columnIndex++));
            view.setEndstelleName(ObjectTools.getStringSilent(values, columnIndex++));
            view.setEndstelleGeoId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setEndstelleOrt(ObjectTools.getStringSilent(values, columnIndex++));
            view.setEndstelleTyp(ObjectTools.getStringSilent(values, columnIndex++));
            view.setRangierId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
            view.setProduktId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setVorgabeSCV(ObjectTools.getDateSilent(values, columnIndex++));
            view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
            view.setKuendigung(ObjectTools.getDateSilent(values, columnIndex++));
            view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
            view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
            view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
            view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
            view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
            return view;
        }
    }
}
