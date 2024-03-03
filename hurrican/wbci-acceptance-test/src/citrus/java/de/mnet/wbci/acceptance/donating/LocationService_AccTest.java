/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.util.*;
import com.consol.citrus.actions.EchoAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.role.AtlasEsbTestRole;
import de.mnet.wbci.citrus.ResponseCallback;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.StandortTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class LocationService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests location service interface called by Hurrican on Altas ESB. No specific search strategy is specified in the
     * search request and the location service returns a single match in the search response.
     * <p/>
     * <pre>
     *     Hurrican              AtlasESB
     *     SearchBuildings  ->
     *                      <-   SearchBuildingsResponse
     * </pre>
     */
    @CitrusTest
    public void LocationService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.LocationService_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        hurrican().createWbciLocationSearch(new StandortTestBuilder().buildValid(WBCI_CDM_VERSION, geschaeftsfallTyp))
                .withResponseCallback(new ResponseCallback<List<Long>>() {
                    public void doWithResponse(List<Long> responseObject, TestContext context) {
                        // set result variable that is validated at the very end of the test case
                        context.setVariable("locationGeoIds", responseObject);
                    }
                });

        atlas().receiveLocationSearch("SEARCH_REQUEST", AtlasEsbTestRole.LOCATION_SEARCH_BUILDINGS);

        atlas().sendLocationSearchResponse("SEARCH_RESPONSE");

        // repeat location result validation which is set by synchronous response callback
        repeatOnError(
                new EchoAction() {
                    public void doExecute(TestContext context) {
                        List<Long> geoIds = (List<Long>) context.getVariables().get("locationGeoIds");
                        if (geoIds != null && geoIds.size() == 1) {
                            setMessage("Location geoIds just as expected: " + geoIds + " - All values OK");
                            super.doExecute(context);
                        }
                        else {
                            throw new CitrusRuntimeException("Location geoIds not as expected: " + geoIds);
                        }
                    }
                }
        ).autoSleep(1000L).index("i").until("i gt 10");
    }

    /**
     * Tests location service interface called by Hurrican on Altas ESB. This time first exact match query has no
     * results and a second phonetic search query is performed, since this feature is active.
     * <p/>
     * <pre>
     *     Hurrican              AtlasESB
     *     SearchBuildings  ->
     *                      <-   SearchBuildingsResponse
     *     SearchBuildings  ->
     *                      <-   SearchBuildingsResponse
     * </pre>
     */
    @CitrusTest
    public void LocationService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.LocationService_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        hurrican().setWbciPhoneticSearchFeature(true);

        hurrican().createWbciLocationSearch(new StandortTestBuilder().buildValid(WBCI_CDM_VERSION, geschaeftsfallTyp))
                .withResponseCallback(new ResponseCallback<List<Long>>() {
                    public void doWithResponse(List<Long> responseObject, TestContext context) {
                        // set result variable that is validated at the very end of the test case
                        context.setVariable("locationGeoIds", responseObject);
                    }
                });

        atlas().receiveLocationSearch("SEARCH_REQUEST", AtlasEsbTestRole.LOCATION_SEARCH_BUILDINGS);

        atlas().sendLocationSearchResponse("SEARCH_RESPONSE");

        atlas().receiveLocationSearch("SEARCH_REQUEST_PHONETIC", AtlasEsbTestRole.LOCATION_SEARCH_BUILDINGS);

        atlas().sendLocationSearchResponse("SEARCH_RESPONSE_PHONETIC");

        // repeat location result validation which is set by synchronous response callback
        repeatOnError(
                new EchoAction() {
                    public void doExecute(TestContext context) {
                        List<Long> geoIds = (List<Long>) context.getVariables().get("locationGeoIds");
                        if (geoIds != null && geoIds.size() == 2) {
                            setMessage("Location geoIds just as expected: " + geoIds + " - All values OK");
                            super.doExecute(context);
                        }
                        else {
                            throw new CitrusRuntimeException("Location geoIds not as expected: " + geoIds);
                        }
                    }
                }
        ).autoSleep(1000L).index("i").until("i gt 10");
    }


    /**
     * Tests the validation of an incoming SearchBuildingsRequest
     * <p/>
     * <pre>
     *     Hurrican              AtlasESB
     *     SearchBuildings  ->
     *                      <-   SearchBuildingsResponse
     *                           should result in an ExceptionLog entry because Response is invalid
     * </pre>
     */
    @CitrusTest
    public void LocationService_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.LocationService_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        hurrican().saveLatestExceptionLogEntryId(ExceptionLogEntryContext.WBCI_LOCATION_SEARCH_RESPONSE_ERROR);

        hurrican().createWbciLocationSearch(new StandortTestBuilder().buildValid(WBCI_CDM_VERSION, geschaeftsfallTyp))
                .withResponseCallback(new ResponseCallback<List<Long>>() {
                    public void doWithResponse(List<Long> responseObject, TestContext context) {
                        // set result variable that is validated at the very end of the test case
                        context.setVariable("locationGeoIds", responseObject);
                    }
                });

        atlas().receiveLocationSearch("SEARCH_REQUEST", AtlasEsbTestRole.LOCATION_SEARCH_BUILDINGS);

        atlas().sendLocationSearchResponse("SEARCH_RESPONSE");

        sleep(2000L);

        hurrican().assertWbciExceptionLogEntryCreated(ExceptionLogEntryContext.WBCI_LOCATION_SEARCH_RESPONSE_ERROR);
    }

}
