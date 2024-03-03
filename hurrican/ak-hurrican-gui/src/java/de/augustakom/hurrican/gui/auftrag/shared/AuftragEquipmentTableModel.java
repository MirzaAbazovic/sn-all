/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.05.2005 17:49:28
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;


/**
 * TableModel fuer Objekte des Typs <code>AuftragEquipmentView</code>.
 *
 *
 */
public class AuftragEquipmentTableModel extends AKTableModel<AuftragEquipmentView> {

    private static final int COL_ORTSTEIL = 0;
    private static final int COL_SWITCH = 1;
    private static final int COL_HW_EQN = 2;
    private static final int COL_BUCHT = 3;
    private static final int COL_LEISTE1 = 4;
    private static final int COL_STIFT1 = 5;
    private static final int COL_ES_TYP = 6;
    private static final int COL_ENDSTELLE = 7;
    private static final int COL_ES_NAME = 8;
    private static final int VBZ = 9;
    private static final int COL_AUFTRAG_ID = 10;
    private static final int COL_PRODUKT = 11;
    private static final int COL_AUFTRAG_STATUS = 12;
    private static final int COL_PROFILE_NAME = 13;
    private static final int COL_GERAETEBEZ = 14;
    private static final int COL_MGMTBEZ = 15;
    private static final int COL_VPN = 16;

    private static final int COL_COUNT = 17;

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ORTSTEIL:
                return "HVT";
            case COL_SWITCH:
                return "Switch";
            case COL_HW_EQN:
                return "HW-EQN";
            case COL_BUCHT:
                return "Bucht";
            case COL_LEISTE1:
                return "Leiste 1";
            case COL_STIFT1:
                return "Stift 1";
            case COL_ES_TYP:
                return "ES-Typ";
            case COL_ENDSTELLE:
                return "Endstelle";
            case COL_ES_NAME:
                return "ES-Name";
            case VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_AUFTRAG_ID:
                return "Auftrag-ID";
            case COL_PRODUKT:
                return "Produkt";
            case COL_AUFTRAG_STATUS:
                return "Status";
            case COL_PROFILE_NAME:
                return "DSLAM-Profil";
            case COL_GERAETEBEZ:
                return "Gerätebezeichnung";
            case COL_MGMTBEZ:
                return "Managementbezeichnung";
            case COL_VPN:
                return "VPN ID";
            default:
                return "";
        }
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragEquipmentView) {
            AuftragEquipmentView view = (AuftragEquipmentView) tmp;
            switch (column) {
                case COL_ORTSTEIL:
                    return view.getHvtOrtsteil();
                case COL_SWITCH:
                    return view.getEqSwitch();
                case COL_HW_EQN:
                    return view.getEqHwEqn();
                case COL_BUCHT:
                    return view.getEqBucht();
                case COL_LEISTE1:
                    return view.getEqLeiste1();
                case COL_STIFT1:
                    return view.getEqStift1();
                case COL_ES_TYP:
                    return view.getEndstelleTyp();
                case COL_ENDSTELLE:
                    return view.getEndstelle();
                case COL_ES_NAME:
                    return view.getEndstelleName();
                case VBZ:
                    return view.getVbz();
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_PRODUKT:
                    return view.getAnschlussart();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatusText();
                case COL_PROFILE_NAME:
                    return view.getDslamProfile();
                case COL_GERAETEBEZ:
                    return view.getGeraeteBezeichung();
                case COL_MGMTBEZ:
                    return view.getMgmtBezeichnung();
                case COL_VPN:
                    return view.getVpnNr();
                default:
                    return "";
            }
        }
        return super.getValueAt(row, column);
    }

    /**
     * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_AUFTRAG_ID) {
            return Long.class;
        }
        else if (columnIndex == COL_VPN) {
            return Long.class;
        }
        return String.class;
    }

}


