/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 07:55:05
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.ConsistenceCheckDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.consistence.HistoryConsistence;

/**
 * DAO-Implementierung von <code>ConsistenceCheckDAO</code>.
 *
 *
 */
public class ConsistenceCheckDAOImpl implements ConsistenceCheckDAO {

    private static final Logger LOGGER = Logger.getLogger(ConsistenceCheckDAOImpl.class);

    private static final String AUFTRAG_DATEN =
            "select count(*) as ANZAHL, AUFTRAG_ID as ID from T_AUFTRAG_DATEN where GUELTIG_BIS>? group by AUFTRAG_ID having count(*)>1";
    private static final String AUFTRAG_TECHNIK =
            "select count(*) as ANZAHL, AUFTRAG_ID as ID from T_AUFTRAG_TECHNIK where GUELTIG_BIS>? group by AUFTRAG_ID having count(*)>1";
    private static final String RANGIERUNG_EQ_IN =
            "select count(*) as ANZAHL, EQ_IN_ID as ID, 'EQ_IN_ID: '||EQ_IN_ID as HINT from T_RANGIERUNG "
                    + "where GUELTIG_BIS>? and EQ_IN_ID is not null and FREIGEGEBEN='1' group by EQ_IN_ID having count(*)>1";
    private static final String RANGIERUNG_EQ_OUT =
            "select count(*) as ANZAHL, EQ_OUT_ID as ID, 'EQ_OUT_ID: '||EQ_OUT_ID as HINT from T_RANGIERUNG "
                    + "where GUELTIG_BIS>? and PHYSIK_TYP<>802 and EQ_OUT_ID is not null and FREIGEGEBEN='1' group by EQ_OUT_ID having count(*)>1";
    private static final String INT_ACCOUNT =
            "select count(*) as ANZAHL, ACCOUNT as ID, LI_NR from T_INT_ACCOUNT where GUELTIG_BIS>? group by ACCOUNT, LI_NR having count(*)>1";
    private static final String ENDSTELLE_LTG_DATEN =
            "select count(*) as ANZAHL, ES_ID as ID from T_ES_LTG_DATEN where GUELTIG_BIS>? group by ES_ID having count(*)>1";
    private static final String ENDSTELLE_ANSPRECHPARTNER =
            "select count(*) as ANZAHL, ES_ID as ID from T_ES_ANSP where GUELTIG_BIS>? group by ES_ID having count(*)>1";

    private static Map<Class, HistoryConsistenceInput> hcMap = new HashMap<Class, HistoryConsistenceInput>() {{// NOSONAR squid:S1171 ; could not done with ImmutableMap
        put(AuftragDaten.class, new HistoryConsistenceInput("T_AUFTRAG_DATEN", "AUFTRAG_ID", AUFTRAG_DATEN));
        put(AuftragTechnik.class, new HistoryConsistenceInput("T_AUFTRAG_TECHNIK", "AUFTRAG_ID", AUFTRAG_TECHNIK));
        put(Rangierung.class, new HistoryConsistenceInput("T_RANGIERUNG", "<not-supported>", RANGIERUNG_EQ_IN, RANGIERUNG_EQ_OUT));
        put(IntAccount.class, new HistoryConsistenceInput("T_INT_ACCOUNT", "ACCOUNT", INT_ACCOUNT));
        put(EndstelleLtgDaten.class, new HistoryConsistenceInput("T_ES_LTG_DATEN", "ES_ID", ENDSTELLE_LTG_DATEN));
        put(EndstelleAnsprechpartner.class, new HistoryConsistenceInput("T_ES_ANSP", "ES_ID", ENDSTELLE_ANSPRECHPARTNER));
    }};
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.ConsistenceCheckDAO#checkHistoryConsistence(java.lang.Class)
     */
    public List<HistoryConsistence> checkHistoryConsistence(Class type) {
        HistoryConsistenceInput historyConsistenceInput = hcMap.get(type);
        if (historyConsistenceInput == null) {
            throw new IllegalArgumentException(String.format("Unsupported class '%s' for consistence check!",
                    type.getCanonicalName()));
        }
        return checkHistoryConsistence(historyConsistenceInput);
    }

