/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2011 18:58:03
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import java.util.*;
import org.netbeans.swing.outline.RowModel;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.mnet.wita.model.IoArchive;

public class IoArchiveRowModel implements RowModel {

    // Namen der Spalten
    public static final String REQUEST_GESCHAEFTSFALL_COLUMN = "Geschäftsfall";
    public static final String IO_TYPE_COLUMN = "IO-Typ";
    public static final String WITA_EXT_ORDER_NO_COLUMN = "Ref.Nr.";
    public static final String TIMESTAMP_SENT_COLUMN = "Gesendet-Zeitstempel";
    public static final String REQUEST_MELDUNGSCODE_TEXT_COLUMN = "Req.-Meldungscode/-text";
    public static final String REQUEST_TIMESTAMP_COLUMN = "Req.-Zeitstempel";

    // Workaround, um in Actions, auf eine selektierte Zeile zugreifen zu können
    public static final int REQUEST_XML_COLUMN = 1001;
    private static final int REQUEST_XML_COLUMN_INTERNAL = REQUEST_XML_COLUMN - 1;
    public static final int IO_SOURCE_COLUMN = 1003;
    private static final int IO_SOURCE_COLUMN_INTERNAL = IO_SOURCE_COLUMN - 1;

    private static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public int getColumnCount() {
        // Die beiden anderen Spalten Request-XML und Response-XML sollen nicht angezeigt werden
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int index) {
        return String.class;
    }

    @Override
    public String getColumnName(int index) {
        switch (index) {
            case 0:
                return IO_TYPE_COLUMN;
            case 1:
                return WITA_EXT_ORDER_NO_COLUMN;
            case 2:
                return TIMESTAMP_SENT_COLUMN;
            case 3:
                return REQUEST_MELDUNGSCODE_TEXT_COLUMN;
            case 4:
                return REQUEST_TIMESTAMP_COLUMN;
            case REQUEST_XML_COLUMN_INTERNAL:
                return "Request XML";
            default:
                break;
        }
        return null;
    }

    @Override
    public Object getValueFor(Object node, int column) {
        IoArchive ioArchive = (IoArchive) node;
        switch (column) {
            case 0:
                return ioArchive.getIoType().toString();
            case 1:
                return ioArchive.getWitaExtOrderNo();
            case 2:
                return ioArchive.getTimestampSent() != null ? DateTools.formatDate(Date.from(ioArchive.getTimestampSent().toInstant()), TIMESTAMP_FORMAT) : null;
            case 3:
                return formatMeldungsCodeAndTest(ioArchive.getRequestMeldungscode(), ioArchive.getRequestMeldungstext());
            case 4:
                return DateTools.formatDate(Date.from(ioArchive.getRequestTimestamp().toInstant()), TIMESTAMP_FORMAT);
            case REQUEST_XML_COLUMN_INTERNAL:
                return ioArchive.getRequestXml();
            case IO_SOURCE_COLUMN_INTERNAL:
                return ioArchive.getIoSource();
            default:
                return null;
        }
    }

    private static String formatMeldungsCodeAndTest(String meldungsCode, String meldungsText) {
        return StringTools.join(new String[] { meldungsCode, meldungsText }, " - ", true);
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public void setValueFor(Object node, int column, Object node2) {
        // do nothing for row
    }

}
