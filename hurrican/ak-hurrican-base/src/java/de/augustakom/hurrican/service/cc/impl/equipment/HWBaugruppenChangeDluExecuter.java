/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2010 08:03:03
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Executer-Klasse, um einen DLU-Schwenk (von EWSD auf HiG oder umgekehrt) durchzufuehren.
 * <p/>
 * Ablauf des Executers: <ul> <li> DLU ermitteln <li> DLU-Equipments ermitteln <li> V5 Mappings ermitteln <ul><li>
 * Anzahl Equipments / V5-Mappings zur Sicherheit nochmal validieren</ul> <li> DLU Daten aktualisieren <li>
 * Equipment-Daten aktualisieren (HW_EQN, Switch, V5-Port) </ul>
 */
public class HWBaugruppenChangeDluExecuter extends AbstractHWBaugruppenChangeDluExecuter implements IWarningAware {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeDluExecuter.class);

    private HWBaugruppenChange hwBgChange;
    HWBaugruppenChangeDlu hwBgChangeDlu;
    private HWRack dluToChange;
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    private HWService hwService;
    private RangierungsService rangierungsService;

    List<Equipment> dluEquipments;
    List<HWBaugruppenChangeDluV5> v5Mappings;
    private Map<Long, HWBaugruppenChangeDluV5> v5Map;

    private AKWarnings warnings;

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param hwBgChangeDlu
     * @param hwBaugruppenChangeService
     * @param hwService
     * @param rangierungsService
     * @param fileToImport
     */
    public void configure(HWBaugruppenChange hwBgChange,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            HWService hwService,
            RangierungsService rangierungsService) {
        setHwBgChange(hwBgChange);
        setHwBaugruppenChangeService(hwBaugruppenChangeService);
        setHwService(hwService);
        setRangierungsService(rangierungsService);

        Set<HWBaugruppenChangeDlu> dluSet = this.hwBgChange.getHwBgChangeDlu();
        if ((dluSet == null) || dluSet.isEmpty()) {
            throw new IllegalArgumentException("No DLU change definition found!");
        }
        else if (dluSet.size() > 1) {
            throw new IllegalArgumentException("Count of DLU change definitions invalid!");
        }

        setHwBgChangeDlu(dluSet.iterator().next());
        setDluToChange(hwBgChangeDlu.getDluRackOld());
        if ((dluToChange == null) || !StringUtils.equals(dluToChange.getRackTyp(), HWRack.RACK_TYPE_DLU)) {
            throw new IllegalArgumentException("DLU to change is not defined!");
        }

        initAKWarnings();
    }


    @Override
    public void execute() throws StoreException {
        try {
            dluEquipments = readEquipments4Dlu(hwBgChangeDlu, hwService, rangierungsService);
            if (CollectionTools.isEmpty(dluEquipments)) {
                throw new StoreException("No equipments found for the given DLU!");
            }

            readV5Mappings();
            validateEquipmentsToV5Mappings();
            modifyHwRackDlu();
            modifyEquipments();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Error during DLU change: " + e.getMessage(), e);
        }
    }


    /**
     * Ermittelt die bereits eingelesenen HW_EQN/V5-Port Mappings zu dem aktuellen DLU-Schwenk.
     *
     * @throws FindException
     */
    void readV5Mappings() throws FindException {
        v5Mappings = hwBaugruppenChangeService.findDluV5Mappings(hwBgChangeDlu.getId());
    }


    /**
     * Ueberprueft, ob die Anzahl der geladenen Equipments mit der Anzahl der ermittelten V5-Mappings uebereinstimmt.
     * <br> Der Vergleich wird nur dann durchgefuehrt, wenn der DLU-Schwenk ein MediaGateway definiert hat (also ein
     * Wechsel von EWSD auf HiG statt findet). <br> Es wird lediglich die Anzahl der Ports verglichen, da ein
     * detaillierterer Check bereits beim Import der V5-Mapping statt findet. <br> Ist kein MediaGateway angegebenen
     * (also Rueckfall von HiG auf EWSD) wird erwartet, dass keine V5-Mappings definiert sind.
     *
     * @throws FindException
     */
    void validateEquipmentsToV5Mappings() throws FindException {
        if (StringUtils.isNotBlank(hwBgChangeDlu.getDluMediaGatewayNew())) {
            // EWSD --> HiG: V5-Mappings und DLU-Ports muessen vorhanden sein und gleiche Anzahl besitzen
            if (CollectionTools.isEmpty(v5Mappings)) {
                throw new FindException("No V5 mappings found!");
            }
            else if (CollectionTools.isEmpty(dluEquipments)) {
                throw new FindException("No DLU equipments found!");
            }

            if (NumberTools.notEqual(dluEquipments.size(), v5Mappings.size())) {
                throw new FindException("Count of DLU ports and V5 mappings are different!");
            }
        }
        else if (CollectionTools.isNotEmpty(v5Mappings)) {
            // HiG --> EWSD: V5-Mappings duerfen nicht definiert sein
            throw new FindException("V5 mappings are defined but not expected!");
        }
    }


    /**
     * Aktualisiert die DLU-Daten mit den neuen/angegebenen Werten.
     *
     * @throws ValidationException
     * @throws StoreException
     */
    void modifyHwRackDlu() throws StoreException, ValidationException {
        HWDlu dlu = (HWDlu) dluToChange;
        dlu.setDluNumber(hwBgChangeDlu.getDluNumberNew());
        dlu.setMediaGatewayName(hwBgChangeDlu.getDluMediaGatewayNew());
        dlu.setAccessController(hwBgChangeDlu.getDluAccessControllerNew());
        dlu.setHwSwitch(hwBgChangeDlu.getDluSwitchNew());

        hwService.saveHWRack(dlu);
    }


    /**
     * Durchlaeuft alle DLU-Equipments und aktualisiert die HW_EQN, Switch-ID und V5-Port Information.
     *
     * @throws StoreException
     */
    void modifyEquipments() throws StoreException {
        if (CollectionTools.isNotEmpty(dluEquipments)) {
            for (Equipment equipmentToChange : dluEquipments) {
                HWBaugruppenChangeDluV5 v5 = getV5Mapping(equipmentToChange);
                if (v5 == null) {
                    warnings.addAKWarning(this,
                            String.format("No V5-Port mapping found for equipment (ID: {0}, HW_EQN: {1})",
                                    new Object[] { String.format("%s", equipmentToChange.getId()), equipmentToChange.getHwEQN() })
                    );
                }

                equipmentToChange.setHwSwitch(hwBgChangeDlu.getDluSwitchNew());
                equipmentToChange.setHwEQN(changeDluNumberOfHwEqn(equipmentToChange.getHwEQN(), hwBgChangeDlu.getDluNumberNew()));
                equipmentToChange.setV5Port((v5 != null) ? v5.getV5Port() : null);

                rangierungsService.saveEquipment(equipmentToChange);
            }
        }
    }


    /**
     * Ermittelt das passende HW_EQN/V5-Port Mapping zu dem angegebenen DLU-Port.
     *
     * @param dluEquipment
     * @return
     */
    HWBaugruppenChangeDluV5 getV5Mapping(Equipment dluEquipment) {
        if (v5Map == null) {
            v5Map = new HashMap<>();
            if (CollectionTools.isNotEmpty(v5Mappings)) {
                for (HWBaugruppenChangeDluV5 v5 : v5Mappings) {
                    v5Map.put(v5.getDluEquipment().getId(), v5);
                }
            }
        }

        return v5Map.get(dluEquipment.getId());
    }


    /**
     * Ersetzt die DLU-Nummer in der angegebenen HW_EQN durch den Wert, der ueber {@code dluNumber} angegeben ist. <br>
     * Beispiel: <br> HW_EQN = 0250-00-00-01  <br> DLU-Nummer = 0600       <br> Ergebnis: 0600-00-00-01
     *
     * @param hwEqn
     * @param dluNumber
     * @return
     */
    String changeDluNumberOfHwEqn(String hwEqn, String dluNumber) {
        if (StringUtils.isBlank(hwEqn) || StringUtils.isBlank(dluNumber)) {
            throw new IllegalArgumentException("Given HW_EQN or DLU number is invalid! Shouldn´t be NULL or empty.");
        }

        String[] hwEqnParts = StringUtils.split(hwEqn, Equipment.HW_EQN_SEPARATOR);
        if ((hwEqnParts == null) || (hwEqnParts.length != 4)) {
            throw new IllegalArgumentException("Old HW_EQN definition is not valid: " + hwEqn);
        }

        return new StringBuilder()
                .append(StringUtils.leftPad(dluNumber, 4, "0"))
                .append(Equipment.HW_EQN_SEPARATOR)
                .append(StringUtils.substringAfter(hwEqn, Equipment.HW_EQN_SEPARATOR))
                .toString();
    }

    void initAKWarnings() {
        if (warnings == null) {
            warnings = new AKWarnings();
        }
    }

    public void setHwBaugruppenChangeService(HWBaugruppenChangeService hwBaugruppenChangeService) {
        this.hwBaugruppenChangeService = hwBaugruppenChangeService;
    }

    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    public void setHwBgChange(HWBaugruppenChange hwBgChange) {
        this.hwBgChange = hwBgChange;
    }

    public void setHwBgChangeDlu(HWBaugruppenChangeDlu hwBgChangeDlu) {
        this.hwBgChangeDlu = hwBgChangeDlu;
    }

    public void setDluToChange(HWRack dluToChange) {
        this.dluToChange = dluToChange;
    }

    @Override
    public AKWarnings getWarnings() {
        return warnings;
    }

}
