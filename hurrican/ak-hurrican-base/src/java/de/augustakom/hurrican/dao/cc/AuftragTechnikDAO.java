/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2004 08:27:46
 */
package de.augustakom.hurrican.dao.cc;


import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragTechnik;

/**
 * DAO-Interface fuer Objekte des Typs <code>AuftragTechnik</code>.
 *
 *
 */
public interface AuftragTechnikDAO extends FindDAO, StoreDAO, HistoryUpdateDAO<AuftragTechnik> {

    /**
     * Sucht nach einem AuftragTechnik-Datensatz mit der Auftrag-ID <code>auftragId</code>. Zusaetzlich muss das
     * Gueltig-von und Gueltig-bis Datum aktuell sein.
     *
     * @param auftragId die Auftrags-ID
     * @return Instanz von AuftragTechnik.
     */
    AuftragTechnik findByAuftragId(Long auftragId);

    /**
     * Sucht nach der aktuellen AuftragTechnik, die einer best. Endstellen-Gruppe zugeordnet ist.
     *
     * @param esGruppeId ID der Endstellen-Gruppe deren AuftragTechnik gesucht wird
     * @return Instanz von AuftragTechnik, oder {@code null}
     */
    AuftragTechnik findAuftragTechnik4ESGruppe(Long esGruppeId);

    /**
     * Sucht nach allen AuftragTechnik-Eintraegen mit einem best. IntAccount.
     *
     * @param intAccountId
     * @return Liste mit Objekten des Typs <code>AuftragTechnik</code>.
     */
    List<AuftragTechnik> findByIntAccountId(Long intAccountId);

    /**
     * Setzt die IntAccount-ID der AuftragTechnik von <code>intAccIdOld</code> auf <code>intAccIdNew</code>. <br>
     * Wichtig: es wird nur der aktuelle Datensatz geaendert!!!
     *
     * @param intAccIdNew ID des neuen IntAccounts
     * @param intAccIdOld ID des alten IntAccounts
     */
    void update4IntAccount(Long intAccIdNew, Long intAccIdOld);

    /**
     * Sucht nach einer AuftragTechnik fuer eine vbzId.
     *
     * @param vbzId VBZ nach der gesucht wird
     * @return AuftragTechnik oder null
     */
    AuftragTechnik findAuftragTechnik4VbzId(Long vbzId);

    /**
     * Sucht nach AuftragTechniken fuer eine vbzId.
     *
     * @param vbzId VBZ nach der gesucht wird
     * @return AuftragTechniken oder leere Liste
     */
    List<AuftragTechnik> findAuftragTechniken4VbzId(Long vbzId);
}


