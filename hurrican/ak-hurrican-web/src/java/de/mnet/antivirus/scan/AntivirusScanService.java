/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 09:10:01
 */
package de.mnet.antivirus.scan;


/**
 * Scan-Service ueberprueft in Zusammenarbeit mit Sophos (http://www.sophos.com/) ob eine Datei einen Virus enthaelt
 * oder nicht.
 */
public interface AntivirusScanService {

    public enum AntivirusScanStatus {
        FILE_OK,
        FILE_INFECTED,
        FILE_CAUSES_ERROR
    }

    /**
     * Methode prueft ein Byte-Array auf Viren, indem dieses in eine Datei geschrieben wird und ein Check durch Sophos
     * ausgeloest wird. Als Pruefergebnis liefert die Methode den Infektionsstatus.
     *
     * @param Dateiinhalt der zu prüfenden Datei
     * @return Infektionsstatus {@link AntivirusScanStatus}
     * @throws RuntimeException bei Fehler
     */
    public AntivirusScanStatus scanFileStream(byte[] stream);

}


