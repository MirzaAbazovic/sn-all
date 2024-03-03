/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.io.*;
import java.math.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@XStreamAlias("ITEM")
@ObjectsAreNonnullByDefault
public class CpsCallScreening implements Serializable {
    @XStreamAlias("COUNTRYCODE")
    public final BigInteger countryCode;

    @XStreamAlias("LAC")
    @CheckForNull
    /** In Italien (CC=39) darf LAC fuehrende Nullen enthalten */
    public final String localAreaCode;

    @XStreamAlias("DN")
    @CheckForNull
    public final BigInteger dialNumber;

    @XStreamAlias("TYPE")
    public Type type;

    public CpsCallScreening(Type type, BigInteger countryCode, String localAreaCode, BigInteger dialNumber) {
        this.type = type;
        this.countryCode = countryCode;
        this.localAreaCode = localAreaCode;
        this.dialNumber = dialNumber;
    }

    public CpsCallScreening(Type type, BigInteger countryCode, String localAreaCode) {
        this.type = type;
        this.countryCode = countryCode;
        this.localAreaCode = localAreaCode;
        this.dialNumber = null;
    }

    public CpsCallScreening(Type type, BigInteger countryCode) {
        this.type = type;
        this.countryCode = countryCode;
        this.localAreaCode = null;
        this.dialNumber = null;
    }

    public static final Map<String, Type> CS_OLD_NEW_MAP = ImmutableMap.of(
            "CS_OUT_WHITE", Type.OUTGOING_WHITELIST,
            "CS_OUT_BLACK", Type.OUTGOING_BLACKLIST,
            "CS_IN_WHITE", Type.INCOMING_WHITELIST,
            "CS_IN_BLACK", Type.INCOMING_BLACKLIST
    );

    public enum Type {
        @XStreamAlias("OUTGOING_WHITELIST")
        OUTGOING_WHITELIST,
        @XStreamAlias("OUTGOING_BLACKLIST")
        OUTGOING_BLACKLIST,
        @XStreamAlias("INCOMING_WHITELIST")
        INCOMING_WHITELIST,
        @XStreamAlias("INCOMING_BLACKLIST")
        INCOMING_BLACKLIST
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CpsCallScreening that = (CpsCallScreening) o;

        if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null) {
            return false;
        }
        if (dialNumber != null ? !dialNumber.equals(that.dialNumber) : that.dialNumber != null) {
            return false;
        }
        if (localAreaCode != null ? !localAreaCode.equals(that.localAreaCode) : that.localAreaCode != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + (localAreaCode != null ? localAreaCode.hashCode() : 0);
        result = 31 * result + (dialNumber != null ? dialNumber.hashCode() : 0);
        result = 31 * result + type.hashCode();
        return result;
    }
}
