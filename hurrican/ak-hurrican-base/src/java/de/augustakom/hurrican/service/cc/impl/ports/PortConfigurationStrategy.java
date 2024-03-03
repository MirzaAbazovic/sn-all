/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 11:06:30
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.cc.impl.ports.PortConfigurationStrategyType.HwEqnPatternType;
import de.augustakom.hurrican.service.cc.impl.ports.PortConfigurationStrategyType.HwSchnittstelleType;


/**
 * Klasse fuer die Definition von verschiedenen Port-Konfigurations Strategien. <br> Die einzelnen Strategien sind ueber
 * verschiedene private Implementierungen innerhalb dieser Klasse realisiert. Die notwendige Strategie kann ueber die
 * Methode {@link #get(PortConfigurationStrategyType) } ermittelt werden.
 *
 *
 */
public abstract class PortConfigurationStrategy {
    private static final Logger LOGGER = Logger.getLogger(PortConfigurationStrategy.class);

    public abstract String getHwSchnittstelle();

    public abstract String getRangSsType();

    private static List<Pair<PortConfigurationStrategyType, PortConfigurationStrategy>> strategies =
            new ArrayList<Pair<PortConfigurationStrategyType, PortConfigurationStrategy>>();

    static {
        add(new PortConfigurationStrategyType(HwSchnittstelleType.AB, Boolean.FALSE, Boolean.FALSE), new PhoneABStandalone());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.UK0, Boolean.FALSE, Boolean.FALSE), new PhoneUK0Standalone());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.AB, Boolean.TRUE, Boolean.FALSE), new PhoneABCombi());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.UK0, Boolean.TRUE, Boolean.FALSE), new PhoneUK0Combi());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.ADSL, Boolean.TRUE, Boolean.FALSE), new AdslFirst());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.ADSL, Boolean.FALSE, Boolean.FALSE), new AdslFirst());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.ADSL, Boolean.TRUE, Boolean.TRUE), new AdslSecond());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.SDSL, Boolean.FALSE, Boolean.FALSE), new Sdsl());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.MVO, Boolean.FALSE, Boolean.FALSE), new Mvo());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.V52, Boolean.FALSE, Boolean.FALSE), new V52());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.SDH, Boolean.FALSE, Boolean.FALSE), new SDH());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.PDH, Boolean.FALSE, Boolean.FALSE), new PDH());
        add(new PortConfigurationStrategyType(HwSchnittstelleType.VDSL2, Boolean.FALSE, Boolean.FALSE), new VDSL());
    }

    private static void add(PortConfigurationStrategyType strategyType, PortConfigurationStrategy strategy) {
        strategies.add(Pair.create(strategyType, strategy));
    }

    public static PortConfigurationStrategy get(PortConfigurationStrategyType portConfigurationStrategyType) {
        for (Pair<PortConfigurationStrategyType, PortConfigurationStrategy> strategy : strategies) {
            if (portConfigurationStrategyType.compare(strategy.getFirst())) {
                return strategy.getSecond();
            }
        }
        LOGGER.warn("get() - no port configuration strategy found for type: " + portConfigurationStrategyType.toString());
        return null;
    }


    /**
     * Gibt ein RegEx Pattern zurueck, das den Aufbau der HW_EQN fuer den entsprechenden Baugruppen-Typ definiert.
     *
     * @param hwEqnPatternType
     * @return
     */
    public String getHwEqnPattern(HwEqnPatternType hwEqnPatternType) {
        if (HwEqnPatternType.EWSD_DLU.equals(hwEqnPatternType)) {
            return Equipment.HW_EQN_PATTERN_EWSD_DLU;
        }
        else if (HwEqnPatternType.XDSL_AGB.equals(hwEqnPatternType)) {
            return Equipment.HW_EQN_PATTERN_XDSL_AGB;
        }
        else if (HwEqnPatternType.XDSL_MUC.equals(hwEqnPatternType)) {
            return Equipment.HW_EQN_PATTERN_XDSL_MUC;
        }
        else if (HwEqnPatternType.SDH.equals(hwEqnPatternType)) {
            return Equipment.HW_EQN_PATTERN_SDH_PDH;
        }
        else if (HwEqnPatternType.PDH.equals(hwEqnPatternType)) {
            return Equipment.HW_EQN_PATTERN_SDH_PDH;
        }
        return null;
    }


    /* ======================================================= *
     * Strategien                                              *
     * ======================================================= */

    /* Strategy-Klasse, um die Werte fuer reine analoge Telephonie-Ports zu definieren */
    private static class PhoneABStandalone extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_AB;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_AB;
        }
    }

    /* Strategy-Klasse, um die Werte fuer reine ISDN Telephonie-Ports zu definieren */
    private static class PhoneUK0Standalone extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_UK0;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_UK0;
        }
    }

    /* Strategy-Klasse, um die Werte fuer analoge Telephonie-Ports zu definieren, die mit ADSL-Ports kombiniert werden. */
    private static class PhoneABCombi extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_AB;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_AB;
        }
    }

    /* Strategy-Klasse, um die Werte fuer ISDN Telephonie-Ports zu definieren, die mit ADSL-Ports kombiniert werden. */
    private static class PhoneUK0Combi extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_UK0;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_UK0;
        }
    }

    /* Strategy-Klasse, um die Werte fuer den ersten ADSL-Port zu definieren (verbunden mit Rangierung.EQ_IN) */
    private static class AdslFirst extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_OUT;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_OUT;
        }
    }

    /* Strategy-Klasse, um die Werte fuer den zweiten ADSL-Port zu definieren (verbunden mit Rangierung-additional.EQ_OUT) */
    private static class AdslSecond extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_IN;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_ADSL_IN;
        }
    }

    /* Strategy-Klasse, um die Werte fuer einen SDSL-Port zu definieren */
    private static class Sdsl extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_SDSL_OUT;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_SDSL_OUT;
        }
    }

    // MVO
    /* Strategy-Klasse, um die Werte fuer einen SDSL-Port zu definieren */
    private static class Mvo extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_MVO;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_MVO;
        }
    }

    // V52
    /* Strategy-Klasse, um die Werte fuer einen SDSL-Port zu definieren */
    private static class V52 extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_V52;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_V52;
        }
    }

    // SDH
    /* Strategy-Klasse, um die Werte fuer einen SDH-Port zu definieren */
    private static class SDH extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_SDH;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_SDH;
        }
    }

    // PDH
    /* Strategy-Klasse, um die Werte fuer einen PDH-Port zu definieren */
    private static class PDH extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_PDH_IN;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_PDH_IN;
        }
    }

    // VDSL
    /* Strategy-Klasse, um die Werte fuer einen VDSL-Port zu definieren */
    private static class VDSL extends PortConfigurationStrategy {
        @Override
        public String getHwSchnittstelle() {
            return Equipment.HW_SCHNITTSTELLE_VDSL2;
        }

        @Override
        public String getRangSsType() {
            return Equipment.HW_SCHNITTSTELLE_VDSL2;
        }
    }
}


