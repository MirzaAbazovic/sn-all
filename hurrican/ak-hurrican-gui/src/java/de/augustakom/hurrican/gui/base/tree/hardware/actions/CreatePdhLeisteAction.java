/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.2010 11:48:11
 */
package de.augustakom.hurrican.gui.base.tree.hardware.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJFrame;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.tree.hardware.PdhLeisteDialog;
import de.augustakom.hurrican.model.cc.HVTStandort;


/**
 * Action, um einen Dialog fuer die Port-Generierung zu oeffnen.
 */
public class CreatePdhLeisteAction extends AKAbstractAction {
    private static final Logger LOGGER = Logger.getLogger(CreatePdhLeisteAction.class);
    private static final String CREATE_PDH_LEISTE_TITLE = "PDH-Leiste anlegen ...";
    private static final String CREATE_PDH_LEISTE_ACTION = "create.pdhleiste";
    private final HVTStandort hvtStandort;
    private final AKJFrame parentFrame;

    public CreatePdhLeisteAction(AKJFrame parentFrame, HVTStandort hvtStandort) {
        this.parentFrame = parentFrame;
        this.hvtStandort = hvtStandort;
        setName(CREATE_PDH_LEISTE_TITLE);
        setActionCommand(CREATE_PDH_LEISTE_ACTION);
        setTooltip("Öffnet einen Dialog, über den eine SDH-Leite angelegt werden kann");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            PdhLeisteDialog dlg = new PdhLeisteDialog(hvtStandort);
            DialogHelper.showDialog(parentFrame, dlg, true, true);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(parentFrame, e);
        }
    }
}
