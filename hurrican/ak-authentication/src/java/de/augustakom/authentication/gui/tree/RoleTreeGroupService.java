/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 08:39:43
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKRoleService;

/**
 * TreeService fuer die RoleTreeGroup. <br> Der TreeService laedt alle AKRole-Objekte und stellt sie unterhalb der
 * TreeGroup dar.
 *
 *
 */
public class RoleTreeGroupService extends AbstractTreeService<AKRole, RoleTreeGroup> {

    @Resource(name = "de.augustakom.authentication.service.AKRoleService")
    private AKRoleService roleService;

    @Override
    protected List<AKRole> getChildren(Object parent, Object filter) throws TreeException {
        try {
            return roleService.findAll();
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, RoleTreeGroup data) throws TreeException {
        nodeToFill.setText(data.getDisplayName());
        nodeToFill.setTooltip(data.getTooltip());
        nodeToFill.setIconName(data.getIconURL());
        nodeToFill.setUserObject(data);
    }
}
