/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 15:47:41
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.hibernate.HibernateInClauseHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.RufnummerDAO;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.DNBlock;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;

/**
 * Hibernate DAO-Implementierung fuer die Verwaltung von Objekten vom Typ <code>Rufnummer</code>.
 *
 *
 */
public class RufnummerDAOImpl extends Hibernate4DAOImpl implements RufnummerDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type) {
        Example ex = Example.create(example);
        ex.excludeZeroes();
        return getByExampleDAO().queryByCreatedExample(ex, type);
    }

    @Override
    public List<Rufnummer> findDNsByDnNoOrig(final Long dnNoOrig, final Date validFrom) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "dnNoOrig", dnNoOrig);
        if (validFrom != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.LESS_EQUAL, "gueltigVon", validFrom);
        }
        criteria.addOrder(Order.asc("gueltigVon"));
        criteria.addOrder(Order.asc("dnNo"));

        return criteria.list();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findByQuery(de.augustakom.hurrican.model.billing.query.RufnummerQuery)
     */
    @Override
    public List<Rufnummer> findByQuery(final RufnummerQuery query) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        if (WildcardTools.containsWildcard(query.getOnKz())) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.LIKE, "onKz", query.getOnKz());
        }
        else {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "onKz", query.getOnKz());
        }

        if (WildcardTools.containsWildcard(query.getDnBase())) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.LIKE, "dnBase", query.getDnBase());
        }
        else {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "dnBase", query.getDnBase());
        }

        if (query.getAuftragNoOrig() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", query.getAuftragNoOrig());
        }

        if (query.getDnNoOrig() != null) {
            CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "dnNoOrig", query.getDnNoOrig());
        }

        criteria.add(Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_AKT));
        return criteria.list();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findByAuftragNoOrig(java.lang.Long, boolean, boolean)
     */
    @Override
    public List<Rufnummer> findByAuftragNoOrig(final Long auftragNoOrig, final boolean onlyActual, final boolean onlyLast) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        if (onlyActual) {
            criteria.add(Restrictions.or(
                    Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_AKT),
                    Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_NEU)));
        }
        if (onlyLast) {
            // Bug-ID 16
            criteria.add(Restrictions.eq("histLast", Boolean.TRUE));
        }
        criteria.addOrder(Order.desc("mainNumber")); // Sortierung lt. Frau Prediger
        criteria.addOrder(Order.asc("onKz"));
        criteria.addOrder(Order.asc("dnBase"));
        criteria.addOrder(Order.asc("rangeFrom"));

        @SuppressWarnings("unchecked")
        List<Rufnummer> result = criteria.list();
        List<Rufnummer> retVal = new ArrayList<>();
        if (result != null) {
            for (Rufnummer dn : result) {
                retVal.add(dn);

                if (!onlyLast) {
                    // es muessen auch noch die DNs mit Status 'NEU' gesucht werden, deren AuftragNoOrig NULL ist.
                    Criteria criteriaNewDN = session.createCriteria(Rufnummer.class);
                    CriteriaHelper.addExpression(criteriaNewDN, CriteriaHelper.EQUAL, "dnNoOrig", dn.getDnNoOrig());
                    CriteriaHelper.addExpression(criteriaNewDN, CriteriaHelper.EQUAL, "histStatus",
                            BillingConstants.HIST_STATUS_NEU);
                    criteriaNewDN.add(Restrictions.isNull("auftragNoOrig"));

                    @SuppressWarnings("unchecked")
                    List<Rufnummer> newDNs = criteriaNewDN.list();
                    if (newDNs != null) {
                        for (Rufnummer dnNew : newDNs) {
                            retVal.add(dnNew);
                        }
                    }
                }
            }
        }

        return retVal;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findByAuftragNoOrig(java.lang.Long)
     */
    @Override
    public List<Rufnummer> findByAuftragNoOrig(final Long auftragNoOrig) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        criteria.add(Restrictions.or(
                Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_AKT),
                Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_NEU)));
        criteria.addOrder(Order.desc("mainNumber"));
        criteria.addOrder(Order.asc("onKz"));
        criteria.addOrder(Order.asc("dnBase"));
        criteria.addOrder(Order.asc("rangeFrom"));

        return criteria.list();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findAllRNs4Auftrag(java.lang.Long)
     */
    @Override
    public List<Rufnummer> findAllRNs4Auftrag(final Long auftragNoOrig) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        criteria.addOrder(Order.desc("mainNumber"));
        criteria.addOrder(Order.asc("onKz"));
        criteria.addOrder(Order.asc("dnBase"));
        criteria.addOrder(Order.asc("rangeFrom"));
        criteria.addOrder(Order.asc("histCnt"));

        return criteria.list();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findRNs4Auftrag(java.lang.Long, java.util.Date)
     */
    @Override
    public List<Rufnummer> findRNs4Auftrag(final Long auftragNoOrig, final Date validDate) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "auftragNoOrig", auftragNoOrig);
        criteria.add(Restrictions.le("gueltigVon", validDate));
        criteria.add(Restrictions.ge("gueltigBis", validDate));
        criteria.addOrder(Order.desc("mainNumber"));
        criteria.addOrder(Order.asc("onKz"));
        criteria.addOrder(Order.asc("dnBase"));
        criteria.addOrder(Order.asc("rangeFrom"));
        criteria.addOrder(Order.asc("histCnt"));

        return criteria.list();
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findOnkz4AuftragNoOrig(java.lang.Long)
     */
    @Override
    public List<String> findOnkz4AuftragNoOrig(Long auftragNoOrig) {
        @SuppressWarnings("unchecked")
        List<String> result = find(
                "select r.onKz from " + Rufnummer.class.getName() + " r "
                        + "where r.auftragNoOrig=? and " + " r.oeNoOrig not in (?,?,?) "
                        + "and (r.histStatus=? or r.histStatus=?)",
                new Object[] { auftragNoOrig, Rufnummer.OE__NO_ROUTING, Rufnummer.OE__NO_GEAEND_RUF,
                        Rufnummer.OE__NO_KEIN_ANSCHLUSS, BillingConstants.HIST_STATUS_AKT, BillingConstants.HIST_STATUS_NEU });
        return result;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findHauptRNs(java.util.List)
     */
    @Override
    public Map<Long, String> findHauptRNs(List<Long> auftragNoOrigs) {

        Map<String, Object> defParams = new HashMap<>();
        defParams.put("isHNr", Boolean.TRUE);
        defParams.put("oeRouting", Rufnummer.OE__NO_ROUTING);
        defParams.put("hStatus1", BillingConstants.HIST_STATUS_AKT);
        defParams.put("hStatus2", BillingConstants.HIST_STATUS_NEU);

        List<Object[]> result = HibernateInClauseHelper.list(getSessionFactory(),
                "select r.onKz, r.dnBase, r.directDial, r.auftragNoOrig "
                        + "from " + Rufnummer.class.getName() + " r "
                        + "where %s and r.mainNumber= :isHNr "
                        + " and r.oeNoOrig<> :oeRouting "
                        + "and (r.histStatus= :hStatus1 or r.histStatus= :hStatus2)",
                defParams, "r.auftragNoOrig", auftragNoOrigs);

        if (result != null) {
            Map<Long, String> retVal = new HashMap<>();
            for (Object[] values : result) {
                String onkz = ObjectTools.getStringSilent(values, 0);
                String dnBase = ObjectTools.getStringSilent(values, 1);
                String directDial = ObjectTools.getStringSilent(values, 2);
                Long aId = ObjectTools.getLongSilent(values, 3);

                StringBuilder rn = new StringBuilder(onkz);
                rn.append("/");
                rn.append(dnBase);
                if (StringUtils.isNotBlank(directDial)) {
                    rn.append(directDial);
                }

                retVal.put(aId, rn.toString());
            }
            return retVal;
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findLastRufnummer(java.lang.Long)
     */
    @Override
    public Rufnummer findLastRufnummer(Long dnNoOrig) {
        @SuppressWarnings("unchecked")
        List<Rufnummer> result = find(
                "from " + Rufnummer.class.getName() + " dn "
                        + "where dn.dnNoOrig=? and dn.histLast=?",
                new Object[] { dnNoOrig, Boolean.TRUE });
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findLastDNBlock(java.lang.Long)
     */
    @Override
    public DNBlock findLastDNBlock(Long blockNoOrig) {
        @SuppressWarnings("unchecked")
        List<DNBlock> result = find(
                "from " + DNBlock.class.getName() + " db "
                        + "where db.blockNoOrig=? and db.histLast=?",
                new Object[] { blockNoOrig, Boolean.TRUE });
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findDnNotHistLastTillDate(java.util.Date)
     */
    @Override
    public List<Long> findDnNotHistLastTillDate(Date date) {
        @SuppressWarnings("unchecked")
        List<Long> result = find("select r.dnNo "
                + "from " + Rufnummer.class.getName() + " r "
                + "where r.histLast=? and " + " r.gueltigBis <= ? "
                + "Group by r.dnNo", new Object[] { Boolean.FALSE, date });
        return result;
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.RufnummerDAO#findAuftragDNViewsByQuery(de.augustakom.hurrican.model.billing.query.RufnummerQuery)
     */
    @Override
    public List<AuftragDNView> findAuftragDNViewsByQuery(RufnummerQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.DN_NO, d.DN__NO, d.ONKZ, d.DN_BASE, d.HIST_STATE, d.RANGE_FROM, ");
        sql.append("d.RANGE_TO, d.REAL_DATE, d.LAST_CARRIER, d.ACT_CARRIER, d.FUTURE_CARRIER, ");
        sql.append("k.CUST_NO, k.PARENT_CUST_NO, k.NAME, k.FIRSTNAME, k.CUSTOMER_TYPE, k.VIP, k.FERNKATASTROPHE, ");
        sql.append("a.STREET, a.HOUSE_NUM, a.PO_BOX, a.ZIP_CODE, a.CITY, ag.AUFTRAG__NO, ag.ASTATUS FROM ");
        sql.append("DN d, CUSTOMER k, ADDRESS a, ");
        sql.append("AUFTRAG ag WHERE d.ORDER__NO=ag.AUFTRAG__NO ");
        sql.append("AND ag.CUST_NO=k.CUST_NO and a.ADDR_NO=k.POSTAL_ADDR_NO ");

        if (query.getKundeNo() != null) {
            sql.append(" and ag.CUST_NO=? ");
            params.add(query.getKundeNo());
            types.add(new LongType());
        }
        if (query.getAuftragNoOrig() != null) {
            sql.append(" and d.ORDER__NO=? ");
            params.add(query.getAuftragNoOrig());
            types.add(new LongType());
        }
        if (query.getDnNoOrig() != null) {
            sql.append(" and d.DN__NO=? ");
            params.add(query.getDnNoOrig());
            types.add(new LongType());
        }
        if (StringUtils.isNotBlank(query.getOnKz())) {
            if (WildcardTools.containsWildcard(query.getOnKz())) {
                sql.append(" and d.ONKZ like ? ");
                params.add(WildcardTools.replaceWildcards(query.getOnKz()));
                types.add(new StringType());
            }
            else {
                sql.append(" and d.ONKZ=? ");
                params.add(query.getOnKz());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getDnBase())) {
            if (WildcardTools.containsWildcard(query.getDnBase())) {
                sql.append(" and CONCAT(d.DN_BASE,SUBSTR(RTRIM(d.RANGE_FROM),1,1)) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getDnBase()));
                types.add(new StringType());
            }
            else {
                sql.append(" and CONCAT(d.DN_BASE,SUBSTR(RTRIM(d.RANGE_FROM),1,1)) <=? ");
                sql.append(" and CONCAT(d.DN_BASE,d.RANGE_TO) >= ? ");
                params.add(query.getDnBase());
                types.add(new StringType());
                params.add(query.getDnBase());
                types.add(new StringType());
            }
        }
        if (query.isOnlyValidToFuture()) {
            sql.append("and d.VALID_TO>=? ");
            params.add(DateTools.getActualSQLDate());
            types.add(new DateType());
        }
        if (query.getValid() != null) {
            sql.append("and ? between d.VALID_FROM and d.VALID_TO ");
            params.add(query.getValid());
            types.add(new DateType());
        }
        if (query.isOnlyActive()) {
            sql.append("and (d.HIST_STATE=? or d.HIST_STATE=?) ");
            params.add(BillingConstants.HIST_STATUS_AKT);
            types.add(new StringType());
            params.add(BillingConstants.HIST_STATUS_NEU);
            types.add(new StringType());
        }

        sql.append("and ag.HIST_LAST=? ");
        params.add("1");
        types.add(new StringType());

        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<AuftragDNView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragDNView view = new AuftragDNView();
                view.setDnNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setDnNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setOnKz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setDnBase(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHistStatus(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangeFrom(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRangeTo(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRealDate(ObjectTools.getDateSilent(values, columnIndex++));
                view.setLastCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setActCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setFutureCarrier(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setHauptKundenNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVorname(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundenTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVip(ObjectTools.getStringSilent(values, columnIndex++));
                view.setFernkatastrophe(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setStrasse(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPostfach(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPlz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setOrt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex));
                retVal.add(view);
            }

            return retVal;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findAuftragIdsByEinzelrufnummer(String onkz, String dnBase) {
        return findLatestAuftragIdByOnkzAndDnBase(onkz, dnBase, false, null, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> findAuftragIdsByBlockrufnummer(String onkz, String dnBase) {
        return findLatestAuftragIdByOnkzAndDnBase(onkz, dnBase, true, null, null, false);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> findNonBillableAuftragIds(String onkz, String dnBase, String rangeFrom, String rangeTo) {
        return findLatestAuftragIdByOnkzAndDnBase(onkz, dnBase, false, rangeFrom, rangeTo, true);
    }

    /**
     * Sucht nach den letzten, aktiven Aufträgen, die die angegebene Onkz und DnBase zugeordnet wurde. Es werden nur
     * Aufträge berücksichtigt, die folgende Kriterien erfüllen: <ul> <li>ONKZ stimmt überein</li> <li>DN_BASE stimmt
     * überein</li> <li>AUFTRAG_NO_ORIG is ungleich null (Taifun Nr)</li> <li>DIRECT_DIAL not null bei Blockrufnummern,
     * null bei Einzelrufnummern</li> <li>Gruppierung auf DN__NO mit höchster DN_NO</li> </ul>
     *
     * @param onkz                   Ortskennzahl
     * @param dnBase                 Rufnummer
     * @param expectBlockIgnoreRange Flag steuert, ob nach Blockrufnummern (true) gesucht werden soll; falls auf true
     *                               gesetzt, wird nach Bloecken gesucht, die rangeFrom/To Information aber ignoriert
     * @param rangeFrom              nur zu setzen, wenn {@code expectBlockIgnoreRange=false} ist; gibt den rangeFrom
     *                               Wert an, ueber den gesucht werden soll
     * @param rangeTo                nur zu setzen, wenn {@code expectBlockIgnoreRange=false} ist; gibt den rangeTo Wert
     *                               an, ueber den gesucht werden soll
     * @param nonBillable            Boolean-Flag, das angibt, ob exclusiv nur non billable rufnummern gesucht werden
     *                               sollen.
     * @return die letzte gültige Taifun Nr. (AUFTRAG_NO_ORIG), das heißt der Rufnummerneintrag mit der höchsten DN_NO.
     */
    private Set<Long> findLatestAuftragIdByOnkzAndDnBase(
            final String onkz,
            final String dnBase,
            final boolean expectBlockIgnoreRange,
            final String rangeFrom,
            final String rangeTo,
            final boolean nonBillable) {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Rufnummer.class);
        if (expectBlockIgnoreRange) {
            criteria.add(Restrictions.isNotNull(Rufnummer.DIRECT_DIAL));
        }
        else if (StringUtils.isNotBlank(rangeFrom)) {
            criteria.add(Restrictions.isNotNull(Rufnummer.DIRECT_DIAL));
            criteria.add(Restrictions.eq(Rufnummer.RANGE_FROM, rangeFrom));
            criteria.add(Restrictions.eq(Rufnummer.RANGE_TO, rangeTo));
        }
        else {
            criteria.add(Restrictions.isNull(Rufnummer.DIRECT_DIAL));
        }

        if (nonBillable) {
            criteria.add(Restrictions.eq(Rufnummer.NON_BILLABLE, true));
        }

        criteria.add(Restrictions.eq(Rufnummer.ONKZ, onkz))
                .add(Restrictions.eq(Rufnummer.DN_BASE, dnBase))
                .add(Restrictions.isNotNull(Rufnummer.AUFTRAG_NO_ORIG))
                .setProjection(Projections.projectionList()
                        .add(Property.forName(Rufnummer.DN_NO).max().as(Rufnummer.DN_NO))
                        .add(Property.forName(Rufnummer.DN_NO_ORIG).group().as(Rufnummer.DN_NO_ORIG)))
                .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

        List dnNos = criteria.list();
        Set<Long> results = new HashSet<>();
        if (dnNos != null && !dnNos.isEmpty()) {
            for (Object dnNo : dnNos) {
                Map entry = (Map) dnNo;
                Long dnNoId = (Long) entry.get(Rufnummer.DN_NO);
                // one query for each dnNoId should be ok, because the data sets are already cached.
                Rufnummer rufnummer = (Rufnummer) session.createCriteria(Rufnummer.class)
                        .add(Restrictions.eq(Rufnummer.DN_NO, dnNoId))
                        .uniqueResult();
                if (rufnummer != null) {
                    results.add(rufnummer.getAuftragNoOrig());
                }
            }

        }
        return results;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
