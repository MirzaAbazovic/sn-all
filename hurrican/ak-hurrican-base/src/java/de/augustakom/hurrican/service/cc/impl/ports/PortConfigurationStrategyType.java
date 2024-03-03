/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 11:11:22
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * Klasse definiert Strategie-Typen fuer die Konfiguration von Ports.
 *
 *
 */
public class PortConfigurationStrategyType {

    public enum HwSchnittstelleType {
        AB, UK0, MVO, V52, ADSL, SDSL, SDH, PDH, VDSL2, GFAST;
    }

    public enum HwEqnPatternType {
        EWSD_DLU, XDSL_AGB, XDSL_MUC, SDH, PDH;
    }

    private HwSchnittstelleType hwSchnittstelleType;
    private Boolean createPortsForCombiPhysic;
    private Boolean createSecondPort;


    public PortConfigurationStrategyType(HwSchnittstelleType hwSchnittstelleType, Boolean createPortsForCombiPhysic, Boolean createSecondPort) {
        this.hwSchnittstelleType = hwSchnittstelleType;
        this.createPortsForCombiPhysic = createPortsForCombiPhysic;
        this.createSecondPort = createSecondPort;
    }


    static HwSchnittstelleType getHwSchnittstelleType(String hwSchnittstelle) {
        if (HWBaugruppenTyp.HW_SCHNITTSTELLE_ADSL.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.ADSL;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_SDSL.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.SDSL;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_AB.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.AB;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_UK0.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.UK0;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_V52.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.V52;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_MVO.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.MVO;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_SDH.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.SDH;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_PDH.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.PDH;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_VDSL2.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.VDSL2;
        }
        else if (HWBaugruppenTyp.HW_SCHNITTSTELLE_GFAST.equals(hwSchnittstelle)) {
            return HwSchnittstelleType.GFAST;
        }
        return null;
    }

    static HwEqnPatternType getHwEqnPatternType(String hwTypeName) {
        if ("EWSD_DLU".equals(hwTypeName)) {
            return HwEqnPatternType.EWSD_DLU;
        }
        else if ("XDSL_AGB".equals(hwTypeName)) {
            return HwEqnPatternType.XDSL_AGB;
        }
        else if ("XDSL_MUC".equals(hwTypeName)) {
            return HwEqnPatternType.XDSL_MUC;
        }
        else if ("SDH".equals(hwTypeName)) {
            return HwEqnPatternType.SDH;
        }
        else if ("PDH".equals(hwTypeName)) {
            return HwEqnPatternType.PDH;
        }
        return null;
    }


    /**
     * Vergleicht das angegebene PortConfigurationStrategyType Objekt mit dem aktuellen Modell.
     *
     * @param o
     * @return true, wenn die Objekte identische Werte besitzen
     */
    public boolean compare(PortConfigurationStrategyType o) {
        boolean match = true;
        if (((o.hwSchnittstelleType != null) && !o.hwSchnittstelleType.equals(this.hwSchnittstelleType))
                || ((o.hwSchnittstelleType == null) && (this.hwSchnittstelleType != null))) {
            match = false;
        }

        if (((o.createPortsForCombiPhysic != null) && !o.createPortsForCombiPhysic.equals(this.createPortsForCombiPhysic))
                || ((o.createPortsForCombiPhysic == null) && (this.createPortsForCombiPhysic != null))) {
            match = false;
        }

        if (((o.createSecondPort != null) && !o.createSecondPort.equals(this.createSecondPort))
                || ((o.createSecondPort == null) && (this.createSecondPort != null))) {
            match = false;
        }

        return match;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PortConfigurationStrategyType [");
        if (hwSchnittstelleType != null) {
            builder.append("hwSchnittstelleType=");
            builder.append(hwSchnittstelleType);
            builder.append(", ");
        }
        if (createPortsForCombiPhysic != null) {
            builder.append("createPortsForCombiPhysic=");
            builder.append(createPortsForCombiPhysic);
            builder.append(", ");
        }
        if (createSecondPort != null) {
            builder.append("createSecondPort=");
            builder.append(createSecondPort);
        }
        builder.append("]");
        return builder.toString();
    }
}
