/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Helper class for the CCAuftragStatusService.
 * <br />
 * This helper was created for encapsulating the auftrag status service logic into an independent service. This
 * service can then be invoked with different transaction propagation levels, depending on the CCAuftragStatusService
 * use case.
 *
 */
public interface CCAuftragStatusServiceHelper {
    /**
     * Setzt den Status des Auftrags mit der ID <code>auftragId</code> auf 'Kuendigung'.
     * <br />Laeuft innerhalb einer neuen Transaction.
     *
     * @param auftragId        ID des zu kuendigenden Auftrags.
     * @param kuendigungsDatum Datum, zu dem der Auftrag gekuendigt werden soll.
     * @param user        Aktueller User.
     * @throws StoreException wenn bei der Kuendigung ein Fehler auftritt.
     */
    void kuendigeAuftragReqNew(Long auftragId, Date kuendigungsDatum, AKUser user) throws StoreException;

    /**
     * Setzt den Status des Auftrags mit der ID <code>auftragId</code> auf 'Kuendigung'.
     * <br />Laeuft innerhalb der Transaction des Callers oder wenn kein Tx aktiv ist, startet eine neue Tx.
     *
     * @param auftragId        ID des zu kuendigenden Auftrags.
     * @param kuendigungsDatum Datum, zu dem der Auftrag gekuendigt werden soll.
     * @param user        Aktueller User.
     * @throws StoreException wenn bei der Kuendigung ein Fehler auftritt.
     */
    void kuendigeAuftragReq(Long auftragId, Date kuendigungsDatum, AKUser user) throws StoreException;
}
