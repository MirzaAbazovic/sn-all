/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2011 07:58:27
 */
package de.mnet.wita.bpm;

/**
 * Interface zur Definition der moeglichen Task-Variablen.
 */
public enum WitaTaskVariables {
    /**
     * Name der Variable, unter dem die ID der aktuellen/zugehoerigen AKM-PV gehalten wird.
     */
    AKM_PV_ID("akmpv.id"),
    /**
     * Name der Variable, unter dem die ID des aktuellen/zugehoerigen CB-Vorgangs gehalten wird.
     */
    CB_VORGANG_ID("cb.vorgang.id"),

    /**
     * Name der Variable, unter der der Antwortcode der zu sendenden RuemPv gehalten wird.
     */
    RUEM_PV_ANTWORTCODE("ruempv.antwortcode"),

    /**
     * Name der Variable, unter der der Antworttext der zu sendenden RuemPv gehalten wird.
     */
    RUEM_PV_ANTWORTTEXT("ruempv.antworttext"),

    /**
     * Variablen-Name fuer den TalOrderWorkflow, der den Typ einer erhaltenen oder gesendeten Message enthaelt.
     */
    WITA_MESSAGE_TYPE("witaMessageType"),

    /**
     * Variablen-Name fuer den TEQ Message Typ, der angibt ob es sich um eine positive oder negative Teq handelt.
     */
    TEQ_MESSAGE_TYPE("isPositiveTeq"),

    /**
     * Variablen-Name fuer den TalOrderWorkflow, der den Aenderungstyp einer erhaltenen oder gesendeten Message
     * enthaelt.
     */
    WITA_MESSAGE_AENDERUNGSKENNZEICHEN("witaMessageAenderungskennzeichen"),

    /**
     * Variablen-Name fuer den TalOrderWorkflow, der den Geschaeftsfall einer erhaltenen oder gesendeten Message
     * enthaelt.
     */
    WITA_MESSAGE_GESCHAEFTSFALL("witaMessageGeschaeftsfall"),

    /**
     * Variablen-Name fuer den TalOrderWorkflow, der die MWF-Objekte einer zu sendenden Message (WITA-Out) enthaelt.
     */
    WITA_OUT_MWF_ID("wita.out.mwf.id"),

    /**
     * Variablen-Name fuer den TalOrderWorkflow, der die MWF-Objekte einer erhaltenen Message (WITA-In) enthaelt.
     */
    WITA_IN_MWF_ID("wita.in.mwf.id"),

    /**
     * Variablen-Name der gesetzt wird, wenn der Workflow einen Error hat.
     */
    WORKFLOW_ERROR("workflowError"),

    /**
     * Variablen-Name zur Uebergabe von Nachrichten an den Enduser im Fehlerfall.
     */
    WORKFLOW_ERROR_MESSAGE("workflowErrorMessage"),

    /**
     * Variablen-Name zur Uebergabe des Status, in den ein fehlgeschlagener Workflow zurueckgesetzt wird.
     */
    WORKFLOW_ERROR_RESET_STATE("workflowErrorResetState"),

    /**
     * Variable, um den Neustart des Prozesses ohne aggregation zu markieren.
     */
    RESTART("restart");

    public final String id;

    private WitaTaskVariables(String name) {
        this.id = name;

    }

}
