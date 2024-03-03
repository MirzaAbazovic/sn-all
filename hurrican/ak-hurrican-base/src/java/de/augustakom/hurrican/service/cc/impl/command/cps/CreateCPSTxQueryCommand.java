/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2009 15:05:54
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.io.*;
import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlTokenSource;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Node;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusRequestData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusResponseData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSGetServiceOrderStatusResponseLongDataConverter;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * Command-Klasse, um eine CPS-Transaction, die den Status einer CPS-Transaction ermittelt, anzulegen. <br>
 *
 *
 */
public class CreateCPSTxQueryCommand extends AbstractCPSCommand {
    private static final Logger LOGGER = Logger.getLogger(CreateCPSTxQueryCommand.class);

    /**
     * Key fuer die Uebergabe der zugehoerigen CPS-Transaction an das Command.
     */
    public static final String KEY_CPS_TRANSACTION = "cps.transaction";

    private CPSTransaction cpsTx = null;
    private CPSTransaction cpsQueryTx = null;

    private AbstractMarshaller xmlMarshaller = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSCommand#configureAndGetWSTemplate()
     */
    @Override
    protected WebServiceTemplate configureAndGetWSTemplate() throws ServiceCommandException {
        return configureAndGetWSTemplate(WebServiceConfig.WS_CFG_CPS_SOURCE_AGENT_SYNC);
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        try {
            init();
            buildAndSaveTx();

            ServiceRequest serviceRequest = buildRequestPayload(cpsQueryTx, cpsTx.getOrderNoOrig());
            WebServiceTemplate wsTemplate = configureAndGetWSTemplate();
            Object responsePayLoad = wsTemplate.marshalSendAndReceive(serviceRequest);

            /*
             * automatic unmarshalling not possible, because tertio sends the following message
             *
             *	<ns0:ServiceRequestResponseResponse xmlns:ns0="http://OutputMessageNamespace" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
             *		<ServiceResponse>
             *			<ns:TransactionId>1700885</ns:TransactionId>
             * 			<ns:SOResult>0</ns:SOResult>
             *			<ns:SOResponseData>&lt;?xml version="1.0" encoding="UTF8"?&gt;&lt;GETSOSTATUS_RESPONSE&gt;&lt;SO_STATUS&gt;&lt;/SO_STATUS&gt;&lt;SO_RESULT&gt;&lt;/SO_RESULT&gt;&lt;SO_RESPONSE&gt;TransactionID not in database!&lt;/SO_RESPONSE&gt;&lt;/GETSOSTATUS_RESPONSE&gt;</ns:SOResponseData>
             *		</ServiceResponse>
             * </ns0:ServiceRequestResponseResponse>
             *
             * this should be a "ServiceRequestResponse" and fixed from tertio.
             * see the WSDL for the right response!
             */

            XmlTokenSource xmlObject = (XmlTokenSource) responsePayLoad;
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            XmlTokenSource o = cursor.getObject();

            ServiceResponse serviceResponse = ServiceResponse.Factory.parse(o.xmlText());
            ServiceResponse2 serviceResponse2 = serviceResponse.getServiceResponse();

            CPSGetServiceOrderStatusResponseData soData = null;
            if (serviceResponse.getServiceResponse().getSOResponseData() != null) {
                // SOResponseData in CPS-Tx eintragen
                cpsQueryTx.setResponseData(serviceResponse2.toString().getBytes(StringTools.CC_DEFAULT_CHARSET));
                soData = parseServiceResponseSOData2GetSOStatus(serviceResponse2);
            }

            saveQueryCPSTx();
            return soData;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error during CPS-Transaction query: " + e.getMessage(), e);
        }
    }


    /*
     * Erzeugt einen ServiceRequest
     */
    protected ServiceRequest buildRequestPayload(CPSTransaction cpsTx, Object value) throws FindException,
            ServiceNotFoundException, XmlException, HurricanServiceCommandException {
        ApplicationKey applicationKey = ApplicationKey.Factory.newInstance();
        applicationKey.setName(CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER);
        applicationKey.setValue("" + value);
        ApplicationKey[] applicationKeysArray = new ApplicationKey[1];
        applicationKeysArray[0] = applicationKey;

        ApplicationKeys applicationKeys = ApplicationKeys.Factory.newInstance();
        applicationKeys.setApplicationKeyArray(applicationKeysArray);

        return buildRequestPayload(cpsTx, applicationKeys);
    }


