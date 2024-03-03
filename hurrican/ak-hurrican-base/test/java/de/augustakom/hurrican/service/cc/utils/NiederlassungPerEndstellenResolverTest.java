/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2011 09:17:13
 */
package de.augustakom.hurrican.service.cc.utils;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;

/**
 * Testklasse fuer {@link NiederlassungPerEndstellenResolver}.
 *
 *
 * @since Release 10
 */
@Test(groups = { BaseTest.UNIT })
public class NiederlassungPerEndstellenResolverTest extends BaseTest {

    private NiederlassungPerEndstellenResolver cut;

    @BeforeTest
    void instantiateClassUnderTest() {
        cut = new NiederlassungPerEndstellenResolver();
    }

    public void findHvtGruppeWithReferenceToNiederlassung_NoStandortId() throws FindException, ServiceNotFoundException {
        HVTGruppe hvtGruppe = cut.findHvtGruppeWithReferenceToNiederlassung(null);
        assertNull(hvtGruppe);
    }

    public void findHvtGruppeWithReferenceToNiederlassung_NoHVTGruppeForStandortIdFound() throws FindException,
            ServiceNotFoundException {
        HVTService mock = mock(HVTService.class);
        Long standortId = 12L;
        when(mock.findHVTGruppe4Standort(standortId)).thenReturn(null);
        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(mock).when(spy).getHvtService();
        HVTGruppe hvtGruppe = spy.findHvtGruppeWithReferenceToNiederlassung(standortId);
        assertNull(hvtGruppe);
    }

    public void findHvtGruppeWithReferenceToNiederlassung_HVTGruppeFoundWithoutNiederlassungId() throws FindException,
            ServiceNotFoundException {
        HVTService mock = mock(HVTService.class);
        Long standortId = 12L;
        when(mock.findHVTGruppe4Standort(standortId)).thenReturn(new HVTGruppe());
        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(mock).when(spy).getHvtService();
        HVTGruppe hvtGruppe = spy.findHvtGruppeWithReferenceToNiederlassung(standortId);
        assertNull(hvtGruppe);
    }

    /**
     * Testmethode fuer {@link NiederlassungPerEndstellenResolver#findHvtGruppeWithReferenceToNiederlassung(Long)} .
     * Findet eine korrekte {@link HVTGruppe} fuer die gegebene StandortId, die auch eine Niederlassung referenziert.
     */
    public void findHvtGruppeWithReferenceToNiederlassung_HVTGruppeFoundWithNiederlassungId() throws FindException,
            ServiceNotFoundException {
        HVTService mock = mock(HVTService.class);
        Long standortId = 12L;
        HVTGruppe hvtGruppe = new HVTGruppe();
        final Long niederlassungId = 1234L;
        hvtGruppe.setNiederlassungId(niederlassungId);
        when(mock.findHVTGruppe4Standort(standortId)).thenReturn(hvtGruppe);
        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(mock).when(spy).getHvtService();
        HVTGruppe result = spy.findHvtGruppeWithReferenceToNiederlassung(standortId);
        assertNotNull(result);
        assertEquals(result.getNiederlassungId(), niederlassungId);
    }

    public void findSite4HVTStandortId_NoProperHVTGruppeFound() throws FindException, ServiceNotFoundException {
        Long niederlassungId = 12L;
        NiederlassungService mock = mock(NiederlassungService.class);
        when(mock.findNiederlassung(niederlassungId)).thenReturn(null);
        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(mock).when(spy).getNiederlassungService();
        doReturn(null).when(spy).findHvtGruppeWithReferenceToNiederlassung(Mockito.anyLong());
        Niederlassung niederlassung = spy.findSite4HVTStandortId(niederlassungId);
        assertNull(niederlassung);
    }

    public void findSite4HVTStandortId_NoNiederlassungFound() throws FindException, ServiceNotFoundException {
        Long niederlassungId = 12L;
        NiederlassungService mock = mock(NiederlassungService.class);
        when(mock.findNiederlassung(niederlassungId)).thenReturn(null);
        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(mock).when(spy).getNiederlassungService();
        HVTGruppe hvtGruppe = new HVTGruppe();
        hvtGruppe.setNiederlassungId(niederlassungId);
        doReturn(hvtGruppe).when(spy).findHvtGruppeWithReferenceToNiederlassung(Mockito.anyLong());
        Niederlassung niederlassung = spy.findSite4HVTStandortId(niederlassungId);
        assertNull(niederlassung);
    }

    public void findSite4HVTStandortId_NiederlassungFound() throws FindException, ServiceNotFoundException {
        Long niederlassungId = 12L;
        NiederlassungPerEndstellenResolver spy = spy(cut);
        HVTGruppe hvtGruppe = new HVTGruppe();
        hvtGruppe.setNiederlassungId(niederlassungId);
        doReturn(hvtGruppe).when(spy).findHvtGruppeWithReferenceToNiederlassung(Mockito.anyLong());

        NiederlassungService mock = mock(NiederlassungService.class);
        doReturn(mock).when(spy).getNiederlassungService();
        Niederlassung niederlassung = new Niederlassung();
        when(mock.findNiederlassung(niederlassungId)).thenReturn(niederlassung);

        Niederlassung result = spy.findSite4HVTStandortId(niederlassungId);
        assertNotNull(result);
        assertEquals(result, niederlassung);
    }

    public void findSite4Endstellen_EndstellenNull() throws FindException, ServiceNotFoundException {
        Niederlassung result = cut.findSite4Endstellen(null);
        assertNull(result);
    }

    public void findSite4Endstellen_EndstellenEmpty() throws FindException, ServiceNotFoundException {
        Niederlassung result = cut.findSite4Endstellen(Collections.<Endstelle>emptyList());
        assertNull(result);
    }

    @Test(groups = { "unit" })
    public void findSite4Endstellen_NoEndstelleOfTypeB() throws FindException, ServiceNotFoundException {
        Endstelle endstelleNotOfTypeB = new Endstelle();
        endstelleNotOfTypeB.setEndstelleTyp("XXXXX");
        Niederlassung result = cut.findSite4Endstellen(Arrays.asList(endstelleNotOfTypeB));
        assertNull(result);
    }

    public void findSite4Endstellen_NoNiederlassungFoundForEndstelleOfTypeB() throws FindException,
            ServiceNotFoundException {
        Endstelle endstelleOfTypeB = new Endstelle();
        endstelleOfTypeB.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        NiederlassungPerEndstellenResolver spy = spy(cut);
        doReturn(null).when(spy).findSite4HVTStandortId(Mockito.anyLong());

        Niederlassung result = spy.findSite4Endstellen(Arrays.asList(endstelleOfTypeB));
        assertNull(result);
    }

    public void findSite4Endstellen_NiederlassungFoundForEnstelleOfTypeB() throws FindException,
            ServiceNotFoundException {
        Endstelle endstelleOfTypeB = new Endstelle();
        endstelleOfTypeB.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        NiederlassungPerEndstellenResolver spy = spy(cut);
        Niederlassung niederlassung = new Niederlassung();
        doReturn(niederlassung).when(spy).findSite4HVTStandortId(Mockito.anyLong());

        Niederlassung result = spy.findSite4Endstellen(Arrays.asList(endstelleOfTypeB));
        assertNotNull(result);
        assertEquals(result, niederlassung);
    }

    public void findSite4Auftrag_AuftragIdNull() throws FindException, ServiceNotFoundException {
        Niederlassung result = cut.findSite4Auftrag(null);
        assertNull(result);
    }

} // end

