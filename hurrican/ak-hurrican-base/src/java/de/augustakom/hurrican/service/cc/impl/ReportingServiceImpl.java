package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.OptionalTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.net.AbstractIPTools;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.DatabaseRuntimeException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ConnectService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HousingService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.ReportingService;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.logindata.LoginDataService;
import de.augustakom.hurrican.service.cc.impl.logindata.LoginDataVoipGatherer;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;

@CcTxRequiredReadOnly
public class ReportingServiceImpl implements ReportingService {
    private static final Logger LOGGER = Logger.getLogger(ReportingServiceImpl.class);
    public static final String REPORTING_DATE_FORMAT = "dd.MM.yyyy";
    static final String CPE_PREFIX = "cpe.";
    private static final String HOTLINE_PREFIX = "hotline.";
    private static final String HOUSING_PREFIX = "housing.";
    private static final String GA_HOTLINE_PREFIX = "gahotline.";
    private static final String AP_PREFIX = "ap.";
    private static final String AP_ES_A_PREFIX = "ap.a.";
    private static final String AP_ES_B_PREFIX = "ap.b.";
    private static final String AP_PBX_ENTERPRISE_PREFIX = "ap.pbx.enterprise.";
    private static final String AP_PBX_SITE_PREFIX = "ap.pbx.site.";
    static final String ENDSTELLE_A_PREFIX = "esa.";
    static final String ENDSTELLE_B_PREFIX = "esb.";
    private static final String CARRIERBESTELLUNG_PREFIX = "cb.";
    private static final String IP_ADDRESS_KEY = "ip_address";
    private static final String HAS_4_DRAHT_OPTION_KEY = "4drahtoption";
    private static final String VERBINDUNGSBEZEICHNUNG_KEY = "verbez";
    private static final String ES_B_PREFIX = "b.";
    private static final String ES_A_PREFIX = "a.";
    private static final String IPSECS2S_PREFIX = "ipsecs2s.";
    private static final String IPSECS2S_ROUTER_PASSIV_PREFIX = "router.passive.";
    private static final String IPSECS2S_ROUTER_AKTIV_PREFIX = "router.active.";
    private static final String ACCOUNTS_KEY = "accounts";
    private static final String VOIP_DN_2_EG_PORT_KEY = "voipdn2egport";

    private static final String ENDSTELLE_CONNECT_PREFIX = "connect";
    private static final String ENDSTELLE_CONNECT_A_SUFFIX = "a";
    private static final String ENDSTELLE_CONNECT_B_SUFFIX = "b";

    private static final String MVS_PREFIX = "mvs.";
    private static final String IPV6_PREFIX = "ipv6.";
    private static final String MVS_SITE_PREFIX = "site.";
    private static final String MVS_ENTERPRISE_PREFIX = "enterprise.";
    private static final String VOIPDNBLOCKLENGTH = "voipdnblocklength";
    private static final String VOIP_DN_SIP_DATA_KEY = "voipdnsipdata";
    private static final String VOIP_DN_PLAN_KEY = "voipdnplan";

    private static final String SIP_LOGIN_PREFIX = "sip.login";
    private static final String SIP_PEERING_PARTNER_PREFIX = "sip.peering.partner";
    private static final String LOGINDATA_INTERNET = "logindata.internet";
    private static final String LOGINDATA_VOIP = "logindata.voip";


    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HousingService")
    private HousingService housingService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    private AnsprechpartnerService ansprechpartnerService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ConnectService")
    private ConnectService connectService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService ccKundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.IPSecService")
    private IPSecService ipSecService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AccountService")
    private AccountService accountService;
    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;
    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voIPService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungService;
    @Resource(name = "de.augustakom.hurrican.service.cc.MVSService")
    private MVSService mvsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.SipPeeringPartnerService")
    private SipPeeringPartnerService sipPeeringPartnerService;
    @Resource
    private LoginDataService loginDataService;

    private static final String LINE_FORMAT_3_TABS = "%s:\t\t\t%s%n";
    private static final String LINE_FORMAT_4_TABS = "%s:\t\t\t\t%s%n";

    /**
     * @return Returns the ipAddressService.
     */
    private IPAddressService getIpAddressService() {
        return ipAddressService;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.ReportingService#getValue(java.lang.Long, java.lang.String)
     */
    @Override
    public String getValue(Long orderNo, String reportingKeyName) {
        String reportingKeyName1 = reportingKeyName;
        if (reportingKeyName1 == null) {
            LOGGER.error("got null reportingKeyName for orderNo " + orderNo);
            return null;
        }
        if (StringUtils.isBlank(reportingKeyName1)) {
            LOGGER.error("got blank reportingKeyName for orderNo " + orderNo);
            return null;
        }
        if (orderNo == null) {
            LOGGER.error("got null as orderNo");
            return null;
        }

        reportingKeyName1 = reportingKeyName1.toLowerCase();
        String result = null;
        try {
            result = getValueOrException(orderNo, reportingKeyName1);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception when trying to get value for orderNo " + orderNo + " and reportingKeyName "
                    + reportingKeyName1, e);
        }
        if (result == null) {
            LOGGER.error("No value found for orderNo " + orderNo + " and reportingKeyName " + reportingKeyName1);
        }
        return result;
    }

    String getValueOrException(Long orderNo, String reportingKeyName) throws FindException {
        List<AuftragDaten> auftragDaten = findAuftragDatenForReporting(orderNo);
        if (CollectionTools.isEmpty(auftragDaten)) {
            LOGGER.error("No Hurrican order for the given Taifun orderNo found!");
            return null;
        }
        if (ACCOUNTS_KEY.equalsIgnoreCase(reportingKeyName)) {
            return generateAciiIntAccounts(findIntAccountsForReporting(auftragDaten));
        }

        if (IP_ADDRESS_KEY.equalsIgnoreCase(reportingKeyName)) {
            return generateAsciiIpAddresses(findAuftragDatenForReporting(orderNo));
        }

        if (HAS_4_DRAHT_OPTION_KEY.equalsIgnoreCase(reportingKeyName)) {
            return auftragService
                    .has4DrahtOptionOrderNo(orderNo) ? "Ja" : "Nein";
        }

        if (VERBINDUNGSBEZEICHNUNG_KEY.equalsIgnoreCase(reportingKeyName)) {
            return generateAsciiFromVerBez(findVbzsForReporting(orderNo));
        }

        if (reportingKeyName.startsWith(ENDSTELLE_A_PREFIX)) {
            return transformNullToEmptyString(getEndstellenKey(
                    reportingKeyName.substring(ENDSTELLE_A_PREFIX.length()),
                    findEndstelle(orderNo, Endstelle.ENDSTELLEN_TYP_A)));
        }

        if (reportingKeyName.startsWith(ENDSTELLE_B_PREFIX)) {
            return transformNullToEmptyString(getEndstellenKey(
                    reportingKeyName.substring(ENDSTELLE_B_PREFIX.length()),
                    findEndstelle(orderNo, Endstelle.ENDSTELLEN_TYP_B)));
        }

        if (reportingKeyName.startsWith(HOTLINE_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(
                    orderNo, reportingKeyName.substring(HOTLINE_PREFIX.length()), Ansprechpartner.Typ.HOTLINE_SERVICE));
        }
        if (reportingKeyName.startsWith(GA_HOTLINE_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(
                    orderNo, reportingKeyName.substring(GA_HOTLINE_PREFIX.length()), Ansprechpartner.Typ.HOTLINE_GA));
        }
        if (reportingKeyName.startsWith(AP_ES_A_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(
                    orderNo, reportingKeyName.substring(AP_ES_A_PREFIX.length()), Ansprechpartner.Typ.ENDSTELLE_A));
        }
        if (reportingKeyName.startsWith(AP_ES_B_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(
                    orderNo, reportingKeyName.substring(AP_ES_B_PREFIX.length()), Ansprechpartner.Typ.ENDSTELLE_B));
        }
        if (reportingKeyName.startsWith(AP_PBX_ENTERPRISE_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(orderNo,
                    reportingKeyName.substring(AP_PBX_ENTERPRISE_PREFIX.length()),
                    Ansprechpartner.Typ.PBX_ENTERPRISE_ADMINISTRATOR));
        }
        if (reportingKeyName.startsWith(AP_PBX_SITE_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(orderNo,
                    reportingKeyName.substring(AP_PBX_SITE_PREFIX.length()), Ansprechpartner.Typ.PBX_SITE_ADMINISTRATOR));
        }
        if (reportingKeyName.startsWith(AP_PREFIX)) {
            return transformNullToEmptyString(getAnsprechpartnerKey(orderNo,
                    reportingKeyName.substring(AP_PREFIX.length()), Ansprechpartner.Typ.TECH_SERVICE));
        }
        if (reportingKeyName.startsWith(CPE_PREFIX)) {
            return getCPEKey(orderNo,
                    reportingKeyName.substring(CPE_PREFIX.length()));
        }
        if (reportingKeyName.startsWith(IPSECS2S_PREFIX)) {
            return getIPSecS2SKey(orderNo,
                    reportingKeyName.substring(IPSECS2S_PREFIX.length()), auftragDaten);
        }
        if (reportingKeyName.startsWith(HOUSING_PREFIX)) {
            return findHousingForReporting(orderNo,
                    reportingKeyName.substring(HOUSING_PREFIX.length()));
        }
        if (reportingKeyName.startsWith(VOIP_DN_2_EG_PORT_KEY)) {
            return transformNullToEmptyString(findVoIPDN2EGPort(auftragDaten));
        }
        if (reportingKeyName.startsWith(VOIPDNBLOCKLENGTH)) {
            return getVoipDnBlockLengths(auftragDaten);
        }
        if (reportingKeyName.startsWith(VOIP_DN_SIP_DATA_KEY)) {
            return getLoginDataVoip(orderNo, false, false);
        }
        if (reportingKeyName.startsWith(VOIP_DN_PLAN_KEY)) {
            return getVoipDnPlan(auftragDaten);
        }

        if (reportingKeyName.startsWith(MVS_PREFIX)) {
            return transformNullToEmptyString(getMVSKey(auftragDaten, reportingKeyName.substring(MVS_PREFIX.length())));
        }

        if (reportingKeyName.startsWith(IPV6_PREFIX)) {
            return transformNullToEmptyString(getIPv6Key(orderNo, auftragDaten,
                    reportingKeyName.substring(IPV6_PREFIX.length())));
        }

        if (reportingKeyName.startsWith(SIP_LOGIN_PREFIX)) {
            return getSipLogin(auftragDaten);
        }

        if (reportingKeyName.startsWith(SIP_PEERING_PARTNER_PREFIX)) {
            return getSipPeeringPartner(auftragDaten);
        }
        if (reportingKeyName.startsWith(LOGINDATA_INTERNET)) {
            return getLoginDataInternet(orderNo);
        }
        if (reportingKeyName.startsWith(LOGINDATA_VOIP)) {
            return getLoginDataVoip(orderNo, true, true);
        }

        Matcher matcher = Pattern.compile(
                "^" + ENDSTELLE_CONNECT_PREFIX + "\\.(.*)\\.([" + ENDSTELLE_CONNECT_A_SUFFIX
                        + ENDSTELLE_CONNECT_B_SUFFIX + "])$"
        ).matcher(reportingKeyName);
        if (matcher.matches()) {

            String endstelleTyp = null;
            if (matcher.group(2).equals(ENDSTELLE_CONNECT_A_SUFFIX)) {
                endstelleTyp = Endstelle.ENDSTELLEN_TYP_A;
            }
            else if (matcher.group(2).equals(ENDSTELLE_CONNECT_B_SUFFIX)) {
                endstelleTyp = Endstelle.ENDSTELLEN_TYP_B;
            }
            EndstelleConnect endstelleConnect = findEndstelleConnect(orderNo, endstelleTyp);
            return transformNullToEmptyString(getEndstelleConnectKey(matcher.group(1), endstelleConnect));
        }
        return null;
    }

