/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2007 10:51:41
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.tal.ProduktDtag;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.ESAATalOrderService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt den Datensatz fuer Geschaeftsfall
 * B010 CuDa
 * B011 Glasfaser
 * fuer die elektronische Talschnittstelle gegenüber der DTAG Version 3.00
 *
 */
public class GetDataPhysikCommand extends AbstractTALDataCommand {

    private static final long serialVersionUID = -8757909913160883894L;

    private static final Logger LOGGER = Logger.getLogger(GetDataPhysikCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.ESAATalOrderService")
    private ESAATalOrderService esaaTalOrderServiceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    private Endstelle endstelle;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            CBVorgang cbVorgang = getCBVorgang();
            Carrierbestellung cb = getCarrierbestellung();

            endstelle = getEndstelle(cbVorgang.getAuftragId(), cb.getCb2EsId());
            if (endstelle != null) {
                if (endstelle.getRangierId() != null) {

                    Rangierung rangierung = rangierungsService.findRangierung(endstelle.getRangierId());
                    Equipment equipment = rangierungsService.findEquipment(rangierung.getEqOutId());
                    ProduktDtag prodDtag = findDtagProduktConfig(equipment.getRangSSType());

                    if ((prodDtag != null) && (prodDtag.getProduktDtag() != null)) {
                        List<TALSegment> result = new ArrayList<TALSegment>();
                        switch (prodDtag.getProduktDtag()) {
                            case 7:
                            case 8:
                                createLWL(equipment, prodDtag, result);
                                break;
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                                break;
                            default:
                                createCuDa(cbVorgang, equipment, prodDtag, result);
                                break;
                        }

                        return result;
                    }
                }
                else {
                    throw new HurricanServiceCommandException("Es konnte keine Rangierung ermittelt werden (Segment Physik)");
                }
            }
            else {
                throw new HurricanServiceCommandException("Es konnte keine Endstelle ermittelt werden (Segment Physik)");
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Es konnte kein Physiksegment(B010 oder B011) erstellt werden (Segment Physik):" + e.getMessage(), e);
        }
    }

