/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.14
 */
package de.mnet.wbci.exception;

/**
 * Repräsentiert eine Exception die auftritt, wenn eine RufnummernPortierungSelection eines WITA-Vorgangs nicht zu den
 * vorabgestimmten Rufnummernportierung der WBCI-Vorabstimmung übereinstimmt.
 *
 *
 */
public class InvalidRufnummerPortierungException extends WbciBaseException {

    private static final long serialVersionUID = -9172425785931598301L;
    private final String wbciVorabstimmungsId;

    public InvalidRufnummerPortierungException(String wbciVorabstimmungsId) {
        super(String.format(
                "Die Rufnummernportierung der WBCI-Vorabstimmung '%s' kann nicht als Selektion für den WITA-Vorgang übernommen werden; die Rufnummern des Taifun-Auftrags sind entsprechend anzupassen!",
                wbciVorabstimmungsId));
        this.wbciVorabstimmungsId = wbciVorabstimmungsId;
    }

    public String getWbciVorabstimmungsId() {
        return wbciVorabstimmungsId;
    }
}
