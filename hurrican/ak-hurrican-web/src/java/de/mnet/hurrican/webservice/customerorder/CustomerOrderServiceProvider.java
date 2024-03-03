/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.13
 */
package de.mnet.hurrican.webservice.customerorder;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.jws.*;
import javax.xml.datatype.*;
import javax.xml.ws.*;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.BusinessKeyBuilder;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.impl.evn.EvnServiceImpl;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnEnum;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceException;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceFault;
import de.augustakom.hurrican.service.cc.impl.logindata.LoginDataService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataActiveOrderNotFoundException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataImsOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotUniqueOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotValidProductException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataOrderNotFoundException;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;
import de.augustakom.hurrican.service.exceptions.helper.HandleErrorFactory;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CallType;
import de.mnet.esb.cdm.customer.customerorderservice.v1.Communication;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ContactDetails;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ContactPerson;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CustomerOrderLogin;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CustomerOrderService;
import de.mnet.esb.cdm.customer.customerorderservice.v1.DataUsageDetails;
import de.mnet.esb.cdm.customer.customerorderservice.v1.DataUsageDetailsStatus;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ESBFault;
import de.mnet.esb.cdm.customer.customerorderservice.v1.LineIdsPerCustomerOrderId;
import de.mnet.esb.cdm.customer.customerorderservice.v1.OrderStatus;
import de.mnet.esb.cdm.customer.customerorderservice.v1.OrderStatusParameter;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.webservice.customerorder.services.CustomerOrderServiceLoginDataMapper;
import de.mnet.hurrican.webservice.customerorder.services.OrderStatusService;
import de.mnet.hurrican.webservice.customerorder.services.PublicOrderStatus;
import de.mnet.hurrican.webservice.customerorder.services.VerbindungsBezeichnungMapperService;
import de.mnet.hurrican.webservice.customerorder.services.VerbindungsBezeichnungService;

