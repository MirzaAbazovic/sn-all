/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 08:48:23
 */
package de.augustakom.hurrican.service.cc.fttx;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Service-Interface fuer die Verwatlung von S- und C-VLANs.
 */
@ObjectsAreNonnullByDefault
public interface VlanService extends ICCService {

    /**
     * Ermittelt die notwendigen {@link CvlanServiceTyp}en, die zum Zeitpunkt {@code when} zu dem Auftrag mit der Id
     * {@code auftragId} angelegt werden muessen. <br> Das Set der {@link CvlanServiceTyp}es setzt sich dabei wie folgt
     * zusammen: <br> <ul> <li>Default (Mandatory) {@link CvlanServiceTyp}es des Produkts für die gegebenen StandortTyp
     * (TechLocationTypeRefId) und OLT Hersteller (hvtTechnikId) <li>{@link CvlanServiceTyp}es der technischen
     * Leistungen, die zum Zeitpunkt {@code when} zum Auftrag aktiv sind und als moegliche {@link CvlanServiceTyp}es dem
     * Produkt sowie dem EKP-Rahmenvertrag zugeordnet sind </ul>
     *
     * @param auftragId             ID des Auftrags
     * @param prodId                Produkt-ID des Auftrags
     * @param ekpFrameContract      EKP-Rahmenvertrag mit den moeglichen / erlaubten {@link CvlanServiceTyp}es
     * @param techLocationTypeRefId StandortTyp des OLT Standortes
     * @param hvtTechnikId          Hersteller ID der OLT
     * @param when                  Zeitpunkt, zu dem die {@link CvlanServiceTyp}en berechnet werden sollen
     * @return Set mit den notwendigen {@link CvlanServiceTyp}en zum Zeitpunkt {@code when}
     */
    public Set<CvlanServiceTyp> getNecessaryCvlanServiceTypes4Auftrag(Long auftragId, Long prodId,
            EkpFrameContract ekpFrameContract, Long techLocationTypeRefId, Long hvtTechnikId, LocalDate when)
            throws FindException;

    /**
     * Berechnet die VLANs zu dem angegeben Auftrag fuer den Zeitpunkt {@code when} unter Beachtung des {@link
     * HWOlt#getVlanAktivAb()} Datums. <ul> <li>Ist vlanAktivAb <code>null</code> => keine Vlan Berechnung</li>
     * <li>Ansonsten werden Vlans berechnet, wobei diese erst ab max(vlanAktivAb, when) gültig werden</li> </ul>
     *
     * @param ekpFrameContract
     * @param auftragId
     * @param prodId
     * @param when
     * @return
     * @throws FindException
     */
    List<EqVlan> calculateEqVlans(EkpFrameContract ekpFrameContract, Long auftragId, Long prodId, LocalDate when)
            throws FindException;

    /**
     * @see {@link VlanService#calculateEqVlans(EkpFrameContract, Long, Long, LocalDate)}
     * Der {@link EkpFrameContract} wird aus dem angegebenen Auftrag ermittelt.
     *
     * @param auftragId
     * @param prodId
     * @param when
     * @return
     * @throws FindException
     */
    List<EqVlan> calculateEqVlans(Long auftragId, Long prodId, LocalDate when)
            throws FindException;

    /**
     * Berechnet die VLANs zu dem angegebenen Auftrag fuer den Zeitpunkt {@code when} und weist dem Auftrag diese zu.
     * Diese Methode kann mehrmals aufgerufen werden und historisiert die VLANs nur bei Änderung.
     *
     * @param auftragId
     * @param prodId
     * @param when
     * @param auftragAktion (optionale) Angabe der {@link AuftragAktion}, durch die die VLANs neu berechnet werden
     *                      sollen
     * @return
     * @throws FindException
     * @throws StoreException
     */
    List<EqVlan> assignEqVlans(EkpFrameContract ekpFrameContract, Long auftragId, Long prodId, LocalDate when,
            @Nullable AuftragAktion auftragAktion)
            throws FindException, StoreException;

    /**
     * Ermittelt alle gespeicherten EqVlans zu einem Equipment.
     *
     * @param equipmentId
     * @return
     */
    List<EqVlan> findEqVlans(Long equipmentId);

    /**
     * Ermittelt alle EqVlan zu einem Equipment, die zum angegebenen Datum gueltig sind.
     *
     * @param equipmentId
     * @param validDate
     * @return
     */
    List<EqVlan> findEqVlans(Long equipmentId, Date validDate);

