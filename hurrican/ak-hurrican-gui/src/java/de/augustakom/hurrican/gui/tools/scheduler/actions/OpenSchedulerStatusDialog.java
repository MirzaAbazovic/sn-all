/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2006 15:31:16
 */
package de.augustakom.hurrican.gui.tools.scheduler.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.scheduler.status.AKSchedulerStatusDialog;


/**
 * Action, um den Dialog fuer den Scheduler-Status anzuzeigen.
 *
 *
 */
public class OpenSchedulerStatusDialog extends AKAbstractAction {

    public static final String SCHEDULER_DEFAULT = "default.Scheduler";

    @Override
    public void actionPerformed(ActionEvent e) {
        AKSchedulerStatusDialog dialog = new AKSchedulerStatusDialog(SCHEDULER_DEFAULT);
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);
    }

}
