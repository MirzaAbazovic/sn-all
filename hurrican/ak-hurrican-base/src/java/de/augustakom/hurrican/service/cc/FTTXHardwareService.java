/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2010
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.view.FTTBDpuImportView;
import de.augustakom.hurrican.model.cc.view.FTTBDpuKarteImportView;
import de.augustakom.hurrican.model.cc.view.FTTBDpuPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTBMduImportView;
import de.augustakom.hurrican.model.cc.view.FTTBMduPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTXStandortImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service, zur Bereitstellung von FTTX Hardware Operationen.
 */
public interface FTTXHardwareService extends ICCService {

    /**
     * Legt die Stifte fuer einen FTTB-Standort an
     *
     * @param hvtName
     * @param leiste
     * @param stift
     * @param type    Leisten-Typ
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTBStift(String hvtName, String leiste, String stift, RangSchnittstelle type) throws StoreException;

    /**
     * Legt eine MDU an
     *
     * @param model     Daten der MDU
     * @param sessionId Id der Benutzer-Session
     * @return die angelegte MDU
     * @throws StoreException Falls ein Fehler auftrat
     */
    HWMdu generateFTTBMdu(FTTBMduImportView model, Long sessionId) throws StoreException;

    /**
     * Legt einen MDU-Port an
     *
     * @param view      Daten des MDU-Ports
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTBMduPort(FTTBMduPortImportView view, Long sessionId) throws StoreException;

    /**
     * Legt die Standort-Daten fuer einen FTTB-Standort an
     *
     * @param model     Daten des Standorts
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTXStandort(FTTXStandortImportView model, Long sessionId) throws StoreException;

    /**
     * Funktion aktualisiert die Seriennummer einer MDU. Falls sich die Seriennummer geaendert hat, wird ein updateMDU
     * an den CPS gesendet.
     *
     * @param bezeichnung  MDU-Bezeichnung
     * @param seriennummer Seriennummer
     * @return Pair mit der ermittelten MDU und Boolean Flag, ob die Seriennummer geaendert wurde.
     * @throws StoreException
     */
    Pair<HWMdu, Boolean> updateMDU(String bezeichnung, String seriennummer, Long sessionId) throws StoreException;

    /**
     * Legt eine ONT an
     *
     * @param model     view Daten der ONT
     * @param sessionId Id der Benutzer-Session
     * @return die angelegte ONT
     * @throws StoreException falls ein Fehler auftrit
     */
    HWOnt generateFTTHOnt(FTTHOntImportView model, Long sessionId) throws StoreException;

    /**
     * Legt eine DPO an
     *
     * @param model     view Daten der DPO
     * @param sessionId Id der Benutzer-Session
     * @return die angelegte DPO
     * @throws StoreException falls ein Fehler auftrit
     */
    HWDpo generateFTTBHDpo(FTTBHDpoImportView model, Long sessionId) throws StoreException;

    /**
     * Legt einen ONT-Port an
     *
     * @param view      Daten des ONT-Ports
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTHOntPort(FTTHOntPortImportView view, Long sessionId) throws StoreException;

    /**
     * Legt einen DPO-Port an
     *
     * @param view      Daten des DPO-Ports
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTBHDpoPort(FTTBHDpoPortImportView view, Long sessionId) throws StoreException;

    /**
     * prüft, ob alle relevanten ONT Daten, welche in der DB keine Pflichtfelder sind, gesetzt sind
     *
     * @return true, wenn alle Pflichtfelder gesetzt sind
     */
    boolean checkIfOntFieldsComplete(HWOnt hwOnt);

    /**
     * prüft, ob alle relevanten DPO Daten, welche in der DB keine Pflichtfelder sind, gesetzt sind
     *
     * @return true, wenn alle Pflichtfelder gesetzt sind
     */
    boolean checkIfDpoFieldsComplete(HWDpo hwDpo);

    /**
     * Vergleicht die Daten einer bereits persistierten ONT mit einem neuen Update Datensatz. Alle Felder bis auf die
     * Seriennummer muessen uebereinstimmen. Wenn die Seriennummer sich aendert, handelt es sich um einen Tausch
     * (Defekt).
     */
    boolean isOntEqual(FTTHOntImportView ontImportView, HWOnt ont) throws FindException;

