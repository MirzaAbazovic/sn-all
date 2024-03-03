/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.command.mdu.endpoint;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.view.FTTBMduImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurrican.webservice.command.mdu.exceptions.CommandMDUCreateDataException;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequest;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestDocument;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestFailure;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestFailureDocument;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestResponse;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUCreateDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um MDU-Daten von Command zu importieren.
 *
 *
 */
public class CommandMDUCreateDataRequestEndpoint extends CommandBaseRequestEndpoint<FTTBMduImportView> {
    private static final Logger LOGGER = Logger.getLogger(CommandMDUCreateDataRequestEndpoint.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandMDUCreateDataRequestEndpoint called");
        return super.invokeInternal(requestObject);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#execute(Object)
     */
    @Override
    protected void execute(FTTBMduImportView model) throws Exception {
        if (StringUtils.isBlank(model.getBezeichnung()) || StringUtils.isBlank(model.getSeriennummer())) {
            throw new StoreException("Daten nicht vollständig!");
        }
        // Erzeuge MDU
        HWMdu mdu = fttxHardwareService.generateFTTBMdu(model, getSessionId());
        if (mdu != null) {
            CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction4MDUInit(mdu.getId(), false, getSessionId(), false);
            if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
                CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
                cpsService.sendCPSTx2CPS(cpsTx, getSessionId());
            }
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateResponseMessage(java.lang.String,
     * Object)
     */
    @Override
    protected Object generateResponseMessage(String responseString, FTTBMduImportView model) throws Exception {
        CommandMDUCreateDataRequestResponseDocument responseDocument = CommandMDUCreateDataRequestResponseDocument.Factory.newInstance();
        CommandMDUCreateDataRequestResponse response = responseDocument.addNewCommandMDUCreateDataRequestResponse();
        response.setBezeichnung(model.getBezeichnung());

        return response;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateFaultMessage(java.lang.Exception,
     * Object)
     */
    @Override
    protected XmlObject generateFaultMessage(Exception e, FTTBMduImportView model) throws CommandException {
        CommandMDUCreateDataRequestFailureDocument failureDocument = CommandMDUCreateDataRequestFailureDocument.Factory.newInstance();
        CommandMDUCreateDataRequestFailure failure = failureDocument.addNewCommandMDUCreateDataRequestFailure();
        failure.setBezeichnung(model.getBezeichnung());
        failure.setErrorMsg(e.getMessage());

        throw new CommandMDUCreateDataException(e.getMessage(), e, failureDocument);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#mapRequestToModel(java.lang.Object)
     */
    @Override
    protected FTTBMduImportView mapRequestToModel(Object requestObject) throws Exception {
        CommandMDUCreateDataRequestDocument document = (CommandMDUCreateDataRequestDocument) requestObject;
        CommandMDUCreateDataRequest data = document.getCommandMDUCreateDataRequest();

        FTTBMduImportView model = new FTTBMduImportView();
        model.setBezeichnung(StringUtils.trimToNull(data.getBezeichnung()));
        model.setHersteller(StringUtils.trimToNull(data.getHersteller()));
        model.setSeriennummer(StringUtils.trimToNull(data.getSeriennummer()));
        model.setModellnummer(StringUtils.trimToNull(data.getModellNummer()));
        model.setBgTyp(StringUtils.trimToNull(data.getBgTyp()));
        model.setOlt(StringUtils.trimToNull(data.getOlt()));
        model.setOltRack(StringUtils.trimToNull(data.getOltRack()));
        model.setOltSubrack(StringUtils.trimToNull(data.getOltSubrack()));
        model.setOltSlot(StringUtils.trimToNull(data.getOltSlot()));
        model.setOltPort(StringUtils.trimToNull(data.getOltPort()));
        model.setGponId(StringUtils.trimToNull(data.getGponId()));
        model.setCatv(StringUtils.trimToNull(data.getCaTvOnline()));
        model.setStandort(StringUtils.trimToNull(data.getStandort()));
        model.setRaum(StringUtils.trimToNull(data.getRaumbezeichnung()));

        return model;
    }
}
