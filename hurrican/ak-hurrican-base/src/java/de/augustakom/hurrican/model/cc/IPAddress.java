/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2011 11:07:20
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.net.AbstractIPTools;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.tools.hibernate.IPAddressInterceptor;

/**
 * Model-Klasse fuer die Abbildung einer IP Adresse (IPV4 oder IPV6). <br> <b>ACHTUNG</b> <br> Beim Speichern von
 * IP-Adressen wird die Klasse {@link IPAddressInterceptor} mit angezogen. Der Interceptor wird dazu verwendet, die
 * gueltigVon/Bis Daten sowie die BinaryRepresentation fuer die IP-Adresse zu setzen. <br> Der Interceptor wird deshalb
 * verwendet, da die IP-Adresse als Sub-Entitaet von mehreren Modell-Klassen (z.B. {@link EndgeraetIp}) verwendet wird!
 */
@Entity
@Table(name = "T_IP_ADDRESS")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IP_ADDRESS_0", allocationSize = 1)
public class IPAddress extends AbstractCCIDModel implements HistoryModel {

    private final static String EUI64_POST_FIX = "eui-64";

    private AddressTypeEnum ipType;
    private String address;
    private IPAddress prefixRef;
    private Date gueltigVon;
    private Date gueltigBis;
    private Long billingOrderNo;
    private Long netId;
    private String userW;
    private Date freigegeben;
    private Reference purpose;
    public static final String BINARY_REPRESENTATION = "binaryRepresentation";
    private String binaryRepresentation;

    @Transient
    /** Kopiert nur die Adress relevanten Daten */
    public void copyAddressDataOnly(IPAddress copyFrom) {
        if (copyFrom == null) {
            return;
        }
        setAddress(copyFrom.getAddress());
        setIpType(copyFrom.getIpType());
    }

    @Transient
    /** Kopiert die gesamte Entität */
    public void copy(IPAddress copyFrom) {
        if (copyFrom == null) {
            return;
        }
        setIpType(copyFrom.getIpType());
        setAddress(copyFrom.getAddress());
        setPrefixRef(copyFrom.getPrefixRef());
        setGueltigVon(copyFrom.getGueltigBis());
        setGueltigBis(copyFrom.getGueltigBis());
        setBillingOrderNo(copyFrom.getBillingOrderNo());
        setNetId(copyFrom.getNetId());
        setUserW(copyFrom.getUserW());
        setFreigegeben(copyFrom.getFreigegeben());
        setPurpose(copyFrom.getPurpose());
        setBinaryRepresentation(copyFrom.getBinaryRepresentation());
    }

    @Transient
    public boolean isIPV4() {
        return (ipType != null) ? !ipType.isIPv6 : false;
    }

    @Transient
    public boolean isIPV6() {
        return (ipType != null) ? ipType.isIPv6 : false;
    }

    @Transient
    private AbstractIPTools getTools() {
        return isIPV4() ? IPToolsV4.instance() : IPToolsV6.instance();
    }

    @Transient
    public String getAddressWithoutPrefix() {
        return getTools().getAddressWithoutPrefix(this.getAddress());
    }

    @Transient
    public boolean requiresPrefix() {
        return (ipType == AddressTypeEnum.IPV6_relative) || (ipType == AddressTypeEnum.IPV6_relative_eui64);
    }

    @Transient
    public boolean isPrefixAddress() {
        return ((ipType == AddressTypeEnum.IPV4_prefix) || (ipType == AddressTypeEnum.IPV6_prefix));
    }

    @Transient
    public boolean requiresNoPrefixLength() {
        return ((ipType == AddressTypeEnum.IPV4) || (ipType == AddressTypeEnum.IPV6_full));
    }

    /**
     * Berechnet die Netmask zu der aktuellen IP-Adresse.
     */
    @Transient
    public String getNetmask() {
        return getTools().getNetmask(getAddress());
    }

    /**
     * Berechnet die Netmask zu der aktuellen IP-Adresse.
     */
    @Transient
    public int getPrefixLength() {
        return getTools().getPrefixLength4Address(getAddress());
    }

    /**
     * Erstellt die Endgeraete Darstellung auf Basis des Adresstyps
     */
    @Transient
    public String getEgDisplayAddress() {
        switch (getIpType()) {
            case IPV6_full_eui64:
                return getAddress() + " " + EUI64_POST_FIX;
            case IPV6_relative_eui64:
                return buildRelativeEGAddress(EUI64_POST_FIX);
            case IPV6_relative:
                return buildRelativeEGAddress(null);
            default:
                return getAbsoluteAddress();
        }
    }

    @Transient
    private String buildRelativeEGAddress(String postFix) {
        IPAddress prefix = getPrefixRef();
        String dhcpPrefix = "?";
        if ((prefix != null) && (prefix.getPurpose() != null)) {
            dhcpPrefix = prefix.getPurpose().getStrValue();
        }
        return (StringUtils.isEmpty(postFix)) ? String.format("%s %s", dhcpPrefix, getAddress()) : String.format(
                "%s %s %s", dhcpPrefix, getAddress(), postFix);
    }

