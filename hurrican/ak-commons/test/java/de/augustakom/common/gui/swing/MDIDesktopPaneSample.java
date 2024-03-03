/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;


/**
 * Test- bzw. Beispiel-Klasse fuer die MDIDesktopPane.
 *
 *
 */
public class MDIDesktopPaneSample extends AKJFrame {

    private MDIDesktopPane desktop = null;

    public static void main(String[] args) {
        MDIDesktopPaneSample sample = new MDIDesktopPaneSample();
        sample.setSize(new Dimension(800, 600));
        sample.setVisible(true);
    }

    /**
     * Konstruktor
     */
    public MDIDesktopPaneSample() {
        super();
        createGUI();
    }

    /* Erstellt die GUI */
    private void createGUI() {
        desktop = new MDIDesktopPane();
        this.setDefaultCloseOperation(AKJFrame.EXIT_ON_CLOSE);
        setTitle("Test for MDIDesktopPane");

        AKJInternalFrame frameOne = new AKJInternalFrame("Frame 1", true, true, true);
        frameOne.setSize(new Dimension(200, 300));
        frameOne.getContentPane().add(new AKJLabel("InternalFrame One"));

        AKJInternalFrame frameTwo = new AKJInternalFrame("Frame 2", true, true, true);
        frameTwo.setSize(new Dimension(200, 300));
        frameTwo.getContentPane().add(new AKJLabel("InternalFrame Two"));

        this.getContentPane().add(new JScrollPane(desktop), BorderLayout.CENTER);
        desktop.add(frameOne);
        desktop.add(frameTwo);
    }
}
