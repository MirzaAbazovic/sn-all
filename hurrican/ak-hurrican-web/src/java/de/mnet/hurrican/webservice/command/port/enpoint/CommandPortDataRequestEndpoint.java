/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.command.port.enpoint;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.view.FTTBMduPortImportView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequest;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequest.Ports.Port;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestDocument;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailure;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailure.Ports;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailureDocument;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestResponse;
import de.mnet.hurricanweb.command.port.types.CommandPortDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um Port-Daten von Command zu importieren.
 *
 *
 */
public class CommandPortDataRequestEndpoint extends CommandBaseRequestEndpoint<List<FTTBMduPortImportView>> {
    private static final Logger LOGGER = Logger.getLogger(CommandPortDataRequestEndpoint.class);
    private List<String> errorPorts = new ArrayList<String>();
    private String mdu = null;

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandPortDataRequestEndpoint called");
        return super.invokeInternal(requestObject);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#execute(Object)
     */
    @Override
    protected void execute(List<FTTBMduPortImportView> model) throws Exception {
        StringBuilder error = new StringBuilder();
        errorPorts.clear();
        for (FTTBMduPortImportView view : model) {
            try {
                fttxHardwareService.generateFTTBMduPort(view, getSessionId());
            }
            catch (Exception e) {
                errorPorts.add(view.getPort());
                error.append(e.getMessage()).append("\n");
            }
        }
        if (CollectionTools.isNotEmpty(errorPorts)) {
            throw new StoreException(error.toString());
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateResponseMessage(java.lang.String,
     * Object)
     */
    @Override
    protected Object generateResponseMessage(String responseString, List<FTTBMduPortImportView> model) throws Exception {
        CommandPortDataRequestResponseDocument responseDocument = CommandPortDataRequestResponseDocument.Factory.newInstance();
        CommandPortDataRequestResponse response = responseDocument.addNewCommandPortDataRequestResponse();
        response.setMdu(mdu);

        return response;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateFaultMessage(java.lang.Exception,
     * Object)
     */
    @Override
    protected XmlObject generateFaultMessage(Exception e, List<FTTBMduPortImportView> model) throws CommandException {
        CommandPortDataRequestFailureDocument failureDocument = CommandPortDataRequestFailureDocument.Factory.newInstance();
        CommandPortDataRequestFailure failure = failureDocument.addNewCommandPortDataRequestFailure();
        failure.setErrorMsg(e.getMessage());
        if (CollectionTools.isNotEmpty(errorPorts)) {
            Ports ports = failure.addNewPorts();
            for (String port : errorPorts) {
                de.mnet.hurricanweb.command.port.types.CommandPortDataRequestFailure.Ports.Port errorPort = ports.addNewPort();
                errorPort.setPort(port);
            }
        }

        return failureDocument;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#mapRequestToModel(java.lang.Object)
     */
    @Override
    protected List<FTTBMduPortImportView> mapRequestToModel(Object requestObject) throws Exception {
        CommandPortDataRequestDocument document = (CommandPortDataRequestDocument) requestObject;
        CommandPortDataRequest data = document.getCommandPortDataRequest();

        List<FTTBMduPortImportView> model = new ArrayList<>();
        mdu = StringUtils.trimToNull(data.getMdu());
        List<Port> ports = data.getPorts().getPortList();
        if (CollectionTools.isNotEmpty(ports)) {
            for (Port port : ports) {
                FTTBMduPortImportView view = new FTTBMduPortImportView();
                view.setMdu(mdu);
                view.setBgTyp(StringUtils.trimToNull(port.getBgTyp()));
                view.setModulNummer(StringUtils.trimToNull(port.getModulNummer()));
                view.setHwVariante(StringUtils.trimToNull(port.getHardwareVariante()));
                view.setPort(StringUtils.trimToNull(port.getPort()));
                view.setSchnittstelle(StringUtils.trimToNull(port.getSchnittstelle()));
                view.setLeiste(StringUtils.trimToNull(port.getLeiste()));
                view.setStift(StringUtils.trimToNull(port.getStift()));
                model.add(view);
            }
        }

        return model;
    }
}
