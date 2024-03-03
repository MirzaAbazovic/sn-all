/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.14
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the {@link AuftragDaten} object linked with the current {@link WbciGeschaeftsfall},
 * with the help of the VorabstimmungsID and verifies that the AuftragDaten-Status
 * matches the expected status.
 */
public class VerifyAuftragDatenStatusAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private AuftragDatenDAO auftragDatenDAO;
    private Long expectedStatus;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyGfStatusAction.class);

    /**
     * @param wbciCommonService
     * @param auftragDatenDAO
     * @param expectedStatus the Id of the expected {@link AuftragStatus}
     */
    public VerifyAuftragDatenStatusAction(WbciCommonService wbciCommonService, AuftragDatenDAO auftragDatenDAO, Long expectedStatus) {
        super("verifyGFStatus");
        this.wbciCommonService = wbciCommonService;
        this.auftragDatenDAO = auftragDatenDAO;
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);
        final AuftragDaten auftragDaten = auftragDatenDAO.findByAuftragId(wbciGeschaeftsfall.getAuftragId());

        LOGGER.info("Validating AuftragDaten status");
        Assert.assertEquals(auftragDaten.getStatusId(), expectedStatus);
        LOGGER.info(String.format("AuftragDaten status '%s' - value OK", expectedStatus));
    }
    
}