    private String getSipPeeringPartner(final List<AuftragDaten> auftragDatens) {
        final Date now = new Date();
        final Function<Pair<SipPeeringPartner, Auftrag2PeeringPartner>, String> ppToString =
                pp -> pp.getFirst().getName()
                        + "  gültig ab: "
                        + DateTools.formatDate(pp.getSecond().getGueltigVon(), REPORTING_DATE_FORMAT);

        final List<Auftrag2PeeringPartner> auftrag2PeeringPartners = auftragDatens.stream()
                .flatMap(ad -> findAuftrag2PeeringPartner(ad.getAuftragId()).stream())
                .collect(Collectors.toList());

        final Optional<Pair<SipPeeringPartner, Auftrag2PeeringPartner>> nextPeeringPartner = getNextPeeringPartner(auftrag2PeeringPartners, now);
        final Supplier<Optional<Pair<SipPeeringPartner, Auftrag2PeeringPartner>>> actualPeeringPartner = getActualPeeringPartner(auftrag2PeeringPartners, now);
        return OptionalTools
                .orElse(nextPeeringPartner, actualPeeringPartner)
                .map(ppToString)
                .orElse("");
    }

    private Supplier<Optional<Pair<SipPeeringPartner, Auftrag2PeeringPartner>>> getActualPeeringPartner(
            final List<Auftrag2PeeringPartner> auftrag2PeeringPartners, final Date now) {
        return () -> auftrag2PeeringPartners.stream()
                .filter(a2p -> (DateTools.isBefore(a2p.getGueltigVon(), now) || a2p.getGueltigVon().getTime() == now.getTime())
                        && DateTools.isAfter(a2p.getGueltigBis(), now))
                .max(Comparator.comparing(Auftrag2PeeringPartner::getGueltigVon))
                .map(a2p -> Pair.create(sipPeeringPartnerService.findPeeringPartnerById(a2p.getPeeringPartnerId()), a2p));
    }

    private Optional<Pair<SipPeeringPartner, Auftrag2PeeringPartner>> getNextPeeringPartner(
            final List<Auftrag2PeeringPartner> auftrag2PeeringPartners, final Date now) {
        return auftrag2PeeringPartners.stream()
                .filter(a2p -> DateTools.isAfter(a2p.getGueltigVon(), now)
                        && DateTools.isAfter(a2p.getGueltigBis(), now)
                        && DateTools.isBefore(a2p.getGueltigVon(), a2p.getGueltigBis()))
                .min(Comparator.comparing(Auftrag2PeeringPartner::getGueltigVon))
                .map(a2p -> Pair.create(sipPeeringPartnerService.findPeeringPartnerById(a2p.getPeeringPartnerId()), a2p));
    }

    private List<Auftrag2PeeringPartner> findAuftrag2PeeringPartner(final long auftragId) {
        try {
            return sipPeeringPartnerService.findAuftragPeeringPartners(auftragId);
        }
        catch (FindException e) {
            throw new DatabaseRuntimeException(e);
        }
    }

    private String getVoipDnBlockLengths(final List<AuftragDaten> auftragDaten) throws FindException {
        final StringBuilder value = new StringBuilder();
        for (final AuftragDaten ad : auftragDaten) {
            for (final AuftragVoipDNView auftragVoipDNView : voIPService.findVoIPDNView(ad.getAuftragId())) {
                if (auftragVoipDNView.isBlock()) {
                    if (value.length() > 0) {
                        value.append(String.format("%n"));
                    }
                    value.append(auftragVoipDNView.getOnKz())
                            .append("/")
                            .append(auftragVoipDNView.getDnBase())
                            .append(" ")
                            .append(auftragVoipDNView.getRangeFrom())
                            .append(" - ")
                            .append(auftragVoipDNView.getRangeTo())
                            .append('.');
                }
            }
        }
        return value.toString();
    }

    @Nonnull
    private String getVoipDnSipData(final List<AuftragDaten> auftragDaten) {
        final StringBuilder sb = new StringBuilder();

        final Function<AuftragVoipDNView, String> toString = view -> {
            final String sipDomain = (view.getSipDomain() == null) ? ""
                    : StringUtils.defaultString(view.getSipDomain().getStrValue());
            final String sipPassword = StringUtils.defaultString(view.getSipPassword());
            if (sb.length() > 0) {
                sb.append(String.format("%n"));
            }
            sb.append(generateLoginDataFormat(view.getFormattedSipLogin(), sipPassword, sipDomain));
            return sb.toString();
        };

        return getFromVoipDnSipData(auftragDaten, toString);
    }

    @Nonnull
    private String getLoginDataInternet(final Long orderNo) {
        try {
            final LoginData loginData = loginDataService.getLoginData(orderNo, true, false);
            if (loginData != null && loginData.getLoginDataInternet() != null) {
                final Map<String, String> internetLoginData = loginDataInternetAsMap(loginData.getLoginDataInternet());
                final String result = internetLoginData.entrySet().stream()
                        .map(e -> String.format("%s:\t%s%n", e.getKey(), e.getValue()))
                        .reduce("", (s1, s2) -> s1 + s2);
                return result;
            }
        }
        catch (LoginDataException e) {
            final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNo);
            LOGGER.warn(msg, e);
        }
        catch (Exception e) {
            final String msg = String.format("Technischer Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNo);
            LOGGER.error(msg, e);
        }
        return "";
    }

    private Map<String, String> loginDataInternetAsMap(LoginDataInternet loginDataInternet) {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        if (StringUtils.isNotEmpty(loginDataInternet.getPppUser())) {
            builder.put("PPP-User", loginDataInternet.getPppUserWithRealm());
        }
        if (StringUtils.isNotEmpty(loginDataInternet.getPppPassword())) {
            builder.put("PPP-Passwort", loginDataInternet.getPppPassword());
        }
        if (loginDataInternet.getIpMode() != null) {
            builder.put("IP Protokoll", loginDataInternet.getIpMode().humanReadable());
        }
        if (loginDataInternet.getPbitDaten() != null) {
            builder.put("PCP Wert für Daten (p-bit)", loginDataInternet.getPbitDaten().toString());
        }
        if (loginDataInternet.getPbitVoip() != null) {
            builder.put("PCP Wert für VoIP (p-bit)", loginDataInternet.getPbitVoip().toString());
        }
        if (loginDataInternet.getVlanIdDaten() != null) {
            builder.put("VLAN-ID Daten", loginDataInternet.getVlanIdDaten().toString());
        }
        if (loginDataInternet.getVlanIdVoip() != null) {
            builder.put("VLAN-ID VoIP", loginDataInternet.getVlanIdVoip().toString());
        }
        if (StringUtils.isNotEmpty(loginDataInternet.getAftrAddress())) {
            builder.put("AFTR Adresse", loginDataInternet.getAftrAddress());
        }
        if (loginDataInternet.getAtmParameterVPI() != null) {
            builder.put("ATM-Parameter VPI", loginDataInternet.getAtmParameterVPI().toString());
        }
        if (loginDataInternet.getAtmParameterVCI() != null) {
            builder.put("ATM-Parameter VCI", loginDataInternet.getAtmParameterVCI().toString());
        }

        return builder.build();
    }

