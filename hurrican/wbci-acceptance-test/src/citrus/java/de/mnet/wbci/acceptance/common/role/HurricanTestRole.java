/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.acceptance.common.role;

import static de.augustakom.hurrican.service.cc.RegistryService.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.regex.*;
import com.consol.citrus.TestAction;
import com.consol.citrus.TestActor;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.actions.SendMessageAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.RepeatOnErrorUntilTrueDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.mail.message.CitrusMailMessageHeaders;
import com.consol.citrus.mail.model.MailResponse;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.AbstractValidationCallback;
import com.consol.citrus.validation.builder.StaticMessageContentBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierContactBuilder;
import de.augustakom.hurrican.model.cc.Registry;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.common.service.HistoryService;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.actions.AbstractWbciTestAction;
import de.mnet.wbci.citrus.actions.AssertEscalationEmailEntryAction;
import de.mnet.wbci.citrus.actions.AssertExceptionLogEntryCreatedAction;
import de.mnet.wbci.citrus.actions.AssertNoIoArchiveEntryCreated;
import de.mnet.wbci.citrus.actions.AssignUserToWbciGeschaeftsfallAction;
import de.mnet.wbci.citrus.actions.CancelHurricanOrdersTestAction;
import de.mnet.wbci.citrus.actions.ClearKlaerfallTestAction;
import de.mnet.wbci.citrus.actions.ClearScheduledRequestsTestAction;
import de.mnet.wbci.citrus.actions.CloseGeschaefsfallIssueAction;
import de.mnet.wbci.citrus.actions.CloseWbciGeschaeftsfallAction;
import de.mnet.wbci.citrus.actions.CloseWbciGeschaeftsfallProcessingAction;
import de.mnet.wbci.citrus.actions.CreateElektraServiceRequestTestAction;
import de.mnet.wbci.citrus.actions.CreateWbciLocationSearchTestAction;
import de.mnet.wbci.citrus.actions.CreateWbciMeldungTestAction;
import de.mnet.wbci.citrus.actions.CreateWbciStornoTestAction;
import de.mnet.wbci.citrus.actions.CreateWbciTvTestAction;
import de.mnet.wbci.citrus.actions.CreateWbciVorgangTestAction;
import de.mnet.wbci.citrus.actions.DetermineIsSearchBuildingExpected;
import de.mnet.wbci.citrus.actions.MarkGfAsAutomatableAction;
import de.mnet.wbci.citrus.actions.MarkMnetWitaRequestAsUnsent;
import de.mnet.wbci.citrus.actions.ResetAutomatedAkmTrProcessingTestAction;
import de.mnet.wbci.citrus.actions.ResetAutomatedErlmTvProcessingTestAction;
import de.mnet.wbci.citrus.actions.ResetAutomatedOutgoingRuemVaProcessingTestAction;
import de.mnet.wbci.citrus.actions.ResetAutomatedRuemVaProcessingTestAction;
import de.mnet.wbci.citrus.actions.ResetAutomatedStrAufhErlmDonatingProcessingTestAction;
import de.mnet.wbci.citrus.actions.ResetAutomatedStrAufhErlmProcessingTestAction;
import de.mnet.wbci.citrus.actions.SaveLatestExceptionLogEntryIdAction;
import de.mnet.wbci.citrus.actions.SendCarrierEscalationOverviewReportAction;
import de.mnet.wbci.citrus.actions.SendCarrierSpecificEscalationReportsAction;
import de.mnet.wbci.citrus.actions.SendEscalationMailForVaTestAction;
import de.mnet.wbci.citrus.actions.SendInternalverviewReportAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedAkmTrProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedErlmTvProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedIncomingAkmTrProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedOutgoingRuemVaProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedRuemVaProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedStrAufhErlmDonatingProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerAutomatedStrAufhErlmProcessingTestAction;
import de.mnet.wbci.citrus.actions.TriggerSchedulerTestAction;
import de.mnet.wbci.citrus.actions.TriggerWbciHousekeepingTestAction;
import de.mnet.wbci.citrus.actions.VerifyAnswerDeadlineAction;
import de.mnet.wbci.citrus.actions.VerifyAuftragDatenStatusAction;
import de.mnet.wbci.citrus.actions.VerifyAutomatableAction;
import de.mnet.wbci.citrus.actions.VerifyAutomationTasksAction;
import de.mnet.wbci.citrus.actions.VerifyBauauftragExistTestAction;
import de.mnet.wbci.citrus.actions.VerifyGfStatusAction;
import de.mnet.wbci.citrus.actions.VerifyIoArchiveEntryCreatedAction;
import de.mnet.wbci.citrus.actions.VerifyKundenwunschterminAction;
import de.mnet.wbci.citrus.actions.VerifyLinkedStrAenGfStatusAction;
import de.mnet.wbci.citrus.actions.VerifyNonBillingRelevantOrdersAssignedAction;
import de.mnet.wbci.citrus.actions.VerifyStornoRequestScheduledAction;
import de.mnet.wbci.citrus.actions.VerifyStornoRequestStatusAction;
import de.mnet.wbci.citrus.actions.VerifyTvRequestScheduledAction;
import de.mnet.wbci.citrus.actions.VerifyTvRequestStatusAction;
import de.mnet.wbci.citrus.actions.VerifyVaRequestScheduledAction;
import de.mnet.wbci.citrus.actions.VerifyVaRequestStatusAction;
import de.mnet.wbci.citrus.actions.VerifyWbciBaseRequestMetaDataSetAction;
import de.mnet.wbci.citrus.actions.VerifyWbciIncomingRequestAssignedToOrderAction;
import de.mnet.wbci.citrus.actions.VerifyWbciIncomingRequestCreatedAction;
import de.mnet.wbci.citrus.actions.VerifyWbciIncomingRequestNotAssignedToOrderAction;
import de.mnet.wbci.citrus.actions.VerifyWbciOutgoingRequestCreatedAction;
import de.mnet.wbci.citrus.actions.VerifyWechselterminAction;
import de.mnet.wbci.citrus.actions.VerifyWitaCbVorgangCancelledTestAction;
import de.mnet.wbci.citrus.actions.VerifyWitaCbVorgangExistTestAction;
import de.mnet.wbci.citrus.actions.VerifyWitaVorabstimmungAbgebendAction;
import de.mnet.wbci.citrus.builder.HurricanAuftragBuilder;
import de.mnet.wbci.converter.MeldungsCodeConverter;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciAutomationDonatingService;
import de.mnet.wbci.service.WbciAutomationService;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciEscalationService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciLocationService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciVaService;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Hurrican test behavior holds specific message sender/receiver components and business services for acting as Hurrican
 * in Citrus tests. Behavior provides special test building methods for Hurrican. Methods are used in test builders in
 * order to build Hurrican test logic. Test building methods in this class do delegate back to test builder which is
 * injected automatically by TestNG test builder class before execution.
 *
 *
 */
public class HurricanTestRole extends AbstractTestRole {

    @Autowired
    @Qualifier("hurricanTestActor")
    protected TestActor hurricanTestActor;
    @Autowired
    @Qualifier("atlasEsbTestActor")
    protected TestActor atlasEsbTestActor;
    @Autowired
    @Qualifier("WbciMeldungService")
    private WbciMeldungService wbciMeldungService;
    @Autowired
    @Qualifier("WbciVaKueMrnService")
    private WbciVaService<WbciGeschaeftsfallKueMrn> wbciVaKueMrnService;
    @Autowired
    @Qualifier("WbciVaKueOrnService")
    private WbciVaService<WbciGeschaeftsfallKueOrn> wbciVaKueOrnService;
    @Autowired
    @Qualifier("WbciVaRrnpService")
    private WbciVaService<WbciGeschaeftsfallRrnp> wbciVaRrnpService;
    @Autowired
    @Qualifier("WbciTvService")
    private WbciTvService<WbciGeschaeftsfallRrnp> wbciTvService;
    @Autowired
    @Qualifier("WbciStornoService")
    private WbciStornoService<?> wbciStornoService;
    @Autowired
    @Qualifier("WbciCommonService")
    private WbciCommonService wbciCommonService;
    @Autowired
    @Qualifier("WbciLocationService")
    private WbciLocationService wbciLocationService;
    @Autowired
    @Qualifier("HistoryService")
    private HistoryService historyService;
    @Autowired
    @Qualifier("WbciSchedulerService")
    private WbciSchedulerService wbciSchedulerService;
    @Autowired
    @Qualifier("WbciAutomationService")
    private WbciAutomationService wbciAutomationService;
    @Autowired
    @Qualifier("WbciAutomationDonatingService")
    private WbciAutomationDonatingService wbciAutomationDonatingService;
    @Autowired
    @Qualifier("WbciEscalationService")
    private WbciEscalationService wbciEscalationService;
    @Autowired
    @Qualifier("WbciGeschaeftsfallService")
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    @Qualifier("WbciElektraService")
    private WbciElektraService wbciElektraService;
    @Autowired
    @Qualifier("ExceptionLogService")
    private ExceptionLogService exceptionLogService;
    @Autowired
    @Qualifier("WitaTalOrderService")
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    @Qualifier("MwfEntityService")
    private MwfEntityService mwfEntityService;

