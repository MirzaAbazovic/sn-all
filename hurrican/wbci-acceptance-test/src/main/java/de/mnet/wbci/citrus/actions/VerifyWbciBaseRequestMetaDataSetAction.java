/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus Test Action to verify whether the current {@link WbciRequest} has to necessary meta data defined.
 */
public class VerifyWbciBaseRequestMetaDataSetAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciGeschaeftsfallStatus expectedStatus;
    private IOType ioType;

    /**
     * @param wbciCommonService
     * @param expectedStatus      the {@link de.mnet.wbci.model.WbciGeschaeftsfall#getStatus()} to check
     * @param ioTypeOfBaseRequest defines if the meta data should be checked for incoming ({@code IOType.IN}) or
     *                            outgoing ({@code IOType.OUT}) {@link de.mnet.wbci.model.WbciRequest}s
     */
    public VerifyWbciBaseRequestMetaDataSetAction(WbciCommonService wbciCommonService, WbciGeschaeftsfallStatus expectedStatus,
            IOType ioTypeOfBaseRequest) {
        super("verifyWbciRequestMetaData");
        this.wbciCommonService = wbciCommonService;
        this.expectedStatus = expectedStatus;
        this.ioType = ioTypeOfBaseRequest;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);
        if (vorabstimmungsId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        final WbciRequest wbciRequest = wbciCommonService.findLastWbciRequest(vorabstimmungsId);
        Assert.assertNotNull(wbciRequest);
        Assert.assertNotNull(wbciRequest.getProcessedAt());
        Assert.assertNotNull(wbciRequest.getCreationDate());
        Assert.assertNotNull(wbciRequest.getUpdatedAt());
        Assert.assertTrue(isDateBeforeOrEqualTo(DateConverterUtils.asLocalDateTime(wbciRequest.getCreationDate()), DateConverterUtils.asLocalDateTime(wbciRequest.getProcessedAt())));
        Assert.assertTrue(isDateBeforeOrEqualTo(DateConverterUtils.asLocalDateTime(wbciRequest.getCreationDate()), DateConverterUtils.asLocalDateTime(wbciRequest.getUpdatedAt())));
        Assert.assertEquals(wbciRequest.getWbciGeschaeftsfall().getStatus(), expectedStatus);
        Assert.assertEquals(wbciRequest.getIoType(), ioType);
    }

    private boolean isDateBeforeOrEqualTo(LocalDateTime dateTime, LocalDateTime compareAgainst) {
        return dateTime.isBefore(compareAgainst) || dateTime.isEqual(compareAgainst);
    }

}
