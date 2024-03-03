/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 10:50:24
 */
package de.augustakom.hurrican.service.base.iface;

import de.augustakom.hurrican.service.base.exceptions.DeleteException;

/**
 * Interface definiert Methoden fuer Service-Implementierungen, die Objekte loeschen koennen.
 *
 *
 */
public interface DeleteService extends IHurricanService {

    /**
     * Loescht ein Objekt ueber dessen eindeutige ID.
     *
     * @param id ID des Objekts, das geloescht werden soll.
     * @throws DeleteException wenn beim Loeschen des Objekts ein Fehler aufgetritt.
     */
    public void deleteById(Object id)
            throws DeleteException;

}




