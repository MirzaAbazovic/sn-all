/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2007 08:28:16
 */
package de.augustakom.hurrican.service.base.impl;

import java.util.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Abstrakte Klasse fuer Service-Commands.
 *
 *
 */
public abstract class AbstractHurricanServiceCommand implements IWarningAware, IServiceCommand, ApplicationContextAware {

    private Map<String, Object> values = null;
    private AKWarnings warnings = null;
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Gibt den ueber {@link ApplicationContextAware#setApplicationContext(ApplicationContext)} gesetzten
     * ApplicationContext zurueck.
     *
     * @return
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Kann von Ableitungen ueberschrieben werden, um die benoetigten Daten fuer die Ausfuehrung des Commands zu laden.
     * <br> Fuer den Aufruf der Methode sind die Command-Klasse jedoch selbst zustaendig!
     *
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt oder wenn nicht alle benoetigten
     *                       Daten geladen werden konnten.
     */
    protected void loadRequiredData() throws FindException {
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#prepare(java.lang.String, java.lang.Object)
     */
    @Override
    public void prepare(String name, Object value) {
        if (values == null) {
            values = new HashMap<String, Object>();
        }
        values.put(name, value);
    }

    /**
     * Gibt ein Objekt zurueck, das zuvor ueber die Methode <code>prepare(String, Object)</code> unter dem Namen
     * <code>name</code> gespeichert wurde.
     *
     * @param name Name des gesuchten Objekts.
     * @return
     */
    protected Object getPreparedValue(String name) {
        if (values != null) {
            return values.get(name);
        }
        return null;
    }

    /**
     * @param name         Name des gesuchten Objekts.
     * @param expectedType erwarteter Typ des Objekts
     * @param ignoreNull   Flag, ob null-Values ignoriert werden sollen
     * @param errMsg       Fehlertext fuer die FindException
     * @return
     * @throws FindException falls der Wert {@code null} ist und nicht ignoriert werden soll oder falls der gesuchte
     *                       Wert nicht dem Typ entspricht.
     *
     * @see getPreparedValue(java.lang.Object) Zusaetzlich prueft die Methode, ob das Objekt vom Typ
     * <code>expectedType</code> ist. Ist dies nicht der Fall, wird eine FindException mit der Message
     * <code>errMsg</code> erzeugt.
     */
    protected <T> T getPreparedValue(String name, Class<T> expectedType, boolean ignoreNull, String errMsg) throws FindException {
        Object value = getPreparedValue(name);
        if ((value == null) && (ignoreNull)) {
            return null;
        }
        else {
            if (expectedType.isInstance(value)) {
                return expectedType.cast(value);
            }
            else {
                throw new FindException(errMsg);
            }
        }
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public abstract Object execute() throws Exception;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#getServiceCommandName()
     */
    @Override
    public String getServiceCommandName() {
        return getClass().getName();
    }

    /**
     * @see de.augustakom.common.tools.messages.IWarningAware#getWarnings()
     */
    @Override
    public AKWarnings getWarnings() {
        return warnings;
    }

    /**
     * Setzt die Warnings.
     *
     * @param warnings
     *
     */
    protected void setWarnings(AKWarnings warnings) {
        this.warnings = warnings;
    }

    /**
     * Fuegt eine Warnung hinzu.
     *
     * @param source
     * @param warning
     */
    protected void addWarning(Object source, String warning) {
        if (warnings == null) {
            warnings = new AKWarnings();
        }
        warnings.addAKWarning(source, warning);
    }

    /**
     * Fuegt Warnungen hinzu.
     */
    protected void addWarnings(AKWarnings warnings) {
        if (this.warnings == null) {
            this.warnings = new AKWarnings();
        }
        this.warnings.addAKWarnings(warnings);
    }

}
