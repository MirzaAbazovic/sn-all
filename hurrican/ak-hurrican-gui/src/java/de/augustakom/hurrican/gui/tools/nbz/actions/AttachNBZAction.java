/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2005 10:16:13
 */
package de.augustakom.hurrican.gui.tools.nbz.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.nbz.AttachNBZDialog;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;

/**
 * Action, um den Dialog zu öffnen, über den eine NBZ zugeordnet werden kann.
 */
public class AttachNBZAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(AttachNBZAction.class);

    public static final String KUNDE_NO = "KUNDE__NO";

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            KundeAdresseView kundeAdresseView = null;

            AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
            if (mo != null) {
                Object model = mo.getModel();
                if ((model != null) && (model instanceof KundeAdresseView)) {
                    kundeAdresseView = (KundeAdresseView) model;
                }
            }
            if (getValue(OBJECT_4_ACTION) instanceof KundeAdresseView) {
                kundeAdresseView = (KundeAdresseView) getValue(OBJECT_4_ACTION);
            }
            if (kundeAdresseView != null) {
                AttachNBZDialog dlg = new AttachNBZDialog(kundeAdresseView.getKundeNo());
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            }
            else {
                MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Keinen Kunden gefunden."));
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }
}
