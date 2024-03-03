/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2004 11:17:08
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;


/**
 * Jasper-DataSource fuer die VPN-Uebersicht. <br> Folgende Fields werden untersteutzt: <ul> <li>AUFTRAG_ID
 * <li>ORDER__NO <li>VBZ <li>INBETRIEBNAHME <li>KUENDIGUNG <li>AUFTRAG_STATUS <li>ANSCHLUSSART <li>ENDSTELLE
 * <li>ENDSTELLE_NAME <li>ENDSTELLE_ORT </ul>
 *
 *
 */
public class VPNUebersichtJasperDS implements JRDataSource {

    private List<AuftragEndstelleView> data = null;
    private Iterator<AuftragEndstelleView> dataIterator = null;
    private AuftragEndstelleView currentData = null;

    /**
     * Konstruktor mit Angabe der Report-Daten.
     */
    public VPNUebersichtJasperDS(List<AuftragEndstelleView> data) {
        super();
        this.data = data;
        if (this.data != null) {
            this.dataIterator = this.data.iterator();
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
    public Object getFieldValue(JRField jrField) throws JRException {
        if (currentData != null) {
            if ("AUFTRAG_ID".equals(jrField.getName())) {
                return currentData.getAuftragId();
            }
            else if ("ORDER__NO".equals(jrField.getName())) {
                return currentData.getAuftragNoOrig();
            }
            else if (AbstractCCJasperDS.VBZ_KEY.equals(jrField.getName())) {
                return currentData.getVbz();
            }
            else if ("INBETRIEBNAHME".equals(jrField.getName())) {
                return currentData.getInbetriebnahme();
            }
            else if ("KUENDIGUNG".equals(jrField.getName())) {
                return currentData.getKuendigung();
            }
            else if ("AUFTRAG_STATUS".equals(jrField.getName())) {
                return currentData.getAuftragStatusText();
            }
            else if ("ANSCHLUSSART".equals(jrField.getName())) {
                return currentData.getAnschlussart();
            }
            else if ("ENDSTELLE".equals(jrField.getName())) {
                return currentData.getEndstelle();
            }
            else if ("ENDSTELLE_NAME".equals(jrField.getName())) {
                return currentData.getEndstelleName();
            }
            else if ("ENDSTELLE_ORT".equals(jrField.getName())) {
                return currentData.getEndstelleOrt();
            }
        }
        return null;
    }

}


