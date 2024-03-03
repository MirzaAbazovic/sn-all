/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 11:17:57
 */
package de.mnet.wita.service;

import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

/**
 * Service-Definition fuer {@code MWFEntity} Objekte.
 */
public interface MwfEntityService extends WitaService {

    /**
     * Speichert die MwfEntity
     */
    <T extends MwfEntity> T store(T toStore);

    /**
     * Ermittelt {@code MWFEntity} Objekte ueber das angegebene Beispiel-Objekt.
     */
    <T extends MwfEntity> List<T> findMwfEntitiesByExample(T example);

    /**
     * Ermittelt {@link MwfEntity} Objekte ueber das angegebene Property.
     */
    <T extends MwfEntity> List<T> findMwfEntitiesByProperty(Class<T> type, String property, Object value);

    /**
     * Sucht nach dem Mwf-Auftrag eines CbVorgangs.
     *
     * @param cbVorgangId Id des CbVorgang
     * @throws AuftragNotFoundException wenn der Auftrag nicht gefunden wird oder mehrere Aufträege für den cbVorgang
     *                                  vorhanden sind
     */
    Auftrag getAuftragOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht nach den {@link Storno} zu einem CBVorgang, falls vorhanden
     *
     * @param cbVorgangId Id des CbVorgang
     * @return die {@link Storno}s, falls vorhanden
     * @throws AuftragNotFoundException wenn kein {@link Storno} gefunden wird
     */
    List<Storno> getStornosOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht nach den Terminverschiebungen zu einem CBVorgang, falls vorhanden
     *
     * @param cbVorgangId Id des CbVorgang
     * @return die {@link TerminVerschiebung}en, falls keine existieren
     * @throws AuftragNotFoundException wenn der TV-Auftrag nicht gefunden wird oder mehrere TV-Aufträge für den
     *                                  cbVorgang vorhanden sind
     */
    List<TerminVerschiebung> getTerminVerschiebungenOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht die letzte AkmPv-Meldung nach der gegebenen {@code vertragsNummer}.
     */
    AnkuendigungsMeldungPv getLastAkmPv(String vertragsNummer);

    /**
     * Sucht die letzte AbmPv-Meldung nach der gegebenen {@code vertragsNummer}.
     */
    AuftragsBestaetigungsMeldungPv getLastAbmPv(String vertragsNummer);

    /**
     * Sucht die letzte VzmPv-Meldung nach der gegebenen {@code vertragsNummer}.
     */
    VerzoegerungsMeldungPv getLastVzmPv(String vertragsNummer);

    /**
     * Sucht die letzte AbbmPv-Meldung nach der gegebenen {@code vertragsNummer}.
     */
    AbbruchMeldungPv getLastAbbmPv(String vertragsNummer);

    /**
     * Checked ob die letzte Meldung die an den Workflow geschickt wurde eine TAM ist.
     *
     * @param currentTam die aktuelle TAM, die verarbeitet wird (für enthaltene externeAuftragsnummer werden zugehörigen
     *                   Meldungen gesucht)
     */
    boolean isLastMeldungTam(Meldung<?> currentTam);

    /**
     * Sucht nach der letzten ABM zu der angegebenen {@code externeAuftragsnummer}.
     */
    AuftragsBestaetigungsMeldung getLastAbm(String externeAuftragsnummer);

    /**
     * Ermittelt alle {@link MnetWitaRequest}, die noch nicht an den JMS gesendet wurden.
     *
     * @param maxResultsPerGeschaeftsfall gibt Anzahl der Requests pro Geschaeftsfall an, die ermittelt werden sollen
     * @return Liste mit {@link MnetWitaRequest#id}s, die erneut an die WITA-Schnittstelle gesendet werden koennen.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<Long> findUnsentRequestsForEveryGeschaeftsfall(Integer maxResultsPerGeschaeftsfall)
            throws FindException;

    /**
     * Ermittelt den noch nicht gesendeten {@link MnetWitaRequest} zu einem CB-Vorgang.
     *
     * @param witaCbVorgangId Id des WITA-CB-Vorgangs
     * @return das {@link MnetWitaRequest} Objekt zu dem CbVorgang oder null
     */
    MnetWitaRequest findUnsentRequest(Long witaCbVorgangId);

    /**
     * Ermittelt alle Anlagen, die noch nicht im Scan-View archiviert wurden
     *
     * @return Liste {@link Anlage}en, die noch nicht archiviert wurden
     */
    List<Anlage> findUnArchivedAnlagen();

    /**
     * Sucht alle Meldungen die noch nicht Richtung BSI protokolliert wurden: noch nicht geschickte, noch nicht
     * angefasste, die noch geschickt werden müssen.
     *
     * @return Liste von {@link Meldung} die noch nicht Richtung BSI protokolliert wurden.
     */
    List<Meldung<?>> findMeldungenToBeSentToBsi();

    /**
     * Sucht alle Requests die noch nicht Richtung BSI protokolliert wurden: noch nicht geschickte, noch nicht
     * angefasste, die noch geschickt werden müssen.
     *
     * @return Liste von {@link MnetWitaRequest} die noch nicht Richtung BSI protokolliert wurden.
     */
    List<MnetWitaRequest> findRequestsToBeSentToBsi();

    /**
     * Sucht alle vorgehaltenen(!) Requests, bei denen der Delay noch in BSI protokolliert werden muss.
     *
     * @param maxCountOfRequests Anzahl der maximal zu ermittelnden {@link MnetWitaRequest} Objekte. (Zur Info: die
     *                           Result-Groesse ist i.d.R. kleiner als maxCountOfRequests; auch dann, wenn noch mehr
     *                           Requests delayed sind. Dies kommt daher, da sich die Limitierung auf das DB-Query
     *                           auswirkt, dieses jedoch noch weiter gefiltert wird.)
     * @return Liste von {@link MnetWitaRequest} Objekten, fuer die noch ein Delay Eintrag in BSI protkolliert werden
     * muss.
     */
    List<MnetWitaRequest> findDelayedRequestsToBeSentToBsi(int maxCountOfRequests);

    /**
     * Gibt die Vertragsnummer für alle Meldungen zu dem gegebenen Business-Key zurück, ignoriert Meldungen, die kein
     * Vertragsnummer (= {@code null}) haben
     *
     * @throws NoSuchElementException   falls keine Vertragsnummer gefunden
     * @throws IllegalArgumentException falls mehrere verschiedene Vertragsnummern gefunden
     */
    String findVertragsnummerFor(String businessKey);

    /**
     * @return {@code true} iff bereits eine {@link Meldung} vom angegebenen Typ (= {@code clazz}) empfangen wurde.
     */
    <T extends Meldung<?>> boolean checkMeldungReceived(String extAuftragsnr, Class<T> clazz);

    /**
     * Sucht alle Meldungen für die der Versand einer SMS in Frage kommt. Dies sind alle Meldungen mit dem SMS_STATUS
     * "OFFEN".
     *
     * @return Liste von {@link Meldung}, die den SMS_STATUS "OFFEN" haben.
     */
    List<Meldung<?>> findMeldungenForSmsVersand();

    List<Meldung<?>> findMeldungenForEmailVersand();
}
