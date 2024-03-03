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
 * Hilfsklasse, um aus (ausgehenden) WBCI Daten einen String fuer BSI Protokolleintraege zu generieren.
 */
@Component
public class OutboundMessageCommentGenerator {

    //
    // Request Templates
    //

    public static final String VA = "%s zum Kundenwunschtermin %s an '%s' gesendet. (VA-ID '%s')";
    public static final String TV = "Terminverschiebung mit neuem Kundenwunschtermin %s an '%s' gesendet. (TV-ID '%s', VA-ID '%s')";
    public static final String STR_AUFH_ABG = "Stornierung Aufhebungsanfrage an '%s' gesendet. Stornogrund: '%s' (StornoID '%s', VA-ID '%s')";
    public static final String STR_AUFH_AUF = "Stornierung Aufhebungsanfrage an '%s' gesendet. (StornoID '%s', VA-ID '%s')";
    public static final String STR_AEN_ABG = "Stornierung Änderungsanfrage an '%s' gesendet. Stornogrund: '%s' (StornoID '%s', VA-ID '%s')";
    public static final String STR_AEN_AUF = "Stornierung Änderungsanfrage an '%s' gesendet. (StornoID '%s', VA-ID '%s')";

    //
    // Meldung Templates
    //

    public static final String ABBM = "Abbruchmeldung zur Vorabstimmung an '%s' gesendet. Grund: %s (VA-ID '%s')";
    public static final String RUEM_VA = "Vorabstimmungsantwort an '%s' gesendet. Bestätigter Wechseltermin: %s (VA-ID '%s')";
    public static final String AKM_TR = "Meldung zur Übernahme der techn. Ressource gesendet an '%s'. Übernahme gewünscht: %s. (VA-ID '%s')";
    public static final String ABBM_TR = "Abbruchmeldung zur Übernahme der techn. Ressource an '%s' gesendet. Grund: %s (VA-ID '%s')";
    public static final String TV_ERLM = "Erledigungsmeldung zur Terminverschiebung an '%s' gesendet. Bestätigter Wechseltermin: %s (TV-ID '%s', VA-ID '%s')";
    public static final String TV_ABBM = "Abbruchmeldung zur Terminverschiebung an '%s' gesendet. Grund: %s (TV-ID '%s', VA-ID '%s')";
    public static final String STORNO_ERLM = "Erledigungsmeldung zur Stornoanfrage an '%s' gesendet. (StornoID '%s', VA-ID '%s')";
    public static final String STORNO_ABBM = "Abbruchmeldung zur Stornoanfrage an '%s' gesendet. Grund: %s (StornoID '%s', VA-ID '%s')";

    public String getAbbmBemerkung(CarrierCode carrierCodeEkpAuf, String meldungsText, String vorabstimmungsId) {
        return String.format(ABBM, carrierCodeEkpAuf, meldungsText, vorabstimmungsId);
    }

    public String getVaBemerkung(GeschaeftsfallTyp geschaeftsfallTyp, CarrierCode carrierCodeEkpAbg, LocalDateTime kundenwunschtermin, String vorabstimmungsId) {
        return String.format(VA, geschaeftsfallTyp.getLongName(), formatDate(kundenwunschtermin), carrierCodeEkpAbg, vorabstimmungsId);
    }

    public String getTvBemerkung(CarrierCode carrierCodeEkpAuf, LocalDateTime kundenwunschtermin, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV, formatDate(kundenwunschtermin), carrierCodeEkpAuf, aenderungsId, vorabstimmungsId);
    }

    public String getStrAufhAbgBemerkung(CarrierCode carrierCodeEkpAuf, String stornoGrund, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AUFH_ABG, carrierCodeEkpAuf, stornoGrund, stornoId, vorabstimmungsId);
    }

    public String getStrAufhAufBemerkung(CarrierCode carrierCodeEkpAbg, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AUFH_AUF, carrierCodeEkpAbg, stornoId, vorabstimmungsId);
    }

    public String getStrAenAbgBemerkung(CarrierCode carrierCodeEkpAuf, String stornoGrund, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AEN_ABG, carrierCodeEkpAuf, stornoGrund, stornoId, vorabstimmungsId);
    }

    public String getStrAenAufBemerkung(CarrierCode carrierCodeEkpAbg, String stornoId, String vorabstimmungsId) {
        return String.format(STR_AEN_AUF, carrierCodeEkpAbg, stornoId, vorabstimmungsId);
    }

    public String getRuemVaBemerkung(CarrierCode carrierCodeEkpAuf, LocalDateTime bestaetigterTermin, String vorabstimmungsId) {
        return String.format(RUEM_VA, carrierCodeEkpAuf, formatDate(bestaetigterTermin), vorabstimmungsId);
    }

    public String getAkmTrBemerkung(CarrierCode carrierCodeEkpAbg, Boolean resourcenUebernahme, String vorabstimmungsId) {
        return String.format(AKM_TR, carrierCodeEkpAbg, formatBoolean(resourcenUebernahme), vorabstimmungsId);
    }

    public String getAbbmTrBemerkung(CarrierCode carrierCodeEkpAuf, String meldungsText, String vorabstimmungsId) {
        return String.format(ABBM_TR, carrierCodeEkpAuf, meldungsText, vorabstimmungsId);
    }

    public String getTvErlmBemerkung(CarrierCode carrierCodeEkpAuf, LocalDateTime bestaetigterTermin, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV_ERLM, carrierCodeEkpAuf, formatDate(bestaetigterTermin), aenderungsId, vorabstimmungsId);
    }

    public String getTvAbbmBemerkung(CarrierCode carrierCodeEkpAuf, String meldungsText, String aenderungsId, String vorabstimmungsId) {
        return String.format(TV_ABBM, carrierCodeEkpAuf, meldungsText, aenderungsId, vorabstimmungsId);
    }

    public String getStornoErlmBemerkung(CarrierCode receiverCarrierCode, String stornoId, String vorabstimmungsId) {
        return String.format(STORNO_ERLM, receiverCarrierCode, stornoId, vorabstimmungsId);
    }

    public String getStornoAbbmBemerkung(CarrierCode receiverCarrierCode, String meldungsText, String stornoId, String vorabstimmungsId) {
        return String.format(STORNO_ABBM, receiverCarrierCode, meldungsText, stornoId, vorabstimmungsId);
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