/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 10:02:54
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.Auftrag2TechLeistungDAO;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.mnet.common.tools.DateConverterUtils;


/**
 * Hibernate DAO-Implementierung von <code>AuftragTechLeistungDAO</code>.
 *
 *
 */
public class Auftrag2TechLeistungDAOImpl extends Hibernate4DAOImpl implements Auftrag2TechLeistungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen(final Long ccAuftragId, final Long techLeistungId, final boolean onlyAct) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Auftrag2TechLeistung.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragId", ccAuftragId);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "techLeistungId", techLeistungId);
        if (onlyAct) {
            crit.add(Restrictions.or(
                    Restrictions.isNull("aktivBis"),
                    Restrictions.ge("aktivBis", DateTools.getHurricanEndDate())));
        }

        return crit.list();
    }

    @Override
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen(final Long ccAuftragId, final Long[] techLeistungIds, final Date validDate, final boolean ignoreAktivVon) {
        Session session = sessionFactory.getCurrentSession();
        Date validDateTrunc = DateUtils.truncate(validDate, Calendar.DAY_OF_MONTH);

        Criteria crit = session.createCriteria(Auftrag2TechLeistung.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "auftragId", ccAuftragId);
        crit.add(Restrictions.in("techLeistungId", techLeistungIds));
        if (!ignoreAktivVon) {
            CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "aktivVon", validDateTrunc);
        }
        crit.add(Restrictions.or(
                Restrictions.gt("aktivBis", validDateTrunc),
                Restrictions.isNull("aktivBis")));

        return crit.list();
    }

    @Override
    @Nonnull
    public List<Auftrag2TechLeistung> findActiveA2TLGrouped(Long auftragId, LocalDate checkDate) {
        StringBuilder hql = new StringBuilder("select a.auftragId, a.techLeistungId, ");
        hql.append(" sum(a.quantity), a.aktivVon, a.aktivBis from ");
        hql.append(Auftrag2TechLeistung.class.getName()).append(" a where ");
        hql.append(" a.auftragId=? and (a.aktivBis is null or a.aktivBis > ?) ");
        hql.append(" group by a.auftragId, a.techLeistungId, a.aktivVon, a.aktivBis ");

        List<Object[]> result = find(
                hql.toString(), new Object[] { auftragId, DateConverterUtils.asDate(checkDate) });
        if (CollectionTools.isNotEmpty(result)) {
            ImmutableList.Builder<Auftrag2TechLeistung> retVal = ImmutableList.builder();
            for (Object[] values : result) {
                Auftrag2TechLeistung model = new Auftrag2TechLeistung();
                model.setAuftragId(ObjectTools.getLongSilent(values, 0));
                model.setTechLeistungId(ObjectTools.getLongSilent(values, 1));
                model.setQuantity(ObjectTools.getLongSilent(values, 2));
                model.setAktivVon(ObjectTools.getDateSilent(values, 3));
                model.setAktivBis(ObjectTools.getDateSilent(values, 4));
                retVal.add(model);
            }
            return retVal.build();
        }

        return ImmutableList.of();
    }

    @Override
    @Nonnull
    public List<Auftrag2TechLeistung> findActiveA2TLGrouped(Long auftragId) {
        return findActiveA2TLGrouped(auftragId, LocalDate.now());
    }

    @Override
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen4Verlauf(final Long verlaufId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Auftrag2TechLeistung.class);
        crit.add(Restrictions.or(
                Restrictions.eq("verlaufIdReal", verlaufId),
                Restrictions.eq("verlaufIdKuend", verlaufId)));

        return crit.list();
    }

    @Override
    public void delete(Auftrag2TechLeistung leistung) {
        sessionFactory.getCurrentSession().delete(leistung);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


