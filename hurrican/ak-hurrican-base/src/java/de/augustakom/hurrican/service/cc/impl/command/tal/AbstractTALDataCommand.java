/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2007 14:16:14
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;


/**
 * Abstrakte Command-Klasse fuer alle TAL-Commands, die Daten fuer die el. TAL-Bestellung sammeln.
 *
 *
 */
public abstract class AbstractTALDataCommand extends AbstractTALCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractTALDataCommand.class);

    /** Schnittstelle DTAG**/
    /**
     * Key fuer die Übergabe des minimalen Vorkommens eines Segments*
     */
    public static final String KEY_SEGMENT_MIN = "seg.min";
    /**
     * Key fuer die Übergabe des maximalen Vorkommens eines Segments*
     */
    public static final String KEY_SEGMENT_MAX = "seg.max";
    /**
     * KEY fuer dei Uebergabe der Vorgangs-ID
     */
    public static final String KEY_CBVORGANG_ID = "vorg.id";

    /**
     * HashMap mit den fuer die ITEX-Schnittstelle ungueltigen Zeichen zzgl. Angabe der dafuer zu verwendenden Zeichen.
     */
    protected static final Map<String, String> invalidITEXChars = new HashMap<String, String>();

    // Initialisierung der Map mit den ungueltigen Zeichen
    static {
        String replace = "|#|";
        invalidITEXChars.put("\n", replace);
        invalidITEXChars.put("\r", replace);
        invalidITEXChars.put("\t", replace);
        invalidITEXChars.put(";", replace);
    }

    /**
     * Ermittelt den aktuellen CB-Vorgang.
     *
     * @return Objekt vom Typ <code>CBVorgang</code>
     * @throws HurricanServiceCommandException wenn der CB-Vorgang nicht ermittelt werden konnte.
     */
    protected CBVorgang getCBVorgang() throws HurricanServiceCommandException {
        try {
            Long cbId = getPreparedValue(KEY_CBVORGANG_ID,
                    Long.class, false, "Die ID des betroffenen CB-Vorgang wurde nicht gesetzt!");

            CarrierElTALService caService = getCCService(CarrierElTALService.class);
            CBVorgang cbVorgang = caService.findCBVorgang(cbId);

            if (cbVorgang == null) {
                throw new HurricanServiceCommandException("CB_Vorgang konnte nicht ermittelt werden!");
            }
            return cbVorgang;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Ermittlung des CB-Vorgang: " + e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob die notwendige Segment-Anzahl eingehalten wird.
     *
     * @param anzahlVorhanden Anzahl der vorhandenen/ermittelten Segmente
     * @param segmentName     Name des zu pruefenden Segments
     * @return true wenn die Anzahl Segment mit der Konfiguration uebereinstimmt.
     * @throws HurricanServiceCommandException wenn die Konfiguration (Min- und Max-Wert) nicht ermittelt werden
     *                                         konnte.
     */
    protected boolean checkSegmentAnzahl(Integer anzahlVorhanden, String segmentName) throws HurricanServiceCommandException {
        try {
            Integer min = getPreparedValue(KEY_SEGMENT_MIN,
                    Integer.class, false, "Die Segmentanzahl kann nicht überprüft werden." +
                            "MIN wurde nicht gesetzt! Segment: " + segmentName
            );
            Integer max = getPreparedValue(KEY_SEGMENT_MAX,
                    Integer.class, false, "Die Segmentanzahl kann nicht überprüft werden." +
                            "MAX wurde nicht gesetzt! Segment: " + segmentName
            );

            if ((min == 0) && (anzahlVorhanden >= 0)) {
                if (anzahlVorhanden <= max) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if ((min != 0) && (anzahlVorhanden >= min)) {
                if (anzahlVorhanden <= max) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Segmentermittlung der Carrierbestellung: " + e.getMessage(), e);
        }
    }

    /**
     * Verifiziert Mussfelder auf Dateninhalt. Ist das Object 'var' <code>null</code> (oder bei String ein Leerstring),
     * wird eine Exception generiert.
     *
     * @param var     das zu pruefende Objekt
     * @param talSeg  das TAL-Segment, dem das Objekt hinzugefuegt werden soll
     * @param errText Fehlertext, falls das Objekt 'var' <code>null</code> ist.
     * @throws HurricanServiceCommandException
     */
    protected void verifyMandatory(Object var, TALSegment talSeg, String errText) throws HurricanServiceCommandException {
        if (talSeg == null) {
            throw new HurricanServiceCommandException("TAL-Segment ist nicht angegeben!");
        }

        if ((var == null) || ((var instanceof String) && StringUtils.isBlank((String) var))) {
            throw new HurricanServiceCommandException("Mussfeld darf nicht leer sein. " + errText);
        }
        talSeg.addValue(var);
    }

    /*
     * Ueberprueft, ob die Vertragsnummer ein 'A' enthaelt. Ist dies der Fall,
     * wird die Nummer nach dem A auf 6-stellig mit fuehrenden 0 aufgefuellt.
     * Bsp.: 170A123 --> 170A000123
     *       5999    --> 5999
     * @param vtrNr
     * @return
     */
    protected String getVerifiedVtrNr(String vtrNr) throws HurricanServiceCommandException {
        if (StringUtils.contains(vtrNr, "A")) {
            if (vtrNr.length() >= 10) {
                return vtrNr;
            }
            else {
                try {
                    String[] splitted = StringUtils.split(vtrNr, "A");
                    StringBuilder modified = new StringBuilder();
                    modified.append(splitted[0]);
                    modified.append("A");

                    int fillCount = 6 - splitted[1].length();
                    for (int i = 0; i < fillCount; i++) {
                        modified.append("0");
                    }
                    modified.append(splitted[1]);

                    return modified.toString();
                }
                catch (Exception e) {
                    throw new HurricanServiceCommandException("Vertragsnummer besitzt nicht das erwartete Format", e);
                }
            }
        }
        else {
            return vtrNr;
        }
    }
}


