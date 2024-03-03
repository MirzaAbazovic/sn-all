/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 21.10.13 
 */
package de.mnet.wbci.acceptance.common;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.mail.model.MailResponse;
import org.springframework.util.StringUtils;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.actions.AbstractWbciTestAction;
import de.mnet.wbci.citrus.actions.AssertEscalationEmailEntryAction;
import de.mnet.wbci.helper.MailEscalationHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciEscalationService;

/* Bitte beachten: Wenn ein Test in dieser Suite scheitert, koennen die darauffolgenden auch scheitern...
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class EscalationService_AccTest extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the correct execution of the generation and sending action of the {@link WbciEscalationService}.
     * To save the mails to local disk => set property: mail.save.to.disk=true
     */
    @CitrusTest
    public void EscalationService_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.EscalationReport_Carrier_Overview_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        hurrican().cleanUpUnsentEmailEntrys();

        hurrican().sendCarrierEscalationOverviewReport();

        //test escalation overview report
        final String subject = MailEscalationHelper.generateCarrierOverviewReportSubject(new Date());
        hurrican().assertEscalationReportEmailEntry(subject);

        hurrican().triggerProcessPendingEmails();
        hurrican().receiveAllEmails(subject,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP_GROUP);
    }

    /**
     * Tests the correct execution of the generation and sending action of the {@link WbciEscalationService}.
     * To save the mails to local disk => set property: mail.save.to.disk=true
     */
    @CitrusTest
    public void EscalationService_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.EscalationReport_CarrierSpecific_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        hurrican().cleanUpUnsentEmailEntrys();

        hurrican().sendCarrierSpecificEscalationReports();
        hurrican().addMailSenderAndReceipentToContext();

        hurrican().triggerProcessPendingEmails();

        action(new AbstractWbciTestAction("verifyCarrierReportsEmailAction") {
            @Override
            public void doExecute(final TestContext testContext) {
                final List<CarrierCode> carrierCodes = (List<CarrierCode>) testContext.getVariables()
                        .get(VariableNames.ESCALATION_REPORT_CARRIER_LIST);
                final String from = testContext.getVariable(VariableNames.ESCALATION_MAIL_SENDER);
                final String to = testContext.getVariable(VariableNames.ESCALATION_MAIL_RECIPIENTS).replace(";", ",");
                final List<String> expectedSubjects = new ArrayList<>(carrierCodes.size());

                for (CarrierCode carrierCode : carrierCodes) {
                    //test escalation report for the current carrier
                    expectedSubjects.add(MailEscalationHelper.generateCarrierReportSubject(carrierCode, new Date()));
                }

                for (CarrierCode carrierCode : carrierCodes) {
                    log.info("validate email receiving for carrier {}", carrierCode.getITUCarrierCode());
                    final int numberOfRecipients = StringUtils.commaDelimitedListToSet(to).size();
                    for (int i = 0; i < numberOfRecipients; i++) {
                        hurrican().receiveEmail(from, to, expectedSubjects,
                                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP,
                                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP_GROUP).execute(testContext);
                        hurrican().ackEmail(MailResponse.OK_CODE, MailResponse.OK_MESSAGE).execute(testContext);
                    }
                }
            }

        });
    }


    /**
     * Tests the correct execution of the generation and sending action of the {@link
     * WbciEscalationService#sendInternalOverviewReport()}.
     * To save the mails to local disk => set property: mail.save.to.disk=true
     */
    @CitrusTest
    public void EscalationService_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.EscalationReport_Internal_Overview_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        hurrican().cleanUpUnsentEmailEntrys();

        hurrican().sendInternalOverviewReport();

        //test escalation overview report
        final String subject = MailEscalationHelper.generateInternalOverviewReportSubject(new Date());
        hurrican().assertEscalationReportEmailEntry(subject);

        hurrican().triggerProcessPendingEmails();
        hurrican().receiveAllEmails(subject,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP_GROUP);
    }


    /**
     * Tests the sending of an escalation mail for a VA.
     * To save the mails to local disk => set property: mail.save.to.disk=true
     */
    @CitrusTest
    public void EscalationMailForVa_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.EscalationMailVa_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);
        hurrican().cleanUpUnsentEmailEntrys();

        String vorabstimmungsId = hurrican().createPreAgreementId(RequestTyp.VA);
        variable(VariableNames.PRE_AGREEMENT_ID, vorabstimmungsId);
        VorabstimmungsAnfrage vorabstimmungsAnfrage = new VorabstimmungsAnfrageTestBuilder<>()
                .withWbciGeschaeftsfall(
                        new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withVorabstimmungsId(vorabstimmungsId)
                                .withAufnehmenderEKP(CarrierCode.MNET)
                                .withAbgebenderEKP(CarrierCode.DTAG)
                                .buildValid(WBCI_CDM_VERSION, geschaeftsfallTyp)
                )
                .withAnswerDeadline(getDateInWorkingDaysFromNow(-5).toLocalDate())
                .withIsMnetDeadline(false)
                .buildValid(WBCI_CDM_VERSION, geschaeftsfallTyp);
        hurrican().storeWbciEntity(vorabstimmungsAnfrage);

        hurrican().createEscalationContactForCarrierIfNotPresent(CarrierCode.DTAG);

        String loginName = "testUser";
        String email = "test.user@m-net.de";
        AKUser user = new AKUserBuilder()
                .withLoginName(loginName)
                .withEmail(email)
                .build();
        hurrican().sendEscalationMailForVaTestAction(user);

        String subject = String.format("WBCI-Eskalation VA-KUE-MRN (DEU.DTAG) %s (Stand %s, Applikationsmodus <nicht gesetzt>)",
                vorabstimmungsId,
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR));
        hurrican().assertEscalationEmailEntry(email, hurrican().DEFAULT_CARRIER_ESCALATION_EMAIL, subject);

        hurrican().triggerProcessPendingEmails();
        hurrican().receiveEmail(email, hurrican().DEFAULT_CARRIER_ESCALATION_EMAIL, subject,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP,
                AssertEscalationEmailEntryAction.SUBJECT_REG_EXP_GROUP);
        hurrican().ackEmail();
    }

}
