/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import java.time.format.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Citrus test action creates new meldung via remote service call. Test action automatically sets the vorabstimmungsId
 * of the wbci geschaeftsfall that is associated with
 *
 *
 */
public class CreateWbciMeldungTestAction extends AbstractWbciTestAction {

    private WbciMeldungService wbciMeldungService;
    private Meldung<?> meldung;
    private RequestTyp initialRequestTyp;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWbciMeldungTestAction.class);

    /**
     * Constructor setting the action name field.
     */
    public CreateWbciMeldungTestAction(Meldung<?> meldung, WbciMeldungService wbciMeldungService) {
        this(meldung, wbciMeldungService, RequestTyp.VA);
    }

    /**
     * Constructor setting the action name field.
     */
    public CreateWbciMeldungTestAction(Meldung<?> meldung, WbciMeldungService wbciMeldungService,
            RequestTyp initialRequestTyp) {
        super("createWbciMeldung");
        this.meldung = meldung;
        this.wbciMeldungService = wbciMeldungService;
        this.initialRequestTyp = initialRequestTyp;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = getVorabstimmungsId(context);
        if (vorabstimmungsId == null) {
            throw new CitrusRuntimeException(
                    "Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        if (initialRequestTyp.isTerminverschiebung()) {
            String aenderungsId = context.getVariable(VariableNames.CHANGE_ID);

            if (aenderungsId == null) {
                throw new CitrusRuntimeException(
                        "Missing aenderungsId test variable for creating meldung after tv request type ("
                                + initialRequestTyp + ")"
                );
            }

            if (MeldungTyp.ABBM == meldung.getTyp()) {
                ((Abbruchmeldung) meldung).setAenderungsIdRef(aenderungsId);
            }
            else if (MeldungTyp.ERLM == meldung.getTyp()) {
                ((Erledigtmeldung) meldung).setAenderungsIdRef(aenderungsId);
                LocalDate tvDate = LocalDate.parse(context.getVariable(VariableNames.RESCHEDULED_CUSTOMER_DATE), DateTimeFormatter.ISO_LOCAL_DATE);
                ((Erledigtmeldung) meldung).setWechseltermin(tvDate);
            }
        }

        if (initialRequestTyp.isStorno()) {
            String stornoId = context.getVariable(VariableNames.STORNO_ID);

            if (stornoId == null) {
                throw new CitrusRuntimeException(
                        "Missing stornoId test variable for creating meldung after storno request type ("
                                + initialRequestTyp + ")"
                );
            }

            if (MeldungTyp.ABBM == meldung.getTyp()) {
                ((Abbruchmeldung) meldung).setStornoIdRef(stornoId);
            }
            else if (MeldungTyp.ERLM == meldung.getTyp()) {
                ((Erledigtmeldung) meldung).setStornoIdRef(stornoId);
            }
        }

        LOGGER.info(String.format("Creating WBCI meldung '%s' zur VorabstimmungsId '%s' ", meldung.getTyp(), vorabstimmungsId));
        wbciMeldungService.createAndSendWbciMeldung(meldung, vorabstimmungsId);
    }

}
