/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2005 09:47:22
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.util.*;

import de.augustakom.common.gui.iface.AKSaveSelectedTableRow;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;

/**
 * Table Model für die Freigabe von Rangierungen
 */
public class RangierungFreigabeTableModel extends AKTableModel<PhysikFreigebenView> {

    public static final int COL_RANGIER_ID = 0;
    public static final int COL_AUFTRAG_ID = 1;
    public static final int COL_DELETE = 2;
    public static final int COL_IN_BEARBEITUNG = 3;
    public static final int COL_KUNDEN_NO = 4;
    public static final int COL_PRODUKT_HURRICAN = 5;
    public static final int COL_PRODUKT_BILLING = 6;
    public static final int COL_AUFTRAG_STATUS = 7;
    public static final int COL_BA_STATUS = 8;
    public static final int COL_NIEDERLASSUNG = 9;
    public static final int COL_HVT = 10;
    public static final int COL_KUENDIGUNG_CB = 11;
    public static final int COL_CLARIFY_INFO = 12;
    public static final int COL_RANG_BEMERKUNG = 13;

    public static final int COL_COUNT = 14;

    private final AKSaveSelectedTableRow listener;

    public RangierungFreigabeTableModel(AKSaveSelectedTableRow listener) {
        this.listener = listener;
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_RANGIER_ID:
                return "Rangierung-ID";
            case COL_AUFTRAG_ID:
                return "Auftrag-ID";
            case COL_DELETE:
                return "Freigeben";
            case COL_KUNDEN_NO:
                return "Kunden-ID";
            case COL_PRODUKT_HURRICAN:
                return "Produkt-Hurrican";
            case COL_PRODUKT_BILLING:
                return "Produkt-Billing";
            case COL_AUFTRAG_STATUS:
                return "Auftrag-Status";
            case COL_BA_STATUS:
                return "BA-Status";
            case COL_NIEDERLASSUNG:
                return "Niederlassung";
            case COL_KUENDIGUNG_CB:
                return "Kündigung-CB";
            case COL_CLARIFY_INFO:
                return "Klärfall-Info";
            case COL_RANG_BEMERKUNG:
                return "Rang_Bemerkung";
            case COL_IN_BEARBEITUNG:
                return "In Bearbeitung";
            case COL_HVT:
                return "HVT";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof PhysikFreigebenView) {
            PhysikFreigebenView view = (PhysikFreigebenView) tmp;
            switch (column) {
                case COL_RANGIER_ID:
                    return view.getRangierId();
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_DELETE:
                    return view.getFreigeben();
                case COL_KUNDEN_NO:
                    return view.getKundenNo();
                case COL_PRODUKT_HURRICAN:
                    return view.getTechProduct();
                case COL_PRODUKT_BILLING:
                    return view.getBillingProduct();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatus();
                case COL_BA_STATUS:
                    return view.getBaStatus();
                case COL_NIEDERLASSUNG:
                    return view.getNiederlassung();
                case COL_KUENDIGUNG_CB:
                    return view.getCbKuendigung();
                case COL_CLARIFY_INFO:
                    return view.getClarifyInfo();
                case COL_RANG_BEMERKUNG:
                    return view.getRangierungBemerkung();
                case COL_IN_BEARBEITUNG:
                    return view.getInBearbeitung();
                case COL_HVT:
                    return view.getHvt();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof PhysikFreigebenView) {
            PhysikFreigebenView view = (PhysikFreigebenView) tmp;
            switch (column) {
                case COL_DELETE:
                    setFreigeben(view, (Boolean) aValue);
                    break;
                case COL_IN_BEARBEITUNG:
                    setBearbeitung(view, (Boolean) aValue);
                    break;
                default:
                    break;
            }
        }
    }

    private void setFreigeben(PhysikFreigebenView view, Boolean freigeben) {
        if ((view != null) && (freigeben != null)) {
            if (freigeben) {
                // Freigabe setzen, wenn ...
                // bestimmte Stati erfüllt - Wunsch Tichy
                if (isFreigabeErlaubt(view)) {
                    view.setFreigeben(freigeben);
                }
                else {
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(),
                            new HurricanGUIException("Um die Rangierung freizugeben muss der Auftrag auf 'Storno', "
                                    + "'Absage' oder 'Auftrag Gekündigt' stehen!")
                    );
                }
            }
            else {
                // Freigabe zurücknehmen
                view.setFreigeben(freigeben);
            }
        }
    }

    private void setBearbeitung(PhysikFreigebenView view, Boolean bearbeiten) {
        if ((view != null) && (bearbeiten != null)) {
            view.setInBearbeitung(bearbeiten);
            listener.saveRow(view);
        }
    }

    public boolean isFreigabeErlaubt(PhysikFreigebenView view) {
        if ((view != null) && isAuftragStatusValid(view)) {
            return true;
        }
        return false;
    }

    private boolean isAuftragStatusValid(PhysikFreigebenView view) {
        Number[] stati = new Number[] { null, AuftragStatus.STORNO, AuftragStatus.ABSAGE, AuftragStatus.AUFTRAG_GEKUENDIGT };
        return NumberTools.isIn(view.getAuftragStatusId(), stati);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return ((column == COL_DELETE) || (column == COL_IN_BEARBEITUNG)) ? true : false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_RANGIER_ID:
                return Long.class;
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_DELETE:
                return Boolean.class;
            case COL_KUNDEN_NO:
                return Long.class;
            case COL_KUENDIGUNG_CB:
                return Date.class;
            case COL_IN_BEARBEITUNG:
                return Boolean.class;
            default:
                return String.class;
        }
    }

}
