/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 16:37:03
 */
package de.augustakom.hurrican.model.exmodules.tal;

import java.util.*;


/**
 * Modell bildet ein Segment fuer die el. TAL-Bestellung ab. <br> In einem Segment koennen unterschiedliche Daten (z.B.
 * Stammdaten, Rufnummern etc. gehalten werden). Welche Daten gehalten werden, ist ueber die Segment-Kennung (z.B.
 * 'B001') definiert.
 *
 *
 */
public class TALSegment extends AbstractTALBestellungModel {

    /**
     * Segmentname fuer 'Auftragsdaten'
     */
    public static final String SEGMENT_NAME_B001 = "B001";
    /**
     * Segmentname fuer 'Netzbetreiberdaten'
     */
    public static final String SEGMENT_NAME_B002 = "B002";
    /**
     * Segmentname fuer 'Rufnummern'
     */
    public static final String SEGMENT_NAME_B003 = "B003";
    /**
     * Segmentname fuer 'Adressdaten'
     */
    public static final String SEGMENT_NAME_B004 = "B004";
    /**
     * Segmentname fuer 'Rueckmeldedaten'
     */
    public static final String SEGMENT_NAME_B005 = "B005";
    /**
     * Segmentname fuer 'Zusatzdaten Portierung'
     */
    public static final String SEGMENT_NAME_B006 = "B006";
    /**
     * Segmentname fuer 'Lage TAE Daten'
     */
    public static final String SEGMENT_NAME_B007 = "B007";
    /**
     * Segmentname fuer 'Anschlusskuendigung'
     */
    public static final String SEGMENT_NAME_B008 = "B008";
    /**
     * Segmentname fuer 'Vertragsdaten' (LBZ/VtrNr)
     */
    public static final String SEGMENT_NAME_B009 = "B009";
    /**
     * Segmentname fuer 'CuDa'
     */
    public static final String SEGMENT_NAME_B010 = "B010";
    /**
     * Segmentname fuer 'Glasfaser'
     */
    public static final String SEGMENT_NAME_B011 = "B011";
    /**
     * Segmentname fuer 'CCA'
     */
    public static final String SEGMENT_NAME_B012 = "B012";
    /**
     * Segmentname fuer 'ISIS/OPAL'
     */
    public static final String SEGMENT_NAME_B013 = "B013";
    /**
     * Segmentname fuer 'UEVT-Standort'
     */
    public static final String SEGMENT_NAME_B014 = "B014";
    /**
     * Segmentname fuer 'Ansprechpartner NBT'
     */
    public static final String SEGMENT_NAME_B015 = "B015";
    /**
     * Segmentname fuer 'Auftragsklammer'
     */
    public static final String SEGMENT_NAME_B016 = "B016";
    /**
     * Segmentname fuer 'Sonstiges'
     */
    public static final String SEGMENT_NAME_B017 = "B017";
    /**
     * Segmentname fuer 'Antwortdaten'
     */
    public static final String SEGMENT_NAME_B018 = "B018";
    /**
     * Segmentname fuer 'Alternativprodukt'
     */
    public static final String SEGMENT_NAME_B019 = "B019";
    /**
     * Segmentname fuer 'Rueckmeldedaten RNRM'
     */
    public static final String SEGMENT_NAME_B020 = "B020";
    /**
     * Segmentname fuer 'Zusatzinformationen'
     */
    public static final String SEGMENT_NAME_B021 = "B021";
    /**
     * Segmentname fuer 'HVT-Karussel'
     */
    public static final String SEGMENT_NAME_B022 = "B022";
    /**
     * Segmentname fuer 'Nutzungsaenderungsdaten'
     */
    public static final String SEGMENT_NAME_B023 = "B023";

    /**
     * Gibt die Index-Position in der Value-List fuer den Wert 'Auftragsnummer' in Segment B001 an.
     */
    public static final int INDEX_B001_AUFTRAGSNUMMER = 2;

    private String segmentName = null;
    private List<Object> values = null;

    /**
     * @return Returns the segmentName.
     */
    public String getSegmentName() {
        return segmentName;
    }

    /**
     * Uebergabe des Segment-Namens. Als Name sollte immer eine Konstante aus dieser Klasse angegeben werden (z.B.
     * SEGMENT_NAME_B002).
     *
     * @param segmentName The segmentName to set.
     */
    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    /**
     * Fuegt dem Segment einen neuen Wert hinzu.
     *
     * @param value Wert, der dem Segment hinzugefuegt werden soll
     *
     */
    public void addValue(Object value) {
        if (values == null) {
            values = new ArrayList<Object>();
        }
        values.add(value);
    }

    /**
     * Aendert das Value-Objekt an Index 'index' auf 'newValue'.
     *
     * @param index    Index des zu ersetzenden Wertes
     * @param newValue neuer zu setzender Wert
     *
     */
    public void changeValue(int index, Object newValue) {
        if (values != null) {
            values.set(index, newValue);
        }
    }

    /**
     * Gibt alle Values des Segments zurueck.
     *
     * @return
     *
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * Gibt den Wert an Position 'index' zurueck.
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     *
     */
    public Object getValue(int index) throws IndexOutOfBoundsException {
        if (values != null) {
            return values.get(index);
        }
        return null;
    }

    /**
     * Gibt den Wert an Position 'index' als Long-Objekt zurueck.
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     *
     */
    public Long getValueAsLong(int index) throws IndexOutOfBoundsException {
        Object value = getValue(index);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * Gibt den Wert an Position 'index' als String-Objekt zurueck.
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     *
     */
    public String getValueAsString(int index) throws IndexOutOfBoundsException {
        Object value = getValue(index);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

}


