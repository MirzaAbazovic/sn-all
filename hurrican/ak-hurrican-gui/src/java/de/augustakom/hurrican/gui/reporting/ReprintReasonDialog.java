/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2007 09:10:19
 */
package de.augustakom.hurrican.gui.reporting;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.reporting.ReportReason;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Dialog, um den Grund für den erneuten Druck eines Reports auszuwählen.
 *
 *
 */
public class ReprintReasonDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(ReprintReasonDialog.class);

    private AKJComboBox cbReason = null;

    private Boolean reprintBeforeArchive = null;


    /**
     * Konstruktor Das Flag gibt an ob der vorhergehende Report bereits archiviert wurde. True, Report wurde noch nicht
     * archiviert
     */
    public ReprintReasonDialog(Boolean beforeArchive) {
        super("de/augustakom/hurrican/gui/reporting/resources/ReprintReasonDialog.xml");
        this.reprintBeforeArchive = beforeArchive;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL("de/augustakom/hurrican/gui/images/printer.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, true, true);

        AKJTextArea taInfo = getSwingFactory().createTextArea("info");
        taInfo.setEditable(false);
        taInfo.setWrapStyleWord(true);
        taInfo.setLineWrap(true);

        cbReason = getSwingFactory().createComboBox("reason", new AKCustomListCellRenderer<>(ReportReason.class, ReportReason::getName));

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(taInfo, GBCFactory.createGBC(100, 100, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbReason, GBCFactory.createGBC(100, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dataPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(400, 110));
    }

    /* Laedt die Liste der verfügbaren Reportreasons*/
    private void read() {
        try {
            setWaitCursor();

            // Lade alle ReportReasons
            ReportService service = getReportService(ReportService.class);
            List<ReportReason> list = service.findReportReasons(reprintBeforeArchive);

            if (CollectionTools.isNotEmpty(list)) {
                cbReason.addItems(list, true, ReportReason.class, true);
            }
            else {
                // Keine Gründe für erneuten Druck vorhanden, beende Dialog
                prepare4Close();
                setValue(null);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        ReportReason reason = (ReportReason) cbReason.getSelectedItem();
        // Falls kein Grund ausgewählt wurde, zeige Hinweis
        if (reason == null || (reason != null && reason.getId() == null)) {
            MessageHelper.showInfoDialog(this, "Bitte einen Grund für das erneute Drucken des Reports auswählen", null, true);
        }
        else {
            // Beende Dialog
            prepare4Close();
            setValue(reason);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }


}


