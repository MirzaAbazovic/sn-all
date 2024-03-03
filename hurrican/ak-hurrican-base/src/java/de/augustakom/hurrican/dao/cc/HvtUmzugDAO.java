/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;

/**
 * DAO Interface fuer Queries bezogen auf den {@link HvtUmzug}.
 */
public interface HvtUmzugDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Ermittelt alle Auftraege / Endstellen, die an GeoIds von {@code hvtIdStandort} und {@code kvzNr} angeschlossen
     * sind und gibt diese als {@link de.augustakom.common.tools.lang.Pair} zurueck. <br/>
     * Das Query uebernimmt eine Basis-Filterung der Auftraege ueber den Status; folgende Stati werden ausgefiltert:
     * <ul>
     *     <li>{@link de.augustakom.hurrican.model.cc.AuftragStatus#STORNO}</li>
     *     <li>{@link de.augustakom.hurrican.model.cc.AuftragStatus#ABSAGE}</li>
     *     <li>{@link de.augustakom.hurrican.model.cc.AuftragStatus#AUFTRAG_GEKUENDIGT}</li>
     *     <li>{@link de.augustakom.hurrican.model.cc.AuftragStatus#KONSOLIDIERT}</li>
     * </ul>
     * Alle weiteren notwendigen Filterungen (z.B. ueber Datum) muessen vom Aufrufer durchgefuehrt werden.
     * @param hvtUmzugId ID des betroffenen {@link HvtUmzug}s
     * @return Liste mit einem Pair aus der Hurrican Auftrags-ID sowie Endstellen-Typ
     */
    List<Pair<Long, String>> findAuftraegeAndEsTypForHvtUmzug(Long hvtUmzugId);

    List<HvtUmzug> findHvtUmzuegeWithStatus(HvtUmzugStatus... status);

    Set<Long> findAffectedStandorte4UmzugWithoutStatus(HvtUmzugStatus... status);

    /**
     * Loescht ein {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail} Objekt.
     * @param toDelete zu loeschender Datensatz
     */
    void deleteHvtUmzugDetail(@Nonnull HvtUmzugDetail toDelete);

    /**
     * Ermittelt, ob der entsprechende Kvz bei den HVT-Umzügen schon umgezogen ist
     * (HvtUmzugStatus = BEENDET oder AUSGEFUEHRT)
     * @param standort              zu prüfender HvtStandort
     * @param kvzNr                 zu prüfende KvzNr
     * @return  Set mit HvtUmzugId's des entsprechenden kvz in HvtUmzug
     */
    Set<Long> findKvz4HvtUmzugWithStatusUmgezogen(HVTStandort standort, String kvzNr);

}
