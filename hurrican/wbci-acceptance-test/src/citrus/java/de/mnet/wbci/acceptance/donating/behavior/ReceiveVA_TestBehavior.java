/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import static de.mnet.wbci.acceptance.common.role.AtlasEsbTestRole.*;

import java.util.*;
import com.consol.citrus.TestAction;
import org.springframework.util.StringUtils;

import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.role.HurricanTestRole;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending a VA to the donating carrier (M-Net):
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *      VA                  ->
 * </pre>
 */
public class ReceiveVA_TestBehavior extends AbstractTestBehavior {

    protected GeneratedTaifunData generatedTaifunData;
    private boolean searchBuildings = true;
    private boolean searchBuildingsDefault = true;
    private boolean klaerfall = false;
    private String klaerfallGrund = null;
    private int requestedCustomerDateOffset = 14;
    private CarrierCode aufnehmenderEKP = CarrierCode.DTAG;
    private boolean searchBuildingsAutoDetermination = false;
    private WbciGeschaeftsfall expectedWbciGeschaeftsfallForSearchBuilding;
    private GeschaeftsfallTyp geschaeftsfallTyp;
    private WbciGeschaeftsfallStatus expectedWbciGeschaeftsfallStatus = WbciGeschaeftsfallStatus.ACTIVE;
    private WbciRequestStatus expectedWbciRequestStatus = WbciRequestStatus.VA_EMPFANGEN;

    public ReceiveVA_TestBehavior() {
    }

