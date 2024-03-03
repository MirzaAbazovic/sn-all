/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import org.springframework.util.StringUtils;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending a Terminverschiebung to the donating carrier (M-Net).
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *      TV                  ->
 * </pre>
 */
public class ReceiveTV_TestBehavior extends AbstractTestBehavior {

    private WbciRequestStatus expectedRequestStatus = WbciRequestStatus.TV_EMPFANGEN;
    private String changeId;
    private boolean skipUpdatedKundenwunschterminCheck = false;
    private boolean skipAnswerDeadlineCheck = false;
    private boolean skipTvRequestChecks = false;
    private boolean skipGeschaeftsfallChecks = false;
    private boolean expectedKlaerfall = false;

    public ReceiveTV_TestBehavior() {
    }

    public ReceiveTV_TestBehavior withChangeId(String changeId) {
        this.changeId = changeId;
        return this;
    }

    public ReceiveTV_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    /**
     * The updated Kundenwunschtermin check should be skipped if the auto-processing feature is turned off.
     *
     * @return
     */
    public ReceiveTV_TestBehavior withSkipUpdatedKundenwunschterminCheck() {
        this.skipUpdatedKundenwunschterminCheck = true;
        return this;
    }

    /**
     * The AnswerDeadline check should be disabled when the TV is automatically processed or when the TV is not
     * persisted by hurrican (as its an invalid request)
     *
     * @return
     */
    public ReceiveTV_TestBehavior withSkipAnswerDeadlineCheck() {
        this.skipAnswerDeadlineCheck = true;
        return this;
    }

    /**
     * The TV request checks (deadline, request status, kundenwunschtermin) should be skipped. This is typically
     * required when an invalid TV request is sent to hurrican, which does not get persisted in the hurrican database.
     *
     * @return
     */
    public ReceiveTV_TestBehavior withSkipTvRequestChecks() {
        this.skipTvRequestChecks = true;
        return this;
    }

    /**
     * The TV request checks (deadline, request status, kundenwunschtermin) should be skipped. This is typically
     * required when an invalid TV request is sent to hurrican, which does not get persisted in the hurrican database.
     *
     * @return
     */
    public ReceiveTV_TestBehavior withSkipGeschaeftsfallChecks() {
        this.skipGeschaeftsfallChecks = true;
        return this;
    }

    public String getChangeId() {
        return changeId;
    }

    @Override
    public void apply() {
        // only generate aenderungsId when not explicitly set before
        if (!StringUtils.hasText(getChangeId())) {
            variables().add(VariableNames.CHANGE_ID, "wbci:createVorabstimmungsId('DEU.DTAG', 'T')");
        }
        else {
            variables().add(VariableNames.CHANGE_ID, getChangeId());
        }

        variables().add(VariableNames.RESCHEDULED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 21)");

        atlas().sendCarrierChangeReschedule("TV");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.TV);

        if (!skipGeschaeftsfallChecks) {
            hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        }

        // tv request checks
        if (!skipTvRequestChecks) {
            hurrican().assertTvRequestStatus(expectedRequestStatus);
            if (!skipUpdatedKundenwunschterminCheck) {
                hurrican().assertKundenwunschtermin(VariableNames.RESCHEDULED_CUSTOMER_DATE, RequestTyp.TV);
            }

            hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);
            if (!skipAnswerDeadlineCheck) {
                hurrican().assertTvAnswerDeadlineIsSet();
            }
        }
    }

}
