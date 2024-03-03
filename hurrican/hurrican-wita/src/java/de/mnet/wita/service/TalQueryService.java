/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 13:05:53
 */
package de.mnet.wita.service;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Service zur Unterstuetzung der GUI fuer TAL-relevante Abfragen.
 */
public interface TalQueryService extends WitaService {

    /**
     * Ermittelt zugehoerige Hurrican-Auftraege einer Terminverschiebung, fuer die auch eine Terminverschiebung
     * durchgefuehrt werden kann.
     */
    List<TalSubOrder> findPossibleSubOrdersForTerminverschiebung(List<WitaCBVorgang> cbVorgaenge,
            AuftragDaten auftragDaten) throws FindException;

    /**
     * Ermittelt zugehoerige Hurrican-Auftraege eines Stornos, fuer die auch ein Storno durchgefuehrt werden kann.
     */
    List<TalSubOrder> findPossibleSubOrdersForStorno(List<WitaCBVorgang> cbVorgaenge, AuftragDaten auftragDaten)
            throws FindException;

    /**
     * Ermittelt zugehoerige Hurrican-Auftraege einer Erlmk, fuer die auch eine ERLM durchgefuehrt werden kann.
     */
    List<TalSubOrder> findPossibleSubOrdersForErlmk(CBVorgang cbVorgang, AuftragDaten auftragDaten)
            throws FindException;

    /**
     * Ermittelt ob zu einer Carrierbestellung ESAA oder interne el. Vorgaenge vorhanden sind.
     *
     * @return true falls mindestens ein ESAA oder interner Vorgang vorliegt, sonst false
     */
    boolean hasEsaaOrInterneCBVorgaenge(Long cbId) throws FindException;

    /**
     * Ermittelt die Endstelle für den übergebenen Cb-Vorgang. Beachtet dabei, ob es sich um REX-MK oder nicht handelt.
     */
    Endstelle findEndstelleForCBVorgang(CBVorgang cbVorgang) throws FindException;
}
