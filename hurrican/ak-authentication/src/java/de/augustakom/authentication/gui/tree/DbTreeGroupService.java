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
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.AKDbService;

/**
 * TreeService fuer die AccountTreeGroup. <br> Der TreeService laedt alle AKAccount-Objekte und stellt sie unterhalb der
 * TreeGroup dar.
 */
public class DbTreeGroupService extends AbstractTreeService<AKDb, DbTreeGroup> {

    @Resource(name = "de.augustakom.authentication.service.AKDbService")
    private AKDbService dbService;

    @Override
    protected List<AKDb> getChildren(Object parent, Object filter) throws TreeException {
        try {
            return dbService.findAll();
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, DbTreeGroup data) throws TreeException {
        nodeToFill.setText(data.getDisplayName());
        nodeToFill.setTooltip(data.getTooltip());
        nodeToFill.setIconName(data.getIconURL());
        nodeToFill.setUserObject(data);
    }
}
