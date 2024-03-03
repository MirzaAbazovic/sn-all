/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2005 13:04:16
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Jasper-DataSource, um die Verlauf-Actions (=Leistungs-Differenz) zu einem bestimmten Verlauf zu ermitteln.
 *
 *
 */
public class VerlaufActionJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(VerlaufActionJasperDS.class);

    private Long verlaufId = null;
    private List<Auftrag2TechLeistung> zugaenge = null;
    private List<Auftrag2TechLeistung> abgaenge = null;
    private int recordCount = -1;

    private TechLeistung techLsZugang = null;
    private TechLeistung techLsAbgang = null;
    private CCLeistungsService leistungsService = null;

    private int actRecord = 0;

    /**
     * Konstruktor mit Angabe der relevanten Verlaufs-ID.
     */
    public VerlaufActionJasperDS(Long verlaufId) {
        super();
        this.verlaufId = verlaufId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() {
        try {
            leistungsService = getCCService(CCLeistungsService.class);
            List<Auftrag2TechLeistung> leistungen = leistungsService.findAuftrag2TechLeistungen4Verlauf(verlaufId);

            zugaenge = new ArrayList<Auftrag2TechLeistung>();
            abgaenge = new ArrayList<Auftrag2TechLeistung>();

            CollectionUtils.select(leistungen, new Predicate() {
                @Override
                public boolean evaluate(Object obj) {
                    return NumberTools.equal(((Auftrag2TechLeistung) obj).getVerlaufIdReal(), verlaufId);
                }
            }, zugaenge);

            CollectionUtils.select(leistungen, new Predicate() {
                @Override
                public boolean evaluate(Object obj) {
                    return NumberTools.equal(((Auftrag2TechLeistung) obj).getVerlaufIdKuend(), verlaufId);
                }
            }, abgaenge);

            recordCount = (zugaenge != null) ? zugaenge.size() : -1;
            recordCount = ((abgaenge != null) && (abgaenge.size() > recordCount)) ? abgaenge.size() : recordCount;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        techLsZugang = null;
        techLsAbgang = null;

        if ((recordCount > 0) && (actRecord < recordCount)) {
            try {
                if ((zugaenge != null) && (zugaenge.size() > actRecord)) {
                    techLsZugang = leistungsService.findTechLeistung(
                            zugaenge.get(actRecord).getTechLeistungId());
                }

                if ((abgaenge != null) && (abgaenge.size() > actRecord)) {
                    techLsAbgang = leistungsService.findTechLeistung(
                            abgaenge.get(actRecord).getTechLeistungId());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            actRecord++;
            return true;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("LEISTUNG_NAME_ZUGANG".equals(field)) {
            return (techLsZugang != null) ? techLsZugang.getTypAndName() : null;
        }
        else if ("LEISTUNG_NAME_ABGANG".equals(field)) {
            return (techLsAbgang != null) ? techLsAbgang.getTypAndName() : null;
        }
        return null;
    }

}


