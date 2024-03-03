/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2009 14:20:02
 */
package de.augustakom.hurrican.service.billing;

import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Ueber diesen Service koennen Statusmeldungen an externe Systeme(Taifun) uebergeben werden
 *
 *
 */
public interface BillingWorkflowService extends IBillingService {

    /**
     * Funktion uerbemittelt eine StatusId an das externe Billing-System (Taifun). <br> Dabei wird von dem
     * Hurrican-Auftrag die Taifun Auftragsnummer ermittelt und auch geprueft, ob das (Hurrican)Produkt ueberhaupt fuer
     * Status-Meldungen vorgesehen ist.
     *
     * @param auftragId Hurrican-Auftragsnummer
     * @param statusId  Id des Status
     * @param value     Wert, der zusaetzlich zum Status uebertragen wird
     * @param sessionId Id der Benutzersession, optional
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void changeOrderState(Long auftragId, Long statusId, String value, Long sessionId) throws StoreException;

    /**
     * Funktion uebermittelt eine StatusId an das externe System(Taifun)
     *
     * @param verlaufId Id des Hurrican-Verlaufs
     * @param sessionId Id der Benutzersession, optional
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void changeOrderState4Verlauf(Long verlaufId, Long sessionId) throws StoreException;

}


