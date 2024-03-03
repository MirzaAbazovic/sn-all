/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2015
 */
package de.mnet.wita.citrus;

/**
 *
 */
public final class VariableNames {

    /** Technical params */
    public static final String CDM_VERSION = "cdmVersion";
    public static final String USE_CASE = "useCase";

    /** Test Data Objects */
    public static final String TEST_WORKFLOW = "testWorkflow";
    public static final String TEST_DATA = "testData";
    public static final String TEST_DATA2 = "testData2";

    /** Test data identifier */
    public static final String EXTERNAL_ORDER_ID = "externalOrderId";
    public static final String MASTER_EXTERNAL_ORDER_ID = "externalOrderIdMaster";
    public static final String CB_VORGANG_ID = "cbVorgangId";
    public static final String KUENDIGUNG_EXTERNAL_ORDER_ID = "externalOrderIdKuendigung";
    public static final String CONTRACT_ID = "contractId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String THIRD_PARTY_SALESMAN_CUSTOMER_ID = "3rdPartySalesmanCustomerId";
    public static final String REQUESTED_CUSTOMER_DATE = "requestedCustomerDate";
    public static final String REQUESTED_CUSTOMER_DATE_TV = "tvRequestedCustomerDate";
    public static final String CONFIRMED_CUSTOMER_DATE = "confirmedCustomerDate";
    public static final String PRODUCT_IDENTIFIER = "productIdentifier";
    public static final String PRE_AGREEMENT_ID = "preAgreementId";
    public static final String TESTFALL_ID = "testfallId";

    /**
     * Variables for generated Values
     **/
    public static final String ENDKUNDE_NACHNAME = "endkunde.nachname";
    public static final String ENDKUNDE_VORNAME = "endkunde.vorname";
    public static final String STANDORT_STRASSE = "standort.strasse";
    public static final String STANDORT_HNR = "standort.hnr";
    public static final String STANDORT_HNR_ZUSATZ = "standort.hnr.zusatz";
    public static final String STANDORT_PLZ = "standort.plz";
    public static final String STANDORT_ORT = "standort.ort";
    public static final String STANDORT_ZUSATZ = "standort.zusatz";
    public static final String STANDORT_ORTSTEIL = "standort.ortsteil";

    public static final String RUFNUMMER_ONKZ = "rufnummer.onkz.%s";
    public static final String RUFNUMMER_DNBASE = "rufnummer.dnbase.%s";
    public static final String RUFNUMMER_DIRECT_DIAL = "rufnummer.direct.dial.%s";
    public static final String RUFNUMMER_RANGE_FROM = "rufnummer.range.from.%s";
    public static final String RUFNUMMER_RANGE_TO = "rufnummer.range.to.%s";

    public static final String LBZ_LEITUNGSSCHLUESSELZAHL = "lbz.leitungsschluesselzahl";
    public static final String LBZ_ONKZ_A = "lbz.onkz.a";
    public static final String LBZ_ONKZ_B = "lbz.onkz.b";
    public static final String LBZ_ORDNUNGSNUMMER = "lbz.ordnungsnummer";

    /**
     * Prevent instantiation.
     */
    private VariableNames() {
    }
}
