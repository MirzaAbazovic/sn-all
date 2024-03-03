/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.14
 */

package de.mnet.wbci.service;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.model.CarrierCode;

/**
 * Responsible for the escalation overview report and carrier based excel reports via E-Mail.
 */
public interface WbciEscalationService extends WbciService {
    /**
     * Generate and send out a summary of the carrier specific escalation reports ({@link #sendCarrierSpecificEscalationReports()}.
     */
    void sendCarrierEscalationOverviewReport();

    /**
     * Generate and send out the internal overview report about the current deadlines.
     */
    void sendInternalOverviewReport();


    /**
     * Generate and send out the carrier based excel reports via E-Mail.
     *
     * @return list of carriers who have to receive a report email (should should contain all WBCI-Carriers)
     */
    List<CarrierCode> sendCarrierSpecificEscalationReports();

    /**
     * Creates and sends an escalation email to the partner carrier for the provided vorabstimmungsId.
     *
     * @param vorabstimmungsId
     */
    void sendVaEscalationMailToCarrier(String vorabstimmungsId, AKUser user);


}
