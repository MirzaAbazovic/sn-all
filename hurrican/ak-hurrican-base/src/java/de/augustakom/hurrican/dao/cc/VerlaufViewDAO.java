/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2005 16:32:37
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.VerlaufAmRlView;
import de.augustakom.hurrican.model.cc.view.VerlaufDispoRLView;
import de.augustakom.hurrican.model.cc.view.VerlaufEXTView;
import de.augustakom.hurrican.model.cc.view.VerlaufFieldServiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufStConnectView;
import de.augustakom.hurrican.model.cc.view.VerlaufStOnlineView;
import de.augustakom.hurrican.model.cc.view.VerlaufStVoiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;


/**
 * DAO-Interface fuer die Ermittlung von Verlauf-Views.
 *
 *
 */
public interface VerlaufViewDAO {

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung) fuer die angegebene Abteilung. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @param abteilungId Filtert nur die Verlaeufe, fuer die die Abteilung verantwortlich ist.
     * @return Liste mit Objekten des Typs {@link VerlaufUniversalView}
     */
    public List<VerlaufUniversalView> findBasWithUniversalQuery(Long abteilungId, Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung) fuer die Abteilung Dispo bzw. Netzplanung. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @param abteilungId Filtert nur die Verlaeufe, fuer die die Abteilung verantwortlich ist.
     * @return Liste mit Objekten des Typs {@link VerlaufUniversalView}
     */
    public List<VerlaufUniversalView> find4DispoOrNP(Long abteilungId, Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen Ruecklaeufern (keine Projektierung!) fuer die Abteilung Dispo bzw. Netzplanung. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @param abteilungId Filtert nur die Verlaeufe, fuer die die Abteilung verantwortlich ist.
     * @return Liste mit Objekten des Typs {@link VerlaufDispoRLView}.
     */
    public List<VerlaufDispoRLView> findRL4DispoOrNP(Long abteilungId, Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung!) fuer die Abteilung ST Connect. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @return Liste mit Objekten des Typs {@link VerlaufStConnectView}.
     */
    public List<VerlaufStConnectView> find4STConnect(Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung!) fuer die Abteilung ST Voice. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @return Liste mit Objekten des Typs {@link VerlaufStVoiceView}.
     */
    public List<VerlaufStVoiceView> find4STVoice(Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung!) fuer die Abteilung ST Online. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @param abteilungId die Id der Abteilung für die Verläufe gesucht werden
     * @return Liste mit Objekten des Typs {@link VerlaufStOnlineView}.
     */
    public List<VerlaufStOnlineView> find4STOnline(Long abteilungId, Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlaeufen (keine Projektierung!) fuer die Abteilung Extern. Falls {@code
     * realisierungFrom} und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen
     * Realisierungsterminen durchsucht (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @return Liste mit Objekten des Typs {@link VerlaufEXTView}.
     */
    public List<VerlaufEXTView> find4EXTERN(Long abteilungId, Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen aktiven Verlauefen (keine Portierung!) fuer FieldService. Falls {@code realisierungFrom}
     * und/oder {@code realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen Realisierungsterminen durchsucht
     * (die Daten selber werden eingeschlossen, also <= und >=).
     *
     * @return Liste mit Objekten des Typs {@link VerlaufFieldServiceView}.
     */
    public List<VerlaufFieldServiceView> find4FieldService(Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach allen Ruecklaeufern (keine Projektierung!) fuer AM. Falls {@code realisierungFrom} und/oder {@code
     * realisierungTo} gesetzt ist, werden alle Verläufe nach gültigen Realisierungsterminen durchsucht (die Daten
     * selber werden eingeschlossen, also <= und >=).
     *
     * @return Liste mit Objekten des Typs {@link VerlaufAmRlView}.
     */
    public List<VerlaufAmRlView> findRL4Am(Date realisierungFrom, Date realisierungTo);

    /**
     * Sucht nach den Projektierungen fuer eine best. Abteilung. <br> Ueber das Flag <code>ruecklaeufer</code> kann bei
     * den Abteilungen DISPO und NP entschieden werden, ob nach zu verteilenden Projektierungen oder nach den
     * Ruecklaeufern gesucht werden soll (bei AM immer Ruecklaeufer).
     *
     * @param abtId        ID der Abteilung, fuer die die Projektierung ermittelt werden soll
     * @param ruecklaeufer Flag, ob bei DISPO nach Ruecklaeufern gesucht werden soll
     * @return Liste mit Objekten des Typs {@link ProjektierungsView}.
     */
    public List<ProjektierungsView> findProjektierungen(Long abtId, boolean ruecklaeufer);

    /**
     * Ermittelt alle Bauauftraege (abgeschlossen und aktiv, keine Stornos!) des angegebenen Kunden, die innerhalb des
     * Zeitraums 'min'-'max' liegen.
     *
     * @param kundeNo Kundennummer
     * @param minDate minimales, zu beruecksichtigendes Realisierungsdatum
     * @param maxDate maximales, zu beruecksichtigendes Realisierungsdatum
     * @return
     *
     */
    public List<AbstractBauauftragView> findBAVerlaufViews4KundeInShortTerm(
            Long kundeNo, Date minDate, Date maxDate);
}