    private List<HistoryConsistence> checkHistoryConsistence(HistoryConsistenceInput hci) {
        Session session = sessionFactory.getCurrentSession();
        List<HistoryConsistence> retVal = new ArrayList<>();
        for (String sql : hci.sqls) {
            LOGGER.debug("SQL: " + sql);

            SQLQuery sqlQuery = session.createSQLQuery(sql);
            sqlQuery.setParameters(
                    new Object[] { new Date() },
                    new Type[] { new DateType() }
            );
            List<Object[]> result = sqlQuery.list();
            if (result != null) {
                for (Object[] values : result) {
                    HistoryConsistence hc = new HistoryConsistence();
                    hc.setAnzahl(ObjectTools.getIntegerSilent(values, 0));
                    hc.setId(ObjectTools.getObjectSilent(values, 1, Object.class));
                    hc.setIdType(hci.idName);
                    hc.setTable(hci.tableName);
                    hc.setHinweis(ObjectTools.getStringSilent(values, 2));
                    retVal.add(hc);
                }
            }
        }
        return retVal;
    }

    /**
     * @see de.augustakom.hurrican.dao.cc.ConsistenceCheckDAO#findMultipleUsedIntAccounts()
     */
    public List<IntAccount> findMultipleUsedIntAccounts() {
        List<String> ids = findMultipleUsedIntAccountIds();
        if (ids != null) {
            List<IntAccount> retVal = new ArrayList<>();
            String hql = "from " + IntAccount.class.getName() + " where account = :account";
            Session currentSession = sessionFactory.getCurrentSession();
            for (String account : ids) {
                Query query = currentSession.createQuery(hql);
                query.setString("account", account);
                @SuppressWarnings("unchecked")
                List<IntAccount> accs = (List<IntAccount>) query.list();
                if (CollectionTools.isNotEmpty(accs)) {
                    retVal.add(accs.get(0));
                }
            }
            return retVal;
        }
        return null;
    }

    /*
     * Ermittelt die IDs der mehrfach verwendeten IntAccounts.
     */
    protected List<String> findMultipleUsedIntAccountIds() {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery("SELECT ACCOUNT FROM T_INT_ACCOUNT acc "
                + "INNER JOIN T_AUFTRAG_TECHNIK at ON acc.id=at.INT_ACCOUNT_ID "
                + "INNER JOIN T_AUFTRAG_DATEN ad ON at.AUFTRAG_ID=ad.AUFTRAG_ID "
                + "WHERE acc.GUELTIG_VON<=:now AND acc.GUELTIG_BIS>:now "
                + "   AND at.GUELTIG_VON<=:now AND at.GUELTIG_BIS>:now "
                + "   AND ad.GUELTIG_VON<=:now AND ad.GUELTIG_BIS>:now "
                + "   AND acc.KUENDIGUNGSDATUM IS NULL AND ad.STATUS_ID>=:statusIdFrom AND ad.STATUS_ID<:statusIdTo "
                + "GROUP BY ACCOUNT HAVING count(*)>1");
        sqlQuery.setDate("now", new Date());
        sqlQuery.setLong("statusIdFrom", AuftragStatus.IN_BETRIEB);
        sqlQuery.setLong("statusIdTo", AuftragStatus.KUENDIGUNG);
        return (List<String>) sqlQuery.list();
    }

    private static final class HistoryConsistenceInput {
        final String tableName;
        final String idName;
        final List<String> sqls;

        protected HistoryConsistenceInput(String tableName, String idName, String... sqls) {
            this.tableName = tableName;
            this.idName = idName;
            this.sqls = Arrays.asList(sqls);
        }
    }

}
