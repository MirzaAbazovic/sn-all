/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus Test-Action, um einen WBCI Geschaeftsfall zu schliessen.
 */
public class CloseWbciGeschaeftsfallProcessingAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private String vorabstimmungsId;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseWbciGeschaeftsfallProcessingAction.class);

    public CloseWbciGeschaeftsfallProcessingAction(WbciCommonService wbciCommonService) {
        super("closeWbciGeschaeftsfallProcessingAction");
        this.wbciCommonService = wbciCommonService;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = this.vorabstimmungsId == null ? getVorabstimmungsId(context) : this.vorabstimmungsId;

        WbciRequest lastWbciRequest = wbciCommonService.findLastWbciRequest(vorabstimmungsId);
        if (lastWbciRequest != null) {
            LOGGER.info(String.format("Closing processing of wbci geschaeftsfall '%s'", vorabstimmungsId));
            wbciCommonService.closeProcessing(lastWbciRequest.getId());
        }
        else {
            LOGGER.warn(String.format("Unable to close processing of wbci geschaeftsfall '%s'", vorabstimmungsId));
        }
    }

    public CloseWbciGeschaeftsfallProcessingAction withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

}
