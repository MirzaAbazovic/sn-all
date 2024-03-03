/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 01.06.2010 09:29:35
  */

package de.augustakom.hurrican.model.cc;

/**
 * Konfiguration des Schicht2 Protokolls auf einem DSLAM-Port.
 *
 *
 */
public enum Schicht2Protokoll {
    // Bei Erweiterung Trigger TRBIU_EQUIPMENT (equipmentTrigger.sql) anpassen

    /**
     * bisheriger Default
     */
    ATM,
    /**
     * Modus für neue Mehrdraht-SDSL-Anschlüsse
     */
    EFM;
}
