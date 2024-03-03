/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2010 13:23:11
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;


//@formatter:off
/**
 * Klasse, um die einen Baugruppen-Schwenk vom Typ 'Baugruppentyp aendern' durchzufuehren. <br/>
 * Ablauf fuer den Baugruppen-Schwenk: <br/>
 * <ul>
 *     <li>Baugruppe ermitteln und den Typ aendern
 *     <li>Physiktyp der Rangierungen ggf. anpassen
 * </ul>
 */
//@formatter:on
public class HWBaugruppenChangeBgTypeExecuter implements HWBaugruppenChangeExecuter {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeBgTypeExecuter.class);

    private HWService hwService;
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    private RangierungsService rangierungsService;
    private PhysikService physikService;
    private HWBaugruppenChange hwBgChange;

    private List<HWBaugruppenChangePort2PortView> portMappingViews;

    private HWBaugruppenChangeBgType hwBgChangeBgType;
    private HWBaugruppe hwBaugruppeToChange;
    private List<Equipment> equipmentsOfHwBaugruppe;

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param toExecute
     * @param hwService
     * @param hwBaugruppenChangeService
     * @param rangierungsService
     * @param physikService
     */
    public void configure(HWBaugruppenChange toExecute,
            HWService hwService,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            RangierungsService rangierungsService,
            PhysikService physikService) {
        this.physikService = physikService;
        this.hwService = hwService;
        this.hwBaugruppenChangeService = hwBaugruppenChangeService;
        this.rangierungsService = rangierungsService;

        setHwBgChange(toExecute);
    }

    /**
     * Fuehrt den Baugruppen-Schwenk durch
     *
     * @throws StoreException
     */
    public void execute() throws StoreException {
        try {
            checkData();
            loadPortMappingViews();
            changeBgType();
            modifyAndActivateEquipments();
            modifyAndActivateRangierung();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ausfuehrung des Baugruppen-Schwenks: " + e.getMessage(), e);
        }
    }


    /**
     * Aendert den Typ der angegebenen Baugruppe.
     */
    void changeBgType() throws StoreException {
        try {
            hwBaugruppeToChange.setHwBaugruppenTyp(hwBgChangeBgType.getHwBaugruppenTypNew());

            hwService.saveHWBaugruppe(hwBaugruppeToChange);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Aenderung des Baugruppen-Typs: " + e.getMessage(), e);
        }
    }


    /**
     * Ermittelt die Ports der Baugruppe und aendert den Status der Ports auf 'rangiert' ab.
     */
    void modifyAndActivateEquipments() throws StoreException {
        try {
            equipmentsOfHwBaugruppe = rangierungsService.findEquipments4HWBaugruppe(hwBaugruppeToChange.getId());
            if (CollectionTools.isNotEmpty(equipmentsOfHwBaugruppe)) {
                for (Equipment equipment : equipmentsOfHwBaugruppe) {
                    equipment.setStatus(EqStatus.rang);
                    rangierungsService.saveEquipment(equipment);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Aktualisierung des Ports: " + e.getMessage(), e);
        }
    }


    /**
     * Ermittelt die betroffenen Rangierungen und aendert den Status und ggf. den Physiktyp der Rangierung ab. (Es wird
     * nur der Physiktyp geaendert, sofern ein neuer Physiktyp angegeben ist und das Equipment als EQ_IN verbunden
     * ist.)
     */
    void modifyAndActivateRangierung() throws StoreException {
        if (CollectionTools.isNotEmpty(equipmentsOfHwBaugruppe)) {
            try {
                for (Equipment equipment : equipmentsOfHwBaugruppe) {
                    Rangierung rangierungIn = rangierungsService.findRangierung4Equipment(equipment.getId(), true);
                    modifyAndActivateRangierung(rangierungIn, hwBgChange.getPhysikTypNew(), true);

                    Rangierung rangierungOut = rangierungsService.findRangierung4Equipment(equipment.getId(), false);
                    if (rangierungOut != null) {
                        modifyAndActivateRangierung(rangierungOut, hwBgChange.getPhysikTypNew(), false);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException("Fehler bei der Aktivierung der Rangierung: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Setzt den Status der Rangierung auf 'freigegeben' und aendert ggf. den Physiktyp der Rangierung.
     */
    void modifyAndActivateRangierung(Rangierung toModify, PhysikTyp newPhysikTyp, boolean isPrimeRangierung) throws StoreException {
        if (toModify != null) {
            try {
                if (newPhysikTyp != null) {
                    if (isPrimeRangierung) {
                        toModify.setPhysikTypId(newPhysikTyp.getId());
                    }
                    else {
                        PhysikTyp oldPhysikTyp = physikService.findPhysikTyp(toModify.getPhysikTypId());
                        if (physikService.manufacturerChanged(oldPhysikTyp, newPhysikTyp)) {
                            // passenden Physiktyp vom neuen Hersteller ermitteln und setzen
                            PhysikTyp newChildPT = physikService.findCorrespondingPhysiktyp(
                                    oldPhysikTyp, newPhysikTyp.getHvtTechnikId());
                            toModify.setPhysikTypId(newChildPT.getId());
                        }
                    }
                }
                toModify.setFreigegeben(Freigegeben.freigegeben);

                rangierungsService.saveRangierung(toModify, false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException("Fehler bei der Aenderung des Physiktyps der Rangierung: " + e.getMessage(), e);
            }
        }
    }

    /*
     * Laedt die View-Daten mit den betroffenen Port-Mappings.
     * Die View-Objekte werden fuer die Ermittlung der Rangierungen benoetigt, um den
     * Physiktyp ggf. anzuaendern.
     */
    private void loadPortMappingViews() throws FindException {
        portMappingViews = hwBaugruppenChangeService.findPort2PortViews(hwBgChange);
        if (CollectionTools.isEmpty(portMappingViews)) {
            throw new FindException("View mit den Port-Mappings konnte nicht ermittelt werden!");
        }
    }


    /* Ueberprueft die fuer die Ausfuehrung notwendigen Daten. */
    private void checkData() throws StoreException {
        if (hwBgChange == null) {
            throw new StoreException("Planung zur Ausfuehrung ist nicht angegeben!");
        }

        if ((hwBgChange.getHwBgChangeBgType() == null) || hwBgChange.getHwBgChangeBgType().isEmpty()) {
            throw new StoreException("Die angegebene Planung enthaelt nicht die benoetigten Daten!");
        }
        else {
            setHwBgChangeBgType(hwBgChange.getHwBgChangeBgType().iterator().next());
            setHwBaugruppeToChange(hwBgChangeBgType.getHwBaugruppe());
        }
    }

    public void setHwBgChange(HWBaugruppenChange hwBgChange) {
        this.hwBgChange = hwBgChange;
    }

    public void setHwBgChangeBgType(HWBaugruppenChangeBgType hwBgChangeBgType) {
        this.hwBgChangeBgType = hwBgChangeBgType;
    }

    public void setHwBaugruppeToChange(HWBaugruppe hwBaugruppeToChange) {
        this.hwBaugruppeToChange = hwBaugruppeToChange;
    }

    public void setEquipmentsOfHwBaugruppe(List<Equipment> equipmentsOfHwBaugruppe) {
        this.equipmentsOfHwBaugruppe = equipmentsOfHwBaugruppe;
    }

}