    @Autowired
    @Qualifier("WitaConfigService")
    private WitaConfigService witaConfigService;
    @Autowired
    @Qualifier("MailService")
    private MailService mailService;

    @Autowired
    private CCAuftragDAO ccAuftragDAO;
    @Autowired
    private AuftragDatenDAO auftragDatenDAO;
    @Autowired
    private VerlaufDAO verlaufDAO;

    @Value("${verify.sleep.db.short}")
    private int dbSleepTimeShort;
    @Value("${verify.max.db.retry.short}")
    private int dbMaxRetryShort;

    @Value("${verify.sleep.db.long}")
    private int dbSleepTimeLong;
    @Value("${verify.max.db.retry.long}")
    private int dbMaxRetryLong;

    @Autowired
    @Qualifier("citrusSmtpServer")
    private Endpoint citrusSmtpServer;

    /**
     * Create new wbci meldung with custom Citrus test action. The test action automatically sets the vorabstimmungsId
     * from the wbci geschaeftsfall which was created before in test.
     *
     * @param meldung
     * @return
     */
    public CreateWbciMeldungTestAction createWbciMeldung(Meldung<?> meldung) {
        CreateWbciMeldungTestAction createWbciMeldungTestAction = new CreateWbciMeldungTestAction(meldung,
                wbciMeldungService);
        testBuilder.action(createWbciMeldungTestAction);
        return createWbciMeldungTestAction;
    }

    /**
     * Create new wbci meldung with custom Citrus test action. The test action automatically sets the vorabstimmungsId
     * from the wbci geschaeftsfall which was created before in test.
     *
     * @param meldung
     * @param initialRequestTyp
     * @return
     */
    public CreateWbciMeldungTestAction createWbciMeldung(Meldung<?> meldung, RequestTyp initialRequestTyp) {
        CreateWbciMeldungTestAction createWbciMeldungTestAction = new CreateWbciMeldungTestAction(meldung,
                wbciMeldungService, initialRequestTyp);
        testBuilder.action(createWbciMeldungTestAction);
        return createWbciMeldungTestAction;
    }

    /**
     * Creates new wbci geschaeftsfall with custom Citrus test action. No simulator use case name is set so default
     * simulator behavior (usually only TEQ) is triggered in an end-to-end acceptance test scenario.
     *
     * @param geschaeftsfall
     * @return
     */
    public CreateWbciVorgangTestAction createWbciVorgang(WbciGeschaeftsfall geschaeftsfall) {
        return createWbciVorgang(geschaeftsfall, null);
    }

    /**
     * Creates new wbci geschaeftsfall with custom Citrus test action. Simulator use case is explicitly set so
     * respective wbci simulator use case is triggered in an end-to-end acceptance test scenario.
     *
     * @param geschaeftsfall
     * @param projektKenner
     * @return
     */
    private CreateWbciVorgangTestAction createWbciVorgang(WbciGeschaeftsfall geschaeftsfall, String projektKenner) {
        CreateWbciVorgangTestAction createVaServiceTestAction;
        if (geschaeftsfall instanceof WbciGeschaeftsfallKueMrn) {
            createVaServiceTestAction = new CreateWbciVorgangTestAction(wbciVaKueMrnService, geschaeftsfall);
        }
        else if (geschaeftsfall instanceof WbciGeschaeftsfallKueOrn) {
            createVaServiceTestAction = new CreateWbciVorgangTestAction(wbciVaKueOrnService, geschaeftsfall);
        }
        else {
            createVaServiceTestAction = new CreateWbciVorgangTestAction(wbciVaRrnpService, geschaeftsfall);
        }

        createVaServiceTestAction.setActor(hurricanTestActor);

        if (StringUtils.hasText(projektKenner)) {
            createVaServiceTestAction.setProjektKenner(projektKenner);
        }

        testBuilder.action(createVaServiceTestAction);
        return createVaServiceTestAction;
    }

    /**
     * Creates a new WBCI 'Terminverschiebung' with custom Citrus test action.
     *
     * @param tvTermin     the expected date of the 'Terminverschiebung'
     * @param aenderungsId The aenderungsId used for the creation of the TV. If not set the server will generate one.
     *                     This parameter should be used only for KFT-Tests because they expect fixed ids.
     * @return
     */
    public CreateWbciTvTestAction createWbciTv(LocalDate tvTermin, String aenderungsId) {
        CreateWbciTvTestAction createWbciTvTestAction = new CreateWbciTvTestAction(wbciTvService, wbciCommonService,
                tvTermin, aenderungsId);
        createWbciTvTestAction.setActor(hurricanTestActor);

        testBuilder.action(createWbciTvTestAction);
        return createWbciTvTestAction;
    }

    /**
     * Creates a new WBCI 'Storno' with custom Citrus test action.
     *
     * @param stornoAnfrage the stornoAnfrage to be created
     * @return
     */
    public CreateWbciStornoTestAction createWbciStorno(StornoAnfrage<?> stornoAnfrage) {
        CreateWbciStornoTestAction createWbciStoroTestAction = new CreateWbciStornoTestAction<>(wbciStornoService,
                stornoAnfrage);
        createWbciStoroTestAction.setActor(hurricanTestActor);

        testBuilder.action(createWbciStoroTestAction);
        return createWbciStoroTestAction;
    }

    /**
     * Creates a new search on location service with custom Citrus test action. Test action is returned to caller where
     * caller can add custom response callback.
     *
     * @param standort the standort to execute search request for
     * @return
     */
    public CreateWbciLocationSearchTestAction createWbciLocationSearch(Standort standort) {
        CreateWbciLocationSearchTestAction createWbciLocationSearchTestAction = new CreateWbciLocationSearchTestAction(wbciLocationService, standort);
        createWbciLocationSearchTestAction.setActor(hurricanTestActor);

        testBuilder.action(createWbciLocationSearchTestAction);
        return createWbciLocationSearchTestAction;
    }

    /**
     * Creates a new elektra service request with custom Citrus test action. MeldungTyp is manadatory for ELEKTRA
     * interaction. Based on meldungTyp the corresponding elektra automated processing is invoked via remote service
     * call.
     *
     * @param meldungTyp
     * @return
     */
    public CreateElektraServiceRequestTestAction createElektraServiceRequest(MeldungTyp meldungTyp) {
        return createElektraServiceRequest(RequestTyp.VA, meldungTyp);
    }

    /**
     * Creates a new elektra service request with custom Citrus test action. MeldungTyp is manadatory for ELEKTRA
     * interaction. Based on requestTyp and meldungTyp the corresponding elektra automated processing is invoked via
     * remote service call.
     *
     * @param requestTyp
     * @param meldungTyp
     * @return
     */
    public CreateElektraServiceRequestTestAction createElektraServiceRequest(RequestTyp requestTyp, MeldungTyp meldungTyp) {
        CreateElektraServiceRequestTestAction createElektraServiceRequestTestAction = new CreateElektraServiceRequestTestAction(wbciElektraService, requestTyp, meldungTyp);
        createElektraServiceRequestTestAction.setActor(hurricanTestActor);

        testBuilder.action(createElektraServiceRequestTestAction);
        return createElektraServiceRequestTestAction;
    }


