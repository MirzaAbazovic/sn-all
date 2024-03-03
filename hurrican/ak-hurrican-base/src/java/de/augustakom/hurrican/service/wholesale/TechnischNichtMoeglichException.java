/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2012 13:48:11
 */
package de.augustakom.hurrican.service.wholesale;

/**
 * TechnischNichtMoeglichException soll u.a. bei modifyPort geworfen werden, falls z.B. <ul> <li>die Baugruppe eine
 * hoehere Bandbreite nicht unterstuetzt und eine Portwechsel nicht erlaubt ist, oder</li> <li>ein Portwechsel zwar
 * erlaubt ist, jedoch kein freier Port mehr zur Verfuegung steht.</li> </ul>
 */
public class TechnischNichtMoeglichException extends WholesaleException {

    private WholesaleException cause = null;
    private String fehlerbeschreibung;

    public TechnischNichtMoeglichException() {
        super(WholesaleFehlerCode.TECHNISCH_NICHT_MOEGLICH);
    }

    public TechnischNichtMoeglichException(WholesaleException cause) {
        super(WholesaleFehlerCode.TECHNISCH_NICHT_MOEGLICH);
        this.cause = cause;
    }

    public TechnischNichtMoeglichException(String fehlerbeschreibung) {
        this();
        this.fehlerbeschreibung = fehlerbeschreibung;
    }

    @Override
    public String getFehlerBeschreibung() {
        if (cause != null) {
            return cause.getFehlerBeschreibung();
        }
        else if (fehlerbeschreibung != null) {
            return fehlerbeschreibung;
        }
        else {
            return "";
        }
    }
}
