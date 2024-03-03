/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 10:37:01
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.shared.view.AnschriftView;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * Jasper DataSource, um die Adresse eines Kunden zu drucken.
 *
 *
 */
public class AdresseJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(AdresseJasperDS.class);

    private boolean next = true;
    private Long kundeNoOrig = null;
    protected AnschriftView anschrift = null;

    /**
     * Konstruktor fuer Ableitungen.
     */
    protected AdresseJasperDS() {
    }

    /**
     * Konstruktor fuer die Jasper-DataSource.
     *
     * @param kundeNoOrig (original) Kundennummer des Kunden, dessen Adresse ausgelesen werden soll.
     */
    public AdresseJasperDS(Long kundeNoOrig) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init() Liest die Adress-Daten des
     * Kunden aus.
     */
    @Override
    protected void init() throws AKReportException {
        try {
            CCKundenService ks = getCCService(CCKundenService.class);
            anschrift = ks.findAnschrift(kundeNoOrig);

            if (anschrift != null) {
                anschrift.prettyFormat();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Die Anschrift des Kunden konnte nicht ermittelt werden!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        if ((anschrift != null) && next) {
            next = false;
            return true;
        }
        return false;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if ("ADRESSE1".equals(jrField.getName())) {
            return anschrift.getAnschrift1();
        }
        else if ("ADRESSE2".equals(jrField.getName())) {
            return anschrift.getAnschrift2();
        }
        else if ("ADRESSE3".equals(jrField.getName())) {
            return anschrift.getAnschrift3();
        }
        else if ("ADRESSE4".equals(jrField.getName())) {
            return anschrift.getAnschrift4();
        }
        else if ("STRASSE".equals(jrField.getName())) {
            return anschrift.getStrasse();
        }
        else if ("PLZ_ORT".equals(jrField.getName())) {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(anschrift.getPlz())) {
                sb.append(StringUtils.trimToEmpty(anschrift.getPlz()));
                sb.append(" ");
            }
            if (StringUtils.isNotBlank(anschrift.getOrt())) {
                sb.append(anschrift.getOrt());
            }
            return sb.toString();
        }
        return null;
    }

}


