/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2009 13:54:07
 */
package de.augustakom.hurrican.service.cc.impl.command.command;

import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Command-Klasse fuer Command-Commands.
 *
 *
 */
public abstract class AbstractCommandCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractCommandCommand.class);

    private MnetWebServiceTemplate commandWebServiceTemplate = null;
    protected WebServiceConfig wsCfg = null;
    protected Object requestPayload = null;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        getPreparedValues();
        validateValues();
        buildRequestPayload();
        return null;
    }

    /**
     * Liest die Objekte aus, die zuvor ueber die Methode <code>prepare(String, Object)</code> unter dem Namen
     * <code>name</code> fuer das Command gespeichert wurden.
     *
     * @throws Exception
     */
    protected abstract void getPreparedValues() throws Exception;

    /**
     * Ueberprueft die Werte
     *
     * @throws Exception
     */
    protected abstract void validateValues() throws Exception;

    /**
     * Generiert ein Payload-Objekt
     *
     * @throws Exception
     */
    protected abstract void buildRequestPayload() throws Exception;

    /**
     * Konfiguriert das WebService-Template mit Daten aus der Tabelle T_WEBSERVICE_CONFIG
     *
     * @param templateId ID des zu verwendenden Konfigurationseintrags
     * @return konfiguriertes WebServiceTemplate
     * @throws ServiceCommandException falls notwendige Konfigurationsparameter nicht vorhanden sind oder bei der
     *                                 Konfiguration ein Fehler auftritt
     */
    protected WebServiceTemplate configureAndGetWSTemplate(Long templateId) throws ServiceCommandException {
        try {
            loadWsConfigTemplate(templateId);

            MnetWebServiceTemplate wst = getCommandWebServiceTemplate();
            wst.configureWSTemplate(wsCfg);

            return wst;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die WebService Konfiguration
     *
     * @throws Exception
     */
    protected void loadWsConfigTemplate(Long templateId) throws Exception {
        QueryCCService qs = getCCService(QueryCCService.class);
        wsCfg = qs.findById(templateId, WebServiceConfig.class);
    }

    /**
     * @return the commandWebServiceTemplate
     */
    public MnetWebServiceTemplate getCommandWebServiceTemplate() {
        return commandWebServiceTemplate;
    }

    /**
     * @param commandWebServiceTemplate the commandWebServiceTemplate to set
     */
    public void setCommandWebServiceTemplate(MnetWebServiceTemplate commandWebServiceTemplate) {
        this.commandWebServiceTemplate = commandWebServiceTemplate;
    }
}


