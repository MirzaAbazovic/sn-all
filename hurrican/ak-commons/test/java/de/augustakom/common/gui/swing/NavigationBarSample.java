/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 12:38:54
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKNavigationBarListener;


/**
 *
 */
public class NavigationBarSample extends AKJFrame implements AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(NavigationBarSample.class);

    public static void main(String[] args) {
        NavigationBarSample sample = new NavigationBarSample();
        sample.showGUI();
    }

    private void showGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ArrayList values = new ArrayList();
        for (int i = 0; i < 10; i++) {
            values.add(new Object());
        }

        AKJNavigationBar navBar = new AKJNavigationBar(this, true, true);
        navBar.setData(values);
        this.getContentPane().add(navBar);
        this.setSize(new Dimension(300, 100));
        this.setVisible(true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    public void showNavigationObject(Object obj, int number) {
        LOGGER.debug("show obj: " + number);
        LOGGER.debug("--> Object to show: " + obj);
    }
}


