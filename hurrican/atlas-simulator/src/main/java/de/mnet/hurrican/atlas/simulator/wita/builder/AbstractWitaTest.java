package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.actions.SleepAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.definition.AbstractActionDefinition;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.RepeatOnErrorUntilTrueDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.endpoint.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.common.service.HistoryService;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.config.AtlasSimulatorConfiguration;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderMessageHeaders;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderServiceVersion;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.actions.CloseCbVorgangAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForAkmPvUserTaskStatusChangeAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForCbVorgangCreationAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForCbVorgangStatusChangeAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForIoArchiveEntryByContractIdAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForIoArchiveEntryByExtOrderNoAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForMnetWitaRequestSentByExtOrderNoAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WaitForMwfMeldungCreationByContractIdAction;
import de.mnet.hurrican.atlas.simulator.wita.actions.WriteVariablesTestAction;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

/**
 *
 */
public class AbstractWitaTest extends AbstractSimulatorTestBuilder {

    @Autowired
    @Qualifier("simInboundEndpoint")
    private Endpoint simInboundEndpoint;

    @Autowired
    @Qualifier("witaNotificationOutboundEndpoint")
    protected Endpoint notificationOutboundEndpoint;

    @Autowired
    @Qualifier("notificationInboundEndpoint")
    protected Endpoint notificationInboundEndpoint;

    @Autowired
    protected WbciDao wbciDao;

    /** Atlas cdm version. If set Atlas interface XPath and message templates will be used. */
    private WitaLineOrderServiceVersion serviceVersion;

    /** External order id injected by wita endpoint message handler */
    private String externalOrderId;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MwfEntityService mwfEntityService;

    @Autowired
    private CarrierElTALService carrierElTALService;

    @Autowired
    private EndstellenService endstellenService;

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    @Autowired
    private CCAuftragService auftragService;

    @Autowired
    private RufnummerService rufnummerService;

    @Autowired
    private WitaTalOrderService witaTalOrderService;

    /**
     * Receives new initial order request with automatic order id extraction from message payload.
     *
     * @param template the automatically added payload template.
     */
    protected ReceiveMessageActionDefinition receiveOrderREQ(String template) {
        return receiveOrderREQ(template, true);
    }

    /**
     * Receives new initial order request with automatic order id extraction from message payload.
     *
     * @param template            the automatically added payload template.
     * @param enableDatabaseCheck enable to check if hurrican order is created in the DB
     */
    protected ReceiveMessageActionDefinition receiveOrderREQ(String template, boolean enableDatabaseCheck) {
        if (simulatorConfiguration.isTemplateValidationActive()) {
            return (ReceiveMessageActionDefinition) receiveOrderREQ(enableDatabaseCheck)
                    .payload(getXmlTemplate(template))
                    .description("Wait for " + template + " ...");
        }
        else {
            LOGGER.warn("Template validation skipped due to simulator configuration");
            return (ReceiveMessageActionDefinition) receiveOrderREQ(enableDatabaseCheck)
                    .description("Wait for " + template + " ...");
        }
    }

    @Override
    public void execute(TestContext context) {
        // adds the the interface version as a variable in the test context so that its accessible to all tests as a variable
        if (serviceVersion != null) {
            context.setVariable(WitaLineOrderMessageHeaders.INTERFACE_VERSION, serviceVersion.getMajorVersion());
        }

        super.execute(context);
    }

    protected ReceiveMessageActionDefinition receiveOrderREQ() {
        return receiveOrderREQ(true);
    }

    protected ReceiveMessageActionDefinition receiveOrderREQ(boolean enableDatabaseCheck) {
        if (enableDatabaseCheck) {
            waitForCbVorgangCreation();
            writeVariablesTestAction();
        }
        return (ReceiveMessageActionDefinition) receive(simInboundEndpoint)
                .selector(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID + " = '" + externalOrderId + "'")
                .extractFromHeader(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID, WitaLineOrderVariableNames.EXTERNAL_ORDER_ID)
                .extractFromHeader(WitaLineOrderMessageHeaders.CONTRACT_ID, WitaLineOrderVariableNames.CONTRACT_ID)
                .extractFromHeader(WitaLineOrderMessageHeaders.CUSTOMER_ID, WitaLineOrderVariableNames.CUSTOMER_ID)
                .extractFromHeader(WitaLineOrderMessageHeaders.THIRD_PARTY_SALESMAN_CUSTOMER_ID, WitaLineOrderVariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID)
                .description("Wait for ORDER REQ ...");
    }

