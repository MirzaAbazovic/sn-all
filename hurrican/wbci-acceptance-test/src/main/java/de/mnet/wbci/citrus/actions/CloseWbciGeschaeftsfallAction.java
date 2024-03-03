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

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Citrus Test-Action, um einen WBCI Geschaeftsfall zu schliessen.
 */
public class CloseWbciGeschaeftsfallAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private String vorabstimmungsId;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseWbciGeschaeftsfallAction.class);

    public CloseWbciGeschaeftsfallAction(WbciCommonService wbciCommonService, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("closeWbciGeschaeftsfallAction");
        this.wbciCommonService = wbciCommonService;
        this.wbciGeschaeftsfallService = wbciGeschaeftsfallService;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = this.vorabstimmungsId == null ? getVorabstimmungsId(context) : this.vorabstimmungsId;

        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (wbciGeschaeftsfall != null) {
            LOGGER.info(String.format("Closing wbci geschaeftsfall '%s'", vorabstimmungsId));
            wbciGeschaeftsfallService.closeGeschaeftsfall(wbciGeschaeftsfall.getId());
        }
        else {
            LOGGER.warn(String.format("Unable to close wbci geschaeftsfall '%s'", vorabstimmungsId));
        }
    }

    public CloseWbciGeschaeftsfallAction withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

}
