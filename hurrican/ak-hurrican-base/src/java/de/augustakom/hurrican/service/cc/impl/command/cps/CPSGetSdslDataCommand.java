/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 09:16:20
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSdslData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSdslDslamPortData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Command-Klasse, um SDSL-Daten fuer einen Auftrag zu ermitteln. <br> Die ermittelten Daten werden von dem Command in
 * einem XML-Element in der vom CPS erwarteten Struktur aufbereitet.
 */
public class CPSGetSdslDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetSdslDataCommand.class);

    private RegularExpressionService regularExpressionService;
    private EndstellenService endstellenService;
    private RangierungsService rangierungsService;
    private HWService hwService;
    private HVTService hvtService;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            CPSSdslData sdslData = new CPSSdslData();

            Set<Long> orderIdsOfCpsTx = getOrderIDs4CPSTx();
            if (CollectionTools.isEmpty(orderIdsOfCpsTx)) {
                throw new HurricanServiceCommandException("Es wurden keine Auftraege fuer die Ermittlung der SDSL Ports angegeben!");
            }

            boolean firstOrder = true;
            for (Long orderId : orderIdsOfCpsTx) {
                Equipment sdslEquipment = getSdslEquipment(orderId);
                if (sdslEquipment != null) {
                    HWBaugruppe baugruppe = hwService.findBaugruppe(sdslEquipment.getHwBaugruppenId());

                    if (firstOrder) {
                        // falls erster Auftrag: DSLAM Informationen ermitteln
                        defineDslamData(sdslData, baugruppe, sdslEquipment);
                        firstOrder = false;
                    }

                    // DSLAM Port ermitteln und in sdslData schreiben
                    defineDslamPort(sdslData, baugruppe.getHwBaugruppenTyp(), sdslEquipment);
                }
            }

            if (CollectionTools.isNotEmpty(sdslData.getDslamPorts())) {
                // SDSL-Daten an ServiceOrder-Data nur uebergeben, wenn auch Ports vorhanden sind
                // (speziell bei Produkt 'ConnectDSL SDSL' haeufig keine Ports zugeordnet, da virtuell)
                getServiceOrderData().setSdsl(sdslData);
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading SDSL-Data: " + e.getMessage(), this.getClass());
        }
    }


    /**
     * Ermittelt die DSLAM-Daten zu dem angegebenen Equipment.
     */
    void defineDslamData(CPSSdslData cpsSdslData, HWBaugruppe baugruppe, Equipment sdslEquipment) throws FindException, HurricanServiceCommandException {
        HWRack rack = (baugruppe != null) ? hwService.findRackById(baugruppe.getRackId()) : null;
        HVTTechnik manufacturer = (rack != null) ? hvtService.findHVTTechnik(rack.getHwProducer()) : null;

        if ((rack == null) || (manufacturer == null)) {
            throw new HurricanServiceCommandException("Es konnten nicht alle DSLAM-Informationen ermittelt werden!");
        }

        cpsSdslData.setDslamManufacturer(manufacturer.getCpsName());
        cpsSdslData.setDslamName(rack.getGeraeteBez());
        cpsSdslData.setDslamPortType(
                (sdslEquipment.getSchicht2Protokoll() != null)
                        ? sdslEquipment.getSchicht2Protokoll().name() : Schicht2Protokoll.ATM.name()
        );
    }


    /**
     * Ermittelt abhaengig vom Baugruppen-Typ die notwendige DSLAM-Port Information. Ueber den BG-Typ wird eine
     * bestimmte Regular-Expression ermittelt. Der mit der RegExp matchende Part von HW_EQN wird durch einen Leerstring
     * ersetzt. Der restliche String ist die zu verwendende DSLAM Port-Information.
     *
     * @param cpsSdslData
     * @param bgTyp         Baugruppen-Typ
     * @param sdslEquipment Equipment Datensatz vom DSLAM-Port
     * @return DSLAM-Port Information abhaengig vom Baugruppentyp
     */
    void defineDslamPort(CPSSdslData cpsSdslData, HWBaugruppenTyp bgTyp, Equipment sdslEquipment) throws HurricanServiceCommandException {
        try {
            if ((bgTyp != null) && (sdslEquipment != null)) {
                String hwEqn = regularExpressionService.match(bgTyp.getHwTypeName(),
                        HWBaugruppenTyp.class, CfgRegularExpression.Info.CPS_DSLAM_PORT, sdslEquipment.getHwEQN());

                cpsSdslData.addCPSSdslDslamPortData(new CPSSdslDslamPortData(hwEqn));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error evaluating DSLAM_PORT: " + e.getMessage());
        }
    }


    /**
     * Ermittelt das SDSL Equipment zu dem angegebenen Auftrag.
     *
     * @return Instanz von {@link Equipment} oder {@code null}, falls der Auftrag keine Rangierung bzw. eine Rangierung
     * ohne EQ_IN Port besitzt.
     */
    Equipment getSdslEquipment(Long orderId) throws HurricanServiceCommandException {
        try {
            Endstelle esB = endstellenService.findEndstelle4Auftrag(orderId, Endstelle.ENDSTELLEN_TYP_B);
            Rangierung dslRang = ((esB != null) && (esB.getRangierId() != null))
                    ? rangierungsService.findRangierungTx(esB.getRangierId()) : null;

            if ((dslRang != null) && (dslRang.getEqInId() != null)) {
                Equipment sdslEquipment = rangierungsService.findEquipment(dslRang.getEqInId());
                return sdslEquipment;
            }

            return null;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung des SDSL Equipments: " + e.getMessage(), e);
        }
    }

    /**
     * Injected by Spring
     */
    public void setRegularExpressionService(RegularExpressionService regularExpressionService) {
        this.regularExpressionService = regularExpressionService;
    }

    /**
     * Injected by Spring
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected by Spring
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected by Spring
     */
    @Override
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected by Spring
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

}
