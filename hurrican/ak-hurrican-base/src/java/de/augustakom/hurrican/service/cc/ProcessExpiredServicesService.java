/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.common.tools.messages.AKWarnings;

/**
 * Service-Interface, um Auftraege mit abgelaufenen Leistungen zu ermitteln und zu prozessieren.
 */
public interface ProcessExpiredServicesService extends ICCService {

    /**
     * Ermittelt alle (Taifun) Auftraege, bei denen eine als 'AUTO_EXPIRE' markierte Leistung zu 'heute' ablaeuft
     * und erstellt fuer die zugehoerigen Hurrican-Auftraege einen Bauauftrag vom Typ 'automatischer Downgrade' mit
     * Realisierungstermin = 'naechster Werktag'. <br/>
     * Ueber den Bauauftrag ist die automatische CPS-Provisionierung 'geschenkt'.
     *
     * @return {@link de.augustakom.common.tools.messages.AKWarnings} mit Fehlern, die evtl. bei der Prozessierung
     * der einzelnen Auftraege auftreten.
     * @throws de.augustakom.hurrican.exceptions.ExpiredServicesException wenn ein nicht vorhergesehener Fehler auftritt
     */
    AKWarnings processExpiredServices(Long sessionId);

}
