/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 10:23:37
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;

/**
 * TableModel fuer die tabellarische Darstellung von Objekten des Typs <code>KundeAdresseView</code>.
 */
public class KundeAdresseTableModel extends AKTableModel<KundeAdresseView> {

    private enum Column {
        CUSTOMER__NO("Kunde__No", Long.class),
        OLD_CUSTOMER_NO("alte K__No", Long.class),
        MAIN_CUSTOMER_NO("Hauptkunden-No", Long.class),
        NAME("Name", String.class),
        VORNAME("Vorname", String.class),
        STRASSE("Strasse", String.class),
        NUMMER("Nummer", String.class),
        PLZ("PLZ"),
        ORT("Ort"),
        KUNDENTYP("Kundentyp"),
        AREA("Niederlassung");

        private final String name;
        private final Class<?> type;

        private Column(String name) {
            this(name, String.class);
        }

        private Column(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }
    }

    public KundeAdresseTableModel() {
        super(null);
    }

    /**
     * @param column the column to search for in {@link Column}
     * @return the {@link Column} i.e. {{result.ordinal() == column}}
     * @throws IndexOutOfBoundsException if {{column}} is not a valid index
     */
    private Column getEnum4Column(int column) {
        if ((0 <= column) && (column < Column.values().length)) {
            return Column.values()[column];
        }
        throw new IndexOutOfBoundsException("Column " + column + " not found in enum" + Column.class);
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return getEnum4Column(column).name;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof KundeAdresseView) {
            KundeAdresseView view = (KundeAdresseView) o;
            switch (getEnum4Column(column)) {
                case CUSTOMER__NO:
                    return view.getKundeNo();
                case OLD_CUSTOMER_NO:
                    return view.getOldKundeNo();
                case MAIN_CUSTOMER_NO:
                    return view.getHauptKundenNo();
                case NAME:
                    return view.getName();
                case VORNAME:
                    return view.getVorname();
                case STRASSE:
                    return view.getStrasse();
                case NUMMER:
                    return StringTools.join(
                            new String[] { StringUtils.trimToEmpty(view.getNummer()),
                                    StringUtils.trimToNull(view.getHausnummerZusatz()) }, " ", true
                    );
                case PLZ:
                    return view.getPlz();
                case ORT:
                    return view.getCombinedOrtOrtsteil();
                case KUNDENTYP:
                    return view.getKundenTyp();
                case AREA:
                    return view.getAreaName();
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
        return getEnum4Column(columnIndex).type;
    }
}