    @Nonnull
    private String getLoginDataVoip(final Long orderNo, boolean withImsSwitchCheck, boolean withProductCheck) {
        try {
            final LoginData loginData = loginDataService.getLoginDataVoip(orderNo, true, withImsSwitchCheck, withProductCheck);
            if (loginData != null && loginData.getLoginDataVoip() != null
                    && !loginData.getLoginDataVoip().getVoipDnList().isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                for (LoginDataVoipDn loginDataVoipDn : loginData.getLoginDataVoip().getVoipDnList()) {
                    if (sb.length() > 0) {
                        sb.append(String.format("%n"));
                    }
                    if (loginDataVoipDn.getValidFrom() != null && loginDataVoipDn.getValidFrom().isAfter(LocalDate.now())) {
                        sb.append(String.format("gültig ab %s%n%n", loginDataVoipDn.getValidFrom().format(DateTimeFormatter.ofPattern(REPORTING_DATE_FORMAT))));
                    }
                    sb.append(generateLoginDataFormat(loginDataVoipDn.getSipHauptrufnummer(), loginDataVoipDn.getSipPassword(),
                            loginDataVoipDn.getSipDomain()));
                }
                return sb.toString();
            }
        }
        catch (LoginDataException e) {
            final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNo);
            LOGGER.warn(msg, e);
        }
        catch (Exception e) {
            final String msg = String.format("Technischer Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNo);
            LOGGER.error(msg, e);
        }
        return "";
    }

    private String generateLoginDataFormat(String sipUser, String sipPassword, String sipRegistrar) {
        return String.format(LINE_FORMAT_3_TABS, "SIP-Benutzername", sipUser) +
                String.format(LINE_FORMAT_4_TABS, "SIP-Passwort", sipPassword) +
                String.format(LINE_FORMAT_4_TABS, "SIP-Registrar", sipRegistrar);
    }

    @Nonnull
    private String getSipLogin(final List<AuftragDaten> auftragDaten) {
        return getFromVoipDnSipData(auftragDaten, view -> {
            final Optional<String> nextPlan = view.getFirstInFutureVoipDnPlanView()
                    .map(VoipDnPlanView::getSipLogin);
            final Optional<String> actualPlan = view.getActiveVoipDnPlanView(new Date())
                    .map(p -> p.getSipLogin() + "  gültig ab: " + DateTools.formatDate(p.getGueltigAb(), REPORTING_DATE_FORMAT));

            return nextPlan.orElse(actualPlan.orElse(""));
        });
    }

    @Nonnull
    private String getFromVoipDnSipData(final List<AuftragDaten> auftragDaten, final Function<AuftragVoipDNView, String> convertToString) { //NOSONAR; false positiv, arg 'convertToString' is used in map-call
        return auftragDaten.stream()
                .flatMap(this::findVoipDnViewsForAuftrag)
                .filter(view -> view != null && view.isBlock() && DateTools.isDateAfterOrEqual(view.getGueltigBis(), new Date()))
                .map(convertToString)
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");
    }

    private Stream<AuftragVoipDNView> findVoipDnViewsForAuftrag(final AuftragDaten auftragDaten) {  //NOSONAR; false positiv, because it is used as methodreference in 'getFromVoipDnSipData'
        try {
            return voIPService.findVoIPDNView(auftragDaten.getAuftragId()).stream();
        }
        catch (FindException e) {
            throw new DatabaseRuntimeException(e);
        }
    }

    @Nonnull
    private String getVoipDnPlan(final List<AuftragDaten> auftragDatenList) throws FindException {
        final String lineFormat = "%-25s%-18s%n";
        final String header = String.format(lineFormat, "Rufnummer", "Durchwahlbereich") +
                StringUtils.repeat("-", 25 + 18) + String.format("%n");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final StringBuilder sb = new StringBuilder();

        boolean moreThanOnePlan = false;
        for (final AuftragDaten auftragDaten : auftragDatenList) {
            for (final AuftragVoipDNView auftragVoipDnView : voIPService.findVoIPDNView(auftragDaten.getAuftragId())) {
                final Optional<VoipDnPlanView> planOptional = auftragVoipDnView.getFirstInFutureOrLatestVoipDnPlanView();
                if (!planOptional.isPresent()) {
                    continue;
                }
                final VoipDnPlanView plan = planOptional.get();

                if (moreThanOnePlan) {
                    sb.append(String.format("%n"));
                }
                else {
                    moreThanOnePlan = true;
                }

                sb.append("gültig ab ").append(dateFormat.format(plan.getGueltigAb()));
                sb.append(String.format("%n%n"));
                sb.append(header);

                for (final VoipDn2DnBlockView block : plan.getSortedVoipDn2DnBlockViews()) {
                    final String rufnummer = block.getOnkz() + " " + block.getDnBase();
                    sb.append(String.format(lineFormat, rufnummer, block.toString()));
                }
            }
        }
        return sb.toString();
    }

    private String getAnsprechpartnerKey(Long orderNo, String substring, Typ typ) throws FindException {
        Ansprechpartner ansprechpartner = findPreferredAnsprechpartner(orderNo, typ);
        if ((ansprechpartner == null) || (ansprechpartner.getAddress() == null)) {
            return "";
        }
        if ("name".equalsIgnoreCase(substring)) {
            return ansprechpartner.getAddress().getCombinedNameData();
        }
        if ("phone".equalsIgnoreCase(substring)) {
            return ansprechpartner.getAddress().getTelefon();
        }
        if ("email".equalsIgnoreCase(substring)) {
            return ansprechpartner.getAddress().getEmail();
        }
        if ("fax".equalsIgnoreCase(substring)) {
            return ansprechpartner.getAddress().getFax();
        }
        if ("mobile".equalsIgnoreCase(substring)) {
            return ansprechpartner.getAddress().getHandy();
        }
        return "";
    }

    private String transformNullToEmptyString(String stringToTransform) {
        if (stringToTransform == null) {
            return "";
        }
        return stringToTransform;
    }

    private String getCPEKey(Long orderNo, String substring) throws FindException {
        if (substring.startsWith(ES_A_PREFIX)) {
            return getCPE4ESKey(orderNo,
                    substring.substring(ES_A_PREFIX.length()), Endstelle.ENDSTELLEN_TYP_A);
        }
        if (substring.startsWith(ES_B_PREFIX)) {
            return getCPE4ESKey(orderNo,
                    substring.substring(ES_B_PREFIX.length()), Endstelle.ENDSTELLEN_TYP_B);
        }
        return getCPE4ESKey(orderNo, substring, null);
    }

