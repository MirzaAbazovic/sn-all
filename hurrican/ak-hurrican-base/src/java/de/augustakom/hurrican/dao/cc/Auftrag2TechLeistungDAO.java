/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 10:01:56
 */
package de.augustakom.hurrican.dao.cc;

import java.time.*;
import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AuftragTechLeistungDAO</code>.
 *
 *
 */
public interface Auftrag2TechLeistungDAO extends ByExampleDAO, StoreDAO {

    /**
     * Ermittelt alle die Zuordnungen von einem Auftrag zu den techn. Leistungen. Ueber das Flag <code>onlyAct</code>
     * wird bestimmt ob alle (false) oder nur die noch aktiven (true) Leistungen ermittelt werden sollen.
     *
     * @param ccAuftragId    Auftrags-ID
     * @param techLeistungId (optional) ID der zu beachtenden techn. Leistung. Erfolgt keine Angabe, werden alle techn.
     *                       Leistungen beachtet.
     * @param onlyAct
     * @return die ermittelten technischen Leistungen
     *
     */
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen(Long ccAuftragId,
            Long techLeistungId, boolean onlyAct);

    /**
     * Ermittelt die Zuordnungen von technischen Leistungen zu einem Auftrag zu einem bestimmten Datum. <br> Die
     * Gueltigkeit wird dabei mit 'AktivVon<=validDate && AktivBis>validDate' geprueft. (Es wird dabei nur das Datum,
     * nicht die Uhrzeit beruecksichtigt!)
     *
     * @param auftragId       Auftrags-ID
     * @param techLeistungIds IDs der zu beruecksichtigenden Leistungen
     * @param validDate       zu beruecksichtigendes Datum
     * @return
     */
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen(Long ccAuftragId,
            Long[] techLeistungIds, Date validDate, boolean ignoreAktivVon);


    /**
     * Ermittelt alle aktiven technischen Leistungen, die einem bestimmten Auftrag zugeordnet sind. <br> Das Ergebnis
     * wird ueber die technische Leistung gruppiert - dadurch erhaelt man fuer jeden Leistungstyp lediglich ein Result.
     * Die Quantity ist auf-summiert.
     *
     * @param auftragId
     * @return
     *
     */
    public List<Auftrag2TechLeistung> findActiveA2TLGrouped(Long auftragId);

    /**
     * Ermittelt alle zur "checkDate" aktiven technischen Leistungen, die einem bestimmten Auftrag zugeordnet sind.
     *
     * @param auftragId
     * @param checkDate
     * @return
     */
    public List<Auftrag2TechLeistung> findActiveA2TLGrouped(Long auftragId, LocalDate checkDate);

    /**
     * Ermittelt alle Auftrags-Leistungen, die einem bestimmten Verlauf (Realisierung od. Kuendigung) zugeordnet sind.
     *
     * @param verlaufId Verlaufs-ID
     * @return Liste mit Objekten des Typs <code>AuftragTechLeistung</code>.
     *
     */
    public List<Auftrag2TechLeistung> findAuftragTechLeistungen4Verlauf(Long verlaufId);

    /**
     * Loescht die uebergebene Auftrag2TechLeistung.
     *
     * @param leistung die zu loeschende Leistung
     */
    public void delete(Auftrag2TechLeistung leistung);

}


