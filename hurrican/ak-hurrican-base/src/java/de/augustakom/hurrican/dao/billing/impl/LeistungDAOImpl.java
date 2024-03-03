/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 14:12:58
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.LeistungDAO;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungLang;
import de.augustakom.hurrican.model.billing.ServiceBlockPrice;
import de.augustakom.hurrican.model.billing.VatRate;


/**
 * Hibernate DAO-Implementierung fuer Objekte vom Typ <code>Leistung</code>
 *
 *
 */
public class LeistungDAOImpl extends Hibernate4FindDAOImpl implements LeistungDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List queryByExample(Object example, Class type) {
        Example ex = Example.create(example)
                .excludeZeroes()
                .excludeProperty("histLast");

        return getByExampleDAO().queryByCreatedExample(ex, type);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List queryByExample(Object example, Class type, String[] orderParamsAsc, String[] orderParamsDesc) {
        throw new InvalidDataAccessApiUsageException("method not supported on class " + getClass().getName());
    }

    @Override
    public Leistung findLeistungByNoOrig(Long leistungNoOrig) {
        List<Leistung> result = findLeistungen(Collections.singletonList(leistungNoOrig));
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public List<Leistung> findLeistungen(final List<Long> leistungNoOrigs) {
        Session session = sessionFactory.getCurrentSession();
        boolean leistungNosExist = false;
        StringBuilder hql = new StringBuilder("select l.leistungNo, l.leistungNoOrig, l.name, ");
        hql.append("l.oeNoOrig, l.externProduktNo, l.externLeistungNo, l.externMiscNo, ");
        hql.append("l.fibuGebuehrenArt, l.techExport, l.preis, l.preisQuelle, l.vatCode, ");
        hql.append("l.leistungKat, l.billingCode from ");
        hql.append(Leistung.class.getName()).append(" l ");
        hql.append(" where l.histStatus= :histStatus ");
        if (CollectionTools.isNotEmpty(leistungNoOrigs)) {
            hql.append("and l.leistungNoOrig in (:lNoOrigs) ");
            leistungNosExist = true;
        }

        Query query = session.createQuery(hql.toString());
        query.setString("histStatus", BillingConstants.HIST_STATUS_AKT);
        if (leistungNosExist) {
            query.setParameterList("lNoOrigs", leistungNoOrigs, new LongType());
        }

        @SuppressWarnings({ "rawtypes" })
        List result = query.list();
        if (result != null) {
            List<Leistung> retVal = new ArrayList<>();
            for (Object aResult : result) {
                Object[] values = (Object[]) aResult;
                Leistung model = new Leistung();
                model.setLeistungNo(ObjectTools.getLongSilent(values, 0));
                model.setLeistungNoOrig(ObjectTools.getLongSilent(values, 1));
                model.setName(ObjectTools.getStringSilent(values, 2));
                model.setOeNoOrig(ObjectTools.getLongSilent(values, 3));
                model.setExternProduktNo(ObjectTools.getLongSilent(values, 4));
                model.setExternLeistungNo(ObjectTools.getLongSilent(values, 5));
                model.setExternMiscNo(ObjectTools.getLongSilent(values, 6));
                model.setFibuGebuehrenArt(ObjectTools.getStringSilent(values, 7));
                model.setTechExport(ObjectTools.getBooleanSilent(values, 8));
                model.setPreis(ObjectTools.getFloatSilent(values, 9));
                model.setPreisQuelle(ObjectTools.getStringSilent(values, 10));
                model.setVatCode(ObjectTools.getStringSilent(values, 11));
                model.setLeistungKat(ObjectTools.getStringSilent(values, 12));
                model.setBillingCode(ObjectTools.getStringSilent(values, 13));
                retVal.add(model);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public Leistung findLeistungByNoOrigWithoutLang(final Long leistungNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Leistung.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "leistungNoOrig", leistungNoOrig);
        criteria.add(Restrictions.eq("histStatus", BillingConstants.HIST_STATUS_AKT));

        @SuppressWarnings({ "rawtypes" })
        List result = criteria.list();
        return ((result != null) && (result.size() == 1)) ? (Leistung) result.get(0) : null;
    }

    @Override
    public String findRechnungstext(final Long leistungNo, final String value, final String language) {
        final StringBuilder hql = new StringBuilder("select ll.name from ");
        hql.append(LeistungLang.class.getName()).append(" ll where ");
        hql.append("ll.leistungNo= :lNo and trim(ll.language)= :lang ");
        if (value != null) {
            hql.append("and ll.value= :value");
        }

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("lNo", leistungNo);
        query.setString("lang", language);
        if (value != null) {
            query.setString("value", value);
        }

        @SuppressWarnings({ "unchecked" })
        List<String> result = query.list();
        if ((result != null) && (!result.isEmpty())) {
            return result.get(0);
        }

        return null;
    }

    @Override
    public Float findVatRate(final String vatCodeId) {
        final StringBuilder hql = new StringBuilder("select v.vatRate from ");
        hql.append(VatRate.class.getName()).append(" v where ");
        hql.append("v.validFrom <= :date and v.validTo >= :date ");
        hql.append("and v.vatCodeId = :id");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setDate("date", new Date());
        query.setString("id", vatCodeId);

        @SuppressWarnings({ "unchecked" })
        List<Float> result = query.list();
        if ((result != null) && (!result.isEmpty())) {
            return result.get(0);
        }

        return null;
    }

    @Override
    public List<Leistung> findUDRServices4Order(final Long orderNoOrig, final Date validDate) {
        final StringBuilder hql = new StringBuilder("select l.leistungNoOrig from ");
        hql.append(Leistung.class.getName()).append(" l, ");
        hql.append(BAuftragPos.class.getName()).append(" ap where ");
        hql.append("ap.auftragNoOrig= :orderNoOrig and ");
        hql.append("l.leistungNoOrig=ap.leistungNoOrig and l.histLast= :last and ");
        hql.append("l.leistungKat like :udrCategory and l.billingCode like :udrBillCode ");
        hql.append("and ap.chargeFrom<= :validDate and (ap.chargeTo> :validDate or ap.chargeTo is null) ");
        hql.append("group by l.leistungNoOrig");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("orderNoOrig", orderNoOrig);
        query.setString("last", "1");
        query.setString("udrCategory", Leistung.LEISTUNG_CATEGORY_VOLUME + WildcardTools.DB_WILDCARD);
        query.setString("udrBillCode", Leistung.LEISTUNG_BILLING_CODE_PREFIX_UDR + WildcardTools.DB_WILDCARD);
        query.setDate("validDate", validDate);

        @SuppressWarnings({ "unchecked" })
        List<Long> result = query.list();
        if (CollectionTools.isNotEmpty(result)) {
            List<Leistung> retVal = new ArrayList<>();
            for (Long leistungNoOrig : result) {
                retVal.add(findLeistungByNoOrig(leistungNoOrig));
            }
            return retVal;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ServiceBlockPrice> findServiceBlockPrices(final Long leistungNo) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(ServiceBlockPrice.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, "leistungNo", leistungNo);

        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


