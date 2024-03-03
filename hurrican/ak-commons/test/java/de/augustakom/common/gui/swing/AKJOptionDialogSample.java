/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2004 07:56:00
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;


/**
 * Test-Klasse fuer AKJOptionDialog
 *
 *
 */
public class AKJOptionDialogSample extends AKJAbstractOptionDialog {

    public AKJOptionDialogSample() {
        super("de/augustakom/common/gui/swing/SwingFactoryTest.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected void createGUI() {
        setTitle("sample option dialog");
        setSize(new Dimension(250, 250));

        AKJButton close = new AKJButton("close");
        close.setActionCommand("close");
        close.addActionListener(getActionListener());
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(close, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
        if ("close".equals(command)) {
            System.exit(0);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    public static void main(String[] args) {
        AKJOptionDialogSample sample = new AKJOptionDialogSample();
        DialogHelper.showDialog(null, sample, false, true);
    }
}
