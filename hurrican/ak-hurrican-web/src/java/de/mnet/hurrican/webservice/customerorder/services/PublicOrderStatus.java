/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2015
 */
package de.mnet.hurrican.webservice.customerorder.services;

import java.io.*;
import java.util.*;

/**
 * Represents public order status
 */
public class PublicOrderStatus implements Serializable {

    private final StatusValue statusValue;
    private final Map<String, String> details = new HashMap<>();

    /**
     * Public order statuses
     */
    public enum StatusValue {
        // @formatter:off
        UNDEFINIERT(                100, "UNDEFINIERT"),
        START_BEARBEITUNG(          200, "START_BEARBEITUNG"),
        ANBIETERWECHSEL(            300, "ANBIETERWECHSEL"),
        ANBIETERWECHSEL_NEGATIV(    400, "ANBIETERWECHSEL_NEGATIV"),
        ANBIETERWECHSEL_POSITIV(    500, "ANBIETERWECHSEL_POSITIV"),
        LEITUNGSBESTELLUNG(         600, "LEITUNGSBESTELLUNG"),
        LEITUNGSBESTELLUNG_POSITIV( 700, "LEITUNGSBESTELLUNG_POSITIV"),
        LEITUNGSBESTELLUNG_NEGATIV( 800, "LEITUNGSBESTELLUNG_NEGATIV"),
        TERMIN_HINWEIS(             900, "TERMIN_HINWEIS"),
        SCHALTTERMIN_NEGATIV(      1300, "SCHALTTERMIN_NEGATIV"),
        SCHALTTERMIN_NEU(          1400, "SCHALTTERMIN_NEU"),
        IN_BETRIEB(                1500, "IN_BETRIEB");
        // @formatter:on

        private int statusPrio;
        private String statusIdentifier;

        StatusValue(int statusPrio, String statusIdentifier) {
            this.statusPrio = statusPrio;
            this.statusIdentifier = statusIdentifier;
        }

        /**
         * Status prio bei statusermittlung
         * Der Status mit dem hoeherem Wert hat hoehere prio
         */
        public int getStatusPrio() {
            return statusPrio;
        }

        /**
         * Bezeichner für den Status, der in Schnittstellen nach außen verwendet werden soll.
         */
        public String getStatusIdentifier() {
            return statusIdentifier;
        }
    }

    public PublicOrderStatus(StatusValue statusValue) {
        this.statusValue = statusValue;
    }

    public static PublicOrderStatus create(StatusValue statusValueEnum) {
        return new PublicOrderStatus(statusValueEnum);
    }

    public PublicOrderStatus addDetail(String key, String value) {
        this.details.put(key, value);
        return this;
    }

    public StatusValue getStatusValue() {
        return statusValue;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PublicOrderStatus that = (PublicOrderStatus) o;

        if (statusValue != that.statusValue)
            return false;
        return details != null ? details.equals(that.details) : that.details == null;

    }

    @Override
    public int hashCode() {
        int result = statusValue != null ? statusValue.hashCode() : 0;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PublicOrderStatus{" +
                "statusValue=" + statusValue +
                ", details=" + details +
                '}';
    }
}
