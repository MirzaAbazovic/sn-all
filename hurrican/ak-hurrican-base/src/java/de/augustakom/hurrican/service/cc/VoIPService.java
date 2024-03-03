/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 09:43:36
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;

/**
 * Service-Interface fuer die Verwaltung von VoIP Daten.
 *
 *
 */
 public interface VoIPService extends ICCService {

    /**
     * Erstellt einen VoIP-Zusatz fuer den angegebenen Auftrag. <br> Falls der Auftrag bereits einen aktiven VoIP-Zusatz
     * hat wird dieser zurueck gegeben!
     *
     * @param auftragId ID des Auftrags, der einen VoIP-Zusatz erhalten soll.
     * @param sessionId Session-ID des aktuellen Users.
     * @return erzeugter VoIP-Zusatz vom Typ <code>AuftragVoIP</code>
     * @throws StoreException wenn bei der Generierung vom VoIP-Zusatz ein Fehler auftritt.
     *
     */
     AuftragVoIP createVoIP4Auftrag(Long auftragId, Long sessionId) throws StoreException;

    /**
     * Ermittelt die VoIP-Daten zu einem Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen VoIP Daten gesucht werden.
     * @return Instanz vom Typ <code>AuftragVoIP</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     AuftragVoIP findVoIP4Auftrag(Long auftragId) throws FindException;

    /**
     * Speichert den angegebenen VoIP-Zusatz.
     *
     * @param toSave        zu speicherndes Objekt
     * @param createHistory Flag, ob von dem Datensatz eine Historisierung erzeugt werden soll.
     * @param sessionId     Session-ID des aktuellen Users
     * @return AuftragVoIP-Objekt (entweder das uebergebene Objekt oder das durch die Historisierung neu erzeugte
     * Objekt).
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
     AuftragVoIP saveAuftragVoIP(AuftragVoIP toSave, boolean createHistory, Long sessionId)
            throws StoreException;

    /**
     * Version der nachfolgenden Funktion <code>createVoIPDN4Auftrag</code> ohne Passwort. Das Passwort wird automatisch
     * generiert.
     *
     * @param auftragId ID des Auftrags, der einen VoIP-Zusatz erhalten soll.
     * @param dnNoOrig  eine Dialnummer zum Auftrag.
     * @return erzeugter VoIPDN-Zusatz vom Typ <code>AuftragVoIPDN</code>
     * @throws StoreException wenn bei der Generierung vom VoIPDN-Zusatz ein Fehler auftritt.
     *
     */
     AuftragVoIPDN createVoIPDN4Auftrag(Long auftragId, Long dnNoOrig) throws StoreException;

    /**
     * Erstellt einen VoIPDN-Zusatz fuer den angegebenen Auftrag.
     * <br> Falls die Rufnummer auf diesem Auftrag bereits VoIP-Daten
     * besitzt werden diese zurueck gegeben und nicht erneut angelegt!
     *
     * @param auftragId ID des Auftrags, der einen VoIP-Zusatz erhalten soll.
     * @param dnNoOrig  eine Dialnummer zum Auftrag.
     * @param pw        das Passwort zur Dialnummer.
     * @return erzeugter VoIPDN-Zusatz vom Typ <code>AuftragVoIPDN</code>
     * @throws StoreException wenn bei der Generierung vom VoIPDN-Zusatz ein Fehler auftritt.
     *
     */
     AuftragVoIPDN createVoIPDN4Auftrag(Long auftragId, Long dnNoOrig, String pw) throws StoreException;

    /** Generiert SipLogin und SipHauptrufnummer für Blockrufnummern und speichert diese in den VoIP-Plan Daten der Rufnummer
     *  ab.
     * @param target      VoipDnPlan fuer den die Login Daten generiert werden
     * @param activePlan Zum aktuellen Zeitpunkt aktiver VoipDnPlan
     * @param onkz      Ortsnetzkennzahl
     * @param dnBase    Basisrufnummer
     * @param rangeFrom Range-From, wird nur mitgegeben, wenn keine Zentrale vorhanden ist
     * @param sipDomain Sip domain der Voip Rufnummer
     * @return erzeugter VoIPDN-Plan vom Typ <code>VoipDnPlan</code>
     * @throws StoreException
     */
     void createVoIPLoginDaten(VoipDnPlan target, VoipDnPlan activePlan, String onkz, final String dnBase,
            final String rangeFrom, String sipDomain) throws StoreException;

    /** Generiert SipHauptrufnummer für Block- und Einzelrufnummern
     * @param onkz      Ortsnetzkennzahl
     * @param dnBase    Basisrufnummer bzw. Einzelrufnummer
     * @param start     Blockrufnummern: Range-From wird mitgegeben, wenn keine Zentrale vorhanden ist, sonst Start von Zentrale
     *                  Einzelrufnummern: leer
     * @return erzeugte SipHauptrufnummer
     */
     String generateSipHauptrufnummer(final String onkz, final String dnBase, final String start);


    /**
     * Ermittelt die VoIP-Daten zu einer Rufnummer eines Auftrags (dies kann nur kein oder ein Satz sein).
     *
     * @param auftragId ID des Auftrags, dessen VoIPDN Daten gesucht werden.
     * @param dnNoOrig  ID der Dialnumber, für die das VoIPDN-Objekt gesucht werden.
     * @return Instanz vom Typ <code>AuftragVoIPDN</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     AuftragVoIPDN findByAuftragIDDN(Long auftragId, Long dnNoOrig) throws FindException;

    /**
     * Ermittelt die VoIP-Daten zu allen Rufnummern eines Auftrags.
     */
     List<AuftragVoIPDN> findByAuftragId(Long auftragId) throws FindException;

    /**
     * Ermittelt Views mit Rufnummern-Daten sowie dem VoIP-Passwort zu dem angegebenen Auftrag. <br> Folgende Rufnummern
     * werden beruecksichtigt: <br> <ul> <li>Rufnummern mit Status ALT/AKT/NEU <li>Rufnummern, die dem zugehoerigen
     * Taifun-Auftrag zugeordnet sind </ul>
     *
     * @param auftragId ID des Auftrags, dessen VoIP-DN Daten gesucht werden.
     * @return Liste von Instanzen vom Typ <code>AuftragVoipDNView</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     List<AuftragVoipDNView> findVoIPDNView(Long auftragId) throws FindException;

    /**
     * liefert
     *
     * @param dialNumbers
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    List<AuftragVoipDNView> createVoipDNView(Collection<AuftragVoIPDN> dialNumbers) throws FindException,
            ServiceNotFoundException;

    /**
     * Speichert den angegebenen VoIPDN-Zusatz.
     *
     * @param toSave zu speicherndes Objekt
     * @return void
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
     void saveAuftragVoIPDN(AuftragVoIPDN toSave) throws StoreException;

    /**
     * Speichert für jede VoIP Rufnummer die SIP Domäne
     */
     void saveSipDomainOnVoIPDNs(List<AuftragVoipDNView> auftragVoipDNViews, Long auftragId)
            throws StoreException;

    /**
     * Speichert die Zuordnung von Rufnummern oder Rufnummernblöcke zu den festgelegten Endgeräteports.
     *
     * @param auftragVoipDNViews
     * @throws StoreException
     */
     void saveAuftragVoIPDNs(Collection<AuftragVoipDNView> auftragVoipDNViews) throws StoreException;

    /**
     * überprüft ob jedem Port genau eine Hauptrufnr. zugeordnet wurde
     *
     * @param auftragVoipDNViews
     * @return <code>true<code> wenn jedem Port eine Hauprufnr. zugeordnet wurde, ansonsten <code>false</code>
     */
     boolean validateHauprufnrToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews);

    /**
     * überprüft ob jedem Port max. 10 Rufnummern zugeordnet wurden
     *
     * @param auftragVoipDNViews
     * @return <code>true<code> wenn die Anzahl an Rufnummern je Port <= 10 ist, ansonsten <code>false</code>
     */
     boolean validateRufnrCountToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews);

    /**
     * überprüft, ob jede Rufnummer/jeder Block einem Port zugewiesen wurde
     *
     * @param auftragVoipDNViews
     * @return <code>true<code> wenn jeder Rufnummer ein Port zugewiesen wurde, ansonsten <code>false</code>
     */
    boolean validatePortToDNAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews);

    /**
     * überprüft, ob Rufnummern oder Bloecke konfiguriert werden sollen
     *
     * @param auftragVoipDNViews
     * @return <code>true<code> wenn Bloecke und <code>false<code> wenn Rufnummern konfiguriert sind
     */
     boolean checkDNBlocks(Collection<AuftragVoipDNView> auftragVoipDNViews);

    /**
     * überprüft, ob die komplette Portzuordnung fehlerfrei ist
     *
     * @param auftragVoipDNViews
     * @return ein leeres AKWarnings-Objekt (isEmpty() == true) wenn keine Fehler gefunden wurden, ansonsten enthält es
     * die Warnungen
     */
     AKWarnings validatePortAssignment(Collection<AuftragVoipDNView> auftragVoipDNViews);

    /**
     * Ordnet automatisch Rufnummern oder Rufnummernblöcke dem oder den Endgeräteports zu. Jeder Port benötigt eine
     * Hauptrufnummer - nicht mehr und nicht weniger. Maximal bekommt ein Port 10 Rufnummern zugeordnet. Im Falle von
     * Rufnummernblöcken hat weiterhin jeder Port genau einen Hauptrufnummernblock. Jeder Block wird jedem Port
     * zugeordnet.
     */
     void assignVoIPDNs2EGPorts(Collection<AuftragVoipDNView> auftragVoipDNViews, Long auftragId)
            throws StoreException;

    /**
     * prueft, ob die sich die Portzuordnungen zu den Rufnummern zeitlich ueberschneiden und somit nicht eindeutig sind
     *
     * @param auftragVoipDNViews
     * @return
     */
    boolean validatePortzuordnungEindeutig(final Collection<AuftragVoipDNView> auftragVoipDNViews);

    List<AuftragVoIPDN2EGPort> findAuftragVoIPDN2EGPorts(Long auftragVoipDnId);

    List<AuftragVoIPDN2EGPort> findAuftragVoIPDN2EGPortsValidAt(Long auftragVoipDnId, Date validAt);

    void updateRufnummernplaene(AuftragVoipDNView auftragVoipDNView, AuftragVoIPDN auftragVoIPDN,
            boolean sipDomainChanged) throws StoreException;

    /**
     * Erzeugt ein zufaelliges Passwort mit der fuer SIP PWs vorgesehenen Laenge (aktuell 6).
     */
    String generateSipPassword();

    /**
     * Ueberschreibt die SIP-Domaenen der VoIP Rufnummern des angegebenen Auftrags, falls ein Endgeraet SIP-Domaenen
     * konfiguriert hat.
     */
    Either<String, String> migrateSipDomainOfVoipDNs(Long auftragId, boolean switchModified,
            CalculatedSipDomain4VoipAuftrag initialSipDomainState) throws ServiceNotFoundException, FindException;
}
