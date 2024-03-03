/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 11:13:19
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import de.augustakom.common.tools.reports.AKReportException;


/**
 * Jasper DataSource fuer das AKOnline-Anschreiben.
 *
 *
 */
public class AKOnlineJasperDS extends AbstractCCJasperDS {

    private boolean printData = true;
    private Long kundeNoOrig = null;

    /**
     * Konstruktor mit Angabe der Kundennummer.
     *
     * @param kundeNoOrig
     */
    public AKOnlineJasperDS(Long kundeNoOrig) {
        this.kundeNoOrig = kundeNoOrig;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    protected void init() throws AKReportException {
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    public Object getFieldValue(JRField jrField) throws JRException {
        if ("KUNDE__NO".equals(jrField.getName())) {
            return kundeNoOrig;
        }
        return null;
    }

}


