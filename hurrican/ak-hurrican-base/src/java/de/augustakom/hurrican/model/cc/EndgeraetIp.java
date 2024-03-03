/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 11:04:52
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.BooleanTools;

/**
 * Modell um IPs zu einem Endgeraet zu halten
 *
 *
 */
public class EndgeraetIp extends AbstractCCIDModel {

    private static final long serialVersionUID = -2075352030945581785L;

    public static final EndgeraetIpComparator ENDGERAETIP_COMPARATOR = new EndgeraetIpComparator();

    public static final class EndgeraetIpComparator implements Comparator<EndgeraetIp>, Serializable {
        private static final long serialVersionUID = 2701542361631866430L;

        @Override
        public int compare(EndgeraetIp o1, EndgeraetIp o2) {
            if (!o1.getIpAddressRef().getAbsoluteAddress().equals(o2.getIpAddressRef().getAbsoluteAddress())) {
                return o1.getIpAddressRef().getAbsoluteAddress().compareTo(o2.getIpAddressRef().getAbsoluteAddress());
            }
            return 0;
        }
    }

    public enum AddressType {
        LAN,
        LAN_VRRP,
        WAN
    }

    private String addressType;
    private IPAddress ipAddressRef;

    public void setAddressType(String type) {
        if (type != null) {
            AddressType.valueOf(type);
            this.addressType = type;
        }
        else {
            this.addressType = null;
        }
    }

    public void setAddressType(AddressType type) {
        if (type != null) {
            this.addressType = type.name();
        }
        else {
            this.addressType = null;
        }
    }

    /**
     * Gibt den Typ der IP-Adresse zurueck, z.B. "WAN (V6)"
     * @return
     */
    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public String getAddressTypeFormated() {
        if ((getIpAddressRef() != null) && (getIpAddressRef().getIpType() != null)) {
            return String.format("%s (%s)", getAddressType(), getIpAddressRef().getIpType().mnemonic);
        }
        return getAddressType();
    }

    /**
     * Gibt den Typ der IP-Adresse zurueck und beruecksichtigt dabei zusaetzlich die uebergeordnete
     * Endgeraete-Konfiguration. <br/>
     * Bei WAN Adressen wird hier das Flag {@link EGConfig#getWanIpFest()} ausgewertet. <br/>
     * Bsp.: "WAN (V6)" wenn Flag nicht gesetzt; "WAN (V6) fest konfiguriert" wenn gesetzt.
     * @param egConfig
     * @return
     */
    public String getAddressTypeFormatted(EGConfig egConfig) {
        if ((getIpAddressRef() != null) && (getIpAddressRef().getIpType() != null)) {
            String base = String.format("%s (%s)", getAddressType(), getIpAddressRef().getIpType().mnemonic);
            if (StringUtils.containsIgnoreCase(base, AddressType.WAN.name())
                    && egConfig != null
                    && BooleanTools.nullToFalse(egConfig.getWanIpFest())) {
                return String.format("%s fest konfiguriert", base);
            }
            return base;
        }
        return getAddressType();
    }

    public String getAddressType() {
        return addressType;
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isLanIp() {
        return (AddressType.LAN.name().equals(addressType));
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isLanVRRPIp() {
        return (AddressType.LAN_VRRP.name().equals(addressType));
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isWanIp() {
        return AddressType.WAN.name().equals(addressType);
    }

    @SuppressWarnings("JpaAttributeTypeInspection")
    public IPAddress getIpAddressRef() {
        return ipAddressRef;
    }

    public void setIpAddressRef(IPAddress ipAddressRef) {
        this.ipAddressRef = ipAddressRef;
    }

    @Override
    public String toString() {
        return String.format("EndgeraetIp [ipAddressRef=%s, addressType=%s]", ipAddressRef, addressType);
    }

}
