/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2010 11:49:08
 */
package de.mnet.hurrican.webservice.command.customer.endpoint;

import java.lang.reflect.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.hurrican.model.cc.view.FTTXKundendatenView;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequest;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestDocument;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestFailure;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestFailureDocument;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestResponse;
import de.mnet.hurricanweb.command.customer.types.CommandCustomerDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um Kunden-Daten zu einem Wohnungs-Stift abrufen zu können.
 *
 *
 */
public class CommandCustomerDataRequestEndpoint extends CommandBaseRequestEndpoint<FTTXKundendatenView> {
    private static final Logger LOGGER = Logger.getLogger(CommandCustomerDataRequestEndpoint.class);
    private static final String XML_ELEMENT_NETWORK_ELEMENT_ID = "networkElementId";
    private static final String XML_ELEMENT_PORT = "port";

    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandCustomerDataRequestEndpoint called");
        return super.invokeInternal(requestObject);
    }

    @Override
    protected void execute(FTTXKundendatenView model) throws Exception {
        fttxInfoService.getKundendaten4Command(model);
    }

    /**
     * Setzt nur Property Werte, die != null sind.
     */
    private void setPropertyAndIgnoreNull(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if ((value != null) && (name != null) && (bean != null)) {
            PropertyUtils.setProperty(bean, name, value);
        }
    }

    @Override
    protected Object generateResponseMessage(String responseString, FTTXKundendatenView model) throws Exception {
        CommandCustomerDataRequestResponseDocument responseDocument = CommandCustomerDataRequestResponseDocument.Factory.newInstance();
        CommandCustomerDataRequestResponse response = responseDocument.addNewCommandCustomerDataRequestResponse();

        // Optionale Felder
        setPropertyAndIgnoreNull(response, XML_ELEMENT_NETWORK_ELEMENT_ID, model.getGeraetebezeichnung());
        setPropertyAndIgnoreNull(response, XML_ELEMENT_PORT, model.getPort());
        setPropertyAndIgnoreNull(response, "kundeAnrede", model.getKdAnrede());
        setPropertyAndIgnoreNull(response, "kundeName", model.getKdName());
        setPropertyAndIgnoreNull(response, "kundeVorname", model.getKdVorname());
        setPropertyAndIgnoreNull(response, "kundeTelefon", model.getKdTelefon());
        setPropertyAndIgnoreNull(response, "kundeMobil", model.getKdMobil());
        setPropertyAndIgnoreNull(response, "kundeMail", model.getKdMail());
        setPropertyAndIgnoreNull(response, "esAnrede", model.getEsAnrede());
        setPropertyAndIgnoreNull(response, "esName", model.getEsName());
        setPropertyAndIgnoreNull(response, "esVorname", model.getEsVorname());
        setPropertyAndIgnoreNull(response, "esTelefon", model.getEsTelefon());
        setPropertyAndIgnoreNull(response, "esMobil", model.getEsMobil());
        setPropertyAndIgnoreNull(response, "esMail", model.getEsMail());
        setPropertyAndIgnoreNull(response, "anschlussdoseLage", model.getAnschlussDoseLage());
        setPropertyAndIgnoreNull(response, "egTyp", model.getEgTyp());
        setPropertyAndIgnoreNull(response, "egSeriennummer", model.getEgSeriennummer());
        setPropertyAndIgnoreNull(response, "egCwmpId", model.getEgCwmpId());
        setPropertyAndIgnoreNull(response, "auftragsnummerHurrican", model.getAuftragsnummerHurrican());
        setPropertyAndIgnoreNull(response, "auftragsnummerTaifun", model.getAuftragsnummerTaifun());
        setPropertyAndIgnoreNull(response, "vbz", model.getVbz());
        setPropertyAndIgnoreNull(response, "produkt", model.getProdukt());
        setPropertyAndIgnoreNull(response, "auftragsstatus", model.getAuftragsstatus());

        // Pflichtfelder
        response.setPortstatus(model.getPortstatus());

        return response;
    }

    @Override
    protected XmlObject generateFaultMessage(Exception e, FTTXKundendatenView model) throws CommandException {
        CommandCustomerDataRequestFailureDocument failureDocument = CommandCustomerDataRequestFailureDocument.Factory.newInstance();
        CommandCustomerDataRequestFailure failure = failureDocument.addNewCommandCustomerDataRequestFailure();

        // Optionale Felder
        try {
            setPropertyAndIgnoreNull(failure, XML_ELEMENT_NETWORK_ELEMENT_ID, model.getGeraetebezeichnung());
            setPropertyAndIgnoreNull(failure, XML_ELEMENT_PORT, model.getPort());
        }
        catch (Exception ex) {
            throw new CommandException(ex.getMessage(), ex);
        }

        // Pflichtfelder
        failure.setErrorMsg(e.getMessage());

        return failureDocument;
    }

    @Override
    protected FTTXKundendatenView mapRequestToModel(Object requestObject) throws Exception {
        CommandCustomerDataRequestDocument document = (CommandCustomerDataRequestDocument) requestObject;
        CommandCustomerDataRequest data = document.getCommandCustomerDataRequest();

        FTTXKundendatenView model = new FTTXKundendatenView();
        model.setGeraetebezeichnung(StringUtils.trimToNull(data.getNetworkElementId()));
        model.setPort(StringUtils.trimToNull(data.getPort()));
        model.setDatum((data.getDatum() != null) ? data.getDatum().getTime() : null);
        return model;
    }

}
