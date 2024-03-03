/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 07:57:35
 */
package de.mnet.wita.config;

/**
 * Interface zur Definition von verschiedenen Konstanten, die in der WITA Umgebung benoetigt werden.
 */
public interface WitaConstants {

    /**
     * Anzahl Tage fuer die Mindestvorlaufzeit einer WITA-Bestellung.
     */
    int MINDESTVORLAUFZEIT = 7;

    /**
     * Anzahl Tage fuer die Mindestvorlaufzeit einer Terminverschiebung nach erhaltener TAM.
     */
    int MINDESTVORLAUFZEIT_NACH_TAM = 4;

    /**
     * Anzahl der Arbeitstage pro Woche.
     */
    int WORKDAYS_OF_WEEK = 5;

    /**
     * Anzahl Tage fuer die Mindestvorlaufzeit einer HVt nach KVz Wechselbestellung.
     */
    int MINDESTVORLAUFZEIT_HVT_NACH_KVZ = 17;

    String MELDUNGSCODE_6012 = "6012";

    String DISPLAYNAME_TA = "TA";
    String DISPLAYNAME_NICHT_TA = "Nicht-TA";

}
