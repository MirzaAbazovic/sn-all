/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 11:34:32
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.math.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.AuftragDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;

/**
 * Hibernate DAO-Implementierung fuer Objekte vom Typ <code>de.augustakom.hurrican.model.billing.BAuftrag</code>
 *
 *
 */
public class AuftragDAOImpl extends Hibernate4DAOImpl implements AuftragDAO {

    private final static String FIBU_GEBUEHREN_ART_VERBINDUNG = "USAGE          ";
    private final static String FIBU_GEBUEHREN_ART_RABATT = "DISCOUNT       ";
    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public BAuftrag findByAuftragNoOrig(final Long auftragNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BAuftrag.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        criteria.add(Restrictions.eq("histLast", Boolean.TRUE));

        @SuppressWarnings("unchecked")
        List<BAuftrag> result = criteria.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public BAuftrag findAuftragAkt(final Long auftragNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BAuftrag.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        criteria.add(Restrictions.eq("histStatus", "AKT"));

        @SuppressWarnings("unchecked")
        List<BAuftrag> result = criteria.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public List<BAuftrag> findByBuendelNo(final Integer buendelNo) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BAuftrag.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "bundleOrderNo", buendelNo);
        criteria.add(Restrictions.eq("histLast", Boolean.TRUE));
        criteria.add(Restrictions.not(Restrictions.eq("atyp", "KUEND")));

        @SuppressWarnings("unchecked")
        List<BAuftrag> result = criteria.list();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BAuftragPos> findAuftragPos4Auftrag(Long auftragNoOrig) {
        // Achtung: Sortier-Reihenfolge muss beachtet werden!
        return find("from " + BAuftragPos.class.getName() +
                " ap where ap.auftragNoOrig=? order by ap.itemNo asc", auftragNoOrig);
    }

    @Override
    public List<BAuftragPos> findAuftragPos4Auftrag(final Long auftragNoOrig,
            final List<Long> externLeistungNos, final boolean checkInServiceValues, Date chargeToBegin, Date chargeToEnd) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("select ap.itemNo from ");
        Query query;
        if (!checkInServiceValues) {
            hql.append(BAuftragPos.class.getName()).append(" ap, ");
            hql.append(Leistung.class.getName()).append(" l ");
            hql.append("where ap.leistungNoOrig=l.leistungNoOrig ");
            hql.append("and l.histStatus= :histStat and l.externLeistungNo in (:externIds) ");
            if (auftragNoOrig != null) {
                hql.append(" and ap.auftragNoOrig= :aNoOrig ");
            }
            if (chargeToBegin != null && chargeToEnd != null) {
                hql.append(" and ap.chargeTo between :chargeToBegin and :chargeToEnd ");
            }

            query = session.createQuery(hql.toString());
            query.setString("histStat", BillingConstants.HIST_STATUS_AKT);
            query.setParameterList("externIds", externLeistungNos);
            if (auftragNoOrig != null) {
                query.setLong("aNoOrig", auftragNoOrig);
            }
            if (chargeToBegin != null && chargeToEnd != null) {
                query.setDate("chargeToBegin", chargeToBegin);
                query.setDate("chargeToEnd", chargeToEnd);
            }
        }
        else {
            hql.append(BAuftragPos.class.getName()).append(" ap, ");
            hql.append(Leistung.class.getName()).append(" l, ");
            hql.append(ServiceValue.class.getName()).append(" sv ");
            hql.append("where ap.leistungNoOrig=l.leistungNoOrig ");
            hql.append("and l.histStatus= :histStat and l.leistungNo=sv.leistungNo and ");
            hql.append("sv.externLeistungNo in (:externIds) ");
            if (auftragNoOrig != null) {
                hql.append(" and ap.auftragNoOrig= :aNoOrig ");
            }
            if (chargeToBegin != null && chargeToEnd != null) {
                hql.append(" and ap.chargeTo between :chargeToBegin and :chargeToEnd ");
            }

            query = session.createQuery(hql.toString());
            query.setString("histStat", BillingConstants.HIST_STATUS_AKT);
            query.setParameterList("externIds", externLeistungNos);
            if (auftragNoOrig != null) {
                query.setLong("aNoOrig", auftragNoOrig);
            }
            if (chargeToBegin != null && chargeToEnd != null) {
                query.setDate("chargeToBegin", chargeToBegin);
                query.setDate("chargeToEnd", chargeToEnd);
            }
        }

        @SuppressWarnings("unchecked")
        List<Long> result = query.list();
        if (result != null) {
            List<BAuftragPos> retVal = new ArrayList<>();
            for (Long itemNo : result) {
                retVal.add(findById(itemNo, BAuftragPos.class));
            }
            return retVal;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BAuftrag> findAuftraege4Kunde(Long kundeNo) {
        return find("from " + BAuftrag.class.getName() + " a where a.kundeNo=? and a.histLast=? "
                        + "and a.histStatus not in (?)",
                new Object[] { kundeNo, Boolean.TRUE, BillingConstants.HIST_STATUS_UNG }
        );
    }

    @Override
    public BAuftrag findAuftrag4CwmpId(final String cwmpId, final Date valid) {
        final String sql = "SELECT a.* FROM DEVICE__FRITZBOX df JOIN DEVICE d ON d.dev_no = df.dev_no "
                + "JOIN AUFTRAGPOS ap ON ap.dev_no = d.dev_no JOIN AUFTRAG a ON ap.order__no = a.auftrag__no "
                + "WHERE :auftragValid BETWEEN a.valid_from AND a.valid_to "
                + "AND :deviceValid BETWEEN nvl2(ap.DEVICE_VALID_FROM, ap.DEVICE_VALID_FROM, :deviceValidFromNvl)"
                + "                         AND nvl2(ap.DEVICE_VALID_TO, ap.DEVICE_VALID_TO, :deviceValidToNvl)"
                + "AND df.cwmp_id = :cwmpId";

        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql).addEntity(BAuftrag.class);
        query.setDate("auftragValid", valid);
        query.setDate("deviceValid", valid);
        query.setDate("deviceValidFromNvl", valid);
        query.setDate("deviceValidToNvl", DateTools.getBillingEndDate());
        query.setString("cwmpId", cwmpId);
        return (BAuftrag) query.uniqueResult();
    }

    @Override
    public List<BVPNAuftragView> findVPNAuftraege() {

        @SuppressWarnings("unchecked")
        List<Object[]> queryResult = find(
                "SELECT a.auftragNoOrig, a.kundeNo, k.name, k.vorname FROM "
                        + Leistung.class.getName() + " l, "
                        + BAuftragPos.class.getName() + " ap, "
                        + BAuftrag.class.getName() + " a, "
                        + Kunde.class.getName() + " k "
                        + "where l.leistungNoOrig = ap.leistungNoOrig "
                        + "and ap.auftragNoOrig = a.auftragNoOrig "
                        + "and k.kundeNo = a.kundeNo "
                        + "and l.externMiscNo between ? and ? "
                        + "and l.histLast=? and a.histLast=? "
                        + "group by a.auftragNoOrig, a.kundeNo, k.name, k.vorname",
                new Object[] { Leistung.EXT_MISC_NO_VPN_MIN, Leistung.EXT_MISC_NO_VPN_MAX,
                        Boolean.TRUE, Boolean.TRUE }
        );

        if (queryResult != null) {
            List<BVPNAuftragView> result = new ArrayList<>();
            for (Object[] values : queryResult) {

                BVPNAuftragView view = new BVPNAuftragView();
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, 0));
                view.setKundeNo(ObjectTools.getLongSilent(values, 1));
                view.setKundeName(ObjectTools.getStringSilent(values, 2));
                view.setKundeVorname(ObjectTools.getStringSilent(values, 3));
                result.add(view);
            }
            return result;
        }

