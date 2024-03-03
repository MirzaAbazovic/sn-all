/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2007 16:41:16
 */
package de.augustakom.hurrican.service.billing.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Jasper DataSource, um eine Statistik der Druck-Files zu erstellen.
 *
 *
 */
public class PrintStatisticJasperDS extends AbstractBillingJasperDS {

    private static final Logger LOGGER = Logger.getLogger(PrintStatisticJasperDS.class);

    private Long printSetNo = null;
    private String billCycle = null;

    private List<GroupedSummary> summaries = null;
    private Iterator<GroupedSummary> dataIterator = null;
    private GroupedSummary currentData = null;

    private String[] groupNames = new String[] {
            "%Normalversand.02%",
            "%Normalversand.04%",
            "%Normalversand.06%",
            "%Normalversand.08%",
            "%Normalversand.10%",
            "%Normalversand.12%",
            "%Normalversand.14%",
            "%Normalversand.XL%",
            "%Spezialversand%",
            "%Werbefreiversand%",
    };

    /**
     * Konstruktor mit Angabe der PrintSet-No, zu der die Statistik erstellt werden soll.
     *
     * @param printSetNo ID des PrintSets dessen Details ermittelt werden sollen.
     * @param billCycle  Name des Rechnungslaufs (z.B. Januar 2007) der auf der Statistik angedruckt werden soll.
     */
    public PrintStatisticJasperDS(Long printSetNo, String billCycle) throws AKReportException {
        super();
        this.printSetNo = printSetNo;
        this.billCycle = billCycle;
        init();
    }

    /* Laedt die Report-Daten. */
    private void init() throws AKReportException {
        try {
            summaries = new ArrayList<>();

            RechnungsService rs = getBillingService(RechnungsService.class);
            for (String gn : groupNames) {
                Integer[] sums = rs.sumPagesAndBills(printSetNo, gn);

                if ((sums != null) && (sums.length == 2)) {
                    GroupedSummary gs = new GroupedSummary();
                    gs.setGroupName(gn);
                    gs.setSumPages(sums[0]);
                    gs.setSumBills(sums[1]);

                    summaries.add(gs);
                }
            }
            dataIterator = summaries.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Daten fuer Print-Statistik konnten nicht ermittelt werden!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (dataIterator != null) {
            hasNext = dataIterator.hasNext();
            if (hasNext) {
                currentData = dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("GROUPNAME".equals(field)) {
            return (currentData != null) ? StringUtils.replace(currentData.getGroupName(), "%", "") : null;
        }
        else if ("SUM_PAGES".equals(field)) {
            return (currentData != null) ? currentData.getSumPages() : null;
        }
        else if ("SUM_BILLS".equals(field)) {
            return (currentData != null) ? currentData.getSumBills() : null;
        }
        else if ("BILL_CYCLE".equals(field)) {
            return billCycle;
        }

        return null;
    }

    /*
     * Hilfs-Modell fuer die Speicherung der Daten.
     */
    static class GroupedSummary {
        private String groupName = null;
        private Integer sumPages = null;
        private Integer sumBills = null;

        /**
         * @return Returns the groupName.
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * @param groupName The groupName to set.
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        /**
         * @return Returns the sumPages.
         */
        public Integer getSumPages() {
            return sumPages;
        }

        /**
         * @param sumPages The sumPages to set.
         */
        public void setSumPages(Integer sumPages) {
            this.sumPages = sumPages;
        }

        /**
         * @return Returns the sumBills.
         */
        public Integer getSumBills() {
            return sumBills;
        }

        /**
         * @param sumBills The sumBills to set.
         */
        public void setSumBills(Integer sumBills) {
            this.sumBills = sumBills;
        }

    }
}


