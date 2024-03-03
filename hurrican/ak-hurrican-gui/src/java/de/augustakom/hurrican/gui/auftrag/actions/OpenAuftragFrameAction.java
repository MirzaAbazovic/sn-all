/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.2006 14:01:57
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;

import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Action, um das Auftragsfenster zu oeffnen.
 *
 *
 */
public class OpenAuftragFrameAction extends AbstractServiceAction {

    public OpenAuftragFrameAction() {
        setName("Auftrag öffnen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CCAuftragModel auftragModel = null;
        if (getValue(OBJECT_4_ACTION) instanceof CCAuftragModel) {
            auftragModel = (CCAuftragModel) getValue(OBJECT_4_ACTION);
        }
        else {
            auftragModel = findModelByType(CCAuftragModel.class);
        }

        if (auftragModel != null && auftragModel.getAuftragId() != null) {
            AuftragDataFrame.openFrame(auftragModel);
        }
    }

}


