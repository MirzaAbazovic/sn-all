/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 14:30:09
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Panel fuer die Anzeige/Bearbeitung der Projektierungen fuer FieldService.
 *
 *
 */
public class ProjektierungFieldServicePanel extends AbstractProjektierungPanel {

    private static final Logger LOGGER = Logger.getLogger(ProjektierungFieldServicePanel.class);

    // GUI-Komponenten
    private AKJDateComponent dcDatumAn = null;
    private AKJDateComponent dcErledigtAm = null;
    private AKJTextField tfBearbeiter = null;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID (FieldService).
     */
    public ProjektierungFieldServicePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungFieldServicePanel.xml",
                Abteilung.FIELD_SERVICE, false, true);
        createGUI();
        super.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblDatumAn = getSwingFactory().createLabel("datum.an");
        AKJLabel lblErledigtAm = getSwingFactory().createLabel("erledigt.am");
        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter");

        dcDatumAn = getSwingFactory().createDateComponent("datum.an", false);
        dcErledigtAm = getSwingFactory().createDateComponent("erledigt.am", false);
        tfBearbeiter = getSwingFactory().createTextField("bearbeiter", false);

        AKJButton btnErledigen = getSwingFactory().createButton("erledigen", getActionListener());
        AKJButton btnUebernahme = getSwingFactory().createButton("uebernehmen", getActionListener());
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnBemerkungen = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnErledigen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnUebernahme, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnPrint, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnBemerkungen, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnShowPorts, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(lblDatumAn, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(dcDatumAn, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfBearbeiter, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblErledigtAm, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcErledigtAm, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnPnl, GBCFactory.createGBC(100, 0, 0, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 4, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnPrint, btnBemerkungen, btnErledigen, btnUebernahme, btnShowPorts);
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#showDetails4Verlauf(java.lang.Integer)
     */
    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
        if (verlaufId != null) {
            try {
                BAService bas = getCCService(BAService.class);
                VerlaufAbteilung va = bas.findVerlaufAbteilung(verlaufId, abteilungId);
                if (va != null) {
                    tfBearbeiter.setText(va.getBearbeiter());
                    dcDatumAn.setDate(va.getDatumAn());
                    dcErledigtAm.setDate(va.getDatumErledigt());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("print".equals(command)) {
            printProjektierung();
        }
        else if ("bemerkungen".equals(command)) {
            showBemerkungen((getActView() != null) ? getActView().getVerlaufId() : null);
        }
        else if ("erledigen".equals(command)) {
            projektierungAbschliessenTechnik();
        }
        else if ("uebernehmen".equals(command)) {
            projektierungUebernehmen();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getActView());
        }
    }
}


