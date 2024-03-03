/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 17:42:43
 */
package de.augustakom.hurrican.service.billing.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.service.billing.KundenService;

/**
 * Jasper-DataSource, um die Adresse eines Kunden zu laden (fuer interne Reports!).
 *
 *
 */
public class KundeAdresseJasperDS extends AbstractBillingJasperDS {

    private static final Logger LOGGER = Logger.getLogger(KundeAdresseJasperDS.class);

    private Long kundeNoOrig = null;
    private Kunde kunde = null;
    private KundeAdresseView kundeAdresse = null;

    private boolean printData = true;

    /**
     * Konstruktor mit Angabe der (original) Kundennummer.
     *
     * @param kundeNoOrig
     */
    public KundeAdresseJasperDS(Long kundeNoOrig) throws AKReportException {
        super();
        this.kundeNoOrig = kundeNoOrig;
        init();
    }

    /* Laedt die benoetigten Report-Daten. */
    private void init() throws AKReportException {
        if(kundeNoOrig == null) {
            return;
        }
        try {
            KundenService ks = getBillingService(KundenService.class);
            kunde = ks.findKunde(kundeNoOrig);

            KundeQuery query = new KundeQuery();
            query.setKundeNo(kundeNoOrig);
            List<KundeAdresseView> views = ks.findKundeAdresseViewsByQuery(query);
            kundeAdresse = ((views != null) && (!views.isEmpty())) ? views.get(0) : null;

            if (kundeAdresse == null) {
                printData = false;
                throw new AKReportException("Adresse des Kunden konnte nicht ermittelt werden!");
            }
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Adresse konnte nicht geladen werden!", e);
        }
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
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (kundeAdresse != null) {
            if ("KNAME".equals(field)) {
                return kundeAdresse.getName();
            }
            else if ("KVORNAME".equals(field)) {
                return kundeAdresse.getVorname();
            }
            else if ("KSTRASSE".equals(field)) {
                StringBuilder sb = new StringBuilder();
                sb.append((kundeAdresse.getStrasse() != null) ? kundeAdresse.getStrasse() : "");
                sb.append((kundeAdresse.getNummer() != null) ? " " + kundeAdresse.getNummer() : "");
                sb.append((kundeAdresse.getHausnummerZusatz() != null) ? " " + kundeAdresse.getHausnummerZusatz() : "");
                return sb.toString();
            }
            else if ("KPLZ_ORT".equals(field)) {
                return StringUtils.trimToEmpty(kundeAdresse.getPlz()) + " " + kundeAdresse.getCombinedOrtOrtsteil();
            }
            else if ("VERTRIEB".equals(field)) {
                return kundeAdresse.getKundenbetreuer();
            }
            else if ("KUNDE__NO".equals(field)) {
                return kundeNoOrig;
            }
            else if ("KUNDENTYP".equals(field)) {
                return (kunde != null) ? kunde.getKundenTyp() : null;
            }
        }

        return null;
    }

}
