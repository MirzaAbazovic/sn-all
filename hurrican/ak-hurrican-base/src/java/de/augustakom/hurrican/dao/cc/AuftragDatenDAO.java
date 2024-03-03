/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 13:48:36
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;

/**
 * DAO-Interface fuer die Verwaltung der Auftrags-Daten.
 *
 *
 */
public interface AuftragDatenDAO extends FindDAO, StoreDAO, ByExampleDAO, HistoryUpdateDAO<AuftragDaten> {

    /**
     * Sucht nach einem AuftragDaten-Datensatz mit der Auftrag-ID <code>auftragId</code>. Zusaetzlich muss das
     * Gueltig-von und Gueltig-bis Datum aktuell sein.
     *
     * @param auftragId die Auftrags-ID
     * @return Instanz von AuftragDaten.
     */
    AuftragDaten findByAuftragId(Long auftragId);

    /**
     * Sucht nach allen AuftragDaten, die einem best. Kunden zugeordnet sind.
     *
     * @param kundeNo Kundennummer
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     */
    List<AuftragDaten> findByKundeNo(Long kundeNo);

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die einem best. Buendel zugeordnet sind.
     *
     * @param buendelNr         Buendel-Nr
     * @param buendelNrHerkunft
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     */
    List<AuftragDaten> findByBuendelNr(Integer buendelNr, String buendelNrHerkunft);

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die einem bestimmten Billing-Auftrag zugeordnet sind.
     *
     * @param orderNoOrig Billing-Auftragsnummer
     * @param ignoreStatus Soll der Status ignoriert werden? (ja -> alle Aufträge, nein -> nur aktive
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>
     *
     */
    List<AuftragDaten> findByOrderNoOrig(Long orderNoOrig, boolean ignoreStatus);

    /**
     * Sucht nach den Auftrags-Daten, die zu der Endstelle mit der ID <code>endstelleId</code> gehoeren.
     *
     * @param endstelleId ID der Endstelle
     * @return Instanz von <code>AuftragDaten</code> oder <code>null</code>.
     */
    AuftragDaten findByEndstelleId(Long endstelleId);

    /**
     * Sucht nach Auftrags-Daten, deren Endstelle der GeoId zugeordnet ist. Optional kann noch auf eine Liste von Produkten
     * (deren ID) gefiltert werden.
     *
     * @param geoId      GeoId der Endstelle
     * @param produktIds [optional] Produkt IDs
     * @return Liste mit AuftragDaten
     */
    List<AuftragDaten> findByGeoIdProduktIds(Long geoId, Long... produktIds);

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die einen bestimmten Port nutzten oder nutzten.
     *
     * @param equipmentId ID des Equipments
     * @return Liste von AuftragDaten (moeglicherweise leer)
     */
    List<AuftragDaten> findAuftragDatenByEquipment(Long equipmentId);

    /**
     * Sucht Auftragsdaten nach dem Equipment switch und hwEQN bzw. Gültigkeit der Auftragsdaten/technik. <br>
     * <b>Achtung:</b> sollte nur für "echte" (EWSD) switches verwendet werden, da ansonsten die Treffermenge riesig
     * werden kann.
     *
     * @param switchAK switch name z.B. AUG01
     * @param hwEQN
     * @param gueltig
     * @return
     */
    List<AuftragDaten> findAuftragDatenByEquipment(String switchAK, String hwEQN, Date gueltig);

    /**
     * Sucht Auftragsdaten nach der Rack(DSLAM) Gerätebezeichnung und hwEQN bzw. Gültigkeit der Auftragsdaten/technik.
     * <br>
     *
     * @param rackBezeichnung
     * @param hwEQN
     * @param gueltig
     * @return
     */
    List<AuftragDaten> findAuftragDatenByRackAndEqn(String rackBezeichnung, String hwEQN, Date gueltig);

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die auf einer bestimmten Baugruppe geschaltet sind oder waren.
     *
     * @param baugruppeId ID der Baugruppe
     * @return Liste von Instanzen von AuftragDaten (moeglicherweise leer)
     */
    List<AuftragDaten> findAuftragDatenByBaugruppe(Long baugruppeId);

    /**
     * Sucht nach dem Vater-Produkt bzw. dem Vater-Auftrag eines best. Buendels. <br> <br> Es handelt sich dann um das
     * Vater-Produkt bzw. den Vater-Auftrag, wenn das Flag 'isParent' des zugehoerigen Produkts auf <code>true</code>
     * gesetzt ist. <br> Es wird natuerlich nur nach den aktuellen(!) Auftrags-Daten gesucht. <br> Ausserdem darf der
     * Status der Auftrags-Daten nicht auf 'storno' oder 'Absage' stehen und darf auch nicht gekuendigt sein (Status <
     * 9800).
     *
     * @param kundeNo         Kundennummer, die dem Auftrag zugeordnet sein muss.
     * @param buendelNr       Nr. des Buendels, von dem der Parent-Auftrag gesucht wird
     * @param buendelHerkunft Name der Buendel-Herkunft fuer die gesucht Auftrags-Daten.
     * @return Instanz von <code>AuftragDaten</code> oder <code>null</code>.
     */
    AuftragDaten findParent4Buendel(Long kundeNo, Integer buendelNr, String buendelHerkunft);

    /**
     * Sucht nach allen sperr-relevanten Auftraegen eines best. Kunden.
     *
     * @param kundeNo Kundennummer des Kunden, dessen sperr-relevante Auftraege gesucht werden.
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     */
    List<AuftragDaten> find4Sperre(Long kundeNo);

    /**
     * Sucht nach der Produkt-ID eines best. Auftrags.
     *
     * @param auftragId ID des Auftrags, dessen Produkt-ID gesucht wird
     * @return ID des Produkts.
     */
    Long findProduktId4Auftrag(Long auftragId);

    /**
     * Ermittelt die Auftrag-IDs fuer einen best. Kunden Das Produkt ist optional .
     *
     * @param kundeNo Kundennummer
     * @param prodId  Produkt-ID (optional)
     * @return Liste mit den Auftrag-IDs.
     */
    List<Long> findAuftragIds(Long kundeNo, Long prodId);

    /**
     * Ermittelt die Auftrag-IDs eines Kunden von den aktiven (oder in der Realisierung befindenden) Auftraegen mit
     * einer Produkt-Gruppen ID aus <code>produktGruppen</code>.
     *
     * @param kundeNo
     * @param produktGruppen
     * @return
     */
    List<Long> findAuftragIdsInProduktGruppe(Long kundeNo, List<Long> produktGruppen);

    /**
     * Ermittelt alle aktiven AuftragDaten - Objekte die an der angegebenen Baugruppe realisiert sind und<br> status >=
     * AuftragStatus.TECHNISCHE_REALISIERUNG und status < AuftragStatus.AUFTRAG_GEKUENDIGT haben. Die Ergebnisliste ist
     * absteigend nach Status sortiert (wichtig für die Berechnung der EqVlans).
     *
     */
    List<AuftragDaten> findAktiveAuftragDatenByBaugruppe(Long baugruppeId);

    /**
     * Ermittelt AuftragDaten zur gegebenen auftragNoOrig und der B&uuml;ndelnummer.
     *
     * @param auftragNoOrig (original) Auftrags-Nummer aus dem Billing-System.
     * @param buendelNo B&uuml;ndelnummer.
     * @return Liste mit AuftragDaten.
     */
    List<AuftragDaten> findAuftragDatenByAuftragNoOrigAndBuendelNo(Long auftragNoOrig, Integer buendelNo);

    /**
     * Ermittelt alle aktiven AuftragDaten - Objekte die zu dem
     * angegebenen {@code ortsteil} zugewiesen sind mit der angegebenen {@code produktGruppe}.
     *
     * @param ortsteil
     * @param produktGrouppe
     * @return Liste mit AuftragDaten
     */
    @Nonnull
    List<AuftragDaten> findAktiveAuftragDatenByOrtsteilAndProduktGroup(@Nonnull String ortsteil, @Nonnull String produktGrouppe);
}


