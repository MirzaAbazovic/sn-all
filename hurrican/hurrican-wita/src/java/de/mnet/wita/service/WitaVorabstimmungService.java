/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 17:28:23
 */
package de.mnet.wita.service;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungAbgebend;

/**
 * Service zum Verwalten von Vorabstimmungen.
 */
public interface WitaVorabstimmungService extends WitaService {

    /**
     * Speichert eine Vorabstimmung.
     */
    Vorabstimmung saveVorabstimmung(Vorabstimmung vorabstimmung);

    /**
     * Speichert eine abgebende Vorabstimmung.
     */
    VorabstimmungAbgebend saveVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend);

    /**
     * Gibt eindeutige Vorabstimmung oder null fuer Endstelle und AuftragDaten zurueck.
     */
    Vorabstimmung findVorabstimmung(Endstelle endstelle, AuftragDaten auftragDaten);

    /**
     * Gibt eindeutige Vorabstimmung oder null fuer Endstelle und AuftragId zurueck.
     */
    Vorabstimmung findVorabstimmung(Endstelle endstelle, Long auftragId);

    /**
     * Gibt eindeutige abgebende Vorabstimmung oder null fuer Endstelle und AuftragDaten zurueck.
     *
     * @param endstelle {@link Endstelle#endstelleTyp}
     * @param auftragId {@link AuftragDaten#auftragId}
     */
    VorabstimmungAbgebend findVorabstimmungAbgebend(String endstelle, Long auftragId);

    /**
     * Ermittelt alle {@link Vorabstimmung}s-Objekte, die zur Auftrags-Klammer {@code auftragsKlammer} gehoeren. <br>
     * <br> Die Ermittlung der Vorabstimmung ueber die Auftrags-Klammer ist dann notwendig bzw. sinnvoll, wenn ein
     * Anbieterwechsel mit Klammerung ausgeloest wird. <br> In diesem Fall wird in der Vorabstimmung i.d.R. nur die
     * ONKZ/Rufnummer angegeben und ist somit fuer alle Hurrican-Auftraege der Auftrags-Klammer identisch. <br> Bei
     * Vorabstimmungen mit Angabe der LBZ / VtrNr und gleichzeitiger Klammerung sollte die Vorabstimmung pro Hurrican
     * Auftrag hinterlegt und nicht ueber die Auftrags-Klammer ermittelt werden.
     *
     * @param auftragsKlammer Auftrags-Klammer, zu der die {@link Vorabstimmung}s-Objekte gesucht werden.
     * @param endstelle       Endstelle, ueber die der Typ der zu beruecksichtigenden Endstelle ermittelt wird.
     * @return Liste mit den ermittelten {@link Vorabstimmung}s-Objekten.
     */
    List<Vorabstimmung> findVorabstimmungForAuftragsKlammer(Long auftragsKlammer, Endstelle endstelle);

    /**
     * Gibt eindeutige Rex-Mk Vorabstimmung oder null fuer AuftragId zurueck.
     *
     * @throws WitaBaseException falls mehr als eine Vorabstimmung gefunden wurde
     */
    Vorabstimmung findVorabstimmungForRexMk(Long auftragId);

    /**
     * Loescht die uebergebene {@link Vorabstimmung}
     */
    void deleteVorabstimmung(Vorabstimmung vorabstimmung);

    /**
     * Loescht die uebergebene {@link VorabstimmungAbgebend}
     */
    void deleteVorabstimmungAbgebend(VorabstimmungAbgebend vorabstimmungAbgebend);
}
