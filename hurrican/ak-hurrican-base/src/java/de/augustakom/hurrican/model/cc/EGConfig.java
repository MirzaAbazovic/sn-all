/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 10:41:48
 */
package de.augustakom.hurrican.model.cc;

import java.lang.reflect.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.dao.cc.VrrpPriority;


/**
 * Modell fuer Endgeraete-Details.
 */
@Entity
@Table(name = "T_EG_CONFIG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EG_CONFIG_0", allocationSize = 1)
public class EGConfig extends AbstractCCIDModel {

    private static final long serialVersionUID = -5777536929969726660L;

    private Long eg2AuftragId;
    private EGType egType;
    private String modellZusatz;
    private String serialNumber;
    private Boolean natActive;
    private Boolean dhcpActive;
    /**
     * Flag, ob die WAN-IP fest konfiguriert ist
     */
    private Boolean wanIpFest;
    private Boolean qosActive;
    private Short ipCount;
    private String bemerkung;
    private String bemerkungKunde;
    private String bearbeiter;
    private String egUser;
    private String egPassword;
    private String calledStationId;
    private String callingStationId;
    private Integer intervall;
    private Integer reEnable;
    private Integer idleTimer;
    private Integer attemps;
    private Integer frequency;
    private Boolean kanalbuendelung;
    private Integer wanVc;
    private Integer wanVp;
    private Boolean dnsServerActive;
    private Boolean firewallActive;
    private ServiceVertrag serviceVertrag;
    private Integer mtu;
    private String dnsServerIP;
    private Set<PortForwarding> portForwardings = new HashSet<>();
    private Set<EndgeraetAcl> endgeraetAcls = new HashSet<>();
    private IPAddress dhcpPoolFromRef;
    private IPAddress dhcpPoolToRef;
    private IPAddress triggerpunktRef;
    private Boolean snmpMNet;
    private Boolean snmpCustomer;
    private String softwarestand;
    private VrrpPriority vrrpPriority;

    private Schicht2Protokoll schicht2Protokoll;

    /**
     * Erzeugt eine Kopie des EGConfig-Objekts.
     */
    public static EGConfig createCopy(EGConfig toCopy, Long eg2AuftragIdDest) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        EGConfig newConfig = new EGConfig();
        PropertyUtils.copyProperties(newConfig, toCopy);
        newConfig.setId(null);
        newConfig.setEg2AuftragId(eg2AuftragIdDest);

        // PortForwardings kopieren
        Set<PortForwarding> forwardings = new HashSet<>();
        for (PortForwarding portForwarding : toCopy.getPortForwardings()) {
            PortForwarding copyOfPf = new PortForwarding();
            PropertyUtils.copyProperties(copyOfPf, portForwarding);
            copyOfPf.setId(null);
            if (copyOfPf.getDestIpAddressRef() != null) {
                copyOfPf.setDestIpAddressRef(copyIPAddress(copyOfPf.getDestIpAddressRef()));
            }
            if (copyOfPf.getSourceIpAddressRef() != null) {
                copyOfPf.setSourceIpAddressRef(copyIPAddress(copyOfPf.getSourceIpAddressRef()));
            }
            forwardings.add(copyOfPf);
        }
        newConfig.setPortForwardings(forwardings);

        // ACLs kopieren, diese sind many-to-many, sollen also die gleichen Objekte bleiben!
        Set<EndgeraetAcl> acls = new HashSet<>(toCopy.getEndgeraetAcls());
        newConfig.setEndgeraetAcls(acls);