    /**
     * Adds receive action for error messages originating from Hurrican.
     */
    protected AbstractActionDefinition receiveError(String template) {
        return receiveError(template, WitaLineOrderMessageHeaders.JMS_HEADER_NAME_EXTERNAL_ORDER, getExternalOrderId());
    }

    @SuppressWarnings("unchecked")
    protected AbstractActionDefinition receiveError(String template, String jmsHeaderName, String jmsHeaderValue) {
        if (serviceVersion != null) {
            if (jmsHeaderName == null)
                return receive(notificationInboundEndpoint)
                        .payload(getXmlTemplate(template))
                        .timeout(simulatorConfiguration.getDefaultTimeout())
                        .description("Wait for ERROR MESSAGE ...");
            else {
                String messageSelector = String.format("%s='%s'", jmsHeaderName, jmsHeaderValue);
                return receive(notificationInboundEndpoint)
                        .selector(messageSelector)
                        .payload(getXmlTemplate(template))
                        .timeout(simulatorConfiguration.getDefaultTimeout())
                        .description("Wait for ERROR MESSAGE ... with selector " + messageSelector);
            }
        }
        else {
            return new AbstractActionDefinition(autoDelay(1000));
        }
    }

    /**
     * Adds receive action for intermediate notifications from Hurrican such as STORNO or TV.
     */
    protected ReceiveMessageActionDefinition receiveNotification() {
        return (ReceiveMessageActionDefinition) receive(notificationInboundEndpoint)
                .selector(WitaLineOrderMessageHeaders.EXTERNAL_ORDER_ID + " = '" + externalOrderId + "'")
                .timeout(simulatorConfiguration.getDefaultTimeout())
                .description("Wait for NOTIFICATION ...");
    }

    /**
     * Adds receive action for intermediate notifications from Hurrican such as STORNO or TV.
     */
    protected ReceiveMessageActionDefinition receiveNotification(String template) {
        if (simulatorConfiguration.isTemplateValidationActive()) {
            return (ReceiveMessageActionDefinition) receiveNotification()
                    .payload(getXmlTemplate(template))
                    .description("Wait for " + template + " ...");
        }
        else {
            LOGGER.warn("Template validation skipped due to simulator configuration");
            return (ReceiveMessageActionDefinition) receiveNotification()
                    .description("Wait for " + template + " ...");
        }
    }

    /**
     * Adds sending action for wita notification message.
     * @param template
     * @return
     */
    protected SendMessageActionDefinition sendNotification(String template) {
        return sendNotification(template, getNotificationSoapActionHeader(), getNotificationSoapAction());
    }

