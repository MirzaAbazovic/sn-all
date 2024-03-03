/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2005 07:43:13
 */
package de.augustakom.common.tools.messages;


/**
 * Interface fuer Klassen, die Warnungen generieren - und diese zurueck geben - koennen.
 *
 *
 */
public interface IWarningAware {

    /**
     * Gibt die generierten Warnungen zurueck.
     *
     * @return Objekt mit den generierten Warnungen.
     */
    public AKWarnings getWarnings();

}


