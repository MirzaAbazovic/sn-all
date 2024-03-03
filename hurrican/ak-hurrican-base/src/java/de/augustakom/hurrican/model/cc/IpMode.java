/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2013 12:08:21
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * Verfuegbare IP Modes (IPv4, DS_LITE, DUAL_STACK)
 */
public enum IpMode {
    IPV4, // nur IPv4
    DS_LITE, // nur IPv6
    DUAL_STACK; // IPv4 und IPv6

    public static IpMode fromLeistungen(Collection<Long> techLeistungIds) {
        if (CollectionUtils.isNotEmpty(techLeistungIds)) {
            boolean ipV4 = CollectionUtils.containsAny(techLeistungIds, TechLeistung.TECH_LEISTUNGEN_IPV4);
            boolean ipV6 = CollectionUtils.containsAny(techLeistungIds, TechLeistung.TECH_LEISTUNGEN_IPV6);
            if (ipV4 && ipV6) {
                return IpMode.DUAL_STACK;
            }
            else if (!ipV4 && ipV6) {
                return IpMode.DS_LITE;
            }
        }
        return IpMode.IPV4;
    }

    public boolean hasIpV6() {
        switch (this) {
            case DS_LITE:
            case DUAL_STACK:
                return true;
            case IPV4:
                return false;
            default:
                throw new IllegalStateException("Unkown mode: " + this);
        }
    }

    public String humanReadable() {
        switch (this) {
            case DS_LITE:
                return "IPv6 DS-Lite";
            case IPV4:
                return "IPv4";
            case DUAL_STACK:
                return "IPv6 DualStack";
            default:
                return "";
        }
    }
}


