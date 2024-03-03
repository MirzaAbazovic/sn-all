/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2012 15:24:56
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys;
import com.evolving.wsdl.sa.v1.types.ServiceRequest.ApplicationKeys.ApplicationKey;
import com.evolving.wsdl.sa.v1.types.ServiceResponse;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlTokenSource;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceResponseSOData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * Command-Klasse, um eine CPS-Transaction an den CPS-Server per WebService zu uebermitteln, welcher die Tx synchron
 * verarbeitet.
 */
public class CPSSendSyncTxCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSSendSyncTxCommand.class);

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        CPSTransaction cpsTx = (CPSTransaction) getPreparedValue(KEY_CPS_TX);
        send2CPS(cpsTx);
        return null;
    }

    /*
     * Sendet die angegebene CPS-Tx ueber das synchrone Interface an den CPS.
     */
    protected void send2CPS(CPSTransaction toSend) throws ServiceCommandException {
        try {
            WebServiceTemplate wsTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_CPS_SOURCE_AGENT_SYNC);
            ServiceRequest serviceRequest = buildRequestPayload(toSend, toSend.getOrderNoOrig());
            Object responsePayLoad = wsTemplate.marshalSendAndReceive(serviceRequest);

            XmlTokenSource xmlObject = (XmlTokenSource) responsePayLoad;
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            XmlTokenSource o = cursor.getObject();

            ServiceResponse serviceResponse = ServiceResponse.Factory.parse(o.xmlText());
            ServiceResponse2 serviceResponse2 = serviceResponse.getServiceResponse();

            // SOResponseData in CPS-Tx eintragen
            toSend.setResponseData(serviceResponse2.toString().getBytes(StringTools.CC_DEFAULT_CHARSET));

            // Result analysieren und Status entsprechend setzen
            String responseResultMsg = convertSOResult2Message(serviceResponse2.getSOResult());
            if (serviceResponse2.getSOResult() == SERVICE_RESPONSE_SORESULT_CODE_SUCCESS) {
                toSend.setTxState(CPSTransaction.TX_STATE_SUCCESS);
                createCPSTxLog(toSend, responseResultMsg, true, false);
            }
            else if (serviceResponse2.getSOResult() == SERVICE_RESPONSE_SORESULT_SUB_EXIST) {
                toSend.setTxState(CPSTransaction.TX_STATE_SUCCESS);
                createCPSTxLog(toSend, responseResultMsg, true, false);
            }
            else {
                toSend.setTxState(CPSTransaction.TX_STATE_FAILURE_CLOSED);
                createCPSTxLog(toSend, responseResultMsg);

                CPSServiceResponseSOData soData = null;
                if (serviceResponse2.getSOResponseData() != null) {
                    soData = parseServiceResponseSOData(serviceResponse2);

                    // falls ErrorMsg vorhanden, diese loggen; sonst die 'normale' Message
                    if (StringUtils.isNotEmpty(soData.getErrorMessage())) {
                        createCPSTxLog(toSend, soData.getErrorMessage());
                    }
                    else if (StringUtils.isNotEmpty(soData.getComment())) {
                        createCPSTxLog(toSend, soData.getComment());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            toSend.setTxState(CPSTransaction.TX_STATE_TRANSMISSION_FAILURE);
            createCPSTxLog(toSend, "Error: " + e.getMessage());
            throw new HurricanServiceCommandException(
                    "Error in communication with CPS: " + ExceptionUtils.getFullStackTrace(e), e);
        }
        finally {
            try {
                cpsService.saveCPSTransactionTxNew(toSend, getSessionId());
            }
            catch (Exception saveEx) {
                LOGGER.error(saveEx.getMessage(), saveEx);
            }
        }
    }

    /*
     * Erzeugt einen ServiceRequest
     */
    protected ServiceRequest buildRequestPayload(CPSTransaction cpsTx, Long taifunOrderNo) throws FindException,
            ServiceNotFoundException, XmlException, HurricanServiceCommandException {
        ApplicationKey applicationKey = ApplicationKey.Factory.newInstance();
        applicationKey.setName(CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER);
        applicationKey.setValue("" + taifunOrderNo);
        ApplicationKey[] applicationKeysArray = new ApplicationKey[1];
        applicationKeysArray[0] = applicationKey;

        ApplicationKeys applicationKeys = ApplicationKeys.Factory.newInstance();
        applicationKeys.setApplicationKeyArray(applicationKeysArray);

        return buildRequestPayload(cpsTx, applicationKeys);
    }

}