        return null;
    }

    @Override
    public List<BAuftragPos> findBAuftragPos4Report(final Long auftragNoOrig, final Boolean onlyAkt) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("select distinct pos.itemNo, pos.itemNoOrig, ");
        hql.append("pos.auftragNoOrig, pos.createAuftragNo, ");
        hql.append("pos.terminateAuftragNo, pos.isPlanned, pos.leistungNoOrig, pos.parameter, ");
        hql.append("pos.menge, pos.chargeFrom, pos.chargeTo, pos.chargedUntil, pos.preis, pos.listenpreis, ");
        hql.append("l.leistungKat from ");
        hql.append(Leistung.class.getName()).append(" l, ");
        hql.append(BAuftragPos.class.getName()).append(" pos ");
        hql.append(" where pos.auftragNoOrig = :auftragNoOrig ");
        hql.append(" and pos.leistungNoOrig = l.leistungNoOrig ");
        hql.append(" and l.histStatus = :histStatus ");
        hql.append(" and l.generateBillPos = :genBill ");
        hql.append(" and l.fibuGebuehrenArt != :discount ");
        hql.append(" and l.fibuGebuehrenArt != :usage ");
        if (!onlyAkt) {
            hql.append(" and (pos.chargeTo is null or pos.chargeTo > :date) ");
        }
        else {
            hql.append(" and (pos.chargeTo is null or pos.chargeTo = :endDate )");
        }
        hql.append(" order by pos.chargeTo asc, pos.listenpreis asc, pos.preis asc ");

        Query query = session.createQuery(hql.toString());

        query.setLong("auftragNoOrig", auftragNoOrig);
        query.setString("histStatus", BillingConstants.HIST_STATUS_AKT);
        query.setBoolean("genBill", true);
        query.setString("discount", FIBU_GEBUEHREN_ART_RABATT);
        query.setString("usage", FIBU_GEBUEHREN_ART_VERBINDUNG);
        if (!onlyAkt) {
            query.setDate("date", DateTools.getActualSQLDate());
        }
        else {
            query.setDate("endDate", DateTools.getBillingEndDate());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.list();
        if (result != null) {
            List<BAuftragPos> retVal = new ArrayList<>();
            for (Object[] values : result) {
                BAuftragPos model = new BAuftragPos();

                model.setItemNo(ObjectTools.getLongSilent(values, 0));
                model.setItemNoOrig(ObjectTools.getLongSilent(values, 1));
                model.setAuftragNoOrig(ObjectTools.getLongSilent(values, 2));
                model.setCreateAuftragNo(ObjectTools.getLongSilent(values, 3));
                model.setTerminateAuftragNo(ObjectTools.getLongSilent(values, 4));
                model.setIsPlanned(ObjectTools.getBooleanSilent(values, 5));
                model.setLeistungNoOrig(ObjectTools.getLongSilent(values, 6));
                model.setParameter(ObjectTools.getStringSilent(values, 7));
                model.setMenge(ObjectTools.getLongSilent(values, 8));
                model.setChargeFrom(ObjectTools.getDateSilent(values, 9));
                model.setChargeTo(ObjectTools.getDateSilent(values, 10));
                model.setChargedUntil(ObjectTools.getDateSilent(values, 11));
                model.setPreis(ObjectTools.getFloatSilent(values, 12));
                model.setListenpreis(ObjectTools.getFloatSilent(values, 13));
                retVal.add(model);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public BAuftrag findAuftragStornoByAuftragNoOrig(Long auftragNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(" FROM " + BAuftrag.class.getName() + " as a "
                + " where a.auftragNoOrig = ? "
                + " and a.astatus=? " + " order by a.histCnt DESC");
        query.setParameters(new Object[] { auftragNoOrig, BAuftrag.STATUS_STORNIERT }, new Type[] { new LongType(), new IntegerType() });
        @SuppressWarnings("unchecked")
        List<BAuftrag> queryResult = (List<BAuftrag>) query.list();
        BAuftrag bAuftrag = null;
        if (CollectionTools.isNotEmpty(queryResult)) {
            bAuftrag = queryResult.get(0);
        }
        return (bAuftrag != null) ? bAuftrag : null;
    }

    @Override
    public BAuftrag findBySAPOrderId(final String sapOrderId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from " + BAuftrag.class.getName() + " a "
                + " where a.sapId = :sapId " + " and a.gueltigVon <= :date " + " and a.gueltigBis >= :date ");
        query.setString("sapId", sapOrderId);
        query.setDate("date", DateTools.getActualSQLDate());

        @SuppressWarnings("unchecked")
        List<BAuftrag> result = query.list();
        if ((result != null) && (!result.isEmpty())) { return result.get(0); }
        return null;
    }

    @Override
    public List<BAuftrag> findAuftraege4SAP(AuftragSAPQuery query) {
        List<Long> orderNOs = null;
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        StringBuilder sql = new StringBuilder("select distinct a.AUFTRAG__NO from ");
        sql.append("AUFTRAG a ");
        if (StringUtils.isNotBlank(query.getSapDebitorId())) {
            sql.append("inner join BILL_SPEC bs on a.BILL_SPEC_NO=bs.BILL_SPEC_NO ");
        }
        sql.append("where ");
        if (StringUtils.isNotBlank(query.getSapDebitorId())) {
            sql.append("bs.EXT_DEBITOR_ID LIKE ?");
            params.add(WildcardTools.replaceWildcards(query.getSapDebitorId()));
            types.add(new StringType());
        }
        if (StringUtils.isNotBlank(query.getSapId())) {
            if (!params.isEmpty()) {
                sql.append(" and ");
            }
            sql.append("a.SAP_ID LIKE ?");
            params.add(WildcardTools.replaceWildcards(query.getSapId()));
            types.add(new StringType());
        }
        if (query.isNurAktuelle()) {
            sql.append(" AND A.HIST_STATUS = '" + BillingConstants.HIST_STATUS_AKT + "'");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<BigDecimal> result = sqlQuery.list();
        if (CollectionTools.isNotEmpty(result)) {
            List<Long> retVal = new ArrayList<>();
            for (BigDecimal value : result) {
                retVal.add(value.longValue());
            }
            orderNOs = retVal;
        }

        if (CollectionTools.isNotEmpty(orderNOs)) {
            List<BAuftrag> retVal = new ArrayList<>();
            for (Long orderNO : orderNOs) {
                retVal.add(findByAuftragNoOrig(orderNO));
            }
            return retVal;
        }

        return null;
    }

    @Override
    public TimeSlotView findTimeSlotView4Auftrag(Long auftragNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery("SELECT ts.TIME_SLOT_DATE, tsc.DAYTIME_FROM, tsc.DAYTIME_TO, "
                + "tsc.WEEKDAY, tsc.AREA_NO "
                + "FROM AUFTRAG a "
                + "INNER JOIN AUFTRAG__KOMBI kombi ON a.AUFTRAG_NO=kombi.AUFTRAG_NO "
                + "INNER JOIN TIME_SLOT ts ON ts.TIME_SLOT_NO = kombi.TIME_SLOT_NO "
                + "INNER JOIN TIME_SLOT_CONFIG tsc ON tsc.TIME_SLOT_CONFIG_NO = ts.TIME_SLOT_CONFIG_NO "
                + "WHERE a.AUFTRAG__NO=:auftragNo AND a.HIST_LAST='1'");
        sqlQuery.setParameter("auftragNo", auftragNoOrig);
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();

        if (CollectionTools.isNotEmpty(result) && (result.size() == 1)) {
            Object[] values = result.get(0);
            TimeSlotView retVal = new TimeSlotView();
            retVal.setDate(ObjectTools.getDateSilent(values, 0));
            retVal.setDaytimeFrom(ObjectTools.getDateSilent(values, 1));
            retVal.setDaytimeTo(ObjectTools.getDateSilent(values, 2));
            retVal.setWeekday(ObjectTools.getLongSilent(values, 3));
            retVal.setAreaNo(ObjectTools.getLongSilent(values, 4));

            return retVal;
        }

        return null;
    }

    @Override
    public Map<Long, TimeSlotView> findTimeSlotViews4Auftrag(List<Long> auftragNoOrigs) {
        StringBuilder sql = new StringBuilder("select a.AUFTRAG__NO, ts.TIME_SLOT_DATE, tsc.DAYTIME_FROM, tsc.DAYTIME_TO, ");
        sql.append("tsc.WEEKDAY, tsc.AREA_NO ");
        sql.append("from AUFTRAG a ");
        sql.append("inner join AUFTRAG__KOMBI kombi on a.AUFTRAG_NO=kombi.AUFTRAG_NO ");
        sql.append("inner join TIME_SLOT ts on ts.TIME_SLOT_NO = kombi.TIME_SLOT_NO ");
        sql.append("inner join TIME_SLOT_CONFIG tsc on tsc.TIME_SLOT_CONFIG_NO = ts.TIME_SLOT_CONFIG_NO ");
        sql.append("where a.AUFTRAG__NO in (%s) and a.HIST_LAST='1'");

        Map<Long, TimeSlotView> retVal = new HashMap<>(auftragNoOrigs.size());

        int packages = ((auftragNoOrigs.size() - 1) / 1000) + 1; // -1: consider case size == MAX_IN_LENGTH
        for (int idx = 0; idx < packages; idx++) {
            int fromIndex = idx * 1000;
            int toIndex = fromIndex + 1000;
            if (toIndex > auftragNoOrigs.size()) {
                toIndex = auftragNoOrigs.size();
            }

            String toExecute = String.format(sql.toString(), buildInParameter(auftragNoOrigs.subList(fromIndex, toIndex)));
            Session session = sessionFactory.getCurrentSession();
            SQLQuery sqlQuery = session.createSQLQuery(toExecute);
            @SuppressWarnings("unchecked")
            List<Object[]> result = sqlQuery.list();

            if (CollectionTools.isNotEmpty(result)) {
                for (Object[] values : result) {
                    Long auftragNoOrig = ObjectTools.getLongSilent(values, 0);

                    TimeSlotView timeSlotView = new TimeSlotView();
                    timeSlotView.setDate(ObjectTools.getDateSilent(values, 1));
                    timeSlotView.setDaytimeFrom(ObjectTools.getDateSilent(values, 2));
                    timeSlotView.setDaytimeTo(ObjectTools.getDateSilent(values, 3));
                    timeSlotView.setWeekday(ObjectTools.getLongSilent(values, 4));
                    timeSlotView.setAreaNo(ObjectTools.getLongSilent(values, 5));

                    retVal.put(auftragNoOrig, timeSlotView);
                }
            }
        }

        return retVal;
    }

    private String buildInParameter(List<Long> auftragNoOrigs) {
        StringBuilder params = new StringBuilder();
        boolean isFirst = true;
        for (Long auftragNoOrig : auftragNoOrigs) {
            if (!isFirst) {
                params.append(",");
            }
            params.append(auftragNoOrig);
            isFirst = false;
        }
        return params.toString();
    }

    @Override
    public List<BAuftragLeistungView> findAuftragLeistungView(final Long kundeNo, final Long taifunOrderNoOrig, final boolean noKuendAuftraege,
            final boolean onlyActLeistungen, final boolean onlyLeistungen4Extern) {
        final StringBuilder sql = new StringBuilder("SELECT");
        sql.append(" A.CUST_NO, A.ATYP, A.ASTATUS, A.HIST_STATUS, A.AUFTRAG_NO, A.AUFTRAG__NO, A.OLD_SERVICE__NO,");
        sql.append(" A.BUNDLE_AUFTRAG__NO, A.VALID_FROM, A.VALID_TO, A.BILL_SPEC_NO,");
        sql.append(" OE.OE_NO, OE.OE__NO, OE.NAME AS OE_NAME,");
        sql.append(" AP.ITEM_NO, AP.ITEM__NO, AP.QUANTITY, AP.CHARGE_FROM, AP.CHARGE_TO, AP.PARAMETER, AP.FREE_TEXT, AP.CHARGED_UNTIL,");
        sql.append(" L.LEISTUNG_NO, L.LEISTUNG__NO, L.NAME AS LEISTUNG_NAME,");
        sql.append(" SVP.VALUE AS SERVICE_VALUE,");
        sql.append(" NVL(SVP.EXT_MISC__NO, L.EXT_MISC__NO) AS EXT_MISC__NO,");
        sql.append(" NVL(SVP.EXT_PRODUKT__NO, L.EXT_PRODUKT__NO) AS EXT_PRODUKT__NO,");
        sql.append(" NVL(SVP.EXT_LEISTUNG__NO, L.EXT_LEISTUNG__NO) AS EXT_LEISTUNG__NO,");
        sql.append(" 0 as VERSION ");  // workaround, da Billing-Modelle von AbstractHurricanModel ableitet u. somit ein Version Feld benoetigt!
        sql.append(" FROM AUFTRAG A");
        sql.append(" JOIN OE OE ON A.OE__NO = OE.OE__NO AND OE.HIST_STATUS = :histStatus");
        sql.append(" JOIN AUFTRAGPOS AP ON AP.ORDER__NO = A.AUFTRAG__NO");
        sql.append(" JOIN LEISTUNG L ON L.LEISTUNG__NO = AP.SERVICE_ELEM__NO AND L.HIST_STATUS = :histStatus");
        sql.append(" LEFT JOIN SERVICE_VALUE_PRICE SVP ON SVP.LEISTUNG_NO = L.LEISTUNG_NO AND SVP.VALUE = AP.PARAMETER");
        sql.append(" WHERE A.CUST_NO = :kundeNo AND A.HIST_LAST = :histLast AND A.HIST_STATUS <> :notAuftragHistStatus");

        if (taifunOrderNoOrig != null) {
            sql.append(" AND a.AUFTRAG__NO = :taifunOrderNoOrig ");
        }
        if (noKuendAuftraege) {
            sql.append(" AND A.ATYP <> :aTypKuend");
        }
        if (onlyLeistungen4Extern) {
            sql.append(" AND (NVL(SVP.EXT_PRODUKT__NO, L.EXT_PRODUKT__NO) IS NOT NULL")
                    .append(" OR NVL(SVP.EXT_LEISTUNG__NO, L.EXT_LEISTUNG__NO) IS NOT NULL)");
        }
        // Required ordering for filterLeistungViews()
        sql.append(" ORDER BY AP.ITEM_NO ASC");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql.toString()).addEntity(BAuftragLeistungView.class);
        query.setLong("kundeNo", kundeNo);
        query.setBoolean("histLast", true);
        query.setString("notAuftragHistStatus", BillingConstants.HIST_STATUS_UNG);
        query.setString("histStatus", BillingConstants.HIST_STATUS_AKT);
        if (taifunOrderNoOrig != null) {
            query.setLong("taifunOrderNoOrig", taifunOrderNoOrig);
        }
        if (noKuendAuftraege) {
            query.setString("aTypKuend", BillingConstants.ATYP_KUEND);
        }

        @SuppressWarnings("unchecked")
        List<BAuftragLeistungView> result = query.list();
        return filterLeistungViews(result, onlyActLeistungen);
    }

    private List<BAuftragLeistungView> filterLeistungViews(List<BAuftragLeistungView> allViews, boolean onlyActLeistungen) {
        Map<Long, BAuftragLeistungView> map = new HashMap<>();
        for (BAuftragLeistungView leistungsView : allViews) {
            if (onlyActLeistungen) {
                // nur aktive Auftragspositionen/Leistungen ermitteln - jede Leistung nur einmal
                if (leistungsView.isActiveAuftragpos() && !map.containsKey(leistungsView.getItemNoOrig())) {
                    map.put(leistungsView.getItemNoOrig(), leistungsView);
                }
                else if (!leistungsView.isActiveAuftragpos() && map.containsKey(leistungsView.getItemNoOrig())) {
                    // Auftragsposition inaktiv --> entfernen, falls der Map zugeordnet
                    map.remove(leistungsView.getItemNoOrig());
                }
            }
            else {
                // jede Leistung nur einmal!
                map.put(leistungsView.getItemNoOrig(), leistungsView);
            }
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<BAuftragView> findAuftragViews(final Long kundeNo) {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery("SELECT"
                + " A.AUFTRAG_NO, A.AUFTRAG__NO, A.CUST_NO, A.ATYP, A.HIST_STATUS, A.USERW, A.DATEW, "
                + " OE.NAME AS OE_NAME," + " 0 AS VERSION "
                + " FROM AUFTRAG A"
                + " JOIN OE OE ON A.OE__NO = OE.OE__NO AND OE.HIST_STATUS = :oeHistStatus"
                + " WHERE A.CUST_NO = :kundeNo " + " AND (A.HIST_LAST = :histLast OR A.HIST_STATUS = :histStatusUng) "
                + " AND A.HIST_STATUS <> :histStatusAlt"
                + " ORDER BY A.DATEW DESC")
                .addEntity(BAuftragView.class);
        query.setLong("kundeNo", kundeNo);
        query.setString("oeHistStatus", BillingConstants.HIST_STATUS_AKT);
        query.setString("histStatusUng", BillingConstants.HIST_STATUS_UNG);
        query.setString("histStatusAlt", BillingConstants.HIST_STATUS_ALT);
        query.setBoolean("histLast", Boolean.TRUE);

        @SuppressWarnings("unchecked")
        List<BAuftragView> result = query.list();
        return result;
    }

    @Override
    public String findDebitorNrForAuftragNo(Long auftragNo) {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery("SELECT DISTINCT BS.EXT_DEBITOR_ID" +
                "  FROM   AUFTRAG A JOIN BILL_SPEC BS ON A.BILL_SPEC_NO = BS.BILL_SPEC_NO" +
                " WHERE   A.AUFTRAG_NO = :auftragNo");
        sqlQuery.setParameter("auftragNo", auftragNo);
        return sqlQuery.uniqueResult().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findAuftragIdsByGeoId(final Long geoId, final boolean ignoreCancelledOrders) {
        final StringBuilder hql = new StringBuilder()
                .append("select a.").append(BAuftrag.AUFTRAG_NO_ORIG)
                .append("   from ")
                .append(BAuftrag.class.getName()).append(" a, ")
                .append(Adresse.class.getName()).append(" addr ")
                .append(" where a.apAddressNo = addr.adresseNo ")
                .append(" and a.histLast = :histLast ")
                .append(" and a.histStatus != :histStatus ")
                .append(" and addr.geoId = :geoId ");
        if (ignoreCancelledOrders) {
            hql.append(" and (a.kuendigungsdatum > :now or a.kuendigungsdatum is null)");
        }

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());

        query.setBoolean("histLast", Boolean.TRUE);
        query.setString("histStatus", BillingConstants.HIST_STATUS_UNG);
        query.setLong("geoId", geoId);
        if (ignoreCancelledOrders) {
            query.setDate("now", new Date());
        }

        @SuppressWarnings("unchecked")
        final List<Long> auftragIds = query.list();
        return auftragIds;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
