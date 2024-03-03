/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 18:41:22
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 * Typ einer IP-Adresse.
 *
 *
 * @since Release 10
 */
public enum AddressTypeEnum {
    NOT_VALID(false, 0, ""),
    /**
     * IPV4 Format ohne Präfixlänge <ul> <li>192.168.2.10</li> <li>10.97.196.24</li> </ul>
     */
    IPV4(false, 1, "V4"),

    /**
     * IPV4 Format mit Präfixlänge; keine Netzadresse!
     */
    IPV4_with_prefixlength(false, 2, "V4"),

    /**
     * IPV4 Netz Adressen <ul> <li>192.168.2.0/24</li> <li>20.3.0.0/16</li> </ul>
     */
    IPV4_prefix(false, 8, "V4Net"),

    /**
     * IPV6 Format ohne Präfixlänge <ul> <li>2001:0db8:85a3:08d3:1319:8a2e:0370:7344</li> <li>2001:0db8:85a3:08d3::</li>
     * </ul>
     */
    IPV6_full(true, 3, "V6"),

    /**
     * IPV6 Format mit Präfixlänge; keine Netzadresse!
     */
    IPV6_with_prefixlength(true, 4, "V6"),

    /**
     * IPV6 Netz Adressen <ul> <li>2001:0db8:85a3:08d3::/64</li> <li>2001:0db8:a001::/48</li> </ul>
     */
    IPV6_prefix(true, 9, "V6Net"),

    /**
     * Wie bei IPV6_full Adressen, nur dass das Präfix immer 64 bit groß ist
     */
    IPV6_full_eui64(true, 5, "V6EUI"),

    /**
     * Wie bei IPV6_relativen Adressen, nur dass das Prefix immer 64 bit gro&szlig; ist.
     */
    IPV6_relative_eui64(true, 6, "V6REUI"),

    /**
     * Relative Adressen sehen je nachdem wie groß das Präfix ist folgendermaßen aus <ul> <li>Präfix =
     * 2001:0db8:a001::/48 und relative Adresse = a001:1319:8a2e:0370:7347</li> <li>Präfix = 2001:0db8:85a3:08d3::/64
     * und relative Adresse = 1319:8a2e:0370:7347</li> </ul>
     */
    IPV6_relative(true, 7, "V6R");


    public final boolean isIPv6;
    /**
     * Type Id aus der Userstory 'RFC-995.03 - Kontextabhängige Erfassung von IP-Adressen'
     */
    public final Integer typeId;
    public final String mnemonic;

    AddressTypeEnum(boolean isIPv6, Integer typeId, String mnemonic) {
        this.isIPv6 = isIPv6;
        this.typeId = typeId;
        this.mnemonic = mnemonic;
    }

    public static List<String> getTypeNames(boolean getIPV6) {
        AddressTypeEnum[] addressTypes = AddressTypeEnum.values();
        List<String> typeNames = new ArrayList<>();
        for (AddressTypeEnum addressTypeEnum : addressTypes) {
            if ((addressTypeEnum != AddressTypeEnum.NOT_VALID)
                    && (getIPV6 == addressTypeEnum.isIPv6)) {
                typeNames.add(addressTypeEnum.name());
            }
        }
        return typeNames;
    }
}
