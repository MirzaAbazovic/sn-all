/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2007 14:49:37
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;


/**
 * TableModel fuer die tabellarische Darstellung von Objekten des Typs <code>RInfoAdresseView</code>. <br>
 *
 *
 */
public class RInfoAdresseTableModel extends AKTableModel<RInfoAdresseView> {

    protected static final int COL_SAP_NO = 0;
    protected static final int COL_NO = 1;
    protected static final int COL_NAME = 2;
    protected static final int COL_VORNAME = 3;
    protected static final int COL_STRASSE = 4;
    protected static final int COL_NUMMER = 5;
    protected static final int COL_PLZ = 6;
    protected static final int COL_ORT = 7;
    protected static final int COL_KUNDENTYP = 8;
    protected static final int COL_AREA = 9;

    protected static final int COL_COUNT = 10;

    /**
     * Konstruktor fuer das TableModel
     */
    public RInfoAdresseTableModel() {
        super(null);
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_SAP_NO:
                return "SAP-Debitorennummer";
            case COL_NO:
                return "Kunde__No";
            case COL_NAME:
                return "Re-Name";
            case COL_VORNAME:
                return "Re-Vorname";
            case COL_STRASSE:
                return "Re-Strasse";
            case COL_NUMMER:
                return "Re-Nummer";
            case COL_PLZ:
                return "Re-PLZ";
            case COL_ORT:
                return "Re-Ort";
            case COL_KUNDENTYP:
                return "Kundentyp";
            case COL_AREA:
                return "Niederlassung";
            default:
                return " ";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof RInfoAdresseView) {
            RInfoAdresseView view = (RInfoAdresseView) o;
            switch (column) {
                case COL_SAP_NO:
                    return view.getExtDebitorNo();
                case COL_NO:
                    return view.getKundeNo();
                case COL_NAME:
                    return view.getName();
                case COL_VORNAME:
                    return view.getVorname();
                case COL_STRASSE:
                    return view.getStrasse();
                case COL_NUMMER:
                    return StringUtils.trimToEmpty(view.getNummer());
                case COL_PLZ:
                    return view.getPlz();
                case COL_ORT:
                    return view.getOrt();
                case COL_KUNDENTYP:
                    return view.getKundenTyp();
                case COL_AREA:
                    return view.getAreaName();
                default:
                    break;
            }
        }

        return null;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }


    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_NO) {
            return Long.class;
        }
        else {
            return super.getColumnClass(columnIndex);
        }
    }

}


