/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.text.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.PortStatisticDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.view.PortStatisticsView;

/**
 * Implementierung von {@link PortStatisticDAO}
 */
public class PortStatisticDAOImpl extends Hibernate4FindDAOImpl implements PortStatisticDAO {

    private static final Logger LOGGER = Logger.getLogger(PortStatisticDAOImpl.class);
    private static final String MONTH_FORMAT = "MM-yyyy";

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public int generatePortUsageStatistics() {
        String GATHERING_QUERY = ""
                + " insert into t_port_statistics"
                + " select count(eq.eq_id) as anzahl, BGT.HW_SCHNITTSTELLE_NAME, eq.hw_schnittstelle, AD.PROD_ID, hvt.asb, hg.onkz, n.text as niederlassung, :monat as monat"
                + " from t_equipment eq"
                + " join T_RANGIERUNG r on R.GUELTIG_BIS > :now and R.GUELTIG_VON <= :now and (R.EQ_IN_ID = EQ.EQ_ID or R.EQ_OUT_ID = EQ.EQ_ID)"
                + " JOIN T_ENDSTELLE es ON ES.RANGIER_ID = R.RANGIER_ID OR ES.RANGIER_ID_ADDITIONAL = R.RANGIER_ID"
                + " JOIN T_AUFTRAG_TECHNIK at ON AT.GUELTIG_BIS > :now and AT.GUELTIG_VON <= :now and AT.AT_2_ES_ID = ES.ES_GRUPPE"
                + " JOIN T_AUFTRAG_DATEN ad ON AD.GUELTIG_BIS > :now and AD.GUELTIG_VON <= :now and AD.AUFTRAG_ID = AT.AUFTRAG_ID"
                + " join T_HW_BAUGRUPPE bg on EQ.HW_BAUGRUPPEN_ID = BG.ID"
                + " join T_HW_BAUGRUPPEN_TYP bgt on BGT.ID = BG.HW_BG_TYP_ID"
                + " join t_hvt_standort hvt on HVT.GUELTIG_BIS > :now and HVT.GUELTIG_VON <= :now and EQ.HVT_ID_STANDORT = HVT.HVT_ID_STANDORT"
                + " join T_HVT_GRUPPE hg on HVT.HVT_GRUPPE_ID = HG.HVT_GRUPPE_ID"
                + " join T_NIEDERLASSUNG n on HG.NIEDERLASSUNG_ID = N.ID"
                + " where BGT.HW_SCHNITTSTELLE_NAME in (:adsl, :sdsl, :ab, :uko)"
                + " group by BGT.HW_SCHNITTSTELLE_NAME, eq.hw_schnittstelle, AD.PROD_ID, hvt.asb, hg.onkz, n.text order by hvt.asb, hg.onkz";

        Date now = new Date();
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(GATHERING_QUERY);
        sqlQuery.setParameter("monat", (new SimpleDateFormat(MONTH_FORMAT)).format(now));
        sqlQuery.setDate("now", now);
        sqlQuery.setParameter("adsl", HWBaugruppenTyp.HW_SCHNITTSTELLE_ADSL);
        sqlQuery.setParameter("sdsl", HWBaugruppenTyp.HW_SCHNITTSTELLE_SDSL);
        sqlQuery.setParameter("ab", HWBaugruppenTyp.HW_SCHNITTSTELLE_AB);
        sqlQuery.setParameter("uko", HWBaugruppenTyp.HW_SCHNITTSTELLE_UK0);
        return sqlQuery.executeUpdate();
    }

