/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.command.stift.endpoint;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.view.FTTBStifteImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequest;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequest.Stifte.Stift;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestDocument;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestFailure;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestFailure.Stifte;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestFailureDocument;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestResponse;
import de.mnet.hurricanweb.command.stift.types.CommandStiftDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um Stift/Leisten-Daten von Command zu importieren.
 *
 *
 */
public class CommandStiftDataRequestEndpoint extends CommandBaseRequestEndpoint<FTTBStifteImportView> {
    private static final Logger LOGGER = Logger.getLogger(CommandStiftDataRequestEndpoint.class);
    private List<String> errorStifte = null;

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandStiftDataRequestEndpoint called");
        return super.invokeInternal(requestObject);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#execute(Object)
     */
    @Override
    protected void execute(FTTBStifteImportView model) throws Exception {
        errorStifte = new ArrayList<>();
        StringBuilder error = new StringBuilder();

        // Standort ermitteln
        String hvtName = (StringUtils.isBlank(model.getVersorger())) ? model.getStandort() : model.getVersorger();
        // Erzeuge Stifte
        for (String stift : model.getStifte()) {
            try {
                fttxHardwareService.generateFTTBStift(hvtName, model.getLeiste(), StringUtils.leftPad(stift, 2, "0"),
                        RangSchnittstelle.valueOf(model.getTyp()));
            }
            catch (Exception e) {
                errorStifte.add(stift);
                error.append("Fehler beim Anlegen von Stift " + stift + " : " + e.getMessage() + "\n");
            }
        }
        if (!errorStifte.isEmpty()) {
            throw new StoreException(error.toString());
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateResponseMessage(java.lang.String,
     * Object)
     */
    @Override
    protected Object generateResponseMessage(String responseString, FTTBStifteImportView model) throws Exception {
        CommandStiftDataRequestResponseDocument responseDocument = CommandStiftDataRequestResponseDocument.Factory.newInstance();
        CommandStiftDataRequestResponse response = responseDocument.addNewCommandStiftDataRequestResponse();
        response.setLeiste(model.getLeiste());

        return response;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateFaultMessage(java.lang.Exception,
     * Object)
     */
    @Override
    protected XmlObject generateFaultMessage(Exception e, FTTBStifteImportView model) throws CommandException {
        CommandStiftDataRequestFailureDocument failureDocument = CommandStiftDataRequestFailureDocument.Factory.newInstance();
        CommandStiftDataRequestFailure failure = failureDocument.addNewCommandStiftDataRequestFailure();
        failure.setLeiste(model.getLeiste());
        if (!errorStifte.isEmpty()) {
            Stifte stifte = failure.addNewStifte();
            for (String stift : errorStifte) {
                CommandStiftDataRequestFailure.Stifte.Stift st = stifte.addNewStift();
                st.setStift(stift);
            }
        }
        failure.setErrorMsg(e.getMessage());

        return failureDocument;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#mapRequestToModel(java.lang.Object)
     */
    @Override
    protected FTTBStifteImportView mapRequestToModel(Object requestObject) throws Exception {
        CommandStiftDataRequestDocument document = (CommandStiftDataRequestDocument) requestObject;
        CommandStiftDataRequest data = document.getCommandStiftDataRequest();

        FTTBStifteImportView model = new FTTBStifteImportView();
        model.setStandort(StringUtils.trimToNull(data.getStandort()));
        model.setTyp(StringUtils.trimToNull(data.getTyp()));
        model.setLeiste(StringUtils.trimToNull(data.getLeiste()));
        model.setVersorger(StringUtils.trimToNull(data.getVersorger()));
        List<String> stifte = new ArrayList<>();
        for (Stift stift : data.getStifte().getStiftList()) {
            stifte.add(StringUtils.trimToNull(stift.getStift()));
        }
        model.setStifte(stifte);

        return model;
    }
}
