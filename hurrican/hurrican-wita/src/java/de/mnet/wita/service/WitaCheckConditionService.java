/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 08:40:03
 */
package de.mnet.wita.service;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Prüft verschiedenste Rahmenbedingungen für WITA, z.B. ob ein {@link Storno} gesendet werden darf.
 */
public interface WitaCheckConditionService extends WitaService, WitaConstants {

    static final String STORNO_TOO_LATE = "Die Stornierung wurde zu spät eingestellt, sodass die Realisierung nicht mehr verschoben werden kann!";
    static final String STORNO_WITH_KLAMMER_ONLY_AFTER_ABM = "Eine Stornierung ist nur möglich, wenn für den geklammerten Ursprungsauftrag bereits eine Auftragsbestätigung eingegangen ist!";
    static final String STORNO_ONLY_BEFORE_ERLM = "Eine Stornierung ist nur möglich, wenn für den Auftrag noch keine Erledigtmeldung eingegangen ist.";
    static final String STORNO_ONLY_BEFORE_ABBM = "Eine Stornierung ist nicht mehr nötig, da für den Auftrag bereits eine Abbruchmeldung eingegangen ist.";
    static final String STORNO_ONLY_AFTER_QEB = "Eine Stornierung ist nur möglich, wenn für den Ursprungsauftrag bereits eine qualifizierte Eingangsbestätigung eingegangen ist!";

    static final String TV_TERMIN_IN_VERGANGENHEIT = "KWT der Terminverschiebung darf nicht in der Vergangenheit liegen.";
    static final String TV_MINDESTVORLAUFZEIT = "Das Vorgabedatum für die Terminverschiebung ist nicht gültig! Es muss die Mindestvorlaufzeit von %s AT sowie das angegebene Zeitfenster eingehalten werden!";
    static final String TV_ONLY_AFTER_QEB = "Eine Terminverschiebung für %s ist nur möglich, wenn für den Ursprungsauftrag bereits eine qualifizierte Eingangsbestätigung eingegangen ist.";
    static final String TV_ONLY_BEFORE_ERLM = "Eine Terminverschiebung für %s ist nur möglich, wenn für den Auftrag noch keine Erledigtmeldung eingegangen ist.";
    static final String TV_ONLY_BEFORE_ABBM = "Eine Terminverschiebung für %s ist nur möglich, wenn für den Auftrag noch keine Abbruchmeldung eingegangen ist.";
    static final String TV_WITH_KLAMMER_ONLY_AFTER_ABM = "Eine Terminverschiebung für %s ist nur möglich, wenn für den geklammerten Ursprungsauftrag bereits eine Auftragsbestätigung eingegangen ist.";
    static final String TV_ONLY_36_HOURS_BEFORE = "Die Terminverschiebung kann nicht mehr eingestellt werden, da die Realisierung nur bis zu max. 36h vorher verschoben werden kann!";

    static final String WBCI_MORE_THEN_ONE_VA_ID = "Die ausgelösten WITA-Vorgänge dürfen sich nicht auf mehere WBCI-Vorabstimmungen beziehen!";

    /**
     * Prüft, ob für die gegebenen {@link WitaCBVorgang} und {@link Auftrag} ein {@link Storno} gesendet werden darf.
     *
     * @throws WitaUserException falls der {@link Storno} nicht ausgelöst werden darf
     */
    void checkConditionsForStorno(WitaCBVorgang cbVorgang, Auftrag auftrag);

    /**
     * Prüft, ob für die gegebenen {@link WitaCBVorgang} und {@link Auftrag} eine {@link TerminVerschiebung} auf den
     * {@code neuerTermin} gesendet werden darf.
     *
     * @throws WitaUserException falls die {@link TerminVerschiebung} nicht ausgelöst werden darf
     */
    void checkConditionsForTv(WitaCBVorgang cbVorgang, Auftrag auftrag, LocalDate neuerTermin);

    /**
     * Prüft, ob die angegeben {@link CBVorgang}e mit Daten der gegebenfalls referenzierten WBCI-Vorabstimmung
     * übereinstimmen.
     *
     * @param createdCbVorgaenge alle erzeugten, zusammenhängendne Cbvorgänge
     */
    void checkConditionsForWbciPreagreement(List<? extends CBVorgang> createdCbVorgaenge);

    /**
     * Prüft ob die angegebene Auftragsbestätigungsmeldung mit den Daten des {@link WitaCBVorgang}s übereintimmt.
     *
     * @param cbVorgang aktuell vorhandener {@link WitaCBVorgang}.
     * @param abm       eingegangene {@link AuftragsBestaetigungsMeldung}.
     */
    void checkConditionsForAbm(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) throws StoreException;
}
