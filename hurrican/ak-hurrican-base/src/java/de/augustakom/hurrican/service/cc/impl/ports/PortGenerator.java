/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 10:49:59
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.impl.ports.PortConfigurationStrategyType.HwEqnPatternType;
import de.augustakom.hurrican.service.cc.impl.ports.PortConfigurationStrategyType.HwSchnittstelleType;


/**
 * Klasse, um ein Equipment-Objekt zu generieren. Die Klasse ermittelt gewisse Default-Werte an Hand der angegebenen
 * Baugruppe. Weitere Spezial-Werte werden ueber eine passende PortConfiguratorStrategy gesetzt.
 *
 *
 */
public class PortGenerator {

    private static final Logger LOGGER = Logger.getLogger(PortGenerator.class);

    private PortGeneratorDetails portGeneratorDetails;
    private HWService hwService;
    private HWSwitchService hwSwitchService;
    private AKUser user;

    public void configurePortGenerator(PortGeneratorDetails portGeneratorDetails, HWService hwService, HWSwitchService hwSwitchService, AKUser user) {
        this.portGeneratorDetails = portGeneratorDetails;
        this.hwService = hwService;
        this.hwSwitchService = hwSwitchService;
        this.user = user;
    }

    /**
     * Generiert fuer die angegebenen Import-Werte den bzw. die notwendigen Ports. (Mehr als ein Port wird generiert,
     * wenn in den PortGeneratorDetails das Flag createPortsForCombiPhysic auf TRUE gesetzt ist.)
     *
     * @param portGeneratorImport
     * @return
     */
    public List<Equipment> generatePort(PortGeneratorImport portGeneratorImport) {
        try {
            List<Equipment> result = new ArrayList<>();

            HWBaugruppe hwBaugruppe = hwService.findBaugruppe(portGeneratorDetails.getHwBaugruppenId());
            HWRack rack = hwService.findRackById(hwBaugruppe.getRackId());
            HWBaugruppenTyp baugruppenTyp = hwBaugruppe.getHwBaugruppenTyp();

            Equipment equipment = new Equipment();
            result.add(equipment);

            // Default-Werte ueber die Baugruppe ermitteln
            equipment.setStatus(EqStatus.frei);
            equipment.setHwBaugruppenId(portGeneratorDetails.getHwBaugruppenId());
            equipment.setHvtIdStandort(rack.getHvtIdStandort());
            if (rack instanceof HWDlu) {
                equipment.setHwSwitch(((HWDlu) rack).getHwSwitch());
            }
            else {
                equipment.setHwSwitch(hwSwitchService.findSwitchByName(portGeneratorImport.getSwitchName()));
            }

            equipment.setHwEQN(portGeneratorImport.getHwEqn());
            equipment.setV5Port(portGeneratorImport.getV5Port());
            equipment.setRangVerteiler(portGeneratorImport.getRangVerteilerIn());
            equipment.setRangBucht(portGeneratorImport.getRangBuchtIn());
            equipment.setRangLeiste1(portGeneratorImport.getRangLeiste1In());
            equipment.setRangStift1(portGeneratorImport.getRangStift1In());
            equipment.setRangLeiste2(portGeneratorImport.getRangLeiste2In());
            equipment.setRangStift2(portGeneratorImport.getRangStift2In());
            equipment.setUserW((user != null) ? user.getLoginName() : HurricanConstants.UNKNOWN);
            definePortValuesByStrategy(equipment, baugruppenTyp, Boolean.FALSE);
            equipment.setSchicht2Protokoll(baugruppenTyp.getDefaultSchicht2Protokoll());

            HwSchnittstelleType hwSchnittstelleType = PortConfigurationStrategyType.getHwSchnittstelleType(baugruppenTyp.getHwSchnittstelleName());
            if (BooleanTools.nullToFalse(portGeneratorDetails.getCreatePortsForCombiPhysic()) && HwSchnittstelleType.ADSL.equals(hwSchnittstelleType)) {
                Equipment equipmentOut = new Equipment();
                result.add(equipmentOut);

                PropertyUtils.copyProperties(equipmentOut, equipment);
                equipmentOut.setRangVerteiler(portGeneratorImport.getRangVerteilerOut());
                equipmentOut.setRangBucht(portGeneratorImport.getRangBuchtOut());
                equipmentOut.setRangLeiste1(portGeneratorImport.getRangLeiste1Out());
                equipmentOut.setRangStift1(portGeneratorImport.getRangStift1Out());
                equipmentOut.setRangLeiste2(portGeneratorImport.getRangLeiste2Out());
                equipmentOut.setRangStift2(portGeneratorImport.getRangStift2Out());
                definePortValuesByStrategy(equipmentOut, baugruppenTyp, Boolean.TRUE);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Ermittelt die notwendige PortConfigurationStrategy und uebergibt die darin ermittelten Werte dem
     * Equipment-Modell.
     */
    private void definePortValuesByStrategy(Equipment equipment, HWBaugruppenTyp baugruppenTyp, Boolean createSecondPort) {
        HwSchnittstelleType hwSchnittstelleType = PortConfigurationStrategyType.getHwSchnittstelleType(baugruppenTyp.getHwSchnittstelleName());
        HwEqnPatternType hwEqnPatternType = PortConfigurationStrategyType.getHwEqnPatternType(baugruppenTyp.getHwTypeName());
        PortConfigurationStrategyType strategyType =
                new PortConfigurationStrategyType(hwSchnittstelleType, portGeneratorDetails.getCreatePortsForCombiPhysic(), createSecondPort);

        PortConfigurationStrategy strategy = PortConfigurationStrategy.get(strategyType);
        if (strategy != null) {
            equipment.setHwSchnittstelle(strategy.getHwSchnittstelle());
            equipment.setRangSSType(strategy.getRangSsType());

            // Format von HW_EQN pruefen
            String hwEqnPattern = strategy.getHwEqnPattern(hwEqnPatternType);
            if (StringUtils.isNotBlank(hwEqnPattern)) {
                if (!equipment.getHwEQN().matches(hwEqnPattern)) {
                    throw new RuntimeException(
                            "Defined HW_EQN does not match the given pattern! HW_EQN: " + equipment.getHwEQN() + "; Pattern: " + hwEqnPattern);
                }
            }
            else {
                throw new RuntimeException("Pattern for HW_EQN not found for strategy type " + strategyType.toString());
            }
        }
        else {
            throw new RuntimeException("PortConfigurationStrategy not found for type " + strategyType.toString());
        }
    }

}
