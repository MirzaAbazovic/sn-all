/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public abstract class AbstractWitaWbciTestAction extends AbstractWitaTestAction {

    protected final WbciDao wbciDao;

    public AbstractWitaWbciTestAction(String actionName, WbciDao wbciDao) {
        super(actionName);
        this.wbciDao = wbciDao;
    }

    /**
     * Loads the specific wbci geschaeftsfall object using the wbci dao. The vorabstimmungsId is defined as a test
     * variable and should contained in the provided context.
     *
     * @param context the test context holding the test variables.
     * @return a {@link de.mnet.wbci.model.WbciGeschaeftsfall}
     */
    protected WbciGeschaeftsfall getWbciGeschaeftsfall(TestContext context) {
        String preAggrementId = (String) context.getVariableObject(PRE_AGREEMENT_ID);
        if (preAggrementId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(preAggrementId);
        if (wbciGeschaeftsfall == null) {
            throw new CitrusRuntimeException("Could not reload WbciGeschaeftsfall Object for VorabstimmungId " + preAggrementId);
        }
        return wbciGeschaeftsfall;
    }

    protected String createPreAgreementId(TestContext context, RequestTyp requestTyp) {
        String nextValueWithLeadingZeros = String.format("%08d", wbciDao.getNextSequenceValue(requestTyp));

        // z.B. 'DEU.MNET.VH12345678'
        final String vorabstimmungsId = String.format("%s.%s%s%s",
                CarrierCode.MNET.getITUCarrierCode(),
                requestTyp.getPreAgreementIdCode(),
                WbciConstants.VA_ROUTING_PREFIX_HURRICAN,
                nextValueWithLeadingZeros);
        if (context != null) {
            context.setVariable(PRE_AGREEMENT_ID, vorabstimmungsId);
        }
        return vorabstimmungsId;
    }

}
