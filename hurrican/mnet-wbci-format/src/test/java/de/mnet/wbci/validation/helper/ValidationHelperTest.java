/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.helper;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static org.mockito.Mockito.*;

import junit.framework.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1MeldungStorno;
import de.mnet.wbci.validation.groups.V1MeldungTv;
import de.mnet.wbci.validation.groups.V1MeldungVa;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrn;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnp;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1MeldungWarn;
import de.mnet.wbci.validation.groups.V1Request;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1RequestWarn;

@Test(groups = UNIT)
public class ValidationHelperTest {
    @InjectMocks
    private final ValidationHelper testling = new ValidationHelper();
    @Mock
    private WbciMessage wbciMessageMock;
    @Mock
    private WbciGeschaeftsfall wbciGeschaeftsfallMock;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] requestsAndExpectedErrorGroup() {
        // req-typ, gf-type, groups
        return new Object[][] {
                { RequestTyp.VA, GeschaeftsfallTyp.VA_RRNP, V1RequestVaRrnp.class },
                { RequestTyp.VA, GeschaeftsfallTyp.VA_KUE_MRN, V1RequestVaKueMrn.class },
                { RequestTyp.VA, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestVaKueOrn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestTvVaKueOrn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_KUE_MRN, V1RequestTvVaKueMrn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_RRNP, V1RequestTvVaRrnp.class },
                { RequestTyp.STR_AEN_AUF, GeschaeftsfallTyp.VA_KUE_ORN, V1Request.class },
                { RequestTyp.STR_AEN_ABG, GeschaeftsfallTyp.VA_KUE_ORN, V1Request.class },
                { RequestTyp.STR_AUFH_AUF, GeschaeftsfallTyp.VA_KUE_ORN, V1Request.class },
                { RequestTyp.STR_AUFH_ABG, GeschaeftsfallTyp.VA_KUE_ORN, V1Request.class },
                { RequestTyp.UNBEKANNT, GeschaeftsfallTyp.VA_UNBEKANNT, V1Request.class }
        };
    }

    @DataProvider(name = "meldungenAndExpectedErrorGroup")
    public Object[][] getMeldungenAndExpectedErrorGroup() {
        // medlung-typ, gf-type, groups
        return new Object[][] {
                { GeschaeftsfallTyp.VA_KUE_MRN, Erledigtmeldung.class, MeldungTyp.ERLM, V1MeldungVa.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, ErledigtmeldungStornoAen.class, MeldungTyp.ERLM, V1MeldungStorno.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, ErledigtmeldungStornoAuf.class, MeldungTyp.ERLM, V1MeldungStorno.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, ErledigtmeldungTerminverschiebung.class, MeldungTyp.ERLM, V1MeldungTv.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, Abbruchmeldung.class, MeldungTyp.ABBM, V1MeldungVa.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, AbbruchmeldungStornoAen.class, MeldungTyp.ABBM, V1MeldungStorno.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, AbbruchmeldungStornoAuf.class, MeldungTyp.ABBM, V1MeldungStorno.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, AbbruchmeldungTechnRessource.class, MeldungTyp.ABBM_TR, V1Meldung.class },
                { GeschaeftsfallTyp.VA_KUE_ORN, UebernahmeRessourceMeldung.class, MeldungTyp.AKM_TR, V1MeldungVaKueOrn.class },
                { GeschaeftsfallTyp.VA_RRNP, UebernahmeRessourceMeldung.class, MeldungTyp.AKM_TR, V1MeldungVaRrnp.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, UebernahmeRessourceMeldung.class, MeldungTyp.AKM_TR, V1MeldungVaKueMrn.class },
                { GeschaeftsfallTyp.VA_KUE_MRN, RueckmeldungVorabstimmung.class, MeldungTyp.RUEM_VA, V1MeldungVaKueMrn.class },
                { GeschaeftsfallTyp.VA_KUE_ORN, RueckmeldungVorabstimmung.class, MeldungTyp.RUEM_VA, V1MeldungVaKueOrn.class },
                { GeschaeftsfallTyp.VA_RRNP, RueckmeldungVorabstimmung.class, MeldungTyp.RUEM_VA, V1MeldungVaRrnp.class },
        };
    }

    @DataProvider
    public Object[][] requestsAndExpectedWarningGroup() {
        // req-typ, gf-type, groups
        return new Object[][] {
                { RequestTyp.VA, GeschaeftsfallTyp.VA_RRNP, V1RequestVaRrnpWarn.class },
                { RequestTyp.VA, GeschaeftsfallTyp.VA_KUE_MRN, V1RequestVaKueMrnWarn.class },
                { RequestTyp.VA, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestVaKueOrnWarn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestTvVaKueOrnWarn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_KUE_MRN, V1RequestTvVaKueMrnWarn.class },
                { RequestTyp.TV, GeschaeftsfallTyp.VA_RRNP, V1RequestTvVaRrnpWarn.class },
                { RequestTyp.STR_AEN_AUF, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestWarn.class },
                { RequestTyp.STR_AEN_ABG, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestWarn.class },
                { RequestTyp.STR_AUFH_AUF, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestWarn.class },
                { RequestTyp.STR_AUFH_ABG, GeschaeftsfallTyp.VA_KUE_ORN, V1RequestWarn.class },
                { RequestTyp.UNBEKANNT, GeschaeftsfallTyp.VA_UNBEKANNT, V1RequestWarn.class }
        };
    }

    @DataProvider(name = "meldungenAndExpectedWarningGroup")
    public Object[][] getMeldungenAndExpectedWarningGroup() {
        // meldung-typ, gf-type, groups
        return new Object[][] {
                { MeldungTyp.ERLM, GeschaeftsfallTyp.VA_KUE_MRN, V1MeldungWarn.class },
                { MeldungTyp.ABBM, GeschaeftsfallTyp.VA_KUE_MRN, V1MeldungWarn.class },
                { MeldungTyp.ABBM_TR, GeschaeftsfallTyp.VA_KUE_MRN, V1MeldungWarn.class },
                { MeldungTyp.AKM_TR, GeschaeftsfallTyp.VA_KUE_MRN, V1MeldungWarn.class },
                { MeldungTyp.RUEM_VA, GeschaeftsfallTyp.VA_KUE_MRN, V1MeldungVaKueMrnWarn.class },
                { MeldungTyp.RUEM_VA, GeschaeftsfallTyp.VA_RRNP, V1MeldungVaRrnpWarn.class }
        };
    }

    @Test(dataProvider = "meldungenAndExpectedErrorGroup")
    public void testMeldungErrorValidationGroups(GeschaeftsfallTyp gfTyp, Class<? extends Meldung> meldungClass, MeldungTyp meldungTyp, Class<?> expectedGroup) throws Exception {
        configureMocksForMeldung(meldungClass, meldungTyp, gfTyp);

        Class<?>[] groups = testling.getErrorValidationGroups(V1, wbciMessageMock);
        assertGroups(expectedGroup, groups);
    }

    @Test(dataProvider = "requestsAndExpectedErrorGroup")
    public void testRequestsErrorValidationGroups(RequestTyp requestTyp, GeschaeftsfallTyp geschaeftsfallTyp,
            Class<?> expectedGroup) throws Exception {
        configureMocksForRequest(requestTyp, geschaeftsfallTyp);

        Class<?>[] groups = testling.getErrorValidationGroups(V1, wbciMessageMock);
        assertGroups(expectedGroup, groups);
    }

    @Test(dataProvider = "meldungenAndExpectedWarningGroup")
    public void testMeldungWarningValidationGroups(MeldungTyp meldungTyp, GeschaeftsfallTyp geschaeftsfallTyp, Class<?> expectedGroup) throws Exception {
        configureMocksForMeldung(Meldung.class, meldungTyp, geschaeftsfallTyp);

        Class<?>[] groups = testling.getWarningValidationGroups(V1, wbciMessageMock);
        assertGroups(expectedGroup, groups);
    }

    @Test(dataProvider = "requestsAndExpectedWarningGroup")
    public void testRequestsWarningValidationGroups(RequestTyp requestTyp, GeschaeftsfallTyp geschaeftsfallTyp,
            Class<?> expectedGroup) throws Exception {
        configureMocksForRequest(requestTyp, geschaeftsfallTyp);

        Class<?>[] groups = testling.getWarningValidationGroups(V1, wbciMessageMock);
        assertGroups(expectedGroup, groups);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unsupported CDM-Version 'UNKNOWN'!")
    public void testUnsupportedCdmType() {
        testling.getWarningValidationGroups(UNKNOWN, wbciMessageMock);
    }

    @Test
    public void testUnsupportedRequestType() {
        when(wbciMessageMock.getTyp()).thenReturn(RequestTyp.STR_AEN_ABG);
        Class<?>[] groups = testling.getWarningValidationGroups(V1, wbciMessageMock);
        Assert.assertNull(groups);
    }

    private void assertGroups(Class<?> expectedGroup, Class<?>[] groups) {
        if (expectedGroup != null) {
            Assert.assertEquals(1, groups.length);
            Assert.assertEquals(expectedGroup, groups[0]);
        }
        else {
            Assert.assertNull(groups);
        }
    }

    private void configureMocksForMeldung(Class<? extends Meldung> meldungClass, MeldungTyp meldungTyp, GeschaeftsfallTyp gfTyp) {
        wbciMessageMock = Mockito.mock(meldungClass);
        when(wbciMessageMock.getTyp()).thenReturn(meldungTyp);
        when(wbciMessageMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getTyp()).thenReturn(gfTyp);
    }

    private void configureMocksForRequest(RequestTyp requestTyp, GeschaeftsfallTyp geschaeftsfallTyp) {
        wbciMessageMock = Mockito.mock(WbciRequest.class);
        when(wbciMessageMock.getTyp()).thenReturn(requestTyp);
        when(wbciMessageMock.getWbciGeschaeftsfall()).thenReturn(wbciGeschaeftsfallMock);
        when(wbciGeschaeftsfallMock.getTyp()).thenReturn(geschaeftsfallTyp);
    }
}
