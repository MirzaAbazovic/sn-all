/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:59:18
 */
package de.augustakom.hurrican.service.base.iface;

import de.augustakom.hurrican.service.base.exceptions.LoadException;

/**
 * Interface definiert Methoden fuer eine Service-Implementierung, die Objekte laedt.
 *
 *
 */
public interface LoadService extends IHurricanService {

    /**
     * Laedt die Eigenschaften des Objekts <code>toLoad</code>. Das Objekt wird ueber eine eindeutige ID
     * (<code>id</code>) identifiziert. <br>
     *
     * @param sessionId Session-ID des angemeldeten Benutzers
     * @param id        ID des zu ladenden Objekts
     * @param toLoad    Objekt, das 'gefuellt' werden soll
     * @throws LoadException wenn beim Laden des Objekts ein Fehler aufgetreten ist (z.B. ungueltige ID).
     */
    public void load(Object id, Object toLoad)
            throws LoadException;

}
