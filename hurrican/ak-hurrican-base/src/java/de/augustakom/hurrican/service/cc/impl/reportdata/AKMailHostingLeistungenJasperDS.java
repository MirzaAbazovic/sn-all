/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 14:20:42
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.query.KundeLeistungQuery;
import de.augustakom.hurrican.model.billing.view.KundeLeistungView;
import de.augustakom.hurrican.service.billing.LeistungService;


/**
 * Jasper-DataSource um die Internet-Leistungen (AK-eMail, AK-Hosting und AK-flat) eines Kunden zu ermitteln.
 *
 *
 */
public class AKMailHostingLeistungenJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(AKMailHostingLeistungenJasperDS.class);

    private Long kundeNoOrig = null;
    protected Iterator<KundeLeistungView> dataIterator = null;
    private KundeLeistungView currentData = null;

    /**
     * Konstruktor mit Angabe der (original) Kundennummer des Kunden, dessen AK-online Leistungen ermittelt werden
     * sollen.
     *
     * @param kundeNoOrig
     */
    public AKMailHostingLeistungenJasperDS(Long kundeNoOrig) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init() Laedt die benoetigten Daten.
     */
    @Override
    protected void init() throws AKReportException {
        try {
            this.dataIterator = null;

            List<Long> oeNos = new ArrayList<Long>();
            oeNos.add(OE.OE__NO_AKEMAIL);
            oeNos.add(OE.OE__NO_AKEMAIL_K);
            oeNos.add(OE.OE__NO_AKFLAT);
            oeNos.add(OE.OE__NO_AKFLAT_K);
            oeNos.add(OE.OE__NO_AKHOSTING);
            oeNos.add(OE.OE__NO_AKHOSTING_K);

            KundeLeistungQuery query = new KundeLeistungQuery();
            query.setKundeNo(kundeNoOrig);
            query.setOeNoOrigs(oeNos);

            LeistungService ls = getBillingService(LeistungService.class);
            List<KundeLeistungView> leistungViews = ls.findLeistungen4Kunde(query);
            if (leistungViews != null) {
                this.dataIterator = leistungViews.iterator();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Die Online-Leistungen des Kunden konnten nicht ermittelt werden.", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentData = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (currentData != null) {
            if ("MENGE".equals(jrField.getName())) {
                return currentData.getMenge();
            }
            else if ("LEISTUNG".equals(jrField.getName())) {
                return currentData.getLeistungRechnungstext();
            }
        }
        return null;
    }

}


