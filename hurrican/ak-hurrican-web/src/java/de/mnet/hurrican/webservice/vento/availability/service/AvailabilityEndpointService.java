/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.13
 */
package de.mnet.hurrican.webservice.vento.availability.service;

import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.service.base.exceptions.FindException;

public interface AvailabilityEndpointService {

    /**
     * Ermittelt die Availability Informationen zu einer Geo-ID. Unter anderem sind dies: <li>Connection-Type (z.B. HVT,
     * FTTB, FTTH, Gewofag etc.) <li>Technology-Type (z.B. ISDN, POTS, ADSL_ISDN, SDSL, SHDSL etc.) <li>Leitungslaenge
     * (bei HVT) <li>max. Bandbreite, falls speziell konfiguriert. Zusätzlich werden fuer die GeoId HVT und FTTC_KVZ
     * Standorte bei bedarf nachgetragen.
     */
    public VentoGetAvailabilityInformationResponse getAvailabilityInformation(
            VentoGetAvailabilityInformationRequest request) throws FindException;
}