    /**
     * Adds sending action for wita notification message.
     */
    protected SendMessageActionDefinition sendNotification(String template, String soapActionHeader, String soapAction) {
        return (SendMessageActionDefinition) send(notificationOutboundEndpoint)
                .payload(getXmlTemplate(template))
                .header(soapActionHeader, soapAction)
                .description("Sending " + template);
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveEntryByContractId(MeldungsType meldungsType, Long numberOfEntries) {
        return repeatOnError(new WaitForIoArchiveEntryByContractIdAction(historyService, meldungsType, numberOfEntries))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveEntryByContractId(MeldungsType meldungsType) {
        return waitForIoArchiveEntryByContractId(meldungsType, 1L);
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveEntryByExtOrderNo(MeldungsType meldungsType, Long numberOfEntries) {
        return repeatOnError(new WaitForIoArchiveEntryByExtOrderNoAction(historyService, meldungsType, numberOfEntries))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    @SuppressWarnings("unchecked")
    <T extends MnetWitaRequest> RepeatOnErrorUntilTrueDefinition waitForMnetWitaRequestByExtOrder(Class<T> type) {
        return repeatOnError(new WaitForMnetWitaRequestSentByExtOrderNoAction(mwfEntityService, type))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    @SuppressWarnings("unchecked")
    <T extends Meldung> RepeatOnErrorUntilTrueDefinition waitForMwfMeldungCreationByContractId(Class<T> type) {
        return repeatOnError(new WaitForMwfMeldungCreationByContractIdAction(mwfEntityService, type))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    protected RepeatOnErrorUntilTrueDefinition waitForCbVorgangStatusChange(Long expectedStatus) {
        return repeatOnError(new WaitForCbVorgangStatusChangeAction(carrierElTALService, expectedStatus))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    protected RepeatOnErrorUntilTrueDefinition waitForCbVorgangClosed() {
        return waitForCbVorgangStatusChange(CBVorgang.STATUS_CLOSED);
    }

    protected RepeatOnErrorUntilTrueDefinition waitForAkmPvUserTaskStatusChange(UserTask.UserTaskStatus expectedStatus) {
        return repeatOnError(new WaitForAkmPvUserTaskStatusChangeAction(witaUsertaskService, expectedStatus))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveEntryByExtOrderNo(MeldungsType meldungsType) {
        return waitForIoArchiveEntryByExtOrderNo(meldungsType, 1L);
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveRequestEntry() {
        return waitForIoArchiveRequestEntry(1L);
    }

    protected RepeatOnErrorUntilTrueDefinition waitForIoArchiveRequestEntry(Long numberOfEntries) {
        return waitForIoArchiveEntryByExtOrderNo(null, numberOfEntries);
    }

    protected CloseCbVorgangAction closeCbVorgang() {
        CloseCbVorgangAction closeCbVorgangAction = new CloseCbVorgangAction(carrierElTALService, witaTalOrderService);
        getTestCase().addTestAction(closeCbVorgangAction);
        return closeCbVorgangAction;
    }

    private String getNotificationSoapActionHeader() {
        if (serviceVersion != null) {
            return "SoapAction"; // return TIBCO JMS soap action header
        } else {
            return "citrus_soap_action"; // return Http soap action header
        }
    }

    private String getNotificationSoapAction() {
        if (serviceVersion != null) {
            return serviceVersion.getMeldungSoapAction();
        }

        return "";
    }

    private WriteVariablesTestAction writeVariablesTestAction() {
        WriteVariablesTestAction writeVariablesTestAction = new WriteVariablesTestAction(externalOrderId,
                carrierElTALService, endstellenService, auftragService, rufnummerService);
        getTestCase().addTestAction(writeVariablesTestAction);
        return writeVariablesTestAction;
    }

    private RepeatOnErrorUntilTrueDefinition waitForCbVorgangCreation() {
        return repeatOnError(new WaitForCbVorgangCreationAction(externalOrderId, carrierElTALService))
                .autoSleep(2000L)
                .until("i gt 20");
    }

    /**
     * Adds sleep action with default time delay. Used by default after each request response so Hurrican workflow can
     * process next step before moving on.
     */
    protected SleepAction autoDelay() {
        return autoDelay(getSimulatorConfiguration().getMessageAutoDelay());
    }

    /**
     * Adds sleep action with the defined sleep time (in ms).
     */
    protected SleepAction autoDelay(long time) {
        return sleep(time);
    }

    /**
     * Gets the use case version (Atlas CDM version) associated with this test builder instance.
     */
    public String getUseCaseVersion() {
        if (serviceVersion != null) {
            return serviceVersion.toString();
        }

        return null;
    }

    public WitaLineOrderServiceVersion getServiceVersion() {
        return this.serviceVersion;
    }

    public void setServiceVersion(WitaLineOrderServiceVersion serviceVersion) {
        if (serviceVersion != null) {
            getTestCase().getParameters().put("Version", serviceVersion.name());
        }

        this.serviceVersion = serviceVersion;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        getTestCase().getParameters().put("ExtOrderId", externalOrderId);
        this.externalOrderId = externalOrderId;
    }

    private AtlasSimulatorConfiguration getSimulatorConfiguration() {
        return (AtlasSimulatorConfiguration) simulatorConfiguration;
    }

    /**
     * Error message will occurs, because Hurrican send only the XML without creating an workflow!
     */
    protected void receiveErrorNoActiveWorkflow() {
        if (this.getServiceVersion() != null) {
            receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1000");
        }
    }

    @Override
    protected String getInterfaceName() {
        return "wita";
    }
}
