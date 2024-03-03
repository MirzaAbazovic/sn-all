/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 10:35:13
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Verwaltung von Service-Chains und Command-Objekten.
 *
 *
 */
public interface ChainService extends ICCService {

    /**
     * Sucht nach allen definierten Service-Chains.
     *
     * @param chainType Angabe des Chain-Typs, zu dem die Chains geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>ServiceChain</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<ServiceChain> findServiceChains(String chainType) throws FindException;

    /**
     * Speichert die angegebene ServiceChain ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveServiceChain(ServiceChain toSave) throws StoreException;

    /**
     * Loescht die angegebene Service-Chain.
     *
     * @param chainId ID der zu loeschenden Chain
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    public void deleteServiceChain(Long chainId) throws DeleteException;

    /**
     * Ermittelt die ServiceCommands zu einer bestimmten Referenz.
     *
     * @param refId       ID des Referenz-Modells
     * @param refClass    Angabe der Referenz-Klasse
     * @param commandType (optional) Angabe, welche Art von Command ermittelt werden soll (Konstante aus
     *                    ServiceCommand).
     * @return Liste mit Objekten des Typs <code>ServiceCommand</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<ServiceCommand> findServiceCommands4Reference(Long refId, Class<?> refClass, String commandType)
            throws FindException;

    /**
     * Ordnet einer bestimmten Referenz die ServiceCommands aus der Liste <code>commands</code> zu.
     *
     * @param refId    ID des Referenz-Objekts
     * @param refClass Typ des Referenz-Objekts
     * @param commands Liste mit den Commands, die zugeordnet werden sollen.
     * @throws StoreException wenn bei der Zuordnung ein Fehler auftritt.
     *
     */
    public void saveCommands4Reference(Long refId, Class<?> refClass, List<ServiceCommand> commands)
            throws StoreException;

    /**
     * Ermittelt eine Liste mit allen Commands, die fuer Physik-Aenderungen zustaendig sind.
     *
     * @param commandTyp Typ der gesuchten ServiceCommands (Konstante aus ServiceCommand).
     * @return Liste mit Objekten des Typs <code>ServiceCommand</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<ServiceCommand> findServiceCommands(String commandTyp) throws FindException;

    /**
     * Löscht alle Commands, die einer Referenz zugeordnet sind.
     *
     * @param refId
     * @param refClass
     * @throws StoreException
     *
     */
    public void deleteCommands4Reference(Long refId, Class<?> refClass) throws DeleteException;
}


