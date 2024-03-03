/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2011 10:34:46
 */
package de.mnet.wita.acceptance.common;

import static de.augustakom.common.tools.matcher.RetryMatcher.*;
import static de.mnet.wita.IOArchiveProperties.IOType.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import javax.sql.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.matcher.RealTimeSeries;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.service.HurricanContextStarter;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.TALOrderService;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

/**
 * Abstrakte Klasse fuer alle WITA Integration-Tests.
 */
@Transactional
public abstract class AbstractWitaAcceptanceBaseTest extends BaseTest {

    private static final String ACCEPTANCE_TEST_WITA_CDM_VERSION = "acceptance.test.wita.cdm.version";
    private static final Logger LOGGER = Logger.getLogger(AbstractWitaAcceptanceBaseTest.class);
    // @formatter:off
    private static final ImmutableList<String> WITA_CONFIGS = ImmutableList.of(
            "de/augustakom/hurrican/service/reporting/resources/ReportServicesServer.xml",
            "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCTestService.xml",
            "de/augustakom/hurrican/service/exmodules/archive/resources/ArchiveClientServices.xml",
            "de/mnet/common/common-client-context.xml",
            "de/mnet/wita/v1/wita-client-context.xml",
            "de/mnet/wita/wita-test-context.xml",
            "de/mnet/wita/wita-test-tx-context.xml",
            "de/mnet/common/common-camel.xml",
            "de/mnet/wbci/wbci-client-context.xml",
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml"
);
    // @formatter:on

    public static HurricanContextStarter hurricanContextStarter;
    /**
     * <b>ATTENTION:</b> <ul> <li>this date must be equal with REAL_DATE on Rufnummern in Taifun (see deployment
     * scripts)!</li> <li>next_day function of ORACLE gives always next day even if it is already the correct day of
     * week</li> </ul>
     */
    private static LocalDateTime PORT_DATE;
    @Autowired
    protected CarrierService carrierService;
    @Autowired
    protected Provider<AcceptanceTestDataBuilder> testDataBuilderProvider;
    @Autowired
    protected CarrierElTALService carrierElTalService;
    @Autowired
    protected TALOrderService talOrderService;
    @Autowired
    protected AcceptanceTestWorkflowService acceptanceTestWorkflowService;
    @Autowired
    protected AcceptanceTestWorkflow accTestWorkflow;
    @Autowired
    protected Provider<AcceptanceTestWorkflow> accTestWorkflowProvider;
    @Qualifier("txIoArchiveDao")
    @Autowired
    protected IoArchiveDao ioArchiveDao;
    @Qualifier("txWbciDao")
    @Autowired
    protected WbciDao wbciDao;
    @Qualifier("txCBVorgangDAO")
    @Autowired
    protected CBVorgangDAO cbVorgangDAO;
    @Autowired
    protected EndstellenService endstellenService;
    @Autowired
    protected RangierungsService rangierungsService;
    @Autowired
    protected WitaConfigService witaConfigService;
    @Autowired
    @Qualifier("taifunDataSource")
    protected DataSource taifunDataSource;
    private boolean autowired = false;

    protected static <T> RealTimeSeries<T> overTime(Function<?, T> sampleFunction) {
        String recipient = System.getProperty("wita.message.recipient", "SIMULATOR");

        if (recipient.equals("SIMULATOR")) {
            return RealTimeSeries.overTime(sampleFunction)
                    .sampledEvery(Duration.ofMillis(200))
                    .within(Duration.ofSeconds(40));
        }
        else {
            return RealTimeSeries.overTime(sampleFunction)
                    .sampledEvery(Duration.ofSeconds(10))
                    .within(Duration.ofMinutes(2));
        }
    }

    public void waitForSucessfulTestCaseExecution(WitaSimulatorTestUser useCase) throws InterruptedException {
        //sleep until the expected messages are sent from the simulator
        //TODO make rest call to WITA simulator to check test execution result
        final int millis = 10000;
        LOGGER.info(String.format("sleep for %sms to wait for finishing the simulator use case '%s'", millis, useCase.getName()));
        Thread.sleep(millis);
    }

    @BeforeGroups(groups = { BaseTest.ACCEPTANCE, BaseTest.ACCEPTANCE_INTEGRATION })
    public void setUp() throws Exception {
        LOGGER.info("Starting Akzeptanztest Context, started by: " + this.getClass());
        hurricanContextStarter = new HurricanContextStarter(WITA_CONFIGS, ImmutableList.of("common",
                "authentication-test", "hurrican-wita-test"));
        hurricanContextStarter.startContext();
    }

    @BeforeMethod(groups = { BaseTest.ACCEPTANCE, BaseTest.ACCEPTANCE_INTEGRATION })
    public void autowireBean() {
        if (!autowired) {
            hurricanContextStarter.autowireBean(this);
            autowired = true;
        }
        accTestWorkflow.reset();
    }

    @BeforeMethod(alwaysRun = true)
    public void setDefaultWitaVersion() {
        setDefaultWitaVersionTo(getWitaVersionForAcceptanceTest());
    }

    @AfterGroups(groups = { BaseTest.ACCEPTANCE, BaseTest.ACCEPTANCE_INTEGRATION })
    public void tearDown() throws Exception {
        hurricanContextStarter.tearDown();
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return hurricanContextStarter.getApplicationContext();
    }

    public Long getSessionId() {
        return hurricanContextStarter.getSessionId();
    }

