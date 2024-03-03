/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.03.2005 15:26:50
 */
package de.augustakom.hurrican.gui.utils;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Hilfsklasse, um einen Bauauftrag oder eine Projektierung zu drucken.
 *
 *
 */
public class PrintVerlaufHelper extends AbstractHurricanServiceHelper {

    private static final Logger LOGGER = Logger.getLogger(PrintVerlaufHelper.class);

    /**
     * Druckt einen best. Verlauf aus.
     *
     * @param verlaufId     ID des Verlaufs
     * @param projektierung Flag, ob eine Projektierung (true) oder ein Bauauftrag (false) gedruckt werden soll.
     * @param withDetails   Flag gibt an, ob die Verlaufsbemerkungen mit gedruckt werden sollen
     * @param compact       Angabe, ob der Report mit grosser Schrift ({@code false}) oder kompakt ({@code true})
     *                      gedruckt werden soll
     * @throws AKReportException
     */
    public void printVerlauf(Long verlaufId, boolean projektierung, boolean withDetails, boolean compact)
            throws AKReportException {
        try {
            BAService bas = getCCService(BAService.class);
            JasperPrint jpBA = bas.reportVerlauf(verlaufId,
                    HurricanSystemRegistry.instance().getSessionId(), projektierung, compact);

            if (withDetails) {
                JasperPrint jpDetails = bas.reportVerlaufDetails(verlaufId);
                AKJasperReportHelper.joinJasperPrints(jpBA, jpDetails);
            }

            JasperPrintManager.printReport(jpBA, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (projektierung) {
                throw new AKReportException("Projektierungs-Report konnte nicht erstellt werden!", e);
            }
            throw new AKReportException("Bauauftrags-Report konnte nicht erstellt werden!", e);
        }
    }

    /**
     * Druckt den letzten Bauauftrag eines best. Auftrags aus.
     *
     * @param auftragId     Auftrags-ID
     * @param projektierung Flag, ob eine Projektierung (true) oder ein Bauauftrag (false) gedruckt werden soll.
     * @param compact
     * @throws AKReportException
     * @throws FindException
     */
    public void printVerlauf4Auftrag(Long auftragId, boolean projektierung, boolean compact) throws AKReportException {
        try {
            BAService bas = getCCService(BAService.class);
            JasperPrint jpBA = bas.reportVerlauf4Auftrag(auftragId,
                    HurricanSystemRegistry.instance().getSessionId(), projektierung, compact);

            JasperPrintManager.printReport(jpBA, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (projektierung) {
                throw new AKReportException("Projektierungs-Report konnte nicht erstellt werden!\nGrund: " +
                        e.getMessage(), e);
            }
            throw new AKReportException("Bauauftrags-Report konnte nicht erstellt werden!\nGrund: " +
                    e.getMessage(), e);
        }
    }

}