    private String getCPE4ESKey(Long orderNo, String substring, String endstelle) throws FindException {
        if ("portforwarding".equalsIgnoreCase(substring)) {
            return generateAsciiFromPortForwardings(orderNo, findPortForwardingsForReporting(orderNo, endstelle));
        }
        if ("routing".equalsIgnoreCase(substring)) {
            return generateAsciiFromRoutings(orderNo, findRoutingsForReporting(orderNo, endstelle));
        }
        if ("ip.wan".equalsIgnoreCase(substring)) {
            return generateAsciiFromIPs(findWanEndgeraetIPsForReporting(orderNo, endstelle));
        }
        if ("ip.lan".equalsIgnoreCase(substring)) {
            return generateAsciiFromIPs(findLanEndgeraetIPsForReporting(orderNo, endstelle));
        }
        if ("ip.vrrp".equalsIgnoreCase(substring)) {
            return generateVrrpIp(generateAsciiFromIPs(findVrrpEndgeraetIPsForReporting(orderNo, endstelle)),
                    findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("dhcp".equalsIgnoreCase(substring)) {
            return generateAsciiDhcp(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("nat".equalsIgnoreCase(substring)) {
            return generateAsciiNat(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("hersteller".equalsIgnoreCase(substring)) {
            return generateAsciiHersteller(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("modellzusatz".equalsIgnoreCase(substring)) {
            return generateAsciiModellZusatz(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("modell".equalsIgnoreCase(substring)) {
            return generateAsciiModell(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("serial".equalsIgnoreCase(substring)) {
            return generateAsciiSerialNumber(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("bemerkung".equalsIgnoreCase(substring)) {
            return generateAsciiBemerkung(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("etage".equalsIgnoreCase(substring)) {
            return generateAsciiEtage(filterEG2Auftrag4Endstelle(findEg2AuftraegeForReporting(orderNo), endstelle));
        }
        if ("raum".equalsIgnoreCase(substring)) {
            return generateAsciiRaum(filterEG2Auftrag4Endstelle(findEg2AuftraegeForReporting(orderNo), endstelle));
        }
        if ("gebaeude".equalsIgnoreCase(substring)) {
            return generateAsciiGebaeude(filterEG2Auftrag4Endstelle(findEg2AuftraegeForReporting(orderNo), endstelle));
        }
        if ("name".equalsIgnoreCase(substring)) {
            return generateAsciiEgName(findEgsForReporting(orderNo, endstelle));
        }
        if ("servicevertrag".equalsIgnoreCase(substring)) {
            return generateAsciiServiceVertrag(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("kennung".equalsIgnoreCase(substring)) {
            return generateAsciiKennung(findEgConfigsForReporting(orderNo, endstelle));
        }
        if ("passwort".equalsIgnoreCase(substring)) {
            return generateAsciiPasswort(findEgConfigsForReporting(orderNo, endstelle));
        }

        return "";
    }

    private String getEndstellenKey(String substring, Endstelle endstelle) throws FindException {
        if (endstelle == null) {
            return "";
        }
        if (substring.startsWith(CARRIERBESTELLUNG_PREFIX)) {
            return getCarrierBestellungKey(
                    substring.substring(CARRIERBESTELLUNG_PREFIX.length()), endstelle);
        }
        Long addressId = endstelle.getAddressId();
        CCAddress address = ccKundenService.findCCAddress(addressId);

        if ("name".equalsIgnoreCase(substring)) {
            if (address != null) {
                return address.getCombinedNameData();
            }
            else {
                return endstelle.getName();
            }
        }
        if ("ort".equalsIgnoreCase(substring)) {
            if (address != null) {
                return address.getCombinedOrtOrtsteil();
            }
            else {
                return endstelle.getOrt();
            }
        }
        if ("plz".equalsIgnoreCase(substring)) {
            if (address != null) {
                return address.getPlz();
            }
            else {
                return endstelle.getPlz();
            }
        }
        if ("strasse".equalsIgnoreCase(substring) && address != null) {
            return address.getStrasse();

        }
        if ("strassezusatz".equalsIgnoreCase(substring) && address != null) {
            return address.getStrasseAdd();
        }
        if ("hausnummer".equalsIgnoreCase(substring) && address != null) {
            return address.getNummer();
        }
        if ("hausnummerzusatz".equalsIgnoreCase(substring) && address != null) {
            return address.getHausnummerZusatz();
        }
        if ("technologie".equalsIgnoreCase(substring)) {
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
            if (hvtStandort != null) {
                Reference reference = referenceService.findReference(hvtStandort.getStandortTypRefId());
                if (reference != null) {
                    return reference.getStrValue();
                }
            }
        }
        if ("anschlussart".equalsIgnoreCase(substring)) {
            Long anschlussartId;
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
            if (hvtStandort != null && HVTStandort.HVT_STANDORT_TYP_FTTB_H.equals(hvtStandort.getStandortTypRefId())) {
                anschlussartId = endstelle.getAnschlussart();
            }
            else {
                anschlussartId = hvtService.findAnschlussart4HVTStandort(endstelle.getHvtIdStandort());
            }
            Anschlussart anschlussart = physikService.findAnschlussart(anschlussartId);
            if (anschlussart != null) {
                return anschlussart.getAnschlussart();
            }
        }
        if ("leitungsart".equalsIgnoreCase(substring)) {
            Leitungsart leitungsart = physikService.findLeitungsart4ES(endstelle.getId());
            if (leitungsart != null) {
                return leitungsart.getName();
            }
        }
        if ("bandbreite".equalsIgnoreCase(substring)) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
            return leistungService.getBandwidthString(auftragDaten.getAuftragId());
        }
        return "";
    }

    private String getEndstelleConnectKey(String substring, EndstelleConnect ec) {
        if (ec == null) {
            return "";
        }
        if ("gebaeude".equalsIgnoreCase(substring)) {
            return ec.getGebaeude();
        }
        if ("etage".equalsIgnoreCase(substring)) {
            return ec.getEtage();
        }
        if ("raum".equalsIgnoreCase(substring)) {
            return ec.getRaum();
        }
        if ("schrank".equalsIgnoreCase(substring)) {
            return ec.getSchrank();
        }
        if ("uebergabe".equalsIgnoreCase(substring)) {
            return ec.getUebergabe();
        }
        if ("bandbreite".equalsIgnoreCase(substring)) {
            return ec.getBandbreite();
        }
        if ("if".equalsIgnoreCase(substring)) {
            String result = "";
            if (ec.getSchnittstelle() != null) {
                result += ec.getSchnittstelle() + " ";
            }
            if (ec.getEinstellung() != null) {
                result += ec.getEinstellung();
            }
            return result;
        }
        if ("routerinfo".equalsIgnoreCase(substring)) {
            return (ec.getRouterinfo() == null) ? "" : ec.getRouterinfo()
                    .toString();
        }
        if ("routertyp".equalsIgnoreCase(substring)) {
            return ec.getRoutertyp();
        }
        if ("bemerkung".equalsIgnoreCase(substring)) {
            return ec.getBemerkung();
        }
        if ("default.gateway".equalsIgnoreCase(substring)) {
            return ec.getDefaultGateway();
        }
        return "";
    }

    private String getCarrierBestellungKey(String substring, Endstelle endstelle) throws FindException {
        Carrierbestellung c = findLastCarrierBestellungOfEndstelle(endstelle.getId());
        if (c == null) {
            return "";
        }
        if ("bereitstellung".equalsIgnoreCase(substring)) {
            Date bereitstellung = c.getBereitstellungAm();
            if (bereitstellung != null) {
                SimpleDateFormat format = new SimpleDateFormat(REPORTING_DATE_FORMAT);
                return format.format(bereitstellung);
            }
        }
        if ("lbz".equalsIgnoreCase(substring)) {
            return c.getLbz();
        }
        if ("vtrnr".equalsIgnoreCase(substring)) {
            return c.getVtrNr();
        }
        if ("kundevorort".equalsIgnoreCase(substring)) {
            if (Boolean.TRUE.equals(c.getKundeVorOrt())) {
                return "Ja";
            }
            else if (Boolean.FALSE.equals(c.getKundeVorOrt())) {
                return "Nein";
            }
        }

        return "";
    }

    private String getIPSecS2SKey(Long orderNo, String substring, List<AuftragDaten> auftragDatenList)
            throws FindException {
        if ("hostname".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getHostname();
        }
        if ("hostname.passive".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getHostnamePassive();
        }
        if ("wanip".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getVirtualWanIp();
        }
        if ("wansubmask".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getVirtualWanSubmask();
        }
        if ("wangateway".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getWanGateway();
        }
        if ("lanip".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getVirtualLanIp();
        }
        if ("lan2Scramble".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getVirtualLan2Scramble();
        }
        if ("lansubmask".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getVirtualLanSubmask();
        }
        if ("dialinno".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getIsdnDialInNumber();
        }
        if ("splittunnel".equalsIgnoreCase(substring)) {
            Boolean splitTunnel = findIpSecSite2SiteForReporting(orderNo, auftragDatenList).getSplitTunnel();
            if (BooleanTools.nullToFalse(splitTunnel)) {
                return "Ja";
            }
            return "Nein";
        }
        if ("preshared".equalsIgnoreCase(substring)) {
            Boolean hasPreshared = findIpSecSite2SiteForReporting(orderNo, auftragDatenList).getHasPresharedKey();
            if (BooleanTools.nullToFalse(hasPreshared)) {
                return "Ja";
            }
            return "Nein";
        }
        if ("certificate".equalsIgnoreCase(substring)) {
            Boolean hasCertificate = findIpSecSite2SiteForReporting(orderNo, auftragDatenList).getHasCertificate();
            if (BooleanTools.nullToFalse(hasCertificate)) {
                return "Ja";
            }
            return "Nein";
        }
        if ("tokenserialno".equalsIgnoreCase(substring)) {
            return findIpSecTokenSerialNoForReporting(orderNo,
                    auftragDatenList);
        }
        if ("loopbackip".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getLoopbackIp();
        }
        if ("loopbackip.passive".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getLoopbackIpPassive();
        }
        if ("access.carrier".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getAccessCarrier();
        }
        if ("access.bandbreite".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getAccessBandwidth();
        }
        if ("access.typ".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo, auftragDatenList)
                    .getAccessType();
        }
        if ("access.auftragsnr".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getAccessAuftragNr();
        }
        if (substring.startsWith(IPSECS2S_ROUTER_PASSIV_PREFIX)) {
            return findIpSecSite2SiteEgInfosForReporting(
                    orderNo, substring.substring(IPSECS2S_ROUTER_PASSIV_PREFIX.length()), EG.EG_IPSEC_ROUTER_PASSIV);
        }
        if (substring.startsWith(IPSECS2S_ROUTER_AKTIV_PREFIX)) {
            return findIpSecSite2SiteEgInfosForReporting(orderNo,
                    substring.substring(IPSECS2S_ROUTER_AKTIV_PREFIX.length()), EG.EG_IPSEC_ROUTER_AKTIV);
        }
        if ("description".equalsIgnoreCase(substring)) {
            return findIpSecSite2SiteForReporting(orderNo,
                    auftragDatenList).getDescription();
        }
        return "";
    }

    private String findIpSecSite2SiteEgInfosForReporting(Long orderNo, String substring, final Long type)
            throws FindException {
        EG2Auftrag endgeraet = null;
        List<EG2Auftrag> eg2a = findEg2AuftraegeForReporting(orderNo);
        List<EG2Auftrag> selection = (List<EG2Auftrag>) CollectionTools.select(eg2a, object -> object.getEgId().equals(type));
        if (selection.size() == 1) {
            endgeraet = selection.get(0);
        }
        if (endgeraet != null) {
            if ("lanip".equalsIgnoreCase(substring)) {
                Set<EndgeraetIp> ipSet = endgeraet.getLanEndgeraetIps();
                return generateAsciiFromIPs(ipSet);
            }
            if ("wanip".equalsIgnoreCase(substring)) {
                Set<EndgeraetIp> ipSet = endgeraet.getWanEndgeraetIps();
                return generateAsciiFromIPs(ipSet);
            }
            EGConfig egConfig = endgeraeteService.findEGConfig(endgeraet.getId());
            if (egConfig != null) {
                if (egConfig.getEgType() != null && "model".equalsIgnoreCase(substring)) {
                    return egConfig.getEgType().getModell();
                }
                if ("serialno".equalsIgnoreCase(substring)) {
                    return egConfig.getSerialNumber();
                }
            }
        }
        return "";
    }

    private Carrierbestellung findLastCarrierBestellungOfEndstelle(Long esId) throws FindException {
        List<Carrierbestellung> cbs = carrierService.findCBs4EndstelleTx(esId);
        return ((cbs != null) && (!cbs.isEmpty())) ? cbs.get(0) : null;
    }

    private Endstelle findEndstelle(Long orderNo, String endstellenTyp) throws FindException {
        List<AuftragDaten> auftragDatenList = findAuftragDatenForReporting(orderNo);
        if ((auftragDatenList != null) && !auftragDatenList.isEmpty()) {
            Long auftragId = auftragDatenList.get(0).getAuftragId();
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle es : endstellen) {
                    if (es.getEndstelleTyp().equals(endstellenTyp)) {
                        return es;
                    }
                }
            }
        }
        return null;
    }

    private EndstelleConnect findEndstelleConnect(Long orderNo, String endstellenTyp) throws FindException {
        Endstelle endstelle = findEndstelle(orderNo, endstellenTyp);
        if (endstelle != null) {
            return connectService.findEndstelleConnectByEndstelle(endstelle);
        }
        return null;
    }

    private Ansprechpartner findPreferredAnsprechpartner(Long orderNo, Typ typ) throws FindException {
        List<AuftragDaten> auftragDatenList = findAuftragDatenForReporting(orderNo);
        if ((auftragDatenList != null) && !auftragDatenList.isEmpty()) {
            Long auftragId = auftragDatenList.get(0).getAuftragId();
            if (auftragId != null) {
                Ansprechpartner ap = ansprechpartnerService.findPreferredAnsprechpartner(typ, auftragId);
                if (ap != null) {
                    return ap;
                }
            }
        }
        return null;
    }

    private void assertNotNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                throw new IllegalArgumentException("The given parameter may not be null");
            }
        }
    }

    private String generateAsciiFromVerBez(List<String> vbzs) {
        assertNotNull(vbzs);

        Set<String> uniqueVbzs = new HashSet<>();
        for (String vbz : vbzs) {
            uniqueVbzs.add(vbz);
        }

        StringBuilder sb = new StringBuilder();
        for (String vbz : uniqueVbzs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(vbz);
        }
        return sb.toString();
    }

    private String generateAsciiFromPortForwardings(Long orderNo, List<PortForwarding> portForwardings) {
        assertNotNull(orderNo, portForwardings);

        StringBuilder sb = new StringBuilder();
        final String lineFormat = "%1$-12s %2$-50s%n";

        for (PortForwarding portForwarding : portForwardings) {
            if (sb.length() > 0) {
                sb.append(getLineBreak());
            }

            String wanIp = (portForwarding.getSourceIpAddressRef() != null)
                    ? portForwarding.getSourceIpAddressRef().getAbsoluteAddress() : "";
            String wanPort = portForwarding.getSourcePortNullsafe();
            String lanIp = (portForwarding.getDestIpAddressRef() != null)
                    ? portForwarding.getDestIpAddressRef().getAbsoluteAddress() : "";
            String lanPort = String.format("%s", portForwarding.getDestPort());
            String protokoll = portForwarding.getTransportProtocolNullsafe();

            appendLineIfArgNotNull(sb, "WAN-IP:", wanIp, lineFormat);
            appendLineIfArgNotNull(sb, "WAN-PORT:", wanPort, lineFormat);
            appendLineIfArgNotNull(sb, "LAN-IP:", lanIp, lineFormat);
            appendLineIfArgNotNull(sb, "LAN-PORT:", lanPort, lineFormat);
            appendLineIfArgNotNull(sb, "Protokoll:", protokoll, lineFormat);
        }

        return sb.toString();
    }

    private void appendLineIfArgNotNull(StringBuilder stringBuilder, String type, String arg, String format) {
        if (arg != null) {
            Formatter rowFormatter = new Formatter(stringBuilder, Locale.GERMANY);
            rowFormatter.format(format, type, arg);
        }
    }

    private String generateAsciiFromRoutings(Long orderNo, List<Routing> routings) {
        assertNotNull(orderNo, routings);

        // @formatter:off
        StringBuilder sb = new StringBuilder();
        final String lineFormat = "%1$-12s %2$-50s%n";

        for (Routing routing : routings) {
            if (sb.length() > 0) {
                sb.append(getLineBreak());
            }

            IPAddress ipAddress = routing.getDestinationAdressRef();
            String ip = (ipAddress.isIPV4()) ? ipAddress.getAddress() : IPToolsV6.instance().expandAddress(ipAddress.getAbsoluteAddress());

            appendLineIfArgNotNull(sb, "IP-Netz:", ip, lineFormat);
            appendLineIfArgNotNull(sb, "Gateway:", routing.getNextHop(), lineFormat);
        }
        // @formatter:on
        return sb.toString();
    }

    private String generateAsciiFromIPs(Collection<EndgeraetIp> endgeraetIps) {
        assertNotNull(endgeraetIps);

        StringBuilder sb = new StringBuilder();
        for (EndgeraetIp ip : endgeraetIps) {
            IPAddress ipAddress = ip.getIpAddressRef();
            if (ipAddress != null) {
                appendSeparatorIfNotEmpty(sb, STRING_IP_DELIMITER);
                sb.append(ipAddress.getEgDisplayAddress());
            }
        }
        return sb.toString();
    }

    private AbstractIPTools getIPTools(IPAddress ipAddress) {
        if (ipAddress.isIPV4()) {
            return IPToolsV4.instance();
        }
        else {
            return IPToolsV6.instance();
        }
    }

    private String getAddressWithoutPrefix(IPAddress ipAddress) {
        String result = "";
        if (ipAddress != null) {
            result = getIPTools(ipAddress).getAddressWithoutPrefix(ipAddress.getAbsoluteAddress());
        }
        return result;
    }

    private String generateAciiIntAccounts(Set<String> intAccounts) {
        assertNotNull(intAccounts);
        StringBuilder sb = new StringBuilder();
        for (String intAccount : intAccounts) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(intAccount);
        }
        return sb.toString();
    }

    private String generateAsciiNat(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            Boolean natActive = egConfig.getNatActive();
            appendSeparatorIfNotEmpty(sb);
            if (Boolean.TRUE.equals(natActive)) {
                sb.append("Aktiv");
            }
            else if (Boolean.FALSE.equals(natActive)) {
                sb.append("Inaktiv");
            }
            else {
                sb.append("Unbekannt");
            }
        }
        return sb.toString();
    }

    private String generateAsciiHersteller(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            if (egConfig.getEgType() != null) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(egConfig.getEgType().getHersteller());
            }
        }
        return sb.toString();
    }

    private String generateAsciiModell(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            if (egConfig.getEgType() != null) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(egConfig.getEgType().getModell());
            }
        }
        return sb.toString();
    }

    private String generateAsciiModellZusatz(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            if (egConfig.getEgType() != null) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(egConfig.getModellZusatz());
            }
        }
        return sb.toString();
    }

    private String generateAsciiBemerkung(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(egConfig.getBemerkungKunde());
        }
        return sb.toString();
    }

    private String generateAsciiEgName(List<EG> egs) {
        StringBuilder sb = new StringBuilder();
        for (EG eg : egs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(eg.getEgName());
        }
        return sb.toString();
    }

    private String generateAsciiSerialNumber(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(egConfig.getSerialNumber());
        }
        return sb.toString();
    }

    private String generateAsciiDhcp(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            Boolean dhcpActive = egConfig.getDhcpActive();
            appendSeparatorIfNotEmpty(sb);
            if (Boolean.TRUE.equals(dhcpActive)) {
                sb.append("Aktiv");
            }
            else if (Boolean.FALSE.equals(dhcpActive)) {
                sb.append("Inaktiv");
            }
            else {
                sb.append("Unbekannt");
            }
        }
        return sb.toString();
    }