    /**
     * Vergleicht die Daten einer bereits persistierten DPO mit einem neuen Update Datensatz. Alle Felder bis auf die
     * Seriennummer muessen uebereinstimmen. Wenn die Seriennummer sich aendert, handelt es sich um einen Tausch
     * (Defekt).
     */
    boolean isDpoEqual(FTTBHDpoImportView dpoImportView, HWDpo dpo) throws FindException;

    /**
     * Legt eine DPU an
     *
     * @param model     view Daten der DPU
     * @param sessionId Id der Benutzer-Session
     * @return die angelegte DPU
     * @throws StoreException falls ein Fehler auftrit
     */
    HWDpu generateFTTBDpu(FTTBDpuImportView model, Long sessionId) throws StoreException;

    /**
     * Legt einen DPU-Port an
     *
     * @param view      Daten des DPU-Ports
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTBDpuPort(FTTBDpuPortImportView view, Long sessionId) throws StoreException;

    /**
     * Legt eine DPU-Karte an
     *
     * @param view      Daten der DPU-Karte
     * @param sessionId Id der Benutzer-Session
     * @throws StoreException Falls ein Fehler auftrat
     */
    void generateFTTBDpuKarte(FTTBDpuKarteImportView view, Long sessionId) throws StoreException;

    /**
     * prüft, ob alle relevanten DPU Daten, welche in der DB keine Pflichtfelder sind, gesetzt sind
     *
     * @return true, wenn alle Pflichtfelder gesetzt sind
     */
    boolean checkIfDpuFieldsComplete(HWDpu hwDpu);

    /**
     * Vergleicht die Daten einer bereits persistierten DPU mit einem neuen Update Datensatz. Alle Felder bis auf die
     * Seriennummer muessen uebereinstimmen. Wenn die Seriennummer sich aendert, handelt es sich um einen Tausch
     * (Defekt).
     */
    boolean isDpuEqual(FTTBDpuImportView dpuImportView, HWDpu dpu) throws FindException;

    /**
     * prüft, ob {@link HWOltChild} gelöscht werden kann und führt die Löschung anschließend aus
     * - prüft, ob an der {@link HWOltChild} aktive Aufträge hängen
     * - löscht die Resource per CPS-Service
     * - löscht die ONT in Hurrican und
     * - gibt die Ports, die an der ONT hängen frei
     * @param hwOltChild Daten der HWOltChild
     * @param checkLastCpsTx Flag, ob eine Validierung mit der letzten CPS-TX ausgeführt werden soll
     * @param sessionId  Id der Benutzer-Session
     * @throws FindException, StoreException, ValidationException
     * @return gibt die ID der CPSTx zurück
     */
    Long checkHwOltChildForActiveAuftraegeAndDelete(HWOltChild hwOltChild, boolean checkLastCpsTx, Long sessionId)throws FindException, StoreException, ValidationException;

    /**
     * prüft, ob auf der OltChild aktive Aufträge geschalten sind
     * @param hwOltChild
     * @throws FindException
     * @return  String mit gefundenen auftragDaten.getAuftragId()
     */
    String checkHwOltChildForActiveAuftraege(HWOltChild hwOltChild)throws FindException;

    /**
     * Findet alle Aufträge zu einem OltChild über die zugehörigen Equipments, Rangierung und Endstelle.
     * @param hwOltChild
     * @return
     * @throws FindException
     */
    Set<AuftragDaten> findAuftraege4OltChild(HWOltChild hwOltChild) throws FindException;

    /**
     * triggert das Löschen der Resource im CPS-Service
     * löscht den OltChild in Hurrican und gibt die Ports frei, die an dem OltChild hängen (Rangierung = 0)
     * Über das Flag checkLastCpsTx kann gesteuert werden, ob eine Kompatibilitätsprüfung mit der letzten
     * CPS-Transaktion stattfinden soll
     *
     * @param hwOltChild
     * @param checkLastCpsTx
     * @param sessionId
     * @throws FindException
     * @throws StoreException
     * @throws ValidationException
     * @return gibt die ID der CPSTx zurück
     */
    Long deleteHwOltChildWithCpsTx(HWOltChild hwOltChild, boolean checkLastCpsTx, final Long sessionId)
            throws FindException, StoreException, ValidationException;

    /**
     * loescht den {@code HwOltChild} in Hurrican und gibt die Ports frei, die an dem OltChild haengen (Rangierung = 0)
     *
     * @param hwOltChild
     * @throws FindException
     * @throws StoreException
     * @throws ValidationException
     */
    void deleteHwOltChild(HWOltChild hwOltChild) throws FindException, StoreException, ValidationException;
}
