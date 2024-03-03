/*
* Copyright (c) 2007 - M-net Telekommunikations GmbH
* All rights reserved.
* -------------------------------------------------------
* File created: 28.08.2007 11:51:29
*/
package de.augustakom.hurrican.gui.tools.sap.actions;

import java.awt.event.*;

import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.tools.sap.SAPDatenFrame;


/**
 * Action, um ein Frame mit SAP-Daten zu oeffnen.
 *
 *
 */
public class OpenSAPDatenAction extends AbstractServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        SAPDatenFrame.showSAPDaten(null);
    }


}

