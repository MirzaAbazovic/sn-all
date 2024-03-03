/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2011 08:39:50
 */
package de.mnet.wita.dao;

import java.time.*;
import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

/**
 * DAO-Interface fuer MWF-Entitäten.
 */
public interface MwfEntityDao extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Sucht nach dem MWF-{@link Auftrag} eines CbVorgangs.
     *
     * @param cbVorgangId Id des CbVorgang
     * @throws AuftragNotFoundException wenn kein {@link Auftrag} gefunden wird
     * @throws IllegalArgumentException wenn mehrere {@link Auftrag}e für den cbVorgang vorhanden sind
     */
    Auftrag getAuftragOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht nach den {@link Storno} zu einem CBVorgang
     *
     * @param cbVorgangId Id des CbVorgang
     * @return die {@link Storno}s
     * @throws AuftragNotFoundException wenn kein {@link Storno} nicht gefunden wird
     */
    List<Storno> getStornosOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht nach den {@link TerminVerschiebung}en zu einem CBVorgang
     *
     * @param cbVorgangId Id des CbVorgang
     * @return die {@link TerminVerschiebung}en
     * @throws AuftragNotFoundException wenn keine {@link TerminVerschiebung} gefunden wird
     */
    List<TerminVerschiebung> getTerminverschiebungenOfCbVorgang(Long cbVorgangId);

    /**
     * Sucht die letzte AkmPv-Meldung unter der gegebenen {@code vertragsNummer}.
     */
    AnkuendigungsMeldungPv getLastAkmPv(String vertragsNummer);

    /**
     * Sucht die letzte AbmPv-Meldung unter der gegebenen {@code vertragsNummer}.
     */
    AuftragsBestaetigungsMeldungPv getLastAbmPv(String vertragsNummer);

    /**
     * Sucht die letzte Vzm-Pv Meldung unter der gegebenen Vertragsnummer
     */
    VerzoegerungsMeldungPv getLastVzmPv(String vertragsNummer);

    /**
     * Sucht die letzte AbbmPv-Meldung unter der gegebenen {@code vertragsNummer}.
     */
    AbbruchMeldungPv getLastAbbmPv(String vertragsNummer);

    /**
     * Sucht nach der letzten {@link TerminAnforderungsMeldung} zu der angegebenen {@code externeAuftragsnummer}
     *
     * @param externeAuftragsnummer zu der die letzte TAM gesucht werden soll
     * @return letzte {@link TerminAnforderungsMeldung}
     */
    TerminAnforderungsMeldung getLastTam(String externeAuftragsnummer);

    /**
     * Sucht die letzte ABM-Meldung anhand der externeAuftragsnummer.
     */
    AuftragsBestaetigungsMeldung getLastAbm(String externeAuftragsnummer);

    /**
     * Ermittelt alle {@link MnetWitaRequest}, die noch nicht an den JMS gesendet wurden.
     *
     * @param {@link     GeschaeftsfallTyp} - Geschaeftsfalltyp der Request
     * @param maxResults - maximum number of results
     * @return Liste mit {@link MnetWitaRequest} Objekten, die erneut an den JMS gesendet werden koennen.
     */
    List<Long> findUnsentRequests(GeschaeftsfallTyp geschaeftsfallTyp, int maxResults);

    List<Meldung<?>> findAllMeldungen(String externeAuftragsnummer);

    /**
     * Ermittelt den letzten Meldungstyp (first) und den PV-Zustimmungsstatus (second).
     */
    Pair<String, String> findLastMeldungsTyp(String externeAuftragsnummer, String vertragsnummer);

    List<MnetWitaRequest> findAllRequests(String externeAuftragsnummer);

    /**
     * Ermittelt alle Request von einem bestimmten Typ, mit der angegebenen externen Auftragsnummer
     *
     * @return Liste von Requests vom angegebenen Typ
     */
    <T extends MnetWitaRequest> List<T> findAllRequests(String externeAuftragsnummer, Class<T> requestClass);

    /**
     * Sucht alle Meldungen die noch nicht Richtung BSI protokolliert wurden: noch nicht geschickte, noch nicht
     * angefasste, die noch geschickt werden müssen.
     *
     * @return Liste von {@link Meldung} die noch nicht Richtung BSI protokolliert wurden.
     */
    List<Meldung<?>> findMeldungenToBeSentToBsi();

    /**
     * Sucht alle Requests die noch nicht Richtung BSI protokolliert wurden: noch nicht geschickte, noch nicht
     * angefasste, die noch geschickt werden müssen. <br><br> <b>zu beachten</b>: die Methode liefert immer nur max.
     * 1000 Requests zurueck!
     *
     * @return Liste von {@link MnetWitaRequest} die noch nicht Richtung BSI protokolliert wurden.
     */
    List<MnetWitaRequest> findRequestsToBeSentToBsi();

    /**
     * Ermittelt alle WITA Requests, die noch nicht an die WITA Schnittstelle uebertragen wurden und bei denen auch noch
     * kein DELAY_SENT_TO_BSI protokolliert wurde. <br> Zusaetzlich wird noch ueber das MWF_CREATION_DATE gefiltert und
     * nur die Requests ermittelt, deren {@link MnetWitaRequest#getMwfCreationDate()} < {@code maxCreationDate} ist.
     * (Dient dazu, Requests heraus zu filtern, die noch in der "minutes request on hold" Zeitspanne liegen.) <br><br>
     * <b>zu beachten</b>: die Methode liefert immer nur max. {@code maxCountofRequests} Requests zurueck!
     *
     * @param maxCreationDate
     * @param maxCountOfRequests
     * @return
     */
    List<MnetWitaRequest> findDelayedRequestsToBeSentToBsi(LocalDateTime maxCreationDate, int maxCountOfRequests);

    /**
     * Sucht alle PV-Meldungen die schon versucht wurden nach BSI protokolliert zu werden.
     *
     * @return Liste von {@link Meldung} die noch nicht Richtung BSI protokolliert wurden.
     */
    List<Meldung<?>> findPvMeldungenNotToBeSendToBsi();

    /**
     * Findet alle AuftragRequests zu einer AuftragId
     */
    List<Auftrag> getAuftragRequestsForAuftragId(Long auftragId);

    /**
     * Ermittelt den noch nicht gesendeten {@link MnetWitaRequest} zu einem {@link CBVorgang}, die noch nicht geschickt
     * wurden und auch nicht storniert wurden.
     *
     * @param witaCbVorgangId Id des WITA-CB-Vorgangs
     * @return der {@link MnetWitaRequest} zu dem {@link CBVorgang} oder {@code null} falls alle {@link MnetWitaRequest}
     * bereits gesendet wurden.
     */
    MnetWitaRequest findUnsentRequest(Long witaCbVorgangId);

    /**
     * Ermittelt alle Anlagen, die noch nicht im Scan-View archiviert wurden
     *
     * @return Liste {@link Anlage}en, die noch nicht archiviert wurden
     */
    List<Anlage> findUnArchivedAnlagen();

    MnetWitaRequest findRequest4Anlage(Anlage anlage);

    Meldung<?> findMeldung4Anlage(Anlage anlage);

    /**
     * Sucht alle Meldungen für die der Versand einer SMS in Frage kommt. Dies sind alle Meldungen mit dem SMS_STATUS
     * "OFFEN".
     *
     * @return Liste von {@link Meldung}, die den SMS_STATUS "OFFEN" haben.
     */
    List<Meldung<?>> findMeldungenForSmsVersand();

    /**
     * Sucht alle Meldungen für Email-Versand.
     *
     * @return Meldungen.
     */
    List<Meldung<?>> findMessagesForEmailing();
}
