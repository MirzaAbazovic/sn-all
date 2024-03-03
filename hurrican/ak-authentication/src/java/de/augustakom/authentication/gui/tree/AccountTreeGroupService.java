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
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.service.AKAccountService;

/**
 * TreeService fuer die AccountTreeGroup. <br> Der TreeService laedt alle AKAccount-Objekte und stellt sie unterhalb der
 * TreeGroup dar.
 */
public class AccountTreeGroupService extends AbstractTreeService<AKAccount, AccountTreeGroup> {

    @Resource(name = "de.augustakom.authentication.service.AKAccountService")
    private AKAccountService accountService;

    @Override
    protected List<AKAccount> getChildren(Object parent, Object filter) throws TreeException {
        try {
            return accountService.findAll();
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, AccountTreeGroup data) throws TreeException {
        nodeToFill.setText(data.getDisplayName());
        nodeToFill.setTooltip(data.getTooltip());
        nodeToFill.setIconName(data.getIconURL());
        nodeToFill.setUserObject(data);
    }
}
