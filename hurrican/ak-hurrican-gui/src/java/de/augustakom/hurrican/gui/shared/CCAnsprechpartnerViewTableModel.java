/*

 * Copyright (c) 2015 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 24.11.2015

 */

package de.augustakom.hurrican.gui.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;

/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>CCAnsprechpartnerView</code>. Das TableModel wird
 * ben&ouml;tigt, um die Objekte in der Tabelle ausw&auml;hlen zu k&ouml;nnen.
 */
public class CCAnsprechpartnerViewTableModel extends AKTableModel<CCAnsprechpartnerView> {

    public static final int COL_AP_TAKE_OVER = 0;
    public static final int COL_AP_TYP = 1;
    public static final int COL_AP_NAME = 2;
    public static final int COL_AP_VORNAME = 3;
    public static final int COL_AP_AUFTRAG_ID = 4;
    public static final int COL_AP_BILLING_NO = 5;
    public static final int COL_AP_TELEFON = 6;
    public static final int COL_AP_HANDY = 7;
    public static final int COL_AP_EMAIL = 8;

    static final String COL_NAME_AP_TAKE_OVER = "Übernehmen";
    static final String COL_NAME_AP_TYP = "Typ";
    static final String COL_NAME_AP_NAME = "Name";
    static final String COL_NAME_AP_VORNAME = "Vorname";
    static final String COL_NAME_AP_AUFTRAG_ID = "Tech. Auftragsnr.";
    static final String COL_NAME_AP_BILLING_NO = "Taifun Auftragsnr.";
    static final String COL_NAME_AP_TELEFON = "Telefon";
    static final String COL_NAME_AP_HANDY = "Mobil";
    static final String COL_NAME_AP_EMAIL = "E-Mail";

    private static final int COL_COUNT = 9;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        return getColumNames().get(column);
    }

    private Map<Integer, String> getColumNames() {
        final Map<Integer, String> valueMap = new HashMap<>();

        valueMap.put(COL_AP_TAKE_OVER, COL_NAME_AP_TAKE_OVER);
        valueMap.put(COL_AP_TYP, COL_NAME_AP_TYP);
        valueMap.put(COL_AP_NAME, COL_NAME_AP_NAME);
        valueMap.put(COL_AP_VORNAME, COL_NAME_AP_VORNAME);
        valueMap.put(COL_AP_AUFTRAG_ID, COL_NAME_AP_AUFTRAG_ID);
        valueMap.put(COL_AP_BILLING_NO, COL_NAME_AP_BILLING_NO);
        valueMap.put(COL_AP_TELEFON, COL_NAME_AP_TELEFON);
        valueMap.put(COL_AP_HANDY, COL_NAME_AP_HANDY);
        valueMap.put(COL_AP_EMAIL, COL_NAME_AP_EMAIL);

        return valueMap;
    }

    @Override
    public Object getValueAt(int row, int column) {
        CCAnsprechpartnerView model = getDataAtRow(row);
        return getValueMap(model).get(column);
    }

    private Map<Integer, Object> getValueMap(CCAnsprechpartnerView model) {
        final Map<Integer, Object> valueMap = new HashMap<>();

        valueMap.put(COL_AP_TAKE_OVER, BooleanTools.nullToFalse(model.getTakeOver()));
        valueMap.put(COL_AP_TYP, model.getAnsprechpartnerType());
        valueMap.put(COL_AP_NAME, model.getName());
        valueMap.put(COL_AP_VORNAME, model.getVorname());
        valueMap.put(COL_AP_AUFTRAG_ID, model.getAuftragId());
        valueMap.put(COL_AP_BILLING_NO, model.getOrderNo());
        valueMap.put(COL_AP_TELEFON, model.getTelefon());
        valueMap.put(COL_AP_HANDY, model.getHandy());
        valueMap.put(COL_AP_EMAIL, model.getEmail());

        return valueMap;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        final CCAnsprechpartnerView model = getDataAtRow(row);
        if (model != null) {
            if (aValue instanceof Boolean) {
                setObjectValueAt(model, column, (Boolean) aValue);
            }
            else if (aValue instanceof Long) {
                setObjectValueAt(model, column, (Long) aValue);
            }
            else if (aValue instanceof String) {
                setObjectValueAt(model, column, (String) aValue);
            }
            fireTableDataChanged();
        }
    }

    private void setObjectValueAt(CCAnsprechpartnerView mod, int col, Boolean val) {
        if (COL_AP_TAKE_OVER == col) {
            mod.setTakeOver(val);
            deactivateAnsprechpartnerWithSameType(mod);
        }
    }

    private void setObjectValueAt(CCAnsprechpartnerView mod, int col, Long val) {
        switch (col) {
            case COL_AP_AUFTRAG_ID:
                mod.setAuftragId(val);
                break;
            case COL_AP_BILLING_NO:
                mod.setOrderNo(val);
                break;
            default:
                break;
        }
    }

    private void setObjectValueAt(CCAnsprechpartnerView mod, int col, String val) {
        switch (col) {
            case COL_AP_TYP:
                mod.setAnsprechpartnerType(val);
                break;
            case COL_AP_NAME:
                mod.setName(val);
                break;
            case COL_AP_VORNAME:
                mod.setVorname(val);
                break;
            case COL_AP_TELEFON:
                mod.setTelefon(val);
                break;
            case COL_AP_HANDY:
                mod.setHandy(val);
                break;
            case COL_AP_EMAIL:
                mod.setEmail(val);
                break;
            default:
                break;
        }
    }


    /**
     * Wenn der &uuml;bergebene Ansprechpartner gew&auml;hlt wurde, sind die anderen desselben Typs nicht mehr
     * editierbar. Wird der Ansprechpartner abgew&auml;hlt, werden die anderen wieder editierbar.
     *
     * @param selectedModel der gew&auml;hlte Ansprechpartner.
     */
    private void deactivateAnsprechpartnerWithSameType(CCAnsprechpartnerView selectedModel) {
        for (CCAnsprechpartnerView ap : getData()) {
            if (ap.getAnsprechpartnerId().equals(selectedModel.getAnsprechpartnerId())) {
                continue;
            }
            if (ap.getTypeRefId().equals(selectedModel.getTypeRefId())) {
                ap.setTakeOver(Boolean.FALSE);
                ap.setEditable(!BooleanTools.nullToFalse(selectedModel.getTakeOver()));
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        final CCAnsprechpartnerView model = getDataAtRow(row);
        return (column == COL_AP_TAKE_OVER) && BooleanTools.nullToFalse(model.getEditable());
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (COL_AP_TAKE_OVER == columnIndex) {
            return Boolean.class;
        }
        else if (isLongClass(columnIndex)) {
            return Long.class;
        }
        else if (isStringClass(columnIndex)) {
            return String.class;
        }
        return null;
    }

    private boolean isStringClass(int columnIndex) {
        List<Integer> columnTypes = Arrays.asList(COL_AP_TYP, COL_AP_NAME, COL_AP_VORNAME, COL_AP_TELEFON, COL_AP_HANDY, COL_AP_EMAIL);
        return columnTypes.contains(columnIndex);
    }

    private boolean isLongClass(int columnIndex) {
        List<Integer> columnTypes = Arrays.asList(COL_AP_AUFTRAG_ID, COL_AP_BILLING_NO);
        return columnTypes.contains(columnIndex);
    }

}
