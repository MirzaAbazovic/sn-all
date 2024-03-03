/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2009 08:59:13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.ws.IWebServiceConfiguration;


/**
 * Modell-Klasse fuer eine WebService Konfiguration. <br> Ueber dieses Modell kann ein WebService mit URL sowie
 * User/Passwort konfiguriert werden.
 *
 *
 */
public class WebServiceConfig extends AbstractCCIDModel implements IWebServiceConfiguration {

    /**
     * ID der WebService-Konfig fuer den (asynchronen) CPS SourceAgent.
     */
    public static final Long WS_CFG_CPS_SOURCE_AGENT_ASYNC = Long.valueOf(1);
    /**
     * ID der WebService-Konfig fuer den (synchronen) CPS SourceAgent.
     */
    public static final Long WS_CFG_CPS_SOURCE_AGENT_SYNC = Long.valueOf(2);
    /**
     * ID der WebService-Konfig fuer die Taifun WebServices.
     */
    public static final Long WS_CFG_TAIFUN = Long.valueOf(3);
    /**
     * ID der WebService-Konfig fuer die Command WebServices. UserApiWS
     */
    public static final Long WS_CFG_COMMAND = Long.valueOf(4);
    /**
     * ID der WebService-Konfig fuer die Command WebServices. HurricanwebApiWS
     */
    public static final Long WS_CFG_COMMAND_HURRICAN_API = Long.valueOf(5);
    /**
     * ID der WebService-Konfig fuer den MOnline WebService
     */
    public static final Long WS_CFG_MONLINE = Long.valueOf(9);
    /**
     * ID der WebService-Konfig fuer den BSI WebService
     */
    public static final Long WS_CFG_BSI = Long.valueOf(10);
    /**
     * ID der WebService-Konfig fuer die Elektra WebService
     */
    public static final Long WS_CFG_ELEKTRA = Long.valueOf(11);

    /**
     * Command Resource Inventory Webservice.
     */
    public static final Long WS_CFG_COMMAND_RESOURCE_INVENTORY = 12L;

    private String name = null;
    private String destSystem = null;
    private String wsURL = null;
    private String wsSecurementAction = null;
    private String wsSecurementUser = null;
    private String wsSecurementPassword = null;
    private String appSecurementUser = null;
    private String appSecurementPassword = null;
    private String description = null;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the destSystem
     */
    public String getDestSystem() {
        return destSystem;
    }

    /**
     * @param destSystem the destSystem to set
     */
    public void setDestSystem(String destSystem) {
        this.destSystem = destSystem;
    }

    /**
     * @return the wsURL
     */
    @Override
    public String getWsURL() {
        return wsURL;
    }

    /**
     * @param wsURL the wsURL to set
     */
    public void setWsURL(String wsURL) {
        this.wsURL = wsURL;
    }

    /**
     * @return the wsSecurementAction
     */
    @Override
    public String getWsSecurementAction() {
        return wsSecurementAction;
    }

    /**
     * @param wsSecurementAction the wsSecurementAction to set
     */
    public void setWsSecurementAction(String wsSecurementAction) {
        this.wsSecurementAction = wsSecurementAction;
    }

    /**
     * @return the wsSecurementUser
     */
    @Override
    public String getWsSecurementUser() {
        return wsSecurementUser;
    }

    /**
     * @param wsSecurementUser the wsSecurementUser to set
     */
    public void setWsSecurementUser(String wsSecurementUser) {
        this.wsSecurementUser = wsSecurementUser;
    }

    /**
     * @return the wsSecurementPassword
     */
    @Override
    public String getWsSecurementPassword() {
        return wsSecurementPassword;
    }

    /**
     * @param wsSecurementPassword the wsSecurementPassword to set
     */
    public void setWsSecurementPassword(String wsSecurementPassword) {
        this.wsSecurementPassword = wsSecurementPassword;
    }

    /**
     * @return the appSecurementUser
     */
    public String getAppSecurementUser() {
        return appSecurementUser;
    }

    /**
     * @param appSecurementUser the appSecurementUser to set
     */
    public void setAppSecurementUser(String appSecurementUser) {
        this.appSecurementUser = appSecurementUser;
    }

    /**
     * @return the appSecurementPassword
     */
    public String getAppSecurementPassword() {
        return appSecurementPassword;
    }

    /**
     * @param appSecurementPassword the appSecurementPassword to set
     */
    public void setAppSecurementPassword(String appSecurementPassword) {
        this.appSecurementPassword = appSecurementPassword;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}


