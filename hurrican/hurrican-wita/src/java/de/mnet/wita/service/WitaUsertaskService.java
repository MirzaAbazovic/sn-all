/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 17:28:23
 */
package de.mnet.wita.service;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.UserTaskDetails;
import de.mnet.wita.model.VorabstimmungAbgebend;

/**
 * Service zum Verwalten von Usertasks
 */
public interface WitaUsertaskService extends WitaService {

    /**
     * Laedt alle offenen TAM-Usertasks.
     */
    List<TamVorgang> findOpenTamTasks();

    /**
     * Laedt alle offenen TKG-TAM-Usertasks
     */
    List<TamVorgang> findOpenTKGTamTasksWithWiedervorlage();

    /**
     * Laedt alle offenen TAM-Usertasks die kein Wiedervorlage-Datum haben oder deren Wiedervorlage-Datum in der
     * Vergangenheit liegt.
     */
    List<TamVorgang> findOpenTamTasksWithWiedervorlageWithoutTKGTams();

    /**
     * Laedt die Details eines TAM-Usertasks.
     */
    UserTaskDetails loadUserTaskDetails(TamVorgang tamVorgang) throws FindException;

    /**
     * Der uebergebene Benutzer claimt den UserTask.
     *
     * @param user Hurrican-Login-Name falls ein Benutzer den Task uebernimmt oder {@code null} falls der Task
     *             freigegeben werden soll
     */
    <T extends UserTask> T claimUserTask(T userTask, AKUser user) throws StoreException;

    /**
     * Schliesst einen AbgebendeLeitungenUserTask (ohne Daten auf eine Carrierbestellung zu schreiben). Prueft auch, ob
     * der Task nicht von einem anderen User geclaimt ist.
     */
    <T extends UserTask> T closeUserTask(T userTask, AKUser user) throws StoreException;

    /**
     * Schliesst einen UserTask (ohne Daten auf eine Carrierbestellung zu schreiben). <b>Vorsicht:</b> Prueft
     * <b>NICHT</b>, ob der Task nicht von einem anderen User geclaimt ist.
     */
    <T extends UserTask> T closeUserTask(T userTask) throws StoreException;

    /**
     * Setzt die letzen Aenderungen am {@link UserTask} wieder zurueck, z.B. weil vorgehaltener {@link MnetWitaRequest}
     * wieder storniert wurde.
     */
    <T extends UserTask> T resetUserTask(T userTask);

    /**
     * Prueft, ob ein User-Task von einem anderen User geclaimt ist.
     *
     * @param userTask darf null sein
     * @throws WitaBpmException wenn der userTask von einem anderen Benutzer geclaimt ist.
     */
    void checkUserTaskNotClaimedByOtherUser(UserTask userTask, AKUser user);

    /**
     * Laedt alle offenen Usertasks fuer abgebende Leitungen und berücksichtigt dabei das Wiedervorlage-Datum.
     *
     * @return gibt eine Liste von {@code AbgebendeLeitungenVorgang} zurueck.
     * @throws FindException wenn bei der Ermittlung der Tasks ein Fehler auftritt.
     */
    List<AbgebendeLeitungenVorgang> findOpenAbgebendeLeitungenTasksWithWiedervorlage() throws FindException;

    /**
     * Speichert einen UserTask.
     */
    <T extends UserTask> T storeUserTask(T userTask);

    /**
     * Laedt den UserTask fuer eine AKM-PV an Hand der angegebenen externen Auftragsnummer oder {@code null}.
     *
     * @throws IllegalArgumentException falls für die gegebene externe Auftragsnummer mehrere Usertask existieren
     */
    AkmPvUserTask findAkmPvUserTask(String externeAuftragsnummer);

    /**
     * Laedt den UserTask fuer eine AKM-PV an Hand der angegebenen Vertragsnummer oder {@code null}.
     *
     * @throws IllegalArgumentException falls für die gegebene Vertragsnummer mehrere Usertask existieren
     */
    AkmPvUserTask findAkmPvUserTaskByContractId(String vertragsnummer);

