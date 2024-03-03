/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 09:55:44
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.net.AKMailException;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Verarbeitung von Locks. <br> Ueber den Service koennen z.B. Sperren per Auftrag
 * eingerichtet sowie die Sperr-Verteilung definiert werden. <br> Die Sperr-Verteilung definiert, bei welchem Produkt
 * welche Abteilung ueber einen Sperrvorgang informiert wird.
 *
 *
 */
public interface LockService extends ICCService {

    /**
     * Speichert das angegebene Lock-Objekt und erstellt die notwendigen Lock-Detail Objekte dazu.<br> Fuer die
     * Lock-Details ermittelt die Methode alle Abteilungen, die auf Grund der Produktkonfiguration fuer die Sperre
     * benoetigt werden. <br><br> Ist fuer eine Abteilung in den Sperren eine eMail-Adresse konfiguriert, wird auch eine
     * entsprechende Benachrichtigungs-Mail erstellt und versendet. <br><br> Ist in dem Lock-Objekt der Parameter
     * <code>parentLockId</code> gesetzt, wird das referenzierte Lock-Objekt geladen und der Status auf 'finished'
     * gesetzt. Ausserdem wird in einem solchen Fall geprueft, ob der neue Lock-Mode auf den urspruenglichen Lock-Mode
     * (des Parents) folgen darf. <br><br> Eine Sperre ohne Referenzierung auf einen Vorgaenger (also ohne gesetztem
     * Parameter <code>parentLockId</code> ist nur erlaubt, wenn zu dem Auftrag keine aktive Sperre vorhanden ist.
     *
     * @param Lock      Das zu erstellende Lock-Object
     * @param sessionId aktuelle Session-ID des Benutzers. Darueber wird der User-Name ermittelt, der die Sperre
     *                  veranlasst hat.
     * @throws StoreException
     * @throws AKMailException
     */
    public void createLock(Lock lock, Long sessionId)
            throws StoreException, AKMailException;

    /**
     * Ermittelt alle aktiven Lock-Eintraege. <br>
     *
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Lock> findActiveLocks() throws FindException;

    /**
     * Sucht nach allen Sperr-Vorgaengen fuer einen technischen Auftrag.
     *
     * @param auftragId  Auftragnummer, dessen Locks gesucht werden.
     * @param onlyActive Wenn gesetzt, wird nur nach aktiven Locks gesucht.
     * @return Liste mit Objekten des Typs <code>Lock</code> (sortiert nach ID und Erstellungsdatum)
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Lock> findLocks4Auftrag(Long auftragId, boolean onlyActive) throws FindException;

    /**
     * Ermittelt alle Lock-Eintraege, die dem Example-Objekt entsprechen.
     *
     * @param example Example-Objekt
     * @return Liste mit den zum Example passenden Lock-Eintraegen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Lock> findLocksByExample(Lock example) throws FindException;

    /**
     * Ermittelt alle Lock-Detail Eintraege, die dem Example-Objekt entsprechen.
     *
     * @param example Example-Objekt
     * @return Liste mit den zum Example passenden LockDetail Objekten
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<LockDetail> findLockDetailsByExample(LockDetail example) throws FindException;

    /**
     * Ermittelt ein Lock-Objekt ueber die ID.
     *
     * @param lockId
     * @return
     * @throws FindException
     */
    public Lock findLock(Long lockId) throws FindException;

    /**
     * Speichert ein Objekt des Typs <code>Lock</code> ab. <br>
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveLock(Lock toSave) throws StoreException;

    /**
     * Ueberprueft, ob fuer den Auftrag eine aktive Sperre vorhanden ist. (Lock.LOCK_STATE_REF_ID == 'active')
     *
     * @param auftragId ID des Auftrags
     * @return true, falls eine aktive Sperre zu dem Auftrag existiert.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public boolean hasActiveLock(Long auftragId) throws FindException;

    /**
     * Ermittelt alle <code>LockDetail</code> Objekte zu dem ueber die ID angegebenen Lock-Vorgang.
     *
     * @param lockId ID des Lock-Vorgangs
     * @return Liste mit allen zugehoerigen LockDetail Eintraegen.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<LockDetail> findLockDetails(Long lockId) throws FindException;

    /**
     * Speichert ein Objekt des Typs <code>LockDetail</code> ab. <br>
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveLockDetail(LockDetail toSave) throws StoreException;

    /**
     * Beendet alle aktiven Locks eines techn. Auftrags.
     */
    public void finishActiveLocks(Long auftragId) throws StoreException;
}
