/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2004 07:58:07
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;


/**
 * ListCellRenderer fuer die Darstellung von HVT-Standorten in einer ComboBox. <br> Dargestellt wird der Name der
 * zugehoerigen HVT-Gruppe und die ID des HVT-Standortes.
 *
 *
 */
public class HVTStandortListCellRenderer extends DefaultListCellRenderer {

    private Map hvtGruppenMap = null;

    /**
     * Konstruktor.
     */
    public HVTStandortListCellRenderer() {
        super();
    }

    /**
     * Uebergibt dem Renderer eine Map mit allen HVT-Gruppen. <br> Als Key der Map wird die ID der HVT-Gruppe verwendet,
     * als Value Objekte des Typs <code>HVTGruppe</code>.
     *
     * @param hvtGruppenMap Map mit den HVT-Gruppen.
     */
    public void setHVTGruppen(Map hvtGruppenMap) {
        this.hvtGruppenMap = hvtGruppenMap;
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean,
     * boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof HVTStandort) {
            HVTStandort std = (HVTStandort) value;
            HVTGruppe g = null;
            if (hvtGruppenMap != null) {
                g = (HVTGruppe) hvtGruppenMap.get(std.getHvtGruppeId());
            }

            if (g != null) {
                StringBuilder text = new StringBuilder();
                text.append(g.getOrtsteil());
                text.append(" - ");
                text.append(std.getId());
                lbl.setText(text.toString());
            }
            else if (std.getId() != null) {
                lbl.setText(std.getId().toString());
            }
            else {
                lbl.setText(" ");
            }
        }

        return lbl;
    }

}


