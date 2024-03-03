/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 15:05:42
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.Map.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.IPAddressDAO;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPPoolType;
import de.augustakom.hurrican.model.cc.IPPurpose;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchQuery;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressAssignmentRemoteService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.cc.impl.ipaddress.ReleaseDateCalculator;
import de.augustakom.hurrican.service.internet.INetNumService;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6Response;
import de.mnet.monline.ipProviderService.ipp.ProductGroup;
import de.mnet.monline.ipProviderService.ipp.Purpose;
import de.mnet.monline.ipProviderService.ipp.Site;

/**
 * Implementierung von {@link IPAddressService}.
 */
public class IPAddressServiceImpl extends DefaultCCService implements IPAddressService {

    private static final Logger LOGGER = Logger.getLogger(IPAddressServiceImpl.class);

    static final String IPADDRESSPANELVIEW_STATE_ACTIVE = "aktiv";
    static final String IPADDRESSPANELVIEW_STATE_KARENZZEIT = "Karenzzeit läuft";
    static final String IPADDRESSPANELVIEW_STATE_FREIGEGEBEN = "Rückgabe an Pool ist erfolgt";
    static final String IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_OTHER = "auf anderem Auftrag";
    static final String IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_SAME = "auf gleichem Auftrag";

    @Resource(name = "ipAddressDAO")
    private IPAddressDAO ipAddrDao;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.VPNService")
    private VPNService vpnService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressAssignmentRemoteService")
    private IPAddressAssignmentRemoteService ipAddressAssignmentService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Autowired
    private ReleaseDateCalculator releaseDateCalculator;
    @Resource(name = "cc.hibernateTxManager")
    private PlatformTransactionManager tm;

    /**
     * @return Returns the releaseDateCalculator.
     */
    protected ReleaseDateCalculator getReleaseDateCalculator() {
        return releaseDateCalculator;
    }