    /**
     * Laedt den UserTask fuer eine KUE-DT an Hand der angegebenen externen Auftragsnummer.
     */
    KueDtUserTask findKueDtUserTask(String externeAuftragsnummer);

    /**
     * Erzeugt und speichert einen Kuendigung DTAG UserTask.
     *
     * @throws WitaBpmException falls keine Carrierbestellungen zu der Vertragnummer gefunden werden
     */
    KueDtUserTask createKueDtUserTask(ErledigtMeldung erlm);

    /**
     * Erzeugt und speichert einen AkmPvUserTask.
     *
     * @throws WitaBpmException falls keine Carrierbestellungen zu der Vertragnummer gefunden werden
     */
    <T extends Meldung<?> & IncomingPvMeldung> AkmPvUserTask createAkmPvUserTask(T meldung);

    /**
     * Ermittelt einen bestehenden {@link AkmPvUserTask} und aktualisiert diesen mit den Daten aus der angegebenen
     * {@link AnkuendigungsMeldungPv}. <br> Sollte zu der externen Auftragsnummer kein UserTask bestehen, so wird ein
     * neuer UserTask ueber {@link WitaUsertaskService#createAkmPvUserTask(AnkuendigungsMeldungPv)} angelegt.
     *
     * @throws WitaBpmException wenn beim Update-Vorgang Fehler auftreten.
     */
    AkmPvUserTask updateAkmPvUserTask(AnkuendigungsMeldungPv akmPv);

    /**
     * Prüft, ob eine und welche automatische Antwort der uebergebenen {@link AnkuendigungsMeldungPv} durch eine {@link
     * RueckMeldungPv} berechtigt ist. <p> Ermittelt anhand der {@link AnkuendigungsMeldungPv#getVertragsNummer()} die
     * zugehörige {@link Carrierbestellung} die unter Umstaenden eine {@link VorabstimmungAbgebend} enthaelt. Sollte
     * dies der Fall sein, wird geprueft ob diese eine positive oder negative Rueckmeldung vorliegt und ob das
     * Uebernamedatum sowie der aufnehmdede Provider zusammenpassen. Falls ja, wird der {@link RuemPvAntwortCode} fuer
     * eine automatische Antwort zurueckgegeben, ansonsten {@code null}. </p> Falls der AKM-PV eine WBCI
     * Vorabstimmungs-Id zugeordnet ist erfolgt die Ueberpruefung und die entsprechenden Antwort-Codes ueber die Daten
     * aus der WBCI Vorabstimmung.
     *
     * @return den {@link RuemPvAntwortCode} mit dem geantwortet werden soll oder {@code null} falls die {@link
     * AnkuendigungsMeldungPv} nicht automatisch beantwortet werden darf
     */
    Map<WitaTaskVariables, Object> getAutomaticAnswerForAkmPv(AnkuendigungsMeldungPv akmPv);

    /**
     * Laedt die Details eines Kue-Dt-Usertasks.
     */
    UserTaskDetails loadUserTaskDetails(AbgebendeLeitungenVorgang kueDtVorgang) throws FindException;

    /**
     * Schliesst einen AbgebendeLeitungenUserTask ab und schreibt die entsprechenden Daten auf die Carrierbestellung.
     */
    AbgebendeLeitungenUserTask completeUserTask(AbgebendeLeitungenUserTask userTask, AKUser user)
            throws StoreException, FindException;

    /**
     * Sucht nach AuftragDaten zu einder Kuendigung-DTAG.
     */
    List<AuftragDaten> findAuftragDatenForUserTask(AbgebendeLeitungenUserTask task) throws FindException;

    /**
     * Ermittelt die Restfrist fuer die Beantwortung einer TAM.
     */
    int getRestFristInTagen(TamVorgang tamVorgang);

    /**
     * Erzeugt eine eindeutige Zuordnung zwischen übergebenen {@code userTask} und {@code auftragIdAndCbId}
     */
    void createUserTask2AuftragDatenFor(AbgebendeLeitungenUserTask userTask, Pair<Long, Long> auftragIdAndCbId)
            throws FindException, StoreException;
}
