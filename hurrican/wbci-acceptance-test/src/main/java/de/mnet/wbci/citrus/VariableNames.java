/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.13
 */
package de.mnet.wbci.citrus;

/**
 *
 */
public final class VariableNames {

    /**
     * Prevent instantiation.
     */
    private VariableNames() {
    }

    public static final String PRE_AGREEMENT_ID = "preAgreementId";
    public static final String PRE_AGREEMENT_ID_CLEANUP = "preAgreementIdCleanup";
    public static final String CHANGE_ID = "changeId";
    public static final String STORNO_ID = "stornoId";

    public static final String WBCI_GESCHAEFTSFALL_TYPE = "wbciGeschaeftsfallType";
    public static final String WBCI_ISSUER = "wbciIssuer";
    public static final String WBCI_VERSION = "wbciVersion";
    public static final String CDM_VERSION = "cdmVersion";
    public static final String SIMULATOR_USE_CASE = "wbciSimulatorUseCase";

    public static final String REQUESTED_CUSTOMER_DATE = "requestedCustomerDate";
    public static final String RESCHEDULED_CUSTOMER_DATE = "rescheduledCustomerDate";

    public static final String CARRIER_CODE_ABGEBEND = "carrierCodeAbg";
    public static final String CARRIER_CODE_AUFNEHMEND = "carrierCodeAuf";

    public static final String KFT_TEST_MODE = "kftTestMode";
    public static final String RUEMVA_AUTO_PROCESSING_FEATURE = "ruemVaAutoProcessingFeature";
    public static final String RRNP_AUTO_PROCESSING_FEATURE = "rrnpAutoProcessingFeature";
    public static final String AKMTR_AUTO_PROCESSING_FEATURE = "akmTRAutoProcessingFeature";
    public static final String TVS_VA_AUTO_PROCESSING_FEATURE = "tvsVaAutoProcessingFeature";
    public static final String PHONETIC_SEARCH_FEATURE = "phoneticSearchFeature";

    public static final String ESCALATION_REPORT_CARRIER_LIST = "escalationReportCarrierList";
    public static final String ESCALATION_MAIL_SENDER = "escalationMailSender";
    public static final String ESCALATION_MAIL_RECIPIENTS = "escalationMailRecipients";

    public static final String LATEST_EXCEPTION_LOG_ENTRY_ID = "latestExceptionLogEntryId";

    public static final String IS_SEARCH_BUILDING_EXPECTED = "IsSearchByStandortExpected";
    public static final String BILLING_ORDER_NO_ORIG = "billingOrderNoOrig";
    public static final String CUSTOMER_ID = "customerId";

    public static final String UNKNOWN_ONKZ = "unknownOnkz";

    public static final String ENDKUNDE_NACHNAME = "endkunde.nachname";
    public static final String ENDKUNDE_VORNAME = "endkunde.vorname";

    public static final String STANDORT_STRASSE = "standort.strasse";
    public static final String STANDORT_HNR = "standort.hnr";
    public static final String STANDORT_PLZ = "standort.plz";
    public static final String STANDORT_ORT = "standort.ort";
    public static final String STANDORT_GEOID = "standort.geo.id";

    public static final String PORTIERUNG_ONKZ = "portierung.onkz.%s";
    public static final String PORTIERUNG_RUFNUMMER = "portierung.rufnummer.%s";
    public static final String PORTIERUNG_DIRECT_DIAL = "portierung.direct.dial.%s";
    public static final String PORTIERUNG_RANGE_FROM = "portierung.range.from.%s";
    public static final String PORTIERUNG_RANGE_TO = "portierung.range.to.%s";

}
