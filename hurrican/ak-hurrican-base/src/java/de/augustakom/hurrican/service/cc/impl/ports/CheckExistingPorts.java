/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 14:46:25
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Klasse prueft, ob fuer die angegebene Baugruppe bereits einer der anzulegenden Ports existiert.
 */
public class CheckExistingPorts extends AbstractPortConfigurationCheck {

    private static final Logger LOGGER = Logger.getLogger(CheckExistingPorts.class);

    private RangierungsService rangierungsService;

    @Override
    public ServiceCommandResult executeCheck(PortGeneratorDetails portGeneratorDetails, List<PortGeneratorImport> portsToGenerate) {
        try {
            List<Equipment> existingEquipments = (List<Equipment>) CollectionTools.select(
                    rangierungsService.findEquipments4HWBaugruppe(portGeneratorDetails.getHwBaugruppenId()),
                    object -> !object.getRangSSType().equalsIgnoreCase(Equipment.RANG_SS_ADSL_IN)
            );
            HWBaugruppenTyp hwBaugruppenTyp = getHwBaugruppenTyp(portGeneratorDetails.getHwBaugruppenId());

            final int existingEqCount = existingEquipments.size();
            final Integer hwBgPortCount = hwBaugruppenTyp.getPortCount();
            final int portsToGenCount = portsToGenerate.size();

            if (existingEqCount == hwBgPortCount) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Baugruppe ist bereits voll mit Ports bestückt.", getClass());
            }
            else if (existingEqCount > hwBgPortCount) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        String.format("Die Baugruppe ist mit mehr Ports (%d) bestückt als möglich (%d)."
                                , existingEqCount, hwBgPortCount), getClass()
                );
            }

            int alreadyExistingPorts = markPortsAsAlreadyExisting(existingEquipments, portsToGenerate);
            if (existingEqCount + portsToGenCount - alreadyExistingPorts > hwBgPortCount) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        String.format("Überschreitung Maximalzahl möglicher Ports (%d) für diese Baugruppe durch Anlage von weiteren %d" +
                                ". Bereits vorhandene Ports von den anzulegenden: %d/%d."
                                , hwBgPortCount, portsToGenCount - alreadyExistingPorts
                                , alreadyExistingPorts, portsToGenCount), getClass()
                );
            }
            else if (existingEqCount + portsToGenCount - alreadyExistingPorts < hwBgPortCount) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        String.format("%d Ports sind anzulegen, um Baugruppe mit %d max. Ports komplett aufzufüllen" +
                                ". Bereits vorhandene Ports von den anzulegenden: %d/%d."
                                , hwBgPortCount - existingEqCount, hwBgPortCount
                                , alreadyExistingPorts, portsToGenCount), getClass()
                );
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error while checking for existing ports: " + e.getMessage(), getClass());
        }
    }

    /**
     * Markiere alle Ports, die bereits in der Datenbank existieren
     *
     * @return die Anzahl der Ports, die als bereits vorhanden markiert wurden
     */
    private int markPortsAsAlreadyExisting(List<Equipment> existingEquipments, List<PortGeneratorImport> portsToGenerate) {
        int alreadyExistingPorts = 0;
        for (Equipment equipment : existingEquipments) {
            for (PortGeneratorImport portToGenerate : portsToGenerate) {
                if (equipment.getHwEQN().equals(portToGenerate.getHwEqn())) {
                    portToGenerate.setPortAlreadyExists(true);
                    alreadyExistingPorts++;
                }
            }
        }
        return alreadyExistingPorts;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }
}
