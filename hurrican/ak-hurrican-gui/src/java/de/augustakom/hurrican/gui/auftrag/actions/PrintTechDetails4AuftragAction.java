/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2007 09:51:07
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.reports.jasper.AKJasperExporter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Action, um die technischen Details zu einem Auftrag zu drucken. <br> Als Report wird der Bauauftragsreport
 * verwendet.
 *
 *
 */
public class PrintTechDetails4AuftragAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(PrintTechDetails4AuftragAction.class);

    private CCAuftragModel auftragModel = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            printTechDetails();
        }
        else {
            LOGGER.error("Das AuftragModel konnte nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Das Anschreiben mit den Online-Daten konnte nicht gedruckt " +
                            "werden, da der Auftrag nicht ermittelt werden konnte!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /*
     * Druckt die techn. Daten des Auftrags.
     */
    private void printTechDetails() {
        try {
            BAService bas = getCCService(BAService.class);
            JasperPrint jasperPrint = bas.reportTechDetails4Auftrag(
                    auftragModel.getAuftragId(), HurricanSystemRegistry.instance().getSessionId());

            StringBuilder path = new StringBuilder();
            path.append(SystemUtils.USER_HOME);
            path.append(SystemUtils.FILE_SEPARATOR);
            path.append(createFileName(auftragModel.getAuftragId()));
            File fileToGenerate = new File(path.toString());

            AKJasperExporter.exportReport(
                    jasperPrint, AKJasperExporter.EXPORT_TYPE_PDF, path.toString(), null);

            if (fileToGenerate.exists()) {
                Desktop.getDesktop().open(fileToGenerate);
            }
            else {
                throw new HurricanGUIException("File does not exist: " + path);
            }
        }
        catch (Error e) {  // NOSONAR squid:S1181 ; Jasper throws an error, if compiled with different Java version as running JRE
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Generiert fuer den angegebenen Auftrag einen File-Namen, der
     * fuer den Report mit den technischen Details verwendet werden kann.
     */
    private String createFileName(Long auftragId) {
        StringBuilder builder = new StringBuilder();
        try {
            PhysikService physikService = getCCService(PhysikService.class);
            VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(auftragId);
            if (verbindungsBezeichnung != null) {
                builder.append(verbindungsBezeichnung.getVbz());
                builder.append("_");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        builder.append(auftragId.toString());
        String FILE_SUFFIX = "_DSDB.PDF";
        builder.append(FILE_SUFFIX);

        return builder.toString();
    }
}


