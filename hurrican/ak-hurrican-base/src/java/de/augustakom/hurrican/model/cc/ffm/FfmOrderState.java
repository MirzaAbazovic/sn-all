/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;

/**
 * Enum beschreibt die Werte aus {@link NotifyUpdateOrder.StateInfo}, die in Hurrican beruecksichtigt werden muessen.
 */
public enum FfmOrderState {
    /**
     * Auftrag nicht erfolgreich beendet:  Technisch nicht realisierbar
     */
    TNFE,
    /**
     * Auftrag nicht erfolgreich beendet:  Kunde nicht angetroffen
     */
    CUST,
    /**
     * Auftrag vom Techniker in Bearbeitung genommen
     */
    ON_SITE,
    /**
     * Auftrag erfolgreich beendet
     */
    DONE,
    /**
     * Auftrag an Mobile versendet
     */
    SYNC,
    /**
     * Auftrag vom Techniker angesehen
     */
    SEEN,
    /**
     * Auftrag unterbrochen
     */
    INTERRUPTED,
    /**
     * Auftrag neu angelegt
     */
    NEW;

    /**
     * Gets enum value from String representation. Return null if no proper state is found.
     *
     * @param state
     * @return
     */
    public static FfmOrderState from(String state) {
        for (FfmOrderState orderState : values()) {
            if (orderState.name().equals(state)) {
                return orderState;
            }
        }

        return null;
    }

}
