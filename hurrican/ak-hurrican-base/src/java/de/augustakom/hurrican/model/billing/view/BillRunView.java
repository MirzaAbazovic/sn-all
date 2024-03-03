/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2008 14:44:42
 */
package de.augustakom.hurrican.model.billing.view;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.billing.AbstractBillingModel;


/**
 * View-Modell, fuer Daten des Rechnungslaufs.
 *
 *
 */
public class BillRunView extends AbstractBillingModel {

    private Long runNo = null;
    private Long period = null;
    private Date invoiceDate = null;
    private String status = null;
    private String billCycle = null;

    /**
     * @return billCycle
     */
    public String getBillCycle() {
        return billCycle;
    }

    /**
     * @param billCycle Festzulegender billCycle
     */
    public void setBillCycle(String billCycle) {
        this.billCycle = billCycle;
    }

    /**
     * @return invoiceDate
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * @param invoiceDate Festzulegender invoiceDate
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * @return period
     */
    public Long getPeriod() {
        return period;
    }

    /**
     * @param period Festzulegender period
     */
    public void setPeriod(Long period) {
        this.period = period;
    }

    /**
     * @return runNo
     */
    public Long getRunNo() {
        return runNo;
    }

    /**
     * @param runNo Festzulegender runNo
     */
    public void setRunNo(Long runNo) {
        this.runNo = runNo;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status Festzulegender status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Erzeugt einen String fuer die Darstellung in Uebersichtslisten
     *
     * @return
     *
     */
    public String getBillRunDescription() {
        String res = "";
        if (StringUtils.isNotBlank(billCycle)) {
            res += billCycle + "_";
        }
        if (period != null) {
            res += period + "_";
        }
        if (runNo != null) {
            res += runNo;
        }
        return res;
    }

}


