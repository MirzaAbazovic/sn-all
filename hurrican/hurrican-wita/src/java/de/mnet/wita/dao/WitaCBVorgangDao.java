/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2011 17:46:59
 */
package de.mnet.wita.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Implementierung Datenzugriff auf WitaCBVorgaenge
 */
public interface WitaCBVorgangDao extends FindDAO, StoreDAO {

    /**
     * Ermittelt CBVorgaenge, die mit der uebergebenen Auftragsnummer oder der übergebenen CB-ID uebereinstimmt
     * absteigend sortiert nach der CBVorgang Id
     */
    List<CBVorgang> findCbVorgaengeByAuftragOrCBId(Long auftragId, Long cbId);

    /**
     * Ermittelt alle {@link WitaCBVorgang} Objekte die automatisch abgeschlosen werden können.
     *
     * @return
     */
    List<WitaCBVorgang> findWitaCBVorgaengeForAutomation(Long... orderType);

    /**
     * Ermittelt alle WitaCbVorgang-IDs in die mit der angegeben Klammer-ID verknüpft sind.
     *
     * @param klammerId Klammerungsnummer der Wita-Vorgänge
     * @return Liste an WitaVorgangs-IDs
     */
    List<Long> findWitaCBVorgangIDsForKlammerId(Long klammerId);

    /**
     * Searches for a {@link WitaCBVorgang} Objekte that has a {@code cbVorgangRefId} matching the supplied {@code
     * cbVorgangRefId}.
     *
     * @param cbVorgangRefId the reId to match against
     * @return the matching {@link WitaCBVorgang} or null
     */
    WitaCBVorgang findWitaCBVorgangByRefId(Long cbVorgangRefId);

}
