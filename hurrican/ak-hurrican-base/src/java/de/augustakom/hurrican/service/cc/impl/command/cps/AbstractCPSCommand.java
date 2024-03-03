/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2009 13:54:07
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.io.*;
import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceRequestDocument;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Node;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.dao.cc.CPSTransactionDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;
import de.augustakom.hurrican.model.cc.cps.ICPSConstants;
import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceResponseSOData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;
import de.mnet.common.tools.DateConverterUtils;


/**
 * Abstrakte Command-Klasse fuer CPS-Commands.
 */
public abstract class AbstractCPSCommand extends AbstractServiceCommand implements ICPSConstants {

    private static final Logger LOGGER = Logger.getLogger(AbstractCPSCommand.class);

    /**
     * String fuer USERW Eintraege, um den Datensatz als CPS-Provisioning zu kennzeichnen.
     */
    protected static final String CPS_USER = "CPS-Provisioning";

    private static final ResourceReader CPS_ERROR_CODES_RESOURCE_READER = new ResourceReader(
            "de.augustakom.hurrican.service.cc.impl.command.cps.cps_error_codes");

    /**
     * Konstante, um die Session-ID des Users dem Command zu uebergeben.
     */
    public static final String KEY_SESSION_ID = "user.session.id";
    /**
     * Konstante, um die betroffene CPS-Transaction ID dem Command zu uebergeben.
     */
    public static final String KEY_CPS_TX_ID = "cps.tx.id";
    /**
     * Key fuer die Uebergabe des Lock-Modes an das Command.
     */
    public static final String KEY_LOCK_MODE = "lock.mode";
    /**
     * Key fuer die Uebergabe des LazyInit Modes.
     */
    public static final String KEY_LAZY_INIT_MODE = "lazy.init.mode";
    /**
     * Key fuer die Uebergabe der CPS-Transaction
     */
    public static final String KEY_CPS_TX = "cps.tx";

    protected static final String DEFAULT_WARNING = "Order-ID: {0} - {1}";

    private MnetWebServiceTemplate cpsWebServiceTemplate = null;
    private CPSTransactionDAO cpsTransactionDAO = null;
    private AbstractMarshaller xmlMarshaller = null;

    protected CPSService cpsService;
    protected QueryCCService queryCCService;
    protected ProduktService produktService;
    protected ReferenceService referenceService;
    protected CCLeistungsService ccLeistungsService;
    protected HWService hwService;
    protected PhysikService physikService;

