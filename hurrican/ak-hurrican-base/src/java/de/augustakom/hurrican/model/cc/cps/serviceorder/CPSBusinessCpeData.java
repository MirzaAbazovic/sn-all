/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2010 16:41:17
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeAclConverter;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeDhcpSettingConverter;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeIpConfigConverter;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpePortForwardingConverter;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeStaticRouteConverter;

/**
 * Modell-Klasse fuer die Abbildung von Business-CPE Konfigurationen.
 */
@XStreamAlias("ITEM")
public class CPSBusinessCpeData extends AbstractCPSServiceOrderDataModel {

    public static final String DESTINATION_TYPE_IPV4 = "IPV4";
    public static final String DESTINATION_TYPE_IPV6 = "IPV6";
    private static final long serialVersionUID = 8974822267766975492L;

    @XStreamAlias("MANUFACTURER")
    private String manufacturer;
    @XStreamAlias("TYPE")
    private String type;
    @XStreamAlias("SERIAL_NO")
    private String serialNo;
    @XStreamAlias("CWMP_ID")
    private String cwmpId;
    @XStreamAlias("WAN_IP_FIXED")
    private String wanIpFixed;
    @XStreamAlias("NAT")
    private String nat;
    @XStreamAlias("DHCP_SETTINGS")
    @XStreamConverter(CPSBusinessCpeDhcpSettingConverter.class)
    private CPSBusinessCpeDhcpSettingData dhcpSettings;
    @XStreamAlias("DNS")
    private String dns;
    @XStreamAlias("QOS")
    private String qos;
    @XStreamAlias("SITE")
    private String site;
    @XStreamAlias("LAYER2PROTOCOL")
    private String layer2Protocol;
    @XStreamAlias("WAN_OUTER_TAG")
    private Integer wanOuterTag;
    @XStreamAlias("WAN_INNER_TAG")
    private Integer wanInnerTag;
    @XStreamAlias("MGMT_USERNAME")
    private String mgmtUsername;
    @XStreamAlias("MGMT_PASSWORD")
    private String mgmtPassword;
    @XStreamAlias("IP_CONFIG")
    @XStreamConverter(CPSBusinessCpeIpConfigConverter.class)
    private List<CPSBusinessCpeIpConfigData> ipConfigs;
    @XStreamAlias("PORT_FORWARDING")
    @XStreamConverter(CPSBusinessCpePortForwardingConverter.class)
    private List<CPSBusinessCpePortForwardingData> portForwardings;
    @XStreamAlias("STATIC_ROUTES")
    @XStreamConverter(CPSBusinessCpeStaticRouteConverter.class)
    private List<CPSBusinessCpeStaticRouteData> staticRoutes;
    @XStreamAlias("ACLS")
    @XStreamConverter(CPSBusinessCpeAclConverter.class)
    private List<CPSBusinessCpeAclData> acls;
    @XStreamAlias("VOICE_TYPE")
    private String voiceType;

    /**
     * Uebertraegt die Daten aus den Objekten {@code egConfig} und {@code eg2Auftrag} in das aktuelle Objekt.
     *
     * @param egConfig
     * @param eg2Auftrag
     * @param leistungen the TechLeistungs to filter for one of (ID_VOIP_MGA, ID_VOIP_PMX, ID_VOIP_TK)
     */
    public void transferEgConfigData(EGConfig egConfig, EG2Auftrag eg2Auftrag, List<TechLeistung> leistungen) {
        if ((egConfig == null) || (eg2Auftrag == null)) {
            return;
        }

        setManufacturer((egConfig.getEgType() != null) ? egConfig.getEgType().getHersteller() : null);
        setType((egConfig.getEgType() != null) ? egConfig.getEgType().getModell() : null);
        setSerialNo(egConfig.getSerialNumber());
        setWanIpFixed(BooleanTools.getBooleanAsString(egConfig.getWanIpFest()));
        setNat(BooleanTools.getBooleanAsString(egConfig.getNatActive()));
        setDns(BooleanTools.getBooleanAsString(egConfig.getDnsServerActive()));
        setQos(BooleanTools.getBooleanAsString(egConfig.getQosActive()));
        setLayer2Protocol((egConfig.getSchicht2Protokoll() != null) ? egConfig.getSchicht2Protokoll().name() : null);
        setWanOuterTag(egConfig.getWanVp());
        setWanInnerTag(egConfig.getWanVc());
        setMgmtUsername(egConfig.getEgUser());
        setMgmtPassword(egConfig.getEgPassword());

        final TechLeistung techLs = findRelevantVoipLeistung(leistungen);
        if (techLs != null) {
            setVoiceType(techLs.getStrValue());
        }

        if (BooleanTools.nullToFalse(egConfig.getDhcpActive())) {
            setDhcpSettings(new CPSBusinessCpeDhcpSettingData(egConfig));
        }

        createIpConfigs(eg2Auftrag);
        createPortForwardings(egConfig);
        createStaticRoutes(eg2Auftrag);
        createAcls(egConfig);
    }

    private void createIpConfigs(EG2Auftrag eg2Auftrag) {
        if (CollectionTools.isNotEmpty(eg2Auftrag.getEndgeraetIps())) {
            for (EndgeraetIp egIp : eg2Auftrag.getEndgeraetIps()) {
                addIpConfig(new CPSBusinessCpeIpConfigData(egIp));
            }
        }
    }

    private void createPortForwardings(EGConfig egConfig) {
        if (CollectionTools.isNotEmpty(egConfig.getPortForwardings())) {
            for (PortForwarding forwarding : egConfig.getPortForwardings()) {
                if (BooleanTools.nullToFalse(forwarding.getActive())) {
                    addPortForwarding(new CPSBusinessCpePortForwardingData(forwarding));
                }
            }
        }
    }

