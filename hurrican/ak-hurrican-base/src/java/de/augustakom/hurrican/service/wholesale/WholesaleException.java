/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 16:55:03
 */
package de.augustakom.hurrican.service.wholesale;

/**
 * Exception fuer fachliche Fehler bei einem Wholesale Vorgang
 */
public abstract class WholesaleException extends RuntimeException {
    private static final long serialVersionUID = 5128849067635633624L;

    public static enum WholesaleFehlerCode {
        PRODUCT_GROUP_NOT_SUPPORTED("HUR-0001"),
        EXECUTION_DATE_IN_PAST("HUR-0002"),
        PRODUCT_GROUP_NOT_ALLOWED("HUR-0003"),
        ERROR_RESERVING_PORT("HUR-0004"),
        ERROR_GET_ORDER_PARAMETERS("HUR-0005"),
        LINE_ID_DOES_NOT_EXIST("HUR-0006"),
        ERROR_RELEASING_PORT("HUR-0007"),
        NOT_A_WHOLESALE_PRODUCT("HUR-0008"),
        ERROR_MODIFY_PORT("HUR-0009"),
        MODIFY_PORT_PENDING("HUR-0010"),
        ERROR_MODIFY_PORT_RESERVATION_DATE("HUR-0011"),
        ERROR_CANCEL_MODIFY_PORT("HUR-0012"),
        EKP_FRAME_CONTRACT_NOT_EXIST("HUR-0013"),
        AUFTRAG_2_EKP_FRAME_CONTRACT_NOT_FOUND("HUR-0014"),
        /**
         * Keine VLANs berechnet oder keine VLANS gefunden.
         */
        VLANS_NOT_EXIST("HUR-0015"),
        ERROR_READING_CHANGE_REASONS("HUR-0016"),
        ERROR_READING_AVAILABLE_PORTS("HUR-0017"),
        ERROR_CHANGING_PORT("HUR-0018"),
        ERROR_READING_VDSL_PROFILES("HUR-0019"),
        ERROR_CHANGING_VDSL_PROFILES("HUR-0020"),
        ERROR_BAUAUFTRAGSERSTELLUNG("HUR-0021"),

        // @formatter:off
        // Ab hier fangen mit dem Workflow abgemachte Codes an, also bitte nicht umbenennen, aber fuer entsprechende
        // Exceptions benutzen. Diese Codes werden direkt auf Meldungen in der ABBM abgebildet
        // Siehe https://intranet.m-net.de/x/Mxjs Excel-File Mapping Table Meldungscodes
        TECHNISCH_NICHT_MOEGLICH("HUR-1300"), // Auf der vorhandenen technischen Infrastruktur ist das Produkt nicht bereitstellbar (z.B. keine DSL-Versorgung im ASB).
        WEGEN_LEITUNGSDAEMPFUNG_NICHT_MOEGLICH("HUR-1301"), // Die Leitungsdaempfung ist fuer das Produkt zu hoch (auch Teilabschnitte wie Inhouseverkabelung).
        KEIN_FREIER_PORT("HUR-1309"), // Das beauftrage Produkt ist aufgrund Portmangel nicht moeglich.
        LEISTUNGSMERKMAL_NICHT_MOEGLICH("HUR-1009"), // Leistungsmerkmal technisch nicht realisierbar (bei Neuschaltung oder Merkmalaenderung) oder ist vertraglich nicht vereinbart
        PRODUKT_AN_ANSCHLUSSTYP_NICHT_MOEGLICH("HUR-1101"), // z.B. Bestellung FTTH Produkt im FTTC Anschlussgebiet
        MODIFY_PORT_RESERVATION_DATE_TO_EARLIER_DATE("HUR-1401")
        ;
        // @formatter:on

        public final String code;

        WholesaleFehlerCode(String code) {
            this.code = code;
        }

    }

    public final WholesaleFehlerCode fehler;

    public WholesaleException(WholesaleFehlerCode fehler) {
        this.fehler = fehler;
    }

    public abstract String getFehlerBeschreibung();
}
