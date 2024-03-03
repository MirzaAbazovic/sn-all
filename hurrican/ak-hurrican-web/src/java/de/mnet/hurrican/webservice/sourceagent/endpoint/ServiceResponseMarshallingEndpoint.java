/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.mnet.hurrican.webservice.sourceagent.endpoint;

import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponseAcknowledgement;
import com.evolving.wsdl.sa.v1.types.ServiceResponseAcknowledgementDocument;
import com.evolving.wsdl.sa.v1.types.ServiceResponseDocument;
import com.evolving.wsdl.sa.v1.types.ServiceResponseFault;
import com.evolving.wsdl.sa.v1.types.ServiceResponseFaultDocument;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EvnService;
import de.augustakom.hurrican.service.cc.HVTToolService;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;
import de.mnet.hurrican.webservice.resource.inventory.command.CommandResourceInventoryWebserviceClient;

/**
 * WS-Endpoint, um Responses vom CPS entgegen zu nehmen. <br> Aus dem Response-Objekt wird die Transaction-ID ermittelt
 * und die zugehoerige CPS-Tx abgeschlossen. <br> Kann die CPS-Tx nicht erfolgreich abgeschlossen werden, wird ein
 * Fault-Objekt zurueck geliefert, sonst ein Acknowledge-Objekt.
 *
 *
 */
