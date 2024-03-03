package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceRequestAcknowledgement;
import com.evolving.wsdl.sa.v1.types.ServiceRequestFault;
import com.evolving.wsdl.sa.v1.types.ServiceRequestResponse;
import com.evolving.wsdl.sa.v1.types.ServiceRequestResponseDocument;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.dao.hibernate.HibernateSessionHelper;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Command-Klasse, um eine CPS-Transaction an den CPS-Server per WebService zu uebermitteln, welcher die Tx asynchron
 * verarbeitet.
 */
public class CPSSendAsyncTxCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSSendAsyncTxCommand.class);

    public static final String KEY_NEW_TRANSACTION_4_CPS_TX_LOG = "new.tx";

    CPSTransaction cpsTx = null;
    @Autowired
    private ServiceLocator serviceLocator;

    private boolean newTx4Log = true;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        cpsTx = (CPSTransaction) getPreparedValue(KEY_CPS_TX);
        if (getPreparedValue(KEY_NEW_TRANSACTION_4_CPS_TX_LOG, Boolean.class, true, "") != null) {
            newTx4Log = getPreparedValue(KEY_NEW_TRANSACTION_4_CPS_TX_LOG, Boolean.class, true, "");
        }

        validate();

        try {
            cpsTx.setRequestAt(new Date());

            ServiceRequest serviceRequest = buildRequestPayload();
            WebServiceTemplate wsTemplate = configureAndGetWSTemplate();

            LOGGER.info("Calling CPS WebService to send Tx - CPS-Tx ID: " + cpsTx.getId());
            Object responsePayLoad = wsTemplate.marshalSendAndReceive(serviceRequest);
            LOGGER.info("WebService call done");

            LOGGER.info("Begin reading WebService call result...");
            ServiceRequestResponse srr =
                    ((ServiceRequestResponseDocument) responsePayLoad).getServiceRequestResponse();

            if (srr.getServiceRequestAcknowledgement() != null) {
                ServiceRequestAcknowledgement ack = srr.getServiceRequestAcknowledgement();
                if (StringUtils.equals("" + cpsTx.getId(), ack.getTransactionId())) {
                    cpsTx.setTxState(CPSTransaction.TX_STATE_IN_PROVISIONING);
                    createCPSTxLog(cpsTx, "CPS-Tx successfully sent to CPS", newTx4Log);
                }
                else {
                    String msg = "Acknowledged Transaction-ID is different. Expected: " + cpsTx.getId() +
                            "; Returned: " + ack.getTransactionId();
                    createCPSTxLog(cpsTx, msg, newTx4Log);
                    throw new HurricanServiceCommandException(msg);
                }
            }
            else if (srr.getServiceRequestFault() != null) {
                cpsTx.setTxState(CPSTransaction.TX_STATE_FAILURE);
                ServiceRequestFault fault = srr.getServiceRequestFault();

                String msg = "ServiceRequestFault: " + fault.getCode() + " - " + fault.getDescription();
                createCPSTxLog(cpsTx, msg, newTx4Log);
                throw new HurricanServiceCommandException(msg);
            }
            else {
                cpsTx.setTxState(CPSTransaction.TX_STATE_FAILURE);
                String msg = "Result from CPS - expected: ServiceRequestAck/Fault, but is: "
                        + responsePayLoad.getClass();
                createCPSTxLog(cpsTx, msg, newTx4Log);
                throw new HurricanServiceCommandException(msg);
            }
        }
        catch (WebServiceIOException e) {
            LOGGER.error(e.getMessage(), e);
            cpsTx.setTxState(CPSTransaction.TX_STATE_TRANSMISSION_FAILURE);
            String msg = "IO-Error sending Tx to CPS: " + e.getMessage();
            createCPSTxLog(cpsTx, msg, newTx4Log);
            throw new HurricanServiceCommandException(msg, e);
        }
        catch (HurricanServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            cpsTx.setTxState(CPSTransaction.TX_STATE_TRANSMISSION_FAILURE);
            createCPSTxLog(cpsTx, e.getMessage(), newTx4Log);
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            cpsTx.setTxState(CPSTransaction.TX_STATE_TRANSMISSION_FAILURE);
            String msg = "Unknown error while sending Tx to CPS: " + e.getMessage();
            createCPSTxLog(cpsTx, msg, newTx4Log);
            throw new HurricanServiceCommandException(msg, e);
        }
        finally {
            if (newTx4Log) {
                updateCPSTxWithNewTx(cpsTx, getSessionId());
                HibernateSessionHelper.evictObject(serviceLocator, cpsTx);
            }
            else {
                updateCPSTxInExistingTransaction();
            }
        }

        return null;
    }

    private void updateCPSTxInExistingTransaction() {
        try {
            cpsService.saveCPSTransaction(cpsTx, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /*
     * @throws HurricanServiceCommandException
     */
    private void validate() throws HurricanServiceCommandException {
        if (null == cpsTx) {
            throw new HurricanServiceCommandException("CPSTransaction NOT SET!!!");
        }

        if (null == cpsTx.getId()) {
            throw new HurricanServiceCommandException("CPSTransaction ID NOT SET!!!");
        }

        if (null == cpsTx.getServiceOrderPrio()) {
            throw new HurricanServiceCommandException("CPSTransaction ServiceOrderPrio NOT SET!!!");
        }
    }

    /*
     * Erzeugt einen ServiceRequest
     */
    protected ServiceRequest buildRequestPayload() throws FindException,
            ServiceNotFoundException, XmlException, HurricanServiceCommandException {
        List<ApplicationKey> appKeys = new ArrayList<ApplicationKey>();
        String appKeyValue = null;

        if (ArrayUtils.contains(new Long[] {
                CPSTransaction.TX_SOURCE_HURRICAN_MDU,
                CPSTransaction.TX_SOURCE_HURRICAN_DPU,
                CPSTransaction.TX_SOURCE_HURRICAN_ONT,
                CPSTransaction.TX_SOURCE_HURRICAN_DPO}, cpsTx.getTxSource())) {
            HWOltChild oltChild = (HWOltChild) hwService.findRackById(cpsTx.getHwRackId());
            if (oltChild == null) {
                throw new HurricanServiceCommandException("Fehler beim Laden des OltChild-Objekts mit id=" + cpsTx.getHwRackId());
            }
            appKeyValue = oltChild.getGeraeteBez();
        }
        else {
            final AuftragDaten auftragDaten = getAuftragDatenTx(cpsTx.getAuftragId());
            appKeyValue = (auftragDaten != null && auftragDaten.isWholesaleAuftrag())
                ? physikService.findVerbindungsBezeichnungByAuftragIdTx(cpsTx.getAuftragId()).getVbz()
                : cpsTx.getOrderNoOrig().toString();
        }
        ApplicationKey appKeyTaifun = createCPSApplicationKey(CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER,
                (appKeyValue != null) ? appKeyValue : "");
        appKeys.add(appKeyTaifun);

        if (cpsTx.getServiceOrderStackSeq() != null) {
            ApplicationKey appKeyStackSeq = ApplicationKey.Factory.newInstance();
            appKeyStackSeq.setName(CPSTransaction.CPS_APPLICATION_KEY_STACK_SEQUENCE);
            appKeyStackSeq.setValue(cpsTx.getServiceOrderStackSeq().toString());
            appKeys.add(appKeyStackSeq);
        }

        return super.buildRequestPayload(cpsTx, createCPSApplicationKeys(appKeys));
    }

    /**
     * @return the cpsTx
     */
    public CPSTransaction getCpsTx() {
        return cpsTx;
    }

    /**
     * @param cpsTx the cpsTx to set
     */
    public void setCpsTx(CPSTransaction cpsTx) {
        this.cpsTx = cpsTx;
    }
}
