/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.citrus.actions;

import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the VA Request, with the help of the VorabstimmungsID and verifies that the bestaetigerWechseltermin
 * matches the expected.
 */
public class VerifyKundenwunschterminAction extends AbstractRequestAction {

    private final LocalDate expectedDate;
    private final String dateVariableName;

    /**
     * Constructor to check against the date parameter.
     */
    public VerifyKundenwunschterminAction(WbciCommonService wbciCommonService, LocalDate expectedDate, RequestTyp requestTyp) {
        super("verifyKundenwunschtermin", wbciCommonService, requestTyp);
        this.expectedDate = expectedDate;
        this.dateVariableName = null;
    }

    /**
     * Constructor for check against the date variable.
     */
    public VerifyKundenwunschterminAction(WbciCommonService wbciCommonService, String dateVariableName, RequestTyp requestTyp) {
        super("verifyKundenwunschtermin", wbciCommonService, requestTyp);
        this.expectedDate = null;
        this.dateVariableName = dateVariableName;
    }


    @Override
    public void doExecute(TestContext context) {
        //determin expected kwt date
        LocalDate expectedKwtDate;
        if (dateVariableName == null) {
            expectedKwtDate = expectedDate;
        }
        else if (dateVariableName.equals(VariableNames.REQUESTED_CUSTOMER_DATE)
                || dateVariableName.equals(VariableNames.RESCHEDULED_CUSTOMER_DATE)) {
            expectedKwtDate = LocalDate.parse(context.getVariable(dateVariableName), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        else {
            throw new CitrusRuntimeException("Variable Name '" + dateVariableName + " not valid!");
        }

        final WbciRequest wbciRequest = retrieve(context);
        assertNotNull(wbciRequest);
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciRequest.getWbciGeschaeftsfall();
        assertNotNull(wbciGeschaeftsfall);
        assertEquals(wbciGeschaeftsfall.getKundenwunschtermin(), expectedKwtDate, "KWT of WbciGeschaeftsfall is invalid");

        //check dates of the specific request
        if (wbciRequest instanceof VorabstimmungsAnfrage) {
            assertEquals(((VorabstimmungsAnfrage) wbciRequest).getVaKundenwunschtermin(), expectedKwtDate, "KWT of VaRequest is invalid");
        }
        else if (wbciRequest instanceof TerminverschiebungsAnfrage) {
            assertEquals(((TerminverschiebungsAnfrage) wbciRequest).getTvTermin(), expectedKwtDate, "KWT of TvRequest is invalid");
        }
    }

}
