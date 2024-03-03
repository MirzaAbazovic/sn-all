/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 11:44:32
 */
package de.augustakom.hurrican.gui.system;

import java.util.*;
import javax.swing.*;

import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;

/**
 * Start-Klasse fuer die Hurrican-GUI. <br>
 * <p/>
 * Ueber diese Klasse wird der Login-Screen initialisiert - dieser ist fuer den weiteren Start-Vorgang verantwortlich.
 *
 *
 */
public class HurricanStart {

    public static void main(String[] args) {
        InitializeLog4J.initializeLog4J("log4j-gui");
        loadProperties();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.put("Button.showMnemonics", Boolean.TRUE);

                JFrame f = new JFrame();
                f.setLocation(-500, -500);
                f.setVisible(true);

                LoginScreen screen = new LoginScreen(f);
                f.setTitle(screen.getTitle());
                DialogHelper.showWindow(screen, true);

                screen.init();
            }
        });
    }

    private static void loadProperties() {
        Properties props = PropertyUtil.loadPropertyHierarchy(Arrays.asList("common", "ak-hurrican-gui"), "properties",
                true);
        SystemPropertyTools.instance().setSystemProperties(props);
    }
}
