/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2009 13:04:56
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWLtg;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWRouter;


/**
 * Klasse fuer alle Hardware-Definitions-Panels.
 *
 *
 */
public class HWRackDefAdminPanel extends AbstractAdminPanel {

    private static final long serialVersionUID = 8386602592749009986L;
    private AbstractAdminPanel defPanel = null;

    /**
     * Konstruktor
     */
    public HWRackDefAdminPanel() {
        super(null);
    }

    @Override
    protected final void createGUI() {
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void showDetails(Object details) {
    }

    @Override
    public final void loadData() {
    }

    @Override
    public void createNew() {
        if (defPanel != null) {
            defPanel.createNew();
        }
    }

    @Override
    public void saveData() {
        if (defPanel != null) {
            defPanel.saveData();
        }
    }

    /**
     * Gibt an, ob ein Admin Panel verfuegbar ist/angezeigt wird
     */
    public boolean isAdminPanelDefined() {
        return defPanel != null;
    }

    /**
     * @param rack Festzulegender rack
     */
    public void setRack(HWRack rack) {
        defPanel = null;

        this.removeAll();
        if (rack != null) {
            if (HWDlu.class.equals(rack.getClass())) {
                defPanel = new HWDluAdminPanel(rack);
            }
            else if (HWDslam.class.equals(rack.getClass())) {
                defPanel = new HWDslamAdminPanel(rack);
            }
            else if (HWRouter.class.equals(rack.getClass())) {
                defPanel = new HWRouterAdminPanel(rack);
            }
            else if (HWLtg.class.equals(rack.getClass())) {
                defPanel = new HWLtgAdminPanel(rack);
            }
            else if (rack instanceof HWMdu) {
                defPanel = new HWMduAdminPanel((HWMdu) rack);
            }
            else if (HWOlt.class.equals(rack.getClass())) {
                defPanel = new HWOltAdminPanel(rack);
            }
            else if (rack instanceof HWOnt) {
                defPanel = new HwOntAdminPanel((HWOnt) rack);
            }
            else if (rack instanceof HWDpo) {
                defPanel = new HwDpoAdminPanel((HWDpo) rack);
            }
            else if (rack instanceof HWDpu) {
                defPanel = new HwDpuAdminPanel((HWDpu) rack);
            }
            if (defPanel != null) {
                this.add(defPanel, BorderLayout.CENTER);
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void deleteRack() {
        defPanel = null;
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

}
