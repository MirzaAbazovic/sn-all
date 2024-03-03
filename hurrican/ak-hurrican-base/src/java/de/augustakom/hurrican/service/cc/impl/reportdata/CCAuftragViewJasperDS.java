/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2004 10:19:09
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * JasperReports-DataSource fuer Reports, die Auftragsdaten zu einem Kunden darstellen. <br> Folgende Feldnamen werden
 * zurueck geliefert: <br> <ul> <li>AUFTRAG__NO <li>AUFTRAG_ID <li>INBETRIEBNAHME <li>KUENDIGUNG <li>VBZ
 * <li>ANSCHLUSSART <li>AUFTRAGSTATUS <li>BUENDEL_NR <li>VPN_NR </ul>
 *
 *
 */
public class CCAuftragViewJasperDS extends AbstractCCJasperDS implements JRDataSource, JRRewindableDataSource {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragViewJasperDS.class);

    private List<CCKundeAuftragView> data = null;
    private Iterator<CCKundeAuftragView> dataIterator = null;
    private CCKundeAuftragView currentData = null;

    /**
     * Konstruktor mit den Daten fuer die DataSource.
     *
     * @param data
     */
    public CCAuftragViewJasperDS(List<CCKundeAuftragView> data) {
        super();
        this.data = data;
        if (this.data != null) {
            this.dataIterator = this.data.iterator();
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
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
            if ("AUFTRAG__NO".equals(jrField.getName())) {
                return currentData.getAuftragNoOrig();
            }
            else if ("AUFTRAG_ID".equals(jrField.getName())) {
                return currentData.getAuftragId();
            }
            else if ("INBETRIEBNAHME".equals(jrField.getName())) {
                return currentData.getInbetriebnahme();
            }
            else if ("KUENDIGUNG".equals(jrField.getName())) {
                return currentData.getKuendigung();
            }
            else if (VBZ_KEY.equals(jrField.getName())) {
                return currentData.getVbz();
            }
            else if ("ANSCHLUSSART".equals(jrField.getName())) {
                return currentData.getAnschlussart();
            }
            else if ("AUFTRAGSTATUS".equals(jrField.getName())) {
                return currentData.getStatusText();
            }
            else if ("BUENDEL_NR".equals(jrField.getName())) {
                return currentData.getBuendelNr();
            }
            else if ("VPN_NR".equals(jrField.getName()) && (currentData.getVpnId() != null)) {
                try {
                    VPNService vpns = getCCService(VPNService.class);
                    VPN vpn = vpns.findVPN(currentData.getVpnId());
                    return (vpn != null) ? vpn.getVpnNr() : null;
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return Integer.valueOf(-1);
                }
            }
        }

        return null;
    }

    /**
     * @see net.sf.jasperreports.engine.JRRewindableDataSource#moveFirst()
     */
    public void moveFirst() throws JRException {
        if (this.data != null) {
            this.dataIterator = this.data.iterator();
        }
    }

}