        return newConfig;
    }

    private static IPAddress copyIPAddress(IPAddress toCopy) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (toCopy == null) {return null;}
        IPAddress ipAddressCopy = new IPAddress();
        PropertyUtils.copyProperties(ipAddressCopy, toCopy);
        ipAddressCopy.setId(null);
        ipAddressCopy.setGueltigVon(null);
        return ipAddressCopy;

    }

    @Column(name = "EG2A_ID")
    @NotNull
    public Long getEg2AuftragId() {
        return this.eg2AuftragId;
    }

    public void setEg2AuftragId(Long eg2AuftragId) {
        this.eg2AuftragId = eg2AuftragId;
    }

    @Column(name = "RE_ENABLE")
    public Integer getReEnable() {
        return reEnable;
    }

    public void setReEnable(Integer reEnable) {
        this.reEnable = reEnable;
    }

    @Column(name = "BEARBEITER")
    public String getBearbeiter() {
        return this.bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Column(name = "BEMERKUNG")
    public String getBemerkung() {
        return this.bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @Column(name = "BEMERKUNG_KUNDE")
    public String getBemerkungKunde() {
        return this.bemerkungKunde;
    }

    public void setBemerkungKunde(String bemerkungKunde) {
        this.bemerkungKunde = bemerkungKunde;
    }

    @Column(name = "DHCP")
    public Boolean getDhcpActive() {
        return this.dhcpActive;
    }

    public void setDhcpActive(Boolean dhcpActive) {
        this.dhcpActive = dhcpActive;
    }

    @Column(name = "ANZAHL_IP")
    public Short getIpCount() {
        return this.ipCount;
    }

    public void setIpCount(Short ipCount) {
        this.ipCount = ipCount;
    }

    @Column(name = "NAT")
    public Boolean getNatActive() {
        return this.natActive;
    }

    public void setNatActive(Boolean natActive) {
        this.natActive = natActive;
    }

    @Column(name = "SERIAL_NUMBER")
    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(name = "EG_PASSWORD")
    public String getEgPassword() {
        return this.egPassword;
    }

    public void setEgPassword(String egPassword) {
        this.egPassword = egPassword;
    }

    @Transient
    public String getAccessData() {
        if (StringUtils.isNotBlank(getEgUser()) || StringUtils.isNotBlank(getEgPassword())) {
            return StringTools.join(new String[] { getEgUser(), getEgPassword() }, " / ", true);
        }
        return null;
    }

    @Column(name = "EG_USER")
    public String getEgUser() {
        return this.egUser;
    }

    public void setEgUser(String egUser) {
        this.egUser = egUser;
    }

    @Column(name = "ATTEMPS")
    public Integer getAttemps() {
        return attemps;
    }

    public void setAttemps(Integer attemps) {
        this.attemps = attemps;
    }

    @Column(name = "CALLED_STATION_ID")
    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }

    @Column(name = "CALLING_STATION_ID")
    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    @Column(name = "FREQUENCY")
    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Column(name = "IDLE_TIMER")
    public Integer getIdleTimer() {
        return idleTimer;
    }

    public void setIdleTimer(Integer idleTimer) {
        this.idleTimer = idleTimer;
    }

    @Column(name = "INTERVALL")
    public Integer getIntervall() {
        return intervall;
    }

    public void setIntervall(Integer intervall) {
        this.intervall = intervall;
    }

    @Column(name = "KANALBUENDELUNG")
    public Boolean getKanalbuendelung() {
        return kanalbuendelung;
    }

    public void setKanalbuendelung(Boolean kanalbuendelung) {
        this.kanalbuendelung = kanalbuendelung;
    }

    @Column(name = "WANVC")
    public Integer getWanVc() {
        return wanVc;
    }

    public void setWanVc(Integer wanVc) {
        this.wanVc = wanVc;
    }


    @Column(name = "WANVP")
    public Integer getWanVp() {
        return wanVp;
    }

    public void setWanVp(Integer wanVp) {
        this.wanVp = wanVp;
    }

    @Transient
    public String getWanVpVc() {
        if (getWanVp() != null || getWanVc() != null) {
            String vp = (getWanVp() != null) ? String.format("%s", getWanVp()) : null;
            String vc = (getWanVc() != null) ? String.format("%s", getWanVc()) : null;
            return StringTools.join(new String[] { vp, vc }, " / ", true);
        }
        return null;
    }

    @Column(name = "DNS_SERVER")
    public Boolean getDnsServerActive() {
        return dnsServerActive;
    }

    public void setDnsServerActive(Boolean dnsServerActive) {
        this.dnsServerActive = dnsServerActive;
    }


    @Column(name = "FIREWALL")
    public Boolean getFirewallActive() {
        return firewallActive;
    }

    public void setFirewallActive(Boolean firewallActive) {
        this.firewallActive = firewallActive;
    }

    @Column(name = "WAN_IP_FEST")
    public Boolean getWanIpFest() {
        return wanIpFest;
    }

    public void setWanIpFest(Boolean wanIpFest) {
        this.wanIpFest = wanIpFest;
    }

    @Column(name = "QOS_ACTIVE")
    public Boolean getQosActive() {
        return qosActive;
    }

    public void setQosActive(Boolean qosActive) {
        this.qosActive = qosActive;
    }

    @Column(name = "MTU")
    public Integer getMtu() {
        return mtu;
    }

    public void setMtu(Integer mtu) {
        this.mtu = mtu;
    }

    @Column(name = "DNS_SERVER_IP")
    public String getDnsServerIP() {
        return dnsServerIP;
    }

    public void setDnsServerIP(String dnsServerIP) {
        this.dnsServerIP = dnsServerIP;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({ CascadeType.DELETE_ORPHAN, CascadeType.ALL })
    @JoinColumn(name = "EG_CONFIG_ID")
    @Fetch(value = FetchMode.SUBSELECT)
    public Set<PortForwarding> getPortForwardings() {
        return portForwardings;
    }

    public void setPortForwardings(Set<PortForwarding> portForwardings) {
        if (portForwardings != null) {
            this.portForwardings = portForwardings;
        }
        else {
            throw new IllegalArgumentException("Port Forwardings cannot be set to null");
        }
    }

    public void addPortForwarding(PortForwarding portForwardingToAdd) {
        portForwardings.add(portForwardingToAdd);
    }

    public void removePortForwarding(PortForwarding portForwardingToRemove) {
        portForwardings.remove(portForwardingToRemove);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_EG_CONFIG_2_ACL",
            joinColumns = { @JoinColumn(name = "EG_CONFIG_ID") },
            inverseJoinColumns = { @JoinColumn(name = "EG_ACL_ID") })
    @Fetch(value = FetchMode.SUBSELECT)
    public Set<EndgeraetAcl> getEndgeraetAcls() {
        return endgeraetAcls;
    }

    public void setEndgeraetAcls(Set<EndgeraetAcl> endgeraetAcls) {
        if (endgeraetAcls != null) {
            this.endgeraetAcls = endgeraetAcls;
        }
        else {
            throw new IllegalArgumentException("Endgeraet ACLs cannot be set to null");
        }
    }

    /**
     * Fuegt die EndgeraetAcl nur hinzu, falls keine EndgeraetAcl mit dem gleichen Namen vorhanden ist.
     */
    public void addEndgeraetAcl(EndgeraetAcl endgeraetAclToAdd) {
        for (EndgeraetAcl acl : endgeraetAcls) {
            if ((acl.getName() != null) && acl.getName().equals(endgeraetAclToAdd.getName())) {
                return;
            }
        }
        endgeraetAcls.add(endgeraetAclToAdd);
    }

    public void removeEndgeraetAcl(EndgeraetAcl endgeraetAclToRemove) {
        endgeraetAcls.remove(endgeraetAclToRemove);
    }

    @Column(name = "SCHICHT2_PROTOKOLL")
    @Enumerated(EnumType.STRING)
    public Schicht2Protokoll getSchicht2Protokoll() {
        return schicht2Protokoll;
    }

    public void setSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.schicht2Protokoll = schicht2Protokoll;
    }

    @Column(name = "SERVICE_VERTRAG")
    @Enumerated(EnumType.STRING)
    public ServiceVertrag getServiceVertrag() {
        return serviceVertrag;
    }

    public void setServiceVertrag(ServiceVertrag serviceVertrag) {
        this.serviceVertrag = serviceVertrag;
    }

    @Column(name = "VRRP_PRIORITY")
    @Enumerated(EnumType.STRING)
    public VrrpPriority getVrrpPriority() {
        return vrrpPriority;
    }

    public void setVrrpPriority(VrrpPriority vrrpPriority) {
        this.vrrpPriority = vrrpPriority;
    }

    @ManyToOne
    @JoinColumn(name = "EG_TYPE_ID")
    public EGType getEgType() {
        return egType;
    }

    public void setEgType(EGType egType) {
        this.egType = egType;
    }

    @Column(name = "MODELLZUSATZ")
    public String getModellZusatz() {
        return modellZusatz;
    }

    public void setModellZusatz(String modellZusatz) {
        this.modellZusatz = modellZusatz;
    }

    @OneToOne(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DHCP_POOL_FROM_ID", nullable = true)
    public IPAddress getDhcpPoolFromRef() {
        return dhcpPoolFromRef;
    }

    public void setDhcpPoolFromRef(IPAddress dhcpPoolFromRef) {
        this.dhcpPoolFromRef = dhcpPoolFromRef;
    }

    @OneToOne(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DHCP_POOL_TO_ID", nullable = true)
    public IPAddress getDhcpPoolToRef() {
        return dhcpPoolToRef;
    }

    public void setDhcpPoolToRef(IPAddress dhcpPoolToRef) {
        this.dhcpPoolToRef = dhcpPoolToRef;
    }

    // IPAddress entity wird nicht gelöscht (delete-orphan), wenn triggerpunktRef auf Null gesetzt wird, da
    // wir hibernate v3.3 verwenden und dieser Bug erst in v3.5 gefixt wurde
    // (https://hibernate.onjira.com/browse/HHH-4726).
    // Vorsicht: HHH-4726 verursacht https://hibernate.onjira.com/browse/HHH-5267. Vor Einsatz v3.5 prüfen!
    // @Cascade({ CascadeType.DELETE_ORPHAN, CascadeType.ALL}) funktioniert hier nicht.
    @OneToOne(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TRIGGERPUNKT_ID", nullable = true)
    public IPAddress getTriggerpunktRef() {
        return triggerpunktRef;
    }

    public void setTriggerpunktRef(IPAddress triggerpunktRef) {
        this.triggerpunktRef = triggerpunktRef;
    }

    @Column(name = "SNMP_MNET")
    public Boolean getSnmpMNet() {
        return snmpMNet;
    }

    public void setSnmpMNet(Boolean snmpMNet) {
        this.snmpMNet = snmpMNet;
    }

    @Column(name = "SNMP_CUSTOMER")
    public Boolean getSnmpCustomer() {
        return snmpCustomer;
    }

    public void setSnmpCustomer(Boolean snmpCustomer) {
        this.snmpCustomer = snmpCustomer;
    }

    @Column(name = "SW_STAND", nullable = true, length = 128)
    public String getSoftwarestand() {
        return softwarestand;
    }

    public void setSoftwarestand(final String softwarestand) {
        this.softwarestand = softwarestand;
    }
}
