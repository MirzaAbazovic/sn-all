/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2011 13:54:54
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class AbbmCbVorgangConverterTest extends BaseTest {

    @InjectMocks
    private final AbbmCbVorgangConverter abbmCbVorgangConverter = new AbbmCbVorgangConverter();

    @SuppressWarnings("unused")
    @Mock
    private CarrierElTALService carrierElTalService;
    @Mock
    private WitaUsertaskService witaUsertaskService;

    @BeforeClass
    public void initAbbmCbVorgangConverter() {
        MockitoAnnotations.initMocks(this);
    }

    public void readAbbmWithoutPositionsattribute() {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().build();
        String meldungspositionen = abbmCbVorgangConverter.readMeldungsPositionen(abbm);
        MeldungsPosition mpos = abbm.getMeldungsPositionen().iterator().next();
        assertTrue(meldungspositionen.contains(mpos.getMeldungsCode()));
        assertTrue(meldungspositionen.contains(mpos.getMeldungsText()));
    }

    public void readAbbmWithPositionsattribute() {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAnschlussPortierungKorrekt()
                .withPositionsattribute()
                .build();
        String meldungspositionen = abbmCbVorgangConverter.readMeldungsPositionen(abbm);
        MeldungsPosition mpos = abbm.getMeldungsPositionen().iterator().next();
        assertTrue(meldungspositionen.contains(mpos.getMeldungsCode()));
        assertTrue(meldungspositionen.contains(mpos.getMeldungsText()));
        assertTrue(meldungspositionen.contains(mpos.getPositionsattribute().getAlternativprodukt()));
        assertTrue(meldungspositionen.contains(mpos.getPositionsattribute().getFehlauftragsnummer()));
    }

    public void doCloseTamUserTaskOnDefaultAbbm() throws StoreException {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().build();
        WitaCBVorgang witaCbVorgang = new WitaCBVorgang();
        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setWiedervorlageAm(new Date());
        witaCbVorgang.setTamUserTask(tamUserTask);

        abbmCbVorgangConverter.write(witaCbVorgang, abbm);

        // assertEquals(witaCbVorgang.getTamUserTask().getStatus(), UserTaskStatus.GESCHLOSSEN);
        verify(witaUsertaskService).closeUserTask(tamUserTask);
        verifyNoMoreInteractions(witaUsertaskService);
        assertNull(tamUserTask.getWiedervorlageAm());
    }

    public void closeTamUserTaskWhenAuftragAusgefuehrt() throws StoreException {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().addMeldungsposition(
                new MeldungsPosition(AbbmMeldungsCode.AUFTRAG_AUSGEFUEHRT_TV_NOT_POSSIBLE.meldungsCode, "")).build();
        WitaCBVorgang witaCbVorgang = new WitaCBVorgang();
        TamUserTask tamUserTask = new TamUserTask();
        witaCbVorgang.setTamUserTask(tamUserTask);

        abbmCbVorgangConverter.write(witaCbVorgang, abbm);

        // assertEquals(witaCbVorgang.getTamUserTask().getStatus(), UserTaskStatus.GESCHLOSSEN);
        verify(witaUsertaskService).closeUserTask(tamUserTask);
        verifyNoMoreInteractions(witaUsertaskService);
    }

}
