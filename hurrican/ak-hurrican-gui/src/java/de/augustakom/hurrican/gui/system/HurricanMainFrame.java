/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 10:22:15
 */
package de.augustakom.hurrican.gui.system;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AuthenticationSystem;
import de.augustakom.common.gui.iface.AKCommonGUIConstants;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJMenu;
import de.augustakom.common.gui.swing.AKJMenuBar;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AKJStatusBar;
import de.augustakom.common.gui.swing.AKJToolBar;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.ManageGuiComponentHelper;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * MDI-MainFrame fuer die Hurrican-Applikation.
 *
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
        justification = "only one instance from HurricanMainFrame exists")
public class HurricanMainFrame extends AbstractMDIMainFrame {

    private static final Logger LOGGER = Logger.getLogger(HurricanMainFrame.class);
    private static final Dimension DEFAULT_SIZE = new Dimension(1280, 900);
    private static final int AREA_COUNT = 5;

    private SwingFactory swingFactory;
    private static final long serialVersionUID = -4865546612677808045L;

    private static AKJStatusBar statusBar = null;
    private static AKJMenuBar menuBar = null;
    private static AKJToolBar toolBar = null;

    /**
     * Konstruktor fuer das MainFrame.
     */
    public HurricanMainFrame() {
        super(true);
        createGUI();
    }

    @Override
    protected final void initFrame() {
        if (!initialized) {
            try {
                FeatureService featureService = CCServiceFinder.instance().getCCService(FeatureService.class);
                swingFactory = SwingFactory.getInstance(
                        "de/augustakom/hurrican/gui/system/resources/HurricanMainFrame.xml",
                        featureService.getAllOnlineFeatures());
                super.initFrame();
            }
            catch (ServiceNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        setSize(DEFAULT_SIZE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (DEFAULT_SIZE.getWidth() >= tk.getScreenSize().getWidth()) {
            setExtendedState(MAXIMIZED_BOTH);
        }
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainFrameWindowListener());

        setTitle(swingFactory.getText("title"));
        ImageIcon icon = swingFactory.createIcon("title");
        if (icon != null) {
            setIconImage(icon.getImage());
        }

        initStatusBar();
    }

    /**
     * Stellt generelle Informationen in der StatusBar dar.
     */
    private void initStatusBar() {
        // aktuellen User anzeigen
        Object tmp = HurricanSystemRegistry.instance().getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
        if (tmp instanceof AKLoginContext) {
            AKLoginContext loginCtx = (AKLoginContext) tmp;
            AKUser user = loginCtx.getUser();
            String icon = (System.getProperty(AKCommonGUIConstants.ADMIN_FLAG) != null) ? "admin.icon" : "user.icon";

            StringBuilder sysMode = new StringBuilder("Modus: ");
            AuthenticationSystem authSys = (AuthenticationSystem) HurricanSystemRegistry.instance().getValue(
                    HurricanSystemRegistry.AUTHENTICATION_SYSTEM);
            sysMode.append((authSys != null) ? authSys.getDisplayName() : "unbekannt");

            getStatusBar().setText(0, (user != null) ? user.getName() : "unknown", swingFactory.getText(icon));
            getStatusBar().setText(1, sysMode.toString(), null);
            getStatusBar().setText(3, DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR), null);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createMenuBar()
     */
    @Override
    protected void createMenuBar() {
        menuBar = swingFactory.createMenuBar("main.menu.bar", null);

        List<AKJMenu> menus2Check = new ArrayList<>();
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            AKJMenu menu = (AKJMenu) menuBar.getMenu(i);
            // System-, Window- und Hilfe-Menu muessen immer verfuegbar sein.
            if (!"menu.system".equals(menu.getActionCommand()) && !"menu.window".equals(menu.getActionCommand())
                    && !"menu.help".equals(menu.getActionCommand())) {

                menus2Check.add(menu);
            }
        }
        checkMenuRights(menus2Check);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#addAdditionalMenu(de.augustakom.common.gui.swing.AKJMenu)
     */
    @Override
    protected void addAdditionalMenu(AKJMenu toAdd) {
        super.addAdditionalMenu(toAdd);

        List<AKJMenu> menu = new ArrayList<>();
        menu.add(toAdd);

        checkMenuRights(menu);
    }

    /**
     * Ueberprueft die Benutzerrechte fuer ein best. Menu und aendert ggf. dessen Eigenschaften.
     */
    private void checkMenuRights(List<AKJMenu> menus) {
        List<AKManageableComponent> items2Check = new ArrayList<>();
        for (AKJMenu menu : menus) {
            menu.setParentClass(this.getClass());
            items2Check.add(menu);

            for (int k = 0; k < menu.getItemCount(); k++) {
                Object tmp = menu.getItem(k);
                if (tmp instanceof AKJMenuItem) {
                    AKJMenuItem item = (AKJMenuItem) tmp;
                    item.setParentClass(this.getClass());
                    items2Check.add(item);
                }
                else if (tmp instanceof AKJMenu) {
                    List<AKJMenu> subMenu = new ArrayList<>();
                    subMenu.add((AKJMenu) tmp);

                    checkMenuRights(subMenu);
                }
            }
        }

        manageGUI(null, items2Check.toArray(new AKManageableComponent[items2Check.size()]));
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
                if ("menu.window".equals(menu.getActionCommand())) {
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
        return 3;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createToolBar()
     */
    @Override
    protected void createToolBar() {
        toolBar = swingFactory.createToolBar("main.tool.bar");

        List<AKManageableComponent> comps = new ArrayList<>();
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            if (toolBar.getComponentAtIndex(i) instanceof AKManageableComponent) {
                AKManageableComponent comp = (AKManageableComponent) toolBar.getComponentAtIndex(i);
                comps.add(comp);
            }
        }

        manageGUI(this.getClass().getName(), comps.toArray(new AKManageableComponent[comps.size()]));
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getToolBar()
     */
    @Override
    protected AKJToolBar getToolBar() {
        return toolBar;
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#createStatusBar()
     */
    @Override
    protected void createStatusBar() {
        statusBar = new AKJStatusBar(AREA_COUNT);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getStatusBar()
     */
    @Override
    public AKJStatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(String parent, AKManageableComponent[] components) {
        ManageGuiComponentHelper.manageGUI(parent, components);
    }

    /**
     * @see de.augustakom.common.gui.swing.AbstractMDIMainFrame#getMenuIndex4AdditionalMenu()
     */
    @Override
    protected int getMenuIndex4AdditionalMenu() {
        return 1;
    }

}
