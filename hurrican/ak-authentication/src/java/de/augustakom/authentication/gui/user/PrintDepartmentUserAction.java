/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 16:08:27
 */
package de.augustakom.authentication.gui.user;

import java.awt.event.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.reports.jasper.AKJasperDialog;


/**
 * Action, um eine Uebersicht ueber alle Abteilungen und deren zugeordnete Benutzer zu drucken.
 *
 *
 */
public class PrintDepartmentUserAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(PrintDepartmentUserAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            GUISystemRegistry.instance().getMainFrame().setWaitCursor();

            AKDepartmentService ds = getAuthenticationService(AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);
            JasperPrint jp = ds.reportDepartmentUsers();
            AKJasperDialog.viewReport(jp, GUISystemRegistry.instance().getMainFrame());
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
        }
        finally {
            GUISystemRegistry.instance().getMainFrame().setDefaultCursor();
        }
    }

}


