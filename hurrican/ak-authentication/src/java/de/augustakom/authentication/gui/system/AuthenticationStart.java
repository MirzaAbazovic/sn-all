/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 10:10:51
 */
package de.augustakom.authentication.gui.system;

import java.util.*;
import javax.swing.*;

import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;


/**
 * Start-Klasse fuer die Authentication-GUI.
 */
public class AuthenticationStart {

    /**
     * Startet die AuthenticationGUI.
     */
    public static void main(String[] args) {
        InitializeLog4J.initializeLog4J("log4j-authentication");
        loadProperties();
        UIManager.put("Button.showMnemonics", Boolean.TRUE);

        JFrame f = new JFrame();
        f.setLocation(-500, -500);
        f.setVisible(true);

        LoginScreen screen = new LoginScreen(f);
        f.setTitle(screen.getTitle());
        DialogHelper.showWindow(screen, true);
    }

    private static void loadProperties() {
        Properties props = PropertyUtil.loadPropertyHierarchy(Arrays.asList("common", "ak-authentication"), "properties", true);
        SystemPropertyTools.instance().setSystemProperties(props);
    }

}
