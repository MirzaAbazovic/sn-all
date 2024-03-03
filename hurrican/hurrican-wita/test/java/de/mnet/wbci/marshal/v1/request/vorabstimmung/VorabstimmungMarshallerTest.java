/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import java.text.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.VorabstimmungType;
import de.mnet.wbci.marshal.v1.request.AbstactAnfrageMarshallerMock;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class VorabstimmungMarshallerTest extends AbstactAnfrageMarshallerMock {
    @InjectMocks
    private VorabstimmungMarshaller<WbciGeschaeftsfall, VorabstimmungType> testling = new VorabstimmungMarshaller<WbciGeschaeftsfall, VorabstimmungType>() {
        public VorabstimmungType apply(@Nullable WbciGeschaeftsfall input) {
            return super.apply(new KuendigungMitRNPGeschaeftsfallType(), input);
        }
    };

    private LocalDateTime expectedKundenwunschtermin;
    private WbciGeschaeftsfall wbciGeschaeftsfall;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        expectedKundenwunschtermin = LocalDateTime.now().plusDays(7);
        wbciGeschaeftsfall.setKundenwunschtermin(expectedKundenwunschtermin.toLocalDate());
        initMockHandlingVorabstimmung(wbciGeschaeftsfall);
    }

    @Test
    public void testApply() throws Exception {
        VorabstimmungType testlingResult = testling.apply(wbciGeschaeftsfall);

        Assert.assertNotNull(testlingResult.getKundenwunschtermin());
        DateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertEquals(testlingResult.getKundenwunschtermin().toString(),
                requiredFormat.format(Date.from(expectedKundenwunschtermin.atZone(ZoneId.systemDefault()).toInstant())));
        Assert.assertNotNull(testlingResult.getVorabstimmungsId());
        Assert.assertEquals(testlingResult.getVorabstimmungsId(), wbciGeschaeftsfall.getVorabstimmungsId());
        Assert.assertEquals(testlingResult.getWeitereAnschlussinhaber(), expectedPersonOderFirmaList);
        Assert.assertEquals(testlingResult.getEndkunde(), expectedPersonOderFirma);
        Assert.assertEquals(testlingResult.getProjektId(), expectedProjekt);
    }

}
