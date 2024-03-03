package de.mnet.wbci.service;

import java.util.*;

import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.OrderMatchVO;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Interface fuer Services, die einen ausgehenden WBCI-Vorgang (Vorabstimmung) erstellen koennen.
 *
 * @param <GF>
 */
public interface WbciVaService<GF extends WbciGeschaeftsfall> extends WbciService, WbciConstants {

    /**
     * Erstellt und validiert eine neue ausgehende {@link VorabstimmungsAnfrage} für den angegebenen Geschaeftsfall.
     * Tritt ein Fehler bei der Validierung auf, wird eine {@link de.mnet.wbci.exception.WbciValidationException}
     * geworfen. Für alle anderen Fehler wird eine {@link de.mnet.wbci.exception.WbciServiceException} geworfen.
     *
     * @param wbciGeschaeftsfall
     * @return
     */
    VorabstimmungsAnfrage<GF> createWbciVorgang(GF wbciGeschaeftsfall);

    /**
     * Verarbeitet eine eingehende {@link VorabstimmungsAnfrage}.
     *
     * @param metadata              die Ergebnisse der Verarbeitung
     * @param vorabstimmungsAnfrage die Vorabstimmungsanfrage
     */
    void processIncomingVA(@NotNull MessageProcessingMetadata metadata, @NotNull VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage);

    /**
     * Versucht zu der angegebenen {@link VorabstimmungsAnfrage} den zugehoerigen Taifun Auftrag zu ermitteln und diesen
     * auch zuzuordnen. <br/>
     * Eine Zuordnung des Taifun-Auftrags erfolgt nur dann, wenn ein Auftrag eindeutig ermittelt
     * werden konnte. Wenn allerdings zu diesem Taifun-Auftrag bereits ein nicht abgeschlossener WbciGeschaeftsfall
     * existiert, wird keine Zuordnung erfolgen, weil zu einem Zeitpunkt immer nur ein aktiver WbciGeschäftsfall pro
     * Taifun-Auftrag existieren darf. <br/>
     * <br/>
     * Falls der ermittelte TAI-Auftrag bereits einen aktiven Geschaeftsfall besitzt und dieser im Status NEW_VA
     * (wg. STR-AEN) ist, dann wird zusaetzlich ueberprueft, ob der neue WBCI Vorgang der neue Vorgang zur STR-AEN
     * ist. Sofern der Geschaeftsfalltyp dann auch noch zulaessig ist, erfolgt eine automatische Auftragszuordnung
     * und der 'alte' (bisher noch aktive) Vorgang wird geschlossen. Falls der Geschaeftsfalltyp nicht zulaessig ist
     * (Wechsel von RRNP auf MRN/ORN), so wird der neue Vorgang direkt mit einer ABBM abgewiesen.
     * <br/>
     * Falls die ermittelten Taifun-Auftraege nicht eindeutig sind (also mehr als ein Treffer) wird keine
     * Auftragszuordnung durchgefuehrt. Falls eine Zuordnung durchgeführt wurde, kann man ueber
     * {@link de.mnet.wbci.model.WbciGeschaeftsfall#billingOrderNoOrig} auf die zugeordnete TAI AuftragsId zugreifen.
     * <br/>
     * @param vorabstimmungsAnfrage
     * @return {@code true} falls eine Zuordnung stattgefunden hat und {@code false} falls kein Auftrag zugeordnet
     * wurde.
     */
    boolean autoAssignTaifunOrderToVA(@NotNull VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage) throws FindException;

    /**
     * Ermittelt eine Liste mit Taifun-Auftragsdaten und Matching-Information, die als potentiellen Kandidaten für die
     * Zuordnung zu der jeweiligen Vorabstimmung gelten. Es werden lediglich Aufwände mit billing-relevanten Rufnummern
     * herangezogen. Wenn die Liste nur einen Eintrag enthält kann eine eindeutige Zuordnung stattfinden.
     *
     * @param vorabstimmungsId die ID der Vorabstimmung - {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @param filterViolations Flag gibt an, ob nur komplett matchende ({@code true}) Auftraege ermittelt werden sollen
     *                         oder ob auch Auftraege geliefert werden sollen, bei denen bestimmte Parameter nicht
     *                         matchen (also Violations besitzen)
     * @return Liste mit einen {@link Pair} aus {@link BAuftragVO} und {@link de.mnet.wbci.model.OrderMatchVO}s , die
     * die potentiellen Kandidaten für die Zuordnung zu der Vorabstimmungs-Anfrage enthalten.
     */
    Collection<Pair<BAuftragVO, OrderMatchVO>> getTaifunOrderAssignmentCandidates(@NotNull String vorabstimmungsId,
            boolean filterViolations);

    /**
     * Ermittelt die Rufnummern aus der angegebenen {@link VorabstimmungsAnfrage} und sucht mit diesen Rufnummern nach
     * passenden Taifun Auftraegen.
     */
    Set<Long> getOrderNoOrigsByDNs(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall);
}
