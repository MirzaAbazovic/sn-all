/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Verifies that the assigned order of the current VA has a 'Bauauftrag' assigned with the given {@code anlassId}
 * and {@code bauauftragStatusId}
 */
public class VerifyBauauftragExistTestAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private VerlaufDAO verlaufDAO;
    private Long anlassId;
    private Long bauauftragStatusId;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyGfStatusAction.class);

    /**
     * @param wbciCommonService
     *
     */
    public VerifyBauauftragExistTestAction(WbciCommonService wbciCommonService, VerlaufDAO verlaufDAO, 
            Long anlassId, Long bauauftragStatusId) {
        super("verifyBauauftragExistTestAction");
        this.wbciCommonService = wbciCommonService;
        this.verlaufDAO = verlaufDAO;
        this.anlassId = anlassId;
        this.bauauftragStatusId = bauauftragStatusId;

    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);

        LOGGER.info("Validating Verlauf status");
        if (wbciGeschaeftsfall.getAuftragId() != null) {
            List<Verlauf> verlaeufe = verlaufDAO.findByAuftragId(wbciGeschaeftsfall.getAuftragId());
            if (CollectionUtils.isNotEmpty(verlaeufe)) {
                Assert.assertEquals(verlaeufe.size(), 1);
                Assert.assertEquals(verlaeufe.get(0).getAnlass(), anlassId);
                Assert.assertEquals(verlaeufe.get(0).getVerlaufStatusId(), bauauftragStatusId);

                LOGGER.info(String.format("Verlauf Anlass %s and status '%s' - value OK", anlassId, bauauftragStatusId));
            }   
            else {
                Assert.fail(String.format("No Bauauftrag found for GF %s!", wbciGeschaeftsfall.getVorabstimmungsId()));
            }
        }
        else {
            Assert.fail(String.format("WbciGF %s has no order assigned!", wbciGeschaeftsfall.getVorabstimmungsId()));
        }
    }

}
