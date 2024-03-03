/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 15:12:16
 */
package de.augustakom.hurrican.gui.verlauf;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;


/**
 * TableModel fuer die Darstellung des Abteilungsnamen sowie der Datum-an und Datum-erledigt Felder von
 * Abteilungs-Verlaeufen.
 *
 *
 */
public class VerlaufAbtTableModel extends AKTableModel<VerlaufAbteilung> {

    // Paket sichtbar um Cellrenderer sicherer auf Spalten setzen zu koennen
    static final int COL_ABTEILUNG = 0;
    static final int COL_NIEDERLASSUNG = 1;
    static final int COL_DATUM_AN = 2;
    static final int COL_DATUM_REAL = 3;
    static final int COL_DATUM_AB = 4;
    static final int COL_DATUM_AUSGETRAGEN = 5;
    static final int COL_NOT_POSSIBLE = 6;

    private static final int COL_COUNT = 7;

    private Map<Long, Abteilung> abtMap = null;
    private Map<Long, Niederlassung> nlMap = null;


    /**
     * Default-Konstruktor.
     */
    public VerlaufAbtTableModel() {
        super();
        abtMap = new HashMap<Long, Abteilung>();
        nlMap = new HashMap<Long, Niederlassung>();
    }

    /**
     * Uebergibt dem TableModel eine Liste mit allen verfuegbaren Abteilungen.
     *
     * @param abteilungen
     */
    public void setAbteilungen(List<Abteilung> abteilungen) {
        CollectionMapConverter.convert2Map(abteilungen, abtMap, "getId", null);
    }

    public void setNiederlassungen(List<Niederlassung> niederlassungen) {
        CollectionMapConverter.convert2Map(niederlassungen, nlMap, "getId", null);
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
            case COL_ABTEILUNG:
                return "Abteilung";
            case COL_NIEDERLASSUNG:
                return "Niederlassung";
            case COL_DATUM_AN:
                return "BA an";
            case COL_DATUM_REAL:
                return "Realisierung";
            case COL_DATUM_AB:
                return "BA erledigt";
            case COL_DATUM_AUSGETRAGEN:
                return "ausgetragen";
            case COL_NOT_POSSIBLE:
                return "NICHT real.";
            default:
                return super.getColumnName(column);
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        VerlaufAbteilung va = getDataAtRow(row);
        switch (column) {
            case COL_ABTEILUNG:
                Abteilung abt = abtMap.get(va.getAbteilungId());
                return abt.getName();
            case COL_NIEDERLASSUNG:
                Niederlassung nl = nlMap.get(va.getNiederlassungId());
                return (nl != null) ? nl.getName() : null;
            case COL_DATUM_AN:
                return va.getDatumAn();
            case COL_DATUM_REAL:
                return va.getRealisierungsdatum();
            case COL_DATUM_AB:
                return va.getDatumErledigt();
            case COL_DATUM_AUSGETRAGEN:
                return va.getAusgetragenAm();
            case COL_NOT_POSSIBLE:
                return va.getNotPossible();
            default:
                return null;
        }
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
        switch (columnIndex) {
            case COL_ABTEILUNG:
                return String.class;
            case COL_NIEDERLASSUNG:
                return String.class;
            case COL_NOT_POSSIBLE:
                return Boolean.class;
            default:
                return Date.class;
        }
    }

}


