/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2004 11:54:29
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;


/**
 * CellRenderer fuer die Darstellung von Produkt-Physiktyp-Mappings.
 *
 *
 */
public class Produkt2PhysikTypCellRenderer extends DefaultListCellRenderer {

    private Map produkte = null;
    private Map physikTypen = null;

    /**
     * Uebergibt dem Renderer eine Map mit allen Produkten. Als Key wird die ID der Produkte erwartet, als Value Objekte
     * vom Typ <code>Produkt</code>.
     *
     * @param produkte
     */
    public void setProdukte(Map produkte) {
        this.produkte = produkte;
    }

    /**
     * Uebergibt dem Renderer eine Map mit allen PhysikTypen. Als Key wird die ID der PhysikTypen erwartet, als Value
     * Objekte vom Typ <code>PhysikTyp</code>.
     *
     * @param physikTypen
     */
    public void setPhysikTypen(Map physikTypen) {
        this.physikTypen = physikTypen;
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean,
     * boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Produkt2PhysikTyp) {
            Produkt2PhysikTyp p2pt = (Produkt2PhysikTyp) value;

            if (p2pt.getProduktId() != null && p2pt.getPhysikTypId() != null) {
                StringBuilder text = new StringBuilder();
                if (produkte != null) {
                    Produkt p = (Produkt) produkte.get(p2pt.getProduktId());
                    text.append((p != null) ? p.getBezeichnungShort() : "?");
                }

                if (physikTypen != null) {
                    PhysikTyp pt = (PhysikTyp) physikTypen.get(p2pt.getPhysikTypId());
                    text.append("/");
                    text.append((pt != null) ? pt.getName() : "?");
                }

                if (p2pt.getVirtuell() != null && p2pt.getVirtuell().booleanValue()) {
                    text.append(" (virtuell)");
                }

                lbl.setText(text.toString());
            }
            else {
                lbl.setText(" ");
            }
        }

        return lbl;
    }
}


