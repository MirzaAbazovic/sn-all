/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2010 12:57:59
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Klasse, um eine geplante Port-Aenderung zu stornieren. Die Stornierung kann nur erfolgen, so lange die Aenderung noch
 * nicht ausgefuehrt wurde. <br> Abhaengig vom Status der Port-Aenderung wird nur der Planungs-Datensatz storniert oder
 * auch bereits durchgefuehrte Vorbereitungen (z.B. Port-Status aendern) rueckgaengig gemacht.
 */
public class CancelHWBaugruppenChangeExecuter {

    private static final Logger LOGGER = Logger.getLogger(CancelHWBaugruppenChangeExecuter.class);

    private RangierungsService rangierungsService;
    private HWBaugruppenChange hwBgChange;

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param hwBgChange
     * @param rangierungsService
     */
    public void configure(HWBaugruppenChange hwBgChange,
            RangierungsService rangierungsService) {
        setHwBgChange(hwBgChange);
        setRangierungsService(rangierungsService);
    }


    /**
     * Fuehrt die Stornierung des Baugruppen-Schwenks durch.
     *
     * @throws StoreException
     */
    public void execute() throws StoreException {
        try {
            checkData();

            if (hwBgChange.isExecuteAllowed()) {
                // Planung ist bereits vorbereitet --> Aenderungen muessen rueckgaengig gemacht werden!
                doCancel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Stornieren der Planung: " + e.getMessage(), e);
        }
    }


    void doCancel() throws StoreException {
        if (hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_CARD)
                || hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_BG_TYPE)
                || hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.PORT_CONCENTRATION)) {
            doCancelChangeCardOrBgTyp();
        }
        else {
            throw new StoreException("Storno ist fuer diesen Planungs-Typ noch nicht implementiert!");
        }
    }


    /* Fuehrt den Storno fuer den Typ <einfacher Kartenwechsel> und <Baugruppentyp aendern> durch. */
    void doCancelChangeCardOrBgTyp() throws StoreException {
        try {
            if (hwBgChange.getPort2Port() != null) {
                Iterator<HWBaugruppenChangePort2Port> portMappingIterator = hwBgChange.getPort2Port().iterator();
                while (portMappingIterator.hasNext()) {
                    HWBaugruppenChangePort2Port port2port = portMappingIterator.next();
                    rollbackPortState(port2port.getEquipmentOld(), port2port.getEqStateOrigOld(), port2port.getRangStateOrigOld());
                    rollbackPortState(port2port.getEquipmentOldIn(), port2port.getEqStateOrigOld(), port2port.getRangStateOrigOld());

                    rollbackPortState(port2port.getEquipmentNew(), port2port.getEqStateOrigNew(), port2port.getRangStateOrigNew());
                    rollbackPortState(port2port.getEquipmentNewIn(), port2port.getEqStateOrigNew(), port2port.getRangStateOrigNew());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Fehler waehrend der Status-Aenderung der betroffenen Ports/Rangierungen: " + e.getMessage(), e);
        }
    }


    /**
     * Aendert den Status des Equipments und der (evtl) verbundenden Rangierung auf die angegebenen Stati ab.
     */
    void rollbackPortState(Equipment equipment, EqStatus equipmentState, Freigegeben rangierungsState) throws StoreException, FindException {
        if (equipment != null) {
            equipment.setStatus(equipmentState);
            rangierungsService.saveEquipment(equipment);

            Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
            if (rangierung != null) {
                rangierung.setFreigegeben(rangierungsState);
                rangierungsService.saveRangierung(rangierung, false);
            }
        }
    }


    /* Ueberprueft die fuer die Ausfuehrung notwendigen Daten. */
    private void checkData() throws StoreException {
        if (hwBgChange == null) {
            throw new StoreException("Planung zur Ausfuehrung ist nicht angegeben!");
        }
    }


    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    public void setHwBgChange(HWBaugruppenChange hwBgChange) {
        this.hwBgChange = hwBgChange;
    }

}


