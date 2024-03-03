package de.mnet.wbci.model.helper;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

public class WbciRequestHelperTest {

    @DataProvider(name = "requests")
    public static Object[][] requests() {
        return new Object[][] {
                { Arrays.asList(new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new TerminverschiebungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        , true },
                { Arrays.asList(new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new TerminverschiebungsAnfrageTestBuilder<>().withRequestStatus(WbciRequestStatus.TV_ERLM_EMPFANGEN)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        , false },
                { Arrays.asList(new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new TerminverschiebungsAnfrageTestBuilder<>().withRequestStatus(WbciRequestStatus.TV_ERLM_EMPFANGEN)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new StornoAufhebungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        , true },
                { Arrays.asList(new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new TerminverschiebungsAnfrageTestBuilder<>().withRequestStatus(WbciRequestStatus.TV_ERLM_EMPFANGEN)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN),
                        new StornoAufhebungAufAnfrageTestBuilder<>().withRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN)
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        , false },
                { Arrays.asList(new VorabstimmungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        , false }
        };
    }

    @Test(dataProvider = "requests")
    public void testIsActiveStornoOrTvRequestIncluded(List<WbciRequest> requests, boolean activeTvOrStrono) throws Exception {
        assertEquals(WbciRequestHelper.isActiveStornoOrTvRequestIncluded(requests), activeTvOrStrono);
    }
}