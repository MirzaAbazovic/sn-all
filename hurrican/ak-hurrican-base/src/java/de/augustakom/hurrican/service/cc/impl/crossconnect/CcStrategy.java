/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.CcType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.DslType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.HwType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.NlType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TechType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TermType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.*;
import static java.lang.Boolean.*;
import static java.util.EnumSet.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Die Klasse CcStrategy hat intern viele private implementierungen. Diese werden in einer Liste gehalten, aus der via
 * {@link #get(CcStrategyType)} die entsprechende Strategie fuer einen bestimmten Strategie-Typen gesucht wird.
 */
public abstract class CcStrategy {

    public abstract Integer getOuter(CcCalculator ccCalculator);

    public abstract Integer getInner(CcCalculator ccCalculator);

    public static final String MA5600T =  "MA5600T";
    public static final String MA5600V3 = "MA5600v3";
    public static final String MA5603T =  "MA5603T";

    private static final int OFFSET_HSI  =  0;
    private static final int OFFSET_VOIP =  2000;

    // @formatter:off
    private static List<Pair<CcStrategyType, CcStrategy>> strategies = new ArrayList<Pair<CcStrategyType,CcStrategy>>();
    static {
        // * Die Nutzung von "null" an einer Stelle ist gleichbedeutend wie "fuer alle Werte".
        // * Ein bestimmter Wert an einer Stelle wird bei der Suche praeferiert gegenueber "null".
        // * Eine Strategy muss in ihren Werten entweder verschieden von allen anderen sein, oder
        //   eine andere Strategy genauer spezifizieren. Dies wird durch einen Unit-Test in
        //   CcStrategyTest ueberprueft.
        //   Bsp.:  (null, Y,    Z)                     (null, Y,    Z)
        //          (X,    Y,    Z)  ist "genauer"      (X,    null, Z)  ist NICHT "genauer"

        // General
        add(create(null,    of(ALC), null, null,          null,    of(BRAS), of(CPE, IAD), null, null), new CcNull()); // IAD und CPE-CCs haben keine BRAS-Werte
        // SDSL strategies
        add(create(null,    of(ALC), null, of(SDSL),      null,    of(LT),   of(HSI),      null, null), new CcFixed(8, 65));
        add(create(null,    of(ALC), null, of(SDSL),      null,    of(LT),   of(CPE, IAD), null, null), new CcFixed(11, 55));
        add(create(of(MUC), of(ALC), null, of(SDSL),      of(ATM), of(NT),   of(HSI),      null, null), new CcSdslAtmNtHsi());
        add(create(of(MUC), of(ALC), null, of(SDSL),      of(ATM), of(NT),   of(CPE, IAD), null, null), new CcAtmNtCpe());
        add(create(of(MUC), of(ALC), null, of(SDSL),      of(ATM), of(BRAS), of(HSI),      null, null), new CcBrasPool(BrasPool.ATM_SDSL_POOL_PREFIX));
        add(create(null,    of(ALC), null, of(SDSL),      of(IP),  of(NT),   of(HSI),      null, null), new CcSdslIpNtHsiMuc());
        add(create(of(NBG), of(ALC), null, of(SDSL),      of(IP),  of(NT),   of(HSI),      null, null), new CcSdslIpNtHsiNbg());
        add(create(null,    of(ALC), null, of(SDSL),      of(IP),  of(NT),   of(CPE, IAD), null, null), new CcSdslIpNtCpe());
        add(create(null,    of(ALC), null, of(SDSL),      of(IP),  of(BRAS), of(HSI),      null, null), new CcSdslIpBrasHsi());
        add(create(null,    of(ALC), null, of(SDSL),      null,    null,     of(VOIP),     null, null), null);
        // ADSL / VDSL strategies
        add(create(null,    of(ALC), null, of(ADSL),      null,    of(LT),   of(HSI),      null, FALSE), new CcFixed(1, 32));
        add(create(null,    of(ALC), null, of(ADSL),      null,    of(LT),   of(CPE),      null, FALSE), new CcFixed(11, 55));
        add(create(null,    of(ALC), null, of(ADSL),      null,    of(LT),   of(IAD),      null, FALSE), new CcFixed(0, 35));
        add(create(of(MUC), of(ALC), null, of(ADSL),      of(ATM), of(NT),   of(HSI),      null, FALSE), new CcAdslAtmNtHsi());
        add(create(of(MUC), of(ALC), null, of(ADSL),      of(ATM), of(NT),   of(CPE, IAD), null, FALSE), new CcAtmNtCpe());
        add(create(of(MUC), of(ALC), null, of(ADSL),      of(ATM), of(BRAS), of(HSI),      null, FALSE), new CcAdslAtmBrasHsi());
        add(create(of(MUC), of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(HSI),      null, FALSE), new CcAdslIpNtHsiMuc());
        add(create(of(NBG), of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(HSI),      null, FALSE), new CcAdslIpNtHsiNbg());

        add(create(null,    of(ALC), null, of(ADSL,VDSL2),of(IP),  of(NT),   of(CPE),      null, null), new CcAdslIpNtCpe());
        add(create(null,    of(ALC), null, of(ADSL,VDSL2),of(IP),  of(NT),   of(IAD),      null, null), new CcAdslIpNtIad());
        add(create(null,    of(ALC), null, of(ADSL,VDSL2),of(IP),  of(BRAS), of(HSI),      null, null), new CcAdslIpBrasHsi());
        add(create(null,    of(ALC), null, of(ADSL),      of(IP),  of(LT),   of(VOIP),     TRUE, FALSE), new CcFixed(7, 77));
        add(create(of(MUC), of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(VOIP),     TRUE, FALSE), new CcAdslIpNtVoipMuc());
        add(create(of(NBG), of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(VOIP),     TRUE, FALSE), new CcAdslIpNtVoipNbg());
        add(create(null,    of(ALC), null, of(ADSL,VDSL2),of(IP),  of(BRAS), of(VOIP),     TRUE, null), new CcAdslIpBrasVoip());

        // ALC / ADSL strategies mit IPv6 (DsLight (IPv6 only) und DS (IPv4+v6))
        add(create(null,    of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(HSI),      null, TRUE), new CcAdslIpNtHsiMuc());
        add(create(null,    of(ALC), null, of(ADSL),      of(IP),  of(NT),   of(VOIP),     TRUE, TRUE), new CcAdslIpNtVoipMuc());
        add(create(null,    of(ALC), null, of(ADSL),      null,    of(LT),   of(HSI),      null, TRUE), new CcFixed(0, 40));
        add(create(null,    of(ALC), null, of(ADSL),      null,    of(LT),   of(CPE, IAD), null, TRUE), new CcFixed(0, 3));
        add(create(null,    of(ALC), null, of(ADSL),      of(IP),  of(LT),   of(VOIP),     TRUE, TRUE), new CcFixed(0, 200));

        // VDSL strategies
        add(create(null,    of(ALC), null, of(VDSL2),     of(IP),  of(NT),   of(HSI),      null, null), new CcAdslIpNtHsiMuc());
        add(create(null,    of(ALC), null, of(VDSL2),     of(IP),  of(NT),   of(VOIP),     TRUE, null), new CcAdslIpNtVoipMuc());
        add(create(null,    of(ALC), null, of(VDSL2),     null,    of(LT),   of(HSI),      null, null), new CcFixed(0, 40));
        add(create(null,    of(ALC), null, of(VDSL2),     null,    of(LT),   of(CPE, IAD), null, null), new CcFixed(0, 3));
        add(create(null,    of(ALC), null, of(VDSL2),     of(IP),  of(LT),   of(VOIP),     TRUE, null), new CcFixed(0, 200));



        // Huawei HSI
        // BRAS
        add(create(null,    of(HUA),  MA5600T,      null,   null, of(BRAS),  of(HSI), null,  null), new CcHuaMA5600TBrasHsi());
        add(create(null,    of(HUA), MA5600V3,      null,   null, of(BRAS),  of(HSI), null,  null), new CcHuaMA5600v3BrasHsi());
        add(create(null,    of(HUA),  MA5603T,      null,   null, of(BRAS),  of(HSI), null,  null), new CcHuaMA5603TBrasHsi());
        // NT
        add(create(null,    of(HUA),  MA5600T,      null,   null,   of(NT),  of(HSI), null,  null), new CcHuaMA5600TNtHsi());
        add(create(null,    of(HUA), MA5600V3,      null,   null,   of(NT),  of(HSI), null,  null), new CcHuaMA5600v3NtHsi());
        add(create(null,    of(HUA),  MA5603T,      null,   null,   of(NT),  of(HSI), null,  null), new CcHuaMA5603TNtHsi());
        // LT
        add(create(null,    of(HUA),     null,  of(SDSL), of(IP),   of(LT),  of(HSI), null,  null), new CcFixed(8, 65));
        add(create(null,    of(HUA),     null,  of(ADSL), of(IP),   of(LT),  of(HSI), null, FALSE), new CcFixed(1, 32));
        add(create(null,    of(HUA),     null,  of(ADSL), of(IP),   of(LT),  of(HSI), null,  TRUE), new CcFixed(0, 40));
        add(create(null,    of(HUA),     null, of(VDSL2),   null,   of(LT),  of(HSI), null,  null), new CcFixed(0, 40));


        // Huawei VOIP
        // BRAS
        add(create(null,    of(HUA),  MA5600T,      null,   null, of(BRAS), of(VOIP), TRUE, null), new CcHuaMA5600TBrasVoip());
        add(create(null,    of(HUA), MA5600V3,      null,   null, of(BRAS), of(VOIP), TRUE, null), new CcHuaMA5600v3BrasVoip());
        add(create(null,    of(HUA),  MA5603T,      null,   null, of(BRAS), of(VOIP), TRUE, null), new CcHuaMA5603TBrasVoip());
        // NT
        add(create(null,    of(HUA),  MA5600T,      null,   null,   of(NT), of(VOIP), TRUE, null), new CcHuaMA5600TNtVoip());
        add(create(null,    of(HUA), MA5600V3,      null,   null,   of(NT), of(VOIP), TRUE, null), new CcHuaMA5600v3NtVoip());
        add(create(null,    of(HUA),  MA5603T,      null,   null,   of(NT), of(VOIP), TRUE, null), new CcHuaMA5603TNtVoip());
        // LT
        add(create(null,    of(HUA),     null,      null, of(IP),   of(LT), of(VOIP), TRUE, null), new CcFixed(0, 200));


        // Huawei CPE
        // BRAS
        add(create(null,    of(HUA),     null,      null,   null, of(BRAS),  of(CPE), null, null), new CcNull()); // CPE-CCs haben keine BRAS-Werte
        // NT
        add(create(of(AGB), of(HUA),     null,      null, of(IP),   of(NT),  of(CPE), null, null), new CcFixed(null, 2));
        add(create(null,    of(HUA),     null,      null, of(IP),   of(NT),  of(CPE), null, null), new CcFixed(null, 4));
        // LT
        add(create(null,    of(HUA),     null,      null,   null,   of(LT),  of(CPE), null, null), new CcFixed(11, 55));


        // Huawei IAD
        // BRAS
        add(create(null,    of(HUA),     null,      null,   null, of(BRAS),  of(IAD), null, null), new CcNull()); // IAD-CCs haben keine BRAS-Werte
        // NT
        add(create(null,    of(HUA),     null,      null, of(IP),   of(NT),  of(IAD), null, null), new CcFixed(null, 3));
        // LT
        add(create(null,    of(HUA),     null,      null,   null,   of(LT),  of(IAD), null, null), new CcFixed(0, 3));
    }
    // @formatter:on

    private static void add(List<CcStrategyType> strategyTypes, CcStrategy strategy) {
        for (CcStrategyType strategyType : strategyTypes) {
            strategies.add(Pair.create(strategyType, strategy));
        }
    }

    /**
     * Sucht die Strategie, die fuer den angegebenen CcStrategyTyp passt.
     */
    public static CcStrategy get(CcStrategyType type) {
        Pair<CcStrategyType, CcStrategy> best = null;
        for (Pair<CcStrategyType, CcStrategy> strategy : strategies) {
            if (type.compare(strategy.getFirst())) {
                if (best == null) {
                    best = strategy;
                }
                else {
                    if (best.getFirst().isSubsetOf(strategy.getFirst()) == strategy.getFirst().isSubsetOf(best.getFirst())) {
                        throw new IllegalStateException("Fehler in CrossConnection-Konfiguration: Zwei Kandidaten " +
                                "fuer CrossConnection vom Typ " + type.toString() + ": " + best.getFirst().toString() +
                                " und " + strategy.getFirst().toString());
                    }
                    if (strategy.getFirst().isSubsetOf(best.getFirst())) {
                        best = strategy;
                    }
                }
            }
        }
        return best != null ? best.getSecond() : null;
    }

    /**
     * Default-implementation does not return any BRAS Pool.
     */
    public BrasPool getBrasPool(CcCalculator ccCalculator) {
        return null;
    }


    /**
     * Utility function for subclasses. Map a port to a default VC/CVLAN value - Munich logic. <br/><br/> Short:
     * subrackOffset = for each subrack before the subrack with the port,<br/> sum up the maximum number of possible
     * ports<br/> <br/> inner = subrackOffset + (cardNum - 1) * (maximum number of ports on a card possible for subrack
     * type) + portNum
     */
    private static int calculateInnerMuc(int start, CcCalculator ccCalculator) {
        RegularExpressionService regularExpressionService = ccCalculator.getRegularExpressionService();
        HWBaugruppenTyp baugruppenTyp = ccCalculator.getBaugruppenTyp();
        List<HWSubrack> subrackList = ccCalculator.getSubrackList();
        HWSubrack subrack = ccCalculator.getSubrack();
        Equipment port = ccCalculator.getEquipment();
        HWDslam dslam = ccCalculator.getDslam();
        int inner = start;

        if (dslam.getCcOffset() != null) {
            inner += dslam.getCcOffset().intValue();
        }

        for (int i = 0; (i < subrackList.size()) && !subrackList.get(i).getId().equals(subrack.getId()); ++i) {
            HWSubrackTyp type = subrackList.get(i).getSubrackTyp();
            inner += type.getPortCount() * type.getBgCount();
        }

        Integer baugruppenNummer = Integer.valueOf(regularExpressionService.match(baugruppenTyp.getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.CARD_NUM_FROM_HW_EQN, port.getHwEQN()));
        Integer portNummer = Integer.valueOf(regularExpressionService.match(baugruppenTyp.getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.PORT_NUM_FROM_HW_EQN, port.getHwEQN()));
        inner += (subrack.getSubrackTyp().getPortCount() * (baugruppenNummer - 1)) + portNummer;
        return inner;
    }

    /**
     * Utility function for subclasses. Map a port to a default VC/CVLAN value - Nuernberg logic. <br/> Short: inner =
     * 48 * (cardNum - 1) + portNum
     */
    private static int calculateInnerNue(int start, CcCalculator ccCalculator) {
        int inner = start;
        RegularExpressionService regularExpressionService = ccCalculator.getRegularExpressionService();
        HWBaugruppenTyp baugruppenTyp = ccCalculator.getBaugruppenTyp();
        Equipment port = ccCalculator.getEquipment();
        Integer baugruppenNummer = Integer.valueOf(regularExpressionService.match(baugruppenTyp.getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.CARD_NUM_FROM_HW_EQN, port.getHwEQN()));
        Integer portNummer = Integer.valueOf(regularExpressionService.match(baugruppenTyp.getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.PORT_NUM_FROM_HW_EQN, port.getHwEQN()));
        inner += (48 * (baugruppenNummer - 1)) + portNummer;
        return inner;
    }

    /**
     * Wenn n(LT) < 7 :  n(VLAN) = (n(LT) x 64) + 11 + n(P) Wenn n(LT) >= 8:  n(VLAN) = ((n(LT)-1) x 64) + 11 + n(P)
     * <p/>
     * Mit: n(VLAN) = Nummer VLAN N(LT) = Steckplatznummer der Linecard 0-15 n(P)    = Portnummer 0-63
     * <p/>
     * Anmerkung: Slot 7 ist eine Systembaugruppe und kann nicht mit DSL Karten beschaltet werden.
     */
    protected static int calculateInnerHuawei(int offset, CcCalculator ccCalculator) {
        String hwEquipment = ccCalculator.getEquipment().getHwEQN();
        String modNumber = ccCalculator.getBaugruppe().getModNumber();

        int steckplatznummer = getLast2CharactersAsInt(modNumber);
        int portnummer = getLast2CharactersAsInt(hwEquipment);

        int innervlan = 0;
        if (steckplatznummer < 7) {
            innervlan = (steckplatznummer * 64) + 11 + portnummer;
        }
        else {
            innervlan = ((steckplatznummer - 1) * 64) + 11 + portnummer;
        }
        return offset + innervlan;
    }

    protected static int calculateInnerHuaweiMA5600v3(int offset, CcCalculator ccCalculator) {
        int steckplatznummer = getSteckplatznummer(ccCalculator);
        int portnummer = getPortnummer(ccCalculator);

        int innervlan = 0;
        if (steckplatznummer <= 6) {
            innervlan = (steckplatznummer * 64) + 11 + portnummer;
        }
        else if (steckplatznummer >= 9) {
            innervlan = ((steckplatznummer - 1) * 64) + 11 + portnummer;
        }
        return offset + innervlan;
    }

    protected static int calculateInnerHuaweiMA5603T(int offset, CcCalculator ccCalculator) {
        int steckplatznummer = getSteckplatznummer(ccCalculator);
        int portnummer = getPortnummer(ccCalculator);
        return offset + (steckplatznummer * 64) + 11 + portnummer;
    }

    protected static int calculateInnerHuaweiMA5600T(int offset, CcCalculator ccCalculator) {
        int steckplatznummer = getSteckplatznummer(ccCalculator);
        int portnummer = getPortnummer(ccCalculator);

        int innervlan = 0;
        if (steckplatznummer <= 8) {
            innervlan = (steckplatznummer * 64) + 11 + portnummer;
        }
        else if (steckplatznummer >= 11) {
            innervlan = ((steckplatznummer - 1) * 64) + 11 + portnummer;
        }
        return offset + innervlan;
    }

    private static int getSteckplatznummer(CcCalculator ccCalculator) {
        return getLast2CharactersAsInt(ccCalculator.getBaugruppe().getModNumber());
    }

    private static int getPortnummer(CcCalculator ccCalculator) {
        return getLast2CharactersAsInt(ccCalculator.getEquipment().getHwEQN());
    }

    private static int getLast2CharactersAsInt(String sourceString) {
        if (StringUtils.isBlank(sourceString) || (sourceString.length() < 2)) {
            throw new RuntimeException("Cannot parse String for CC calculation.");
        }
        int length = sourceString.length();
        return Integer.parseInt(sourceString.substring(length - 2, length));
    }

    // ==============================================================
    // STRATEGIES
    // ==============================================================

    private static class CcFixed extends CcStrategy {
        private final Integer outer;
        private final Integer inner;

        public CcFixed(Integer outer, Integer inner) {
            this.outer = outer;
            this.inner = inner;
        }

        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return outer;
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return inner;
        }
    }

    private static class CcNull extends CcFixed {
        public CcNull() {
            super(null, null);
        }
    }

    private static class CcSdslAtmNtHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getVpiSDSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(100, ccCalculator);
        }
    }

    private static class CcSdslIpNtHsiMuc extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagSDSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(10, ccCalculator);
        }
    }

    private static class CcSdslIpNtHsiNbg extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagSDSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerNue(10, ccCalculator);
        }
    }

    private static class CcAtmNtCpe extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getVpiCpeMgmt();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(100, ccCalculator);
        }
    }

    /* Luther: Bei RB-VLANs ist die VLAN-ID bei NT-Inner einzutragen, nicht bei NT-Outer */
    private static class CcSdslIpNtCpe extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return null;
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagCpeMgmt();
        }
    }

    private static class CcSdslIpBrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagSDSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcBrasPool extends CcStrategy {
        private final String poolPrefix;

        public CcBrasPool(String atmSdslPoolPrefix) {
            this.poolPrefix = atmSdslPoolPrefix;
        }

        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getBrasPool(poolPrefix).getVp();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getVcFromBrasPool(ccCalculator.getBrasPool(poolPrefix));
        }

        @Override
        public BrasPool getBrasPool(CcCalculator ccCalculator) {
            return ccCalculator.getBrasPool(poolPrefix);
        }
    }

    private static class CcAdslAtmNtHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getVpiUbrADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(100, ccCalculator);
        }
    }

    private static class CcAdslAtmBrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasVpiADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcAdslIpNtHsiMuc extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(10, ccCalculator);
        }
    }

    private static class CcAdslIpNtHsiNbg extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerNue(10, ccCalculator);
        }
    }

    private static class CcAdslIpBrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5600TNtHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5600T(OFFSET_HSI, ccCalculator);
        }
    }

    private static class CcHuaMA5600TNtVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5600T(OFFSET_VOIP, ccCalculator);
        }
    }

    private static class CcHuaMA5600TBrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5600TBrasVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5603TNtHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5603T(OFFSET_HSI, ccCalculator);
        }
    }

    private static class CcHuaMA5603TNtVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5603T(OFFSET_VOIP, ccCalculator);
        }
    }

    private static class CcHuaMA5603TBrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5603TBrasVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5600v3NtHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5600v3(OFFSET_HSI, ccCalculator);
        }
    }

    private static class CcHuaMA5600v3NtVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerHuaweiMA5600v3(OFFSET_VOIP, ccCalculator);
        }
    }

    private static class CcHuaMA5600v3BrasHsi extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagADSL();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    private static class CcHuaMA5600v3BrasVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

    /* Luther: Bei RB-VLANs ist die VLAN-ID bei NT-Inner einzutragen, nicht bei NT-Outer */
    private static class CcAdslIpNtCpe extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return null;
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagCpeMgmt();
        }
    }

    /**
     * Luther: Bei RB-VLANs ist die VLAN-ID bei NT-Inner einzutragen, nicht bei NT-Outer
     */
    private static class CcAdslIpNtIad extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return null;
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagIadMgmt();
        }
    }

    private static class CcAdslIpNtVoipMuc extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerMuc(2010, ccCalculator);
        }
    }

    private static class CcAdslIpNtVoipNbg extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return calculateInnerNue(2010, ccCalculator);
        }
    }

    private static class CcAdslIpBrasVoip extends CcStrategy {
        @Override
        public Integer getOuter(CcCalculator ccCalculator) {
            return ccCalculator.getDslam().getBrasOuterTagVoip();
        }

        @Override
        public Integer getInner(CcCalculator ccCalculator) {
            return ccCalculator.getNtInner();
        }
    }

}