public class ServiceResponseMarshallingEndpoint extends MnetAbstractMarshallingPayloadEndpoint implements SourceAgentEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ServiceResponseMarshallingEndpoint.class);

    private CommandResourceInventoryWebserviceClient resourceInventoryWsClient;

    /**
     * @see de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        Object endpointResult = null;

        ServiceResponseDocument serviceResponseDocument = (ServiceResponseDocument) requestObject;
        ServiceResponse serviceResponse = serviceResponseDocument.getServiceResponse();

        String transactionId = serviceResponse.getServiceResponse().getTransactionId();
        try {
            if (NumberUtils.isNumber(transactionId)) {

                CPSService cps = getCCService(CPSService.class);
                Long txId = Long.valueOf(transactionId);

                CPSTransaction cpsTx = cps.findCPSTransactionById(txId);

                //Notwendig, da der CPS eine asynchrone Antwort schickt, bevor Hurrican es schafft einen commit durchzuführen.
                //Folge: NPE weil cps.findCPSTransactionById(txId) die CpsTx noch nicht finden kann.
                //Eine Loesung ueber eine modifizierte execution_time ist nicht praktikabel, da diese in bestimmten Faellen
                //vom CPS ignoriert wird (z.B. Validierungsfehler). Teilt man das erzeugen/senden/nachbearbeiten in
                // unterschiedliche (DB-)Transaktionen auf, kann dies im worste case zu inkosistentem Datenbestand fuehren
                //(z.B. asynchrone Antwort ueberholt synchronen response -> status wird zuerst auf success,
                // dann auf in preparing geaendert
                if(cpsTx == null)   {
                    int waitSeconds = (System.getProperty("cps.asyncresponse.wait.seconds") != null)
                            ? Integer.parseInt(System.getProperty("cps.asyncresponse.wait.seconds")) : 3;
                    Thread.sleep(waitSeconds * 1000); //dreckiger Hack ;-)
                    cpsTx = cps.findCPSTransactionById(txId);
                }

                boolean isCommandFreigabeNecessary = check4CommandFreigabe(cpsTx);
                Boolean success = cps.finishCPSTransaction(txId, serviceResponseDocument, getSessionId());
                if (isCommandFreigabeNecessary && BooleanTools.nullToFalse(success)) {
                    sendCommandFreigabe(cps.findCPSTransactionById(txId));
                }

                // EVN Änderung abschließen, wenn nötig
                completeEvnChange(cpsTx);

                // Acknowledge-Objekt generieren
                LOGGER.info("sending ServiceResponseAcknowledgement for TxId " + transactionId);
                ServiceResponseAcknowledgementDocument serviceResponseAcknowledgementDocument =
                        ServiceResponseAcknowledgementDocument.Factory.newInstance();
                ServiceResponseAcknowledgement serviceResponseAcknowledgement =
                        serviceResponseAcknowledgementDocument.addNewServiceResponseAcknowledgement();
                serviceResponseAcknowledgement.setTransactionId(transactionId);
                endpointResult = serviceResponseAcknowledgement;
            }
            else {
                throw new Exception("Transaction-ID is not a valid number. ID: " + transactionId);
            }
        }
        catch (Exception e) {
            LOGGER.error("finishCPSTransaction failed", e);
            LOGGER.info("sending ServiceResponseFault for TxId " + transactionId);

            // Fault-Objekt generieren
            ServiceResponseFaultDocument serviceResponseFaultDocument =
                    ServiceResponseFaultDocument.Factory.newInstance();
            ServiceResponseFault serviceResponseFault =
                    serviceResponseFaultDocument.addNewServiceResponseFault();

            serviceResponseFault.setTransactionId(transactionId);
            serviceResponseFault.setCode("error");
            serviceResponseFault.setDescription(e.getMessage());
            endpointResult = serviceResponseFault;
        }

        return endpointResult;
    }

    private void completeEvnChange(CPSTransaction cpsTx) throws Exception {
        final EvnService evnService = getCCService(EvnService.class);
        evnService.completeEvnChange(cpsTx.getAuftragId());
    }

    private boolean check4CommandFreigabe(CPSTransaction cpsTx) throws Exception {
        if (cpsTx.getHwRackId() != null && ArrayUtils.contains(new Long[] {
                CPSTransaction.TX_SOURCE_HURRICAN_MDU,
                CPSTransaction.TX_SOURCE_HURRICAN_DPU,
                CPSTransaction.TX_SOURCE_HURRICAN_ONT,
                CPSTransaction.TX_SOURCE_HURRICAN_DPO }, cpsTx.getTxSource())) {
            return ((HWOltChild) getCCService(HWService.class).findRackById(cpsTx.getHwRackId())).getFreigabe() == null;
        }
        return false;
    }

    // @formatter:off
    /**
     * Die erfolgreiche Beendigung einer CPS Transaktion - wenn notwendig - an Command Systeme melden. Akt. fuer
     * folgende Faelle.
     * <p/>
     * <ul> 
     *  <li>INIT_MDU => Freigabe an Command senden</li> 
     *  <li>CREATE_HARDWARE ONT / DPO / DPU => updateResource an Command senden</li>
     * </ul>
     *
     * @param cpsTx die CPS Tx
     * @throws Exception
     */
    // @formatter:on
    protected void sendCommandFreigabe(CPSTransaction cpsTx) throws Exception {
        final HWService hwService = getCCService(HWService.class);
        final HWOltChild oltChild = (HWOltChild) hwService.findRackById(cpsTx.getHwRackId());
        if (oltChild == null) {
            String typ = "unbekannt";
            if(CPSTransaction.TX_SOURCE_HURRICAN_DPO.equals(cpsTx.getTxSource())) {
                typ = "DPO";
            } else if (CPSTransaction.TX_SOURCE_HURRICAN_DPU.equals(cpsTx.getTxSource())) {
                typ = "DPU";
            } else if(CPSTransaction.TX_SOURCE_HURRICAN_MDU.equals(cpsTx.getTxSource())) {
                typ = "MDU";
            } else if(CPSTransaction.TX_SOURCE_HURRICAN_ONT.equals(cpsTx.getTxSource())) {
                typ = "ONT";
            }
            throw new FindException(String.format("HW-Rack (%s) mit der Id %d wurde nicht gefunden.",
                    typ,
                    cpsTx.getHwRackId()));
        }

        final boolean isMduInit = NumberTools.equal(cpsTx.getTxSource(), CPSTransaction.TX_SOURCE_HURRICAN_MDU)
                && NumberTools.equal(cpsTx.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU);

        final boolean isOntDpoDpuCreateDevice = ArrayUtils.contains(new Long[] { CPSTransaction.TX_SOURCE_HURRICAN_ONT,
                CPSTransaction.TX_SOURCE_HURRICAN_DPO, CPSTransaction.TX_SOURCE_HURRICAN_DPU }, cpsTx.getTxSource())
                && NumberTools.equal(cpsTx.getServiceOrderType(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);

        if (isMduInit) {
            final Date freigabe = oltChild.getFreigabe();
            getCCService(HVTToolService.class).activateMDU(oltChild.getGeraeteBez(), freigabe);
        }
        else if (isOntDpoDpuCreateDevice) {
            resourceInventoryWsClient.updateResource(oltChild);
        }
    }

    public void setResourceInventoryWsClient(CommandResourceInventoryWebserviceClient resourceInventoryWsClient) {
        this.resourceInventoryWsClient = resourceInventoryWsClient;
    }
}
