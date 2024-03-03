/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.11.2005 13:44:26
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;


/**
 * Panel fuer die Darstellung der durchzufuehrenden Leistungs-Aenderungen zu einem Verlauf (Bauauftrag).
 *
 *
 */
public class VerlaufActionPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(VerlaufActionPanel.class);

    private VerlaufActionTableModel tbMdlVerlActions = null;

    /**
     * Default-Konstruktor.
     */
    public VerlaufActionPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufActionPanel.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblLeistungen = getSwingFactory().createLabel("leistungs.diff");
        tbMdlVerlActions = new VerlaufActionTableModel();
        AKJTable tbVerlActions = new AKJTable(tbMdlVerlActions, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbVerlActions.fitTable(new int[] { 170, 120, 80, 80 });
        AKJScrollPane spVerlActions = new AKJScrollPane(tbVerlActions, new Dimension(480, 130));

        this.setLayout(new BorderLayout());
        this.add(lblLeistungen, BorderLayout.NORTH);
        this.add(spVerlActions, BorderLayout.CENTER);
    }

    /**
     * Uebergibt dem Panel die Leistungs-Differenzen, die angezeigt werden sollen.
     *
     * @param verlaufId     ID des Verlaufs, dessen Actions angezeigt werden sollen
     * @param auftragTechLs
     */
    public void setVerlaufActions(Long verlaufId, List<Auftrag2TechLeistung> auftragTechLs) {
        try {
            tbMdlVerlActions.setData(verlaufId, auftragTechLs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


