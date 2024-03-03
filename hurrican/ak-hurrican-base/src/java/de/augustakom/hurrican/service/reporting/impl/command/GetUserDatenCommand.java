/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2007 09:06:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse, um Report-Daten zum aktuellen Benutzer zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetUserDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetUserDatenCommand.class);

    // Konstanten für Properties
    public static final String BEARBEITER = "Bearbeiter";
    public static final String BEARBEITER_NAME = "BearbeiterName";
    public static final String BEARBEITER_VORNAME = "BearbeiterVorname";
    public static final String PHONE = "Phone";
    public static final String PHONE_NEUTRAL = "PhoneNeutral";
    public static final String FAX = "Fax";
    public static final String EMAIL = "Email";

    private String loginName = null;
    private Map<String, Object> map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();

            readUserDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return USER_DATEN;
    }

    /* Ermittelt die Stammdaten eines Auftrags und schreibt diese in die HashMap. */
    protected void readUserDaten() throws HurricanServiceCommandException {
        try {
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService userService = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = (loginName != null) ? userService.findByLoginName(loginName) : null;

            map.put(getPropName(BEARBEITER), (user != null) ? user.getFirstNameAndName() : null);
            map.put(getPropName(BEARBEITER_NAME), (user != null) ? user.getName() : null);
            map.put(getPropName(BEARBEITER_VORNAME), (user != null) ? user.getFirstName() : null);
            map.put(getPropName(PHONE), (user != null) ? user.getPhone() : null);
            map.put(getPropName(PHONE_NEUTRAL), (user != null) ? user.getPhoneNeutral() : null);
            map.put(getPropName(FAX), (user != null) ? user.getFax() : null);
            map.put(getPropName(EMAIL), (user != null) ? user.getEmail() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmp = getPreparedValue(USER_LOGINNAME);
        loginName = (tmp instanceof String) ? (String) tmp : null;
        if (loginName == null) {
            throw new HurricanServiceCommandException("User-Objekt wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetUserDatenCommand.properties";
    }
}


