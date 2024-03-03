/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 16:13:41
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;
import javax.annotation.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.AKAccountService;


/**
 * TreeService-Implementierung fuer ein AKDb-Objekt.
 */
public class DbTreeService extends AbstractTreeService<AKAccount, AKDb> {

    @Resource(name = "de.augustakom.authentication.service.AKAccountService")
    private AKAccountService accountService;

    @Override
    public List<AKAccount> getChildren(Object parent, Object filter) throws TreeException {
        try {
            return accountService.findByDB(((AKDb) parent).getId());
        }
        catch (Exception e) {
            throw new TreeException(TreeException._UNKNOWN, e);
        }
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, AKDb data) throws TreeException {
        nodeToFill.setText(data.getName());
        nodeToFill.setTooltip(data.getDescription());
        nodeToFill.setIconName("de/augustakom/authentication/gui/images/dbaccount.gif");
        nodeToFill.setLeaf(false);
        nodeToFill.setUserObject(data);
    }

}
