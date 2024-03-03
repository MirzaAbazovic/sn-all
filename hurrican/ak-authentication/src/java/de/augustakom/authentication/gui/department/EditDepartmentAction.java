/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2012 18:19:17
 */
package de.augustakom.authentication.gui.department;

import java.awt.event.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.gui.tree.AdminTreeNode;
import de.augustakom.authentication.model.AKDepartment;

/**
 * Action, um ein DepartmentAdminFrame aufzurufen.
 */
public class EditDepartmentAction extends AbstractAuthenticationServiceAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);
        Object treeObject = getValue(SystemConstants.ACTION_PROPERTY_TREENODE);
        if (userObj instanceof AKDepartment) {
            AdminTreeNode treeNode = null;
            if (treeObject instanceof AdminTreeNode) {
                treeNode = (AdminTreeNode) treeObject;
            }
            DepartmentDataFrame frame = new DepartmentDataFrame((AKDepartment) userObj, treeNode);
            GUISystemRegistry.instance().getMainFrame().registerFrame(frame, false);
        }

    }

}


