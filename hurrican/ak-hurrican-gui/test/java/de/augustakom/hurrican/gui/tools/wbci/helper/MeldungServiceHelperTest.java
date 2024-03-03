/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import static org.mockito.Mockito.*;

import java.time.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.service.WbciMeldungService;

@Test(groups = BaseTest.UNIT)
public class MeldungServiceHelperTest {
    @Test
    public void testCreateTvErledigtmeldung() throws Exception {
        WbciMeldungService wbciMeldungServiceMock = Mockito.mock(WbciMeldungService.class);
        final String vaId = "123";
        final LocalDate wechselTermin = LocalDate.now();
        final String aenderungsId = "234";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Assert.assertNotNull(args[0]);
                Assert.assertTrue(args[0] instanceof Erledigtmeldung);
                Erledigtmeldung erledigtmeldung = (Erledigtmeldung) args[0];

                Assert.assertEquals(erledigtmeldung.getWechseltermin(), wechselTermin);
                Assert.assertEquals(erledigtmeldung.getVorabstimmungsIdRef(), vaId);
                Assert.assertEquals(erledigtmeldung.getAenderungsIdRef(), aenderungsId);

                Assert.assertEquals(erledigtmeldung.getMeldungsPositionen().size(), 1L);
                Assert.assertEquals(erledigtmeldung.getMeldungsPositionen().iterator().next().getMeldungsCode(), MeldungsCode.TV_OK);

                return null;
            }
        }).when(wbciMeldungServiceMock).createAndSendWbciMeldung(any(Erledigtmeldung.class), eq(vaId));

        MeldungServiceHelper.createTvErledigtmeldung(wbciMeldungServiceMock, vaId, wechselTermin, aenderungsId);
    }

    @Test
    public void testCreateStornoErledigtmeldung() throws Exception {
        WbciMeldungService wbciMeldungServiceMock = Mockito.mock(WbciMeldungService.class);
        final String vaId = "123";
        final String stornoId = "234";
        final RequestTyp requestTyp = RequestTyp.STR_AEN_AUF;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Assert.assertNotNull(args[0]);
                Assert.assertTrue(args[0] instanceof Erledigtmeldung);
                Erledigtmeldung erledigtmeldung = (Erledigtmeldung) args[0];

                Assert.assertEquals(erledigtmeldung.getClass(), ErledigtmeldungStornoAen.class);
                Assert.assertNull(erledigtmeldung.getWechseltermin());
                Assert.assertEquals(erledigtmeldung.getVorabstimmungsIdRef(), vaId);
                Assert.assertEquals(erledigtmeldung.getStornoIdRef(), stornoId);

                Assert.assertEquals(erledigtmeldung.getMeldungsPositionen().size(), 1L);
                Assert.assertEquals(erledigtmeldung.getMeldungsPositionen().iterator().next().getMeldungsCode(), MeldungsCode.STORNO_OK);

                return null;
            }
        }).when(wbciMeldungServiceMock).createAndSendWbciMeldung(any(Erledigtmeldung.class), eq(vaId));

        MeldungServiceHelper.createStornoErledigtmeldung(wbciMeldungServiceMock, vaId, stornoId, requestTyp);
    }
}
