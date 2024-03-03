/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2012 15:22:50
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungFreigabeInfo;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Interface definiert Funktionen fuer die Freigabe von Rangierungen.
 */
public interface RangierungFreigabeService extends ICCService {

    /**
     * Erstellt eine Map mit zur Freigabe bereitstehenden Rangierungen. 1 (Rangierung) zu n (Auftraegen).
     */
    Map<Long, List<PhysikFreigebenView>> createPhysikFreigabeView(Date freigabeDatum,
            List<PhysikFreigebenView> freigabeList, Boolean onlyKlaerfaelle) throws FindException;

    /**
     * Gibt die angegebenen Rangierungen frei. <br> Hierbei wird das Freigabedatum sowie die Endstellen-ID der
     * Rangierung auf <code>NULL</code> gesetzt. <br> Das Status-Feld <code>Rangierung.freigegeben</code> bleibt
     * unveraendert. <br> Dadurch ist sichergestellt, dass Rangierungen, die gesperrt wurden (z.B. wg.
     * Backbone-Auslastung) nach einer Kuendigung nicht wieder vergeben werden. <br>
     */
    void rangierungenFreigeben(Map<Long, List<PhysikFreigebenView>> rangierungRelationen) throws StoreException;

    /**
     * Gibt eine einzelne Rangierung unter Beachtung des jeweiligen Physiktyps frei<br> <ul> <li>{@code
     * eqIn.manualConfiguration = false} <li>{@code esId = null } <li>{@code FreigabeAb = null } <li>FTTH <ul>
     * <li>{@code freigegeben = gesperrt} <li>{@code gueltigBis = freigabeAb} <li>{@code rangierung.eqIn.status = frei}
     * <li>{@code FreigabeAb = null } </ul> <li>FTTB <ul> <li>{@code eqOut = null} <li>{@code FreigabeAb = null } </ul>
     * <li>Andere <ul> <li>{@code FreigabeAb = null } <li>Bricht Rangierung (eqIn und/oder eqOut) eventuell
     * (GEWOFAG/Sheridan) auf </ul> </ul>
     */
    Rangierung freigebenRangierung(Rangierung rang, String bemerkung, boolean makeHistory) throws StoreException;

    /**
     * Aktualisiert FreigabeKlärfallInfo (@code RANG_CLEARANCE_INFO) der Rangierungen
     */
    AKWarnings saveRangierungFreigabeInfos(Map<Long, List<PhysikFreigebenView>> rangierungRelationen) throws StoreException;

    /**
     * Jede View (und somit die Rangierung der View) wird geprüft, ob die automatische Freigabe greift. Wenn ja, ist das
     * Flag 'freigeben' gesetzt, wenn nein, ist die ClarifyInfo mit einem Text versehen.
     */
    AKWarnings prepareAutomaticClearance(Map<Long, List<PhysikFreigebenView>> rangierungRelationen) throws Exception;

    /**
     * Sucht RangierungsFreigabeInfo
     */
    RangierungFreigabeInfo findRangierungFreigabeInfo(Long rangierId, Long auftragId) throws FindException;

    /**
     * Speichert RangierungsFreigabeInfo
     */
    void saveRangierungFreigabeInfo(RangierungFreigabeInfo toSave) throws StoreException;

