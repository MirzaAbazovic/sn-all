package de.augustakom.hurrican.service.location;

import org.apache.camel.ExchangeTimedOutException;

import de.mnet.esb.cdm.resource.locationservice.v1.SearchRequest;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchResponse;

/**
 * Interface Definition, um ueber einen Service Messages an den ATLAS (ESB) LocationService zu schicken. <br> Info: es
 * gibt keine direkte Implementierung von diesem Interface; eine Instanz wird ueber Spring Remoting als Camel-Proxy
 * erstellt. Eine Instanz soll <b>nur</b> über die {@code CamelProxyLookupService} geholt werden.
 *
 *
 */
public interface LocationService {

    /**
     * Sendet eine Location WBCI Message an den Atlas ESB und gibt die Suchergebnisse zurück.
     *
     * @param request
     * @param <T>
     */
    <T extends SearchRequest, R extends SearchResponse> R searchLocations(T request) throws ExchangeTimedOutException;
}
