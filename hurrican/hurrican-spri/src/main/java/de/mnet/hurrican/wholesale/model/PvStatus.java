/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 17.06.2016

 */

package de.mnet.hurrican.wholesale.model;


import java.util.stream.*;

/**
 * Enummeration für PV Status
 * <p>
 * <p>
 * Created by vergaragi on 14.02.2017.
 */


public enum PvStatus {
    GESENDET("Gesendet"), EMPFANGEN("Empfangen"), FEHLER("Fehler");

    private final String code;

    PvStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PvStatus valueFor(String code) {
        return Stream.of(PvStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElse(FEHLER);
    }
}
  