    /**
     * Entfernt die Rangierungen von der Endstelle, falls folgende Konsistenzprüfungen erfüllt sind: <ul> <li>Die
     * übergebene Rangierung muss der übergebenen Endstelle zugeordnet sein und umgekehrt (bidirektionale
     * Beziehung),</li> <li>Rangierung ist freigegeben,</li> <li>Rangierung keine Historie besitzen,</li>
     * <li>Auftragsstatus des zur Endstelle gehörigen Auftrags muss kleiner als Projektierung sein und</li>
     * <li>Physik-Übernahme zum Auftrag der Endstelle darf nicht existieren</li> <li> für die verbundene
     * Carrierbestellung (Endstelle) darf keine Leitungsbezeichnung, keine Vertragsnummer eingetragen sein sowie kein
     * elektronischer Vorgang existieren.</li> </ul>
     *
     * @param endstelle     die Endstelle der zugehörigen Rangierung wird für Konsistenzchecks benötigt
     * @param rangierung    die Rangierung die von der übergebenen Endstelle entfernt werden soll
     * @param rangierungAdd die zusätzliche Rangierung die von der übergebenen Endstelle entfernt werden soll
     * @param freigabeAb    Optional: Datum, zu dem die Rangierung wieder freigabebereit sein soll.
     * @param bemerkung     Bemerkung fuer die Rangierung
     * @param sessionId     ID der aktuellen User-Session
     * @throws StoreException falls einer der Konsistenzchecks fehlschlägt oder das Entfernen nicht funktioniert
     */
    void removeRangierung(Endstelle endstelle, Rangierung rangierung, Rangierung rangierungAdd,
            Date freigabeAb, String bemerkung, Long sessionId) throws StoreException;


    /**
     * Gibt die {@link Rangierung}en zu den übergebenen Rangierungs-Ids frei, falls folgende Konsistenzprüfungen für
     * alle übergebenen Rangierungen erfüllt sind: <ul> <li>gegebene {@link Rangierung}en sind freigegeben (Endstelle-Id
     * ist {@link Rangierung#RANGIERUNG_NOT_ACTIVE})</li> <li>das {@code FREIGABE_AB}-Datum muss erreicht bzw.
     * überschritten oder {@code null} sein</li> </ul> Falls alle Bedingungen erfüllt sind, werden die Bemerkung, die
     * Endstelle-Id und das Freigabe-Ab-Datum der Rangierung auf {@code null} sowie UserW bzw. DateW auf den aktuellen
     * Benutzer (über {@code sessionId}) bzw. Datum gesetzt. <b>Achtung: </b> Falls {@code rangierId} und {@code
     * rangierIdAdd} übergeben werden, wird nicht überprüft, ob die beiden {@link Rangierung}en der selben {@link
     * Endstelle} zugeordnet sind. Lediglich die {@code LEITUNG_GESAMT_ID} wird verglichen.
     *
     * @param rangierId    der {@link Rangierung}, die freigegeben werden soll (not {@code null})
     * @param rangierIdAdd der optionalen {@link Rangierung}, die ebenfalls freigegeben werden soll (not {@code null})
     * @param sessionId    Id der aktuellen Session (um den zugehörigen Benutzer zu finden)
     * @return {@code true} falls die {@link Rangierung}en freigegeben wurden, ansonsten {@code false} (min. ein
     * Konsistenzcheck ist fehlgeschlagen)
     * @throws IllegalArgumentException falls keine Rangierung zur übergebenen {@code rangierId} oder falls die {@code
     *                                  LEITUNG_GESAMT_ID} nicht übereinstimmt
     * @throws StoreException           falls beim Freigeben der Rangierung(en) ein Fehler auftritt bzw. kein Benutzer
     *                                  zur übergebenen {@code sessionId} gefunden wird
     */
    boolean rangierungenFreigeben(Long rangierId, Long rangierIdAdd, Long sessionId) throws StoreException;

    boolean migrateAtm2EfmIfPossible(final Rangierung rangierung) throws StoreException, FindException;

    /**
     * Funktion gibt MDU- oder DPU-Rangierungen frei
     *
     * @param rackId Rack-Id der MDU
     * @param date   Freigabedatum
     * @throws StoreException
     */
    void freigabeMduDpuRangierungen(Long rackId, Date date) throws StoreException;

    /**
     * Funktion gibt ONT-Rangierungen frei
     *
     * @param rackId         Rack-Id der ONT
     * @param date           Freigabedatum
     * @param reuseEquipment ONT = beenden, DPO (Verteiler-Stift) = wiederverwenden
     * @throws FindException,StoreException
     */
    void beendenHwRackRangierungen(Long rackId, Date date, boolean reuseEquipment) throws FindException, StoreException;

}


