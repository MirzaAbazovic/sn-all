/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 10:06:22
 */
package de.augustakom.authentication.gui.system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.tree.AdminTreePanel;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.common.gui.swing.AKJMenu;
import de.augustakom.common.gui.swing.AKJMenuBar;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJStatusBar;
import de.augustakom.common.gui.swing.AKJToolBar;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.SwingFactory;


/**
 * MDI-MainFrame fuer die Authentication-Verwaltung.
 */
public class MDIMainFrame extends AbstractMDIMainFrame {

    private static final Dimension DEFAULT_SIZE = new Dimension(1024, 768);

    private static final SwingFactory swingFactory =
            SwingFactory.getInstance("de/augustakom/authentication/gui/system/resources/MDIMainFrame.xml");

    private AKJStatusBar statusBar = null;
    private AKJMenuBar menuBar = null;

    private JSplitPane splitPane = null;
    private AdminTreePanel treePanel = null;

    /**
     * Konstruktor fuer das MainFrame.
     */
    protected MDIMainFrame() {
        super(true);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setSize(DEFAULT_SIZE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (DEFAULT_SIZE.getWidth() >= tk.getScreenSize().getWidth()) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainFrameWindowListener());
        addWindowListener(new FrameOpenedListener());

        setTitle(swingFactory.getText("title"));
        ImageIcon icon = swingFactory.createIcon("title");
        if (icon != null) {
            setIconImage(icon.getImage());
        }

        initStatusBar();

        splitPane = new JSplitPane();
        splitPane.setDividerSize(2);
        treePanel = new AdminTreePanel();
        splitPane.setLeftComponent(treePanel);
        splitPane.setRightComponent(new AKJScrollPane(getDesktopPane()));

        this.getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    /* Stellt generelle Informationen in der StatusBar dar. */
    private void initStatusBar() {
        if (getStatusBar() != null) {
            String user = System.getProperty(SystemConstants.DB_NAME + SystemConstants.JDBC_USER_SUFFIX);
            getStatusBar().setText(0, user, "de/augustakom/authentication/gui/images/admin.gif");

            AuthenticationSystem authSys = (AuthenticationSystem) GUISystemRegistry.instance().getValue(
                    GUISystemRegistry.AUTHENTICATION_SYSTEM);
            getStatusBar().setText(1, "System: " + ((authSys != null) ? authSys.getName() : "unbekannt"), null);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createMenuBar()
     */
    @Override
    protected void createMenuBar() {
        menuBar = swingFactory.createMenuBar("menubar.authentication", null);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getAKJMenuBar()
     */
    @Override
    protected AKJMenuBar getAKJMenuBar() {
        return menuBar;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getWindowMenu()
     */
    @Override
    protected AKJMenu getWindowMenu() {
        if (menuBar != null) {
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                AKJMenu menu = (AKJMenu) menuBar.getMenu(i);
                if ("window".equals(menu.getActionCommand())) {
                    return menu;
                }
            }
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getDefaultWindowMenuItemCount()
     */
    @Override
    protected int getDefaultWindowMenuItemCount() {
        return 0;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createToolBar()
     */
    @Override
    protected void createToolBar() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getToolBar()
     */
    @Override
    protected AKJToolBar getToolBar() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createStatusBar()
     */
    @Override
    protected void createStatusBar() {
        statusBar = new AKJStatusBar(5);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getStatusBar()
     */
    @Override
    public AKJStatusBar getStatusBar() {
        return statusBar;
    }

    class FrameOpenedListener extends WindowAdapter {
        /**
         * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
         */
        @Override
        public void windowOpened(WindowEvent e) {
            splitPane.setDividerLocation(0.25);
            treePanel.panelsIsShown();
        }
    }
}
