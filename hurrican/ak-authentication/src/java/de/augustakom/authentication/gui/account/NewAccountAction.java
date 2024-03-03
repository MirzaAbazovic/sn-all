/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 14:36:38
 */
package de.augustakom.authentication.gui.account;

import java.awt.event.*;
import javax.swing.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.ResourceReader;

/**
 * Action, um ein AccountAdminFrame (mit 'leerem' AKAccount-Objekt) aufzurufen.
 */
public class NewAccountAction extends AbstractAuthenticationServiceAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);
        if (userObj instanceof AKDb) {
            AKAccount newAcc = new AKAccount();
            newAcc.setDbId(((AKDb) userObj).getId());
            AccountDataFrame frame = new AccountDataFrame(newAcc, null);
            GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
        }
        else {
            ResourceReader rr = new ResourceReader("de.augustakom.authentication.gui.account.resources.ActionMessages");
            String title = rr.getValue("no.db.title");
            String msg = rr.getValue("no.db.msg");

            MessageHelper.showMessageDialog(GUISystemRegistry.instance().getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
        }
    }
}
