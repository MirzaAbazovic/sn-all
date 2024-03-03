/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 14:47:41
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Klasse prueft, ob auf jedem Port zumindest die notwendigen Daten angegeben sind. Die notwendigen Daten sind: <ul>
 * <li>HW_EQN <li>V5_PORT, falls die zugehoerige Baugruppe eine DLU mit MediaGateway-Information ist <li>SWITCH - Der
 * zugehoerige Switch auf den die Ports geschaltet werden. </ul>
 *
 *
 */
public class CheckNecessaryPortData extends AbstractPortConfigurationCheck {

    private static final Logger LOGGER = Logger.getLogger(CheckNecessaryPortData.class);

    protected HVTService hvtService;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.ports.AbstractPortConfigurationCheck#executeCheck(de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails,
     * java.util.List)
     */
    @Override
    public ServiceCommandResult executeCheck(PortGeneratorDetails portGeneratorDetails,
            List<PortGeneratorImport> portsToGenerate) {

        HWRack rack;
        HVTStandort hvtStandort;
        try {
            rack = findRackForBaugruppe(portGeneratorDetails);
            hvtStandort = findHVTStandort4HWRack(rack);
            if (hvtStandort == null) {
                throw new FindException("Der HVT Standort konnte nicht ermittelt werden!");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error while checking for necessary data: " + e.getMessage(), getClass());
        }

        HWDlu dlu = getHWDlu(rack);
        boolean checkV5Port = hasMediaGatewayName(dlu);
        boolean checkSwitch4DLU = hasSwitchId(dlu);
        boolean checkSwitch4FttCKVZ = isStandortFttCKVZ(hvtStandort);

        for (PortGeneratorImport portToGenerate : portsToGenerate) {
            if (StringUtils.isBlank(portToGenerate.getHwEqn())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "HW_EQN von min. einem Port ist nicht definiert!", getClass());
            }

            if (checkV5Port && StringUtils.isBlank(portToGenerate.getV5Port())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "V5-Port von min. einem Port ist nicht definiert!", getClass());
            }

            if (checkSwitch4DLU) {
                final String portSwitchName = portToGenerate.getSwitchName();
                final HWSwitch dluSwitch = dlu.getHwSwitch();
                if (!dluSwitch.getName().equalsIgnoreCase(portSwitchName)) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID, String
                            .format("Switchkennung Port (%s) weicht von Switchkennung der DLU (%s) ab!",
                                    portSwitchName, dluSwitch), getClass());
                }
                else {
                    portToGenerate.setSwitchName(dluSwitch.getName());
                }
            }
            else if (checkSwitch4FttCKVZ && StringUtils.isBlank(portToGenerate.getSwitchName())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID, String
                        .format("Da der Port (%s) in einen FttC_KVZ Standort eingespielt werden soll, muss eine Switchkennung"
                                        + " immer angegeben sein! Bitte prüfen, dass alle Ports eine KORREKTE Switchkennung erhalten!",
                                portToGenerate.getHwEqn()
                        ), getClass());
            }
        }
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    private boolean isStandortFttCKVZ(HVTStandort hvtStandort) {
        boolean result = false;
        if ((hvtStandort != null)
                && NumberTools.equal(hvtStandort.getStandortTypRefId(), HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
            result = true;
        }
        return result;
    }

    /**
     * provides an instance of {@link HWDlu} casted from the given {@link HWRack}. If the given {@link HWRack} is not an
     * instance of {@link HWDlu} a null value will be returned.
     *
     * @param rack
     * @return A casted instance of {@link HWDlu} from the given parameter rack if it is from type {@link HWDlu}.
     */
    private HWDlu getHWDlu(HWRack rack) {
        HWDlu result = null;
        if (rack instanceof HWDlu) {
            result = (HWDlu) rack;
        }
        return result;
    }

    private boolean hasSwitchId(HWDlu dlu) {
        boolean result = false;
        if ((dlu != null) && dlu.getHwSwitch() != null) {
            result = true;
        }
        return result;
    }

    private boolean hasMediaGatewayName(HWDlu dlu) {
        boolean result = false;
        if ((dlu != null) && StringUtils.isNotBlank(dlu.getMediaGatewayName())) {
            result = true;
        }
        return result;
    }

    private HWRack findRackForBaugruppe(PortGeneratorDetails portGeneratorDetails) throws FindException {
        return hwService.findRackForBaugruppe(portGeneratorDetails.getHwBaugruppenId());
    }

    private HVTStandort findHVTStandort4HWRack(HWRack rack) throws FindException {
        if (rack == null) {return null;}
        return hvtService.findHVTStandort(rack.getHvtIdStandort());

    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }
}
