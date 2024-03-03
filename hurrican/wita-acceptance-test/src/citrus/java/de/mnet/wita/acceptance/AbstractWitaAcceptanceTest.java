/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2015
 */
package de.mnet.wita.acceptance;

import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import javax.validation.constraints.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.TestBehavior;
import com.consol.citrus.dsl.TestNGCitrusTestBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.authentication.AuthenticationContextBeanLoader;
import de.mnet.wita.acceptance.common.behavior.AbstractTestBehavior;
import de.mnet.wita.acceptance.common.role.AtlasEsbTestRole;
import de.mnet.wita.acceptance.common.role.HurricanTestRole;
import de.mnet.wita.acceptance.common.role.KftTestResourceTestRole;
import de.mnet.wita.acceptance.common.role.WorkflowTestRole;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@ContextConfiguration(
    initializers = { AuthenticationContextBeanLoader.class }
)
public class AbstractWitaAcceptanceTest extends TestNGCitrusTestBuilder {

    protected static final String ACCEPTANCE_TEST_WITA_CDM_VERSION = "acceptance.test.wita.cdm.version";

    @Autowired
    private HurricanTestRole hurricanTestRole;
    @Autowired
    private AtlasEsbTestRole atlasEsbTestRole;
    @Autowired
    private WorkflowTestRole workflowTestRole;

    @Autowired
    private KftTestResourceTestRole testResource;

    @Autowired
    protected TaifunDataFactory taifunDataFactory;

    @Autowired
    protected WbciDao wbciDao;

    @Autowired
    protected CarrierService carrierService;
    @Autowired
    protected EndstellenService endstellenService;
    @Autowired
    protected RangierungsService rangierungsService;


    @Autowired
    @Qualifier("WitaConfigService")
    private WitaConfigService witaConfigService;

    /**
     * <b>ATTENTION:</b> <ul> <li>this date must be equal with REAL_DATE on Rufnummern in Taifun (see deployment
     * scripts)!</li> <li>next_day function of ORACLE gives always next day even if it is already the correct day of
     * week</li> </ul>
     */
    private static LocalDateTime portDate;

    @Override
    public void init() {
        super.init();

        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        workflowTestRole.setTestBuilder(this);
    }

    @BeforeMethod(alwaysRun = true)
    public void setDefaultWitaVersion() {
        setDefaultWitaVersionTo(getWitaVersionForAcceptanceTest());
    }

    /**
     * Gets the hurrican test role which provides specific test actions for Hurrican test actor.
     * @return
     */
    protected HurricanTestRole hurrican() {
        return hurricanTestRole;
    }

    /**
     * Gets the atlas test role which provides specific test actions for Atlas ESB test actor.
     * @return
     */
    protected AtlasEsbTestRole atlas() {
        return atlasEsbTestRole;
    }

    protected KftTestResourceTestRole testResource() {
        return testResource;
    }

    /**
     * Gets the workflow test role which provides specific test actions for Wita Activity workflow actor.
     * @return
     */
    protected WorkflowTestRole workflow() {
        return workflowTestRole;
    }

    protected void useCaseV2(WitaAcceptanceUseCase useCase) {
        useCase(useCase, WitaCdmVersion.V2);
    }

    /**
     * Sets the WBCI simulator use case as test variable. Other test actions will make use of this value in order to
     * trigger a use case scenario on simulator in a end-to-end acceptance test scenario (e.g. KFT test)
     *
     * @param useCase
     * @param cdmVersion
     */
    protected void useCase(WitaAcceptanceUseCase useCase, WitaCdmVersion cdmVersion) {
        hurricanTestRole.setUseCase(useCase);
        hurricanTestRole.setCdmVersion(cdmVersion);

        atlasEsbTestRole.setUseCase(useCase);
        atlasEsbTestRole.setCdmVersion(cdmVersion);

        workflowTestRole.setUseCase(useCase);
        workflowTestRole.setCdmVersion(cdmVersion);

        testResource.setUseCase(useCase);
        testResource.setCdmVersion(cdmVersion);

        // these variables are added for convenience to the test context for ALL tests.
        // Whether they are used or not depends on the actual test itself
        variable(VariableNames.CDM_VERSION, cdmVersion.getVersion());
        variable(VariableNames.USE_CASE, useCase.name());

        echo(String.format("Starting WITA use case '%s' with version '%s'", useCase.name(), cdmVersion.name()));
    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;

            abstractTestBehavior.setTestBuilder(this);
            abstractTestBehavior.setHurricanTestRole(hurricanTestRole);
            abstractTestBehavior.setAtlasEsbTestRole(atlasEsbTestRole);
            abstractTestBehavior.setWorkflowTestRole(workflowTestRole);
        }

        super.applyBehavior(behavior);

        // reset test builder for role to this builder instance
        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        workflowTestRole.setTestBuilder(this);
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
        if (portDate == null) {
            final LocalDate date = LocalDate.now().plusDays(14);
            final LocalDate prtLocalDate = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(date);
            portDate = LocalDateTime.from(prtLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()));
        }
        return portDate;
    }

    public enum WorkflowState {
        OPEN_WITH_QEB,
        OPEN_WITH_ABM,
        CLOSED_WITH_ABBM,
        CLOSED_WITH_ENTM
    }

    protected LocalDateTime getDateTimeFromContext(TestContext context, String variableName) {
        final DateTimeFormatter dateTimeFormatter =
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
        return LocalDateTime.of(LocalDate.parse(context.getVariable(variableName), dateTimeFormatter), LocalTime.MIN);
    }

    protected TaifunDataFactory getTaifunDataFactory() {
        return taifunDataFactory;
    }

}