    /*
     * Parst die CPS ServiceResponse.SOData und erstellt ein entsprechendes
     * Java-Modell.
     * @param serviceResp ServiceResponse Objekt vom CPS, dass die SOData enthaelt.
     * @return SOData als Java-Modell
     * @throws HurricanServiceCommandException
     */
    private CPSGetServiceOrderStatusResponseData parseServiceResponseSOData2GetSOStatus(ServiceResponse2 serviceResp)
            throws HurricanServiceCommandException {
        try {
            // SOData aus ServiceResponse auslesen
            Node node = serviceResp.getSOResponseData().getDomNode().getFirstChild();
            XStream xstream = ((XStreamMarshaller) getXmlMarshaller()).getXStream();
            xstream.processAnnotations(CPSGetServiceOrderStatusResponseLongDataConverter.class);
            CPSGetServiceOrderStatusResponseData soData = (CPSGetServiceOrderStatusResponseData) xstream.fromXML(node.getNodeValue());
            if (soData == null) {
                throw new HurricanServiceCommandException("SOData of ServiceResponse not found!");
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
     * Erzeugt eine CPS-Transaktion für die Abfrage des Status
     *
     * @throws ServiceCommandException
     * @throws HurricanServiceCommandException
     */
    private void buildAndSaveTx() throws ServiceCommandException, HurricanServiceCommandException {
        this.cpsQueryTx = new CPSTransaction();
        this.cpsQueryTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_QUERY);
        this.cpsQueryTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
        this.cpsQueryTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
        this.cpsQueryTx.setEstimatedExecTime(new Date());
        this.cpsQueryTx.setOrderNoOrig(cpsTx.getOrderNoOrig());
        this.cpsQueryTx.setAuftragId(cpsTx.getAuftragId());
        this.cpsQueryTx.setServiceOrderPrio(cpsTx.getServiceOrderPrio());

        CPSGetServiceOrderStatusRequestData soData = new CPSGetServiceOrderStatusRequestData();
        soData.setTransactionId(cpsTx.getId());

        String soDataAsXMLString = transformSOData2XML(soData);

        if (StringUtils.isNotBlank(soDataAsXMLString)) {
            cpsQueryTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
        }
        else {
            throw new HurricanServiceCommandException("ServiceOrder-Data not defined!");
        }
        saveQueryCPSTx();
    }

    /*
     * Generiert aus dem uebergebenen Objekt eine XML-Struktur und gibt
     * diese in Form eines Strings zurueck.
     * @param soData zu transformierendes Objekt
     * @return XML-String aus dem angegebenen Objekt
     */
    private String transformSOData2XML(CPSGetServiceOrderStatusRequestData soData) throws ServiceCommandException {
        try {
            if (soData == null) {
                throw new HurricanServiceCommandException("ServiceOrderData object is null!");
            }

            // XML ueber XStream generieren
            XStream xstream = ((XStreamMarshaller) getXmlMarshaller()).getXStream();
            StringWriter stringWriter = new StringWriter();
            PrettyPrintWriter writer = new PrettyPrintWriter(stringWriter, new XmlFriendlyReplacer("_", "_"));
            xstream.marshal(soData, writer);
            String xml = stringWriter.toString();

            return xml;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error creating XML-Stream for ServiceOrder-Data: " + e.getMessage(), e);
        }
    }

    /* Initialisiert das Command. */
    private void init() throws Exception {
        checkValues();
    }

    /* Speichert die aktuelle CPS-Transaction. */
    private void saveQueryCPSTx() throws ServiceCommandException {
        try {
            cpsService.saveCPSTransaction(cpsQueryTx, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error saving CPS-Transaction: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft die uebergebenen Daten. */
    private void checkValues() throws FindException {
        cpsTx = getPreparedValue(KEY_CPS_TRANSACTION, CPSTransaction.class, false,
                "CPS-Transaction object is not defined for command!");
    }

    /**
     * @return the xmlMarshaller
     */
    @Override
    public AbstractMarshaller getXmlMarshaller() {
        return xmlMarshaller;
    }

    /**
     * @param xmlMarshaller the xmlMarshaller to set
     */
    @Override
    public void setXmlMarshaller(AbstractMarshaller xmlMarshaller) {
        this.xmlMarshaller = xmlMarshaller;
    }
}
