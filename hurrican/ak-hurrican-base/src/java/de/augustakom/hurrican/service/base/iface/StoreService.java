/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 10:38:49
 */
package de.augustakom.hurrican.service.base.iface;

import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Interface definiert Methode fuer Service-Implementierungen, die Objekte speichern koennen.
 *
 *
 */
public interface StoreService extends IHurricanService {

    /**
     * Speichert das Objekt <code>toStore</code>. <br> Existiert das Objekt bereits, dann werden die Parameter nur
     * aktualisiert. Ansonsten findet eine Neuanlage statt.
     *
     * @param toStore Objekt, das gespeichert werden soll.
     * @throws StoreException wird geworfen, wenn beim Speichern des Objekts ein Fehler auftritt.
     */
    public void store(Object toStore)
            throws StoreException;
}
