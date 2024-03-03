/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.2015
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;

/**
 *
 */
public class CcHelper {

    public static CcStrategyType.NlType getNlType(Long niederlassungId) {
        if (Niederlassung.ID_MUENCHEN.equals(niederlassungId) ||
                Niederlassung.ID_LANDSHUT.equals(niederlassungId)) {
            return CcStrategyType.NlType.MUC;
        }
        else if (Niederlassung.ID_AUGSBURG.equals(niederlassungId)
                || Niederlassung.ID_KEMPTEN.equals(niederlassungId)) {
            return CcStrategyType.NlType.AGB;
        }
        else if (Niederlassung.ID_NUERNBERG.equals(niederlassungId)) {
            return CcStrategyType.NlType.NBG;
        }
        else if (Niederlassung.ID_MAIN_KINZIG.equals(niederlassungId)) {
            return CcStrategyType.NlType.MKK;
        }
        else {
            throw new RuntimeException("Could not determine Niederlassung for Niederlassung ID: "
                    + niederlassungId);
        }
    }

    public static CcStrategyType.DslType getDslType(Equipment port) {
        if (Equipment.HW_SCHNITTSTELLE_SDSL_OUT.equals(port.getHwSchnittstelle())) {
            return CcStrategyType.DslType.SDSL;
        }
        else if (Equipment.HW_SCHNITTSTELLE_ADSL_OUT.equals(port.getHwSchnittstelle())) {
            return CcStrategyType.DslType.ADSL;
        }
        else if (Equipment.HW_SCHNITTSTELLE_VDSL2.equals(port.getHwSchnittstelle())) {
            return CcStrategyType.DslType.VDSL2;
        }
        else {
            throw new RuntimeException("Could not determine DSL Type: " + port.getHwSchnittstelle());
        }
    }

    public static CcStrategyType.TechType getTechType(HWDslam dslam) {
        if (HVTTechnik.ALCATEL.equals(dslam.getHwProducer())) {
            return CcStrategyType.TechType.ALC;
        }
        else if (HVTTechnik.HUAWEI.equals(dslam.getHwProducer())) {
            return CcStrategyType.TechType.HUA;
        }
        else if (HVTTechnik.SIEMENS.equals(dslam.getHwProducer())) {
            return CcStrategyType.TechType.SIE;
        }
        else {
            throw new RuntimeException("Could not determine Tech Type for HW Producer ID: " + dslam.getHwProducer());
        }
    }

    public static CcStrategyType.HwType getHwType(HWDslam dslam) {
        if (HWDslam.ATM.equals(dslam.getPhysikArt())) {
            return CcStrategyType.HwType.ATM;
        }
        else if (HWDslam.ETHERNET.equals(dslam.getPhysikArt())) {
            return CcStrategyType.HwType.IP;
        }
        else {
            throw new RuntimeException("Could not determine Hardware Type: " + dslam.getPhysikArt());
        }
    }

}
