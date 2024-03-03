/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 05.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import java.time.*;
import java.time.format.*;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;

/**
 * Hilfsklasse, um aus (eingehenden) WBCI Daten einen String fuer BSI Protokolleintraege zu generieren.
 */
@Component
public class InboundMessageCommentGenerator {

    //
    // Request Templates
    //

    public static final String VA = "%s von '%s' zum Kundenwunschtermin %s erhalten. (VA-ID '%s')";
    public static final String TV = "Terminverschiebung von '%s' zum neuen Kundenwunschtermin '%s' erhalten. (TV-ID '%s', VA-ID '%s')";
    public static final String STR_AUFH_ABG = "Stornierung Aufhebungsanfrage von '%s' erhalten. Stornogrund: '%s' (StornoID '%s', VA-ID '%s')";
    public static final String STR_AUFH_AUF = "Stornierung Aufhebungsanfrage von '%s' erhalten. (StornoID '%s', VA-ID '%s')";
    public static final String STR_AEN_ABG = "Stornierung Änderungsanfrage von '%s' erhalten. Stornogrund: '%s' (StornoID '%s', VA-ID '%s')";
    public static final String STR_AEN_AUF = "Stornierung Änderungsanfrage von '%s' erhalten. (StornoID '%s', VA-ID '%s')";

    //
    // Meldung Templates
    //

    public static final String ABBM = "Abbruchmeldung zur Vorabstimmung von '%s' erhalten. Grund: %s (VA-ID '%s')";
    public static final String RUEM_VA = "Vorabstimmungsantwort von '%s' erhalten. Bestätigter Wechseltermin: %s (VA-ID '%s')";
    public static final String AKM_TR = "Meldung zur Übernahme der techn. Ressource von '%s' erhalten. Übernahme gewünscht: %s. (VA-ID '%s')";
    public static final String ABBM_TR = "Abbruchmeldung zur Übernahme der techn. Ressource von '%s' erhalten. Grund: %s (VA-ID '%s')";
    public static final String TV_ERLM = "Erledigungsmeldung zur Terminverschiebung von '%s' erhalten. Bestätigter Wechseltermin: %s (TV-ID '%s', VA-ID '%s')";
    public static final String TV_ABBM = "Abbruchmeldung zur Terminverschiebung von '%s' erhalten. Grund: %s (TV-ID '%s', VA-ID '%s')";
    public static final String STORNO_ERLM = "Erledigungsmeldung zur Stornoanfrage von '%s' erhalten. (StornoID '%s', VA-ID '%s')";
    public static final String STORNO_ABBM = "Abbruchmeldung zur Stornoanfrage von '%s' erhalten. Grund: %s (StornoID '%s', VA-ID '%s')";

    public String getAbbmBemerkung(CarrierCode carrierCodeEkpAbg, String meldungsText, String vorabstimmungsId) {
        return String.format(ABBM, carrierCodeEkpAbg, meldungsText, vorabstimmungsId);
    }

    public String getVaBemerkung(GeschaeftsfallTyp geschaeftsfallTyp, CarrierCode carrierCodeEkpAuf, LocalDateTime kundenwunschtermin, String vorabstimmungsId) {
        return String.format(VA, geschaeftsfallTyp.getLongName(), carrierCodeEkpAuf, formatDate(kundenwunschtermin), vorabstimmungsId);
    }

    public String getTvBemerkung(CarrierCode carrierCodeEkpAbg, LocalDateTime kundenwunschtermin, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV, carrierCodeEkpAbg, formatDate(kundenwunschtermin), aenderungsId, vorabstimmungsId);
    }

    public String getStrAufhAbgBemerkung(CarrierCode carrierCodeEkpAbg, String stornoGrund, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AUFH_ABG, carrierCodeEkpAbg, stornoGrund, stornoId, vorabstimmungsId);
    }

    public String getStrAufhAufBemerkung(CarrierCode carrierCodeEkpAuf, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AUFH_AUF, carrierCodeEkpAuf, stornoId, vorabstimmungsId);
    }

    public String getStrAenAbgBemerkung(CarrierCode carrierCodeEkpAbg, String stornoGrund, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AEN_ABG, carrierCodeEkpAbg, stornoGrund, stornoId, vorabstimmungsId);
    }

    public String getStrAenAufBemerkung(CarrierCode carrierCodeEkpAuf, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AEN_AUF, carrierCodeEkpAuf, stornoId, vorabstimmungsId);
    }

    public String getRuemVaBemerkung(CarrierCode carrierCodeEkpAbg, LocalDateTime bestaetigterTermin, String vorabstimmungsId) {
        return String.format(RUEM_VA, carrierCodeEkpAbg, formatDate(bestaetigterTermin), vorabstimmungsId);
    }

    public String getAkmTrBemerkung(CarrierCode carrierCodeEkpAuf, Boolean resourcenUebernahme, String vorabstimmungsId) {
        return String.format(AKM_TR, carrierCodeEkpAuf, formatBoolean(resourcenUebernahme), vorabstimmungsId);
    }

    public String getAbbmTrBemerkung(CarrierCode carrierCodeEkpAbg, String meldungsText, String vorabstimmungsId) {
        return String.format(ABBM_TR, carrierCodeEkpAbg, meldungsText, vorabstimmungsId);
    }

    public String getTvErlmBemerkung(CarrierCode carrierCodeEkpAbg, LocalDateTime bestaetigterTermin, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV_ERLM, carrierCodeEkpAbg, formatDate(bestaetigterTermin), aenderungsId, vorabstimmungsId);
    }

    public String getTvAbbmBemerkung(CarrierCode carrierCodeEkpAbg, String meldungsText, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV_ABBM, carrierCodeEkpAbg, meldungsText, aenderungsId, vorabstimmungsId);
    }

    public String getStornoErlmBemerkung(CarrierCode senderCarrierCode, String stornoId, String vorabstimmungsId) {
        return String.format(STORNO_ERLM, senderCarrierCode, stornoId, vorabstimmungsId);
    }

    public String getStornoAbbmBemerkung(CarrierCode senderCarrierCode, String meldungsText, String stornoId, String vorabstimmungsId) {
        return String.format(STORNO_ABBM, senderCarrierCode, meldungsText, stornoId, vorabstimmungsId);
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return fmt.format(dateTime);
    }

    private String formatBoolean(Boolean resourcenUebernahme) {
        if (Boolean.TRUE.equals(resourcenUebernahme)) {
            return "Ja";
        }
        return "Nein";
    }


}