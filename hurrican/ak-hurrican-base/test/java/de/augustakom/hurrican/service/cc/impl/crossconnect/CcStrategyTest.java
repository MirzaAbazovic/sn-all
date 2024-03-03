/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2010 11:17:06
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.CcType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.DslType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.HwType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.NlType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TechType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TermType.*;
import static java.lang.Boolean.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.cc.RegularExpressionService;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.CcType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.DslType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.HwType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.NlType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TechType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.TermType;


/**
 * <b>Wichtig</b>: {@link #testThatStrategyCantBeDisambiguous()} testet, ob die Konfiguration der
 * CrossConnection-Strategien nicht gegen Sicherheitsregeln verstoesst und sollte nie abgeschaltet werden.
 *
 *
 */
@Test(groups = BaseTest.UNIT)
public class CcStrategyTest extends BaseTest {

    private CcCalculator calculatorMock;
    private RegularExpressionService regexpMock;

    @BeforeClass
    public void prepare() {
        HWBaugruppe baugruppe = new HWBaugruppe();
        baugruppe.setModNumber("015");
        Equipment port = new Equipment();
        port.setHwEQN("U02-2-015-00");

        HWDslam dslam = new HWDslam();
        dslam.setOuterTagADSL(1);
        dslam.setOuterTagSDSL(2);
        dslam.setOuterTagVoip(3);
        dslam.setOuterTagCpeMgmt(4);
        dslam.setOuterTagIadMgmt(5);
        dslam.setVpiUbrADSL(6);
        dslam.setVpiSDSL(7);
        dslam.setVpiCpeMgmt(8);
        dslam.setBrasOuterTagADSL(9);
        dslam.setBrasOuterTagSDSL(10);
        dslam.setBrasOuterTagVoip(11);
        dslam.setBrasVpiADSL(12);
        calculatorMock = mock(CcCalculator.class);
        when(calculatorMock.getDslam()).thenReturn(dslam);
        when(calculatorMock.getNtOuter()).thenReturn(-1);
        when(calculatorMock.getNtInner()).thenReturn(-2);
        regexpMock = mock(RegularExpressionService.class);
        when(regexpMock.match(anyString(), any(Class.class), any(CfgRegularExpression.Info.class), anyString()))
                .thenReturn("01");
        when(calculatorMock.getRegularExpressionService()).thenReturn(regexpMock);
        when(calculatorMock.getEquipment()).thenReturn(port);
        when(calculatorMock.getBaugruppe()).thenReturn(baugruppe);
        when(calculatorMock.getSubrackList()).thenReturn(new ArrayList<HWSubrack>());
        when(calculatorMock.getSubrack()).thenReturn(new HWSubrackBuilder().init().build());
        when(calculatorMock.getBaugruppenTyp()).thenReturn(new HWBaugruppenTypBuilder().build());
    }