    public ReceiveVA_TestBehavior(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    @Override
    public void apply() {
        // only generate vorabstimmungsId when not explicitly set before
        if (!StringUtils.hasText(getPreAgreementId())) {
            variable(VariableNames.PRE_AGREEMENT_ID, "wbci:createVorabstimmungsId()");
        }
        else {
            variable(VariableNames.PRE_AGREEMENT_ID, getPreAgreementId());
        }

        variable(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.MNET.getITUCarrierCode());
        variable(VariableNames.CARRIER_CODE_AUFNEHMEND, aufnehmenderEKP.getITUCarrierCode());
        variable(VariableNames.REQUESTED_CUSTOMER_DATE, String.format("wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', %s)", requestedCustomerDateOffset));

        GeschaeftsfallTyp gfTyp = (geschaeftsfallTyp == null) ? getGeschaeftsfallTyp() : geschaeftsfallTyp;

        writeToVariables(generatedTaifunData);
        
        atlas().sendCarrierChangeRequest(retrieveMessageTemplate(gfTyp));

        /**
         * If searchBuildingsAutoDetermination is enabled, the test case only expect a search building request if
         * {@link HurricanTestRole#determineIsSearchBuildingsExpected(WbciGeschaeftsfall)} set the variable
         * {@link VariableNames#IS_SEARCH_BUILDING_EXPECTED} to true.
         * This feature should stabilize some flabby tests
         */
        if (searchBuildingsAutoDetermination) {
            hurrican().determineIsSearchBuildingsExpected(expectedWbciGeschaeftsfallForSearchBuilding);
            conditional(
                    applyAndReturnSearchBuildingActions(searchBuildingsDefault)
            ).when(
                    String.format("${%s}", VariableNames.IS_SEARCH_BUILDING_EXPECTED)
            );
        }
        // hurricane uses the location service when processing VA requests of VA-KUE-MRN and VA-KUE-ORN.
        // if search buildings is enabled and kft test mode is disabled the default templates will be used
        // if phonetic search is enabled 2nd location search call is expected
        // if search buildings is disabled no location service call is expected
        else if (searchBuildings && !hurrican().isKftTestMode() && !gfTyp.equals(GeschaeftsfallTyp.VA_RRNP)) {
            applyAndReturnSearchBuildingActions(searchBuildingsDefault);
        }

        hurrican().assertIoArchiveEntryCreated(IOType.IN, gfTyp);
        hurrican().assertKlaerfallStatus(klaerfall, klaerfallGrund);
        hurrican().assertWbciIncomingRequestCreated();
        hurrican().assertWbciBaseRequestMetaDataSet(IOType.IN, expectedWbciGeschaeftsfallStatus);
        hurrican().assertVaRequestStatus(expectedWbciRequestStatus);
        hurrican().assertWechselterminIsNull();
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        if (expectedWbciGeschaeftsfallStatus.equals(WbciGeschaeftsfallStatus.ACTIVE)) {
            hurrican().assertVaAnswerDeadlineIsSet();
        }
    }

    private TestAction[] applyAndReturnSearchBuildingActions(boolean searchBuildingsDefault) {
        List<TestAction> actions = new ArrayList<>();
        if (searchBuildingsDefault) {
            actions.add(atlas().receiveAnyLocationSearch(LOCATION_SEARCH_BUILDINGS));
            actions.add(atlas().sendEmptyLocationSearchResponse());

            // receive 2nd location search when phonetic search is enabled
            if (hurrican().isPhoneticSearch()) {
                actions.add(atlas().receiveAnyLocationSearch(LOCATION_SEARCH_BUILDINGS));
                actions.add(atlas().sendEmptyLocationSearchResponse());
            }
        }
        else {
            actions.add(atlas().receiveLocationSearch("SEARCH_BUILDINGS_REQUEST", LOCATION_SEARCH_BUILDINGS));
            actions.add(atlas().sendLocationSearchResponse("SEARCH_BUILDINGS_RESPONSE"));
        }
        return actions.toArray(new TestAction[actions.size()]);
    }

    private String retrieveMessageTemplate(GeschaeftsfallTyp geschaeftsfallTyp) {
        switch (geschaeftsfallTyp) {
            case VA_KUE_MRN:
                return "VA_KUEMRN_ABG";
            case VA_KUE_ORN:
                return "VA_KUEORN_ABG";
            case VA_RRNP:
                return "VA_RRNP_ABG";
            default:
                throw new IllegalArgumentException(String.format("Unsupported GeschaeftsfallTyp '%s'",
                        geschaeftsfallTyp));
        }
    }

    /**
     * Enables/disables location search on behavior. When default search is disabled test must specify search request
     * and response templates.
     *
     * @param searchBuildings
     * @param defaultSearch
     * @return
     */
    public ReceiveVA_TestBehavior withSearchBuildings(boolean searchBuildings, boolean defaultSearch) {
        this.searchBuildings = searchBuildings;
        this.searchBuildingsDefault = defaultSearch;
        return this;
    }

    public ReceiveVA_TestBehavior withGeneratedTaifunData(final GeneratedTaifunData generatedTaifunData) {
        this.generatedTaifunData = generatedTaifunData;
        return this;
    }

    /**
     * Enables/disables location search on behavior. When default search is disabled test must specify search request
     * and response templates.
     *
     * @param defaultSearch use the default 'searchBuilding' pattern
     * @param expectedWbciGeschaeftsfallForSearchBuilding specifies a kind of search pattern for the search building request.
     *                                                    The wbciGeschaeftsfall should be contain at least a valid
     *                                                    {@link Rufnummernportierung} object.
     * @return
     */
    public ReceiveVA_TestBehavior withSearchBuildingsAutoDetermination(boolean defaultSearch, WbciGeschaeftsfall expectedWbciGeschaeftsfallForSearchBuilding) {
        this.searchBuildingsAutoDetermination = true;
        this.searchBuildingsDefault = defaultSearch;
        this.expectedWbciGeschaeftsfallForSearchBuilding = expectedWbciGeschaeftsfallForSearchBuilding;
        return this;
    }

    public ReceiveVA_TestBehavior withAufnehmenderEKP(CarrierCode aufnehmenderEKP) {
        this.aufnehmenderEKP = aufnehmenderEKP;
        return this;
    }

    public ReceiveVA_TestBehavior withExpectedWbciGeschaeftsfallStatus(WbciGeschaeftsfallStatus expectedWbciGeschaeftsfallStatus) {
        this.expectedWbciGeschaeftsfallStatus = expectedWbciGeschaeftsfallStatus;
        return this;
    }

    public ReceiveVA_TestBehavior withExpectedWbciRequestStatus(WbciRequestStatus expectedWbciRequestStatus) {
        this.expectedWbciRequestStatus = expectedWbciRequestStatus;
        return this;
    }
    
}
