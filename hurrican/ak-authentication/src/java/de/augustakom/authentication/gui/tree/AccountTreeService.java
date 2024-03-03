/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 16:13:41
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKAccount;


/**
 * TreeService-Implementierung fuer ein AKAccount-Objekt.
 */
public class AccountTreeService extends AbstractTreeService<AKAccount, AKAccount> {

    @Override
    public List<AKAccount> getChildren(Object parent, Object filter) throws TreeException {
        return Collections.emptyList();
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, AKAccount data) throws TreeException {
        nodeToFill.setText(data.getName());
        nodeToFill.setTooltip(data.getDescription());
        nodeToFill.setIconName("de/augustakom/authentication/gui/images/dbaccount.gif");
        nodeToFill.setLeaf(true);
        nodeToFill.setUserObject(data);
    }

}