    @DataProvider
    public Object[][] strategyTestData() {
        // @formatter:off
        return new Object[][] {
                // Alcatel
                { MUC, ALC, SDSL,  IP,  NT,   HSI,  null, FALSE, null, "CcSdslIpNtHsiMuc", 2, 11 },
                { MUC, ALC, ADSL,  IP,  NT,   HSI,  null, FALSE, null, "CcAdslIpNtHsiMuc", 1, 11 },
                { MUC, ALC, VDSL2, IP,  NT,   HSI,  null, FALSE, null, "CcAdslIpNtHsiMuc", 1, 11 },
                { MUC, ALC, ADSL,  IP,  NT,   HSI,  null, TRUE, null, "CcAdslIpNtHsiMuc", 1, 11 },
                { NBG, ALC, SDSL,  IP,  NT,   HSI,  null, FALSE, null, "CcSdslIpNtHsiNbg", 2, 11 },
                { NBG, ALC, ADSL,  IP,  NT,   HSI,  null, FALSE, null, "CcAdslIpNtHsiNbg", 1, 11 },
                { NBG, ALC, VDSL2, IP,  NT,   HSI,  null, FALSE, null, "CcAdslIpNtHsiMuc", 1, 11 },
                { NBG, ALC, ADSL,  IP,  NT,   HSI,  null, TRUE, null, "CcAdslIpNtHsiMuc", 1, 11 },

                { MUC, ALC, SDSL,  IP,  NT,   CPE,  null, FALSE, null, "CcSdslIpNtCpe", null, 4 },
                { MUC, ALC, ADSL,  IP,  NT,   CPE,  null, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                { MUC, ALC, ADSL,  IP,  NT,   CPE,  FALSE, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                //{ MUC, ALC, ADSL, IP,  NT,   CPE,  TRUE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, VDSL2, IP,  NT,   CPE,  null, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                { MUC, ALC, VDSL2, IP,  NT,   CPE,  FALSE, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                //{ MUC, ALC, VDSL2, IP,  NT,   CPE,  TRUE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   CPE,  null, TRUE, null, "CcAdslIpNtCpe", null, 4 },
                { MUC, ALC, ADSL,  IP,  NT,   CPE,  FALSE, TRUE, null, "CcAdslIpNtCpe", null, 4 },
                //{ MUC, ALC, ADSL,  IP,  NT,   CPE,  TRUE, TRUE, null, "CcAdslIpNtIad", null, 5 },

                { MUC, ALC, SDSL,  IP,  NT,   IAD,  null, FALSE, null, "CcSdslIpNtCpe", null, 4 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  null, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  FALSE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  TRUE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, VDSL2, IP,  NT,   IAD,  null, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, VDSL2, IP,  NT,   IAD,  FALSE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, VDSL2, IP,  NT,   IAD,  TRUE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  null, TRUE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  FALSE, TRUE, null, "CcAdslIpNtIad", null, 5 },
                { MUC, ALC, ADSL,  IP,  NT,   IAD,  TRUE, TRUE, null, "CcAdslIpNtIad", null, 5 },

                { MUC, ALC, SDSL,  IP,  BRAS, HSI,  null, FALSE, null, "CcSdslIpBrasHsi", 10, -2 },
                { MUC, ALC, ADSL,  IP,  BRAS, HSI,  null, FALSE, null, "CcAdslIpBrasHsi", 9, -2 },
                { MUC, ALC, VDSL2, IP,  BRAS, HSI,  null, FALSE, null, "CcAdslIpBrasHsi", 9, -2 },
                { MUC, ALC, ADSL,  IP,  BRAS, HSI,  null, TRUE, null, "CcAdslIpBrasHsi", 9, -2 },
                { NBG, ALC, SDSL,  IP,  BRAS, HSI,  null, FALSE, null, "CcSdslIpBrasHsi", 10, -2 },
                { NBG, ALC, ADSL,  IP,  BRAS, HSI,  null, FALSE, null, "CcAdslIpBrasHsi", 9, -2 },
                { NBG, ALC, VDSL2, IP,  BRAS, HSI,  null, FALSE, null, "CcAdslIpBrasHsi", 9, -2 },
                { NBG, ALC, ADSL,  IP,  BRAS, HSI,  null, TRUE, null, "CcAdslIpBrasHsi", 9, -2 },

                { MUC, ALC, SDSL,  IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, SDSL,  ATM, BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  ATM, BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, VDSL2, IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  IP,  BRAS, CPE,  null, TRUE, null, "CcNull", null, null },

                { MUC, ALC, SDSL,  IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, SDSL,  ATM, BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  ATM, BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, VDSL2, IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, ALC, ADSL,  IP,  BRAS, IAD,  null, TRUE, null, "CcNull", null, null },

                { MUC, ALC, SDSL,  IP,  LT,   VOIP, TRUE, FALSE, null,  null, null, null },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, null, FALSE, null,  null, null, null },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, FALSE, FALSE, null, null, null, null },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, TRUE, FALSE, null, "CcFixed", 7, 77 },
                { MUC, ALC, VDSL2, IP,  LT,   VOIP, null, FALSE, null,  null, null, null },
                { MUC, ALC, VDSL2, IP,  LT,   VOIP, FALSE, FALSE, null, null, null, null },
                { MUC, ALC, VDSL2, IP,  LT,   VOIP, TRUE, FALSE, null, "CcFixed", 0, 200 },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, null, TRUE, null,  null, null, null },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, FALSE, TRUE, null, null, null, null },
                { MUC, ALC, ADSL,  IP,  LT,   VOIP, TRUE, TRUE, null, "CcFixed", 0, 200 },

                { MUC, ALC, SDSL,  ATM, NT,   CPE,  null, FALSE, null, "CcAtmNtCpe", 8, 101 },
                { MUC, ALC, ADSL,  ATM, NT,   CPE,  null, FALSE, null, "CcAtmNtCpe", 8, 101 },
                { MUC, ALC, VDSL2, ATM, NT,   CPE,  null, FALSE, null, null, null, null},
                { MUC, ALC, ADSL,  ATM, NT,   CPE,  null, TRUE, null, null, null, null},

                { MUC, ALC, SDSL,  ATM, NT,   IAD,  null, FALSE, null, "CcAtmNtCpe", 8, 101 },
                { MUC, ALC, ADSL,  ATM, NT,   IAD,  null, FALSE, null, "CcAtmNtCpe", 8, 101 },
                { MUC, ALC, VDSL2, ATM, NT,   IAD,  null, FALSE, null, null, null, null},
                { MUC, ALC, ADSL,  ATM, NT,   IAD,  null, TRUE, null, null, null, null},

                { MUC, ALC, SDSL,  IP,  NT,   VOIP, TRUE, FALSE, null, null, null, null},
                { MUC, ALC, SDSL,  IP,  NT,   VOIP, TRUE, FALSE, null, null, null, null},
                { MUC, ALC, ADSL,  IP,  NT,   VOIP, TRUE, FALSE, null, "CcAdslIpNtVoipMuc", 3, 2011 },
                { MUC, ALC, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, null, "CcAdslIpNtVoipMuc", 3, 2011 },
                { MUC, ALC, ADSL,  IP,  NT,   VOIP, TRUE, TRUE, null, "CcAdslIpNtVoipMuc", 3, 2011 },
                { NBG, ALC, SDSL,  IP,  NT,   VOIP, TRUE, FALSE, null, null, null, null},
                { NBG, ALC, ADSL,  IP,  NT,   VOIP, TRUE, FALSE, null, "CcAdslIpNtVoipNbg", 3, 2011 },
                { NBG, ALC, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, null, "CcAdslIpNtVoipMuc", 3, 2011 },
                { NBG, ALC, ADSL,  IP,  NT,   VOIP, TRUE, TRUE, null, "CcAdslIpNtVoipMuc", 3, 2011 },

                { MUC, ALC, SDSL,  IP,  BRAS, VOIP, TRUE,  FALSE, null, null, null, null},
                { MUC, ALC, ADSL,  IP,  BRAS, VOIP, TRUE, FALSE, null, "CcAdslIpBrasVoip", 11, -2 },
                { MUC, ALC, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, null, "CcAdslIpBrasVoip", 11, -2 },
                { MUC, ALC, ADSL,  IP,  BRAS, VOIP, TRUE, TRUE, null, "CcAdslIpBrasVoip", 11, -2 },

                { MKK, ALC, VDSL2, IP,  NT,   HSI,  null, FALSE, null, "CcAdslIpNtHsiMuc", 1, 11 },
                { MKK, ALC, VDSL2, IP,  NT,   CPE,  null, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                { MKK, ALC, VDSL2, IP,  NT,   CPE,  FALSE, FALSE, null, "CcAdslIpNtCpe", null, 4 },
                { MKK, ALC, VDSL2, IP,  NT,   IAD,  null, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MKK, ALC, VDSL2, IP,  NT,   IAD,  FALSE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MKK, ALC, VDSL2, IP,  NT,   IAD,  TRUE, FALSE, null, "CcAdslIpNtIad", null, 5 },
                { MKK, ALC, VDSL2, IP,  BRAS, HSI,  null, FALSE, null, "CcAdslIpBrasHsi", 9, -2 },
                { MKK, ALC, VDSL2, IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MKK, ALC, VDSL2, IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MKK, ALC, VDSL2, IP,  LT,   VOIP, null, FALSE, null,  null, null, null },
                { MKK, ALC, VDSL2, IP,  LT,   VOIP, FALSE, FALSE, null, null, null, null },
                { MKK, ALC, VDSL2, IP,  LT,   VOIP, TRUE, FALSE, null, "CcFixed", 0, 200 },
                { MKK, ALC, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, null, "CcAdslIpNtVoipMuc", 3, 2011 },
                { MKK, ALC, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, null, "CcAdslIpBrasVoip", 11, -2 },

                // MKK, Huawei, VDSL2, IPV4 only
                { MKK, HUA, VDSL2, IP,  NT,   CPE,  null, FALSE, null, "CcFixed", null, 4 },
                { MKK, HUA, VDSL2, IP,  NT,   CPE,  FALSE, FALSE, null, "CcFixed", null, 4 },
                { MKK, HUA, VDSL2, IP,  NT,   IAD,  null, FALSE, null, "CcFixed", null, 3 },
                { MKK, HUA, VDSL2, IP,  NT,   IAD,  FALSE, FALSE, null, "CcFixed", null, 3 },
                { MKK, HUA, VDSL2, IP,  NT,   IAD,  TRUE, FALSE, null, "CcFixed", null, 3 },
                { MKK, HUA, VDSL2, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600T, "CcHuaMA5600TNtHsi", 1, 907 },
                { MKK, HUA, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, CcStrategy.MA5600T, "CcHuaMA5600TNtVoip", 3, 2907 },
                { MKK, HUA, VDSL2, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907 },
                { MKK, HUA, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtVoip", 3, 2907 },
                { MKK, HUA, VDSL2, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5603T, "CcHuaMA5603TNtHsi", 1, 971 },
                { MKK, HUA, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, CcStrategy.MA5603T, "CcHuaMA5603TNtVoip", 3, 2971 },


                { MKK, HUA, VDSL2, IP,  LT,   HSI,  null, FALSE, null, "CcFixed", 0, 40 },
                { MKK, HUA, VDSL2, IP,  LT,   CPE,  null, FALSE, null, "CcFixed", 11, 55 },
                { MKK, HUA, VDSL2, IP,  LT,   IAD,  null, FALSE, null, "CcFixed", 0, 3 },
                { MKK, HUA, VDSL2, IP,  LT,   VOIP, null,  FALSE, null, null, null, null },
                { MKK, HUA, VDSL2, IP,  LT,   VOIP, FALSE, FALSE, null, null, null, null },
                { MKK, HUA, VDSL2, IP,  LT,   VOIP, TRUE, FALSE, null, "CcFixed", 0, 200 },

                { MKK, HUA, VDSL2, IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MKK, HUA, VDSL2, IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },

                { MKK, HUA, VDSL2, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600T, "CcHuaMA5600TBrasHsi", 9, -2 },
                { MKK, HUA, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, CcStrategy.MA5600T, "CcHuaMA5600TBrasVoip", 11, -2 },
                { MKK, HUA, VDSL2, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2 },
                { MKK, HUA, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasVoip", 11, -2 },
                { MKK, HUA, VDSL2, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5603T, "CcHuaMA5603TBrasHsi", 9, -2 },
                { MKK, HUA, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, CcStrategy.MA5603T, "CcHuaMA5603TBrasVoip", 11, -2 },

                { MKK, HUA, VDSL2, ATM, NT,   CPE,  null,  FALSE, null, null, null, null},
                { MKK, HUA, VDSL2, ATM, NT,   IAD,  null,  FALSE, null, null, null, null},

                // MUC, Huawei, VDSL2, IPv4 only
                { MUC, HUA, VDSL2, IP,  NT,   CPE,  null, FALSE, null, "CcFixed", null, 4 },
                { MUC, HUA, VDSL2, IP,  NT,   CPE,  FALSE, FALSE, null, "CcFixed", null, 4 },
                { MUC, HUA, VDSL2, IP,  NT,   IAD,  null, FALSE, null, "CcFixed", null, 3 },
                { MUC, HUA, VDSL2, IP,  NT,   IAD,  FALSE, FALSE, null, "CcFixed", null, 3 },
                { MUC, HUA, VDSL2, IP,  NT,   IAD,  TRUE, FALSE, null, "CcFixed", null, 3 },
                { MUC, HUA, VDSL2, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907 },
                { MUC, HUA, VDSL2, IP,  NT,   VOIP, TRUE, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtVoip", 3, 2907 },

                { MUC, HUA, VDSL2, IP,  LT,   HSI,  null, FALSE, null, "CcFixed", 0, 40 },
                { MUC, HUA, VDSL2, IP,  LT,   CPE,  null, FALSE, null, "CcFixed", 11, 55 },
                { MUC, HUA, VDSL2, IP,  LT,   IAD,  null, FALSE, null, "CcFixed", 0, 3 },
                { MUC, HUA, VDSL2, IP,  LT,   VOIP, null,  FALSE, null, null, null, null },
                { MUC, HUA, VDSL2, IP,  LT,   VOIP, FALSE, FALSE, null, null, null, null },
                { MUC, HUA, VDSL2, IP,  LT,   VOIP, TRUE, FALSE, null, "CcFixed", 0, 200 },

                { MUC, HUA, VDSL2, IP,  BRAS, CPE,  null, FALSE, null, "CcNull", null, null },
                { MUC, HUA, VDSL2, IP,  BRAS, IAD,  null, FALSE, null, "CcNull", null, null },
                { MUC, HUA, VDSL2, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2 },
                { MUC, HUA, VDSL2, IP,  BRAS, VOIP, TRUE, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasVoip", 11, -2 },

                { MUC, HUA, VDSL2, ATM, NT,   CPE,  null,  FALSE, null, null, null, null},
                { MUC, HUA, VDSL2, ATM, NT,   IAD,  null,  FALSE,  null, null, null, null},

                // Huawei (ADSL, IPv6, MUC -HUA ist jedoch niederlassungsunabhaengig-)
                { MUC, HUA, ADSL, IP,  NT,   CPE,  null, TRUE, null, "CcFixed", null, 4 },
                { MUC, HUA, ADSL, IP,  NT,   CPE,  FALSE, TRUE, null, "CcFixed", null, 4 },
                { MUC, HUA, ADSL, IP,  NT,   CPE,  TRUE, TRUE, null, "CcFixed", null, 4 },
                { MUC, HUA, ADSL, IP,  NT,   IAD,  null, TRUE, null, "CcFixed", null, 3 },
                { MUC, HUA, ADSL, IP,  NT,   IAD,  FALSE, TRUE, null, "CcFixed", null, 3 },
                { MUC, HUA, ADSL, IP,  NT,   IAD,  TRUE, TRUE, null, "CcFixed", null, 3 },
                { MUC, HUA, ADSL, IP,  NT,   HSI,  null, TRUE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907 },
                { MUC, HUA, ADSL, IP,  NT,   VOIP, TRUE, TRUE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtVoip", 3, 2907 },

                { MUC, HUA, ADSL, IP,  LT,   HSI,  null, TRUE, null, "CcFixed", 0, 40 },
                { MUC, HUA, ADSL, IP,  LT,   CPE,  null, TRUE, null, "CcFixed", 11, 55 },
                { MUC, HUA, ADSL, IP,  LT,   IAD,  null, TRUE, null, "CcFixed", 0, 3 },
                { MUC, HUA, ADSL, IP,  LT,   VOIP, null,  TRUE, null, null, null, null },
                { MUC, HUA, ADSL, IP,  LT,   VOIP, FALSE, TRUE, null, null, null, null },
                { MUC, HUA, ADSL, IP,  LT,   VOIP, TRUE, TRUE, null, "CcFixed", 0, 200 },

                { MUC, HUA, ADSL, IP,  BRAS, CPE,  null, TRUE, null, "CcNull", null, null },
                { MUC, HUA, ADSL, IP,  BRAS, IAD,  null, TRUE, null, "CcNull", null, null },
                { MUC, HUA, ADSL, IP,  BRAS, HSI,  null, TRUE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2 },
                { MUC, HUA, ADSL, IP,  BRAS, VOIP, TRUE, TRUE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasVoip", 11, -2 },

                { MUC, HUA, ADSL, ATM, NT,   CPE,  null,  TRUE, null, null, null, null},
                { MUC, HUA, ADSL, ATM, NT,   IAD,  null,  TRUE, null, null, null, null},

                // Huawei (ADSL, SDSL, IPv4 only, AGB -HUA ist jedoch niederlassungsunabhaengig-)
                { AGB, HUA, SDSL, IP,  LT,   HSI,  null, FALSE, null, "CcFixed", 8, 65},
                { AGB, HUA, SDSL, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907},
                { AGB, HUA, SDSL, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2},

                { AGB, HUA, ADSL, IP,  LT,   HSI,  null, FALSE, null, "CcFixed", 1, 32},
                { AGB, HUA, ADSL, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907},
                { AGB, HUA, ADSL, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2},
                { AGB, HUA, ADSL, IP,  NT,   CPE,  TRUE, TRUE, null, "CcFixed", null, 2 },

                { AGB, HUA, SDSL, IP,  NT,   HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3NtHsi", 1, 907},
                { AGB, HUA, SDSL, IP,  BRAS, HSI,  null, FALSE, CcStrategy.MA5600V3, "CcHuaMA5600v3BrasHsi", 9, -2},
        };
        // @formatter:on
    }

    @Test(dataProvider = "strategyTestData")
    public void testGetStrategy(NlType nlType, TechType techType, DslType dslType,
            HwType hwType, TermType termType, CcType ccType, Boolean voip, Boolean isIpV6, String modelType,
            String wantedStrategy, Integer wantedOuter, Integer wantedInner) {
        CcStrategyType type = new CcStrategyType(nlType, techType, modelType, dslType, hwType, termType, ccType, voip, isIpV6);
        CcStrategy strategy = CcStrategy.get(type);
        if (wantedStrategy != null) {
            assertNotNull(strategy);
            assertEquals(strategy.getClass().getSimpleName(), wantedStrategy);
            assertEquals(strategy.getOuter(calculatorMock), wantedOuter);
            assertEquals(strategy.getInner(calculatorMock), wantedInner);
        }
        else {
            assertNull(strategy);
        }
    }

    public void testThatStrategyCantBeAmbiguous() {
        for (NlType nlType : NlType.values()) {
            for (TechType techType : TechType.values()) {
                for (DslType dslType : DslType.values()) {
                    for (HwType hwType : HwType.values()) {
                        for (TermType termType : TermType.values()) {
                            for (CcType ccType : CcType.values()) {
                                CcStrategy.get(new CcStrategyType(nlType, techType, null, dslType,
                                        hwType, termType, ccType, Boolean.FALSE, Boolean.FALSE));
                                CcStrategy.get(new CcStrategyType(nlType, techType, null, dslType,
                                        hwType, termType, ccType, Boolean.TRUE, Boolean.FALSE));
                                CcStrategy.get(new CcStrategyType(nlType, techType, null, dslType,
                                        hwType, termType, ccType, Boolean.FALSE, Boolean.TRUE));
                                CcStrategy.get(new CcStrategyType(nlType, techType, null, dslType,
                                        hwType, termType, ccType, Boolean.TRUE, Boolean.TRUE));
                            }
                        }
                    }
                }
            }
        }
    }

    @DataProvider
    public Object[][] innerHuaweiTestData() {
        return new Object[][] {
                { "015", "U02-2-015-00", 0, 907 },
                { "015", "U02-2-015-20", 0, 927 },
                { "01-00", "U02-2-015-15", 0, 26 },
                { "01-00", "U02-2-015-15", 2000, 2026 },
                { "01-04", "U02-2-015-18", 0, 285 },
                { "08", "U02-2-015-18", 0, 477 },
                { "08", "U02-2-015-18", 2000, 2477 },
                { "002", "U03-3-002-56", 0, 195},
        };
    }

    @Test(dataProvider = "innerHuaweiTestData")
    public void testCalculateInnerHuawei(String modNumber, String hwEqn, int offset, int expectedInnerHuawei) {
        CcCalculator calc = new CcCalculator();
        HWBaugruppe baugruppe = new HWBaugruppe();
        baugruppe.setModNumber(modNumber);
        Equipment port = new Equipment();
        port.setHwEQN(hwEqn);
        calc.baugruppe = baugruppe;
        calc.port = port;
        assertEquals(CcStrategy.calculateInnerHuawei(offset, calc), expectedInnerHuawei);
    }

    @DataProvider
    public Object[][] innerHuaweiMA5600v3TestData() {
        return new Object[][] {
                { "015", "U02-2-015-00", 0, 907 },
                { "015", "U02-2-015-20", 0, 927 },
                { "09", "U02-2-015-00", 0, 523 },
                { "01-00", "U02-2-015-15", 0, 26 },
                { "01-00", "U02-2-015-15", 2000, 2026 },
                { "01-04", "U02-2-015-18", 0, 285 },
                { "01-06", "U02-2-015-18", 0, 413 },
                { "07", "U02-2-015-18", 0, 0 },
                { "08", "U02-2-015-18", 0, 0 },
                { "08", "U02-2-015-18", 2000, 2000 },
        };
    }

    @Test(dataProvider = "innerHuaweiMA5600v3TestData")
    public void testCalculateInnerHuaweiMA5600v3(String modNumber, String hwEqn, int offset, int expectedInnerHuawei) {
        CcCalculator calc = createCcCalculator(createHWBaugruppe(modNumber), createEquipment(hwEqn));
        assertEquals(CcStrategy.calculateInnerHuaweiMA5600v3(offset, calc), expectedInnerHuawei);
    }

    @DataProvider
    public Object[][] innerHuaweiMA5600TTestData() {
        return new Object[][] {
                { "01-08", "U02-2-015-05", 0, 528 },
                { "01-08", "U02-2-015-05", 1000, 1528 },
                { "09", "U02-2-015-18", 0, 0 },
                { "01-10", "U02-2-015-18", 0, 0 },
                { "11", "U02-2-015-00", 0, 651 },
                { "13", "U02-2-015-00", 0, 779 },
        };
    }

    @Test(dataProvider = "innerHuaweiMA5600TTestData")
    public void testCalculateInnerHuaweiMA5600T(String modNumber, String hwEqn, int offset, int expectedInnerHuawei) {
        CcCalculator calc = createCcCalculator(createHWBaugruppe(modNumber), createEquipment(hwEqn));
        assertEquals(CcStrategy.calculateInnerHuaweiMA5600T(offset, calc), expectedInnerHuawei);
    }

    @DataProvider
    public Object[][] innerHuaweiMA5603TTestData() {
        return new Object[][] {
                { "01-08", "U02-2-015-05", 0, 528 },
                { "01-08", "U02-2-015-05", 1000, 1528 },
                { "09", "U02-2-015-18", 0, 605 },
                { "01-10", "U02-2-015-18", 0, 669 },
                { "11", "U02-2-015-00", 0, 715 },
        };
    }

    @Test(dataProvider = "innerHuaweiMA5603TTestData")
    public void testCalculateInnerHuaweiMA5603T(String modNumber, String hwEqn, int offset, int expectedInnerHuawei) {
        CcCalculator calc = createCcCalculator(createHWBaugruppe(modNumber), createEquipment(hwEqn));
        assertEquals(CcStrategy.calculateInnerHuaweiMA5603T(offset, calc), expectedInnerHuawei);
    }

    private HWBaugruppe createHWBaugruppe(String modNumber) {
        return new HWBaugruppeBuilder()
                .setPersist(false)
                .withModNumber(modNumber)
                .build();
    }

    private Equipment createEquipment(String hwEqn) {
        return new EquipmentBuilder()
                .setPersist(false)
                .withHwEQN(hwEqn)
                .build();
    }

    private CcCalculator createCcCalculator(HWBaugruppe hwBaugruppe, Equipment equipment) {
        CcCalculator calc = new CcCalculator();
        calc.baugruppe = hwBaugruppe;
        calc.port = equipment;
        return calc;
    }

}
