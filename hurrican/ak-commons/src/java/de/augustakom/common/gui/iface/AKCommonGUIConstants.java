/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 07:41:00
 */
package de.augustakom.common.gui.iface;


/**
 * Interface zur Definition von Konstanten, die innerhalb der Common-GUI Klassen verwendet werden.
 */
public interface AKCommonGUIConstants {

    /**
     * SystemProperty-Name fuer das Admin-Flag. <br> Ueber dieses Flag kann ausgewertet werden, ob ein Anwender
     * Administrator-Rechte besitzt. <br> Dies ist der Fall, wenn der Wert von AK.Is.Admin auf <code>true</code> gesetzt
     * ist.
     */
    String ADMIN_FLAG = "AK.Is.Admin";

    /**
     * SystemProperty-Name fuer die Applikations-ID. <br> Ueber diesen Parameter kann den Common-GUI Klassen die
     * aktuelle Applikations-ID mitgeteilt werden.
     */
    String ADMIN_APPLICATION_ID = "AK.Application.Id";

}


