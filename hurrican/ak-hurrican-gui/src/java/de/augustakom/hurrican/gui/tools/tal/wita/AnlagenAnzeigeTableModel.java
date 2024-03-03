/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2011 16:02:53
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.time.format.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.mnet.wita.model.AnlagenDto;

public class AnlagenAnzeigeTableModel extends AKTableModel<AnlagenDto> {

    private static final long serialVersionUID = 2772588760079895675L;

    static final String GESCHAEFTFALL_MELDUNGSTYP = "Geschaeftfall/Meldungstyp";
    static final String IO_TYP = "IO-Typ";
    static final String REF_NR = "Ref.Nr.";
    static final String GESENDET_ZEITSTEMPEL = "Gesendet-Zeitstempel";
    static final String ANLAGENTYP = "Anlagentyp";
    static final String DATEINAME = "Dateiname";

    private static final int COL_GESCHAEFTFALL_ODER_MELDUNGSTYP = 0;
    private static final int COL_IO_TYP = 1;
    private static final int COL_WITA_EXT_ORDER_NO = 2;
    private static final int COL_GESENDET_ZEITSTEMPEL = 3;
    private static final int COL_ANLAGEN_TYP = 4;
    private static final int COL_DATEINAME = 5;

    private static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_GESCHAEFTFALL_ODER_MELDUNGSTYP:
                return GESCHAEFTFALL_MELDUNGSTYP;
            case COL_IO_TYP:
                return IO_TYP;
            case COL_WITA_EXT_ORDER_NO:
                return REF_NR;
            case COL_GESENDET_ZEITSTEMPEL:
                return GESENDET_ZEITSTEMPEL;
            case COL_ANLAGEN_TYP:
                return ANLAGENTYP;
            case COL_DATEINAME:
                return DATEINAME;
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AnlagenDto anlage = getDataAtRow(row);
        switch (column) {
            case COL_GESCHAEFTFALL_ODER_MELDUNGSTYP:
                return anlage.getGeschaeftsfallOderMeldungsTyp();
            case COL_IO_TYP:
                return anlage.getIoType();
            case COL_WITA_EXT_ORDER_NO:
                return anlage.getWitaExtOrderNo();
            case COL_GESENDET_ZEITSTEMPEL:
                return anlage.getSentTimestamp() != null ?
                        anlage.getSentTimestamp().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)) : null;
            case COL_ANLAGEN_TYP:
                return anlage.getArchiveDocumentType();
            case COL_DATEINAME:
                return anlage.getDateiName();
            default:
                return super.getValueAt(row, column);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

}