    /**
     * Ermittelt die absolute IP-Adresse. Bei IPv4 und IPv6 (ohne Präfix) Angabe entspricht dies {@link
     * IPAddress#getAddress()}. Bei IPv6 mit Präfix wird die Präfix-Referenzadresse mit der angegebenen IP-Adresse
     * kombiniert.
     *
     * @return
     */
    @Transient
    public String getAbsoluteAddress() {
        String result = null;
        if (isIPV4()) {
            result = getAddress();
        }
        else if (isIPV6()) {
            if (requiresPrefix() && (getPrefixRef() != null)) {
                result = IPToolsV6.instance().createAbsoluteAddress(getPrefixRef().getAddress(), getAddress());
            }
            else {
                result = getAddress();
            }
        }
        return result;
    }

    @Transient
    public void setV4Address(String address) {
        setAddress(address);
        setIpType(AddressTypeEnum.IPV4);
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PURPOSE", nullable = true)
    public Reference getPurpose() {
        return purpose;
    }

    public void setPurpose(Reference purpose) {
        this.purpose = purpose;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ADDRESS_TYPE", length = 20)
    @NotNull
    public AddressTypeEnum getIpType() {
        return ipType;
    }

    public void setIpType(AddressTypeEnum AddressTypeEnum) {
        this.ipType = AddressTypeEnum;
    }

    @Column(name = "ADDRESS")
    @NotNull
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @ManyToOne
    @JoinColumn(name = "PREFIX")
    public IPAddress getPrefixRef() {
        return prefixRef;
    }

    public void setPrefixRef(IPAddress prefixObj) {
        this.prefixRef = prefixObj;
    }

    @Override
    @Column(name = "GUELTIG_VON")
    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getGueltigVon() {
        return gueltigVon;
    }

    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    @Override
    @Column(name = "GUELTIG_BIS")
    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getGueltigBis() {
        return gueltigBis;
    }

    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    @Column(name = "BILLING_ORDER_NO")
    public Long getBillingOrderNo() {
        return billingOrderNo;
    }

    public void setBillingOrderNo(Long billingOrderNo) {
        this.billingOrderNo = billingOrderNo;
    }

    @Column(name = "NET_ID")
    public Long getNetId() {
        return netId;
    }

    public void setNetId(Long netId) {
        this.netId = netId;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "FREIGEGEBEN")
    public Date getFreigegeben() {
        return freigegeben;
    }

    public void setFreigegeben(Date freigegeben) {
        this.freigegeben = freigegeben;
    }

    @Column(name = "BINARY_REPRESENTATION", length = 128)
    public String getBinaryRepresentation() {
        return binaryRepresentation;
    }

    public void setBinaryRepresentation(String binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((address == null) ? 0 : address.hashCode());
        result = (prime * result) + ((ipType == null) ? 0 : ipType.hashCode());
        result = (prime * result) + ((gueltigBis == null) ? 0 : gueltigBis.hashCode());
        result = (prime * result) + ((gueltigVon == null) ? 0 : gueltigVon.hashCode());
        result = (prime * result) + ((prefixRef == null) ? 0 : prefixRef.hashCode());
        result = (prime * result) + ((netId == null) ? 0 : netId.hashCode());
        result = (prime * result) + ((userW == null) ? 0 : userW.hashCode());
        result = (prime * result) + ((freigegeben == null) ? 0 : freigegeben.hashCode());
        result = (prime * result) + ((purpose == null) ? 0 : purpose.hashCode());
        result = (prime * result) + ((binaryRepresentation == null) ? 0 : binaryRepresentation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IPAddress)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        IPAddress other = (IPAddress) obj;
        if (address == null) {
            if (other.address != null) {
                return false;
            }
        }
        else if (!address.equals(other.address)) {
            return false;
        }
        if (ipType != other.ipType) {
            return false;
        }
        if (gueltigBis == null) {
            if (other.gueltigBis != null) {
                return false;
            }
        }
        else if (!gueltigBis.equals(other.gueltigBis)) {
            return false;
        }
        if (gueltigVon == null) {
            if (other.gueltigVon != null) {
                return false;
            }
        }
        else if (!gueltigVon.equals(other.gueltigVon)) {
            return false;
        }
        if (prefixRef == null) {
            if (other.prefixRef != null) {
                return false;
            }
        }
        else if (!prefixRef.equals(other.prefixRef)) {
            return false;
        }
        if (billingOrderNo == null) {
            if (other.billingOrderNo != null) {
                return false;
            }
        }
        else if (!billingOrderNo.equals(other.billingOrderNo)) {
            return false;
        }
        if (netId == null) {
            if (other.netId != null) {
                return false;
            }
        }
        else if (!netId.equals(other.netId)) {
            return false;
        }
        if (userW == null) {
            if (other.userW != null) {
                return false;
            }
        }
        else if (!userW.equals(other.userW)) {
            return false;
        }
        if (freigegeben == null) {
            if (other.freigegeben != null) {
                return false;
            }
        }
        else if (!freigegeben.equals(other.freigegeben)) {
            return false;
        }
        if (purpose == null) {
            if (other.purpose != null) {
                return false;
            }
        }
        else if (!purpose.equals(other.purpose)) {
            return false;
        }
        if (binaryRepresentation == null) {
            if (other.binaryRepresentation != null) {
                return false;
            }
        }
        else if (!binaryRepresentation.equals(other.binaryRepresentation)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format("IPAddress [AddressTypeEnum=%s, address=%s, prefix=%s, gueltigVon=%s, gueltigBis=%s, freigegeben=%s, purpose=%s, binary=%s]",
                        ipType, address, prefixRef, gueltigVon, gueltigBis, freigegeben, purpose, binaryRepresentation);
    }

}