    /**
     * Gibt die Session-ID zurueck.
     */
    protected Long getSessionId() {
        return (Long) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * Gibt die CPS Transaction-ID zurueck.
     */
    protected Long getCPSTxId() {
        return (Long) getPreparedValue(KEY_CPS_TX_ID);
    }

    /**
     * Gibt den definierten Lock-Mode zurueck.
     *
     * @return Lock-Mode fuer den Auftrag.
     */
    protected String getLockMode() {
        return (String) getPreparedValue(KEY_LOCK_MODE);
    }

    /**
     * Gibt den Lazy-Init Mode zurueck
     *
     * @return
     */
    protected LazyInitMode getLazyInitMode() {
        return (LazyInitMode) getPreparedValue(KEY_LAZY_INIT_MODE);
    }

    /**
     * Fuegt eine neue Warnung hinzu. <br> Als Basis-Text wird 'DEFAULT_WARNING' verwendet. Die Parameter 'auftragId'
     * und 'warning' werden in die Platzhalter eingesetzt.
     *
     * @param auftragId betroffene Auftrags-ID
     * @param warning   Warn-Meldung
     */
    protected void addWarning(Integer auftragId, String warning) {
        if (getWarnings() == null) {
            AKWarnings warnings = new AKWarnings();
            setWarnings(warnings);
        }

        String warnMsg = StringTools.formatString(DEFAULT_WARNING, new Object[] { "" + auftragId, warning });
        LOGGER.info("WARNING: " + warnMsg);
        getWarnings().addAKWarning(this, warnMsg);
    }

    /**
     * Speichert das angegebene CPSTransaction Objekt mit einer neuen DB-Transaction (PROPAGATION_REQURIES_NEW)
     *
     * @param toUpdate
     * @param sessionId
     */
    protected void updateCPSTxWithNewTx(CPSTransaction toUpdate, Long sessionId) {
        try {
            cpsService.saveCPSTransactionTxNew(toUpdate, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * @param cpsTx   CPS-Transaction
     * @param message zu speichernde Message
     * @see AbstractCPSCommand#createCPSTxLog(CPSTransaction, String, boolean) Die Anlage des Logs erfolgt ueber eine
     * eigene DB-Transaction!
     */
    protected void createCPSTxLog(final CPSTransaction cpsTx, final String message) {
        createCPSTxLog(cpsTx, message, true);
    }

    /**
     * Speichert die Message 'message' als Log zur angegebenen CPS-Transaction ab. <br> Zusaetzlich wird die Message als
     * Warning gespeichert. <br>
     *
     * @param cpsTx   CPS-Transaction
     * @param message zu speichernde Message
     * @param newTx   Flag definiert, ob eine eigene (true) oder eine bestehende (false) DB-Transaction verwendet werden
     *                soll
     */
    protected void createCPSTxLog(final CPSTransaction cpsTx, final String message, final boolean newTx) {
        createCPSTxLog(cpsTx, message, newTx, true);
    }

    /**
     * @param cpsTx
     * @param message
     * @param newTx
     * @param withWarning
     * @see AbstractCPSCommand#createCPSTxLog(CPSTransaction, String, boolean)
     */
    protected void createCPSTxLog(final CPSTransaction cpsTx, final String message, final boolean newTx, final boolean withWarning) {
        if (cpsTx == null) { return; }
        try {
            if (cpsTx.getId() != null) {
                CPSTransactionLog log = new CPSTransactionLog(cpsTx.getId(), message);

                if (newTx) {
                    cpsService.saveCPSTransactionLogTxNew(log);
                }
                else {
                    cpsService.saveCPSTransactionLogInTx(log);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            if (withWarning) {
                addWarning(cpsTx.getAuftragId(), message);
            }
        }
    }

    /**
     * Konfiguriert das WebService-Template mit Daten aus der WSConfig-Tabelle und gibt das Template konfiguriert
     * zurueck. <br> <br> Folgende Konfigurationen werden vorgenommen: <ul> <li>Default-URI des WebServices wird gesetzt
     * <li>Security-Interceptor fuer 'UsernameToken' wird mit UserName/Password konfiguriert (nur, wenn entsprechend
     * konfiguriert) </ul> <br>
     *
     * @return das konfigurierte WebServiceTemplate
     * @throws ServiceCommandException falls notwendige Konfigurationsparameter nicht vorhanden sind oder bei der
     *                                 Konfiguration ein Fehler auftritt
     */
    protected WebServiceTemplate configureAndGetWSTemplate() throws ServiceCommandException {
        try {
            return configureAndGetWSTemplate(WebServiceConfig.WS_CFG_CPS_SOURCE_AGENT_ASYNC);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

    /**
     * Konfiguriert das WebService-Template mit Daten aus der Tabelle T_WEBSERVICE_CONFIG
     *
     * @param templateId ID des zu verwendenden Konfigurationseintrags
     * @return konfiguriertes WebServiceTemplate
     * @throws ServiceCommandException falls notwendige Konfigurationsparameter nicht vorhanden sind oder bei der
     *                                 Konfiguration ein Fehler auftritt
     */
    protected WebServiceTemplate configureAndGetWSTemplate(Long templateId) throws ServiceCommandException {
        try {
            QueryCCService qs = getCCService(QueryCCService.class);
            WebServiceConfig wsCfg = qs.findById(templateId, WebServiceConfig.class);

            MnetWebServiceTemplate wst = getCpsWebServiceTemplate();
            wst.configureWSTemplate(wsCfg);

            return wst;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

    /**
     * Generiert aus dem uebergebenen Objekt eine XML-Struktur und gibt diese in Form eines Strings zurueck.
     *
     * @param soData     zu transformierendes Objekt
     * @param marshaller zu verwendender Marshaller XML-String aus dem angegebenen Objekt
     * @throws ServiceCommandException
     */
    public static String transformSOData2XML(AbstractCPSServiceOrderDataModel soData,
            XStreamMarshaller marshaller) throws ServiceCommandException {
        try {
            if (soData == null) {
                throw new HurricanServiceCommandException("ServiceOrderData object is null!");
            }

            // XML ueber XStream generieren
            XStream xstream = marshaller.getXStream();
            StringWriter stringWriter = new StringWriter();
            PrettyPrintWriter writer = new PrettyPrintWriter(stringWriter, new XmlFriendlyReplacer("_", "_"));
            xstream.marshal(soData, writer);
            String xml = stringWriter.toString();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("ServiceOrderData is:");
                LOGGER.info(xml);
            }
            return xml;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error creating XML-Stream for ServiceOrder-Data: " + e.getMessage(), e);
        }
    }

    /**
     * Prüft, ob die Service Order ins Queueing wandert oder sofort ausgeführt werden soll. Sofort ausgeführt wird sie
     * für: <lu> <li> Service Order Typ 'UpdateMDU' </lu>
     */
    boolean checkSuspendSeq(CPSTransaction cpsTx) {
        return CPSTransaction.SERVICE_ORDER_TYPE_UPDATE_MDU.equals(cpsTx.getServiceOrderType())
                || CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE.equals(cpsTx.getServiceOrderType());
    }

    /**
     * Generiert ein ServiceRequest-Objekt fuer eine CPS-Transaction.
     *
     * @param cpsTransaction  CPS-Transaction fuer die ein ServiceRequest generiert werden soll
     * @param applicationKeys Angabe von ApplicationKeys fuer den ServiceRequest
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     * @throws XmlException
     * @throws HurricanServiceCommandException
     */
    protected ServiceRequest buildRequestPayload(CPSTransaction cpsTransaction, ApplicationKeys applicationKeys)
            throws XmlException, HurricanServiceCommandException, ServiceNotFoundException, FindException {
        if (cpsTransaction == null) {
            throw new HurricanServiceCommandException("CPS transaction to build request payload is NULL!");
        }

        boolean suspendSeq = checkSuspendSeq(cpsTransaction);       // SuspendSeq
        boolean recovery = Boolean.FALSE;                           // Recovery
        byte[] soData = cpsTransaction.getServiceOrderData();       // SOData

        ReferenceService rs = getCCService(ReferenceService.class);
        Reference prioRef = rs.findReference(cpsTransaction.getServiceOrderPrio());

        ServiceRequestDocument serviceRequestDocument = ServiceRequestDocument.Factory.newInstance();
        ServiceRequest serviceRequest = serviceRequestDocument.addNewServiceRequest();

        serviceRequest.setServiceOrderType(getServiceOrderTypeString(cpsTransaction.getServiceOrderType()));
        serviceRequest.setTransactionId(cpsTransaction.getId().toString());
        serviceRequest.setApplicationKeys(applicationKeys);
        serviceRequest.setPriority((prioRef != null) ? prioRef.getIntValue() : Integer.valueOf(5));
        serviceRequest.setSuspendSeq(suspendSeq);
        serviceRequest.setRecovery(recovery);
        serviceRequest.setSOData(new String(soData, StringTools.CC_DEFAULT_CHARSET));

        return serviceRequest;
    }

    /**
     * Ermittelt den ServiceOrder-Type.
     *
     * @param serviceOrderTypeId
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    protected String getServiceOrderTypeString(Long serviceOrderTypeId) throws ServiceNotFoundException, FindException {
        ReferenceService refs = getCCService(ReferenceService.class);
        Reference reqTypeRef = refs.findReference(serviceOrderTypeId);
        return reqTypeRef.getStrValue();
    }

    /*
     * Parst die CPS ServiceResponse.SOData und erstellt ein entsprechendes
     * Java-Modell.
     * @param serviceResp ServiceResponse Objekt vom CPS, dass die SOData enthaelt.
     * @return SOData als Java-Modell
     * @throws HurricanServiceCommandException
     */
    protected CPSServiceResponseSOData parseServiceResponseSOData(ServiceResponse2 serviceResp)
            throws HurricanServiceCommandException {
        try {
            // SOData aus ServiceResponse auslesen
            Node node = serviceResp.getSOResponseData().getDomNode().getFirstChild();
            XStream xstream = ((XStreamMarshaller) getXmlMarshaller()).getXStream();
            CPSServiceResponseSOData soData = (CPSServiceResponseSOData) xstream.fromXML(node.getNodeValue());
            if (soData == null) {
                throw new HurricanServiceCommandException("SOData of ServiceResponse not found!");
            }

            // COMMENT + ERROR enthalten selbst XML-Elemente
            //  --> unmarshall ermittelt deshalb diese Inhalte nicht
            //  --> Werte selbst aus String auslesen!
            if (StringUtils.isBlank(soData.getComment()) && StringUtils.isBlank(soData.getErrorMessage())) {
                String cpsResult = serviceResp.getSOResponseData().toString();
                if (cpsResult != null) {
                    String comment = StringUtils.substringBetween(cpsResult, "<COMMENT>", "</COMMENT>");
                    String error = StringUtils.substringBetween(cpsResult, "<ERROR_MSG>", "</ERROR_MSG>");
                    soData.setComment(comment);
                    soData.setErrorMessage(error);
                }
            }

            return soData;
        }
        catch (StreamException e) {
            LOGGER.error(e.getMessage(), e);
            CPSServiceResponseSOData soData = new CPSServiceResponseSOData();
            if (serviceResp.getSOResponseData() != null) {
                soData.setErrorMessage(serviceResp.getSOResponseData().toString());
            }
            else {
                soData.setErrorMessage("Unable to parse ServiceResponse! Error: " + ExceptionUtils.getFullStackTrace(e));
            }
            return soData;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(
                    "Error parsing CPS ServiceResponse.SOData: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt das Produkt zu dem angegebenen Auftrag.
     *
     * @param auftragId
     * @return
     * @throws ServiceCommandException
     */
    protected Produkt getProdukt4Auftrag(Long auftragId) throws ServiceCommandException {
        try {
            return produktService.findProdukt4Auftrag(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error loading product for order! " + e.getMessage(), e);
        }
    }

    /* Generiert einen CPS Application key mit angegebenen Name/Value Pair */
    ApplicationKey createCPSApplicationKey(String name, String value) {
        ApplicationKey appKey = ApplicationKey.Factory.newInstance();
        appKey.setName(name);
        appKey.setValue(value);
        return appKey;
    }

    /*
     * Generiert aus den angegebenen ApplicationKeys ein CPS ApplicationKeys Objekt.
     */
    ApplicationKeys createCPSApplicationKeys(List<ApplicationKey> keys) {
        ApplicationKeys applicationKeys = ApplicationKeys.Factory.newInstance();
        if (keys != null) {
            applicationKeys.setApplicationKeyArray(keys.toArray(new ApplicationKey[keys.size()]));
        }
        return applicationKeys;
    }

    /*
     * Ermittelt aus dem SOResult Key die zugehoerige Error-Message
     * @param cpsSOResult
     * @return
     */
    String convertSOResult2Message(int cpsSOResult) {
        String message = CPS_ERROR_CODES_RESOURCE_READER.getValue("" + cpsSOResult);
        return (StringUtils.isNotBlank(message)) ? message : "" + cpsSOResult;
    }

    /**
     * @return the cpsWebServiceTemplate
     */
    public MnetWebServiceTemplate getCpsWebServiceTemplate() {
        return cpsWebServiceTemplate;
    }

    /**
     * @param cpsWebServiceTemplate the cpsWebServiceTemplate to set
     */
    public void setCpsWebServiceTemplate(MnetWebServiceTemplate cpsWebServiceTemplate) {
        this.cpsWebServiceTemplate = cpsWebServiceTemplate;
    }

    /**
     * @return the cpsTransactionDAO
     */
    public CPSTransactionDAO getCpsTransactionDAO() {
        return cpsTransactionDAO;
    }

    /**
     * @param cpsTransactionDAO the cpsTransactionDAO to set
     */
    public void setCpsTransactionDAO(CPSTransactionDAO cpsTransactionDAO) {
        this.cpsTransactionDAO = cpsTransactionDAO;
    }

    /**
     * Injected
     */
    public AbstractMarshaller getXmlMarshaller() {
        return xmlMarshaller;
    }

    /**
     * Injected
     */
    public void setXmlMarshaller(AbstractMarshaller xmlMarshaller) {
        this.xmlMarshaller = xmlMarshaller;
    }

    /**
     * Injected
     */
    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

    /**
     * Injected
     */
    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    /**
     * Injected
     */
    public void setQueryCCService(QueryCCService queryCCService) {
        this.queryCCService = queryCCService;
    }

    /**
     * Injected
     */
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Pr&uuml;ft, ob es sich um ein Wholesale-Produkt handelt und setzt entsprechend relevante Werte (EKP,
     * Taifunnummer, Line ID und Port ID).
     *
     * @param serviceOrderData das {@link CPSServiceOrderData}-Objekt, das die Werte enth&auml;lt.
     * @param auftragId        die Auftrag ID.
     * @param when             das Ausf&uuml;hrungsdatum
     * @throws ServiceNotFoundException wenn ein Service nicht verf&uuml;gbar ist.
     * @throws FindException            Wenn eine Suchanfrage fehlschl&auml;gt.
     */
    void collectWholesaleProductData(final CPSServiceOrderData serviceOrderData, final Long auftragId, final Date when)
            throws ServiceNotFoundException, FindException {
        final CCAuftragService auftragService = getCCService(CCAuftragService.class);
        final AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (!isWholesaleProduct(auftragDaten)) {
            return;
        }
        final AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
        final String lineId = getLineId(auftragTechnik);
        final String ekpId = getEkpId(auftragId, when);

        serviceOrderData.setEkpId(ekpId);
        serviceOrderData.setTaifunnumber(lineId);
        serviceOrderData.setVerbindungsbezeichnung(lineId);
        if (serviceOrderData.getFttb() != null) {
            serviceOrderData.getFttb().setPortId(lineId);
        }
        fillCpsItem(serviceOrderData, lineId);
    }

    private boolean isWholesaleProduct(final AuftragDaten auftragDaten) {
        return auftragDaten != null && WholesaleProductName.existsWithProductId(auftragDaten.getProdId());
    }

    private void fillCpsItem(final CPSServiceOrderData serviceOrderData, final String lineId) {
        if (serviceOrderData.getAccessDevice() == null || serviceOrderData.getAccessDevice().getItems() == null) {
            return;
        }
        final CpsItem cpsItem = serviceOrderData.getAccessDevice().getItems().get(0);
        if (cpsItem.getEndpointDevice() != null) {
            cpsItem.getEndpointDevice().setPortName(lineId);
        }
    }

    private String getLineId(final AuftragTechnik auftragTechnik) throws ServiceNotFoundException, FindException {
        VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());
        String lineId = (vbz != null) ? vbz.getVbz() : null;
        if (lineId == null) {
            throw new FindException("Unable to get lineId");
        }
        return lineId;
    }

    private Auftrag2EkpFrameContract getAuftrag2EkpFrameContract(final Long auftragId, final Date when)
            throws ServiceNotFoundException {
        final EkpFrameContractService ekpFrameContractService = getCCService(EkpFrameContractService.class);
        return ekpFrameContractService.findAuftrag2EkpFrameContract(auftragId, DateConverterUtils.asLocalDate(when));
    }

    private String getEkpId(final Long auftragId, final Date when) throws ServiceNotFoundException, FindException {
        final Auftrag2EkpFrameContract auftrag2EkpFrameContract = getAuftrag2EkpFrameContract(auftragId, when);
        if (auftrag2EkpFrameContract == null) {
            throw new FindException("Auftrag2EkpFrameContract could not be found!");
        }
        final EkpFrameContract ekpFrameContract = auftrag2EkpFrameContract.getEkpFrameContract();
        if (ekpFrameContract == null) {
            throw new FindException("EKP frame contract could not be found!");
        }
        return ekpFrameContract.getEkpId();
    }

}


