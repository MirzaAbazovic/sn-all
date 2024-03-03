/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 15:55:11
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.sql.Date;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.IABudgetDAO;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;


/**
 * Hibernate DAO-Implementierung von <code>IABudgetDAO</code>.
 *
 *
 */
public class IABudgetDAOImpl extends Hibernate4DAOImpl implements IABudgetDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;


    @Override
    public void delete(IaLevel1 toDelete) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + IaLevel1.class.getName() + " l where l.id=?");
        query.setParameter(0, toDelete.getId());
        query.executeUpdate();
    }


    /**
     * @see de.augustakom.hurrican.dao.cc.IABudgetDAO#findIAViews(de.augustakom.hurrican.model.shared.view.InnenauftragQuery)
     */
    public List<InnenauftragView> findIAViews(InnenauftragQuery query) {
        List params = new ArrayList();
        List<Type> types = new ArrayList<>();
        Date now = DateTools.getActualSQLDate();
        CollectionUtils.addAll(params, new Object[] { now, now, now, now, now, now });
        CollectionUtils.addAll(types, new Type[] { new DateType(), new DateType(), new DateType(), new DateType(), new DateType(), new DateType() });

        StringBuilder sql = new StringBuilder();
        sql.append("select a.ID as AUFTRAG_ID, a.KUNDE__NO, ad.INBETRIEBNAHME, ad.STATUS_ID, ");
        sql.append("t.TDN, p.PROD_ID, p.ANSCHLUSSART, p.PROD_NAME_PATTERN, ast.STATUS_TEXT, ");
        sql.append("i.IA_NUMMER, ai.BEDARFSNUMMER, r.STR_VALUE as WORKING_TYPE, ad.BEMERKUNGEN as AD_BEMERKUNG ");
        sql.append(" from T_AUFTRAG a ");
        sql.append(" inner join T_AUFTRAG_DATEN ad on a.ID = ad.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_TECHNIK at on a.ID = at.AUFTRAG_ID ");
        sql.append(" inner join T_AUFTRAG_STATUS ast on ad.STATUS_ID = ast.ID ");
        sql.append(" left join T_TDN t on at.TDN_ID = t.ID ");
        sql.append(" left join T_PRODUKT p on ad.PROD_ID = p.PROD_ID ");
        sql.append(" left join T_IA i on i.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_AUFTRAG_INTERN ai on ai.AUFTRAG_ID=a.ID ");
        sql.append(" left join T_REFERENCE r on ai.WORKING_TYPE_REF_ID=r.ID ");
        sql.append(" where at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");
        sql.append(" and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ");
        sql.append(" and ((ai.GUELTIG_VON is null or ai.GUELTIG_VON<=?) and (ai.GUELTIG_BIS is null or ai.GUELTIG_BIS>?)) ");
        if (StringUtils.isNotBlank(query.getIaNummer())) {
            sql.append(" and lower(i.IA_NUMMER) like ?");
            params.add(WildcardTools.replaceWildcards(query.getIaNummer().toLowerCase()));
            types.add(new StringType());
        }
        if (StringUtils.isNotBlank(query.getBedarfsNr())) {
            sql.append(" and lower(ai.BEDARFSNUMMER) like ?");
            params.add(WildcardTools.replaceWildcards(query.getBedarfsNr().toLowerCase()));
            types.add(new StringType());
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<InnenauftragView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                int columnIndex = 0;
                InnenauftragView view = new InnenauftragView();
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setInbetriebnahme(ObjectTools.getDateSilent(values, columnIndex++));
                view.setAuftragStatusId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setVbz(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAnschlussart(ObjectTools.getStringSilent(values, columnIndex++));
                view.setProdNamePattern(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragStatusText(ObjectTools.getStringSilent(values, columnIndex++));
                view.setIaNummer(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBedarfsNr(ObjectTools.getStringSilent(values, columnIndex++));
                view.setWorkingType(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragBemerkung(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }

            return retVal;
        }

        return null;
    }

    public String fetchInnenAuftragKostenstelle(Long auftragId) {
        final String sqlStr = ""
                + "select g.KOSTENSTELLE \n"
                + "from T_AUFTRAG_INTERN ti \n"
                + "inner join T_HVT_STANDORT st on st.HVT_ID_STANDORT = ti.HVT_ID_STANDORT \n"
                + "inner join T_HVT_GRUPPE g on g.HVT_GRUPPE_ID = st.HVT_GRUPPE_ID \n"
                + "where ti.AUFTRAG_ID = ? \n";
        final Session session = sessionFactory.getCurrentSession();
        final SQLQuery sqlQuery = session.createSQLQuery(sqlStr);
        final List<Object> result = sqlQuery.setLong(0, auftragId).list();
        if (result != null && !result.isEmpty()) {
            return (String )result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


