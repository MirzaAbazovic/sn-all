/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import org.testng.Assert;

import com.consol.citrus.context.TestContext;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciCommonService;

/**
 * AbstractRequestAction which retrieves the WbciRequest, with the help of the requestTyp and the
 * VorabstimmungsID/ChangeID/StornoID from the TestContext.
 *
 *
 */
public abstract class AbstractRequestAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private RequestTyp requestTyp;

    public AbstractRequestAction(String actionName, WbciCommonService wbciCommonService, RequestTyp requestTyp) {
        super(actionName);
        this.wbciCommonService = wbciCommonService;
        this.requestTyp = requestTyp;
    }

    protected WbciRequest retrieve(TestContext context) {
        switch (requestTyp) {
            case VA:
                return retrieveVa(context);
            case TV:
                return retrieveTv(context);
            case STR_AEN_ABG:
            case STR_AEN_AUF:
            case STR_AUFH_ABG:
            case STR_AUFH_AUF:
                return retrieveStorno(context);
            default:
                throw new IllegalArgumentException(String.format("Unsupported request typ '%s", requestTyp));
        }
    }

    private VorabstimmungsAnfrage retrieveVa(TestContext context) {
        final String vorabstimmungsId = retrievePreagreementId(context);

        final List<VorabstimmungsAnfrage> vaRequests = wbciCommonService.findWbciRequestByType(vorabstimmungsId,
                VorabstimmungsAnfrage.class);
        Assert.assertNotNull(vaRequests);
        Assert.assertEquals(vaRequests.size(), 1);
        return vaRequests.get(0);
    }

    private StornoAnfrage retrieveStorno(TestContext context) {
        final String vorabstimmungsId = retrievePreagreementId(context);

        String changeId = context.getVariable(VariableNames.STORNO_ID);
        Assert.assertNotNull(changeId, String.format("Cannot verify WbciRequest status as no variable matching '%s' was found within the citrus test context", VariableNames.STORNO_ID));

        final WbciRequest wbciRequest = wbciCommonService.findWbciRequestByChangeId(vorabstimmungsId, changeId);
        Assert.assertNotNull(wbciRequest);
        Assert.assertTrue(wbciRequest instanceof StornoAnfrage);
        return (StornoAnfrage) wbciRequest;
    }

    private TerminverschiebungsAnfrage retrieveTv(TestContext context) {
        final String vorabstimmungsId = retrievePreagreementId(context);

        String changeId = context.getVariable(VariableNames.CHANGE_ID);
        Assert.assertNotNull(changeId, String.format("Cannot verify WbciRequest status as no variable matching '%s' was found within the citrus test context", VariableNames.CHANGE_ID));

        final WbciRequest wbciRequest = wbciCommonService.findWbciRequestByChangeId(vorabstimmungsId, changeId);
        Assert.assertNotNull(wbciRequest,
                String.format("Unable to find any TV requests for VorabstimmungsId '%s' and AenderungsId '%s'", vorabstimmungsId, changeId));

            Assert.assertTrue(wbciRequest instanceof TerminverschiebungsAnfrage,
                    String.format("Found request other than TV for VorabstimmungsId '%s' and AenderungsId '%s'", vorabstimmungsId, changeId));

        // return latest tv request (list is ordered by creation date descending)
        return (TerminverschiebungsAnfrage) wbciRequest;
    }

}
