/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.03.2005 15:28:07
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * Jasper-DataSource, um zu pruefen, ob der Kunde einen Auftrag besitzt, fuer den er die Login-Daten zum AK-Kundenportal
 * benoetigt.
 *
 *
 */
public class CheckKdportalJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(CheckKdportalJasperDS.class);

    private Long kundeNoOrig = null;
    private boolean printData = false;

    /**
     * Konstruktor mit Angabe der (original) Kundennummer.
     *
     * @param kundeNoOrig
     * @throws AKReportException
     */
    public CheckKdportalJasperDS(Long kundeNoOrig) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            List<Long> pgIds = new ArrayList<Long>();
            pgIds.add(ProduktGruppe.AK_ONLINE);
            pgIds.add(ProduktGruppe.AK_ADSL);
            pgIds.add(ProduktGruppe.AK_DSLPLUS);

            CCKundenService ks = getCCService(CCKundenService.class);
            printData = ks.hasActiveAuftragInProdGruppe(kundeNoOrig, pgIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Es konnte nicht ermittelt werden, ob der Kunde einen eMail-Auftrag besitzt!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        if (printData) {
            printData = false;  // nur einmal drucken!
            return true;
        }
        return false;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if ("PRINT_MAIL_INFO".equals(jrField.getName())) {
            return Boolean.valueOf(printData);
        }
        return null;
    }

}


