/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2009 13:46:19
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer Konfigurationen, die den CPS betreffen.
 *
 *
 */
public interface CPSConfigService extends ICCService {

    /**
     * Ermittelt das CPSDataChainConfig-Objekt zu den angegebenen Suchparametern.
     *
     * @param prodId                Produkt-ID
     * @param serviceOrderTypeRefId Reference-ID des ServiceOrder-Types
     * @return CPSDataChainConfig-Objekt, ueber das die Service-Chain fuer die Ermittlung der Provisionierungsdaten
     * definiert ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public CPSDataChainConfig findCPSDataChainConfig(Long prodId, Long serviceOrderTypeRefId)
            throws FindException;

    /**
     * Ermittelt alle CPSDataChainConfig-Objekte, die einem bestimmten Produkt zugeordnet sind.
     *
     * @param prodId ID des Produkts
     * @return Liste mit Objekten des Typs <code>CPSDataChainConfig</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<CPSDataChainConfig> findCPSDataChainConfigs(Long prodId) throws FindException;

    /**
     * Speichert das angegebene Objekt.
     *
     * @param toStore zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    public void storeCPSDataChainConfig(CPSDataChainConfig toStore) throws StoreException;

    /**
     * Loescht ein bestehendes CPSDataChainConfig-Objekt.
     *
     * @param toDelete zu loeschendes Objekt
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt
     *
     */
    public void deleteCPSDataChainConfig(CPSDataChainConfig toDelete) throws DeleteException;

}