    private String generateAsciiServiceVertrag(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            if (egConfig.getServiceVertrag() != null) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(egConfig.getServiceVertrag());
            }
        }
        return sb.toString();
    }

    private String generateAsciiKennung(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(egConfig.getEgUser());
        }
        return sb.toString();
    }

    private String generateAsciiPasswort(List<EGConfig> egConfigs) {
        StringBuilder sb = new StringBuilder();
        for (EGConfig egConfig : egConfigs) {
            appendSeparatorIfNotEmpty(sb);
            sb.append(egConfig.getEgPassword());
        }
        return sb.toString();
    }

    private String generateAsciiRaum(List<EG2Auftrag> eg2Auftraege) {
        StringBuilder sb = new StringBuilder();
        for (EG2Auftrag eg2a : eg2Auftraege) {
            String raum = eg2a.getRaum();
            if (StringUtils.isNotBlank(raum)) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(raum);
            }
        }
        return sb.toString();
    }

    private String generateAsciiGebaeude(List<EG2Auftrag> eg2Auftraege) {
        StringBuilder sb = new StringBuilder();
        for (EG2Auftrag eg2a : eg2Auftraege) {
            String gebaeude = eg2a.getGebaeude();
            if (StringUtils.isNotBlank(gebaeude)) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(gebaeude);
            }
        }
        return sb.toString();
    }

    private String generateAsciiEtage(List<EG2Auftrag> eg2Auftraege) {
        StringBuilder sb = new StringBuilder();
        for (EG2Auftrag eg2a : eg2Auftraege) {
            String etage = eg2a.getEtage();
            if (StringUtils.isNotBlank(etage)) {
                appendSeparatorIfNotEmpty(sb);
                sb.append(etage);
            }
        }
        return sb.toString();
    }

    private String generateAsciiIpAddresses(List<AuftragDaten> auftragDatenList) throws FindException {
        StringBuilder sb = new StringBuilder();
        for (AuftragDaten auftragDaten : auftragDatenList) {
            final List<IPAddress> ipAddressList = getIpAddressService().findAssignedIPs4BillingOrder(
                    auftragDaten.getAuftragNoOrig());
            for (IPAddress ipAddress : ipAddressList) {
                appendSeparatorIfNotEmpty(sb, STRING_IP_DELIMITER);
                sb.append(getFormatedIPAddress4Type(ipAddress));
            }
        }
        return sb.toString();
    }

    /**
     * Fixe IP Addressen ohne Praefix Laenge
     */
    private String getFormatedIPAddress4Type(IPAddress ipAddress) {
        String ip = (ipAddress.requiresNoPrefixLength()) ? getAddressWithoutPrefix(ipAddress) : ipAddress
                .getAbsoluteAddress();
        if (ipAddress.getPurpose() != null) {
            return String.format("%s (%s)", ip, ipAddress.getPurpose().getStrValue());
        }
        return ip;
    }

    private void appendSeparatorIfNotEmpty(StringBuilder sb) {
        appendSeparatorIfNotEmpty(sb, STRING_DELIMITER);
    }

    private void appendSeparatorIfNotEmpty(StringBuilder sb, String separator) {
        if (sb.length() != 0) {
            sb.append(separator);
        }
    }

    private List<AuftragDaten> findAuftragDatenForReporting(Long orderNo) throws FindException {
        List<AuftragDaten> auftragDatenList = auftragService.findAuftragDaten4OrderNoOrigTx(orderNo);
        List<AuftragDaten> auftragDatenToReturn = new ArrayList<>();
        if (auftragDatenList != null) {
            for (AuftragDaten auftragDaten : auftragDatenList) {
                Produkt produkt = produktService.findProdukt(auftragDaten.getProdId());
                Long auftragStatus = auftragDaten.getAuftragStatusId();

                if ((auftragStatus < AuftragStatus.AUFTRAG_GEKUENDIGT) && !AuftragStatus.STORNO.equals(auftragStatus)
                        && !AuftragStatus.ABSAGE.equals(auftragStatus)
                        && !BooleanTools.nullToFalse(produkt.getVbzUseFromMaster())) {
                    auftragDatenToReturn.add(auftragDaten);
                }
            }
        }
        return auftragDatenToReturn;
    }

    private Set<String> findIntAccountsForReporting(List<AuftragDaten> auftragDatenList) throws FindException {
        Set<String> result = new HashSet<>();
        for (AuftragDaten ad : auftragDatenList) {
            List<IntAccount> intAccounts = accountService.findIntAccounts4Auftrag(ad.getAuftragId());
            if (CollectionTools.isNotEmpty(intAccounts)) {
                for (IntAccount account : intAccounts) {
                    result.add(account.getAccount());
                }
            }
        }
        return result;
    }

    private List<String> findVbzsForReporting(Long orderNo) throws FindException {
        List<String> vbzs = new ArrayList<>();
        List<CCAuftragIDsView> auftragIdsView = auftragService.findAufragIdAndVbz4AuftragNoOrig(orderNo);
        for (CCAuftragIDsView view : auftragIdsView) {
            Long status = view.getAuftragStatusId();
            if ((status < AuftragStatus.AUFTRAG_GEKUENDIGT) && !AuftragStatus.STORNO.equals(status)
                    && !AuftragStatus.ABSAGE.equals(status)) {
                String vbz = view.getVbz();
                if (vbz != null) {
                    vbzs.add(vbz);
                }
            }
        }
        return vbzs;
    }

    private List<EG2Auftrag> findEg2AuftraegeForReporting(Long orderNo) throws FindException {
        List<AuftragDaten> auftragDatenList = findAuftragDatenForReporting(orderNo);
        List<EG2Auftrag> eg2AuftraegeToReturn = new ArrayList<>();
        if (auftragDatenList != null) {
            for (AuftragDaten auftragDaten : auftragDatenList) {
                List<EG2Auftrag> eg2Auftraege = endgeraeteService.findEGs4Auftrag(auftragDaten.getAuftragId());
                eg2AuftraegeToReturn.addAll(eg2Auftraege);
            }
        }
        return eg2AuftraegeToReturn;
    }

    List<EGConfig> findEgConfigsForReporting(Long orderNo, String esTyp) throws FindException {
        List<EG2Auftrag> eg2Auftraege = findEg2AuftraegeForReporting(orderNo);
        eg2Auftraege = filterEG2Auftrag4Endstelle(eg2Auftraege, esTyp);
        List<EGConfig> egConfigsToReturn = new ArrayList<>();
        if (eg2Auftraege != null) {
            for (EG2Auftrag eg2Auftrag : eg2Auftraege) {
                EGConfig egConfig = endgeraeteService.findEGConfig(eg2Auftrag.getId());
                egConfigsToReturn.add(egConfig);
            }
        }
        return egConfigsToReturn;
    }

    private List<EG> findEgsForReporting(Long orderNo, String esTyp) throws FindException {
        List<EG2Auftrag> eg2Auftraege = findEg2AuftraegeForReporting(orderNo);
        eg2Auftraege = filterEG2Auftrag4Endstelle(eg2Auftraege, esTyp);
        List<EG> egs = new ArrayList<>();
        if (eg2Auftraege != null) {
            for (EG2Auftrag eg2Auftrag : eg2Auftraege) {
                egs.add(endgeraeteService.findEgById(eg2Auftrag.getEgId()));
            }
        }
        return egs;
    }

    private List<Routing> findRoutingsForReporting(Long orderNo, String esTyp) throws FindException {
        List<EG2Auftrag> eg2as = findEg2AuftraegeForReporting(orderNo);
        eg2as = filterEG2Auftrag4Endstelle(eg2as, esTyp);
        List<Routing> routingsToReturn = new ArrayList<>();
        for (EG2Auftrag eg2a : eg2as) {
            routingsToReturn.addAll(eg2a.getRoutings());
        }
        Collections.sort(routingsToReturn, Routing.ROUTING_COMPARATOR);
        return routingsToReturn;
    }

    private List<PortForwarding> findPortForwardingsForReporting(Long orderNo, String esTyp) throws FindException {
        List<EGConfig> egConfigs = findEgConfigsForReporting(orderNo, esTyp);
        List<PortForwarding> portForwardingsToReturn = new ArrayList<>();
        for (EGConfig egConfig : egConfigs) {
            for (PortForwarding pf : egConfig.getPortForwardings()) {
                if ((pf.getActive() == null) || pf.getActive().equals(Boolean.TRUE)) {
                    portForwardingsToReturn.add(pf);
                }
            }
        }
        Collections.sort(portForwardingsToReturn, PortForwarding.PORTFORWARDING_COMPARATOR);
        return portForwardingsToReturn;
    }

    private List<EndgeraetIp> findLanEndgeraetIPsForReporting(Long orderNo, String esTyp) throws FindException {
        return findEndgeraetIPsForReporting(orderNo, esTyp, EG2Auftrag::getLanEndgeraetIps);
    }


    private List<EndgeraetIp> findVrrpEndgeraetIPsForReporting(Long orderNo, String esTyp) throws FindException {
        return findEndgeraetIPsForReporting(orderNo, esTyp, EG2Auftrag::getVrrpEndgeraetIps);
    }

    private List<EndgeraetIp> findWanEndgeraetIPsForReporting(Long orderNo, String esTyp) throws FindException {
        return findEndgeraetIPsForReporting(orderNo, esTyp, EG2Auftrag::getWanEndgeraetIps);
    }

    private List<EndgeraetIp> findEndgeraetIPsForReporting(Long orderNo, String esTyp,
            Function<EG2Auftrag, Set<EndgeraetIp>> lambda) throws FindException {
        List<EG2Auftrag> eg2as = findEg2AuftraegeForReporting(orderNo);
        eg2as = filterEG2Auftrag4Endstelle(eg2as, esTyp);
        List<EndgeraetIp> ipsToReturn = new ArrayList<>();
        for (EG2Auftrag eg2a : eg2as) {
            ipsToReturn.addAll(lambda.apply(eg2a));
        }
        Collections.sort(ipsToReturn, EndgeraetIp.ENDGERAETIP_COMPARATOR);
        return ipsToReturn;
    }


    private String generateVrrpIp(String vrrpIps, List<EGConfig> egConfigs) {
        if (StringUtils.isBlank(vrrpIps) || CollectionUtils.isEmpty(egConfigs)) {
            return "";
        }

        String priority = "";
        EGConfig egConfig = egConfigs.get(0);
        if (egConfig.getVrrpPriority() != null) {
            priority = STRING_IP_DELIMITER + egConfig.getVrrpPriority().getDisplayText();
        }
        return String.format("%s%s",vrrpIps, priority);
    }

    private List<EG2Auftrag> filterEG2Auftrag4Endstelle(List<EG2Auftrag> eg2as, String esTyp) throws FindException {
        if (StringUtils.isBlank(esTyp) || CollectionTools.isEmpty(eg2as)) {
            return eg2as;
        }
        List<EG2Auftrag> result = new ArrayList<>();
        for (EG2Auftrag eg2a : eg2as) {
            Endstelle es = endstellenService.findEndstelle4Auftrag(eg2a.getAuftragId(), esTyp);
            if ((es != null) && NumberTools.equal(es.getId(), eg2a.getEndstelleId())) {
                result.add(eg2a);
            }
        }
        return result;
    }

    private IPSecSite2Site findIpSecSite2SiteForReporting(Long orderNo, List<AuftragDaten> auftragDatenList)
            throws FindException {
        if (auftragDatenList.size() != 1) {
            String msgKey = auftragDatenList.size() + " Aufträge für Billing Auftrag Nr. " + orderNo
                    + " gefunden, sollte genau 1 sein.";
            throw new FindException(msgKey);
        }
        AuftragDaten auftragDaten = auftragDatenList.get(0);
        IPSecSite2Site ipSecS2S = ipSecService.findIPSecSiteToSite(auftragDaten.getAuftragId());
        if (ipSecS2S == null) {
            String msgKey = "Konnte keine IPSecSite2Site Infos zu Billing Auftrag Nr. " + orderNo + " finden.";
            throw new FindException(msgKey);
        }
        return ipSecS2S;
    }

    private String findIpSecTokenSerialNoForReporting(Long orderNo, List<AuftragDaten> auftragDatenList)
            throws FindException {
        if (auftragDatenList.size() != 1) {
            String msgKey = auftragDatenList.size() + " Aufträge für Billing Auftrag Nr. " + orderNo
                    + " gefunden, sollte genau 1 sein.";
            throw new FindException(msgKey);
        }
        AuftragDaten auftragDaten = auftragDatenList.get(0);
        List<IPSecClient2SiteToken> ipSecTokens = ipSecService.findClient2SiteTokens(auftragDaten.getAuftragId());
        if ((ipSecTokens == null) || (ipSecTokens.isEmpty())) {
            String msgKey = "Konnte keine IPSecClient2Site-Tokens zu Billing Auftrag Nr. " + orderNo + " finden.";
            throw new FindException(msgKey);
        }

        IPSecClient2SiteToken result = ipSecTokens.get(0);
        for (IPSecClient2SiteToken ipSecClient2SiteToken : ipSecTokens) {
            if (result.getLieferdatum().compareTo(ipSecClient2SiteToken.getLieferdatum()) > 0) {
                result = ipSecClient2SiteToken;
            }
        }
        return result.getSerialNumber();
    }

    private String findHousingForReporting(Long orderNo, String reportingKey) {
        String result = "";
        String reportingKey1 = reportingKey.toLowerCase();
        try {
            List<Long> auftragIds = getAuftraegeForOrdernoOrig(orderNo);
            for (Long auftragID : auftragIds) {
                AuftragHousing auftragHousing = housingService.findAuftragHousing(auftragID);
                if (reportingKey1.contains("gebaeude.")) {
                    result = appendWhenContentIsNotDuplicate(result, findBuildingKey(auftragHousing, reportingKey1));
                }
                else if (reportingKey1.contains("stockwerk")) {
                    result = appendWhenContentIsNotDuplicate(result, findFloorKey(auftragHousing));
                }
                else if (reportingKey1.contains("raum")) {
                    result = appendWhenContentIsNotDuplicate(result, findRoomKey(auftragHousing));
                }
                else if (reportingKey1.contains("parzelle")) {
                    result = appendWhenContentIsNotDuplicate(result, findParcelKey(auftragHousing));
                }
                else if (reportingKey1.contains("transponder.")) {
                    result = appendWhenContentIsNotDuplicate(result, findTransponderKeys(auftragHousing, reportingKey1));
                }
                else if (reportingKey1.contains("schrank") && (auftragHousing.getRack() != null)) {
                    result = appendWhenContentIsNotDuplicate(result, auftragHousing.getRack());
                }
                else if (reportingKey1.contains("hoeheneinheit") && (auftragHousing.getRackUnits() != null)) {
                    result = appendWhenContentIsNotDuplicate(result, auftragHousing.getRackUnits().toString());
                }
                else if (reportingKey1.contains("stromkreis.")) {
                    result = appendWhenContentIsNotDuplicate(result, getStromKreisInfos(auftragHousing, reportingKey1));
                }
            }

        }
        catch (Exception e) {
            // bei auftretender Exception wird ein Leerstring zurückgegeben
            LOGGER.error(e.getMessage());
        }
        return removeLastComma(result);
    }

    private List<Long> getAuftraegeForOrdernoOrig(Long orderNo) throws FindException {
        List<Long> housingAuftraege = new ArrayList<>();
        List<AuftragDaten> auftragDatenList = findAuftragDatenForReporting(orderNo);
        if (CollectionTools.isNotEmpty(auftragDatenList)) {
            for (AuftragDaten auftragDaten : auftragDatenList) {
                Produkt produkt = produktService.findProdukt4Auftrag(auftragDaten.getAuftragId());
                if (produkt != null) {
                    ProduktGruppe produktGruppe = produktService.findProduktGruppe(produkt.getProduktGruppeId());
                    if ((produktGruppe != null) && NumberTools.equal(produktGruppe.getId(), ProduktGruppe.HOUSING)) {
                        housingAuftraege.add(auftragDaten.getAuftragId());
                    }
                }
            }
        }
        return housingAuftraege;
    }

    private String appendWhenContentIsNotDuplicate(String commaSeparatedList, String newEntry) {
        if (StringUtils.isBlank(newEntry) || commaSeparatedList.contains(newEntry)) {
            return commaSeparatedList;
        }
        return commaSeparatedList + newEntry + ", ";
    }

    private String getStromKreisInfos(AuftragHousing auftrag, String reportingKey) {
        if (auftrag != null) {
            if (reportingKey.contains(".anzahl") && (auftrag.getElectricCircuitCount() != null)) {
                return auftrag.getElectricCircuitCount().toString();
            }
            else if (reportingKey.contains(".leistung") && (auftrag.getElectricCircuitCapacity() != null)) {
                return auftrag.getElectricCircuitCapacity().toString();
            }
            else if (reportingKey.contains(".sicherung") && (auftrag.getElectricSafeguard() != null)) {
                return auftrag.getElectricSafeguard().toString();
            }
            else if (reportingKey.contains(".leistung") && (auftrag.getElectricCircuitCapacity() != null)) {
                return auftrag.getElectricCircuitCapacity().toString();
            }
            else if (reportingKey.contains(".zaehler1.nr") && (auftrag.getElectricCounterNumber() != null)) {
                return auftrag.getElectricCounterNumber();
            }
            else if (reportingKey.contains(".zaehler1.bereitstellung") && (auftrag.getElectricCounterStart() != null)) {
                return auftrag.getElectricCounterStart().toString();
            }
            else if (reportingKey.contains(".zaehler1.kuendigung") && (auftrag.getElectricCounterEnd() != null)) {
                return auftrag.getElectricCounterEnd().toString();
            }
            else if (reportingKey.contains(".zaehler2.nr") && (auftrag.getElectricCounterNumber2() != null)) {
                return auftrag.getElectricCounterNumber2();
            }
            else if (reportingKey.contains(".zaehler2.bereitstellung") && (auftrag.getElectricCounterStart2() != null)) {
                return auftrag.getElectricCounterStart2().toString();
            }
            else if (reportingKey.contains(".zaehler2.kuendigung") && (auftrag.getElectricCounterEnd2() != null)) {
                return auftrag.getElectricCounterEnd2().toString();
            }
            else if (reportingKey.contains(".zaehler3.nr") && (auftrag.getElectricCounterNumber3() != null)) {
                return auftrag.getElectricCounterNumber3();
            }
            else if (reportingKey.contains(".zaehler3.bereitstellung") && (auftrag.getElectricCounterStart3() != null)) {
                return auftrag.getElectricCounterStart3().toString();
            }
            else if (reportingKey.contains(".zaehler3.kuendigung") && (auftrag.getElectricCounterEnd3() != null)) {
                return auftrag.getElectricCounterEnd3().toString();
            }
            else if (reportingKey.contains(".zaehler4.nr") && (auftrag.getElectricCounterNumber4() != null)) {
                return auftrag.getElectricCounterNumber4();
            }
            else if (reportingKey.contains(".zaehler4.bereitstellung") && (auftrag.getElectricCounterStart4() != null)) {
                return auftrag.getElectricCounterStart4().toString();
            }
            else if (reportingKey.contains(".zaehler4.kuendigung") && (auftrag.getElectricCounterEnd4() != null)) {
                return auftrag.getElectricCounterEnd4().toString();
            }
        }
        return "";
    }

    private String findBuildingKey(AuftragHousing auftrag, String reportingKey) throws FindException {
        if ((auftrag != null) && (auftrag.getAuftragId() != null)) {
            HousingBuilding building = housingService.findHousingBuilding4Auftrag(auftrag.getAuftragId());
            if (building != null) {
                String buildingKey = null;
                if (reportingKey.contains(".name")) {
                    buildingKey = building.getBuilding();
                }
                else if (reportingKey.contains(".strasse") && (building.getAddress() != null)) {
                    buildingKey = building.getAddress().getStrasse();
                }
                else if (reportingKey.contains(".hausnr") && (building.getAddress() != null)) {
                    buildingKey = building.getAddress().getNummer();
                }
                else if (reportingKey.contains(".plz") && (building.getAddress() != null)) {
                    buildingKey = building.getAddress().getPlz();
                }
                else if (reportingKey.contains(".ort") && (building.getAddress() != null)) {
                    buildingKey = building.getAddress().getCombinedOrtOrtsteil();
                }
                if (buildingKey == null) {
                    buildingKey = "";
                }
                return buildingKey;
            }
        }
        return "";
    }

    private String findFloorKey(AuftragHousing auftrag) throws FindException {
        if ((auftrag != null) && (auftrag.getFloorId() != null)) {
            HousingFloor floor = housingService.findHousingFloorById(auftrag.getFloorId());
            if ((floor != null) && (floor.getFloor() != null)) {
                return floor.getFloor();
            }
        }
        return "";
    }

    private String findRoomKey(AuftragHousing auftrag) throws FindException {
        if ((auftrag != null) && (auftrag.getRoomId() != null)) {
            HousingRoom room = housingService.findHousingRoomById(auftrag.getRoomId());
            if ((room != null) && (room.getRoom() != null)) {
                return room.getRoom();
            }
        }
        return "";
    }

    private String findParcelKey(AuftragHousing auftrag) throws FindException {
        if ((auftrag != null) && (auftrag.getParcelId() != null)) {
            HousingParcel parcel = housingService.findHousingParcelById(auftrag.getParcelId());
            if ((parcel != null) && (parcel.getParcel() != null)) {
                return parcel.getParcel();
            }
        }
        return "";
    }

    private String findTransponderKeys(AuftragHousing auftrag, String reportingKey) throws FindException {
        StringBuilder transponderString = new StringBuilder();
        if ((auftrag != null) && (auftrag.getAuftragId() != null)) {
            List<AuftragHousingKeyView> transponders = housingService.findHousingKeys(auftrag.getAuftragId());
            if (CollectionTools.isNotEmpty(transponders)) {
                for (AuftragHousingKeyView auftragHousingKeyView : transponders) {
                    if (reportingKey.contains(".namen")
                            && ((auftragHousingKeyView.getCustomerFirstName() != null) || (auftragHousingKeyView
                            .getCustomerLastName() != null))) {
                        String firstName = (auftragHousingKeyView.getCustomerFirstName() != null) ? auftragHousingKeyView
                                .getCustomerFirstName()
                                : "";
                        String lastName = (auftragHousingKeyView.getCustomerLastName() != null) ? auftragHousingKeyView
                                .getCustomerLastName() : "";
                        if (firstName.length() > 0) {
                            firstName = firstName + " ";
                        }
                        transponderString.append(firstName).append(lastName).append(", ");
                    }
                    else if (reportingKey.contains(".nummern") && (auftragHousingKeyView.getTransponderId() != null)) {
                        transponderString.append(auftragHousingKeyView.getTransponderId().toString()).append(", ");
                    }
                }
            }
        }
        return removeLastComma(transponderString.toString());
    }

    String findVoIPDN2EGPort(List<AuftragDaten> auftragDaten) throws FindException {
        StringBuilder sb = new StringBuilder();
        List<AuftragVoipDNView> data = Lists.newArrayList();
        Comparator<EndgeraetPort> compPorts = new EndgeraetPortComparator();
        SortedSet<EndgeraetPort> ports = new TreeSet<>(compPorts);

        // alle aufträge durchgehen und daten sammeln
        for (AuftragDaten auftrag : auftragDaten) {
            // alle views finden und in map ablegen
            for (AuftragVoipDNView view : voIPService.findVoIPDNView(auftrag.getAuftragId())) {
                AuftragVoIPDN value = voIPService.findByAuftragIDDN(auftrag.getAuftragId(), view.getDnNoOrig());
                if (value != null) {
                    data.add(view);
                    // ports suchen und im set ablegen
                    final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts =
                            voIPService.findAuftragVoIPDN2EGPorts(value.getId());
                    for (AuftragVoIPDN2EGPort a2egport : auftragVoIPDN2EGPorts) {
                        if (DateTools.isHurricanEndDate(a2egport.getValidTo())) {
                            ports.add(a2egport.getEgPort());
                        }
                    }
                }
                else {
                    LOGGER.error(String.format("No AuftragVoIPDN for Auftrag %s", view.getAuftragId()));
                }
            }
        }

        final SimpleDateFormat format = new SimpleDateFormat(REPORTING_DATE_FORMAT);
        // String bauen
        for (EndgeraetPort port : ports) {
            if (sb.length() > 0) {
                sb.append(String.format("%n"));
            }
            sb.append(port.getName()).append(": ");
            for (AuftragVoipDNView entry : data) {
                for (final SelectedPortsView view : entry.getSelectedPorts()) {
                    if (DateTools.isHurricanEndDate(view.getValidTo()) && view.isPortSelected(port.getNumber() - 1)) {
                        sb.append(entry.getTaifunDescription())
                                .append(" (Gültig ab: ")
                                .append(format.format(view.getValidFrom()))
                                .append("), ");
                    }
                }
            }
            if (sb.toString().endsWith(", ")) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }

        return sb.toString();
    }

    private String getMVSKey(List<AuftragDaten> auftragDaten, String substring) throws FindException {
        if (substring.startsWith(MVS_ENTERPRISE_PREFIX)) {
            return getMVSEnterpriseKey(auftragDaten, substring.substring(MVS_ENTERPRISE_PREFIX.length()));
        }
        if (substring.startsWith(MVS_SITE_PREFIX)) {
            return getMVSSiteKey(auftragDaten, substring.substring(MVS_SITE_PREFIX.length()));
        }
        return "";
    }

    private String getMVSProperty(Object bean, String propertyName) throws FindException {
        if (bean == null) {
            return "";
        }
        try {
            return (String) PropertyUtils.getProperty(bean, propertyName);
        }
        catch (Exception e) {
            throw new FindException(String.format(
                    "Das Property %s des MVS Auftrages konnte nicht ermittelt werden!", propertyName));
        }
    }

    private AuftragMVSEnterprise findMVSEnterprise(List<AuftragDaten> listAuftragDaten) throws FindException {
        AuftragMVSEnterprise auftragMVSEnterprise = null;
        for (AuftragDaten auftragDaten : listAuftragDaten) {
            auftragMVSEnterprise = mvsService.findMvsEnterprise4Auftrag(auftragDaten.getAuftragId());
            if (auftragMVSEnterprise != null) {
                break;
            }
        }
        return auftragMVSEnterprise;
    }

    private AuftragMVSSite findMVSSite(List<AuftragDaten> listAuftragDaten) throws FindException {
        AuftragMVSSite auftragMVSSite = null;
        for (AuftragDaten auftragDaten : listAuftragDaten) {
            auftragMVSSite = mvsService.findMvsSite4Auftrag(auftragDaten.getAuftragId(), false);
            if (auftragMVSSite != null) {
                break;
            }
        }
        return auftragMVSSite;
    }

    private String getMVSEnterpriseKey(List<AuftragDaten> listAuftragDaten, String substring) throws FindException {
        if ("username".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSEnterprise(listAuftragDaten), "userName");
        }
        if ("password".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSEnterprise(listAuftragDaten), "password");
        }
        if ("domain".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSEnterprise(listAuftragDaten), "domain");
        }
        if ("email.address".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSEnterprise(listAuftragDaten), "mail");
        }

        return "";
    }

    private String getMVSSiteKey(List<AuftragDaten> listAuftragDaten, String substring) throws FindException {
        if ("username".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSSite(listAuftragDaten), "userName");
        }
        if ("password".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSSite(listAuftragDaten), "password");
        }
        if ("subdomain".equalsIgnoreCase(substring)) {
            return getMVSProperty(findMVSSite(listAuftragDaten), "subdomain");
        }
        if ("qualified.domain".equalsIgnoreCase(substring)) {
            AuftragMVSSite auftragMVSSite = findMVSSite(listAuftragDaten);
            if (auftragMVSSite != null) {
                return mvsService.getQualifiedDomain(
                        mvsService.findEnterpriseForSiteAuftragId(auftragMVSSite.getAuftragId()), auftragMVSSite);
            }
        }

        return "";
    }

    private String getIPv6Key(Long orderNo, List<AuftragDaten> listAuftragDaten, String substring) throws FindException {
        if (substring.startsWith("dynamic_address")) {
            return (isDynamicIPv6Aktive(listAuftragDaten)) ? "ja" : "nein";
        }
        else if (substring.startsWith("prefix")) {
            return getIPv6Prefix(orderNo);
        }
        return "";
    }

    private boolean isDynamicIPv6Aktive(List<AuftragDaten> listAuftragDaten) throws FindException {
        for (AuftragDaten auftragDaten : listAuftragDaten) {
            if (leistungService.hasTechLeistung(TechLeistung.ID_DYNAMIC_IP_V6, auftragDaten.getAuftragId(), true)) {
                return true;
            }
        }
        return false;
    }

    private String getIPv6Prefix(Long orderNo) throws FindException {
        List<IPAddress> prefixes = ipAddressService.findV6PrefixesByBillingOrderNumber(orderNo);
        if (CollectionUtils.isNotEmpty(prefixes)) {
            StringBuilder sb = new StringBuilder();
            for (IPAddress prefix : prefixes) {
                appendSeparatorIfNotEmpty(sb, STRING_IP_DELIMITER);
                sb.append(prefix.getAddress());
            }
            return sb.toString();
        }
        return "";
    }

    // entfernt letztes Komma der Kommaseparierten liste
    private String removeLastComma(String commaSeparated) {
        if ((commaSeparated != null) && commaSeparated.endsWith(", ")) {
            return commaSeparated.substring(0,
                    commaSeparated.length() - 2);
        }
        return commaSeparated;
    }

    /**
     * Called by test code
     */
    public void setIpSecService(IPSecService ipSecService) {
        this.ipSecService = ipSecService;
    }

    /**
     * Called by test code
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Called by test code
     */
    public void setEndgeraeteService(EndgeraeteService endgeraeteService) {
        this.endgeraeteService = endgeraeteService;
    }

    /**
     * Called by test code
     */
    public void setAnsprechpartnerService(AnsprechpartnerService ansprechpartnerService) {
        this.ansprechpartnerService = ansprechpartnerService;
    }

    /**
     * Called by test code
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * @param connectService the service to be set
     */
    public void setConnectService(ConnectService connectService) {
        this.connectService = connectService;
    }

    /**
     * Called by test code
     */
    public void setCcKundenService(CCKundenService ccKundenService) {
        this.ccKundenService = ccKundenService;
    }

    /**
     * Called by test code
     */
    public void setCarrierService(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Called by test code
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Wird nur fuer den Test benoetigt.
     *
     * @param ipAddressService The ipAddressService to set.
     */
    void setIpAddressService(IPAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

    /**
     * Called by test code
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Called by test code
     */
    public void setVoIPService(VoIPService voIPService) {
        this.voIPService = voIPService;
    }

    /**
     * Called by test code
     */
    public void setMvsService(MVSService mvsService) {
        this.mvsService = mvsService;
    }

    public static class EndgeraetPortComparator implements Comparator<EndgeraetPort>, Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        @SuppressWarnings("null")
        @SuppressFBWarnings(value = "NP", justification = "falscher Alarm, o1, o2 werden auf null gecheckt bevor sie dereferenziert werden")
        public int compare(EndgeraetPort o1, EndgeraetPort o2) {
            if ((o1 == null) && (o2 == null)) {
                return 0;
            }
            if ((o1 == null) && (o2 != null)) {
                return 1;
            }
            if ((o1 != null) && (o2 == null)) {
                return -1;
            }

            if ((o1.getNumber() == null) && (o2.getNumber() == null)) {
                return 0;
            }
            if ((o1.getNumber() == null) && (o2.getNumber() != null)) {
                return 1;
            }
            if ((o1.getNumber() != null) && (o2.getNumber() == null)) {
                return -1;
            }
            return NumberUtils.compare(o1.getNumber(), o2.getNumber());
        }
    }

    private static String getLineBreak() {
        return String.format("===============================================================%n");

    }

}
