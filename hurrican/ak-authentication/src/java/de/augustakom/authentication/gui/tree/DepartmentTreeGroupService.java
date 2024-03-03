/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 08:06:30
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.service.AKDepartmentService;


/**
 * TreeService fuer die DepartmentTreeGroup. <br> Der TreeService laedt alle AKDepartment-Objekte und stellt sie
 * unterhalb der TreeGroup dar.
 */
public class DepartmentTreeGroupService extends AbstractTreeService<AKDepartment, DepartmentTreeGroup> {

    @Resource(name = "de.augustakom.authentication.service.AKDepartmentService")
    private AKDepartmentService depService;

    @Override
    protected List<AKDepartment> getChildren(Object parent, Object filter) throws TreeException {
        try {
            List<AKDepartment> departments = depService.findAll();
            return departments;
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, DepartmentTreeGroup data) throws TreeException {
        DepartmentTreeGroup tg = data;
        nodeToFill.setText(tg.getDisplayName());
        nodeToFill.setTooltip(tg.getTooltip());
        nodeToFill.setIconName(tg.getIconURL());
        nodeToFill.setUserObject(data);
    }
}
