/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.HurricanConstants;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import org.testng.Assert;

/**
 * Abstract WBCI test action provides access to WBCI specific test data such as the current WBCI geschaeftsfall object.
 */
public abstract class AbstractWbciTestAction extends AbstractTestAction {

    /**
     * Constructor setting the action name field.
     *
     * @param actionName
     */
    public AbstractWbciTestAction(String actionName) {
        setName(actionName);
    }

    @Override
    public void execute(TestContext context) {
        try {
            super.execute(context);
        }
        catch (Error e) {  // NOSONAR squid:S1181 ; errors should cause to fail the test!
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Loads from the business service the specific wbci geschaeftsfall object which is associated with test variables.
     *
     * @param context           the test context holding the test variables.
     * @param wbciCommonService wbci business service
     * @return a {@link WbciGeschaeftsfall}
     */
    protected WbciGeschaeftsfall getWbciGeschaeftsfall(TestContext context, WbciCommonService wbciCommonService) {
        String preAggrementId = (String) context.getVariableObject(VariableNames.PRE_AGREEMENT_ID);
        if (preAggrementId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(preAggrementId);
        if (wbciGeschaeftsfall == null) {
            throw new CitrusRuntimeException("Could not reload WbciGeschaeftsfall Object for VorabstimmungId " + preAggrementId);
        }
        return wbciGeschaeftsfall;
    }

    /**
     * Returns the linked StrAen geschaeftsfall object. This method should only be used when testing the STR-AEN use
     * case, where the first geschaeftsfall (GF1) is cancelled with a STR-AEN and a second geschaeftsfall (GF2) is
     * created, linked to GF1 (via {@link de.mnet.wbci.model.WbciGeschaeftsfall#getStrAenVorabstimmungsId()}) <br />
     * This method expects to find the vorabstimmungsId of GF2 stored in the {@link VariableNames#PRE_AGREEMENT_ID}
     * variable. After loading GF2 using this vorabstimmungsId, GF1 is then loaded using the the {@link
     * de.mnet.wbci.model.WbciGeschaeftsfall#getStrAenVorabstimmungsId()} from GF1.
     *
     * @param context           the test context holding the test variables.
     * @param wbciCommonService wbci business service
     * @return the original StrAen {@link WbciGeschaeftsfall}
     */
    protected WbciGeschaeftsfall getLinkedStrAenGeschaeftsfall(TestContext context, WbciCommonService wbciCommonService) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);

        final String strAenVorabstimmungsId = wbciGeschaeftsfall.getStrAenVorabstimmungsId();
        if (strAenVorabstimmungsId == null) {
            throw new CitrusRuntimeException(String.format("The original StrAen WbciGeschaeftsfall could not be loaded as the StrAenVorabstimmungId is NOT set for VorabstimmungId %s", wbciGeschaeftsfall.getVorabstimmungsId()));
        }

        final WbciGeschaeftsfall linkedWbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(strAenVorabstimmungsId);
        if (linkedWbciGeschaeftsfall == null) {
            throw new CitrusRuntimeException("Could not load WbciGeschaeftsfall Object for VorabstimmungId " + strAenVorabstimmungsId);
        }
        return linkedWbciGeschaeftsfall;
    }

    /**
     * Reads the preagreementId, associated with the test, the from test variables.
     *
     * @param context the test context holding the test variables.
     * @return
     */
    protected String getVorabstimmungsId(TestContext context) {
        return readMandatoryVariableFromTestContext(context, VariableNames.PRE_AGREEMENT_ID);
    }

    /**
     * Reads the stornoId, associated with the test, the from test variables.
     *
     * @param context the test context holding the test variables.
     * @return
     */
    protected String getStornoId(TestContext context) {
        return readMandatoryVariableFromTestContext(context, VariableNames.STORNO_ID);
    }

    /**
     * Reads the changeId (TV Id), associated with the test, the from test variables.
     *
     * @param context the test context holding the test variables.
     * @return
     */
    protected String getChangeId(TestContext context) {
        return readMandatoryVariableFromTestContext(context, VariableNames.CHANGE_ID);
    }

    /**
     * Reads the variable value from the test context.
     *
     * @param context      the test context holding the test variables.
     * @param variableName the name of the variable.
     * @return the non-null value of the variable
     */
    protected String readMandatoryVariableFromTestContext(TestContext context, String variableName) {
        final String variableValue = (String) context.getVariableObject(variableName);
        Assert.assertNotNull(variableValue, String.format("No non-null variable found in the test context matching the variable name '%s'", variableName));
        return variableValue;
    }

    /**
     * Retrieves the preagreement id from the test context. If no preagreement id found within the test context a
     * CitrusRuntimeException will be thrown.
     *
     * @param context the test context
     * @return the preagreement id
     */
    protected String retrievePreagreementId(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);
        if (vorabstimmungsId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID' is null, the test variable hast to be set in the TestContext");
        }
        return vorabstimmungsId;
    }

    protected AKUser getCitrusAkUser(Long userId) {
        return new AKUser(userId, "citrus", HurricanConstants.SYSTEM_USER, "test", null, null, null, null, null, true, null);
    }

    protected AKUser getCitrusAkUser() {
        return getCitrusAkUser(null);
    }
}
