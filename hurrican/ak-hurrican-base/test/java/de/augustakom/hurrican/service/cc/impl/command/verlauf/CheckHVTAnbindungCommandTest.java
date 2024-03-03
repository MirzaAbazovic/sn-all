/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2010 09:40:31
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;


@Test(groups = BaseTest.UNIT)
public class CheckHVTAnbindungCommandTest extends BaseTest {

    @DataProvider(name = "dataProviderCheckAnbindungHVT")
    public Object[][] dataProviderCheckAnbindungHVT() {
        final Endstelle esA = mock(Endstelle.class);
        final Endstelle esB = mock(Endstelle.class);

        return new Object[][] {
                { Produkt.ES_TYP_KEINE_ENDSTELLEN, esA, esB, new Endstelle[] { } },
                { Produkt.ES_TYP_NUR_B, esA, esB, new Endstelle[] { esB } },
                { Produkt.ES_TYP_A_UND_B, esA, esB, new Endstelle[] { esA, esB } },
        };
    }

    @Test(dataProvider = "dataProviderCheckAnbindungHVT")
    public void testCheckAnbindungHVT(Integer produktEndstellen, Endstelle esA, Endstelle esB,
            Endstelle[] expectedInvokationFor) throws Exception {

        final Produkt produkt = mock(Produkt.class);
        when(produkt.getEndstellenTyp()).thenReturn(produktEndstellen);

        final CheckHVTAnbindungCommand command = spy(new CheckHVTAnbindungCommand());
        prepareDataForCommand(command, produkt, esA, esB);

        final Set<Endstelle> invokedFor = new HashSet<Endstelle>();
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Endstelle endstelle = (Endstelle) invocation.getArguments()[0];
                if (endstelle == null) {
                    throw new IllegalArgumentException("invoked on incorrect Endstelle");
                }
                invokedFor.add(endstelle);
                return null;
            }
        }).when(command).checkEndstelle4Anbindung(any(Endstelle.class));

        HVTService hvtService = mock(HVTService.class);
        HVTGruppe hvtGruppe = mock(HVTGruppe.class);
        when(hvtService.findHVTGruppe4Standort(any(Long.class))).thenReturn(hvtGruppe);
        doReturn(hvtService).when(command).getCCService(HVTService.class);

        command.checkAnbindungHVT();

        assertEquals(invokedFor.size(), expectedInvokationFor.length, "invalid number of invokations");
        for (Endstelle endstelle : expectedInvokationFor) {
            assertTrue(invokedFor.contains(endstelle), "missing invokation for endstelle");
        }
    }

    public void testCheckEndstelle4AnbindungEsA() throws Exception {

        final Endstelle esA = mock(Endstelle.class);
        when(esA.isEndstelleA()).thenReturn(true);
        when(esA.isEndstelleB()).thenReturn(false);
        when(esA.getRangierId()).thenReturn(Long.valueOf(1));
        when(esA.getAnschlussart()).thenReturn(Anschlussart.ANSCHLUSSART_KVZ);

        final CheckHVTAnbindungCommand command = spy(new CheckHVTAnbindungCommand());
        prepareDataForCommand(command, null, esA, null);

        doNothing().when(command).checkCarrierbestellung(any(Endstelle.class));
        doNothing().when(command).checkSourceAuftrag4TalNA(any(Endstelle.class));

        HVTService hvtService = mock(HVTService.class);
        HVTGruppe hvtGruppe = mock(HVTGruppe.class);
        when(hvtService.findHVTGruppe4Standort(any(Long.class))).thenReturn(hvtGruppe);
        doReturn(hvtService).when(command).getCCService(HVTService.class);

        command.checkEndstelle4Anbindung(esA);

        // Everything is ok, if no exception is thrown due to checks throw exception on invalid data
    }

    public void testCheckEndstelle4AnbindungEsB() throws Exception {

        final Endstelle esB = mock(Endstelle.class);
        when(esB.isEndstelleA()).thenReturn(false);
        when(esB.isEndstelleB()).thenReturn(true);
        when(esB.getRangierId()).thenReturn(Long.valueOf(1));
        when(esB.getAnschlussart()).thenReturn(Anschlussart.ANSCHLUSSART_HVT);

        final CheckHVTAnbindungCommand command = spy(new CheckHVTAnbindungCommand());
        prepareDataForCommand(command, null, null, esB);

        doNothing().when(command).checkCarrierbestellung(any(Endstelle.class));
        doNothing().when(command).checkSourceAuftrag4TalNA(any(Endstelle.class));
        doNothing().when(command).checkONKZ(any(HVTGruppe.class));

        HVTService hvtService = mock(HVTService.class);
        HVTGruppe hvtGruppe = mock(HVTGruppe.class);
        when(hvtService.findHVTGruppe4Standort(any(Long.class))).thenReturn(hvtGruppe);
        doReturn(hvtService).when(command).getCCService(HVTService.class);

        command.checkEndstelle4Anbindung(esB);

        // Everything is ok, if no exception is thrown due to checks throw exception on invalid data
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckEndstelle4AnbindungException() throws Exception {

        final Endstelle esA = mock(Endstelle.class);
        when(esA.isEndstelleA()).thenReturn(true);
        when(esA.isEndstelleB()).thenReturn(false);
        when(esA.getRangierId()).thenReturn(null);
        when(esA.getAnschlussart()).thenReturn(Anschlussart.ANSCHLUSSART_HVT);

        final CheckHVTAnbindungCommand command = spy(new CheckHVTAnbindungCommand());
        prepareDataForCommand(command, null, esA, null);

        doNothing().when(command).checkCarrierbestellung(any(Endstelle.class));
        doNothing().when(command).checkSourceAuftrag4TalNA(any(Endstelle.class));

        command.checkEndstelle4Anbindung(esA);
    }

    public void testCheckEndstelle4AnbindungNothing2Check() throws Exception {

        final Endstelle esA = mock(Endstelle.class);
        when(esA.isEndstelleA()).thenReturn(true);
        when(esA.isEndstelleB()).thenReturn(false);
        when(esA.getRangierId()).thenReturn(null);
        when(esA.getAnschlussart()).thenReturn(Anschlussart.ANSCHLUSSART_DIREKT);

        final CheckHVTAnbindungCommand command = spy(new CheckHVTAnbindungCommand());
        prepareDataForCommand(command, null, esA, null);

        doNothing().when(command).checkCarrierbestellung(any(Endstelle.class));
        doNothing().when(command).checkSourceAuftrag4TalNA(any(Endstelle.class));

        command.checkEndstelle4Anbindung(esA);

        // Everything is ok, if no exception is thrown due to checks throw exception on invalid data
    }

    private void prepareDataForCommand(CheckHVTAnbindungCommand command, Produkt produkt, Endstelle esA, Endstelle esB) {
        command.setProdukt(produkt);
        command.setEsA(esA);
        command.setEsB(esB);
    }
}
