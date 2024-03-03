package de.augustakom.hurrican.service.cc.impl.evn.model;

/**
 *  EVN (Einzelverbindungsnachweise) values
 *
 */
public enum EvnEnum {

    ACTIVATED,
    DEACTIVATED,
    CHANGE_IN_PROGRESS,
    UNKNOWN;

    public static EvnEnum fromBoolean(Boolean boolValue) {
        if (boolValue != null) {
            if (boolValue) {
                return ACTIVATED;
            } else {
                return DEACTIVATED;
            }
        } else {
            // boolValue == null
            return UNKNOWN;
        }
    }
}
