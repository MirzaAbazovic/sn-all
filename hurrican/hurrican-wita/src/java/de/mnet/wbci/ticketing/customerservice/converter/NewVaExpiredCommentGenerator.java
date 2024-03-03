/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 01.07.2014 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.wbci.model.CarrierCode;

/**
 * Hilfsklasse, um aus ueberfaellige Vorabstimmungen einen String fuer BSI Protokolleintraege zu generieren.
 */
@Component
public class NewVaExpiredCommentGenerator {

    public static final String NEW_VA_EXPIRED_DONATING = "Vorabstimmung nach StornoAenderung aufgehoben, da die neue Vorabstimmung nicht fristgerecht von '%s' erhalten wurde. (VA-ID '%s')";
    public static final String NEW_VA_EXPIRED_RECEIVING = "Vorabstimmung nach StornoAenderung aufgehoben, da die neue Vorabstimmung nicht fristgerecht an '%s' gesendet wurde. (VA-ID '%s')";

    public String getNewVaExpiredDonatingBemerkung(CarrierCode carrierCodeEkpAuf, String vorabstimmungsId) {
        return String.format(NEW_VA_EXPIRED_DONATING, carrierCodeEkpAuf, vorabstimmungsId);
    }

    public String getNewVaExpiredReceivingBemerkung(CarrierCode carrierCodeEkpAbg, String vorabstimmungsId) {
        return String.format(NEW_VA_EXPIRED_RECEIVING, carrierCodeEkpAbg, vorabstimmungsId);
    }
}