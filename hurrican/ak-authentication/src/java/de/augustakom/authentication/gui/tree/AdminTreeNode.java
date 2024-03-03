/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 09:38:24
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * TreeNode-Implementierung fuer den AdminTree.
 */
public class AdminTreeNode extends AKJDefaultMutableTreeNode implements Observer {

    private static final Logger LOGGER = Logger.getLogger(AdminTreeNode.class);

    /**
     * Standardkonstruktor
     */
    public AdminTreeNode() {
        super();
    }

    /**
     * Konstruktor mit Angabe des User-Objects
     */
    public AdminTreeNode(Object userObject) {
        super(userObject);
    }

    /**
     * Ist das UserObject vom Typ <code>java.util.Observable</code>, wird der TreeNode als Observer angemeldet.
     *
     * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
     */
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (userObject instanceof Observable) {
            ((Observable) userObject).addObserver(this);
        }
    }

    /**
     * Veranlasst den TreeNode dazu, die aktuellen Daten des User-Objects darzustellen.
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        fillNode();
    }

    /**
     * Ruft den TreeService fuer das User-Object auf. Dadurch werden die Eigenschaften text, tooltip etc. des Nodes
     * gesetzt.
     */
    private void fillNode() {
        if (getUserObject() != null) {
            try {
                @SuppressWarnings("unchecked")
                AbstractTreeService<Object, Object> treeService = ServiceLocator.instance().getService(
                        getUserObject().getClass().getName(), AbstractTreeService.class);
                treeService.fillNode(this, getUserObject());

                ((DefaultTreeModel) GUISystemRegistry.instance().getValue(
                        GUISystemRegistry.REGKEY_TREE_MODEL)).nodeChanged(this);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
