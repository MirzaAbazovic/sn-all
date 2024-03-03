/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2004 11:21:52
 */
package de.augustakom.common.model;

import org.apache.log4j.Logger;


/**
 * Dieses Interface ist fuer Modell-Klassen gedacht, die Debug-Informationen liefern. <br>
 *
 *
 */
public interface DebugModel {

    /**
     * Gibt die Eigenschaften einer Modell-Klasse ueber den Logger <code>logger</code> aus. <br> Als Modus fuer den
     * Logger wird 'debug' verwendet.
     */
    void debugModel(Logger logger);

}


