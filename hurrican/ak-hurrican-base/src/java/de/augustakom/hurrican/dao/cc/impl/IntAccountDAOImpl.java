/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2004 15:09:44
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.IntAccountDAO;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Hibernate DAO-Implementierung von <code>IntAccountDAO</code>.
 *
 *
 */
public class IntAccountDAOImpl extends HurricanHibernateDaoImpl implements IntAccountDAO {

    private static final String SQL_GET_NEXT_ACCOUNT_VALUE = "SELECT SEQ_ACCOUNT.NEXTVAL FROM DUAL";
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public IntAccount findAccount(final String account, final Integer typ, final LocalDate when) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IntAccount.class.getName());
        hql.append(" acc where acc.account= :account ");
        if (typ != null) {
            hql.append(" and acc.liNr= :liNr ");
        }
        hql.append(" and acc.gueltigVon<= :when  and acc.gueltigBis> :when");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setString("account", account);
        q.setDate("when", DateConverterUtils.asDate(when));
        if (typ != null) {
            q.setInteger("liNr", typ.intValue());
        }

        List<?> result = q.list();
        return (result != null) && !result.isEmpty() ? (IntAccount) result.get(result.size() - 1) : null;
    }

    @Override
    public List<IntAccount> findByAuftragIdAndTyp(final Long ccAuftragId, final Integer accountTyp) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select acc.id from ");
        hql.append(IntAccount.class.getName()).append(" acc, ");
        hql.append(AuftragTechnik.class.getName()).append(" at ");
        hql.append(" where at.auftragId= :aId and at.intAccountId=acc.id and ");
        hql.append(" at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and (acc.gueltigVon is null or acc.gueltigVon<= :now) ");
        hql.append(" and (acc.gueltigBis is null or acc.gueltigBis> :now)");
        if (accountTyp != null) {
            hql.append(" and acc.liNr= :liNr");
        }

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", ccAuftragId);
        q.setDate("now", now);
        if (accountTyp != null) {
            q.setInteger("liNr", accountTyp);
        }

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (CollectionTools.isEmpty(result)) {
            return ImmutableList.of();
        }
        List<IntAccount> retVal = new ArrayList<>(result.size());
        for (Long accountId : result) {
            retVal.add(findById(accountId, IntAccount.class));
        }
        return retVal;
    }

    @Override
    public List<AuftragIntAccountView> findAuftragAccountViews(Long kundeNo, List<Long> produktGruppen) {
        Date now = DateTools.getActualSQLDate();
        List<Object> params = new ArrayList<>();
        List<Type> types = new ArrayList<>();

        CollectionUtils.addAll(params, new Object[] {
                kundeNo, now, now, now, now, now, now,
                AuftragStatus.KUENDIGUNG, AuftragStatus.ABSAGE,
                IntAccount.LINR_ABRECHNUNGSACCOUNT, Produkt.PROD_ID_AKSDSL_256, Produkt.PROD_ID_AKSDSL_1024,
                Produkt.PROD_ID_AKSDSL_512, Produkt.PROD_ID_AKSDSL_2300,
                IntAccount.LINR_EINWAHLACCOUNT, IntAccount.LINR_EINWAHLACCOUNT_KONFIG });

        CollectionUtils.addAll(types, new Type[] {
                new LongType(), new DateType(), new DateType(), new DateType(), new DateType(), new DateType(), new DateType(),
                new LongType(), new LongType(),
                new IntegerType(), new LongType(), new LongType(),
                new LongType(), new LongType(),
                new IntegerType(), new IntegerType() });

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT acc.ID as ACC_ID, acc.ACCOUNT, acc.PASSWORT, acc.RUFNUMMER, acc.LI_NR, ");
        sql.append(" a.ID as AUFTRAG_ID, ad.PRODAK_ORDER__NO, ad.PROD_ID, at.VPN_ID, p.ANSCHLUSSART, t.TDN ");
        sql.append(" FROM t_auftrag a");
        sql.append(" INNER JOIN t_auftrag_technik at on a.id=at.auftrag_id");
        sql.append(" LEFT JOIN t_tdn t on at.tdn_id=t.id");
        sql.append(" INNER JOIN t_auftrag_daten ad on a.id=ad.auftrag_id");
        sql.append(" INNER JOIN t_int_account acc ON at.int_account_id=acc.ID");
        sql.append(" INNER JOIN t_produkt p ON ad.prod_ID=p.prod_id");
        sql.append(" WHERE a.KUNDE__NO=? ");
        sql.append(" AND at.GUELTIG_VON<=? AND at.GUELTIG_BIS>? ");
        sql.append(" AND ad.GUELTIG_VON<=? AND ad.GUELTIG_BIS>? ");
        sql.append(" AND acc.GUELTIG_VON<=? AND acc.GUELTIG_BIS>? ");
        sql.append(" AND ad.STATUS_ID<? AND ad.STATUS_ID>? ");
        sql.append(" AND (acc.GESPERRT IS NULL OR acc.GESPERRT<>1) ");
        sql.append(" AND (acc.LI_NR=? AND ad.PROD_ID IN (?,?,?,?)");
        sql.append(" OR ((acc.LI_NR=? OR acc.LI_NR=?) AND (at.VPN_ID is null OR at.VPN_ID=0)))");
        if (!CollectionTools.isEmpty(produktGruppen)) {
            sql.append(" AND p.PRODUKTGRUPPE_ID IN (");
            for (int i = 0; i < produktGruppen.size(); i++) {
                if (i > 0) { sql.append(","); }
                Long pgId = produktGruppen.get(i);
                sql.append("?");
                params.add(pgId);
                types.add(new LongType());
            }
            sql.append(") ");
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<AuftragIntAccountView> views = new ArrayList<>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                AuftragIntAccountView view = new AuftragIntAccountView();
                view.setAccountId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAccount(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAccountPasswort(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAccountRufnummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAccountLiNr(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVpnId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                views.add(view);
            }
        }

        return views;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.IntAccountDAO#getNextAccountValue()
     */
    @Override
    public Long getNextAccountValue() {
        return ((BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(SQL_GET_NEXT_ACCOUNT_VALUE).uniqueResult()).longValue();
    }

    @Override
    public IntAccount update4History(IntAccount obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new IntAccount(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

