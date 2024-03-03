/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 08:49:31
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;


/**
 * DAO-Interface fuer ServiceCommand-Objekte.
 *
 *
 */
public interface ServiceCommandDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Ermittelt alle ServiceCommands, die einer bestimmten Referenz zugeordnet sind. <br> Die ermittelten Commands
     * werden ueber die OrderNo sortiert (aufsteigend) geliefert.
     *
     * @param refId       ID des Referenz-Modells
     * @param refClass    Referenz-Klasse
     * @param commandType (optional) Typ der zu ermittelnden ServiceCommands
     * @return Liste mit Objekten des Typs <code>ServiceCommand</code>.
     *
     */
    public List<ServiceCommand> findCommands4Reference(Long refId, Class refClass, String commandType);

    /**
     * Loescht die Zuordnung der ServiceCommands zu einer bestimmten Referenz.
     *
     * @param refId    Id des Referenz-Modells von dem die Command-Zuordnung entfernt werden soll
     * @param refClass Referenz-Klasse
     * @return Anzahl geloeschter Datensaetze
     *
     */
    public int deleteCommands4Reference(Long refId, Class refClass);

    /**
     * Loescht die angegebene Service-Chain.
     *
     * @param chainId
     */
    public void deleteServiceChain(Long chainId);

}


