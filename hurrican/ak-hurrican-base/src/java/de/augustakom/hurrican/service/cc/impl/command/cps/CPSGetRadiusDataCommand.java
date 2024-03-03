/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:57:21
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv4Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusIPv6Data;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusRoutesData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.IpRouteService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Command-Klasse, um Radius-Daten fuer einen Auftrag zu ermitteln. <br> Die ermittelten Daten werden von dem Command in
 * einem XML-Element in der vom CPS erwarteten Struktur aufbereitet.
 *
 *
 */
public class CPSGetRadiusDataCommand extends AbstractCPSDataCommand {

    public static final String IP_V4 = "IPv4";
    public static final String IP_V6_DS_LITE = "IPv6_DSlite";
    public static final String IP_V6_DS = "IPv6_DS";

    private static final Logger LOGGER = Logger.getLogger(CPSGetRadiusDataCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.VPNService")
    private VPNService vpnService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AccountService")
    private AccountService accountService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService crossConnectionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.IpRouteService")
    private IpRouteService ipRouteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;
    // TODO Annotiere den Service anstatt ihn über #init zu laden!
    private LeistungService billingLeistungsService;
    @Autowired
    private CpsFacade cpsFacade;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProfileService")
    private ProfileService profileService;

    // ID des relevanten techn. Auftrags fuer die Radius-Daten
    // (z.B. bei TK-Anlagen nicht unbedingt CPSTransaction.auftragId)
    private Long techOrderId = null;

    @Override
    public void init() throws ServiceNotFoundException {
        setBillingLeistungsService(getBillingService(LeistungService.class));
    }

    /**
     * Prueft, ob der RadiusAccount wirklich notwendig ist. <br/> Dies ist eigentlich immer der Fall; mit lediglich
     * folgender Ausnahme: bei Produkt "FTTX Telefon" nur dann, wenn auch eine CPE-Leistung gebucht ist.
     *
     * @param auftragId
     * @param produkt
     * @return
     * @throws Exception
     */
    @SuppressWarnings("SimplifiableIfStatement")
    protected final boolean isRadiusAccountNecessary(long auftragId, Produkt produkt) throws Exception {
        if (Produkt.PROD_ID_FTTX_TELEFONIE.equals(produkt.getProdId())) {
            return hasAuftragTypEndgeraetLeistung(auftragId);
        }
        return true;
    }

    /**
     * @throws ServiceCommandException
     */
    void loadTechnicalOrderId() throws ServiceCommandException {
        techOrderId = findTechOrderId4XDSL(isNecessary());
        if (techOrderId == null) {
            // falls kein xDSL Auftrag geladen --> Auftrag aus CPS-Tx verwenden
            techOrderId = getCPSTransaction().getAuftragId();
        }
    }

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            loadTechnicalOrderId();

            final Produkt produkt = getProdukt4Auftrag(techOrderId);
            if (isRadiusAccountNecessary(techOrderId, produkt)) {

                final List<IntAccount> accounts = getAccounts();
                if (CollectionTools.isEmpty(accounts)) {
                    // IF Statements NICHT zusammenlegen! Sonst funktionieren reine TK-Anlagen nicht mehr!
                    if (needsAccount(produkt)) {
                        throw new HurricanServiceCommandException("No accounts for order found!");
                    }
                }
                else {
                    Date execDate = getCPSTransaction().getEstimatedExecTime();
                    CPSRadiusAccountData radiusAcc = new CPSRadiusAccountData();

                    IntAccount account = accounts.get(0); // nur ersten Account verwenden!
                    defineAccountData(radiusAcc, account);
                    defineRealm(radiusAcc, produkt);

                    if (isDSLProduct(produkt)) {
                        // DSL-Optionen laden
                        loadDSLOptions(radiusAcc, execDate, produkt);

                        // Port-ID setzen
                        definePortSecurity(radiusAcc);
                    }

                    // Account-Typ setzen
                    defineAccountType(radiusAcc, produkt);

                    // Tarif-Typ setzen
                    loadTarif(radiusAcc, execDate, account);

                    // Fixed-IPs laden
                    loadIP(radiusAcc, produkt, execDate);

                    // VPN-Verbindung laden
                    loadVPN(radiusAcc);

                    loadIpMode(execDate, radiusAcc);

                    loadQosProfile(execDate, radiusAcc);

                    // Radius-Daten an ServiceOrder-Data uebergeben
                    CPSRadiusData radius = new CPSRadiusData();
                    radius.setRadiusAccount(radiusAcc);
                    getServiceOrderData().setRadius(radius);
                }
            }
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading Radius-Data: " + e.getMessage(), this.getClass());
        }
    }

    protected final void loadQosProfile(final Date execDate, final CPSRadiusAccountData radiusAcc) throws FindException {
        final TechLeistung qosProfileTechLs =
                leistungsService.findTechLeistung4Auftrag(getAuftragDaten().getAuftragId(),
                        TechLeistung.TYP_SIPTRUNK_QOS_PROFILE, execDate);
        if (qosProfileTechLs != null) {
            radiusAcc.setQosProfile(qosProfileTechLs.getStrValue());
        }
    }

    private void loadIpMode(Date execDate, CPSRadiusAccountData radiusAcc) {
        IpMode ipMode = leistungsService.queryIPMode(techOrderId, DateConverterUtils.asLocalDate(execDate));
        switch (ipMode) {
            case IPV4:
                radiusAcc.setIpMode(IP_V4);
                break;
            case DUAL_STACK:
                radiusAcc.setIpMode(IP_V6_DS);
                break;
            case DS_LITE:
                radiusAcc.setIpMode(IP_V6_DS_LITE);
                break;
            default:
                LOGGER.warn("Unknown ipMode=" + ipMode);
                break;
        }
    }

    /**
     * Ermittelt die aktiven Accounts.
     */
    List<IntAccount> getAccounts() throws FindException {
        List<IntAccount> accounts = accountService.findIntAccounts4Auftrag(techOrderId);
        if (CollectionTools.isNotEmpty(accounts) && (accounts.size() > 1)) {
            List<IntAccount> saved = new ArrayList<>(accounts);
            filterInactiveAccounts(accounts);

            // falls nach Filterung kein Account mehr vorhanden --> urspruengliche Accounts zurueck geben
            if (CollectionTools.isEmpty(accounts)) {
                return saved;
            }
        }
        return accounts;
    }

    /**
     * Uebergibt dem Daten-Objekt die Account-Daten
     */
    void defineAccountData(CPSRadiusAccountData radiusAcc, IntAccount account) throws ServiceCommandException {
        if ((account == null) || (radiusAcc == null)) {
            throw new HurricanServiceCommandException("Account and / or Radius Data not defined!");
        }

        radiusAcc.setUserName(account.getAccount());
        radiusAcc.setPassword(account.getPasswort());
    }


    /**
     * Ermittelt den REALM der Produktgruppe und setzt diesen in das {@link CPSRadiusAccountData} Objekt.
     */
    void defineRealm(CPSRadiusAccountData radiusAccountData, Produkt produkt) throws ServiceCommandException {
        if (produkt != null && radiusAccountData != null) {
            try {
                ProduktGruppe produktGruppe = produktService.findProduktGruppe(produkt.getProduktGruppeId());
                radiusAccountData.setRealm(StringUtils.trimToNull(produktGruppe.getRealm()));
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new HurricanServiceCommandException(String.format("Error loading the account realm: %s",
                        e.getMessage()), e);
            }
        }
    }


    /**
     * Filtert aus der angegebenen Account-Liste die inaktiven (gesperrten) Accounts heraus.
     *
     * @param accounts
     */
    private void filterInactiveAccounts(List<IntAccount> accounts) {
        CollectionUtils.filter(accounts, obj -> {
            IntAccount account = (IntAccount) obj;
            return !BooleanTools.nullToFalse(account.getGesperrt());
        });
    }

    /**
     * Ueberprueft, ob fuer das aktuelle Produkt ein Account unbedingt notwendig ist.
     *
     * @return
     * @throws HurricanServiceCommandException
     */
    boolean needsAccount(Produkt produkt) throws HurricanServiceCommandException {
        try {
            return IntAccount.isEinwahlaccount(produkt.getLiNr());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error loading product definition to validate account options: " + e.getMessage(), e);
        }
    }

    /**
     * Laedt die DSL-Optionen des Auftrags. <br> Darunter fallen die Downstream-Raten des Produkts bzw. des
     * DSLAM-Profils sowie Online-Optionen (z.B. Always-On).
     *
     * @param radiusAcc
     * @param execDate
     * @param produkt
     * @throws HurricanServiceCommandException
     */
    void loadDSLOptions(CPSRadiusAccountData radiusAcc, Date execDate, Produkt produkt)
            throws HurricanServiceCommandException {
        try {
            // Always-On pruefen / setzen
            boolean alwaysOn = ccLeistungsService.hasTechLeistungWithExtLeistungNo(
                    ExterneLeistung.ALWAYS_ON.leistungNo, techOrderId, true);
            if (!alwaysOn) {
                alwaysOn = ccLeistungsService.hasTechLeistungEndsInFutureWithExtLeistungNo(
                        ExterneLeistung.ALWAYS_ON.leistungNo, techOrderId);
            }
            if (!alwaysOn) {
                // bei fixer IP-Adresse ist Always-on inklusive!
                alwaysOn = ccLeistungsService.hasTechLeistungWithExtLeistungNo(
                        ExterneLeistung.FIXED_IP_WITH_ALWAYS_ON.leistungNo, techOrderId, true);
            }
            if (!alwaysOn) {
                // bei fixer IP-Adresse ist Always-on inklusive!
                alwaysOn = ccLeistungsService.hasTechLeistungEndsInFutureWithExtLeistungNo(
                        ExterneLeistung.FIXED_IP_WITH_ALWAYS_ON.leistungNo, techOrderId);
            }
            radiusAcc.setAlwaysOn(BooleanTools.getBooleanAsString(alwaysOn));

            final Endstelle endstelleB = cpsFacade.getEndstelleBWithStandortId(techOrderId);
            final Equipment oltChildPort = cpsFacade.findFttxPort(endstelleB);
            final HWBaugruppe hwBaugruppe = cpsFacade.findBaugruppeByPort(oltChildPort);
            boolean isProfileDSLAM = !hwBaugruppe.getHwBaugruppenTyp().isProfileAssignable();
            if (isProfileDSLAM) {
                // Downstream aus DSLAM-Profil ermitteln
                DSLAMProfile profile = dslamService.findDSLAMProfile4AuftragNoEx(techOrderId, execDate, true);
                if ((profile == null) && needsDSLAMProfile(produkt)) {
                    throw new HurricanServiceCommandException(String.format("DSLAM-Profile for order '%s' not found!", techOrderId));
                }
                radiusAcc.setProfileDataRateDown((profile != null) ? profile.getBandwidth().getDownstreamAsString()
                        : HurricanConstants.UNKNOWN);
            } else {
                // kein DataRateDown-Wert
            }
            // Downstream aus zugeordneter Leistung ermitteln
            boolean downstreamLoaded = loadDownstreamTechLs(radiusAcc, execDate);
            if (!downstreamLoaded) {
                throw new HurricanServiceCommandException("RADIUS parameter not found: ProductDataRateDown");
            }
        }
        catch (HurricanServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading DSL options: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die zum Ausfuehrungszeitpunkt gueltige Downstream-Bandbreite und traegt den Wert in den Radius-Account
     * ein.
     *
     * @param radiusAcc
     * @param execDate
     * @return true wenn eine Downstream-Leistung gefunden wurde; sonst false.
     * @throws FindException
     */
    protected boolean loadDownstreamTechLs(CPSRadiusAccountData radiusAcc, Date execDate) throws FindException {
        TechLeistung downstreamTLS = ccLeistungsService.findTechLeistung4Auftrag(
                techOrderId, TechLeistung.TYP_DOWNSTREAM, execDate);
        if (downstreamTLS != null) {
            radiusAcc.setProductDataRateDown("" + downstreamTLS.getLongValue());
            return true;
        }

        if (StringUtils.isBlank(radiusAcc.getProductDataRateDown())
                && StringUtils.isNotBlank(radiusAcc.getProfileDataRateDown())) {
            radiusAcc.setProductDataRateDown(radiusAcc.getProfileDataRateDown());
            return true;
        }

        return false;
    }

    /**
     * Ermittelt aus dem Produkt den Account-Typ und uebergibt diesen dem Radius-Account.
     *
     * @param radiusAcc
     * @param produkt
     */
    void defineAccountType(CPSRadiusAccountData radiusAcc, Produkt produkt) {
        if ((radiusAcc != null) && (produkt != null)) {
            radiusAcc.setAccountType(produkt.getCpsAccountType());
        }
    }

    /**
     * Prueft, ob fuer das Produkt DSLAM-Profile konfiguriert sind.
     *
     * @param produkt
     * @return 'true', wenn dem Produkt DSLAM-Profile zugeordnet sind
     * @throws HurricanServiceCommandException
     */
    private boolean needsDSLAMProfile(Produkt produkt) throws HurricanServiceCommandException {
        try {
            List<DSLAMProfile> profiles = dslamService.findDSLAMProfiles4Produkt(produkt.getProdId());
            return !profiles.isEmpty();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error validating DSLAM profile for product: " + e.getMessage(), e);
        }
    }

    /**
     * Defines if evn flag is mapped to {@link CPSRadiusAccountData#TARIF_E_FLAT} evn flat tarif
     * At first is evaluated pending env status, next current evn status
     *
     * @param intAccount
     * @return is evn flat tarif
     */
    private boolean isEvnFlatTarif(IntAccount intAccount) {
        if( intAccount.getEvnStatusPending() != null ) {
            return intAccount.getEvnStatusPending();
        } else if (intAccount.getEvnStatus() != null ) {
            return intAccount.getEvnStatus();
        }
        return false;
    }

    /**
     * Ermittelt den Tarif-Typ und uebertraegt diesen in das Objekt <code>radiusAcc</code>.
     *
     * @param radiusAcc
     * @param execDate
     * @throws HurricanServiceCommandException
     */
    private void loadTarif(CPSRadiusAccountData radiusAcc, Date execDate, IntAccount intAccount) throws HurricanServiceCommandException {

        try {
            int tarifType = billingLeistungsService.getUDRTarifType4Auftrag(
                    getCPSTransaction().getOrderNoOrig(), execDate);

            String tarif;

            final boolean evnFlatTarif = isEvnFlatTarif(intAccount);
            if (evnFlatTarif) {
                tarif = CPSRadiusAccountData.TARIF_E_FLAT;
            } else {
                switch (tarifType) {
                    case Leistung.LEISTUNG_VOL_TYPE_FLAT:
                        tarif = CPSRadiusAccountData.TARIF_FLAT;
                        break;
                    case Leistung.LEISTUNG_VOL_TYPE_VOLUME:
                        tarif = CPSRadiusAccountData.TARIF_VOLUME;
                        break;
                    case Leistung.LEISTUNG_VOL_TYPE_TIME:
                        tarif = CPSRadiusAccountData.TARIF_TIME;
                        break;
                    default:
                        tarif = CPSRadiusAccountData.TARIF_FLAT;
                        break;
                }
            }

            radiusAcc.setTarif(tarif);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error while loading Tarif: " + e.getMessage(), e);
        }

    }

    /**
     * Prueft ob die Provisionierung eventuell vorhandener IP Adressen zum Auftrag durchgefuehrt werden darf. Eine der
     * folgenden Regeln muss erfuellt sein: <ul> <li>wenn VPN dem Auftrag zugeordnet <li>wenn Flag im Produkt
     * (T_PRODUKT.CPS_IP_DEFAULT) gesetzt <li>wenn mindestens eine techn. Leistung vom Typ ONL_IP_OPTION dem Auftrag
     * zugeordnet und zum Provisionierungszeitpunkt aktiv ist. <br> Die betroffenen techn. Leistungen: <ul> <li>feste
     * IP-Adresse <li>zusätzliche IP-Adresse <li>feste IP-Adresse incl. Always-on </ul> </ul>
     *
     * @return true, wenn Provisionierung erlaubt, false wenn nicht erlaubt
     */
    private boolean evaluateLoadIPExecState(Produkt produkt, Date execDate) throws FindException {
        // Ist VPN dem Auftrag zugeordnet
        if (vpnService.findVPNByAuftragId(techOrderId) != null) {
            return true;
        }

        // IP Flag in Produkt gesetzt
        if ((produkt != null) && BooleanTools.nullToFalse(produkt.getCpsIPDefault())) {
            return true;
        }

        // Ist min. eine techn. Leistungen vom Typ ONL_IP_DEFAULT dem Auftrag
        // zum Provisionierungszeitpunkt zugeordnet
        List<TechLeistung> onlIPDefault = ccLeistungsService.findTechLeistungen4Auftrag(techOrderId,
                TechLeistung.TYP_ONL_IP_DEFAULT, execDate);
        if (CollectionTools.isNotEmpty(onlIPDefault)) {
            return true;
        }

        // Ist min. eine techn. Leistungen vom Typ ONL_IP_OPTION dem Auftrag zum Provisionierungszeitpunkt zugeordnet
        List<TechLeistung> leistungen = ccLeistungsService.findTechLeistungen4Auftrag(techOrderId,
                TechLeistung.TYP_ONLINE_IP, execDate);
        return CollectionTools.isNotEmpty(leistungen);
    }

    IPAddress getFirstFixedIpV6Address() throws FindException {
        return getIpAddressService().findDHCPv6PDPrefix4TechnicalOrder(getTechOrderId());
    }

    IPAddress getFirstFixedIpV4Address() throws FindException {
        List<IPAddress> fixedIPs = getFixedIpAddresses();
        IPAddress result = null;
        for (IPAddress ipAddress : fixedIPs) {
            if (ipAddress.isIPV4()) {
                result = ipAddress;
                break;
            }
        }
        return result;
    }

    /**
     * @return
     * @throws FindException
     */
    private List<IPAddress> getFixedIpAddresses() throws FindException {
        return getIpAddressService().findFixedIPs4TechnicalOrder(getTechOrderId());
    }

    /**
     * ermittelt die IP-Adresse, wenn es sich um eine IPv6 mit Praefix handelt, berechnet die Methode aus Praefix und
     * relativer Adresse die absolute Addresse.
     */
    private String getAbsoluteAddress(IPAddress ipAddress) throws HurricanServiceCommandException {
        String result = ipAddress.getAbsoluteAddress();
        if (result == null) {
            throw new HurricanServiceCommandException(String.format("Die absolute Adresse zur ID %s und "
                    + "Adresse %s ist nicht ermittelbar!", ipAddress.getId(), ipAddress.getAddress()));
        }
        return result;
    }

    /**
     * Prueft ob eventuell vorhandene (fixe) IP Adressen geladen werden duerfen. Wenn der Auftrag berechtigt ist
     * Adressen zu laden, werden diese ins CPSRadiusAccountData-Objekt uebertragen.
     *
     * @param radiusAcc Objekt, in das die IP-Daten eingetragen werden
     * @throws HurricanServiceCommandException
     */
    void loadIP(CPSRadiusAccountData radiusAcc, Produkt produkt, Date execDate) throws HurricanServiceCommandException {
        try {
            if (!evaluateLoadIPExecState(produkt, execDate)) {
                return;
            }

            final IPAddress fixedIpV4Address = getFirstFixedIpV4Address();
            final IPAddress fixedIpV6Address = getFirstFixedIpV6Address();

            List<IPAddress> nets;
            List<IpRoute> routes;
            if ((fixedIpV4Address != null) || (fixedIpV6Address != null)) {
                nets = getIpAddressService().findNets4TechnicalOrder(getTechOrderId());
                routes = ipRouteService.findIpRoutesByOrder(getTechOrderId());

                CPSRadiusIPv4Data ipv4 = new CPSRadiusIPv4Data();
                if (fixedIpV4Address != null) {
                    ipv4.setFixedIPv4(getAbsoluteAddress(fixedIpV4Address));
                }

                CPSRadiusIPv6Data ipv6 = new CPSRadiusIPv6Data();
                if (fixedIpV6Address != null) {
                    filterNets(fixedIpV6Address, nets);
                    ipv6.setFixedIPv6(fixedIpV6Address.getAddress());
                }
                distributeNets(nets, ipv4, ipv6);
                distributeRoutes(routes, ipv4, ipv6);

                radiusAcc.setIpv4(ipv4);
                radiusAcc.setIpv6(ipv6);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "IP Adressen Ladefehler: " + ExceptionUtils.getFullStackTrace(e), e);
        }
    }

    private void filterNets(final IPAddress fixedIpV6Address, List<IPAddress> nets) {
        if (fixedIpV6Address != null) {
            CollectionUtils.filter(nets, obj -> {
                IPAddress net = (IPAddress) obj;
                return !NumberTools.equal(net.getNetId(), fixedIpV6Address.getNetId());
            });
        }
    }

    private void distributeNets(List<IPAddress> nets, CPSRadiusIPv4Data ipV4Data, CPSRadiusIPv6Data ipV6Data)
            throws HurricanServiceCommandException {
        if (CollectionTools.isNotEmpty(nets)) {
            for (IPAddress net : nets) {
                if (net.isIPV4()) {
                    int prefixLength = IPToolsV4.instance().getPrefixLength4Address(net.getAddress());
                    if (prefixLength == -1) {
                        throw new HurricanServiceCommandException(String.format(
                                "IP V4 Präfix Länge kann für ID %s und Adresse %s "
                                        + "nicht ermittelt werden!", net.getId(), net.getAddress()
                        ));
                    }
                    CPSRadiusRoutesData radiusRoute = CPSRadiusRoutesData.createIPv4(getAbsoluteAddress(net));
                    ipV4Data.addCPSRadiusRoute(radiusRoute);
                }
                else if (net.isIPV6()) {
                    int prefixLength = IPToolsV6.instance().getPrefixLength4Address(net.getAddress());
                    if (prefixLength == -1) {
                        throw new HurricanServiceCommandException(String.format(
                                "IP V6 Präfix Länge kann für ID %s und Adresse %s "
                                        + "nicht ermittelt werden!", net.getId(), net.getAddress()
                        ));
                    }
                    CPSRadiusRoutesData radiusRoute = CPSRadiusRoutesData.createIPv6(getAbsoluteAddress(net));
                    ipV6Data.addCPSRadiusRoute(radiusRoute);
                }
            }
        }
    }

    private void distributeRoutes(List<IpRoute> routes, CPSRadiusIPv4Data ipV4Data, CPSRadiusIPv6Data ipV6Data)
            throws HurricanServiceCommandException {
        if (CollectionTools.isNotEmpty(routes)) {
            for (IpRoute route : routes) {
                IPAddress routeAddress = route.getIpAddressRef();
                if ((routeAddress != null) && routeAddress.isIPV4()) {
                    CPSRadiusRoutesData radiusRoute = CPSRadiusRoutesData.createIPv4(getAbsoluteAddress(routeAddress))
                            .withMetric(route.getMetrik());
                    ipV4Data.addCPSRadiusRoute(radiusRoute);
                }
                else if ((routeAddress != null) && routeAddress.isIPV6()) {
                    CPSRadiusRoutesData radiusRoute = CPSRadiusRoutesData.createIPv6(getAbsoluteAddress(routeAddress))
                            .withMetric(route.getMetrik());
                    ipV6Data.addCPSRadiusRoute(radiusRoute);
                }
            }
        }
    }

    /**
     * Ermittelt die VPN Nr eines Auftrags und uebergibt diese dem Radius-Account.
     */
    void loadVPN(CPSRadiusAccountData radiusAcc) throws ServiceCommandException {
        try {
            AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragIdTx(techOrderId);
            if ((auftragTechnik != null) && (auftragTechnik.getVpnId() != null)) {
                VPN vpn = vpnService.findVPN(auftragTechnik.getVpnId());
                if (vpn != null) {
                    radiusAcc.setVpnId(String.format("%d", vpn.getVpnNr()));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error while loading VPN informations: " + ExceptionUtils.getFullStackTrace(e), e);
        }
    }

    /**
     * Ermittelt die HSI-CrossConnection und definiert aus den BRAS-Daten die Port ID. (Wird fuer die Port-Security
     * verwendet.)
     */
    void definePortSecurity(CPSRadiusAccountData radiusAcc) throws HurricanServiceCommandException {
        try {
            Endstelle esB = endstellenService.findEndstelle4Auftrag(techOrderId, Endstelle.ENDSTELLEN_TYP_B);
            if ((esB == null) || (esB.getRangierId() == null)) {
                throw new HurricanServiceCommandException("Endstelle B nicht gefunden oder besitzt keine Rangierung!");
            }

            Rangierung dslRang = rangierungsService.findRangierung(esB.getRangierId());
            if ((dslRang == null) || (dslRang.getEqInId() == null)) {
                throw new HurricanServiceCommandException(
                        "Rangierung not found or DSL-Equipment not defined!");
            }

            // DTAG u. DSLAM-Port ermitteln
            Equipment dslEQ = rangierungsService.findEquipment(dslRang.getEqInId());
            if (dslEQ == null) {
                throw new HurricanServiceCommandException("DSLAM Equipment not found for order!");
            }

            List<EQCrossConnection> crossConnectionsOnPort =
                    crossConnectionService.findEQCrossConnections(dslEQ.getId(), getCPSTransaction()
                            .getEstimatedExecTime());
            if (CollectionTools.isNotEmpty(crossConnectionsOnPort)) {
                for (EQCrossConnection eqCrossConnection : crossConnectionsOnPort) {
                    if (eqCrossConnection.isHsi()
                            || (eqCrossConnection.isQscHsi() && (eqCrossConnection.getBrasInner() != null))) {
                        StringBuilder portSecurityId = new StringBuilder();
                        portSecurityId.append(String.format("%d", eqCrossConnection.getBrasOuter()));
                        portSecurityId.append("_");
                        portSecurityId.append(String.format("%d", eqCrossConnection.getBrasInner()));

                        radiusAcc.setPortId(portSecurityId.toString());
                        break;
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error loading the CrossConnections of the DSLAM port to define the Radius Port ID: "
                            + e.getMessage()
            );
        }
    }

    /**
     * @return Returns the ipAddressService.
     */
    private IPAddressService getIpAddressService() {
        return ipAddressService;
    }

    /**
     * @param ipAddressService The ipAddressService to set.
     */
    void setIpAddressService(IPAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

    /**
     * Injected
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Injected
     */
    public void setVpnService(VPNService vpnService) {
        this.vpnService = vpnService;
    }

    /**
     * Injected
     */
    public void setCrossConnectionService(EQCrossConnectionService crossConnectionService) {
        this.crossConnectionService = crossConnectionService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * injected by Spring
     */
    public void setIpRouteService(IpRouteService ipRouteService) {
        this.ipRouteService = ipRouteService;
    }

    /**
     * @param billingLeistungsService The billingLeistungsService to set.
     */
    public void setBillingLeistungsService(LeistungService billingLeistungsService) {
        this.billingLeistungsService = billingLeistungsService;
    }

    Long getTechOrderId() {
        return techOrderId;
    }

    /**
     * @param techOrderId The techOrderId to set.
     */
    void setTechOrderId(Long techOrderId) {
        this.techOrderId = techOrderId;
    }

}
