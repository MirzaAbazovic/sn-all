/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2012 13:00:26
 */
package de.augustakom.hurrican.service.cc.impl.command;

/**
 *
 */
public abstract class AbstractXlsImportCommand extends AbstractServiceCommand {

    /**
     * Die Zeile die importiert werden soll.<br> Typ: Row
     */
    public static final String PARAM_IMPORT_ROW = "PARAM_IMPORT_ROW";

    /**
     * Die Session ID des aufrufenden (angemeldeten) Benutzers.<br> Typ: Long
     */
    public static final String PARAM_SESSION_ID = "PARAM_SESSION_ID";

}


