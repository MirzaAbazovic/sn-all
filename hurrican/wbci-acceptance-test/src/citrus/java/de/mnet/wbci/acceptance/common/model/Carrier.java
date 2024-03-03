/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2015
 */
package de.mnet.wbci.acceptance.common.model;

/**
 *
 */
public enum Carrier {

    MNET("DEU.MNET", "CN=mnet, OU=mnet, O=mnet, L=Munich, ST=Unknown, C=de", "482ad1b1"),
    DTAG("DEU.DTAG", "CN=dtag, OU=dtag, O=dtag, L=Bonn, ST=Unknown, C=de", "3328072f"),
    VFDE("DEU.VFDE", "CN=vfde, OU=vfde, O=vfde, L=Munich, ST=Unknown, C=de", "3328072f"),
    TEFGER("DEU.TEFGER", "CN=tefger, OU=tefger, O=tefger, L=Munich, ST=Unknown, C=de", "3328072f"),
    KDVS("DEU.KDVS", "CN=kdvs, OU=kdvs, O=kdvs, L=Munich, ST=Unknown, C=de", "3328072f");

    private final String carrierCode;
    private final String issuer;
    private final String serial;

    Carrier(String carrierCode, String issuer, String serial) {
        this.carrierCode = carrierCode;
        this.issuer = issuer;
        this.serial = serial;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSerial() {
        return serial;
    }

    public static Carrier resolve(String carrierCode) {
        for (Carrier c : Carrier.values()) {
            if (c.getCarrierCode().equals(carrierCode)) {
                return c;
            }
        }
        return null;
    }
}