    private void createStaticRoutes(EG2Auftrag eg2Auftrag) {
        if (CollectionTools.isNotEmpty(eg2Auftrag.getRoutings())) {
            for (Routing routing : eg2Auftrag.getRoutings()) {
                addStaticRoute(new CPSBusinessCpeStaticRouteData(routing));
            }
        }
    }

    private void createAcls(EGConfig egConfig) {
        if (CollectionTools.isNotEmpty(egConfig.getEndgeraetAcls())) {
            for (EndgeraetAcl acl : egConfig.getEndgeraetAcls()) {
                addAcl(new CPSBusinessCpeAclData(acl));
            }
        }
    }

    private TechLeistung findRelevantVoipLeistung(List<TechLeistung> leistungen) {
        TechLeistung relevantLeistung = null;
        if (CollectionTools.isNotEmpty(leistungen)) {
            filterLeistungen(leistungen);
            if (!leistungen.isEmpty()) {
                if (leistungen.size() > 1) {
                    throw new IllegalArgumentException(
                            "Es darf jeweils nur eine der technischen Leistungen gebucht sein: VOIP_MGA oder VOIP_TK oder VOIP_PMX");
                }
                relevantLeistung = leistungen.iterator().next();
            }
        }
        return relevantLeistung;
    }

    private void filterLeistungen(List<TechLeistung> leistungen) {
        CollectionUtils.filter(leistungen, new Predicate() {
            private List<Long> idsToMatch = Arrays.asList(TechLeistung.ID_VOIP_MGA, TechLeistung.ID_VOIP_PMX,
                    TechLeistung.ID_VOIP_TK);

            @Override
            public boolean evaluate(Object object) {
                return idsToMatch.contains(((TechLeistung) object).getId());
            }
        });
    }

    public void addIpConfig(CPSBusinessCpeIpConfigData toAdd) {
        if (getIpConfigs() == null) {
            setIpConfigs(new ArrayList<CPSBusinessCpeIpConfigData>());
        }
        getIpConfigs().add(toAdd);
    }

    public void addPortForwarding(CPSBusinessCpePortForwardingData toAdd) {
        if (getPortForwardings() == null) {
            setPortForwardings(new ArrayList<CPSBusinessCpePortForwardingData>());
        }
        getPortForwardings().add(toAdd);
    }

    public void addStaticRoute(CPSBusinessCpeStaticRouteData toAdd) {
        if (getStaticRoutes() == null) {
            setStaticRoutes(new ArrayList<CPSBusinessCpeStaticRouteData>());
        }
        getStaticRoutes().add(toAdd);
    }

    public void addAcl(CPSBusinessCpeAclData toAdd) {
        if (getAcls() == null) {
            setAcls(new ArrayList<CPSBusinessCpeAclData>());
        }
        getAcls().add(toAdd);
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = StringUtils.deleteWhitespace(manufacturer);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = StringUtils.deleteWhitespace(type);
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCwmpId() {
        return cwmpId;
    }

    public void setCwmpId(String cwmpId) {
        this.cwmpId = cwmpId;
    }

    public String getWanIpFixed() {
        return wanIpFixed;
    }

    public void setWanIpFixed(String wanIpFixed) {
        this.wanIpFixed = wanIpFixed;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    public CPSBusinessCpeDhcpSettingData getDhcpSettings() {
        return dhcpSettings;
    }

    public void setDhcpSettings(CPSBusinessCpeDhcpSettingData dhcpSettings) {
        this.dhcpSettings = dhcpSettings;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getQos() {
        return qos;
    }

    public void setQos(String qos) {
        this.qos = qos;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getLayer2Protocol() {
        return layer2Protocol;
    }

    public void setLayer2Protocol(String layer2Protocol) {
        this.layer2Protocol = layer2Protocol;
    }

    public Integer getWanOuterTag() {
        return wanOuterTag;
    }

    public void setWanOuterTag(Integer wanOuterTag) {
        this.wanOuterTag = wanOuterTag;
    }

    public Integer getWanInnerTag() {
        return wanInnerTag;
    }

    public void setWanInnerTag(Integer wanInnerTag) {
        this.wanInnerTag = wanInnerTag;
    }

    public String getMgmtUsername() {
        return mgmtUsername;
    }

    public void setMgmtUsername(String mgmtUsername) {
        this.mgmtUsername = mgmtUsername;
    }

    public String getMgmtPassword() {
        return mgmtPassword;
    }

    public void setMgmtPassword(String mgmtPassword) {
        this.mgmtPassword = mgmtPassword;
    }

    public List<CPSBusinessCpeIpConfigData> getIpConfigs() {
        return ipConfigs;
    }

    public void setIpConfigs(List<CPSBusinessCpeIpConfigData> ipConfigs) {
        this.ipConfigs = ipConfigs;
    }

    public List<CPSBusinessCpePortForwardingData> getPortForwardings() {
        return portForwardings;
    }

    public void setPortForwardings(List<CPSBusinessCpePortForwardingData> portForwardings) {
        this.portForwardings = portForwardings;
    }

    public List<CPSBusinessCpeStaticRouteData> getStaticRoutes() {
        return staticRoutes;
    }

    public void setStaticRoutes(List<CPSBusinessCpeStaticRouteData> staticRoutes) {
        this.staticRoutes = staticRoutes;
    }

    public List<CPSBusinessCpeAclData> getAcls() {
        return acls;
    }

    public void setAcls(List<CPSBusinessCpeAclData> acls) {
        this.acls = acls;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

}