    public MarkGfAsAutomatableAction markGfAsAutomatable() {
        MarkGfAsAutomatableAction action = new MarkGfAsAutomatableAction(wbciCommonService, wbciDao);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    public ClearKlaerfallTestAction clearKlaerfall() {
        ClearKlaerfallTestAction action = new ClearKlaerfallTestAction(wbciGeschaeftsfallService, wbciCommonService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    public RepeatOnErrorUntilTrueDefinition assertAutomationTasks(List<AutomationTask> automationTasks) {
        return assertAutomationTasks(automationTasks, true);
    }

    /**
     * Verifies that the provided list of automation tasks is assigned to the current WbciGeschaeftsfall
     *
     * @return
     */
    public RepeatOnErrorUntilTrueDefinition assertAutomationTasks(List<AutomationTask> automationTasks, boolean verifyEqualSize) {
        VerifyAutomationTasksAction verifyAutomationTasksAction = new VerifyAutomationTasksAction(
                wbciCommonService, automationTasks, verifyEqualSize);
        verifyAutomationTasksAction.setActor(hurricanTestActor);

        return testBuilder.repeatOnError(verifyAutomationTasksAction)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
    }

    /**
     * Verify an outgoing {@link WbciRequest} is created and stored in the Hurrican database.
     *
     * @return
     */
    public VerifyWbciOutgoingRequestCreatedAction assertWbciOutgoingRequestCreated() {
        VerifyWbciOutgoingRequestCreatedAction verifyWbciOutgoingRequestCreatedAction = new VerifyWbciOutgoingRequestCreatedAction(
                wbciCommonService);
        verifyWbciOutgoingRequestCreatedAction.setActor(hurricanTestActor);

        testBuilder.action(verifyWbciOutgoingRequestCreatedAction);
        return verifyWbciOutgoingRequestCreatedAction;
    }

    public VerifyWbciIncomingRequestCreatedAction assertWbciIncomingRequestCreated() {
        VerifyWbciIncomingRequestCreatedAction verifyWbciIncomingRequestCreatedAction = new VerifyWbciIncomingRequestCreatedAction(
                wbciCommonService);
        verifyWbciIncomingRequestCreatedAction.setActor(hurricanTestActor);

        testBuilder.action(verifyWbciIncomingRequestCreatedAction);
        return verifyWbciIncomingRequestCreatedAction;
    }

    /**
     * Verify if some required meta data are set on the current base {@link WbciRequest}
     *
     * @param ioTypeForBaseRequest specifies if the base request was incoming or outgoing
     * @param expectedStatus
     * @return
     */
    public VerifyWbciBaseRequestMetaDataSetAction assertWbciBaseRequestMetaDataSet(IOType ioTypeForBaseRequest,
            WbciGeschaeftsfallStatus expectedStatus) {
        VerifyWbciBaseRequestMetaDataSetAction verifyWbciBaseRequestMetaDataSetAction = new VerifyWbciBaseRequestMetaDataSetAction(
                wbciCommonService, expectedStatus, ioTypeForBaseRequest);
        verifyWbciBaseRequestMetaDataSetAction.setActor(hurricanTestActor);

        testBuilder.action(verifyWbciBaseRequestMetaDataSetAction);
        return verifyWbciBaseRequestMetaDataSetAction;
    }

    /**
     * Verifies that the VA Request status matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyVaRequestStatusAction assertVaRequestStatus(WbciRequestStatus expectedStatus) {
        VerifyVaRequestStatusAction action = new VerifyVaRequestStatusAction(wbciCommonService, expectedStatus);
        // set atlas esb test actor here as we do not want to verify request status during kft test run
        // this could cause runtime racing conditions in kft
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that a correct {@link VorabstimmungAbgebend} have been set.
     *
     * @param active       indicates the {@link de.mnet.wita.model.VorabstimmungAbgebend#rueckmeldung}
     * @param regExComment regular expression for the {@link de.mnet.wita.model.VorabstimmungAbgebend#bemerkung}.
     */
    public VerifyWitaVorabstimmungAbgebendAction assertVorabstimmungAbgebendSet(final Boolean active, final String regExComment) {
        VerifyWitaVorabstimmungAbgebendAction action = new VerifyWitaVorabstimmungAbgebendAction(wbciCommonService, vorabstimmungAbgebendDao, active, regExComment);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }


    /**
     * Verifies that a {@link WitaCBVorgang} of type {@code cbVorgangTyp} is created and assigned to the current VA-Id.
     *
     * @param cbVorgangTyp
     * @return
     */
    public RepeatOnErrorUntilTrueDefinition assertWitaCbVorgangCreated(final Long cbVorgangTyp) {
        VerifyWitaCbVorgangExistTestAction action = new VerifyWitaCbVorgangExistTestAction(cbVorgangTyp, witaTalOrderService);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);

        return testBuilder.repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
    }

    /**
     * Verifies that NO {@link WitaCBVorgang} is created and assigned to the current VA-Id.
     *
     * @return
     */
    public VerifyWitaCbVorgangCancelledTestAction assertWitaCbVorgangCancelled() {
        VerifyWitaCbVorgangCancelledTestAction action =
                new VerifyWitaCbVorgangCancelledTestAction(witaTalOrderService, mwfEntityService);
        action.setActor(hurricanTestActor);
        testBuilder.repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
        return action;
    }

    /**
     * Markiert den WITA-Vorgang zur aktuellen VA-Id als 'nicht gesendet'
     *
     * @return
     */
    public MarkMnetWitaRequestAsUnsent markWitaRequestAsUnsent() {
        MarkMnetWitaRequestAsUnsent action = new MarkMnetWitaRequestAsUnsent(witaTalOrderService, mwfEntityService);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }


    /**
     * Verifies that the WbciGeschaeftsfall status matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyGfStatusAction assertGfStatus(WbciGeschaeftsfallStatus expectedStatus) {
        VerifyGfStatusAction action = new VerifyGfStatusAction(wbciCommonService, expectedStatus);
        // set atlas esb test actor here as we do not want to verify gf status during kft test run
        // this could cause runtime racing conditions in kft
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the status of the linked WbciGeschaeftsfall matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyLinkedStrAenGfStatusAction assertLinkedStrAenGfStatus(WbciGeschaeftsfallStatus expectedStatus) {
        VerifyLinkedStrAenGfStatusAction action = new VerifyLinkedStrAenGfStatusAction(wbciCommonService, expectedStatus);
        // set atlas esb test actor here as we do not want to verify gf status during kft test run
        // this could cause runtime racing conditions in kft
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Kundenwunschtermin of the applied {@link RequestTyp} matches the supplied {@link LocalDateTime}.
     *
     * @param expectedDate the date to match against
     * @param requestType  type of request, which is responsible for the requested Kundenwunschtermin
     */
    public VerifyKundenwunschterminAction assertKundenwunschtermin(LocalDate expectedDate, RequestTyp requestType) {
        VerifyKundenwunschterminAction action = new VerifyKundenwunschterminAction(wbciCommonService, expectedDate, requestType);
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Kundenwunschtermin of the applied {@link RequestTyp} matches to the variable name
     *
     * @param dateVariableName name of the variable which should be used to check the Kundenwunschtermin. Normally is
     *                         should be {@code VariableNames.REQUESTED_CUSTOMER_DATE} or {@code
     *                         VariableNames.RESCHEDULED_CUSTOMER_DATE} for TV.
     * @param requestType      type of request, which is responsible for the requested Kundenwunschtermin
     */
    public VerifyKundenwunschterminAction assertKundenwunschtermin(String dateVariableName, RequestTyp requestType) {
        VerifyKundenwunschterminAction action = new VerifyKundenwunschterminAction(wbciCommonService, dateVariableName, requestType);
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Wechseltermin matches the supplied {@link LocalDateTime}.
     *
     * @param expectedDate the date to match against
     */
    public VerifyWechselterminAction assertWechseltermin(LocalDateTime expectedDate) {
        VerifyWechselterminAction action = new VerifyWechselterminAction(wbciCommonService, expectedDate);
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Wechseltermin matches to the variable name
     *
     * @param dateVariableName name of the variable which should be used to check the Wechseltermin. Normally is should
     *                         be {@code VariableNames.REQUESTED_CUSTOMER_DATE} for RUEMVA or {@code
     *                         VariableNames.RESCHEDULED_CUSTOMER_DATE} for TV.
     */
    public VerifyWechselterminAction assertWechseltermin(String dateVariableName) {
        VerifyWechselterminAction action = new VerifyWechselterminAction(wbciCommonService, dateVariableName);
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the bestaetigerWechseltermin is {@code null}.
     */
    public VerifyWechselterminAction assertWechselterminIsNull() {
        VerifyWechselterminAction action = new VerifyWechselterminAction(wbciCommonService);
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the answer deadline and the responsibility are set correct.
     */
    public VerifyAnswerDeadlineAction assertVaAnswerDeadlineIsSet() {
        VerifyAnswerDeadlineAction action =
                new VerifyAnswerDeadlineAction(wbciCommonService, RequestTyp.VA, true);
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the answer deadline and the responsibility are set correct.
     */
    public VerifyAnswerDeadlineAction assertTvAnswerDeadlineIsSet() {
        VerifyAnswerDeadlineAction action =
                new VerifyAnswerDeadlineAction(wbciCommonService, RequestTyp.TV, true);
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the answer deadline is null and the responsibility is set correct.
     */
    public VerifyAnswerDeadlineAction assertTvAnswerDeadlineIsNotSet() {
        VerifyAnswerDeadlineAction action =
                new VerifyAnswerDeadlineAction(wbciCommonService, RequestTyp.TV, false);
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the answer deadline and the responsibility are set correct.
     *
     * @param stornoTyp
     */
    public VerifyAnswerDeadlineAction assertStornoAnswerDeadlineIsSet(RequestTyp stornoTyp) {
        VerifyAnswerDeadlineAction action =
                new VerifyAnswerDeadlineAction(wbciCommonService, stornoTyp, true);
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the answer deadline is null and the responsibility is set correct.
     *
     * @param stornoTyp
     */
    public VerifyAnswerDeadlineAction assertStornoAnswerDeadlineIsNotSet(RequestTyp stornoTyp) {
        VerifyAnswerDeadlineAction action =
                new VerifyAnswerDeadlineAction(wbciCommonService, stornoTyp, false);
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the {@link AuftragDaten} status matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyAuftragDatenStatusAction assertAuftragDatenStatus(Long expectedStatus) {
        VerifyAuftragDatenStatusAction action = new VerifyAuftragDatenStatusAction(wbciCommonService, auftragDatenDAO, expectedStatus);
        action.setActor(hurricanTestActor);

        testBuilder.repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
        return action;
    }

    /**
     * Verifies that the assigned order of the current VA has a 'Bauauftrag' assigned with the given {@code anlassId}
     * and {@code bauauftragStatusId}
     *
     * @param anlassId
     * @return
     */
    public VerifyBauauftragExistTestAction verifyBauauftragExist(Long anlassId, Long bauauftragStatusId) {
        VerifyBauauftragExistTestAction action = new VerifyBauauftragExistTestAction(wbciCommonService,
                verlaufDAO, anlassId, bauauftragStatusId);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers all scheduled requests and verifies that the sendAfter property for each of them is set to null.
     *
     * @return
     */
    public TriggerSchedulerTestAction triggerScheduled() {
        TriggerSchedulerTestAction action = new TriggerSchedulerTestAction(wbciSchedulerService, wbciDao);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Clears all requests scheduled to be sent. The scheduled request's <i>processedAt</i> attribute is set to now. As
     * a result the request is skipped, next time the scheduler is triggered.
     *
     * @return
     */
    public ClearScheduledRequestsTestAction clearScheduledRequests() {
        ClearScheduledRequestsTestAction action = new ClearScheduledRequestsTestAction(wbciSchedulerService, wbciDao);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers automated processing of RUME-VA inbound requests
     *
     * @return
     */
    public TriggerAutomatedRuemVaProcessingTestAction triggerAutomatedRuemVaProcessing() {
        TriggerAutomatedRuemVaProcessingTestAction action = new TriggerAutomatedRuemVaProcessingTestAction(wbciAutomationService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers automated processing of outgoing RUME-VA inbound requests
     *
     * @return
     */
    public TriggerAutomatedOutgoingRuemVaProcessingTestAction triggerAutomatedOutgoingRuemVaProcessing() {
        TriggerAutomatedOutgoingRuemVaProcessingTestAction action =
                new TriggerAutomatedOutgoingRuemVaProcessingTestAction(wbciAutomationDonatingService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers automated processing of AKM-TR messages
     *
     * @return
     */
    public TriggerAutomatedAkmTrProcessingTestAction triggerAutomatedAkmTrProcessing() {
        TriggerAutomatedAkmTrProcessingTestAction action = new TriggerAutomatedAkmTrProcessingTestAction(wbciAutomationService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    /**
     * Triggers automated processing of incoming AKM-TR messages
     *
     * @return
     */
    public TriggerAutomatedIncomingAkmTrProcessingTestAction triggerAutomatedIncomingAkmTrProcessing() {
        TriggerAutomatedIncomingAkmTrProcessingTestAction action = new TriggerAutomatedIncomingAkmTrProcessingTestAction(wbciAutomationService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    /**
     * Triggers automated processing of ERLM-TVs inbound requests
     *
     * @return
     */
    public TriggerAutomatedErlmTvProcessingTestAction triggerAutomatedErlmTvProcessing() {
        TriggerAutomatedErlmTvProcessingTestAction action = new TriggerAutomatedErlmTvProcessingTestAction(wbciAutomationService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    /**
     * Triggers automated processing of STR-AUFH ERLMs (M-net = donating carrier)
     *
     * @return
     */
    public TriggerAutomatedStrAufhErlmDonatingProcessingTestAction triggerAutomatedStrAufhErlmDonatingProcessing() {
        TriggerAutomatedStrAufhErlmDonatingProcessingTestAction action =
                new TriggerAutomatedStrAufhErlmDonatingProcessingTestAction(wbciAutomationDonatingService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers automated processing of STR-AUFH ERLMs
     *
     * @return
     */
    public TriggerAutomatedStrAufhErlmProcessingTestAction triggerAutomatedStrAufhErlmProcessing() {
        TriggerAutomatedStrAufhErlmProcessingTestAction action = new TriggerAutomatedStrAufhErlmProcessingTestAction(wbciAutomationService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Prevents all AKM-TRs that are capable of being processed automatically, from being automatically processed, by
     * setting the klaerfall flag on the GF.
     *
     * @return
     */
    public ResetAutomatedAkmTrProcessingTestAction resetAutomatedAkmTrProcessing() {
        ResetAutomatedAkmTrProcessingTestAction action = new ResetAutomatedAkmTrProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Prevents all RUEM-VAs that are capable of being processed automatically, from being automatically processed, by
     * setting the klaerfall flag on the GF.
     *
     * @return
     */
    public ResetAutomatedRuemVaProcessingTestAction resetAutomatedRuemVaProcessing() {
        ResetAutomatedRuemVaProcessingTestAction action = new ResetAutomatedRuemVaProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Prevents all RUEM-VAs that are capable of being processed automatically, from being automatically processed, by
     * setting the klaerfall flag on the GF.
     *
     * @return
     */
    public ResetAutomatedOutgoingRuemVaProcessingTestAction resetAutomatedOutgoingRuemVaProcessing() {
        ResetAutomatedOutgoingRuemVaProcessingTestAction action = new ResetAutomatedOutgoingRuemVaProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Selects all hurrican orders for the given billing order no and changes the state of {@link AuftragDaten} to
     * {@link AuftragStatus#STORNO}
     *
     * @param billingOrderNoOrig
     * @return
     */
    public CancelHurricanOrdersTestAction cancelHurricanOrders(Long billingOrderNoOrig) {
        CancelHurricanOrdersTestAction action = new CancelHurricanOrdersTestAction(auftragDatenDAO, billingOrderNoOrig);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Prevents all ERLM-TVs that are capable of being processed automatically from being automatically processed, by
     * setting the klaerfall flag on the GF
     *
     * @return
     */
    public ResetAutomatedErlmTvProcessingTestAction resetAutomatedErlmTvProcessing() {
        ResetAutomatedErlmTvProcessingTestAction action = new ResetAutomatedErlmTvProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }


    /**
     * Prevents all STR-AUFH ERLMs that are capable of being processed automatically from being automatically processed,
     * by setting the klaerfall flag on the GF
     *
     * @return
     */
    public ResetAutomatedStrAufhErlmProcessingTestAction resetAutomatedErlmStrAufhProcessing() {
        ResetAutomatedStrAufhErlmProcessingTestAction action = new ResetAutomatedStrAufhErlmProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Prevents all STR-AUFH ERLMs (with M-net = donating carrier) that are capable of being processed automatically
     * from being automatically processed, by setting the klaerfall flag on the GF
     *
     * @return
     */
    public ResetAutomatedStrAufhErlmDonatingProcessingTestAction resetAutomatedErlmStrAufhDonatingProcessing() {
        ResetAutomatedStrAufhErlmDonatingProcessingTestAction action = new ResetAutomatedStrAufhErlmDonatingProcessingTestAction(
                wbciDao, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    public void triggerProcessPendingEmails() {
        AbstractWbciTestAction action = new AbstractWbciTestAction("triggerProcessPendingEmails") {
            @Override
            public void doExecute(TestContext context) {
                new SimpleAsyncTaskExecutor().execute(
                        () -> {
                            try {
                                mailService.processPendingEmails();
                            }
                            catch (ProcessPendingEmailsException e) {
                                throw new CitrusRuntimeException(e);
                            }
                        }
                );
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
    }


    /**
     * Sends the internal deadline overview report.
     */
    public SendInternalverviewReportAction sendInternalOverviewReport() {
        SendInternalverviewReportAction action = new SendInternalverviewReportAction(wbciEscalationService);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Sends the escalation overview report.
     */
    public SendCarrierEscalationOverviewReportAction sendCarrierEscalationOverviewReport() {
        SendCarrierEscalationOverviewReportAction action = new SendCarrierEscalationOverviewReportAction(wbciEscalationService);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Sends the escalation carrier reports.
     */
    public SendCarrierSpecificEscalationReportsAction sendCarrierSpecificEscalationReports() {
        SendCarrierSpecificEscalationReportsAction action = new SendCarrierSpecificEscalationReportsAction(wbciEscalationService);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Sends an escalation mail for the current VA from the test context.
     */
    public SendEscalationMailForVaTestAction sendEscalationMailForVaTestAction(AKUser user) {
        SendEscalationMailForVaTestAction action = new SendEscalationMailForVaTestAction(wbciEscalationService, user);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the VA Request has been scheduled or not.
     *
     * @param expectedScheduled the status to match against
     * @return
     */
    public VerifyVaRequestScheduledAction assertVaRequestScheduled(boolean expectedScheduled) {
        VerifyVaRequestScheduledAction action = new VerifyVaRequestScheduledAction(wbciCommonService, wbciDao, expectedScheduled);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the TV Request has been scheduled or not.
     *
     * @param expectedScheduled the status to match against
     * @return
     */
    public VerifyTvRequestScheduledAction assertTvRequestScheduled(boolean expectedScheduled) {
        VerifyTvRequestScheduledAction action = new VerifyTvRequestScheduledAction(wbciCommonService, wbciDao, expectedScheduled);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Storno Request has been scheduled or not.
     *
     * @param expectedScheduled the status to match against
     * @return
     */
    public VerifyStornoRequestScheduledAction assertStornoRequestScheduled(boolean expectedScheduled) {
        VerifyStornoRequestScheduledAction action = new VerifyStornoRequestScheduledAction(wbciCommonService, wbciDao, expectedScheduled);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the Storno Request status matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyStornoRequestStatusAction assertStornoRequestStatus(WbciRequestStatus expectedStatus) {
        VerifyStornoRequestStatusAction action = new VerifyStornoRequestStatusAction(wbciCommonService, expectedStatus);
        // set atlas esb test actor here as we do not want to verify Storno-Request status during kft test run
        // this could cause runtime racing conditions in kft
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verifies that the TV Request status matches the supplied {@code expectedStatus}
     *
     * @param expectedStatus the status to match against
     * @return
     */
    public VerifyTvRequestStatusAction assertTvRequestStatus(WbciRequestStatus expectedStatus) {
        VerifyTvRequestStatusAction action = new VerifyTvRequestStatusAction(wbciCommonService, expectedStatus);
        // set atlas esb test actor here as we do not want to verify TV-Request status during kft test run
        // this could cause runtime racing conditions in kft
        action.setActor(atlasEsbTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verify that an {@link de.mnet.wita.model.IoArchive} entry with the specified values is created for the current
     * VorabstimmungsId
     *
     * @param ioType
     * @param geschaeftsfallTyp
     * @return
     */
    public RepeatOnErrorUntilTrueDefinition assertIoArchiveEntryCreated(IOType ioType,
            GeschaeftsfallTyp geschaeftsfallTyp) {
        return assertIoArchiveEntryCreated(ioType, geschaeftsfallTyp, null);
    }

    /**
     * Verify that th current preagreement has been successfully assigned to the taifun oder number.
     */
    public VerifyWbciIncomingRequestAssignedToOrderAction assertVaRequestAssignedToOrder(Long billingOrderNoOrig) {
        VerifyWbciIncomingRequestAssignedToOrderAction action =
                new VerifyWbciIncomingRequestAssignedToOrderAction(wbciCommonService, billingOrderNoOrig);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verify that the current VA has been successfully assigned to the taifun oder number and has the expected {@link
     * KundenTyp}.
     */
    public RepeatOnErrorUntilTrueDefinition assertVaRequestAssignedToOrder(Long billingOrderNoOrig, KundenTyp customerTyp) {
        VerifyWbciIncomingRequestAssignedToOrderAction action =
                new VerifyWbciIncomingRequestAssignedToOrderAction(wbciCommonService, billingOrderNoOrig, customerTyp);
        action.setActor(hurricanTestActor);
        return testBuilder.repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
    }

    /**
     * Verify that the current VA has not been assigned to the taifun order.
     *
     * @return
     */
    public VerifyWbciIncomingRequestNotAssignedToOrderAction assertVaRequestNotAssignedToOrder() {
        VerifyWbciIncomingRequestNotAssignedToOrderAction action =
                new VerifyWbciIncomingRequestNotAssignedToOrderAction(wbciCommonService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Verify that the current VA has been successfully assigned to the taifun oder number and has the expected {@link
     * KundenTyp}.
     */
    public VerifyNonBillingRelevantOrdersAssignedAction assertNonBillingRelevantTaifunOrderIds(Long... nonBillingRelevantOrderNoOrigs) {
        VerifyNonBillingRelevantOrdersAssignedAction action =
                new VerifyNonBillingRelevantOrdersAssignedAction(wbciCommonService, nonBillingRelevantOrderNoOrigs);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Verify that an io archive entry is present with given properties and meldungs typ.
     *
     * @param ioType
     * @param geschaeftsfallTyp
     * @param meldungsTyp
     * @return
     */
    public <E extends Enum> RepeatOnErrorUntilTrueDefinition assertIoArchiveEntryCreated(IOType ioType,
            GeschaeftsfallTyp geschaeftsfallTyp, E meldungsTyp, int countOfEntries, boolean longVerify) {
        VerifyIoArchiveEntryCreatedAction verifyIoArchiveEntryCreatedAction = new VerifyIoArchiveEntryCreatedAction(
                historyService,
                ioType,
                geschaeftsfallTyp,
                meldungsTyp, countOfEntries);
        verifyIoArchiveEntryCreatedAction.setActor(hurricanTestActor);

        // repeatOnError: max. ${dbMaxRetryShort}x mit je ${dbSleepTimeShort} Sek. sleep zwischen den Aufrufen
        if (longVerify) {
            return testBuilder.repeatOnError(verifyIoArchiveEntryCreatedAction)
                    .autoSleep(dbSleepTimeLong)
                    .until(String.format("i gt %s", dbMaxRetryLong));
        }
        else {
            return testBuilder.repeatOnError(verifyIoArchiveEntryCreatedAction)
                    .autoSleep(dbSleepTimeShort)
                    .until(String.format("i gt %s", dbMaxRetryShort));
        }
    }

    /**
     * see {@link #assertIoArchiveEntryCreated(de.mnet.wbci.model.IOType, de.mnet.wbci.model.GeschaeftsfallTyp, Enum,
     * int, boolean)} for countOfEntries = 1.
     */
    public <E extends Enum> RepeatOnErrorUntilTrueDefinition assertIoArchiveEntryCreated(IOType ioType,
            GeschaeftsfallTyp geschaeftsfallTyp, E meldungsTyp) {
        return assertIoArchiveEntryCreated(ioType, geschaeftsfallTyp, meldungsTyp, 1, false);
    }

    /**
     * Asserts that no IOArchive entry exists matching the supplied parameters
     *
     * @param ioType
     * @param geschaeftsfallTyp
     * @param meldungsTyp
     */
    public <E extends Enum> void assertNoIoArchiveEntryCreated(IOType ioType,
            GeschaeftsfallTyp geschaeftsfallTyp, E meldungsTyp) {
        testBuilder.action(new AssertNoIoArchiveEntryCreated(historyService,
                ioType,
                geschaeftsfallTyp,
                meldungsTyp));
    }

    /**
     * Search for all pending escalation emails in the table 'T_MAIL' and verifies that an e-mail with the assigned
     * subject is included.
     *
     * @param subject subject of the escalation e-mail
     */
    public void assertEscalationReportEmailEntry(final String subject) {
        String from = wbciDao.findById(REGID_MAIL_FROM_HURRICAN, Registry.class).getStringValue();
        String to = wbciDao.findById(REGID_WBCI_ESCALATION_TO, Registry.class).getStringValue();
        assertEscalationEmailEntry(from, to, subject);
    }

    /**
     * Search for all pending escalation emails in the table 'T_MAIL' and verifies that an e-mail with the assigned
     * subject is included.
     *
     * @param from    sender e-mail address
     * @param to      receiver e-mail address
     * @param subject subject of the escalation e-mail
     */
    public void assertEscalationEmailEntry(final String from, final String to, final String subject) {
        AbstractWbciTestAction emailEntryAction = new AssertEscalationEmailEntryAction(mailDAO, from, to, subject);
        emailEntryAction.setActor(hurricanTestActor);
        testBuilder.action(emailEntryAction);
    }

    public void createEscalationContactForCarrierIfNotPresent(final CarrierCode carrierCode) {
        AbstractWbciTestAction testAction = new AbstractWbciTestAction("createExcalationContact for '" + carrierCode + "'") {
            @Override
            public void doExecute(TestContext testContext) {
                de.augustakom.hurrican.model.cc.Carrier carrier = getCarrier(carrierCode);
                createOrUpdateExcalationContact(carrier);
            }

            private de.augustakom.hurrican.model.cc.Carrier getCarrier(final CarrierCode carrierCode) {
                de.augustakom.hurrican.model.cc.Carrier carrier = new de.augustakom.hurrican.model.cc.Carrier();
                carrier.setCudaKuendigungNotwendig(null);
                carrier.setItuCarrierCode(carrierCode.getITUCarrierCode());
                final List<de.augustakom.hurrican.model.cc.Carrier> carrierList =
                        carrierbestellungDAO.queryByExample(carrier, de.augustakom.hurrican.model.cc.Carrier.class);
                if (CollectionUtils.isEmpty(carrierList) || carrierList.size() > 1) {
                    throw new IllegalStateException(String.format("Es konnte keinen eindeutigen Eintrag für den " +
                            "Carrier mit ITUCode '%s' gefunden werden!", carrierCode.getITUCarrierCode()));
                }
                return carrierList.get(0);
            }

            private void createOrUpdateExcalationContact(de.augustakom.hurrican.model.cc.Carrier carrier) {
                CarrierContact carrierContact = new CarrierContactBuilder()
                        .withCarrier(carrier)
                        .withContactType(CarrierContact.TYP_WBCI_ESCALATION_CONTACT)
                        .build();
                final List<CarrierContact> carrierEscalationContacts =
                        carrierbestellungDAO.queryByExample(carrierContact, CarrierContact.class);
                if (!CollectionUtils.isEmpty(carrierEscalationContacts)) {
                    carrierContact = carrierEscalationContacts.get(0);
                }
                else {
                    carrierContact.setBranchOffice("Niederlassung");
                }
                carrierContact.setFaultClearingEmail(DEFAULT_CARRIER_ESCALATION_EMAIL);
                carrierbestellungDAO.store(carrierContact);
            }
        };
        testAction.setActor(hurricanTestActor);
        testBuilder.action(testAction);
    }

    /**
     * Tests that {@link de.mnet.wbci.model.VorabstimmungsAnfrage#getLastMeldungCodes()} matches the expected
     * Meldungscodes.
     *
     * @param expectedMeldungsCodes an unlimited number of Meldungscodes as {@link String}
     */
    public void assertVaMeldungsCodes(final MeldungsCode... expectedMeldungsCodes) {
        AbstractWbciTestAction meldungsCodeTestAction = new AbstractWbciTestAction("assertVaMeldungsCodes") {

            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                final VorabstimmungsAnfrage va = wbciCommonService.findWbciRequestByType(vorabstimmungsId,
                        VorabstimmungsAnfrage.class).get(0);
                assertLastMeldungSet(va);
                assertMeldungCodesMatch(va, expectedMeldungsCodes);
            }

        };
        meldungsCodeTestAction.setActor(atlasEsbTestActor);
        testBuilder.action(meldungsCodeTestAction);
    }

    /**
     * Tests that {@link de.mnet.wbci.model.WbciRequest#getLastMeldungCodes()} matches the expected Meldungscodes.
     *
     * @param expectedMeldungsCodes an unlimited number of Meldungscodes as {@link String}
     */
    public void assertTvMeldungsCodes(final MeldungsCode... expectedMeldungsCodes) {
        AbstractWbciTestAction meldungsCodeTestAction = new AbstractWbciTestAction("assertTvMeldungsCodes") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                final String changeId = getChangeId(context);
                final WbciRequest request = wbciCommonService.findWbciRequestByChangeId(vorabstimmungsId, changeId);
                assertLastMeldungSet(request);
                assertMeldungCodesMatch(request, expectedMeldungsCodes);
            }
        };
        meldungsCodeTestAction.setActor(atlasEsbTestActor);
        testBuilder.action(meldungsCodeTestAction);
    }

    /**
     * Tests that {@link de.mnet.wbci.model.WbciRequest#getLastMeldungCodes()} matches the expected Meldungscodes.
     *
     * @param expectedMeldungsCodes an unlimited number of Meldungscodes as {@link String}
     */
    public void assertStornoMeldungsCodes(final MeldungsCode... expectedMeldungsCodes) {
        AbstractWbciTestAction meldungsCodeTestAction = new AbstractWbciTestAction("assertStornoMeldungsCodes") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                final String stornoId = getStornoId(context);
                final WbciRequest request = wbciCommonService.findWbciRequestByChangeId(vorabstimmungsId, stornoId);
                assertLastMeldungSet(request);
                assertMeldungCodesMatch(request, expectedMeldungsCodes);
            }
        };
        meldungsCodeTestAction.setActor(atlasEsbTestActor);
        testBuilder.action(meldungsCodeTestAction);
    }

    /**
     * asserts if {@link de.mnet.wbci.model.WbciRequest#getLastMeldungCodes()}, {@link
     * de.mnet.wbci.model.WbciRequest#getLastMeldungDate()} and {@link WbciRequest#lastMeldungType} are set.
     *
     * @param request wbci request
     */
    private void assertLastMeldungSet(WbciRequest request) {
        Assert.assertNotNull(request.getLastMeldungCodes(), "lastMeldungCode has to be set!");
        Assert.assertNotNull(request.getLastMeldungDate(), "lastMeldungDate has to be set!");
        Assert.assertNotNull(request.getLastMeldungType(), "lastMeldungType has to be set!");
    }

    private void assertMeldungCodesMatch(WbciRequest request, final MeldungsCode... expectedMeldungsCodes) {
        Set<MeldungsCode> codes = new HashSet<>(MeldungsCodeConverter.retrieveMeldungsCodes(request
                .getLastMeldungCodes()));
        Assert.assertNotNull(codes);
        assertEquals(codes.size(), expectedMeldungsCodes.length);
        assertTrue(codes.containsAll(Arrays.asList(expectedMeldungsCodes)));
    }

    /**
     * Verify if that the {@link Rufnummernblock}s are included in the last {@link RueckmeldungVorabstimmung}.
     *
     * @param rufnummernbloecke an endless number of {@link Rufnummernblock}s
     */
    public void assertRufnummernblock(final Rufnummernblock... rufnummernbloecke) {
        testBuilder.action(new AbstractWbciTestAction("assertRufnummernblock") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                final RueckmeldungVorabstimmung lastRuemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                        RueckmeldungVorabstimmung.class);

                Assert.assertNotNull(lastRuemVa);
                assertTrue(lastRuemVa.getRufnummernportierung() instanceof RufnummernportierungAnlage);

                for (Rufnummernblock expectedRB : rufnummernbloecke) {
                    assertTrue(containsRufnummernBlockNumbers(
                            expectedRB,
                            ((RufnummernportierungAnlage) lastRuemVa.getRufnummernportierung()).getRufnummernbloecke()));
                }
            }
        });
    }

    /**
     * Verify if that the {@link RufnummerOnkz}s are included in the last {@link RueckmeldungVorabstimmung}.
     *
     * @param rufnummernOnkzs an endless number of {@link RufnummerOnkz}s
     */
    public void assertRufnummerOnkzEinzel(final RufnummerOnkz... rufnummernOnkzs) {
        testBuilder.action(new AbstractWbciTestAction("assertRufnummerOnkzEinzel") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                final RueckmeldungVorabstimmung lastRuemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                        RueckmeldungVorabstimmung.class);

                Assert.assertNotNull(lastRuemVa);
                assertTrue(lastRuemVa.getRufnummernportierung() instanceof RufnummernportierungEinzeln);

                for (RufnummerOnkz expectedOnkz : rufnummernOnkzs) {
                    assertTrue(containsRufnummernEinzel(
                            expectedOnkz,
                            ((RufnummernportierungEinzeln) lastRuemVa.getRufnummernportierung()).getRufnummernOnkz()));
                }

            }
        });
    }

    /**
     * Asserts that the number of requests of the supplied {@code requestTyp} matches the supplied {@code
     * expectedNumber}
     */
    public TestAction assertNumberOfRequests(final Class requestTyp, final int expectedNumber) {
        TestAction action = new AbstractWbciTestAction("assertNumberOfRequests") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                List<?> requests = wbciDao.findWbciRequestByType(vorabstimmungsId, requestTyp);
                assertEquals(requests.size(), expectedNumber);
            }
        };

        testBuilder.action(action);
        return action;
    }

    public AbstractTestAction addMailSenderAndReceipentToContext() {
        final AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                testContext.setVariable(VariableNames.ESCALATION_MAIL_SENDER,
                        wbciDao.findById(REGID_MAIL_FROM_HURRICAN, Registry.class).getStringValue());
                testContext.setVariable(VariableNames.ESCALATION_MAIL_RECIPIENTS,
                        wbciDao.findById(REGID_WBCI_ESCALATION_TO, Registry.class).getStringValue());
            }
        };
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Construct receive mail test action. Important: this action is not added to test case by default as this action is
     * also executed directly by manual execution.
     *
     * @param from
     * @param to
     * @param subjects
     * @return
     */
    public ReceiveMessageAction receiveEmail(final String from, final String to, final List<String> subjects,
            final String subjectPattern, final int subjectGroup) {
        ReceiveMessageAction action = new ReceiveMessageAction();
        action.setEndpoint(citrusSmtpServer);
        action.setValidationCallback(new AbstractValidationCallback() {
            @Override
            public void validate(Object payload, Map<String, Object> headers) {
                Assert.assertNotNull(headers);
                assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_FROM), from);
                assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_TO), to);
                assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_CC), "");
                assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_BCC), "");

                // Subjects
                boolean found = false;
                Matcher mailMatcher = Pattern.compile(subjectPattern)
                        .matcher((String) headers.get(CitrusMailMessageHeaders.MAIL_SUBJECT));
                assertEquals(mailMatcher.find(), true);
                for (String subject : subjects) {
                    Matcher subjectMatcher = Pattern.compile(subjectPattern).matcher(subject);
                    assertEquals(subjectMatcher.find(), true);
                    if (mailMatcher.group(subjectGroup).equals(subjectMatcher.group(subjectGroup))) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, String.format("Unable to verify email subject: %s", mailMatcher.group(subjectGroup)));
            }
        });
        action.setName("receiveEmailAction");
        return action;
    }

    /**
     * Acknowledge mail with response code and message.
     *
     * @param code
     * @param message
     * @return
     */
    public SendMessageAction ackEmail(final int code, final String message) {
        MailResponse response = new MailResponse();
        response.setCode(code);
        response.setMessage(message);

        SendMessageAction action = new SendMessageAction();
        action.setEndpoint(citrusSmtpServer);
        action.setMessageBuilder(new StaticMessageContentBuilder(buildMailMessage(response)));
        action.setName("ackEmailAction");

        return action;
    }

    public void receiveAllEmails(final String subject, final String subjectPattern, final int subjectGroup) {
        String from = wbciDao.findById(REGID_MAIL_FROM_HURRICAN, Registry.class).getStringValue();
        String to = wbciDao.findById(REGID_WBCI_ESCALATION_TO, Registry.class).getStringValue().replace(";", ",");
        final int numberOfRecipients = StringUtils.commaDelimitedListToSet(to).size();
        for (int i = 0; i < numberOfRecipients; i++) {
            testBuilder.repeatOnError(receiveEmail(from, to, subject, subjectPattern, subjectGroup))
                    .autoSleep(dbSleepTimeShort)
                    .until(String.format("i gt %s", dbMaxRetryShort));
            ackEmail();
        }
    }

    public ReceiveMessageActionDefinition receiveEmail(final String from, final String to, final String subject,
            final String subjectPattern, final int subjectGroup) {
        return testBuilder.receive(citrusSmtpServer)
                .validationCallback(new AbstractValidationCallback() {
                    @Override
                    public void validate(Object payload, Map<String, Object> headers) {
                        Assert.assertNotNull(headers);
                        assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_FROM), from);
                        assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_TO), to);
                        assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_CC), "");
                        assertEquals(headers.get(CitrusMailMessageHeaders.MAIL_BCC), "");
                        // Subject
                        Matcher subjectMatcher = Pattern.compile(subjectPattern).matcher(subject);
                        Matcher mailMatcher = Pattern.compile(subjectPattern)
                                .matcher((String) headers.get(CitrusMailMessageHeaders.MAIL_SUBJECT));
                        assertEquals(subjectMatcher.find(), true);
                        assertEquals(mailMatcher.find(), true);
                        assertEquals(mailMatcher.group(subjectGroup), subjectMatcher.group(subjectGroup));
                    }
                });
    }

    public SendMessageActionDefinition ackEmail() {
        return testBuilder.send(citrusSmtpServer)
                .message(buildMailMessage(new MailResponse()));
    }

    private Message buildMailMessage(MailResponse mailMessageResponse) {
        return new DefaultMessage(mailMessageResponse) {
            private static final long serialVersionUID = 1L;

            @Override
            public String toString() {
                //Fix for Citrus 2.0 to string conversion exception when using object payloads
                //TODO remove when updating to Citrus 2.0.1
                return String.format("%s [payload: %s][headers: %s]", getClass().getSimpleName().toUpperCase(), getPayload().toString(), copyHeaders());
            }
        };
    }

    /**
     * Creates new preAgreementId from database using the wbci common service.
     *
     * @param requestTyp
     * @return
     */
    public String createPreAgreementId(RequestTyp requestTyp) {
        return wbciCommonService.getNextPreAgreementId(requestTyp);
    }

    /**
     * Creates new preAgreementId for given carrier. In case carrier is other than MNET the id is generated with random
     * String.
     *
     * @param requestTyp
     * @param carrier
     * @return
     */
    public String createPreAgreementId(RequestTyp requestTyp, Carrier carrier) {
        if (carrier.equals(Carrier.MNET)) {
            return createPreAgreementId(requestTyp);
        }

        return String.format("%s.%s%s", carrier.getCarrierCode(), requestTyp.getPreAgreementIdCode(),
                wbciCommonService.getNextSequence4RequestType(requestTyp));
    }

    /**
     * Assigns the supplied Taifun Order Id to the current VA
     *
     * @param tfOrderId
     */
    public void manuallyAssignTaifunOrderId(final Long tfOrderId) {
        testBuilder.action(new AbstractWbciTestAction("manuallyAssignTaifunOrderId") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                try {
                    wbciCommonService.assignTaifunOrder(vorabstimmungsId, tfOrderId, true);
                }
                catch (FindException e) {
                    throw new RuntimeException("Exception caught assigning Taifun Order Id", e);
                }
            }
        });
    }


    /**
     * Assigns the supplied {@code wbciGeschaeftsfallStatus} to the current VA
     *
     * @param wbciGeschaeftsfallStatus
     */
    public void changeGfStatus(final WbciGeschaeftsfallStatus wbciGeschaeftsfallStatus) {
        testBuilder.action(new AbstractWbciTestAction("manuallyAssignGeschaeftsfallStatus") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                try {
                    WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
                    gf.setStatus(wbciGeschaeftsfallStatus);

                    wbciDao.store(gf);
                }
                catch (Exception e) {
                    throw new RuntimeException("Exception caught changing the GF status", e);
                }
            }
        });
    }

    /**
     * Assigns the supplied {@link Technologie} to the current VA
     *
     * @param technologie
     */
    public void changeMnetTechnologie(final Technologie technologie) {
        testBuilder.action(new AbstractWbciTestAction("manuallyAssignMnetTechnologie") {
            @Override
            public void doExecute(TestContext testContext) {
                try {
                    WbciGeschaeftsfall gf = wbciCommonService.findWbciGeschaeftsfall(getVorabstimmungsId(testContext));
                    gf.setMnetTechnologie(technologie);
                    wbciDao.store(gf);
                }
                catch (Exception e) {
                    throw new RuntimeException("Exception caught changing the GF status", e);
                }
            }
        });
    }

    /**
     * Aendert den Wechseltermin der letzten RUEM-VA sowie des Geschaeftsfalls auf das angegebene Datum.
     *
     * @param newChangeDate
     */
    public void changeWechseltermin(final LocalDateTime newChangeDate) {
        testBuilder.action(new AbstractWbciTestAction("changeRuemVaChangeDate") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                try {
                    final RueckmeldungVorabstimmung lastRuemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                            RueckmeldungVorabstimmung.class);
                    final LocalDateTime changeDate = DateTools.stripTimeFromDate(newChangeDate);
                    WbciGeschaeftsfall wbciGF;
                    if (lastRuemVa != null) {
                        lastRuemVa.setWechseltermin(changeDate.toLocalDate());
                        wbciDao.store(lastRuemVa);
                        wbciGF = lastRuemVa.getWbciGeschaeftsfall();
                    }
                    else {
                        wbciGF = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
                    }
                    wbciGF.setWechseltermin(changeDate.toLocalDate());
                    wbciDao.store(wbciGF);
                }
                catch (Exception e) {
                    throw new CitrusRuntimeException("Exception caught while changing the RUEM-VA change date", e);
                }
            }
        });
    }

    /**
     * Aendert den {@link VorabstimmungsAnfrage#creationDate} auf das angegebene Datum.
     */
    public void changeCreationDate(final LocalDateTime newCreationDate) {
        testBuilder.action(new AbstractWbciTestAction("changeVAReceivedDate") {
            @Override
            public void doExecute(TestContext testContext) {
                final String vorabstimmungsId = getVorabstimmungsId(testContext);
                final List<VorabstimmungsAnfrage> vaRequests = wbciCommonService.findWbciRequestByType(vorabstimmungsId, VorabstimmungsAnfrage.class);
                assertEquals(vaRequests.size(), 1, "expected exactly one VA for " + vorabstimmungsId);
                vaRequests.stream().findFirst().ifPresent(va -> {
                    try {
                        va.setCreationDate(Date.from(newCreationDate.atZone(ZoneId.systemDefault()).toInstant()));
                        wbciDao.store(va);
                    }
                    catch (Exception e) {
                        throw new CitrusRuntimeException("Exception caught while changing the VA creation date", e);
                    }
                });
            }
        });
    }


    public CloseWbciGeschaeftsfallAction closeWbciGeschaeftsfallAction() {
        CloseWbciGeschaeftsfallAction action = new CloseWbciGeschaeftsfallAction(wbciCommonService, wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    public CloseWbciGeschaeftsfallProcessingAction closeWbciGeschaeftsfallProcessingAction() {
        CloseWbciGeschaeftsfallProcessingAction action = new CloseWbciGeschaeftsfallProcessingAction(wbciCommonService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    public AssignUserToWbciGeschaeftsfallAction assignUserToWbciGeschaeftsfall(final Long userId) {
        AssignUserToWbciGeschaeftsfallAction assignUserToWbciGeschaeftsfallAction =
                new AssignUserToWbciGeschaeftsfallAction(wbciCommonService, wbciDao, userId);
        assignUserToWbciGeschaeftsfallAction.setActor(hurricanTestActor);

        testBuilder.action(assignUserToWbciGeschaeftsfallAction);
        return assignUserToWbciGeschaeftsfallAction;
    }

    /**
     * Cleans up all unsent mails in table 'T_MAIL'
     */
    public void cleanUpUnsentEmailEntrys() {
        testBuilder.action(
                testBuilder.catchException(
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/03_cleanup_unsent_mails.sql")
                                .ignoreErrors(true)
                )
        );
    }


    /**
     * checks if the list contains a {@link RufnummerOnkz} with the same ONKZ and Rufnummer.
     *
     * @param rufnummerOnkz   expected {@link RufnummerOnkz}
     * @param rufnummernListe list to check
     * @return boolean
     */
    private boolean containsRufnummernEinzel(RufnummerOnkz rufnummerOnkz, List<RufnummerOnkz> rufnummernListe) {
        for (RufnummerOnkz nextRufNr : rufnummernListe) {
            if (nextRufNr.getRufnummer().equals(rufnummerOnkz.getRufnummer()) &&
                    nextRufNr.getOnkz().equals(rufnummerOnkz.getOnkz())) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the list contains a {@link Rufnummernblock} with the same RnrBlockVon and the same RnrBlockBis.
     *
     * @param rb     expected {@link Rufnummernblock}
     * @param listRB list to check
     * @return boolean
     */
    private boolean containsRufnummernBlockNumbers(Rufnummernblock rb, List<Rufnummernblock> listRB) {
        for (Rufnummernblock nextRB : listRB) {
            if (nextRB.getRnrBlockVon().equals(rb.getRnrBlockVon())
                    && nextRB.getRnrBlockBis().equals(rb.getRnrBlockBis())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns the provided taifunOrderId to the Voarbstimmungsanfrage in the TestContext
     *
     * @param taifunOrderId
     */
    public void assignTaifunOrderAndRejectVa(final Long taifunOrderId) {
        testBuilder.action(new AbstractWbciTestAction("assignAndReject") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                try {
                    wbciGeschaeftsfallService.assignTaifunOrderAndRejectVA(vorabstimmungsId, taifunOrderId);
                }
                catch (FindException e) {
                    e.printStackTrace();
                    Assert.fail("FindException thrown within assignTaifunOrderAndRejectVA!");
                }
            }
        });
    }

    /**
     * Closes the WbciGeschaeftsfall issue using the provided comment (i.e. sets the GF 'Klaerfall' flag to false).
     *
     * @param comment
     */
    public CloseGeschaefsfallIssueAction closeGeschaefsfallIssue(final String comment) {
        CloseGeschaefsfallIssueAction action = new CloseGeschaefsfallIssueAction(wbciCommonService, wbciGeschaeftsfallService, comment);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    /**
     * Trigger the Wbci Housekeeping job
     */
    public TriggerWbciHousekeepingTestAction triggerWbciHousekeeping() {
        TriggerWbciHousekeepingTestAction action = new TriggerWbciHousekeepingTestAction(wbciGeschaeftsfallService);
        action.setActor(hurricanTestActor);

        testBuilder.action(action);
        return action;
    }

    public AbstractTestAction enrichGeschaeftsfallWithStrAenPreagreementId(final WbciGeschaeftsfall wbciGeschaeftsfall) {
        AbstractTestAction action = new AbstractWbciTestAction("enrichGeschaeftsfallWithStrAenPreagreementId") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                wbciGeschaeftsfall.setStrAenVorabstimmungsId(vorabstimmungsId);
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Updates the provided variable with the specified value.
     */
    public AbstractTestAction updateVariable(final String variableName, final Object value) {
        AbstractTestAction action = new AbstractWbciTestAction("updateVariable") {
            @Override
            public void doExecute(TestContext context) {
                context.setVariable(variableName, value);
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public void assertKlaerfallStatus(final boolean klaerfallStatus, final String expectedReason) {
        String actionName = klaerfallStatus ? "assertMarkedForClarification" : "assertNotMarkedForClarification";
        AbstractTestAction action = new AbstractWbciTestAction(actionName) {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
                assertEquals(wbciGeschaeftsfall.getKlaerfall(), (Boolean) klaerfallStatus,
                        String.format("Klaerfall status not as expected, expected '%s' but was '%s'", klaerfallStatus, wbciGeschaeftsfall.getKlaerfall()));
                if (klaerfallStatus && expectedReason != null) {
                    assertTrue(wbciGeschaeftsfall.getBemerkungen().matches(expectedReason),
                            String.format("Die Bemerkung '%s' entspricht nicht dem erwarteten Pattern '%s'",
                                    wbciGeschaeftsfall.getBemerkungen(), expectedReason)
                    );
                }
            }
        };
        action.setActor(atlasEsbTestActor);

        testBuilder
                .repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort));
    }

    public void assertBemerkung(final String expectedReason) {
        AbstractTestAction action = new AbstractWbciTestAction("assertGfBemerkung") {
            @Override
            public void doExecute(TestContext context) {
                final String vorabstimmungsId = getVorabstimmungsId(context);
                WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
                String gfBemerkung = wbciGeschaeftsfall.getBemerkungen();
                if (expectedReason == null) {
                    Assert.assertNull(gfBemerkung);
                }
                else {
                    Assert.assertNotNull(gfBemerkung);
                    assertTrue(gfBemerkung.matches(expectedReason),
                            String.format("Die Bemerkung '%s' entspricht nicht dem erwarteten Pattern '%s'",
                                    gfBemerkung, expectedReason)
                    );
                }
            }
        };
        action.setActor(atlasEsbTestActor);
        testBuilder.action(action);
    }

    /**
     * Prueft, ob ein ExceptionLog-Eintrag mit angegebenen Context NACH {@code occuredAfter} erstellt wurde.
     *
     * @param exceptionLogContext
     */
    public void saveLatestExceptionLogEntryId(final ExceptionLogEntryContext exceptionLogContext) {
        SaveLatestExceptionLogEntryIdAction action =
                new SaveLatestExceptionLogEntryIdAction(exceptionLogService, exceptionLogContext);

        action.setActor(hurricanTestActor);
        testBuilder.action(action);
    }

    /**
     * Prueft, ob ein ExceptionLog-Eintrag mit angegebenen Context NACH {@code occuredAfter} erstellt wurde.
     *
     * @param exceptionLogContext
     */
    public void assertWbciExceptionLogEntryCreated(final ExceptionLogEntryContext exceptionLogContext) {
        AssertExceptionLogEntryCreatedAction action =
                new AssertExceptionLogEntryCreatedAction(exceptionLogService, exceptionLogContext);
        action.setActor(hurricanTestActor);

        testBuilder
                .repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryShort * 4));
    }

    /**
     * Determines on the following logic if a SearchBuildRequest is expected:
     *
     * @param expectedWbciGeschaeftsfallForSearchBuilding
     */
    public void determineIsSearchBuildingsExpected(WbciGeschaeftsfall expectedWbciGeschaeftsfallForSearchBuilding) {
        DetermineIsSearchBuildingExpected<? extends WbciGeschaeftsfall> action;
        switch (expectedWbciGeschaeftsfallForSearchBuilding.getTyp()) {
            case VA_KUE_MRN:
                action = new DetermineIsSearchBuildingExpected<>(wbciVaKueMrnService, expectedWbciGeschaeftsfallForSearchBuilding);
                break;
            case VA_KUE_ORN:
                action = new DetermineIsSearchBuildingExpected<>(wbciVaKueOrnService, expectedWbciGeschaeftsfallForSearchBuilding);
                break;
            case VA_RRNP:
                action = new DetermineIsSearchBuildingExpected<>(wbciVaRrnpService, expectedWbciGeschaeftsfallForSearchBuilding);
                break;
            default:
                return;
        }
        action.setActor(hurricanTestActor);
        testBuilder.repeatOnError(action)
                .autoSleep(dbSleepTimeShort)
                .until(String.format("i gt %s", dbMaxRetryLong));
    }

    public VerifyAutomatableAction assertAutomatable(boolean autoprocessing) {
        VerifyAutomatableAction action = new VerifyAutomatableAction(wbciCommonService, autoprocessing);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public AbstractTestAction createHurricanMaxiDslHvtAuftrag(
            final Long billingOrderNoOrig,
            final Long customerId,
            final boolean withDtagPortAndCarrierbestellung,
            final boolean withInvalidCarrierbestellung) {

        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                new HurricanAuftragBuilder(ccAuftragDAO).builMaxiDslHvtAuftrag(
                        billingOrderNoOrig, customerId, withDtagPortAndCarrierbestellung, withInvalidCarrierbestellung);
            }
        };
        action.setName("createHurricanMaxiDslHvtAuftrag");
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CCAuftragDAO getCcAuftragDAO() {
        return ccAuftragDAO;
    }

}
