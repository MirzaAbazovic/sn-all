/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 10:53:03
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer alle Modelle, die die original Kundennummer (aus dem Billing-System) eines Kunden besitzen.
 *
 *
 */
public interface KundenModel {

    /**
     * Setzt die Kundennummer fuer das Objekt.
     *
     * @param kNo Kundennummer
     *
     */
    public void setKundeNo(Long kNo);

    /**
     * Gibt die Kundennummer zurueck.
     *
     * @return Kundennummer
     *
     */
    public Long getKundeNo();

}


