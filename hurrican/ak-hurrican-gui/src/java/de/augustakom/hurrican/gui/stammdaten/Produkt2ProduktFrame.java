/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2005 10:52:44
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Frame zur Konfiguration der moeglichen Ziel-Produkte zu einem best. Produkt.
 *
 *
 */
public class Produkt2ProduktFrame extends AKJAbstractInternalFrame {

    private Produkt produkt = null;

    /**
     * Konstruktor mit Angabe des Produkts, das konfiguriert werden soll.
     *
     * @param toConfig
     * @throws IllegalArgumentException
     */
    public Produkt2ProduktFrame(Produkt toConfig) {
        super(null);
        if (toConfig == null) {
            throw new IllegalArgumentException("Dem Frame muss ein Produkt uebergeben werden!");
        }
        this.produkt = toConfig;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Produkt-2-Produkt Konfiguration für: " + produkt.getBezeichnungShort());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new Produkt2ProduktPanel(produkt));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return (produkt != null) ? "Produkt" + produkt.getId() : super.getUniqueName();
    }

}


