/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 13:30:11
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Jasper-DataSource, um alle Buendel-Auftraege eines best. Auftrags zu laden.
 *
 *
 */
public class BuendelJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(BuendelJasperDS.class);

    private Long auftragId = null;
    private List<ProduktVbzModel> produktVbz = null;
    private Iterator<ProduktVbzModel> dataIterator = null;
    private ProduktVbzModel currentData = null;

    /**
     * Konstruktor mit Angabe der Auftrags-ID.
     *
     * @param auftragId
     * @throws AKReportException
     */
    public BuendelJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            produktVbz = new ArrayList<ProduktVbzModel>();

            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
            if ((ad != null) && (ad.getBuendelNr() != null) && (ad.getBuendelNr().intValue() > 0)) {
                List<CCAuftragIDsView> views = as.findAuftragIdAndVbz4Buendel(
                        ad.getBuendelNr(), ad.getBuendelNrHerkunft());
                if (views != null) {
                    ProduktService ps = getCCService(ProduktService.class);
                    for (CCAuftragIDsView view : views) {
                        Produkt p = ps.findProdukt4Auftrag(view.getAuftragId());
                        if (p != null) {
                            ProduktVbzModel model = new ProduktVbzModel(p.getAnschlussart(), view.getVbz());
                            produktVbz.add(model);
                        }
                    }
                    this.dataIterator = produktVbz.iterator();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Buendel-Auftraege konnten nicht ermittelt werden!");
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
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
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("PRODUKT".equals(field)) {
                return currentData.produkt;
            }
            else if (VBZ_KEY.equals(field)) {
                return currentData.vbz;
            }
        }
        return null;
    }

    /* Hilfs-Modell. */
    private static class ProduktVbzModel {
        private String produkt = null;
        private String vbz = null;

        public ProduktVbzModel(String produkt, String vbz) {
            this.produkt = produkt;
            this.vbz = vbz;
        }
    }
}


