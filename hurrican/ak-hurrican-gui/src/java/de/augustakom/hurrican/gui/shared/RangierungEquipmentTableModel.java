/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2010 11:55:21
 */
package de.augustakom.hurrican.gui.shared;

import java.text.*;
import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs {@link RangierungsEquipmentView}
 *
 *
 */
public class RangierungEquipmentTableModel extends AKTableModel<RangierungsEquipmentView> {

    private static final int COL_RANGIER_ID = 0;
    private static final int COL_PHYSIKTYP = 1;
    private static final int COL_FREIGABE = 2;
    private static final int COL_RAUMBEZEICHNUNG = 3;
    private static final int COL_HW_BG_TYP_IN = 4;
    private static final int COL_MAX_BANDWIDTH = 5;
    private static final int COL_HW_EQN_IN = 6;
    private static final int COL_HW_BEZEICHNUNG = 7;
    private static final int COL_HW_BEMERKUNG = 8;
    private static final int COL_HW_BG_TYP_OUT = 9;
    private static final int COL_HW_EQN_OUT = 10;
    private static final int COL_DTAG_OUT = 11;
    private static final int COL_UETV = 12;
    private static final int COL_KVZ_NUMMER = 13;
    private static final int COL_PHYSIKTYP_ADD = 14;
    private static final int COL_HW_EQN_IN_ADD = 15;
    private static final int COL_HW_BEZEICHNUNG_ADD = 16;
    private static final int COL_ES_ID = 17;
    private static final int COL_FREIGABE_AB = 18;
    private static final int COL_BEMERKUNG = 19;

    private static final int COL_COUNT = 20;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_RANGIER_ID:
                return "Rangier-ID";
            case COL_PHYSIKTYP:
                return "Physiktyp";
            case COL_FREIGABE:
                return "Freigabe";
            case COL_RAUMBEZEICHNUNG:
                return "Raum";
            case COL_HW_BG_TYP_IN:
                return "BG-Typ In";
            case COL_MAX_BANDWIDTH:
                return "max. Bb (kbit/s)";
            case COL_HW_EQN_IN:
                return "HW-EQN In";
            case COL_HW_BEZEICHNUNG:
                return "Hardware In";
            case COL_HW_BEMERKUNG:
                return "HW In Bemerkung";
            case COL_HW_BG_TYP_OUT:
                return "BG-Typ Out";
            case COL_HW_EQN_OUT:
                return "HW-EQN Out";
            case COL_DTAG_OUT:
                return "DTAG";
            case COL_UETV:
                return "UETV";
            case COL_KVZ_NUMMER:
                return "KVZ Nummer";
            case COL_PHYSIKTYP_ADD:
                return "Physiktyp 2";
            case COL_HW_EQN_IN_ADD:
                return "HW-EQN In 2";
            case COL_HW_BEZEICHNUNG_ADD:
                return "Hardware In 2";
            case COL_ES_ID:
                return "ES-Id";
            case COL_FREIGABE_AB:
                return "Frei ab";
            case COL_BEMERKUNG:
                return "Bemerkung";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof RangierungsEquipmentView) {
            RangierungsEquipmentView r = (RangierungsEquipmentView) tmp;

            switch (column) {
                case COL_RANGIER_ID:
                    return r.getRangierId();
                case COL_PHYSIKTYP:
                    return r.getPhysikTyp();
                case COL_FREIGABE:
                    return r.getFreigabe();
                case COL_RAUMBEZEICHNUNG:
                    return r.getRaum();
                case COL_HW_BG_TYP_IN:
                    return r.getHwBgTypEqIn();
                case COL_MAX_BANDWIDTH:
                    return (r.getHwEqInMaxBandwidth() != null && r.getHwEqInMaxBandwidth().getDownstream() != null)
                            ? NumberFormat.getInstance().format(r.getHwEqInMaxBandwidth().getDownstream()) : null;
                case COL_HW_EQN_IN:
                    return r.getHwEqnIn();
                case COL_HW_BEZEICHNUNG:
                    return r.getRackEqIn();
                case COL_HW_BEMERKUNG:
                    return r.getBemerkungEqIn();
                case COL_HW_BG_TYP_OUT:
                    return r.getHwBgTypEqOut();
                case COL_HW_EQN_OUT:
                    return r.getHwEqnOut();
                case COL_DTAG_OUT:
                    return r.getDtagPorts();
                case COL_UETV:
                    return r.getUetv();
                case COL_KVZ_NUMMER:
                    return r.getKvzNummerEqOut();
                case COL_PHYSIKTYP_ADD:
                    return r.getPhysikTypAdd();
                case COL_HW_EQN_IN_ADD:
                    return r.getHwEqnInAdd();
                case COL_HW_BEZEICHNUNG_ADD:
                    return r.getRackEqInAdd();
                case COL_ES_ID:
                    return r.getEsId();
                case COL_FREIGABE_AB:
                    return r.getFreigabeAb();
                case COL_BEMERKUNG:
                    return r.getBemerkung();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_FREIGABE:
                return Boolean.class;
            case COL_RANGIER_ID:
                return Long.class;
            case COL_ES_ID:
                return Long.class;
            case COL_FREIGABE_AB:
                return Date.class;
            default:
                return String.class;
        }
    }

}


