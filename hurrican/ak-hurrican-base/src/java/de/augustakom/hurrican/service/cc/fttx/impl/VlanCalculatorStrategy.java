/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2012 11:39:43
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp.CVlanType;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

/**
 * Einfache Strategy Implementierung für die hardwareabhängige VLAN Berechnung.
 *
 *
 */
abstract class VlanCalculatorStrategy {

    private static List<Pair<Long, VlanCalculatorStrategy>> strategies = new ArrayList<>();

    static {
        strategies.add(new Pair<>(HVTTechnik.ALCATEL, new VlanCalculatorAlcatel()));
        // Huawei als default calculator
        strategies.add(new Pair<>(null, new VlanCalculatorHuawei()));
    }

    /**
     * Findet einen passenden Calculator für eine Hardware.
     *
     * @param hwRack HWRack
     * @return den Calculator, niemals <code>null</code>
     * @throws IllegalStateException when kein passender Calculator ermittelt werden konnte (sollte nie passieren)
     */
    public static VlanCalculatorStrategy get(final HWDslam hwRack) {
        if (HWRack.RACK_TYPE_DSLAM.equals(hwRack.getRackTyp()) && hwRack.isHwOfProducer(HVTTechnik.ALCATEL)) {
            return new VlanCalculatorAlcatelGslam(hwRack);
        }
        throw new IllegalStateException(
                "Fehler in VlanCalculatorStrategy Konfiguration oder falsche Anfrageparameter hvtTechnikId="
                        + hwRack.getHwProducer()
        );
    }

    public static VlanCalculatorStrategy get(final HWOlt hwRack) {
        VlanCalculatorStrategy matchingStrategy = null;
        for (Pair<Long, VlanCalculatorStrategy> strategyPair : strategies) {
            if (hwRack.getHwProducer().equals(strategyPair.getFirst())) {
                matchingStrategy = strategyPair.getSecond();
            }
            if ((strategyPair.getFirst() == null) && (matchingStrategy == null)) {
                matchingStrategy = strategyPair.getSecond();
            }
        }
        if (matchingStrategy == null) {
            throw new IllegalStateException(
                    "Fehler in VlanCalculatorStrategy Konfiguration oder falsche Anfrageparameter hvtTechnikId="
                            + hwRack.getHwProducer()
            );
        }
        return matchingStrategy;
    }

    /**
     * Berechnet den Vlan Wert für die Strecke A10NSP - M-net Switch zum Backbone.
     *
     * @param a10NspNummer die A10Nsp-Nummer für die Strecke (ermittelt aus Ekp und Olt)
     * @param oltNr        die Olt-Nr für die Strecke
     * @param oltSlot      der Port auf der OLT (der MDU bei FTTB oder aus HWEqn des Equipments bei FTTH)
     * @param cvlan        das CVLAN des Ekp für die Strecke/ServiceTyp
     * @param svlanFaktor        Faktor für die Berechnung des SVLAN EKP
     * @return das berechnete SVLAN EKP
     */
    public abstract Integer calculateSvlanEkp(int a10NspNummer, Integer oltNr, Integer oltSlot, CVlan cvlan, int svlanFaktor);

    /**
     * Berechnet den Vlan Wert für die Strecke M-net Switch - OLT.
     *
     * @param a10NspNummer die A10Nsp-Nummer für die Strecke (ermittelt aus Ekp und Olt)
     * @param oltNr        die Olt-Nr für die Strecke
     * @param oltSlot      der Port auf der OLT (der MDU bei FTTB oder aus HWEqn des Equipments bei FTTH)
     * @param cvlan        das CVLAN des Ekp für die Strecke/ServiceTyp
     * @return das berechnete SVLAN OLT
     */
    public abstract Integer calculateSvlanOlt(int a10NspNummer, Integer oltNr, Integer oltSlot, Integer oltOffset,
            CVlan cvlan);

