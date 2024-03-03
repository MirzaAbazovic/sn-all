/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 08:09:05
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;

import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.model.AKUser;


/**
 * TreeService-Implementierung fuer ein User-Objekt.
 *
 *
 */
public class UserTreeService extends AbstractTreeService<AKUser, AKUser> {

    @Override
    protected List<AKUser> getChildren(Object parent, Object filter) throws TreeException {
        return Collections.emptyList();
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, AKUser data) throws TreeException {
        nodeToFill.setText(data.getName() + " " + data.getFirstName());
        nodeToFill.setTooltip(data.getLoginName());
        nodeToFill.setLeaf(true);
        nodeToFill.setUserObject(data);

        if (data.isActive()) {
            nodeToFill.setIconName("de/augustakom/authentication/gui/images/user.gif");
        }
        else {
            nodeToFill.setIconName("de/augustakom/authentication/gui/images/user_inactive.gif");
        }
    }
}
