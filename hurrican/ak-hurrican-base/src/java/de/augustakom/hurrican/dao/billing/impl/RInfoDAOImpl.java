/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2006 11:37:02
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.RInfoDAO;
import de.augustakom.hurrican.dao.billing.tools.BillingDAOTools;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;

/**
 * Hibernate DAO-Implementierung von <code>RInfoExportDAO</code>
 *
 *
 */
public class RInfoDAOImpl extends Hibernate4FindDAOImpl implements RInfoDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<RInfo> findRInfo4KundeNo(final Long kundeNo) {
        if (kundeNo == null) {
            return null;
        }
        Session session = getSessionFactory().getCurrentSession();
        Query q = session.createQuery("from " + RInfo.class.getName() + " r"
                + " where r.kundeNo = :kundeNo" + " order by r.rinfoNo asc");
        q.setLong("kundeNo", kundeNo);

        @SuppressWarnings("unchecked")
        List<RInfo> result = q.list();
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }

    @Override
    public List<RInfoAdresseView> findKundenByRInfoQuery(RInfoQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        // Abfrage auf histStatus = akt
        StringBuilder sql = new StringBuilder();
        sql.append("select c.CUST_NO, c.PARENT_CUST_NO, c.NAME, c.FIRSTNAME,");
        sql.append(" c.CUSTOMER_TYPE, c.CREDIT_RATING_ID, c.AREA_NO, ar.DESCRIPTION,");
        sql.append(" a.STREET, a.HOUSE_NUM, a.HOUSE_NUM_ADD, a.ZIP_CODE, a.CITY, a.DISTRICT, p.NAME as PERSON_NAME,");
        sql.append(" r.BILL_SPEC_NO, r.EXT_DEBITOR_ID");
        sql.append(" from CUSTOMER c ");
        sql.append(" inner join BILL_SPEC r on c.CUST_NO=r.CUST_NO");
        sql.append(" inner join ADDRESS a on r.INV_ADDR_NO=a.ADDR_NO");
        sql.append(" inner join PERSON p on c.CSR_NO=p.PERSON_NO");
        sql.append(" left join AREA ar on c.AREA_NO=ar.AREA_NO");
        sql.append(" where c.IS_ACTIVE = ? AND r.IS_ACTIVE = ? AND a.IS_ACTIVE = ? and c.LOCK_DATE is null");
        params.add(Boolean.TRUE);
        types.add(new BooleanType());
        params.add(Boolean.TRUE);
        types.add(new BooleanType());
        params.add(Boolean.TRUE);
        types.add(new BooleanType());
        if (StringUtils.isNotBlank(query.getSapDebitorNo())) {
            if (WildcardTools.containsWildcard(query.getSapDebitorNo())) {
                sql.append(" AND lower(r.EXT_DEBITOR_ID) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getSapDebitorNo()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(r.EXT_DEBITOR_ID) = ? ");
                params.add(query.getSapDebitorNo().toLowerCase());
                types.add(new StringType());
            }
        }
        if (query.getRInfoNoOrig() != null) {
            sql.append(" AND r.BILL_SPEC_NO=? ");
            params.add(query.getRInfoNoOrig());
            types.add(new LongType());
        }

        if (query.getKundeNo() != null) {
            sql.append(" AND c.CUST_NO=? ");
            params.add(query.getKundeNo());
            types.add(new LongType());
        }

        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();

        if (result != null) {
            List<RInfoAdresseView> retVal = new ArrayList<RInfoAdresseView>();
            for (Object[] aResult : result) {
                int columnIndex = 0;
                RInfoAdresseView view = new RInfoAdresseView();
                view.setKundeNo(ObjectTools.getLongSilent(aResult, columnIndex++));
                view.setHauptKundenNo(ObjectTools.getLongSilent(aResult, columnIndex++));
                view.setName(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setVorname(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setKundenTyp(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setBonitaetId(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setAreaNo(ObjectTools.getLongSilent(aResult, columnIndex++));
                view.setAreaName(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setStrasse(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setNummer(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setHausnummerZusatz(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setPlz(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setOrt(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setOrtsteil(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setKundenbetreuer(ObjectTools.getStringSilent(aResult, columnIndex++));
                view.setRInfoNoOrig(ObjectTools.getLongSilent(aResult, columnIndex++));
                view.setExtDebitorNo(ObjectTools.getStringSilent(aResult, columnIndex));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<RInfo2KundeView> findRInfo2KundeViews(Long billRunId, String year, String month) {
        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery("select r.BILL_SPEC_NO, r.EXT_DEBITOR_ID, "
                + "r.CUST_NO, k.RESELLER_CUST_NO, k.AREA_NO, r.INV_MAXI "
                + "from BILL_SPEC r, CUSTOMER k, BIE_BILL_" + year + month + " stream "
                + "where stream.BILL_SPEC_NO=r.BILL_SPEC_NO and r.CUST_NO=k.CUST_NO "
                + "and stream.RUN_NO=?");
        sqlQuery.setParameters(new Object[] { billRunId }, new Type[] { new LongType() });
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<RInfo2KundeView> retVal = new ArrayList<RInfo2KundeView>();
            for (Object[] values : result) {
                int columnIndex = 0;
                RInfo2KundeView mdl = new RInfo2KundeView();
                Long rfInfoNo = ObjectTools.getLongSilent(values, columnIndex++);
                mdl.setRinfoNo(rfInfoNo);
                mdl.setRinfoNoOrig(rfInfoNo);
                mdl.setExtDebitorId(ObjectTools.getStringSilent(values, columnIndex++));
                mdl.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                mdl.setResellerKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                mdl.setAreaNo(ObjectTools.getLongSilent(values, columnIndex++));
                mdl.setInvMaxi(ObjectTools.getBooleanSilent(values, columnIndex));
                retVal.add(mdl);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public RInfo2KundeView findRInfo2KundeView4BillId(String billId, String year, String month) {
        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery("select r.BILL_SPEC_NO, r.EXT_DEBITOR_ID, "
                + "r.CUST_NO, k.RESELLER_CUST_NO, k.AREA_NO, r.INV_MAXI, "
                + "a.NAME, a.FIRSTNAME, a.STREET, a.PO_BOX, a.ZIP_CODE, a.CITY, a.HOUSE_NUM "
                + "from BILL_SPEC r, CUSTOMER k, BIE_BILL_" + year + month + " stream, ADDRESS a "
                + "where stream.BILL_SPEC_NO=r.BILL_SPEC_NO and r.CUST_NO=k.CUST_NO "
                + "and r.INV_ADDR_NO=a.ADDR_NO "
                + "and stream.BILL_ID=:billId");
        sqlQuery.setParameter("billId", billId);
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();

        if (result != null) {
            if (result.size() != 1) {
                throw new IncorrectResultSizeDataAccessException(
                        "Anzahl gefundener RInfo2KundeViews ungueltig! expected: 1; found: " + result.size(),
                        1, result.size());
            }

            int columnIndex = 0;
            Object[] values = result.get(0);
            RInfo2KundeView retVal = new RInfo2KundeView();
            Long rfInfoNo = ObjectTools.getLongSilent(values, columnIndex++);
            retVal.setRinfoNo(rfInfoNo);
            retVal.setRinfoNoOrig(rfInfoNo);
            retVal.setExtDebitorId(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
            retVal.setResellerKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
            retVal.setAreaNo(ObjectTools.getLongSilent(values, columnIndex++));
            retVal.setInvMaxi(ObjectTools.getBooleanSilent(values, columnIndex++));
            retVal.setAddressName(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressVorname(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressStrasse(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressPostfach(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressPLZ(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressOrt(ObjectTools.getStringSilent(values, columnIndex++));
            retVal.setAddressNr(ObjectTools.getStringSilent(values, columnIndex));
            return retVal;
        }
        return null;
    }

    @Override
    public Date findInvoiceDate(Long billRunId) {
        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(
                "select INVOICE_DATE from "
                        + BillingDAOTools.getSchemaName() + "BIE_RUN " + "where RUN_NO=:billRunId");
        sqlQuery.setParameter("billRunId", billRunId, StandardBasicTypes.LONG);
        return (Date) sqlQuery.uniqueResult();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}