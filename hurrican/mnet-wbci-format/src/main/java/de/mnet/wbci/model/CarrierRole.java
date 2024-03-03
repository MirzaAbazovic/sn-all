/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 10.10.13 
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.model.CarrierCode.*;

/**
 * Represents the two roles that exist within a Vorabstimmung - {@link #AUFNEHMEND} and {@link #ABGEBEND}.
 */
public enum CarrierRole {
    ABGEBEND("<== Abgebend =="),
    AUFNEHMEND("== Aufnehmend ==>");
    private final String name;

    private CarrierRole(String name) {
        this.name = name;
    }

    /**
     * Determines the M-Net role in the Vorabstimmung, using the aufnehmend and abgebend ITU Carrier Codes.
     *
     * @param iTUEkpAuf
     * @param iTUEkpAbg
     * @return the M-Net carrier role or null if it could not be determined
     */
    public static CarrierRole lookupMNetCarrierRoleByITU(String iTUEkpAuf, String iTUEkpAbg) {
        CarrierCode ekpAuf = null;
        CarrierCode ekpAbg = null;

        if (iTUEkpAuf != null) {
            ekpAuf = fromITUCarrierCode(iTUEkpAuf);
        }

        if (iTUEkpAbg != null) {
            ekpAbg = fromITUCarrierCode(iTUEkpAbg);
        }
        return lookupMNetCarrierRoleByCarrierCode(ekpAuf, ekpAbg);
    }

    /**
     * Determines the M-Net role in the Vorabstimmung, using the aufnehmend and abgebend Carrier Codes.
     *
     * @param ekpAuf
     * @param ekpAbg
     * @return the M-Net carrier role or null if it could not be determined
     */
    public static CarrierRole lookupMNetCarrierRoleByCarrierCode(CarrierCode ekpAuf, CarrierCode ekpAbg) {
        if (MNET.equals(ekpAuf)) {
            return AUFNEHMEND;
        }
        else if (MNET.equals(ekpAbg)) {
            return ABGEBEND;
        }
        return null;
    }

    /**
     * Determines the M-Net role for the wbci geschaeftsfall.
     *
     * @param geschaeftsfall
     * @return the M-Net carrier role or null if it could not be determined
     */
    public static CarrierRole lookupMNetCarrierRole(WbciGeschaeftsfall geschaeftsfall) {
        return lookupMNetCarrierRoleByCarrierCode(geschaeftsfall.getAufnehmenderEKP(), geschaeftsfall.getAbgebenderEKP());
    }

    /**
     * Determines the M-Net partner of the Vorabstimmung, using the aufnehmend and abgebend Carrier Codes.
     *
     * @param ekpAuf
     * @param ekpAbg
     * @return the partner carrier or null if it could not be determined
     */
    public static CarrierCode lookupMNetPartnerCarrierCode(CarrierCode ekpAuf, CarrierCode ekpAbg) {
        CarrierRole carrierRole = lookupMNetCarrierRoleByCarrierCode(ekpAuf, ekpAbg);
        if (CarrierRole.AUFNEHMEND.equals(carrierRole)) {
            return ekpAbg;
        }
        else if (CarrierRole.ABGEBEND.equals(carrierRole)) {
            return ekpAuf;
        }
        return ekpAbg;
    }

    public String getName() {
        return name;
    }
}
