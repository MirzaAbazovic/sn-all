/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 14:10:08
 */
package de.augustakom.hurrican.gui.base.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.actions.TreeSearchAction;


/**
 * Panel zur Darstellung eines dynamischen Trees.
 *
 *
 */
public class DynamicTreePanel<T extends DynamicTreeNode> extends AKJAbstractPanel {

    private static final long serialVersionUID = 4554930944950913126L;

    private static final Logger LOGGER = Logger.getLogger(DynamicTreePanel.class);

    private final Class<T> rootNodeClass;
    private DynamicTree tree = null;
    private DynamicTreeNode rootNode = null;
    private TreeModel treeModel = null;

    private final DynamicTreeMouseListener mouseListener;

    /**
     * Konstruktor mit Angabe des Tree-Namens, der aufgebaut werden soll.
     *
     * @param rootNodeClass RootNode Klasse, die instanziert werden soll
     * @param mouseListener MouseListener, der dem Tree hinzugefuegt werden soll
     */
    public DynamicTreePanel(Class<T> rootNodeClass, DynamicTreeMouseListener mouseListener) {
        super(null, new BorderLayout());
        this.rootNodeClass = rootNodeClass;
        this.mouseListener = mouseListener;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        createTree();
        this.add(new AKJScrollPane(tree), BorderLayout.CENTER);
    }

    /**
     * Erzeugt den Tree
     */
    private void createTree() {
        tree = new DynamicTree();

        createTreeModel();

        tree.addTreeWillExpandListener(new DynamicTreeWillExpandListener());
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new DynamicTreeRenderer());
        tree.addMouseListener(mouseListener);
        tree.addKeyListener(new RefreshTreeKeyListener());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(tree);

        expandRoot();
    }

    /**
     * Erstellt das TreeModel und setzt es auf dem Tree
     */
    private void createTreeModel() {
        try {
            rootNode = rootNodeClass.getConstructor(DynamicTree.class).newInstance(tree);
            treeModel = new DefaultTreeModel(rootNode, false);
            tree.setModel(treeModel);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Aktualisiert den Tree. <br> Achtung: Das gesamte TreeModel wird durch diese Methode neu aufgebaut!
     */
    public void refreshTree() {
        createTreeModel();
        tree.setModel(treeModel);
        expandRoot();
    }

    /**
     * Expandiert den Root-Knoten des Trees.
     */
    private void expandRoot() {
        try {
            tree.fireTreeWillExpand(new TreePath(rootNode.getPath()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * KeyListener fuer den Tree. <br> Der KeyListener reagiert auf die Betaetigung von 'F5' und aktualisiert dann den
     * Tree.
     */
    class RefreshTreeKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F5) {
                refreshTree();
            }
            else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F)) {
                DynamicTreeNode node = (DynamicTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    node = rootNode;
                }
                Set<DynamicTreeNode> set = new HashSet<>();
                set.add(node);
                e.consume();
                new TreeSearchAction(set).actionPerformed(new ActionEvent(this, 0, "find.hardware"));
            }
        }
    }

}
