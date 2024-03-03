/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2016
 */
package de.augustakom.hurrican.service.billing;

import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Web service um Daten von Taifun abzufragen <br/>
 * Created by zolleiswo on 03.03.2016.
 */
public interface TnbKennungService extends IBillingService {
    /**
     * ermittelt die Portierungskennung anhand der Taifun-Auftragsnummer
     *
     * @param taifunAutragsNummer die taifun Auftragsnummer
     * @return Die Portierungskennung fuer den Auftrag
     * @throws FindException Ein Fehler beim Aufruf des Taifun-Webservices
     */
    String getTnbKennungFromTaifunWebservice(Long taifunAutragsNummer) throws FindException;
}