    /**
     * Erstellt fuer den Basis-Auftrag sowie fuer evtl. vorhandene Sub-Auftraege das CuDa Segment.
     *
     * @param segmentList
     * @throws HurricanServiceCommandException
     * @throws FindException
     */
    void createCuDa(CBVorgang cbVorgang, Equipment equipment, ProduktDtag prodDtag, List<TALSegment> segmentList)
            throws HurricanServiceCommandException, FindException {
        @SuppressWarnings("unchecked")
        Set<CBVorgangSubOrder> subOrders = (Set<CBVorgangSubOrder>) getPreparedValue(AbstractTALCommand.KEY_SUB_ORDERS);

        // Vier-Draht-Bestellung
        if (Boolean.TRUE.equals(cbVorgang.getVierDraht())) {
            if (CollectionTools.isEmpty(subOrders) || (subOrders.size() != 1)) {
                throw new HurricanServiceCommandException("Zweiter Port fuer 4-Draht-Bestellung fehlt!");
            }
            Pair<Equipment, ProduktDtag> subOrderData = getEquipmentAndProduktConfigDtag(subOrders.iterator().next());
            if (!prodDtag.getId().equals(subOrderData.getSecond().getId())
                    || (equipment.getUetv() == null)
                    || (subOrderData.getFirst().getUetv() == null)
                    || (equipment.getUetv() != subOrderData.getFirst().getUetv())) {
                throw new HurricanServiceCommandException("Schnittstellen/Übertragungsverfahren der Ports nicht definiert oder stimmen nicht ueberein (" +
                        prodDtag.getRangSsType() + " != " + subOrderData.getSecond().getRangSsType() + ")");
            }
            createCuDaSegmentForEquipment(equipment, subOrderData.getFirst(), prodDtag, segmentList);
        }
        // (Geklammerte) Zwei-Draht-Bestellung
        else {
            createCuDaSegmentForEquipment(equipment, null, prodDtag, segmentList);
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (CBVorgangSubOrder subOrder : subOrders) {
                    Pair<Equipment, ProduktDtag> subOrderData = getEquipmentAndProduktConfigDtag(subOrder);
                    if (subOrderData != null) {
                        createCuDaSegmentForEquipment(subOrderData.getFirst(), null, subOrderData.getSecond(), segmentList);
                    }
                }
            }
        }
    }

    private Pair<Equipment, ProduktDtag> getEquipmentAndProduktConfigDtag(CBVorgangSubOrder subOrder)
            throws FindException, HurricanServiceCommandException {
        Endstelle endstelleSubOrder = endstellenService.findEndstelle4Auftrag(subOrder.getAuftragId(), endstelle.getEndstelleTyp());
        if ((endstelleSubOrder != null) && (endstelleSubOrder.getRangierId() != null)) {
            Rangierung rangSubOrder = rangierungsService.findRangierung(endstelleSubOrder.getRangierId());
            if ((rangSubOrder != null) && (rangSubOrder.getEqOutId() != null)) {
                Equipment dtagEquipment = rangierungsService.findEquipment(rangSubOrder.getEqOutId());
                if (dtagEquipment != null) {
                    ProduktDtag produktConfigDtag = findDtagProduktConfig(dtagEquipment.getRangSSType());
                    if (produktConfigDtag != null) {
                        return Pair.create(dtagEquipment, produktConfigDtag);
                    }
                }
            }
        }
        return null;
    }


    /**
     * Erstellt das Segment B010
     *
     * @param equipment
     * @param produktDtag
     * @return Object vom Typ TalSegment
     * @throws HurricanServiceCommandException
     *
     */
    private void createCuDaSegmentForEquipment(Equipment equipment, Equipment equipment2,
            ProduktDtag produktDtag, List<TALSegment> segmentList) throws HurricanServiceCommandException {
        try {
            TALSegment segment = new TALSegment();
            String schaltUevt1 = null;
            String schaltUevt2 = null;

            schaltUevt1 = equipment.getEinbau1DTAG();
            verifyPortLength(schaltUevt1);

            String anzahlCuda = null;
            if (equipment2 != null) {
                if ((equipment.getRangLeiste2() != null) || (equipment2.getRangLeiste2() != null)) {
                    throw new HurricanServiceCommandException("4-Draht-Ports koennen nicht in einer geklammerten " +
                            "4-Draht-Bestellung genutzt werden!");
                }
                schaltUevt2 = equipment2.getEinbau1DTAG();
                anzahlCuda = "4";
            }
            else if (equipment.getRangLeiste2() != null) {
                schaltUevt2 = equipment.getEinbau2DTAG();
            }
            verifyPortLength(schaltUevt2);

            segment.setSegmentName(TALSegment.SEGMENT_NAME_B010);
            if (StringUtils.isNotBlank(anzahlCuda)) {
                verifyMandatory(anzahlCuda, segment, "B010_2: Anzahl der Adern ist nicht gesetzt");
            }
            else {
                verifyMandatory(produktDtag.getB0102(), segment, "B010_2: Anzahl der Adern ist nicht gesetzt");
            }

            verifyMandatory(produktDtag.getB0103(), segment, "B010_3: Kennzeichen für Hochbitratig ist nicht gesetzt");
            verifyMandatory(produktDtag.getB0104(), segment, "B010_4: Kennzeichen für Zwischenregenerator ist nicht gesetzt");
            segment.addValue("N"); // Resourcenprüfung wird momentan in seltenen Fällen über ein extra Formblatt abgewickelt
            if (produktDtag.getB0103().equals("J")) {
                if (equipment.getUetv() == null) {
                    throw new HurricanServiceCommandException("Uebertragungsverfahren fehlt!");
                }
                segment.addValue(equipment.getUetv().name());
            }
            else {
                segment.addValue(null);
            }
            verifyMandatory(schaltUevt1, segment, "B010_7: Schaltangaben sind nicht gesetzt");
            segment.addValue((schaltUevt2 == null) ? null : schaltUevt2);

            segmentList.add(segment);
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ermitteln der Physik CuDa" + e.getMessage());
        }
    }

    /**
     * Erstellt das Segment B011
     *
     * @param equipment
     * @param pDtag
     * @return Objekt vom Typ TALSegment
     * @throws HurricanServiceCommandException
     *
     */
    private void createLWL(Equipment equipment, ProduktDtag pDtag, List<TALSegment> segmentList) throws HurricanServiceCommandException {
        try {
            TALSegment segment = new TALSegment();
            String schaltUevt1 = null;
            String schaltUevt2 = null;

            schaltUevt1 = equipment.getRangBucht() + equipment.getRangLeiste1();
            if (equipment.getRangStift1().equals("100")) {
                schaltUevt1 = schaltUevt1 + "00";
            }
            else {
                schaltUevt1 = schaltUevt1 + equipment.getRangStift1();
            }
            if (equipment.getRangLeiste2() != null) {
                schaltUevt2 = equipment.getRangBucht() + equipment.getRangLeiste2();
                if (equipment.getRangStift2().equals("100")) {
                    schaltUevt2 = schaltUevt2 + "00";
                }
                else {
                    schaltUevt2 = schaltUevt2 + equipment.getRangStift2();
                }
            }

            verifyPortLength(schaltUevt1);
            verifyPortLength(schaltUevt2);

            segment.setSegmentName(TALSegment.SEGMENT_NAME_B011);
            verifyMandatory(pDtag.getB0102(), segment, "B011_2: Anzahl der Fasern ist nicht gesetzt");
            verifyMandatory(schaltUevt1, segment, "B011_3: Schaltangaben für Faser 1 sind nicht gesetzt");
            verifyMandatory(schaltUevt2, segment, "B011_4: Schaltangaben für Faser 2 sind nicht gesetzt");

            segmentList.add(segment);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ermitteln der Physik LWL" + e.getMessage());
        }
    }

    /* Ermittelt die Konfiguration des DTAG Produkts (entspricht der Leitungsart) */
    private ProduktDtag findDtagProduktConfig(String rangSsType) throws HurricanServiceCommandException, FindException {
        ProduktDtag produktDtagExample = new ProduktDtag();
        produktDtagExample.setRangSsType(rangSsType);
        List<ProduktDtag> result = esaaTalOrderServiceService.getProduktDtag(produktDtagExample);

        if (CollectionTools.isEmpty(result) || (result.size() > 1)) {
            throw new HurricanServiceCommandException(
                    "Produktbezeichnung DTAG konnte nicht ermittelt werden!");
        }
        return result.get(0);
    }

    /**
     * Ueberprueft, ob die Schaltangaben 8-stellig sind. Ist dies nicht der Fall, wird eine Exception generiert.
     *
     * @param toCheck
     * @throws HurricanServiceCommandException
     */
    private void verifyPortLength(String toCheck) throws HurricanServiceCommandException {
        if ((toCheck != null) && (toCheck.length() != 8)) {
            throw new HurricanServiceCommandException(
                    "Schaltangaben UEVT falsch! Erwartet 8-stellig, ermittelt " + toCheck.length() + "-stellig!");
        }
    }

}


