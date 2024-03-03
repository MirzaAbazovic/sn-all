package de.augustakom.hurrican.model.cc;

/**
 * Different vorabstimmung processing types for a carrier.
 *
 *
 */
public enum CarrierVaModus {
    /**
     * 'Vorabstimmung' to be done by Fax
     */
    FAX,
    /**
     * 'Vorabstimmung' to be done by WBCI Portal
     */
    PORTAL,
    /**
     * 'Vorabstimmung' to be done by direct WBCI connection between the carrier an M-net
     */
    WBCI
}