    @Override
    @CcTxRequiredReadOnly
    public IPAddress findIpAddress(Long ipAddressId) throws FindException {
        if (ipAddressId == null) { return null; }
        try {
            return ipAddrDao.findById(ipAddressId, IPAddress.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.IPAddressService#findV6PrefixesByBillingOrderNumber(java.lang.Long)
     */
    @Nonnull
    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findV6PrefixesByBillingOrderNumber(Long billingOrderNumber) throws FindException {
        if (billingOrderNumber == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        return ipAddrDao.findV6PrefixesByBillingOrderNumber(billingOrderNumber);
    }

    @Override
    @Nonnull
    @CcTxRequiredReadOnly
    public List<IPAddress> findV4AddressByBillingOrderNumber(@Nonnull final Long billingOrderNumber)
            throws FindException {
        return ipAddrDao.findV4AddressesByBillingOrderNumber(billingOrderNumber);
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findAssignedIPs4BillingOrder(Long billingOrderNo) throws FindException {
        if (billingOrderNo == null) { return Collections.emptyList(); }
        return ipAddrDao.findAssignedIPs4BillingOrder(billingOrderNo);
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findAssignedIPsOnly4BillingOrder(Long billingOrderNo) throws FindException {
        if (billingOrderNo == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        return ipAddrDao.findAssignedIPsOnly4BillingOrder(billingOrderNo);
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findAssignedNetsOnly4BillingOrder(Long billingOrderNo) throws FindException {
        if (billingOrderNo == null) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        return ipAddrDao.findAssignedNetsOnly4BillingOrder(billingOrderNo);
    }

    @Override
    @CcTxRequired
    public IPAddress saveIPAddress(IPAddress toSave, Long sessionId) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            String userW = getUserW(sessionId); // optionaler Wert
            toSave.setUserW(userW);
            HistoryHelper.checkHistoryDates(toSave);

            ipAddrDao.store(toSave);
            return toSave;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * validiert ob einer der Parameter {@code null} ist
     */
    private void validateParamsAssignIpNotNull(Long auftragId, Reference purpose, Integer netmaskSize,
            Reference site, Long sessionId) throws StoreException {
        if ((auftragId == null) || (purpose == null) || (netmaskSize == null) || (site == null)
                || (site.getIntValue() == null)) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
    }

    /**
     * ermittelt alle verfuegbaren IPv4/v6 - Verwendungszwecke
     */
    private List<Reference> findIPPurposes(boolean isIpV4) throws FindException {
        return referenceService.findReferencesByType((isIpV4) ? Reference.REF_TYPE_IP_PURPOSE_TYPE_V4
                : Reference.REF_TYPE_IP_PURPOSE_TYPE_V6, true);
    }

    /**
     * validiert alle fuer die Zuweisung einer V4/V6 IP Adresse notwendigen Parameter
     */
    private void validateParamsAssignIp(Long auftragId, Reference purpose, Integer netmaskSize, Reference site,
            Long sessionId, boolean isIpV4) throws StoreException {
        validateParamsAssignIpNotNull(auftragId, purpose, netmaskSize, site, sessionId);
        if (isIpV4 && (purpose.getIntValue() == null)) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        List<Reference> purposes = Collections.emptyList();
        try {
            purposes = findIPPurposes(isIpV4);
        }
        catch (FindException fe) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, fe);
        }

        if (CollectionUtils.isEmpty(purposes) || !purposes.contains(purpose)) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
    }

    @Override
    @Nonnull
    @CcTxRequired
    public IPAddress assignIPV4(Long auftragId, Reference purpose, Integer netmaskSize, Reference site,
            Long sessionId) throws StoreException {
        validateParamsAssignIp(auftragId, purpose, netmaskSize, site, sessionId, true);

        try {
            ProductGroup.Enum enumProductGroup = getIPPoolType(auftragId);
            Purpose.Enum enumPurpose = getPurpose(purpose);
            Site.Enum enumSite = getSite(site);
            String userW = getUserW(sessionId); // optionaler Wert
            Long vpnId = getVpnId(auftragId); // optionaler Wert

            AssignIPV4Response response = ipAddressAssignmentService.assignIPv4(enumProductGroup, vpnId, enumPurpose,
                    netmaskSize, enumSite, userW, auftragId);

            final AddressTypeEnum ipType = determineIpTypeV4(response.getNetmaskSize(), response.getNetAddress());

            Long billingOrderNo = getBillingOrderNo(auftragId);
            IPAddress ipAddress = new IPAddress();
            ipAddress.setPurpose(purpose);
            ipAddress.setAddress(response.getNetAddress());
            ipAddress.setNetId(response.getNetId());
            ipAddress.setIpType(ipType);
            ipAddress.setBillingOrderNo(billingOrderNo);
            return ipAddress;
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private AddressTypeEnum determineIpTypeV4(int netmaskSize, String netAddress) {
        AddressTypeEnum adressTypeToReturn = null;
        if ((netmaskSize > 0) && (netmaskSize < IPToolsV4.instance().getMaximumBits())) {
            adressTypeToReturn = AddressTypeEnum.IPV4_prefix;
        }
        else if (netAddress.contains(String.valueOf(IPToolsV4.IPV4_PREFIX_LENGTH_DELIMITER))) {
            adressTypeToReturn = AddressTypeEnum.IPV4_with_prefixlength;
        }
        else {
            adressTypeToReturn = AddressTypeEnum.IPV4;
        }
        return adressTypeToReturn;
    }

    @Override
    @Nonnull
    @CcTxRequired
    public IPAddress assignIPV6(Long auftragId, Reference purpose, Integer netmaskSize, Reference site,
            Long sessionId) throws StoreException {
        validateParamsAssignIp(auftragId, purpose, netmaskSize, site, sessionId, false);

        try {
            ProductGroup.Enum enumProductGroup = getIPPoolType(auftragId);
            Site.Enum enumSite = getSite(site);
            String userW = getUserW(sessionId); // optionaler Wert
            Long vpnId = getVpnId(auftragId); // optionaler Wert

            AssignIPV6Response response = ipAddressAssignmentService.assignIPv6(enumProductGroup, vpnId, netmaskSize,
                    enumSite, userW, auftragId);

            Long billingOrderNo = getBillingOrderNo(auftragId);
            IPAddress ipAddress = new IPAddress();
            ipAddress.setPurpose(purpose);
            ipAddress.setAddress(response.getNetAddress());
            ipAddress.setNetId(response.getNetId());
            ipAddress.setIpType(((response.getNetmaskSize() > 0) && (response.getNetmaskSize() < IPToolsV6.instance()
                    .getMaximumBits())) ? AddressTypeEnum.IPV6_prefix : AddressTypeEnum.IPV6_full);
            ipAddress.setBillingOrderNo(billingOrderNo);
            return ipAddress;
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private Long getBillingOrderNo(Long auftragId) throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten(auftragId);
        if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
            throw new FindException(
                    String.format("Die Billing Auftragsnummer zu dem Auftrag %s konnte " + "nicht ermittelt werden!",
                            auftragId)
            );
        }
        return auftragDaten.getAuftragNoOrig();
    }

    private ProductGroup.Enum getIPPoolType(Long auftragId) throws FindException {
        Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
        if ((produkt == null) || (produkt.getIpPool() == null)) {
            throw new FindException(String.format(
                    "Der IP-Pool zu dem Auftrag %s konnte " + "nicht ermittelt werden!", auftragId));
        }
        String name = getIpPoolName4Id(produkt.getIpPool());
        ProductGroup.Enum result = ProductGroup.Enum.forString(name);
        if (result == null) {
            throw new FindException(String.format("Der IP-Pool Enum %s zu dem Auftrag %s konnte "
                    + "nicht ermittelt werden!", name, auftragId));
        }
        return result;
    }

    private String getIpPoolName4Id(Long ipPoolId) {
        String name = null;
        IPPoolType[] types = IPPoolType.values();
        if ((types != null) && (types.length > 0)) {
            for (IPPoolType type : types) {
                if (NumberTools.equal(ipPoolId, type.getId())) {
                    name = type.name();
                    break;
                }
            }
        }
        return name;
    }

    private Purpose.Enum getPurpose(Reference purpose) throws FindException {
        Purpose.Enum enumPurpose = Purpose.Enum.forString(purpose.getStrValue());
        if (enumPurpose == null) {
            throw new FindException(String.format(
                    "Fehler bei der Zuordnung des Verwendungszwecks %s !", purpose.getStrValue()));
        }
        return enumPurpose;
    }

    private Site.Enum getSite(Reference site) throws FindException {
        Site.Enum enumSite = Site.Enum.forString(site.getStrValue());
        if (enumSite == null) {
            throw new FindException(String.format(
                    "Fehler bei der Zuordnung des Standortes %s !", site.getStrValue()));
        }
        return enumSite;
    }

    private String getUserW(Long sessionId) {
        String userW = null;
        if (sessionId != null) {
            userW = getLoginNameSilent(sessionId);
        }
        return userW;
    }

    private Long getVpnId(Long auftragId) throws FindException {
        VPN vpn = vpnService.findVPNByAuftragId(auftragId);
        return (vpn != null) ? vpn.getVpnNr() : null;
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findAssignedIPs4NetId(Long netId, Date dateActive) throws FindException {
        if ((netId == null) || (dateActive == null)) { throw new FindException(FindException.EMPTY_FIND_PARAMETER); }
        return ipAddrDao.findAssignedIPs4NetId(netId, dateActive);
    }

    /**
     * Setzt den {@link IPAddressAssignmentRemoteService} fuer den Test.
     */
    void setIpAddressAssignmentService(IPAddressAssignmentRemoteService service) {
        this.ipAddressAssignmentService = service;
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findAssignedIPs4TechnicalOrder(Long technicalOrderNo) throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten(technicalOrderNo);
        if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) { return Collections.emptyList(); }
        return ipAddrDao.findAssignedIPs4BillingOrder(auftragDaten.getAuftragNoOrig());
    }

    private AuftragDaten getAuftragDaten(Long technicalOrderNo) throws FindException {
        return auftragService.findAuftragDatenByAuftragIdTx(technicalOrderNo);
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findFixedIPs4TechnicalOrder(Long technicalOrderNo) throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten(technicalOrderNo);
        if (auftragDaten == null) { return Collections.emptyList(); }
        return ipAddrDao.findAssignedIPsOnly4BillingOrder(auftragDaten.getAuftragNoOrig());
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findNets4TechnicalOrder(Long technicalOrderNo) throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten(technicalOrderNo);
        if (auftragDaten == null) { return Collections.emptyList(); }
        return ipAddrDao.findAssignedNetsOnly4BillingOrder(auftragDaten.getAuftragNoOrig());
    }

    @Override
    @CcTxRequired
    public IPAddress moveIPAddress(IPAddress toMove, Long billingOrderNo, Long sessionId) throws StoreException {
        if ((toMove == null) || (billingOrderNo == null)) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        if (toMove.getNetId() == null) {
            throw new StoreException(
                    "Dies ist keine gezogene (MOnline) IP Adresse. Umzug kann daher nicht durchgeführt werden!");
        }
        if (toMove.getFreigegeben() != null) {
            throw new StoreException(
                    "Dies ist eine bereits freigegebene IP Adresse. Umzug kann daher nicht durchgeführt werden!");
        }
        if (NumberTools.equal(toMove.getBillingOrderNo(), billingOrderNo)) {
            throw new StoreException(
                    "Die Billing Auftragsnummer entspricht der alten. Umzug kann daher nicht durchgeführt werden!");
        }
        if (!ipAddrDao.findActiveIPs4OtherOrders(toMove.getNetId(), toMove.getBillingOrderNo()).isEmpty()) {
            throw new StoreException(
                    String.format(
                            "Es existiert zur NetId %d mindestens ein aktiver Datensatz, der einem anderen Auftrag zugeordnet ist."
                                    + " Umzug kann daher nicht durchgeführt werden!", toMove.getNetId()
                    )
            );
        }

        // relative IPV6 beruecksichtigen
        // Verweis auf das umzuziehende Netz entfernen
        List<IPAddress> relativeIps = ipAddrDao.findRelativeIPs4Prefix(toMove);
        if (CollectionTools.isNotEmpty(relativeIps)) {
            for (IPAddress relativeIp : relativeIps) {
                relativeIp.setPrefixRef(null);
                saveIPAddress(relativeIp, sessionId);
            }
        }

        IPAddress toSave = new IPAddress();
        Date now = new Date();
        toSave.copy(toMove);
        toMove.setGueltigBis(now);
        toSave.setGueltigVon(now);
        toSave.setGueltigBis(DateTools.getHurricanEndDate());
        toSave.setBillingOrderNo(billingOrderNo);
        saveIPAddress(toMove, sessionId);
        saveIPAddress(toSave, sessionId);

        return toSave;
    }

    @Override
    @CcTxRequired
    public AKWarnings releaseIPAddressesNow(final Collection<IPAddress> ipAddrs, Long sessionId) throws StoreException {
        final AKWarnings warnings = new AKWarnings();
        if (CollectionTools.isNotEmpty(ipAddrs)) {
            final Date now = new Date();
            for (IPAddress ip : ipAddrs) {
                warnings.addAKWarnings(ipIsAssignedOnceOnly(ip));
                releaseIP(ip, now, sessionId);
            }
        }
        return warnings;
    }

    @Override
    public AKWarnings ipIsAssignedOnceOnly(IPAddress ipAddr) {
        AKWarnings warnings = new AKWarnings();
        if (ipAddr.getNetId() != null) {
            List<Long> billingOrderNos = ipAddrDao.findActiveIPs4OtherOrders(ipAddr.getNetId(),
                    ipAddr.getBillingOrderNo());
            if (!billingOrderNos.isEmpty()) {
                warnings.addAKWarning(
                        this,
                        String.format(
                                "Die IP-Adresse %s mit NetId = %d ist noch weiterem Auftrag/weiteren Aufträgen %s zugeordnet!",
                                ipAddr.getAbsoluteAddress(), ipAddr.getNetId(), billingOrderNos.toString())
                );
            }
        }
        else {
            warnings.addAKWarning(
                    this,
                    String.format("Die IP-Adresse %d, %s hat keine zugewiesene Net-ID!", ipAddr.getId(),
                            ipAddr.getAddress())
            );
        }
        return warnings;
    }

    /**
     * gibt eine IP Adresse in der MOnline frei und setzt das Freigabedatum aller stillgelegten IP Adressen
     */
    private void releaseIP(IPAddress ip, Date now, Long sessionId) throws StoreException {
        releaseIPInMonline(ip);
        releaseAllIPsInDB(ip, now, sessionId);
    }

    private void releaseIPInMonline(IPAddress ip) throws StoreException {
        if (ip.isIPV4()) {
            final int netmaskSize = IPToolsV4.instance().netmaskSize(ip.getAddress());
            ipAddressAssignmentService.releaseIPv4(ip.getAddress(), ip.getNetId(), netmaskSize);
        }
        else if (ip.isIPV6()) {
            final int netmaskSize = IPToolsV6.instance().netmaskSize(ip.getAddress());
            ipAddressAssignmentService.releaseIPv6(ip.getAddress(), ip.getNetId(), netmaskSize);
        }
    }

    /**
     * setzt das Freigabedatum aller stillgelegten IP Adressen
     */
    private void releaseAllIPsInDB(IPAddress ip, Date now, Long sessionId) throws StoreException {
        try {
            List<IPAddress> nonReleasedAddresses = ipAddrDao.findNonReleasedIPs4NetId(ip.getNetId());
            releaseIPsInDB(nonReleasedAddresses, now, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void releaseIPsInDB(Collection<IPAddress> ipAddresses, Date freigegeben, Long sessionId) {
        if (CollectionTools.isNotEmpty(ipAddresses)) {
            for (IPAddress ipAddress : ipAddresses) {
                releaseIPInDb(ipAddress, freigegeben, sessionId);
            }
        }
    }

    private void releaseIPInDb(IPAddress ipAddress, Date freigegeben, Long sessionId) {
        String userW = getUserW(sessionId); // optionaler Wert
        ipAddress.setUserW(userW);
        ipAddress.setFreigegeben(freigegeben);
        if ((ipAddress.getGueltigBis() == null)
                || (DateTools.compareDates(ipAddress.getGueltigBis(), DateTools.getHurricanEndDate()) == 0)) {
            ipAddress.setGueltigBis(freigegeben);
        }
        ipAddrDao.store(ipAddress);
    }

    /**
     * Updates erfolgen pro Freigabe in Nested Transactions. Eine readOnly Transaktion fuehrt zu einem Hibernate Flush
     * Mode 'NEVER'. D.h. es wird kein session.flush() aufgerufen! Die modifizierten und bereits per- sistierten
     * Entitaeten aus der Nested Transaction fuehren daher zu keiner StaleObjectStateException.
     *
     * @param sessionId
     * @return
     */
    @Override
    @CcTxRequiredReadOnly
    public AKWarnings releaseIPAddresses(Long sessionId) {
        AKWarnings warnings = new AKWarnings();
        warnings.addAKWarning(null, "Net-ID; BillingOrderNo; IP-Adresse; Fehlerbeschreibung");
        int deallocCount = 0;
        int deallocSuccessCount = 0;
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        LOGGER.info("Start 'releaseIPAdresses'");
        Map<Long, Set<IPAddress>> assignedIps = ipAddrDao.findAllActiveAndAssignedIPs();
        if (MapUtils.isNotEmpty(assignedIps)) {
            for (Entry<Long, Set<IPAddress>> assignedIpEntry : assignedIps.entrySet()) {
                try {
                    Set<IPAddress> ips = assignedIpEntry.getValue();
                    if (CollectionUtils.isNotEmpty(ips) && isReady2Release(ips)) {
                        deallocCount++;
                        AKWarnings deallocWarnings = deallocateIPs(ips, sessionId);
                        if ((deallocWarnings == null) || deallocWarnings.isEmpty()) {
                            deallocSuccessCount++;
                        }
                        else {
                            warnings.addAKWarnings(deallocWarnings);
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    warnings.addAKWarning(this,
                            String.format("%d; ; ; Freigabe NetID:'%s'", assignedIpEntry.getKey(), e.getMessage()));
                }
            }
        }
        stopwatch.stop();
        LOGGER.info(String.format("IP-Adress Freigaben: %d, davon erfolgreich: %d, Verarbeitungszeit: %s",
                deallocCount, deallocSuccessCount, stopwatch.toString()));
        return (deallocCount == 0) ? new AKWarnings() : warnings;
    }

    /**
     * Prueft, ob die IP-Adresse (NetID) zur Freigabe bereit ist
     *
     * @return {@code true} wenn IP-Adresse freigegeben werden kann, andernfalls {@code false}
     */
    boolean isReady2Release(@NotNull Set<IPAddress> ips) throws FindException {
        Set<AuftragDaten> auftragDaten = new HashSet<AuftragDaten>();
        Date now = new Date();
        if (!getAuftragDaten4ActiveOrFuture(ips, auftragDaten, now)) {
            getAuftragDaten4LastOutDatedAddress(ips, auftragDaten, now);
        }

        // Auftrag Status und evtl. Karenzzeit bei Kuendigungen pruefen
        return checkAuftragStatus(auftragDaten) && checkKuendigungen(auftragDaten);
    }

    /**
     * Alle {@code AuftragDaten} fuer die letzte (aktuellste) beendete IP Adresse laden.
     */
    void getAuftragDaten4LastOutDatedAddress(@NotNull Set<IPAddress> ips, @NotNull Set<AuftragDaten> auftragDaten,
            Date now) throws FindException {
        IPAddress lastOutDated = null;
        for (IPAddress ipAddress : ips) {
            if (DateTools.isDateBeforeOrEqual(ipAddress.getGueltigBis(), now)) {
                if (lastOutDated == null) {
                    lastOutDated = ipAddress;
                }
                else if (DateTools.isAfter(ipAddress.getGueltigBis(), lastOutDated.getGueltigBis())) {
                    lastOutDated = ipAddress;
                }
            }
        }
        if (lastOutDated != null) {
            List<AuftragDaten> auftragDatenList = auftragService.findAuftragDaten4OrderNoOrig(lastOutDated
                    .getBillingOrderNo());
            auftragDaten.addAll(auftragDatenList);
        }
    }

    /**
     * Alle {@code AuftragDaten} fuer die aktive bzw. in der Zukunft geplante IP Adresse(n) laden. Normalerweise sollte
     * es nur eine aktive IP Adresse geben (aktiv = Zeitspanne von-bis schliesst heute ein).
     */
    boolean getAuftragDaten4ActiveOrFuture(@NotNull Set<IPAddress> ips, @NotNull Set<AuftragDaten> auftragDaten,
            Date now) throws FindException {
        boolean ipAddressFound = false;
        for (IPAddress ipAddress : ips) {
            if (DateTools.isAfter(ipAddress.getGueltigBis(), now)) {
                ipAddressFound = true;
                List<AuftragDaten> auftragDatenList = auftragService.findAuftragDaten4OrderNoOrig(ipAddress
                        .getBillingOrderNo());
                auftragDaten.addAll(auftragDatenList);
            }
        }
        return ipAddressFound;
    }

    /**
     * Prueft, ob kein Auftrag aktiv oder in Kuendigung (ohne gekuendigt!) ist.
     *
     * @return {@code true} wenn kein Auftrag aktiv, andernfalls {@code false}
     */
    boolean checkAuftragStatus(@NotNull Set<AuftragDaten> auftragDatenSet) {
        for (AuftragDaten auftragDaten : auftragDatenSet) {
            if (auftragDaten.isAuftragActive() || auftragDaten.isInKuendigungEx()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prueft lediglich gekuendigte Auftraege. Gekuendigte Auftraege muessen ein Kuendigungsdatum haben. Dieses Datum
     * muss, damit die IP-Adresse freigegeben werden kann, kleiner Heute - Karenzzeit sein. Alle gekuendigten Auftraege
     * muessen diese Bedingungen erfuellen!
     *
     * @return {@code true} wenn kein Auftrag gekuendigt ist oder wenn alle Kuendingen + Karenzzeit in der Vergangenheit
     * liegen, {@code false} wenn mindestens eine Kuendigung + Karenzzeit in der Zukunft liegt
     */
    boolean checkKuendigungen(@NotNull Set<AuftragDaten> auftragDatenSet) throws FindException {
        Date deadlineForRelease = getReleaseDateCalculator().get();
        for (AuftragDaten auftragDaten : auftragDatenSet) {
            if (auftragDaten.isCancelled()) {
                if (auftragDaten.getKuendigung() == null) {
                    throw new FindException(String.format("Der gekündigte Auftrag %d hat kein Kündigungsdatum!",
                            auftragDaten.getAuftragId()));
                }
                if (DateTools.isDateAfterOrEqual(auftragDaten.getKuendigung(), deadlineForRelease)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Versucht das Set der IP-Adressen freizugeben. Diese Methode ist transaktional, da eine erfolgreiche Freigabe in
     * der MOnline auf jeden Fall sofort in Hurrican persistiert werden sollte.
     */
    AKWarnings deallocateIPs(Set<IPAddress> ips, Long sessionId) {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return executeDeallocateIPsInTransaction(tt, ips, sessionId);
    }

    /**
     * Executer, der die Transaktion klammert.
     */
    private AKWarnings executeDeallocateIPsInTransaction(TransactionTemplate tt, final Set<IPAddress> ips,
            final Long sessionId) {
        return tt.execute(new TransactionCallback<AKWarnings>() {
            @Override
            public AKWarnings doInTransaction(TransactionStatus status) {
                return deallocateIPsInTransaction(ips, sessionId);
            }
        });
    }

    /**
     * Versucht das Set der IP-Adressen freizugeben. Dazu itereriert die Methode ueber alle Adressen. Zunaechst spricht
     * die Methode den MOnline WebService an, wenn dieser Erfolg meldet kann die Freigabe in Hurrican persistiert
     * werden. Wenn die
     */
    private AKWarnings deallocateIPsInTransaction(Set<IPAddress> ips, Long sessionId) {
        AKWarnings warnings = new AKWarnings();
        if (CollectionUtils.isNotEmpty(ips)) {
            boolean ipReleased = releaseWithWarningsInMonline(ips, warnings);
            if (ipReleased) {
                releaseWithWarningsInDb(ips, warnings, sessionId);
            }
        }
        return warnings;
    }

    private boolean releaseWithWarningsInMonline(Set<IPAddress> ips, AKWarnings warnings) {
        boolean ipReleased = true;
        IPAddress ip = ips.iterator().next();
        try {
            releaseIPInMonline(ip);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            String errorMsg = "%d; %d; %s; Freigabe MOnline: '%s'";
            warnings.addAKWarning(null,
                    String.format(errorMsg, ip.getNetId(), ip.getBillingOrderNo(), ip.getAddress(), e.getMessage()));
            ipReleased = false;
        }
        return ipReleased;
    }

    private void releaseWithWarningsInDb(Set<IPAddress> ips, AKWarnings warnings, Long sessionId) {
        Date now = new Date();
        for (IPAddress ip : ips) {
            try {
                releaseIPInDb(ip, now, sessionId);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                String errorMsg = "%d; %d; %s; Freigabe Hurrican DB: %s";
                warnings.addAKWarning(null,
                        String.format(errorMsg, ip.getNetId(), ip.getBillingOrderNo(), ip.getAddress(), e.getMessage()));
            }
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddress> findIPs4NetId(Long netId) throws FindException {
        try {
            return ipAddrDao.findIPs4NetId(netId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<Pair<Long, Reference>> findAllNetIdsWithPurposeFromMonline() throws FindException {
        List<Pair<Long, String>> netIdsWithPoolName = findNetIdsWithPoolName();
        if (CollectionTools.isEmpty(netIdsWithPoolName)) { return Collections.emptyList(); }
        List<Pair<Long, Reference>> netIdsWithPurpose = new ArrayList<Pair<Long, Reference>>(netIdsWithPoolName.size());
        final Reference kundennetz = referenceService.findReference(IPPurpose.Kundennetz.getId());
        final Reference transfernetz = referenceService.findReference(IPPurpose.Transfernetz.getId());
        for (Pair<Long, String> netIdWithPool : netIdsWithPoolName) {
            String pool = netIdWithPool.getSecond().toUpperCase();
            Long netId = netIdWithPool.getFirst();
            Reference purpose = (pool.contains("LINK")) ? transfernetz : kundennetz;
            netIdsWithPurpose.add(new Pair<Long, Reference>(netId, purpose));
        }
        return netIdsWithPurpose;
    }

    private List<Pair<Long, String>> findNetIdsWithPoolName() throws FindException {
        try {
            return getInternetService(INetNumService.class).findAllNetIdsWithPoolName();
        }
        catch (ServiceNotFoundException e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddressPanelView> findAllIPAddressPanelViews(Long billingOrderNo) throws FindException {
        if (billingOrderNo == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        List<IPAddress> ipAddresses = ipAddrDao.findAllAssignedIPs4BillingOrder(billingOrderNo);
        if (CollectionTools.isEmpty(ipAddresses)) {
            return Collections.emptyList();
        }
        List<IPAddressPanelView> retValues = new ArrayList<IPAddressPanelView>();
        for (IPAddress ipAddress : ipAddresses) {
            IPAddressPanelView retValue = new IPAddressPanelView();
            retValue.setIpAddress(ipAddress);
            addStatusToIPAddressPanelView(retValue, ipAddress);
            retValues.add(retValue);
        }
        return retValues;
    }

    private void addStatusToIPAddressPanelView(IPAddressPanelView view, IPAddress ip) {
        final Date now = new Date();
        if (DateTools.isAfter(ip.getGueltigBis(), now)) {
            view.setStatus(IPADDRESSPANELVIEW_STATE_ACTIVE);
        }
        else {
            final int ipAddrAssignmentCnt = ipAddrDao.findIpToOrdersAssignmentCount(ip.getNetId());
            if (ipAddrAssignmentCnt <= 0) {
                if ((ip.getFreigegeben() == null)) {
                    view.setStatus(IPADDRESSPANELVIEW_STATE_KARENZZEIT);
                }
                else {
                    view.setStatus(IPADDRESSPANELVIEW_STATE_FREIGEGEBEN);
                }
            }
            else {
                if (!ipAddrDao.findActiveIPs4OtherOrders(ip.getNetId(), ip.getBillingOrderNo()).isEmpty()) {
                    view.setStatus(IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_OTHER);
                }
                else {
                    view.setStatus(IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_SAME);
                }
            }
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public List<IPAddressSearchView> filterIPsByBinaryRepresentation(IPAddressSearchQuery searchQuery)
            throws FindException {
        if ((searchQuery == null) || (searchQuery.getIpBinary() == null)) {
            throw new FindException(
                    FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            // @formatter:off
            return ipAddrDao.filterIPsByBinaryRepresentation(
                AddressTypeEnum.getTypeNames(!searchQuery.isV4Search()),
                searchQuery.getIpBinary(),
                searchQuery.isFindOnlyActive(),
                searchQuery.getResultLimit());
            // @formatter:on
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public IPAddress findDHCPv6PDPrefix(Long billingOrderNo) throws FindException {
        try {
            return ipAddrDao.findDHCPv6PDPrefix(billingOrderNo);
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new FindException("Es wurde mehr als ein DHCPv6_PD Prefix pro Billing Auftrag ermittelt!", e);
        }
    }

    @Override
    @CcTxRequiredReadOnly
    public IPAddress findDHCPv6PDPrefix4TechnicalOrder(Long auftragId) throws FindException {
        AuftragDaten auftragDaten = getAuftragDaten(auftragId);
        if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
            throw new FindException(String.format(
                    "Zum techn. Auftrag %d konnte kein Billing Auftrag ermittelt werden!", auftragId));
        }
        return findDHCPv6PDPrefix(auftragDaten.getAuftragNoOrig());
    }

    void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    void setIpAddrDao(IPAddressDAO ipAddrDao) {
        this.ipAddrDao = ipAddrDao;
    }

    void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

}
