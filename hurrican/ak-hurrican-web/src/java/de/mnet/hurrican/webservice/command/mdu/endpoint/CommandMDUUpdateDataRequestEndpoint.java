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
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.view.FTTBMduImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurrican.webservice.command.mdu.exceptions.CommandMDUUpdateDataException;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequest;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestDocument;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestFailure;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestFailureDocument;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestResponse;
import de.mnet.hurricanweb.command.mdu.types.CommandMDUUpdateDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um MDU-Daten von Command zu aktualisieren.
 *
 *
 */
public class CommandMDUUpdateDataRequestEndpoint extends CommandBaseRequestEndpoint<FTTBMduImportView> {
    private static final Logger LOGGER = Logger.getLogger(CommandMDUUpdateDataRequestEndpoint.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandMDUUpdateDataRequestEndpoint called");
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
        // Update MDU
        Pair<HWMdu, Boolean> result = fttxHardwareService.updateMDU(model.getBezeichnung(), model.getSeriennummer(), getSessionId());
        if ((result != null) && BooleanTools.nullToFalse(result.getSecond())) {
            HWMdu mdu = result.getFirst();
            CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction4MDUInit(mdu.getId(), true, getSessionId(), false);
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
        CommandMDUUpdateDataRequestResponseDocument responseDocument = CommandMDUUpdateDataRequestResponseDocument.Factory.newInstance();
        CommandMDUUpdateDataRequestResponse response = responseDocument.addNewCommandMDUUpdateDataRequestResponse();
        response.setBezeichnung(model.getBezeichnung());

        return response;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateFaultMessage(java.lang.Exception,
     * Object)
     */
    @Override
    protected XmlObject generateFaultMessage(Exception e, FTTBMduImportView model) throws CommandException {
        CommandMDUUpdateDataRequestFailureDocument failureDocument = CommandMDUUpdateDataRequestFailureDocument.Factory.newInstance();
        CommandMDUUpdateDataRequestFailure failure = failureDocument.addNewCommandMDUUpdateDataRequestFailure();
        failure.setBezeichnung(model.getBezeichnung());
        failure.setErrorMsg(e.getMessage());

        throw new CommandMDUUpdateDataException(e.getMessage(), e, failureDocument);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#mapRequestToModel(java.lang.Object)
     */
    @Override
    protected FTTBMduImportView mapRequestToModel(Object requestObject) throws Exception {
        CommandMDUUpdateDataRequestDocument document = (CommandMDUUpdateDataRequestDocument) requestObject;
        CommandMDUUpdateDataRequest data = document.getCommandMDUUpdateDataRequest();

        FTTBMduImportView model = new FTTBMduImportView();
        model.setBezeichnung(StringUtils.trimToNull(data.getBezeichnung()));
        model.setSeriennummer(StringUtils.trimToNull(data.getSerienNummer()));

        return model;
    }

    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }
}
