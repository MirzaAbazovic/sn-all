/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2011 13:44:47
 */
package de.mnet.wita;

public enum AbmMeldungsCode {
    // @formatter:off
    NO_CHANGE("0000", "keine Änderung zum Auftrag"),
    OUT_OF_CONTRACT_PERIOD(
            "0002", "Der gewünschte Schalttermin liegt ausserhalb des betrieblich möglichen Zeitrahmens oder ist kein vertraglicher Arbeitstag."),
    OUT_OF_VALID_PERIOD("0003","Der gewünschte Schalttermin liegt ausserhalb des zulässigen Zeitrahmens"),
    DATE_CHANGED("0004", "Der Ausführungstermin wurde vom abgebenden Provider geändert"),
    DATE_CHANGED_BY_DTAG("0005", "Der Ausführungstermin wurde von Telekom manuell geändert"),
    PARTIAL_EXECUTION("0006", "Der Auftrag wird teilausgeführt"),
    CUSTOMER_REQUIRED("0011", "Montage beim Endkunden erforderlich"),
    NUMBER_OF_ORDERS_EXCEEDED("5002", "Die Zahl der Aufträge zum geplanten Ausführungstermin liegt über der in der Planunungsabsprache festgelegten Menge." +
    		"Der Auftrag wurde auf den betrieblich nächstmöglichen Termin verschoben");
    // @formatter:on

    public final String meldungsCode;
    public final String meldungsText;

    private AbmMeldungsCode(String meldungsCode, String meldungsText) {
        this.meldungsCode = meldungsCode;
        this.meldungsText = meldungsText;
    }

}
