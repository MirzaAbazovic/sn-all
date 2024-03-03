/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2010 16:43:03
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame, um den AK-Schedulers zu bearbeiten (Jobs und Trigger stoppen, starten, modifizieren).
 *
 *
 */
public class AKSchedulerControllerFrame extends AKJAbstractInternalFrame {

    private AKSchedulerControllerPanel panel;

    public AKSchedulerControllerFrame(String schedulerTyp) {
        super("de/augustakom/hurrican/gui/tools/scheduler/resources/AKSchedulerControllerFrame.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        panel = new AKSchedulerControllerPanel();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void frameWillClose() {
        panel.onClose();
    }

}