    /**
     * Speichert die neuen eqVlans wenn diese sich von den vorherigen (historisierten) unterscheiden. Das "gueltigBis"
     * Datum von historisierten eqVlans wird auf das gueltigVon Datum der neuen Werte gesetzt.
     *
     * @param eqVlans       muessen alle zu einem Equipment gehoeren und die gleiche Gueltigkeit haben
     * @param auftragAktion (optionale) Angabe der {@link AuftragAktion}, durch die die VLANs geaendert werden
     * @return die nach dem Speichern gueltigen Werte (ACHTUNG: die sind nicht immer die uebergebenen Werte)
     * @throws StoreException
     */
    List<EqVlan> addEqVlans(List<EqVlan> eqVlans, @Nullable AuftragAktion auftragAktion) throws StoreException;

    /**
     * Ermittelt alle {@link EqVlan}s, die zum Zeitpunkt {@code when} dem Auftrag mit der {@code auftragId} zugeordnet
     * sind.
     *
     * @param auftragId des Auftrags
     * @param when      Zeitpunkt, zu dem die {@link EqVlan}s ermittelt werden
     * @return
     * @throws FindException
     */
    public List<EqVlan> findEqVlans4Auftrag(Long auftragId, LocalDate when) throws FindException;

    /**
     * Deaktiviert (loescht) alle {@link EqVlan}s, die durch die angegebene {@link AuftragAktion} angelegt wurden und
     * (re)aktiviert die {@link EqVlan}s, die durch die {@link AuftragAktion} deaktiviert wurden.
     *
     * @param auftragId     Id des Auftrags welchem die {@link EqVlan}s zugeordnet sind
     * @param auftragAktion {@link AuftragAktion}, die rueckgaengig gemacht werden soll
     * @return reaktivierte {@link EqVlan}s
     * @throws StoreException
     * @throws FindException
     */
    List<EqVlan> cancelEqVlans(Long auftragId, AuftragAktion auftragAktion) throws StoreException,
            FindException;

    /**
     * Loescht alle {@link EqVlan}s.
     *
     * @param equipmentId EquipmentId, welchem die {@link EqVlan}s zugeordnet sind
     */
    void removeEqVlans(long equipmentId) throws DeleteException;

    /**
     * Verschiebt den Startzeitpunkt für alle {@link EqVlan}s mit dem Startzeitpunkt {@code oldValidFrom} nach {@code
     * newValidFrom}.
     * <p/>
     * Ermittelt alle {@link EqVlan}s des Auftrags und verschiebt die gueltigVon/gueltigBis Werte. Sofern eine {@link
     * AuftragAktion} angegeben ist, werden nur die {@link EqVlan}s modifiziert, die mit dieser {@link AuftragAktion}
     * verbunden sind; ist keine {@link AuftragAktion} angegeben, so werden alle gueltigVon/gueltigBis Werte verschoben,
     * bei denen das {@code originalDate} uebereinstimmt.
     *
     * @param auftragId     Id des Auftrags welchem die {@link EqVlan}s zugeordnet sind
     * @param oldValidFrom  Startzeitpunkt der zu verschiebenden {@link EqVlan}s
     * @param newValidFrom  neuer Startzeitpunkt der Gueltigkeit
     * @param auftragAktion (optional) {@link AuftragAktion}, deren Datum verschoben wird
     * @return verschobene {@link EqVlan}s mit neuer Gueltigkeit
     * @throws StoreException
     * @throws FindException
     */
    List<EqVlan> moveEqVlans4Auftrag(Long auftragId, LocalDate oldValidFrom, LocalDate newValidFrom,
            @Nullable AuftragAktion auftragAktion)
            throws StoreException, FindException;

    /**
     * Aendert / setzt das Datum, ab dem eine OLT mandantenfaehig ist (Wholesale). ACHTUNG: Wird das Datum gesetzt,
     * werden zu allen Ports, die der OLT zugeordnet sind VLANs berechnet und gespeichert. Wird der Wert auf {@code
     * null} gesetzt, werden alle berechneten VLANs geloescht.
     *
     * @param hwOltId     Id der OLT
     * @param vlanAktivAb Datum, ab dem die OLT mandantenfaehig ist. Muss groeßer heute sein (also mindestens morgen)
     * @return first -> die OLT mit vlanAktivAb gesetzt; second -> die Anzahl der Aufträge für die VLans berechnet
     * wurden
     * @throws StoreException
     */
    public Pair<HWOlt, Integer> changeHwOltVlanAb(Long hwOltId, @Nullable Date vlanAktivAb) throws StoreException;

    EqVlan saveEqVlan(@Nonnull EqVlan toSave);
}
