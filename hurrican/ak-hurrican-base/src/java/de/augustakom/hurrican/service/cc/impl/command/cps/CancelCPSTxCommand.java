/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2009 14:24:27
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlTokenSource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSCancelTransactionRequestData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSCancelTransactionResponseData;
import de.augustakom.hurrican.service.base.exceptions.CommunicationException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechInternPredicate;


/**
 * Command-Klasse, um eine CPS-Tx zu stornieren. <br> Dies darf nur erfolgen, so lange die Transaction vom CPS noch
 * nicht bearbeitet wurde; also nur, wenn sich der Status auf PREPARING oder IN_PROVISIONING befindet. <br> Der Status
 * der CPS-Transaction wird dabei auf 'CANCELLED' gesetzt. <br> Evtl. vorhandene Referenzen auf die CPS-Transaction
 * (z.B. vom Bauauftrag oder DN-Leistung) werden hierbei entfernt. <br> Im Falle von TX_SOURCE=HURRICAN_VERLAUF wird
 * auch der Status der VerlaufAbteilungs-Datensaetze zurueck gesetzt (auf Wert 1)!
 *
 *
 */
public class CancelCPSTxCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CancelCPSTxCommand.class);

    private CPSTransaction cpsTxToCancel = null;
    private BAService baService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            loadRequiredData();

            if (NumberTools.isNotIn(cpsTxToCancel.getTxState(),
                    new Number[] { CPSTransaction.TX_STATE_IN_PREPARING, CPSTransaction.TX_STATE_IN_PROVISIONING })) {
                throw new HurricanServiceCommandException("Cancel of CPS-Tx not allowed at actual state!");
            }

            boolean cpsCancelAccepted = true;
            if (NumberTools.isGreater(cpsTxToCancel.getTxState(), CPSTransaction.TX_STATE_IN_PREPARING)) {
                // CPS aufrufen und Transaction stornieren
                cpsCancelAccepted = sendCancelToCPS();
            }

            if (cpsCancelAccepted) {
                // Disjoin aufrufen
                cpsService.disjoinCPSTransaction(getCPSTxId(), getSessionId());

                if (cpsTxToCancel.getVerlaufId() != null) {
                    // Verlaufs-Status zurueck setzen
                    modifyProvisioningOrder();
                }

                // Status der CPS-Tx auf 'cancelled' setzen
                changeCPSTxState();
            }
            else {
                throw new HurricanServiceCommandException(
                        "Cancel not accepted by CPS! (See CPS-Log for details)");
            }

            return null;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error during cancel of CPS Transaction: " + e.getMessage(), e);
        }
    }

    /*
     * Uebermittelt den Storno an den CPS-Server. <br>
     * Fault-Responses werden ueber CPSTransactionLog protokolliert.
     * @return true, wenn der CPS den Storno akzeptiert hat.
     * @throws CommunicationException
     */
    private boolean sendCancelToCPS() throws CommunicationException {
        CPSTransaction cpsCancelTx = null;
        try {
            cpsCancelTx = buildAndSaveCancelTx();

            ApplicationKey appKeyTaifun = createCPSApplicationKey(
                    CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER, "" + cpsCancelTx.getOrderNoOrig());
            List<ApplicationKey> appKeys = new ArrayList<ApplicationKey>();
            appKeys.add(appKeyTaifun);

            ServiceRequest serviceRequest = buildRequestPayload(cpsCancelTx, createCPSApplicationKeys(appKeys));
            serviceRequest.setSuspendSeq(true);
            WebServiceTemplate wsTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_CPS_SOURCE_AGENT_SYNC);
            Object responsePayLoad = wsTemplate.marshalSendAndReceive(serviceRequest);

            XmlTokenSource xmlObject = (XmlTokenSource) responsePayLoad;
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            XmlTokenSource o = cursor.getObject();

            ServiceResponse serviceResponse = ServiceResponse.Factory.parse(o.xmlText());
            if (serviceResponse != null) {
                ServiceResponse2 serviceResponse2 = serviceResponse.getServiceResponse();
                if (serviceResponse.getServiceResponse().getSOResponseData() != null) {
                    // SOResponseData in CPS-Tx eintragen
                    cpsCancelTx.setResponseData(serviceResponse2.toString().getBytes(StringTools.CC_DEFAULT_CHARSET));

                    // Result analysieren und Status entsprechend setzen
                    if (serviceResponse2.getSOResult() == SERVICE_RESPONSE_SORESULT_CODE_SUCCESS) {
                        cpsCancelTx.setTxState(CPSTransaction.TX_STATE_CANCELLED);
                        createCPSTxLog(cpsTxToCancel, "cancel successful");
                        return true;
                    }
                    else {
                        cpsCancelTx.setTxState(CPSTransaction.TX_STATE_FAILURE_CLOSED);
                        createCPSTxLog(cpsTxToCancel, "unable to cancel Transaction!");
                        createCPSTxLog(cpsCancelTx, convertSOResult2Message(serviceResponse2.getSOResult()));

                        CPSCancelTransactionResponseData soData = parseCancelServiceResponseSOData(serviceResponse2);
                        if (soData != null) {
                            createCPSTxLog(cpsCancelTx, soData.getMessage());
                        }

                        return false;
                    }
                }
            }
            else {
                createCPSTxLog(cpsCancelTx, "No ServiceResponse!");
            }

            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new CommunicationException("Error sending cancel command to CPS: " + e.getMessage(), e);
        }
    }

    /*
     * Liest die Result-Daten aus.
     */
    private CPSCancelTransactionResponseData parseCancelServiceResponseSOData(ServiceResponse2 serviceResponse) {
        // TODO parseCancelServiceResponseSOData
        return null;
    }

    /**
     * Erzeugt eine CPS-Transaktion für die Abfrage des Status
     *
     * @throws ServiceCommandException
     * @throws HurricanServiceCommandException
     * @throws StoreException
     */
    private CPSTransaction buildAndSaveCancelTx() throws ServiceCommandException, HurricanServiceCommandException, StoreException {
        CPSTransaction cpsCancelTx = new CPSTransaction();
        cpsCancelTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_TX);
        cpsCancelTx.setTxState(CPSTransaction.TX_STATE_IN_PREPARING);
        cpsCancelTx.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
        cpsCancelTx.setEstimatedExecTime(new Date());
        cpsCancelTx.setOrderNoOrig(cpsTxToCancel.getOrderNoOrig());
        cpsCancelTx.setAuftragId(cpsTxToCancel.getAuftragId());
        cpsCancelTx.setServiceOrderPrio(cpsTxToCancel.getServiceOrderPrio());

        CPSCancelTransactionRequestData soData = new CPSCancelTransactionRequestData();
        soData.setTransactionId(cpsTxToCancel.getId());

        String soDataAsXMLString = transformSOData2XML(soData, (XStreamMarshaller) getXmlMarshaller());

        if (StringUtils.isNotBlank(soDataAsXMLString)) {
            cpsCancelTx.setServiceOrderData(soDataAsXMLString.getBytes(StringTools.CC_DEFAULT_CHARSET));
        }
        else {
            throw new HurricanServiceCommandException("ServiceOrder-Data not defined!");
        }

        cpsService.saveCPSTransaction(cpsCancelTx, getSessionId());
        return cpsCancelTx;
    }

    /*
     * Setzt den Status der CPS-Tx auf 'cancelled'.
     * @throws ServiceCommandException
     */
    private void changeCPSTxState() throws ServiceCommandException {
        try {
            cpsTxToCancel.setTxState(CPSTransaction.TX_STATE_CANCELLED);
            cpsService.saveCPSTransaction(cpsTxToCancel, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error changing state of CPS-Tx to 'cancelled': " + e.getMessage(), e);
        }
    }

    /*
     * Setzt den Status des Provisionierungsauftrags (frueher: Verlauf/Bauauftrag)
     * zurueck. <br>
     * Dabei werden auch die Stati der einzelnen Abteilungsdatensaetze
     * zurueck gesetzt.
     */
    private void modifyProvisioningOrder() throws ServiceCommandException {
        if (cpsTxToCancel.getVerlaufId() == null) { return; }  // modify.. nur, wenn Verlaufs-ID gesetzt

        try {
            List<Long> verlaufIDs = cpsService.findVerlaufIDs4CPSTransaction(cpsTxToCancel);
            for (Long verlaufId : verlaufIDs) {
                // Abteilungsdatensaetze laden und Status auf 'im Umlauf' aendern
                List<VerlaufAbteilung> vas = baService.findVerlaufAbteilungen(verlaufId);
                if (CollectionTools.isNotEmpty(vas)) {
                    CollectionUtils.filter(vas, new VerlaufAbteilungTechInternPredicate());

                    for (VerlaufAbteilung va : vas) {
                        if (NumberTools.equal(va.getVerlaufStatusId(), VerlaufStatus.STATUS_CPS_BEARBEITUNG)) {
                            va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
                            baService.saveVerlaufAbteilung(va);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error modifying provsioning orders for CPS-Tx cancel: " + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            cpsTxToCancel = cpsService.findCPSTransactionById(getCPSTxId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    "Error loading default data for cancel CPS-Tx: " + e.getMessage(), e);
        }
    }

    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

}


