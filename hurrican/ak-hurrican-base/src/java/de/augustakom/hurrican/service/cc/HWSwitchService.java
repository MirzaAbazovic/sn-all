/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 15:33:59
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.UpdateException;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.SwitchMigrationLog;
import de.augustakom.hurrican.model.cc.SwitchMigrationLogAuftrag;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

/**
 * Interface zur Definition eines Services, der fuer Switche zustaendig ist.
 *
 *
 * @since Release 10
 */
public interface HWSwitchService extends ICCService {

    /**
     * liefert ein {@link HWSwitch} anhand seines Namens.
     *
     * @param name
     * @return
     */
    HWSwitch findSwitchByName(String name);

    /**
     * liefert alle bekannten {@link HWSwitch}es.
     *
     * @return
     */
    List<HWSwitch> findAllSwitches();

    /**
     * prüft die Zertifizierung aller den angegebenen Aufträgen zugeordneten Endgeräten für den angegeben Switch
     *
     * @param auftragIds ids der zu überprüfenden Aufträge
     * @param zielSwitch Zielswitch für den auf Zertifizierung der Endgeräte des Aufträge geprüft werden soll
     * @return Liste von Warnings mit Fehlermeldungen bez. nicht vorhandener Zertifizierungen, oder leere Liste wenn
     * alle Endgeräte mit dem Switch zertifiziert sind
     */
    List<Pair<Long, String>> checkAuftraegeForCertifiedSwitches(List<Long> auftragIds, HWSwitch zielSwitch);

    /**
     * Wandelt den Rückgabewert von {@link HWSwitchService#checkAuftraegeForCertifiedSwitches} in
     * {@link de.augustakom.common.tools.messages.AKWarnings} um.
     */
    AKWarnings checkAuftraegeForCertifiedSwitchesAsWarnings(List<Long> auftragIds, HWSwitch zielSwitch);

    /**
     * liefert alle {@link HWSwitch}es fuer die angegebenen Switch-Typen.
     *
     * @param types
     * @return
     */
    List<HWSwitch> findSwitchesByType(HWSwitchType... types);

    /**
     * Stelle alle angegebenen Aufträge auf den neuen Switch um.
     * Dabei wird Folgendes umgestellt.
     *  - Der Switch auf die AuftragTechnik
     *  - Die SIP-Domäne auf den AuftragVOIPDN
     *  - Der SIP-Login auf alle gerade und in der Zukunf aktuelle Rufnummernpläne
     * @param ordersToMigrate umzuschreibende Auftraege
     * @param sourceSwitch alter switch
     * @param destinationSwitch neuer switch
     * @param sessionId sessionId
     * @param execDate CPS execution time
     * @return XLS Datei mit Migrationsergebnissen
     */
    byte[] moveOrdersToSwitch(List<SwitchMigrationView> ordersToMigrate, HWSwitch sourceSwitch, HWSwitch destinationSwitch,
            Long sessionId, Date execDate) throws ServiceNotFoundException, IOException;

    SwitchMigrationLog createMigrationLog(HWSwitch sourceSwitch, HWSwitch destinationSwitch, Long sessionId, Date execDate)
            throws ServiceNotFoundException, IOException;

    void moveOrderToSwitch(SwitchMigrationView orderToMigrate, SwitchMigrationLog migrationLog, Long sessionId)
            throws ServiceNotFoundException, IOException;

    byte[] createMigrationLogXls(SwitchMigrationLog migrationLog) throws IOException;

    /**
     *
     * @return Either.left == null, wenn kein Switch aktualisiert wurde, Either.left != null wenn der Switch aktualisiert
     * wurde, Either.right != null wenn ein Fehler zum Abbruch gefuehrt hat.
     *                          went wrong.
     */
    Either<String, String> updateHwSwitchBasedComponents(Long auftragId, CalculatedSwitch4VoipAuftrag switchInitialState)
            throws FindException;

    /**
     *
     * @param migrationLog entity to store
     * @return the persisted entity
     */
    SwitchMigrationLog storeMigrationLogNewTx(SwitchMigrationLog migrationLog);

    /**
     *
     * @param migrationLogAuftrag entity to store
     * @return the persisted entity
     */
    SwitchMigrationLogAuftrag storeMigrationLogAuftragNewTx(SwitchMigrationLogAuftrag migrationLogAuftrag);

    /**
     * Ändert den Switch für einen Auftrag in einer neuen Tx.
     *
     * @param auftragId auftrag
     * @param switchTo neuer Switch
     */
    void moveOrderToSwitchNewTx(Long auftragId, HWSwitch switchTo) throws ServiceNotFoundException, FindException, UpdateException;

    //@formatter:off
    /**
     * Check the following criteria and send a update to CPS if needed:
     * <li>
     *     <ul>There is existing Bauauftrag for the order and the realisierungTermin is not in the future</ul>
     *     <ul>The last CPS subscriber transaction is 'modifySubscriber' or 'createSubscriber' </ul>
     * </li>
     *
     * @param auftragId {@link de.augustakom.hurrican.model.cc.AuftragTechnik#getAuftragId()}
     * @param sessionId GUI seesion ID from the User
     * @param execDate {@link Date} > now
     * @return  {@link Pair} with CPS-Tx-Id or info if the CPS TX would have been required and {@link AKWarnings} if some warnings occurred during the CPS execution
     * @throws UpdateException if execDate is in the past.
     */
    //@formatter:on
    Pair<Either<Long, Boolean>, AKWarnings> updateCPSNewTx(Long auftragId, Long sessionId, Date execDate) throws UpdateException, FindException;

}