    @Override
    public List<PortStatisticsView> retrievePortStatistics() {
        String sql = "SELECT ANZAHL, KARTEN_TYP, PORT_TYP, PROD_ID, ASB, ONKZ, NIEDERLASSUNG, MONAT "
                + "FROM T_PORT_STATISTICS "
                + "ORDER BY NIEDERLASSUNG, ONKZ, ASB, MONAT";

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        List<Object[]> queryResults = sqlQuery.list();
        List<StatisticsItem> items = new ArrayList<>();
        if (queryResults != null) {
            for (Object[] values : queryResults) {
                StatisticsItem item = new StatisticsItem();
                item.anzahl = ObjectTools.getLongSilent(values, 0);
                item.kartenTyp = ObjectTools.getStringSilent(values, 1);
                item.portTyp = ObjectTools.getStringSilent(values, 2);
                item.productId = ObjectTools.getLongSilent(values, 3);
                item.asb = ObjectTools.getIntegerSilent(values, 4);
                item.onkz = ObjectTools.getStringSilent(values, 5);
                item.niederlassung = ObjectTools.getStringSilent(values, 6);
                item.monat = ObjectTools.getStringSilent(values, 7);
                items.add(item);
            }
        }

        List<PortStatisticsView> result = new ArrayList<>();
        Map<String, Map<String, PortStatisticsView>> aggregated = new HashMap<>();
        for (StatisticsItem item : items) {
            String key = item.niederlassung + "-" + item.onkz + "-" + item.asb;
            Map<String, PortStatisticsView> grouped = aggregated.get(key);
            if (grouped == null) {
                grouped = new HashMap<>();
                aggregated.put(key, grouped);
            }
            PortStatisticsView forMonth = grouped.get(item.monat);
            if (forMonth == null) {
                forMonth = new PortStatisticsView();
                forMonth.setMonat(item.monat);
                forMonth.setNiederlassung(item.niederlassung);
                forMonth.setAsb(item.asb);
                forMonth.setOnkz(item.onkz);
                grouped.put(item.monat, forMonth);
                result.add(forMonth);
            }
            countItem(item, forMonth);
        }
        for (Map.Entry<String, Map<String, PortStatisticsView>> grouped : aggregated.entrySet()) {
            for (Map.Entry<String, PortStatisticsView> forMonth : grouped.getValue().entrySet()) {
                String month = forMonth.getKey();
                PortStatisticsView view = forMonth.getValue();
                String previousMonth = getPreviousMonth(month);
                PortStatisticsView old = grouped.getValue().get(previousMonth);
                if (old == null) {
                    continue;
                }
                long telephonyDiff = view.getTelephonySumme() - old.getTelephonySumme();
                view.setTelephonyDiff(telephonyDiff);
                if (old.getTelephonySumme() != 0) {
                    view.setTelephonyProzent(((100 * telephonyDiff) / (float) old.getTelephonySumme()));
                }
                long adslDiff = view.getAdslPorts() - old.getAdslPorts();
                view.setAdslDiff(adslDiff);
                if (old.getAdslPorts() != 0) {
                    view.setAdslProzent((100 * adslDiff) / (float) old.getAdslPorts());
                }
            }
        }
        return result;
    }

    private String getPreviousMonth(String month) {
        SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
        Date date;
        try {
            date = sdf.parse(month);
        }
        catch (ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return sdf.format(cal.getTime());
    }

    private void countItem(StatisticsItem item, PortStatisticsView forMonth) {
        if (Produkt.PROD_ID_PMX_LEITUNG.equals(item.productId) || Produkt.PROD_ID_ISDN_PMX.equals(item.productId)) {
            forMonth.addPmxPorts(item.anzahl);
        }
        else if (Produkt.PROD_ID_ISDN_TK.equals(item.productId)) {
            forMonth.addIsdnTkPorts(item.anzahl);
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_AB.equals(item.kartenTyp)) {
            forMonth.addAnalogPorts(item.anzahl);
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_UK0.equals(item.kartenTyp)) {
            forMonth.addIsdnPorts(item.anzahl);
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_ADSL.equals(item.kartenTyp) && Equipment.HW_SCHNITTSTELLE_ADSL_IN.equals(item.portTyp)) {
            forMonth.addAdslPorts(item.anzahl);
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_SDSL.equals(item.kartenTyp)) {
            forMonth.addSdslPorts(item.anzahl);
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    static class StatisticsItem {
        long anzahl;
        String kartenTyp;
        String portTyp;
        Long productId;
        Integer asb;
        String onkz;
        String niederlassung;
        String monat;
    }
}
