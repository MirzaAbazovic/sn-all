/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.03.2005 16:30:45
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.AccountService;


/**
 * Jasper-DataSource, um den Verwaltungsaccount eines Kunden zu ermitteln.
 *
 *
 */
public class AKMailHostingPortalJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(AKMailHostingPortalJasperDS.class);

    private Long kundeNoOrig = null;
    private boolean printData = false;
    private IntAccount intAccount = null;

    /**
     * Konstruktor mit Angabe der (original) Kundennummer.
     *
     * @param kundeNoOrig
     * @throws AKReportException
     */
    public AKMailHostingPortalJasperDS(Long kundeNoOrig) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init() Laedt den Verwaltungsaccount des
     * Kunden.
     */
    @Override
    protected void init() throws AKReportException {
        try {
            AccountService accs = getCCService(AccountService.class);
            intAccount = accs.findIntAccount("" + kundeNoOrig, IntAccount.LINR_VERWALTUNGSACCOUNT);
            if ((intAccount != null) && ((intAccount.getGesperrt() == null) || !intAccount.getGesperrt().booleanValue())) {
                printData = true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Der Verwaltungsaccount des Kunden konnte nicht ermittelt werden!", e);
        }
    }

    @Override
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (intAccount != null) {
            if ("ACCOUNT".equals(jrField.getName())) {
                return intAccount.getAccount();
            }
            else if ("PASSWORD".equals(jrField.getName())) {
                return intAccount.getPasswort();
            }
        }
        return null;
    }

}