@ServiceMode(Service.Mode.MESSAGE)
public class CustomerOrderServiceProvider implements CustomerOrderService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerOrderServiceProvider.class);
    public static final String ESB_CUST_ORD_001 = "ESB-CUST-ORD-001";

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    CarrierService carrierService;

    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    KundenService kundenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    AnsprechpartnerService ansprechpartnerService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    EndstellenService endstellenService;

    @Resource
    OrderStatusService orderStatusService;

    @Resource
    LoginDataService loginDataService;

    @Resource
    CustomerOrderServiceLoginDataMapper loginDataMapper;

    @Resource
    EvnServiceImpl evnServiceImpl;

    @Resource
    WebServiceContext webServiceContext;

    @Resource
    AtlasErrorHandlingService atlasErrorHandlingService;

    @Resource
    VerbindungsBezeichnungService verbindungsBezeichnungService;

    /**
     * Kopie aus Service-Repository:<br/> Gibt auf Basis unterschiedlicher Kriterien alle Ansprechpartner für einen
     * Auftrag zurück. Ist kein Ansprechpartner vorhanden, wird eine leere Liste zurückgeben.<br/> <br/> Wird kein
     * Auftrag gefunden, so wird der Error Code "ESB-CUST-ORD-001" zurückgegeben.
     *
     * @param lineContractId DTAG Vertragsnummer
     * @param customerId kunden nummer (BSI)
     * @param customerOrderId orderNoOrig (Billing)
     * @param contactPerson Liste mit ContactPersons
     * @throws ESBFault
     */
    @Override
    public void getOrderDetails(
            java.lang.String lineContractId,
            javax.xml.ws.Holder<java.lang.String> customerId,
            javax.xml.ws.Holder<java.lang.String> customerOrderId,
            javax.xml.ws.Holder<java.util.List<de.mnet.esb.cdm.customer.customerorderservice.v1.ContactPerson>> contactPerson
    ) throws ESBFault {
        final List<CustomerContact> contacts;
        try {
            Carrierbestellung carrierbestellung = getCarriebestellung(lineContractId);
            AuftragDaten auftragDaten = getAuftragDaten(lineContractId, carrierbestellung.getId());
            Long auftragId = auftragDaten.getAuftragId();
            Long kundenNr = getKundenNr(auftragId);
            contacts = getContacts(kundenNr, auftragId, carrierbestellung);

            customerId.value = String.valueOf(kundenNr);
            customerOrderId.value = String.valueOf(auftragDaten.getAuftragNoOrig());
            contactPerson.value = new ArrayList<>();
            addContactPersons(contactPerson.value, contacts);
        }
        catch (ESBFault e) {
            Objects.ToStringHelper faultInfo = Objects.toStringHelper(e);
            if (e.getFaultInfo() != null) {
                faultInfo.add("message", e.getFaultInfo().getErrorMessage());
                faultInfo.add("code", e.getFaultInfo().getErrorCode());
            }
            LOGGER.error(faultInfo.toString());
            throw e;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ESBFault(e.getMessage(), e);
        }
    }

    private void addContactPersons(List<ContactPerson> contactPersons, List<CustomerContact> contacts) {
        for (CustomerContact contact : contacts) {
            ContactPerson cp = new ContactPerson();
            cp.setRole(contact.role);
            // lastname ist Pflichtfeld
            cp.setLastName(contact.lastname != null ? contact.lastname : "unknown");
            cp.setFirstName(contact.firstname);
            cp.setTitle(contact.title);
            ContactPerson.Communication com = new ContactPerson.Communication();
            com.setPhone(contact.phone);
            com.setMobile(contact.mobile);
            com.setEMail(contact.email);
            com.setFax(contact.fax);
            cp.setCommunication(com);
            contactPersons.add(cp);
        }
    }

    /**
     * Ermittle ueber die Vertragsbezeichnung die Carrierbestellung. Anschließend wird der dafuer gueltige Auftrag
     * ermittelt. Für diesen Auftrag wird versucht _einen_ Ansprechpartner entweder fuer Endstelle_B _oder_ Endstelle_A
     * zu ermitteln. Zusaetzlich wird der Kunden auf dem Auftragserfassungessystem (Billing) hinzugefuegt.
     *
     * @param kundenNr
     * @param auftragNr
     * @param carrierbestellung
     * @return
     * @throws FindException
     */
    private List<CustomerContact> getContacts(Long kundenNr, Long auftragNr, Carrierbestellung carrierbestellung) throws FindException {
        final Ansprechpartner.Typ ansprechpartnerType = getEndstellenTyp(carrierbestellung);

        ImmutableList.Builder<CustomerContact> contacts = ImmutableList.builder();

        final List<Ansprechpartner> ansprechpartner = ansprechpartnerService.findAnsprechpartner(
                ansprechpartnerType, auftragNr);
        for (Ansprechpartner a : ansprechpartner) {
            addContact(contacts, "ENDSTELLE", a.getAddress());
        }

        final Kunde kunde = kundenService.findKunde(kundenNr);
        addContact(contacts, "CUSTOMER", kunde);

        return contacts.build();
    }

    private Carrierbestellung getCarriebestellung(String lineContractId) throws ESBFault {
        List<Carrierbestellung> carrierbestellungen = carrierService.findCBsByNotExactVertragsnummer(lineContractId);
        Carrierbestellung carrierbestellung = Iterables.getFirst(carrierbestellungen, null);
        if (carrierbestellung == null) {
            String msg = String.format("No matching Carrier Order found for lineId=%s", lineContractId);
            throw new ESBFault("error", createESBFault(ESB_CUST_ORD_001, msg));
        }
        return carrierbestellung;
    }

    private Ansprechpartner.Typ getEndstellenTyp(Carrierbestellung carrierbestellung) throws FindException {
        List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellung);
        String endstellenType = endstellen.iterator().next().getEndstelleTyp();
        if (endstellenType.equalsIgnoreCase(Endstelle.ENDSTELLEN_TYP_A)) {
            return Ansprechpartner.Typ.ENDSTELLE_A;
        }
        else if (endstellenType.equalsIgnoreCase(Endstelle.ENDSTELLEN_TYP_B)) {
            return Ansprechpartner.Typ.ENDSTELLE_B;
        }
        else {
            throw new IllegalStateException("Unexpected endstellenTyp=" + endstellenType);
        }
    }

    private void addContact(ImmutableList.Builder<CustomerContact> contacts, String role, Kunde kunde) {
        CustomerContact contact = new CustomerContact();
        contact.role = role;
        contact.title = null;
        contact.firstname = kunde.getVorname();
        contact.lastname = kunde.getName();
        contact.phone = kunde.getHauptRufnummer();
        if (Strings.isNullOrEmpty(contact.phone)) {
            contact.phone = kunde.getRnGeschaeft();
        }
        if (Strings.isNullOrEmpty(contact.phone)) {
            contact.phone = kunde.getRnPrivat();
        }
        contact.mobile = kunde.getRnMobile();
        contact.email = kunde.getEmail();
        contact.fax = kunde.getRnFax();
        contacts.add(contact);
    }

    private void addContact(ImmutableList.Builder<CustomerContact> contacts, String role, CCAddress address) {
        if (address == null) {
            return;
        }
        CustomerContact contact = new CustomerContact();
        contact.role = role;
        contact.title = address.getTitel();
        contact.firstname = address.getVorname();
        contact.lastname = address.getName();
        contact.phone = address.getTelefon();
        contact.mobile = address.getHandy();
        contact.email = address.getEmail();
        contact.fax = address.getFax();
        contacts.add(contact);
    }

    // Lt. Alex Hauswald sollen vom Webservice alle (auch gekuendigte und gelockte = aelter als 1Jahr, siehe TKG §95)
    // Auftraege ausgegeben werden. Zu einem spaeteren Zeitpunkt wird entschieden wie weiter gefiltert wird (evtl.
    // auf offene TAL-Bestellung)
    private AuftragDaten getAuftragDaten(String lineContractId, Long carrierbestellungId) throws FindException, ESBFault {
        final List<AuftragDaten> auftraege = carrierService.findAuftragDaten4CB(carrierbestellungId);

        Optional<AuftragDaten> auftrag = filterAuftragDaten(auftraege);

        if(auftrag.isPresent()) {
            return auftrag.get();
        }
        else {
            String msg = String.format("No matching Order found for lineId=%s, carrierbestellungId=%s", lineContractId, carrierbestellungId);
            throw new ESBFault("error", createESBFault(ESB_CUST_ORD_001, msg));
        }
    }

    /**
     * Filters the supplied {@code auftraege} returning <i>zero</i> or <i>one</i> auftrag based on the following criteria:
     * <ul>
     *     <li>the supplied list is empty - no auftrag is returned</li>
     *     <li>the supplied list contains one auftrag - this is returned</li>
     *     <li>the supplied list contains multiple auftraege - the auftrag with the most recent Billing order is returned</li>
     * </ul>
     * @param auftraege the auftraege to be filtered
     * @return zero or one auftrag
     */
    protected Optional<AuftragDaten> filterAuftragDaten(List<AuftragDaten> auftraege) {
        return auftraege.stream()
                    .filter(a -> a.getAuftragNoOrig() != null)
                    .max(Comparator.comparing(AuftragDaten::getAuftragNoOrig));
    }

    private de.mnet.esb.cdm.shared.common.v1.ESBFault createESBFault(CustomerOrderServiceErrorCode errorCodeEnum, String msg) {
        return createESBFault(errorCodeEnum.getExternalErrorCode(), msg);
    }
    private de.mnet.esb.cdm.shared.common.v1.ESBFault createESBFault(String code, String msg) {
        de.mnet.esb.cdm.shared.common.v1.ESBFault causeFault = new de.mnet.esb.cdm.shared.common.v1.ESBFault();
        causeFault.setErrorCode(code);
        causeFault.setErrorMessage(msg);
        return causeFault;
    }

    private Long getKundenNr(long auftragNo) throws FindException {
        final Auftrag auftrag = ccAuftragService.findAuftragById(auftragNo);
        return auftrag.getKundeNo();
    }

    @Override
    public void updateCustomerCommunication(String lineContractId, String lineId, CallType callType, String callResult, XMLGregorianCalendar responseDate, List<Communication> communication) {
        LOGGER.error("by contract not implemented");
    }

    @Override
    public List<ContactDetails> requestCustomerCommunication(String lineContractId, String lineId, CallType callType) throws ESBFault {
        throw new ESBFault("by contract not implemented");
    }

    @Override
    public void getIncidentContactInformation(String lineId, Holder<String> customerId, Holder<String> customerOrderId,
            Holder<List<ContactPerson>> contactPerson) throws ESBFault {
        throw new ESBFault("not implemented");
    }

    @Override
    public void activateSIM(
            @WebParam(name = "customerId", targetNamespace = "http://www.mnet.de/esb/cdm/Customer/CustomerOrderService/v1") String customerId,
            @WebParam(name = "ICCID", targetNamespace = "http://www.mnet.de/esb/cdm/Customer/CustomerOrderService/v1") String iccid)
            throws ESBFault {
        throw new ESBFault("by contract not implemented");
    }

    static class CustomerContact {
        public String role;
        public String lastname;
        public String phone;
        public String firstname;
        public String title;
        public String mobile;
        public String email;
        public String fax;
    }

    @Override
    public List<OrderStatus> getPublicOrderStatus(List<String> customerOrderIds) throws ESBFault {
        if (customerOrderIds != null && !customerOrderIds.isEmpty()) {
            final List<OrderStatus> statuses = customerOrderIds.stream()
                    .filter(id -> StringUtils.isNotEmpty(id))
                    .map(id -> getSinglePublicOrderStatus(id))
                    .collect(Collectors.toList());
            return statuses;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    private OrderStatus getSinglePublicOrderStatus(String customerOrderId) {
        final PublicOrderStatus publicOrderStatus = orderStatusService.getPublicOrderStatus(customerOrderId);
        final OrderStatus result = new OrderStatus();
        result.setCustomerOrderId(customerOrderId);
        result.setStatus(publicOrderStatus.getStatusValue().getStatusIdentifier());
        // details
        publicOrderStatus.getDetails().entrySet().stream()
                .map(entry -> newOrderStatusParameter(entry.getKey(), entry.getValue()))
                .forEach(param -> result.getDetail().add(param));

        return result;
    }


    private OrderStatusParameter newOrderStatusParameter(String key, String value) {
        final OrderStatusParameter param = new OrderStatusParameter();
        param.setKey(key);
        param.setValue(value);
        return param;
    }

    /**
     * Kommunikation Zugangsdaten im Kundenportal
     */
    @Override
    public List<CustomerOrderLogin> getCustomerLoginDetails(List<String> customerOrderId) throws ESBFault {
        final List<CustomerOrderLogin> resultLoginList= new ArrayList<>();
        if (customerOrderId != null) {
            for (String customerOrderIdString : customerOrderId) {
                try {
                    final Long customerOrderIdLong = new Long(customerOrderIdString);
                    try {
                        final LoginData loginData = loginDataService.getLoginData(customerOrderIdLong);
                        final CustomerOrderLogin login = loginDataMapper.mapCustomerOrderLogin(loginData, customerOrderIdLong);
                        resultLoginList.add(login);
                    }
                    catch (LoginDataOrderNotFoundException e) {
                        final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ORDER_NOT_FOUND, "Auftrag nicht gefunden."), e);
                    }
                    catch (LoginDataNotValidProductException e) {
                        throw new ESBFault(e.getMessage(), createESBFault(CustomerOrderServiceErrorCode.PRODUCT_NOT_PROVIDED, "Zugangsdaten nicht ermittelt, Produkt wird nicht unterstuetzt."), e);
                    }
                    catch (LoginDataActiveOrderNotFoundException e) {
                        final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ACTIVE_ORDER_NOT_FOUND, "Kein aktiver Auftrag"), e);
                    }
                    catch (LoginDataNotUniqueOrderException e) {
                        final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.NOT_UNIQUE_ORDER_BY_ID, "Kann nicht eindeutig den Auftrag feststellen"), e);
                    }
                    catch (LoginDataImsOrderException e) {
                        final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.IMS_HW_SWITCH, "Der Auftrag ist auf IMS HWSwitch realisiert."), e);
                    }
                    catch (LoginDataException e) {
                        final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        LOGGER.warn(msg, e);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.UNKNOWN, "Unbekannt"), e);
                    }
                    catch (Exception e) {
                        final String msg = String.format("Technischer Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%s]", customerOrderIdString);
                        LOGGER.error(msg, e);
                        throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.UNKNOWN, "Technischer Fehler"), e);
                    }
                }
                catch (NumberFormatException e) {
                    final String msg = String.format("Billing Auftrag Nr. [%s] muss eine Zahl sein", customerOrderIdString);
                    throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ORDER_ID_NOT_NUMBER, msg), e);
                }
            }
        }
        return resultLoginList;
    }

    @Override
    public List<DataUsageDetails> getDataUsageDetailsStatus(List<String> customerOrderIdList) throws ESBFault {
        if (customerOrderIdList != null) {
            final ImmutableList.Builder<DataUsageDetails> usageDetailsListBuilder = ImmutableList.builder();
            for (String taifunOrderIdStr : customerOrderIdList) {
                try {
                    final List<DataUsageDetailsStatus> statusesList = new ArrayList<>();
                    final AuftragDaten singleAuftrag = getSingleAuftrag(new Long(taifunOrderIdStr));
                    final List<IntAccount> accounts = evnServiceImpl.getIntAccounts4Auftrag(singleAuftrag.getAuftragId());
                    for (IntAccount acc : accounts) {
                        final EvnEnum evn = evnServiceImpl.getEvnData(singleAuftrag, acc);
                        // usage details status
                        final DataUsageDetailsStatus usageDetailsStatus = new DataUsageDetailsStatus();
                        usageDetailsStatus.setRadiusAccountId(acc.getAccount());
                        usageDetailsStatus.setStatus(evn.name());
                        statusesList.add(usageDetailsStatus);
                    }
                    // usage details
                    final DataUsageDetails usageDetails = new DataUsageDetails();
                    usageDetails.setCustomerOrderId(taifunOrderIdStr);
                    usageDetails.getAccount().addAll(statusesList);
                    usageDetailsListBuilder.add(usageDetails);
                }
                catch (NumberFormatException e) {
                    final String msg = String.format("Billing Auftrag Nr. [%s] muss eine Zahl sein", taifunOrderIdStr);
                    throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ORDER_ID_NOT_NUMBER, msg), e);
                }
            }
            // usage details list
            return usageDetailsListBuilder.build();
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    protected AuftragDaten getSingleAuftrag(Long orderNoOrig) throws ESBFault {
        final List<AuftragDaten> auftragDatenList = getAuftragDaten4OrderNoOrig(orderNoOrig);
        if (auftragDatenList.isEmpty()) {
            final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNoOrig);
            throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ORDER_NOT_FOUND, "Auftrag nicht gefunden."));
        }
        final List<AuftragDaten> auftragDatenFilteredList = filterActiveAuftragDaten(auftragDatenList);
        if (auftragDatenFilteredList.isEmpty()) {
            final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNoOrig);
            throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.ACTIVE_ORDER_NOT_FOUND, "Kein aktiver Auftrag"));
        } else if (auftragDatenFilteredList.size() > 1) {
            final String msg = String.format("Fehler bei der Ermittlung von Zugangsdaten für Billing Auftrag Nr. [%d]", orderNoOrig);
            throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.NOT_UNIQUE_ORDER_BY_ID, "Kann nicht eindeutig den Auftrag feststellen"));
        }
        return auftragDatenFilteredList.get(0);
    }

    private List<AuftragDaten> filterActiveAuftragDaten(List<AuftragDaten> auftragDaten) {
        final List<AuftragDaten> active = auftragDaten.stream()
                .filter(AuftragDaten::isAuftragActiveAndInBetrieb)
                .collect(Collectors.toList());
        if (active.size() <= 1) {
            return active;
        } else {
            return active.stream()
                    .filter(ad -> !Produkt.isSdslNDraht(ad.getProdId()))
                    .collect(Collectors.toList());
        }
    }

    private List<AuftragDaten> getAuftragDaten4OrderNoOrig(Long orderNoOrig) throws ESBFault {
        try {
            final List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);
            return auftragDaten4OrderNoOrig != null ? auftragDaten4OrderNoOrig : Collections.EMPTY_LIST;
        }
        catch (FindException e) {
            final String msg = String.format("Technischer Fehler bei der Ermittlung von Techn. Auftrag für Billing Auftrag Nr. [%d]", orderNoOrig);
            LOGGER.error(msg, e);
            throw new ESBFault(msg, createESBFault(CustomerOrderServiceErrorCode.UNKNOWN, "Technischer Fehler"), e);
        }
    }

    @Override
    public void activateDataUsageDetails(String radiusAccountId) {
        try {
            evnServiceImpl.activateEvn(radiusAccountId, HurricanScheduler.getSessionId());
        }
        catch (EvnServiceException e) {
            handleAsyncCallError(e);
        }
    }

    @Override
    public void deactivateDataUsageDetails(String radiusAccountId) {
        try {
            evnServiceImpl.deactivateEvn(radiusAccountId, HurricanScheduler.getSessionId());
        }
        catch (EvnServiceException e) {
            handleAsyncCallError(e);
        }
    }

    private void handleAsyncCallError(EvnServiceException e) {
        final EvnServiceFault serviceFault = e.getEvnServiceFault();
        final BusinessKeyBuilder businessKeyBuilder = new BusinessKeyBuilder();
        if (serviceFault != null && serviceFault.getAuftragDaten() != null && serviceFault.getAuftragDaten().getAuftragNoOrig() != null) {
            businessKeyBuilder.withKeyValue("orderId", serviceFault.getAuftragDaten().getAuftragNoOrig().toString());
        }
        if (serviceFault != null && StringUtils.isNotEmpty(serviceFault.getAccountNumber())) {
            businessKeyBuilder.withKeyValue("account", serviceFault.getAccountNumber());
        }
        final String errorCode = serviceFault != null && serviceFault.getFaultEnum() != null ? serviceFault.getFaultEnum().getExternalErrorCode() : "";

        final HandleError handleError = new HandleErrorFactory(webServiceContext)
                .create(e, errorCode, "CustomerOrderService", businessKeyBuilder.build());
        atlasErrorHandlingService.handleError(handleError);
    }

    @Override
    public List<LineIdsPerCustomerOrderId> getLineIds(List<String> customerOrderId) throws ESBFault {
        final List<Long> origOrderIdList = VerbindungsBezeichnungMapperService.mapToLong(customerOrderId);
        final List<LineIdsPerCustomerOrderId> result = new ArrayList<>();
        for (Long origOrderId : origOrderIdList) {
            final List<VerbindungsBezeichnung> vbList = verbindungsBezeichnungService.getVerbindungsBezeichnung(origOrderId);
            if (CollectionUtils.isNotEmpty(vbList)) {
                result.add(VerbindungsBezeichnungMapperService.mapVerbindungsBezeichnung(origOrderId, vbList));
            } else {
                LOGGER.warn(String.format("Keine Verbindungsbezeichnung für Auftrag Orig. Nummer [%d] gefunden", origOrderId));
            }
        }
        return result;
    }

}