    /**
     * Berechnet den Vlan Wert für die Strecke OLT - MDU.
     *
     * @param a10NspNummer die A10Nsp-Nummer für die Strecke (ermittelt aus Ekp und Olt)
     * @param oltNr        die Olt-Nr für die Strecke
     * @param oltSlot      der Port auf der OLT (der MDU bei FTTB oder aus HWEqn des Equipments bei FTTH)
     * @param cvlan        das CVLAN des Ekp für die Strecke/ServiceTyp
     * @return das berechnete SVLAN MDU
     */
    public abstract Integer calculateSvlanMdu(int a10NspNummer, Integer oltNr, Integer oltSlot, CVlan cvlan);

    /**
     * Calculator für Huawei Hardware.
     *
     *
     */
    private static class VlanCalculatorHuawei extends VlanCalculatorStrategy {

        /**
         * #OLT * svlanFaktor + #Slot auf OLT (bei Unicast) bzw. #OLT * svlanFaktor (bei Multicast).
         *
         * @see VlanCalculatorStrategy#calculateSvlanEkp(int, Integer, Integer, CVlan, int)
         */
        @Override
        public Integer calculateSvlanEkp(int a10NspNummer, Integer oltNr, Integer oltSlot, CVlan cvlan, int svlanFaktor) {
            Integer svlan = oltNr * svlanFaktor;
            if (CVlanType.UNICAST.equals(cvlan.getTyp().getType())) {
                svlan += oltSlot;
            }
            return svlan;
        }

        /**
         * #A10NSP * 50 + #Slot auf OLT (bei Unicast) bzw. #A10NSP * 50 (bei Multicast).
         *
         * @see VlanCalculatorStrategy#calculateSvlanOlt(int, Integer, Integer, Integer, CVlan)
         */
        @Override
        public Integer calculateSvlanOlt(int a10NspNummer, Integer oltNr, Integer oltSlot, Integer oltOffset,
                CVlan cvlan) {
            Integer svlan = a10NspNummer * 50;
            if (CVlanType.UNICAST.equals(cvlan.getTyp().getType())) {
                svlan += oltSlot;
            }
            return svlan;
        }

        /**
         * #A10NSP * 50 + 49.
         *
         * @see VlanCalculatorStrategy#calculateSvlanMdu(int, Integer, Integer, CVlan)
         */
        @Override
        public Integer calculateSvlanMdu(int a10NspNummer, Integer oltNr, Integer oltSlot, CVlan cvlan) {
            return (a10NspNummer * 50) + 49;
        }

    }

    /**
     * Calculator für Alcatel.
     *
     *
     */
    private static class VlanCalculatorAlcatel extends VlanCalculatorHuawei {
        /**
         * Für {@link CVlanProtocoll#IPoE} 2000 zum Huawei Wert addieren, ansonsten Wert von Huawei.
         *
         * @see VlanCalculatorHuawei#calculateSvlanOlt(int, Integer, Integer, Integer, CVlan)
         */
        @Override
        public Integer calculateSvlanOlt(int a10NspNummer, Integer oltNr, Integer oltSlot, Integer oltOffset,
                CVlan cvlan) {
            Integer svlanOlt = super.calculateSvlanOlt(a10NspNummer, oltNr, oltSlot, oltOffset, cvlan);
            if (CVlanProtocoll.IPoE.equals(cvlan.getProtocoll())) {
                svlanOlt = svlanOlt + ((oltOffset != null) ? oltOffset : 0);
            }
            return svlanOlt;
        }
    }

    private static class VlanCalculatorAlcatelGslam extends VlanCalculatorAlcatel {

        private final HWDslam hwDslam;

        private VlanCalculatorAlcatelGslam(final @Nonnull HWDslam hwDslam) {
            this.hwDslam = hwDslam;
        }

        @Override
        public Integer calculateSvlanOlt(int a10NspNummer, Integer oltNr, Integer oltSlot, Integer oltOffset,
                CVlan cvlan) {
            return this.hwDslam.getSvlan();
        }

        @Override
        public Integer calculateSvlanEkp(int a10NspNummer, Integer oltNr, Integer oltSlot, CVlan cvlan, int svlanFaktor) {
            return null;
        }
    }
}
