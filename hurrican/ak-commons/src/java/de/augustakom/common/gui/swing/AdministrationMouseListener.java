/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2004 08:49:32
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKCommonGUIConstants;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.tools.lang.BooleanTools;


/**
 * MouseListener (und MenuKeyListener!) fuer alle GUI-Komponenten vom Typ AKManageableComponent. <br> Der Listener ruft
 * einen Dialog auf, in dem die Rollenrechte fuer die Komponente definiert werden koennen. <br> Voraussetzung dafuer
 * ist, dass folgende System-Property gesetzt sind: <ul> <li><code>AK.Is.Admin</code> auf <code>true</code>
 * (AKCommonGUIConstants.ADMIN_FLAG) <li><code>AK.Application.Id</code> auf die enstprechende Applikations-ID
 * (AKCommonGUIConstants.ADMIN_APPLICATION_ID). </ul> <br><br> Der MouseListener benoetigt die Kombintation
 * <strong>Control+Shift+ mittlere Maustaste</strong>, der KeyListener die Kombination
 * <strong>Control+Shift+#</strong>.
 *
 *
 */
public class AdministrationMouseListener extends MouseAdapter implements MenuKeyListener, AKCommonGUIConstants {

    private static final Logger LOGGER = Logger.getLogger(AdministrationMouseListener.class);

    private Long applicationId = null;

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if ((e.getSource() instanceof AKManageableComponent) &&
                (e.getButton() == MouseEvent.BUTTON2) && e.isControlDown() && e.isShiftDown()) {
            e.consume();
            showAdminDialog((AKManageableComponent) e.getSource());
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if ((e.getSource() instanceof AKManageableComponent) &&
                (e.getButton() == MouseEvent.BUTTON2) && e.isControlDown() && e.isShiftDown()) {
            e.consume();
            showAdminDialog((AKManageableComponent) e.getSource());
        }
    }

    /**
     * @see javax.swing.event.MenuKeyListener#menuKeyReleased(javax.swing.event.MenuKeyEvent)
     */
    public void menuKeyReleased(MenuKeyEvent e) {
        if (e.isShiftDown() && e.isControlDown() && (MenuKeyEvent.VK_NUMBER_SIGN == e.getKeyCode())) {
            e.consume();

            AKManageableComponent comp2Manage = null;
            MenuElement[] me = e.getPath();
            for (int i = me.length - 1; i >= 0; i--) {
                if (comp2Manage == null) {
                    if (me[i] instanceof AKJMenu) {
                        AKJMenu menu = (AKJMenu) me[i];
                        for (int j = 0; j < menu.getItemCount(); j++) {
                            JMenuItem mi = menu.getItem(j);
                            if ((mi != null) && mi.isArmed() && (mi instanceof AKManageableComponent)) {
                                comp2Manage = (AKManageableComponent) mi;
                                break;
                            }
                        }

                        if ((comp2Manage == null) && menu.isSelected()) {
                            comp2Manage = menu;
                        }
                    }
                }
                else {
                    break;
                }
            }

            if (comp2Manage != null) {
                showAdminDialog(comp2Manage);
            }
        }
    }

    /**
     * @see javax.swing.event.MenuKeyListener#menuKeyPressed(javax.swing.event.MenuKeyEvent)
     */
    public void menuKeyPressed(MenuKeyEvent e) {
    }

    /**
     * @see javax.swing.event.MenuKeyListener#menuKeyTyped(javax.swing.event.MenuKeyEvent)
     */
    public void menuKeyTyped(MenuKeyEvent e) {
    }

    /*
     * Ueberprueft, ob der User die benoetigten Rechte besitzt, um
     * die Administration vorhnehmen zu koennen.
     */
    private boolean isAdministrationAllowed() {
        String admin = System.getProperty(ADMIN_FLAG);
        Boolean isAdmin = StringUtils.equalsIgnoreCase(admin, BooleanTools.DEFAULT_TRUE_STRING);
        if (!BooleanTools.nullToFalse(isAdmin)) {
            LOGGER.info("User ist nicht als Administrator eingeloggt. Admin-Dialog fuer Komponente wird nicht angezeigt!");
            return false;
        }

        String appId = System.getProperty(ADMIN_APPLICATION_ID);
        applicationId = null;
        try {
            applicationId = Long.valueOf(appId);
            return true;
        }
        catch (NumberFormatException e) {
            LOGGER.info("Applikations-ID ist nicht gesetzt. Admin-Dialog fuer Komponente wird nicht angezeigt!");
            return false;
        }
    }

    /*
     * Zeigt den Administrations-Dialog fuer die Komponente an.
     * @param comp
     */
    private void showAdminDialog(AKManageableComponent comp) {
        if (isAdministrationAllowed()) {
            try {
                String dlgClass = "de.augustakom.authentication.gui.role.RoleRightsDialog";
                Class<?> c = Class.forName(dlgClass);

                Constructor<?> dlgConst = c.getConstructor(new Class[] { AKManageableComponent.class, Long.class });
                if (dlgConst != null) {
                    Object dlg = dlgConst.newInstance(new Object[] { comp, applicationId });
                    if (dlg != null) {
                        if (dlg instanceof AKJDialog) {
                            DialogHelper.showDialog(null, (AKJDialog) dlg, true, true);
                        }
                        else if (dlg instanceof AKJOptionDialog) {
                            DialogHelper.showDialog(null, (AKJOptionDialog) dlg, true, true);
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}


