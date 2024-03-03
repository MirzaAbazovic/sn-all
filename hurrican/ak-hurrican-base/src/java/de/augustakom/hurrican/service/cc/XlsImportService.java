/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 16:10:52
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.service.base.exceptions.ImportException;

/**
 * Imports aus XLS Dateien.
 *
 *
 */
public interface XlsImportService extends ICCService {

    /**
     * Importiert HVTGruppe, HVTStandort, UEVT, DSLAM, Subrack (des DSLAMs) aus XLS für HVT_STANDORT_TYP_FTTC_KVZ. Die
     * Beschreibung des XLS Formates findet man im Wiki. Verhalten: <ul> <li>HVTGruppe und HVTStandort müssen im XLS
     * vorhanden sein</li> <li>Die nachfolgenden Entitäten sind wie folgt gruppiert und werden nur angelegt wenn im XLS
     * vorhanden <ul> <li>UEVT, inkl. Rangierungsmatrix über alle Produkte</li> <li>Rack (DSLAM), Subrack</li>
     * <li>HVT-Standort-TechType</li> </ul> </li> <li>sind Entitäten (z.B. die HVTGruppe) bereits in der DB vorhanden,
     * werden diese verwendet (ohne "merge")</li> <li>Die Standort-Technik (HVTStandort2Technik) ergibt sich aus dem
     * Hersteller des DSLAM</li> <li>HVT-Standort-TechType wird angelegt, wenn ein entsprechendes verfügbar-von Datum
     * vorhanden ist</li> </ul>
     *
     * @see https://intranet.m-net.de/display/MAITAppHurrican/Import+FTTC+KVZ+Standorte
     */
    XlsImportResultView[] importFttxKvz(byte[] xlsData, Long sessionId) throws ImportException;

    /**
     * Importiert Uevt Stifte. Um dies zu erreichen muss der Service den Umweg ueber eine HVT Bestellung gehen.
     * Beschreibung des XLS Formates im Wiki nachschlagen (link weiter unten). Verhalten: <ul> <li>prueft ob die Leiste
     * bereits existiert, wenn ja Vorgang abbrechen</li> <li>eine Bestellung anlegen (fixe Werte: {@code Angebot am} =
     * Importdatum, {@code Bestelldatum} = Importdatum, {@code Physiktyp} = H, {@code Anzahl CuDA} = 100)</li>
     * <li>danach werden die Stifte erzeugt ({@code UEVT-ClusterNo} = 1)</li> <li>beendet die Bestellung {@code
     * Bereitgestellt} = Importdatum</li> </ul> <b>Achtung</b>: Anlage wird auch dann ausgefuehrt, wenn eine offene
     * Bestellung existiert
     *
     * @see https://intranet.m-net.de/display/MAITAppHurrican/Import+UEVT-Stifte+%28HVT-Bestellungen%29
     */
    XlsImportResultView[] importUevtStifte(byte[] xlsData, Long sessionId) throws ImportException;

    /**
     * Importiert Baugruppen und erzeugt Ports. Die Anzahl der Ports haengt vom Baugruppen-typ ab. Bereits importierte
     * Daten werden nicht nochmals importiert, fehlen Angaben im Excel-File so werden die entsprechenden Zeilen
     * uebersprungen
     *
     * @see https://intranet.m-net.de/display/MAITAppHurrican/Import+Baugruppen
     */
    XlsImportResultView[] importBaugruppenAndPorts(byte[] xlsData, Long sessionId) throws ImportException;

    /**
     * Importiert Fttc KVZ Rangierungen. Bereits importierte Daten werden nicht nochmals importiert, fehlen Angaben im
     * Excel-File so werden die entsprechenden Zeilen uebersprungen.
     *
     * @see https://intranet.m-net.de/display/MAITAppHurrican/Import+Fttc+KVZ+ Rangierungen
     */
    XlsImportResultView[] importFttcKVZRangierungen(byte[] xlsData, Long sessionId) throws ImportException;

    /**
     * Importiert für FTTC_KVZ Standorte KVZ Adressen aus den "offiziellen" DTAG XLS Dateien. Vorhandene KVZ Adressen
     * werden durch die XLS Daten überschrieben, neue KVZ Adressen werden nur importiert, wenn es Equipments dafür gibt
     * (für den Standort und die KVZ-Nummer).
     *
     * @param xlsData
     * @return
     * @throws ImportException
     */
    XlsImportResultView[] importKvzAdressen(byte[] xlsData) throws ImportException;

    /**
     * Importiert Adressen für techn. Standorte (Adresse in HvtGruppe) und aktualisiert die DB. Ein Standort ist
     * eindeutig über die Werte ONKZ/ASB definiert.
     *
     * @param xlsData
     * @return
     * @throws ImportException
     */
    XlsImportResultView[] importHvtAdressen(byte[] xlsData) throws ImportException;

    /**
     * Importiert die Details für einen HVT Umzug aus DTAG XLS Dateien.
     *
     * @throws ImportException
     */
    XlsImportResultView[] importHvtUmzugDetails(HvtUmzug umzug, byte[] xlsData) throws ImportException;
}
