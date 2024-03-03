/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.13
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wita.IOArchiveProperties.IOSource;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.IoArchiveBuilder;

@Test(groups = UNIT)
public class IoArchiveRenderDataTest {

    private final IoArchiveRenderData testling = new IoArchiveRenderData();

    @Test
    public void shouldReturnGeschaeftsfallDisplayNameForWitaArchiveEntry() throws Exception {
        String requestGeschaeftsfall = de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG.name();
        String expectedDisplayName = de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG.getDisplayName();

        IoArchive ioArchiveMock = mock(IoArchive.class);
        when(ioArchiveMock.getRequestMeldungstyp()).thenReturn("");
        when(ioArchiveMock.getRequestGeschaeftsfall()).thenReturn(requestGeschaeftsfall);
        when(ioArchiveMock.getIoSource()).thenReturn(IOSource.WITA);
        String displayName = testling.getDisplayName(ioArchiveMock);
        Assert.assertEquals(displayName, expectedDisplayName);
    }

    @Test
    public void getTooltipTextWbci() {
        MeldungsType meldungsType = MeldungsType.ABM;
        IoArchive ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WITA)
                .withRequestMeldungstyp(meldungsType.name())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), meldungsType.getLongName());
        meldungsType = MeldungsType.STORNO;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WITA)
                .withRequestMeldungstyp(meldungsType.getLongName())
                .build();
        Assert.assertNull(testling.getTooltipText(ioArchive));
        meldungsType = MeldungsType.TV;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WITA)
                .withRequestMeldungstyp(meldungsType.getLongName())
                .build();
        Assert.assertNull(testling.getTooltipText(ioArchive));
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WITA)
                .withRequestMeldungstyp("")
                .build();
        Assert.assertNull(testling.getTooltipText(ioArchive));
    }

    @Test
    public void testWbci() throws Exception {
        GeschaeftsfallTyp type = GeschaeftsfallTyp.VA_KUE_MRN;

        RequestTyp requestTyp = RequestTyp.VA;
        IoArchive ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(requestTyp.name())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), type.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), type.getShortName());

        requestTyp = RequestTyp.STR_AEN_ABG;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(requestTyp.name())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), requestTyp.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), requestTyp.getShortName());

        requestTyp = RequestTyp.TV;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(requestTyp.name())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), requestTyp.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), requestTyp.getShortName());

        MeldungTyp meldungsType = MeldungTyp.AKM_TR;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(meldungsType.getShortName())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), meldungsType.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), meldungsType.getShortName());

        meldungsType = MeldungTyp.ABBM;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(meldungsType.getShortName())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), meldungsType.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), meldungsType.getShortName());

        meldungsType = MeldungTyp.ERLM;
        ioArchive = new IoArchiveBuilder()
                .withIoSource(IOSource.WBCI)
                .withRequestGeschaeftsfall(type)
                .withRequestMeldungstyp(meldungsType.getShortName())
                .build();
        Assert.assertEquals(testling.getTooltipText(ioArchive), meldungsType.getLongName());
        Assert.assertEquals(testling.getDisplayName(ioArchive), meldungsType.getShortName());

    }
}
