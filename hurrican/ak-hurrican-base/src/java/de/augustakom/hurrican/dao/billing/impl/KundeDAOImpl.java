/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 13:16:32
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.hibernate.HibernateInClauseHelper;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.KundeDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;


/**
 * Hibernate DAO-Implementierung, um Modelle vom Typ <code>de.augustakom.hurrican.model.billing.Kunde</code> zu
 * verwalten.
 */
public class KundeDAOImpl extends Hibernate4DAOImpl implements KundeDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Kunde> findByKundeNos(final List<Long> kundeNos) {
        Session session = getSessionFactory().getCurrentSession();
        List<Kunde> result = new ArrayList<>();
        int maxLength = 100;
        int length = kundeNos.size();
        int count = 0;
        while (count < length) {
            int forCount = ((length - count) > maxLength) ? count + maxLength : count + (length - count);
            int arrayLength = ((length - count) > maxLength) ? maxLength : length - count;
            Long[] params = new Long[arrayLength];
            int x = 0;
            for (int i = count; i < forCount; i++) {
                params[x] = kundeNos.get(i);
                x++;
            }

            Criteria criteria = session.createCriteria(Kunde.class);
            criteria.add(Restrictions.in("kundeNo", params));
            @SuppressWarnings("unchecked")
            List<Kunde> tmp = criteria.list();
            result.addAll(tmp);
            count += arrayLength;

            session.clear();
        }
        return result;
    }

    @Override
    public Map<Long, Kunde> findByKundeNos(Long[] kNos) {
        try {
            int maxLength = 100;
            if (kNos.length <= maxLength) {
                Map<Long, Kunde> retVal = new HashMap<>();
                loadByKundeNos(retVal, kNos);
                return retVal;
            }
            Map<Long, Kunde> retVal = new HashMap<>();
            int length = kNos.length;
            int count = 0;
            while (count < length) {
                int forCount = ((length - count) > maxLength) ? count + maxLength : count + (length - count);
                int arrayLength = ((length - count) > maxLength) ? maxLength : length - count;
                Long[] params = new Long[arrayLength];
                int x = 0;
                for (int i = count; i < forCount; i++) {
                    params[x] = kNos[i];
                    x++;
                }
                loadByKundeNos(retVal, params);
                count += arrayLength;
            }
            return retVal;
        }
        catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    /* Laedt alle Kunden mit den angegebenen (original) Kundennummern und
     * speichert sie in der Map <code>kunden</code> ab. <br>
     * Die maximale Laenge von <code>kNos</code> darf 999 sein!
     * @param session die Hibernate-Session.
     * @param kunden Map mit den geladenen Kunden.
     * @param kNos Kundennummern
     * @throws HibernateException
     */
    private void loadByKundeNos(final Map<Long, Kunde> kunden, final Long[] kNos) throws HibernateException {
        Session session = getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Kunde.class);
        criteria.add(Restrictions.in("kundeNo", kNos));
        @SuppressWarnings("unchecked")
        List<Kunde> tmp = criteria.list();

        if (tmp != null) {
            for (Kunde k : tmp) {
                kunden.put(k.getKundeNo(), k);
            }
        }
    }

    @Override
    public List<KundeAdresseView> findByQuery(KundeQuery query) {
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select c.CUST_NO, ca.OLD_CUST_NO, c.PARENT_CUST_NO, c.NAME, c.FIRSTNAME,");
        sql.append(" c.CUSTOMER_TYPE, c.CREDIT_RATING_ID, c.AREA_NO, ar.DESCRIPTION,");
        sql.append(" a.STREET, a.HOUSE_NUM, a.HOUSE_NUM_ADD, a.ZIP_CODE, a.CITY, a.DISTRICT, p.NAME as PERSON_NAME");
        sql.append(" from CUSTOMER c ");
        sql.append(" inner join ADDRESS a on c.POSTAL_ADDR_NO=a.ADDR_NO");
        sql.append(" left join CUSTOMER_ALIAS ca on c.CUST_NO=ca.NEW_CUST_NO");
        sql.append(" left join PERSON p on c.CSR_NO=p.PERSON_NO");
        sql.append(" left join AREA ar on c.AREA_NO=ar.AREA_NO");
        sql.append(" where c.LOCK_DATE IS NULL "); // Gesperrte Kundendaten nicht mehr anzeigen (HUR-2691 - §95 TKG)
        if (StringUtils.isNotBlank(query.getName())) {
            if (WildcardTools.containsWildcard(query.getName())) {
                sql.append(" AND lower(c.NAME) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getName()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(c.NAME) = ? ");
                params.add(query.getName().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getVorname())) {
            if (WildcardTools.containsWildcard(query.getVorname())) {
                sql.append(" AND lower(c.FIRSTNAME) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getVorname()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(c.FIRSTNAME) = ? ");
                params.add(query.getVorname().toLowerCase());
                types.add(new StringType());
            }
        }
        if (query.getKundeNo() != null) {
            sql.append(" AND (c.CUST_NO=? OR ca.OLD_CUST_NO=?)");
            params.add(query.getKundeNo());
            types.add(new LongType());
            params.add(query.getKundeNo());
            types.add(new LongType());
        }
        if (query.getHauptKundenNo() != null) {
            sql.append(" AND c.PARENT_CUST_NO=? ");
            params.add(query.getHauptKundenNo());
            types.add(new LongType());
        }
        if (StringUtils.isNotBlank(query.getStrasse())) {
            if (WildcardTools.containsWildcard(query.getStrasse())) {
                sql.append(" AND lower(a.STREET) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getStrasse()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(a.STREET) = ? ");
                params.add(query.getStrasse().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getPlz())) {
            if (WildcardTools.containsWildcard(query.getPlz())) {
                sql.append(" AND lower(rtrim(a.ZIP_CODE)) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getPlz()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(rtrim(a.ZIP_CODE)) = ? ");
                params.add(query.getPlz().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getOrt())) {
            if (WildcardTools.containsWildcard(query.getOrt())) {
                sql.append(" AND lower(a.CITY) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getOrt()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(a.CITY) = ? ");
                params.add(query.getOrt().toLowerCase());
                types.add(new StringType());
            }
        }
        if (StringUtils.isNotBlank(query.getOrtsteil())) {
            if (WildcardTools.containsWildcard(query.getOrtsteil())) {
                sql.append(" AND lower(a.DISTRICT) like ? ");
                params.add(WildcardTools.replaceWildcards(query.getOrtsteil()).toLowerCase());
                types.add(new StringType());
            }
            else {
                sql.append(" AND lower(a.DISTRICT) = ? ");
                params.add(query.getOrtsteil().toLowerCase());
                types.add(new StringType());
            }
        }

        Session session = getSessionFactory().getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();

        if (result != null) {
            List<KundeAdresseView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                KundeAdresseView view = new KundeAdresseView();
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setOldKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setHauptKundenNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVorname(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundenTyp(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBonitaetId(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAreaNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAreaName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setStrasse(ObjectTools.getStringSilent(values, columnIndex++));
                view.setNummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setHausnummerZusatz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setPlz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setOrt(ObjectTools.getStringSilent(values, columnIndex++));
                view.setOrtsteil(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundenbetreuer(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<Long> findKundeNos4HauptKunde(Long hauptKundeNo) {
        StringBuilder hql = new StringBuilder();
        hql.append("select k.kundeNo from ");
        hql.append(Kunde.class.getName());
        hql.append(" k where k.hauptKundenNo=? ");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), new Object[] { hauptKundeNo });//, BillingConstants.HIST_STATUS_AKT},

        return result;
    }

    @Override
    public Map<Long, String> findKundenNamen(List<Long> kundeNos) {
        Map<Long, String> kunden = new HashMap<>();

        String selectFromWhere = "select name, kundeNo from " + Kunde.class.getName() + " where %s";

        List<Object[]> result = HibernateInClauseHelper.list(getSessionFactory(), selectFromWhere, null, "kundeNo", kundeNos);
        for (Object[] values : result) {
            String kname = ObjectTools.getStringSilent(values, 0);
            Long kId = ObjectTools.getLongSilent(values, 1);
            kunden.put(kId, kname);
        }
        return kunden;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


