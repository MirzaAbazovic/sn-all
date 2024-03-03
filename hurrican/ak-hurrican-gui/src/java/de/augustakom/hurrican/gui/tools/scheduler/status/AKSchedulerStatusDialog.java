/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2006 15:32:03
 */
package de.augustakom.hurrican.gui.tools.scheduler.status;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;


/**
 * Dialog, um den Status des AK-Schedulers anzuzeigen.
 *
 *
 */
public class AKSchedulerStatusDialog extends AKJAbstractOptionDialog {

    public AKSchedulerStatusDialog(String schedulerType) {
        super(null, true);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle("AK-Scheduler Status");
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(new AKSchedulerStatusPanel(), BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


