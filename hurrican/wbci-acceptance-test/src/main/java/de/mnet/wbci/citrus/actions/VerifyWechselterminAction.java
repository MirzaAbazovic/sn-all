/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import java.time.format.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the VA Request, with the help of the VorabstimmungsID and verifies that the Wechseltermin matches the
 * expected.
 */
public class VerifyWechselterminAction extends AbstractRequestAction {

    private final LocalDateTime expectedDate;
    private final String dateVariableName;
    private WbciCommonService wbciCommonService;

    /**
     * Constructor to check against the date parameter.
     */
    public VerifyWechselterminAction(WbciCommonService wbciCommonService, String dateVariableName, LocalDateTime expectedDate) {
        super("verifyWechseltermin", wbciCommonService, RequestTyp.VA);
        this.expectedDate = expectedDate;
        this.dateVariableName = dateVariableName;
        this.wbciCommonService = wbciCommonService;
    }

    /**
     * Constructor to check against the date parameter.
     */
    public VerifyWechselterminAction(WbciCommonService wbciCommonService, LocalDateTime expectedDate) {
        this(wbciCommonService, null, expectedDate);
    }

    /**
     * Constructor for check against the date variable.
     */
    public VerifyWechselterminAction(WbciCommonService wbciCommonService, String dateVariableName) {
        this(wbciCommonService, dateVariableName, null);
    }

    /**
     * Constructor to check if the Wechseltermin is null.
     *
     * @param wbciCommonService
     */
    public VerifyWechselterminAction(WbciCommonService wbciCommonService) {
        this(wbciCommonService, null, null);
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(context, wbciCommonService);
        Assert.assertNotNull(wbciGeschaeftsfall);

        if (dateVariableName == null) {
            Assert.assertEquals(wbciGeschaeftsfall.getWechseltermin(),
                    expectedDate != null ?  DateTools.stripTimeFromDate(expectedDate).toLocalDate() : null);
        }
        else if (dateVariableName.equals(VariableNames.REQUESTED_CUSTOMER_DATE)
                || dateVariableName.equals(VariableNames.RESCHEDULED_CUSTOMER_DATE)) {
            Assert.assertEquals(wbciGeschaeftsfall.getWechseltermin(),
                    dateVariableName != null ? DateTools.stripTimeFromDate(LocalDate.parse(context.getVariable(dateVariableName), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()).toLocalDate() : null);
        }
        else {
            throw new CitrusRuntimeException("Variable Name '" + dateVariableName + " not valid!");
        }
    }

}
