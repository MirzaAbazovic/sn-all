/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetailView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugMasterView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.service.base.exceptions.ExportException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface definiert Funktionen fuer einen HVT-Umzug.
 */
public interface HvtUmzugService extends ICCService {

    /**
     * Methode ermittelt zu dem angegebenen HvtUmzug alle betroffenen Hurrican Auftraege und traegt die notwendigen
     * Daten in die zum Umzug gehoerenden {@link HvtUmzugDetail}s ein. <br/> Falls die Methode zusaetzliche
     * Hurrican-Auftraege findet, die vom Umzug betroffen sind, so werden diese ebenfalls mit eingefuegt.
     *
     * @param hvtUmzug Hvt-Umzug mit den Master-Daten, zu dem die Hurrican-Auftraege ermittelt werden sollen
     */
    void matchHurricanOrders4HvtUmzug(HvtUmzug hvtUmzug) throws StoreException;

    /**
     * Erzeugt ein {@link HvtUmzug}s Objekt sowie eine zughörige {@link de.augustakom.hurrican.model.cc.KvzSperre} wenn
     * der HVTUmzug im Status {@link HvtUmzugStatus#OFFEN} steht.
     *
     * @param hvtUmzug vorinitalisiertes {@link HvtUmzug}s objekt.
     * @throws StoreException
     */
    HvtUmzug createHvtUmzug(HvtUmzug hvtUmzug) throws StoreException;

    /**
     * Persistiert ein {@link HvtUmzug}s - Objekt.
     */
    HvtUmzug saveHvtUmzug(HvtUmzug hvtUmzug);

    /**
     * Load the {@link HvtUmzugMasterView} with all needed view data.
     *
     * @param hvtUmzugStatuses all considerable states
     * @return an empyt {@link List} or a {@link List} of matching {@link HvtUmzugMasterView}s
     */
    List<HvtUmzugMasterView> loadHvtMasterData(HvtUmzugStatus... hvtUmzugStatuses);

    /**
     * Load all needed details as {@link HvtUmzugDetailView} data of an {@link HvtUmzug}.
     *
     * @param hvtUmzugId {@link HvtUmzug#id}
     * @return an empyt {@link List} or a {@link List} of {@link HvtUmzugDetailView}s
     * @throws FindException if some subservice have a not expected error.
     */
    List<HvtUmzugDetailView> loadHvtUmzugDetailData(Long hvtUmzugId) throws FindException;

    /**
     * Ermittelt ein {@link HvtUmzug} Objekt ueber dessen Id.
     */
    HvtUmzug findById(Long id);

