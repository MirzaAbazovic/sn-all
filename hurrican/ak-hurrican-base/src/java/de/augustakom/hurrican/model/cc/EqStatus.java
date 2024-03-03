/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 05.07.2010 15:17:36
  */

package de.augustakom.hurrican.model.cc;

public enum EqStatus {
    // Bei Erweiterung Trigger TRBIU_EQUIPMENT (equipmentTrigger.sql) anpassen

    /**
     * Wert fuer <code>status</code>, der einen freien Equipment-Datensatz darstellt.
     */
    frei,
    /**
     * Wert fuer <code>status</code>, der einen rangierten Equipment-Datensatz darstellt. <br> Ein Equipment-Eintrag ist
     * rangiert, wenn eine Rangierung auf den Stift verweist und freigegeben oder belegt ist.
     */
    rang,
    /**
     * Wert fuer <code>status</code>: Port ist physikalisch abgebaut
     */
    abgebaut,
    /**
     * Wert fuer <code>status</code>: Port ist defekt
     */
    defekt,
    /**
     * Wert fuer <code>status</code>: Port ist gesperrt
     */
    locked,
    res,
    /**
     * Wert fuer <code>status</code>, der einen vorbereiteten Equipment-Datensatz darstellt. <br> Ein Equipment-Eintrag
     * ist vorbereitet, wenn eine Rangierung auf den Stift verweist, diese aber noch nicht freigegeben ist.
     */
    vorb,
    /**
     * Wert fuer <code>status</code>: Port ist fuer Baugruppen-Umbau gesperrt
     */
    WEPLA,;
}
