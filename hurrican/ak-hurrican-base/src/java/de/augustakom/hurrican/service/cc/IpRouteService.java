/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 11:35:08
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von IP-Routes.
 */
public interface IpRouteService extends ICCService {

    /**
     * Ermittelt alle (nicht geloeschten) IP-Routen zu einem Auftrag.
     *
     * @param auftragId ID des Auftrags.
     * @return Liste mit Objekten des Typs {@link IpRoute}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<IpRoute> findIpRoutesByOrder(Long auftragId) throws FindException;

    /**
     * Speichert die angegebene IP-Route ab.
     *
     * @param toSave    zu speichernde Route.
     * @param sessionId
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn das Modell keine gueltigen Daten besitzt
     */
    public void saveIpRoute(IpRoute toSave, Long sessionId) throws StoreException, ValidationException;

    /**
     * Verschiebt die angegebene Route auf den Auftrag mit der Id {@code auftragId}. Die Route wird dabei nur kopiert!
     *
     * @param toMove    zu verschiebende Route
     * @param auftragId ID des Auftrags, dem die Route zugeordnet werden soll
     * @param sessionId Session-ID des Users
     * @throws StoreException wenn beim Umzug der Route ein Fehler auftritt.
     */
    public void moveIpRoute(IpRoute toMove, Long auftragId, Long sessionId) throws StoreException;

    /**
     * Loescht die angegebene IP-Route. Die Route wird dabei nicht physikalisch geloescht, sondern nur mit einem
     * Delete-Flag versehen!
     *
     * @param toDelete zu loeschendes Objekt.
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteIpRoute(IpRoute toDelete, Long sessionId) throws DeleteException;

}