    /**
     * Ermittelt ein {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail} Objekt ueber dessen Id.
     */
    HvtUmzugDetail findDetailById(Long id);

    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug} Datensaetze, die zu 'heute' ausgefuehrt
     * werden sollen.
     */
    List<HvtUmzug> findForCurrentDay();

    /**
     * Ermittelt alle 'alten' {@link de.augustakom.hurrican.model.cc.HVTStandort} IDs, die jemals umgezogen werden
     * mussten oder noch muessen. D.h. es werden die IDs der Standorte ermittelt, die geschlossen werden sollen oder
     * sind.
     */
    Set<Long> findAffectedStandorte4Umzug();

    /**
     * Ermittelt, ob der entsprechende KVZ bereits umgezogen wurde (HvtUmzugStatus = BEENDET oder AUSGEFUEHRT)
     * @param standort          zu prüfender HvtStandort
     * @param kvzNr             zu überprüfender Kvz
     * @return  Set mit HvtUmzugId's des entsprechenden kvz in HvtUmzug
     */
    Set<Long> findKvz4UmzugWithStatusUmgezogen(HVTStandort standort, String kvzNr);

    /**
     * setzt ein @link de.augustakom.hurrican.model.cc.HvtUmzug} von Status 'offen' auf Status 'deaktiviert'
     *
     * @param id id des HvtUmzug
     * @return das geupdatete Objetkt oder andernfalls null
     */
    HvtUmzug disableUmzug(Long id);

    /**
     * Loescht das angegebene {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail} Objekt. <br/>
     * Evtl. verlinkte (neue) Rangierungen werden dabei ueber
     * {@link de.augustakom.hurrican.service.cc.HvtUmzugService#unlockRangierung(de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail, boolean)}
     * freigegeben.
     * @param toDelete zu loeschender Datensatz
     */
    void deleteHvtUmzugDetail(@Nonnull HvtUmzugDetail toDelete);

    /**
     * Ermittelt die neue Rangierung aus dem {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail} und
     * entsperrt diese - die Rangierung steht somit wieder fuer eine neue Vergabe bereit.
     * @param hvtUmzugDetail
     * @param removeFromDetail wenn {@code true}, dann werden die Informationen des neuen Ports von dem HVT-Detail
     *                         entfernt.
     */
    void unlockRangierung(final HvtUmzugDetail hvtUmzugDetail, boolean removeFromDetail);

    /**
     * @param umzugId       zu prüfender {@link HvtUmzug}
     * @param umzugDetailId zu prüfendes Detail
     * @return {@code true} wenn für {@link de.augustakom.hurrican.service.cc.HvtUmzugService#manuellePortPlanung(Long
     * hvtUmzugId, Long hvtUmzugDetailId, Long rangierId, boolean isDefault, boolean isNew)} ein valider Ausgangsstatus
     * vorliegt, anderweitig {@code false}
     */
    boolean manuellePortplanungAllowed(Long umzugId, Long umzugDetailId);

    /**
     * Fuehrt die automatische Port-Planung durch, sammelt aufgetretene Fehler, persistiert erfolgreiche Planungen und
     * liefert die gesammelten Meldungen als Rueckgabewert.
     */
    AKWarnings automatischePortPlanung(Long hvtUmzugId);

    /**
     * Fuehrt die manuelle Port-Planung durch, indem eine Rangierung einem Umzug Detail zugeordnet wird, sammelt
     * aufgetretene Fehler, persistiert erfolgreiche Planungen und liefert die gesammelten Meldungen als Rueckgabewert.
     */
    AKWarnings manuellePortPlanung(Long hvtUmzugId, Long hvtUmzugDetailId, Long rangierId, boolean isDefault, boolean isNew);

    /**
     * Ermittelt fuer den im {@link HvtUmzugDetail} angegebenen Auftrag bzw. Endstelle eine passende Rangierung und
     * sperrt diese. Ausserdem erfolgt hier die Zuordnung der ermittelten Rangierung zum HVT-Umzug bzw. zum {@link
     * HvtUmzugDetail}.
     */
    void reserveRangierung4HvtUmzugDetail(final HvtUmzug hvtUmzug, final HvtUmzugDetail hvtUmzugDetail) throws StoreException, FindException;

    // @formatter:off
    /**
     * Fuehrt die in dem angegebenen {@link HvtUmzug} hinterlegten Planungen aus und
     * setzt den Status auf {@link HvtUmzugStatus#AUSGEFUEHRT}. <br/>
     * Die Ausfuehrung ist nur dann moeglich, wenn der Status auf {@link HvtUmzugStatus#PLANUNG_VOLLSTAENDIG}
     * steht. <br/>
     * <br/>
     * Bei der Ausfuehrung werden folgende Aktionen ausgefuehrt:
     * <ul>
     *     <li>'neue' Rangierungen: ES_ID der 'alten' Rangierung wird uebernommen; Status auf 'freigegeben'</li>
     *     <li>'alte' Rangierungen: ES_ID auf NULL</li>
     *     <li>Endstelle erhaelt neue Rangier-ID(s)</li>
     *     <li>Status von {@link HvtUmzug} wird geaendert</li>
     * </ul>
     *
     * @param hvtUmzug  HVT-Umzug, der ausgefuehrt werden soll
     * @param sessionId aktuelle Session-Id des Users
     */
    // @formatter:on
    AKWarnings executePlanning(final HvtUmzug hvtUmzug, Long sessionId);

    /**
     * Ermittelt zu dem angegebenen {@link HvtUmzug} alle betroffenen Auftraege und schickt fuer diese ein CPS
     * 'modifySubscriber' (sofern der Auftrag fuer CPS erlaubt ist). <br/> Auftretende Fehler werden als {@link
     * de.augustakom.common.tools.messages.AKWarning} gesammelt und zurueck gegeben.
     *
     * @param hvtUmzug  der HVT-Umzug, zu dessen Details CPS modifySubscriber erzeugt werden sollen
     * @param simulate Flag gibt an, ob die CPS-Ausfuehrung wirklich durchgefuehrt (false) oder nur simuliert (true)
     *                 werden soll
     * @param sessionId die aktuelle SessionId des Users
     * @return AKWarnings mit den aufgetretenen Fehlern
     */
    @Nonnull
    AKWarnings sendCpsModifies(final HvtUmzug hvtUmzug, boolean simulate, Long sessionId);

    /**
     * ändert den Status eines {@link HvtUmzug}s auf {@link HvtUmzugStatus#BEENDET}, falls sich dieser im Status
     * {@link HvtUmzugStatus#AUSGEFUEHRT} befindet.
     *
     * @param hvtUmzugId id des betroffenen {@link HvtUmzug}s
     * @return {@link de.augustakom.common.tools.lang.Either}, im Fehlerfall ist Left mit Fehlermeldungen gefüllt, bei
     * Erfolg das geänderte Objekt
     */
    Either<AKWarnings, HvtUmzug> closeHvtUmzug(final Long hvtUmzugId);

    /**
     * ändert den Status eines {@link HvtUmzug}s auf {@link HvtUmzugStatus#PLANUNG_VOLLSTAENDIG}, falls sich dieser im Status
     * {@link HvtUmzugStatus#OFFEN} befindet. <br/>
     * Bedingung dafuer: alle Ports mit zugeordnetem Auftrag besitzen auch einen neuen Port
     *
     * @param hvtUmzugId id des betroffenen {@link HvtUmzug}s
     * @return {@link de.augustakom.common.tools.lang.Either}, im Fehlerfall ist Left mit Fehlermeldungen gefüllt, bei
     * Erfolg das geänderte Objekt
     */
    Either<AKWarnings, HvtUmzug> markHvtUmzugAsDefined(final Long hvtUmzugId);


    /**
     * Erzeugt eine Excel-Datei mit allen Aufträgen und neuen Ports zu den angegebenen HvtUmzug-Id. Die Datei wird auf
     * Basis der Import-Datei erstellt, so dass die Import-Datei mit den neuen Port-Informationen aufgefüllt wird. Alle
     * zusätzliche Aufträge werden am Ende der Datei aufgelistet
     */
    byte[] exportPortsForHvtUmzug(Long hvtUmzugId) throws FindException, ExportException;

    /**
     * Transaktion Wrapper um die Cross Connections einer Endstelle zu deaktivieren
     */
    void deactivateCcs4Endstelle(Endstelle endstelle)throws StoreException, FindException;

    /**
     * Transaktion Wrapper um das DSLAM Profil umzuziehen
     */
    void transferDSLAMProfil(HvtUmzug hvtUmzug, HvtUmzugDetail detail, Long sessionId);

    /**
     * Transaktion Wrapper um die Default Cross Connections einer Endstelle zu berechnen
     */
    AKWarnings calculateDefaultCcs(HvtUmzug hvtUmzug, HvtUmzugDetail detail, Endstelle endstelle, Long sessionId)
            throws FindException, StoreException;

}
