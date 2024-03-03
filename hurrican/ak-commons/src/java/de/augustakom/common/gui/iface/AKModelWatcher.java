/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 12:03:00
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer GUI-Komponenten, die ein Objekt auf Aenderungen ueberpruefen koennen. <br> <br> Verwendung: <ul>
 * <li>Objekt ueber die Methode <code>addObjectToWatch(String, Object)</code> dem 'Watcher' uebergeben <li>Ueber Methode
 * <code>hasChanged(String, Object)</code> das evtl. geaenderte Objekt mit dem zuvor gespeicherten Objekt ueberpruefen.
 * </ul>
 */
public interface AKModelWatcher {

    /**
     * Fuegt der Klasse ein Objekt hinzu, das auf Aenderungen ueberwacht werden soll. <br>
     *
     * @param name    Name fuer das Objekt.
     * @param toWatch Objekt, das ueberwacht werden soll.
     */
    public void addObjectToWatch(String name, Object toWatch);

    /**
     * Entfernt alle eingetragenen Objekte aus der Ueberwachung.
     */
    public void removeObjectsFromWatch();

    /**
     * Entfernt ein best. Objekt von der Ueberwachung.
     *
     * @param name Name des Objekts, das entfernt werden soll.
     */
    public void removeObjectFromWatch(String name);

    /**
     * Ueberprueft, ob das Modell <code>actualModel</code> mit der Kopie uebereinstimmt, die unter dem Namen
     * <code>name</code> gespeichert ist. <br>
     *
     * @param name        Name des Modells
     * @param actualModel evtl. geaendertes Objekt, das mit der Kopie verglichen werden soll.
     * @return true: Objekt hat sich geaendert
     */
    public boolean hasChanged(String name, Object actualModel);

}