    protected WitaCBVorgang assertWorkflowStateForNewCBVorgang(CreatedData createdData,
            final WorkflowState expectedWfState, Long cbVorgangTyp) throws Exception {

        WitaCBVorgang cbVorgang = acceptanceTestWorkflowService.createCBVorgang(createdData, cbVorgangTyp);
        switch (expectedWfState) {
            case OPEN_WITH_QEB:
                acceptanceTestWorkflowService.waitForQEB(cbVorgang);
                break;
            case CLOSED_WITH_ABBM:
                acceptanceTestWorkflowService.waitForABBM(cbVorgang);
                break;
            case OPEN_WITH_ABM:
                acceptanceTestWorkflowService.waitForABM(cbVorgang);
                break;
            case CLOSED_WITH_ENTM:
                acceptanceTestWorkflowService.waitForENTM(cbVorgang);
                break;
            default:
                throw new IllegalArgumentException("WorkflowState to check not supported!");
        }

        return (WitaCBVorgang) carrierElTalService.findCBVorgang(cbVorgang.getId());
    }

    /**
     * waits until all {@code count} messages (inclusive request) are in IO archive found or a timeout of 2 minutes
     */
    protected void waitForAllMessageInIoArchive(final String externeAuftragsnummer, final int count) {
        Function<Void, Boolean> checkFunction = input -> loadIoArchiveEntries(externeAuftragsnummer).size() == count;
        assertThat(String.format("expected %s IO archive entries not found", count),
                overTime(checkFunction),
                eventuallyTrue());
    }

    protected void waitForMessageInIoArchive(final String extAuftragsnr, final MeldungsType type) {
        Function<Void, Boolean> checkFunction = input -> {
            for (IoArchive ioArchive : loadIoArchiveEntries(extAuftragsnr)) {
                if ((ioArchive.getIoType() == IN) && (MeldungsType.of(ioArchive.getRequestMeldungstyp()) == type)) {
                    return true;
                }
            }
            return false;
        };
        LOGGER.info("Waiting for " + type);
        assertThat(String.format("expected %s but not found in IO archive", type), overTime(checkFunction),
                eventuallyTrue());
        LOGGER.info("Received " + type);
    }

    /**
     * loads all entries in IO archive for supplied {@code externeAuftragsnummer}
     */
    protected List<IoArchive> loadIoArchiveEntries(String externeAuftragsnummer) {
        return ioArchiveDao.findIoArchivesForExtOrderNo(externeAuftragsnummer);
    }

    protected void configureWitaSendLimit(GeschaeftsfallTyp geschaeftsfallTyp, KollokationsTyp kollokationsTyp, boolean enable, Long limit) {
        WitaSendLimit sendLimit = witaConfigService.findWitaSendLimit(geschaeftsfallTyp.name(), kollokationsTyp, null);
        assertNotNull(sendLimit, "WitaSendLimit configuration not found for given values!");

        sendLimit.setWitaSendLimit(limit);
        sendLimit.setAllowed(enable);
        witaConfigService.saveWitaSendLimit(sendLimit);
    }

    protected void setDefaultWitaVersionTo(WitaCdmVersion witaCdmVersion) {
        witaConfigService.switchWitaVersion(witaCdmVersion);
    }

    /**
     * Ueberprueft, ob die aktuelle fuer den Akzeptanztest gesetzte WITA-Version (ueber das System-Property {@code
     * ACCEPTANCE_TEST_WITA_CDM_VERSION} definiert) in der angegebenen Liste enthalten ist.
     *
     * @param allowedWitaCdmVersions Angabe der 'erlaubten' WITA-Versionen
     * @return {@code true} wenn die aktuelle WITA-Version fuer den Test-Case in {@code allowedWitaVersions} vorkommt
     */
    protected boolean shouldTestBeExecuted(@NotNull WitaCdmVersion... allowedWitaCdmVersions) {
        WitaCdmVersion currentWitaCdmVersion = getWitaVersionForAcceptanceTest();
        if (currentWitaCdmVersion != null) {
            for (WitaCdmVersion allowedVersion : allowedWitaCdmVersions) {
                if (allowedVersion.equals(currentWitaCdmVersion)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected WitaCdmVersion getWitaVersionForAcceptanceTest() {
        String witaCdmVersionToUse = System.getProperty(ACCEPTANCE_TEST_WITA_CDM_VERSION);
        if (StringUtils.isBlank(witaCdmVersionToUse)) {
            witaCdmVersionToUse = WitaCdmVersion.getDefault().getVersion();
        }
        return WitaCdmVersion.getCdmVersion(witaCdmVersionToUse);
    }

    /**
     * <b>ATTENTION:</b> <ul> <li>this date must be equal with REAL_DATE on Rufnummern in Taifun (see deployment
     * scripts)!</li> <li>next_day function of ORACLE gives always next day even if it is already the correct day of
     * week</li> </ul>
     */
    protected final LocalDateTime getPortDate() {
        if (PORT_DATE == null) {
            final LocalDate date = LocalDate.now().plusDays(14);
            final LocalDate prtLocalDate = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(date);
            PORT_DATE = LocalDateTime.from(prtLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()));
            ;
        }
        return PORT_DATE;
    }

    protected WbciTestDataBuilder getNewWbciTestDataBuilder() {
        return new WbciTestDataBuilder(wbciDao);
    }

    protected TaifunDataFactory getTaifunDataFactory() {
        return hurricanContextStarter.getApplicationContext().getBean(TaifunDataFactory.class);
    }

    public enum WorkflowState {
        OPEN_WITH_QEB,
        OPEN_WITH_ABM,
        CLOSED_WITH_ABBM,
        CLOSED_WITH_ENTM
    }

}
