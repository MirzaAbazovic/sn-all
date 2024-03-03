/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.13
 */
package de.mnet.wbci.acceptance;

import javax.sql.*;
import com.consol.citrus.dsl.TestBehavior;
import com.consol.citrus.dsl.TestNGCitrusTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.wbci.acceptance.common.role.AtlasEsbTestRole;
import de.mnet.wbci.acceptance.common.role.ElektraTestRole;
import de.mnet.wbci.acceptance.common.role.HurricanTestRole;
import de.mnet.wbci.acceptance.kft.KftTestRole;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.VariableValues;
import de.mnet.wbci.citrus.builder.WbciTaifunDatafactory;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public abstract class AbstractWbciAcceptanceTestBuilder extends TestNGCitrusTestBuilder {

    protected static final Long USER_ID_GLINKJO = 2L;
    @Autowired
    @Qualifier("taifunDataSource")
    protected DataSource taifunDataSource;
    @Autowired
    @Qualifier("hurricanDataSource")
    private DataSource hurricanDataSource;
    @Autowired
    private HurricanTestRole hurricanTestRole;
    @Autowired
    private AtlasEsbTestRole atlasEsbTestRole;
    @Autowired
    private ElektraTestRole elektraTestRole;
    @Autowired
    private KftTestRole kftTestRole;
    private WbciCdmVersion wbciCdmVersion;
    private GeschaeftsfallTyp geschaeftsfallTyp;

    @Override
    public void init() {
        super.init();

        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        kftTestRole.setTestBuilder(this);
        elektraTestRole.setTestBuilder(this);

        // set KFT test mode to false during the test execution just in case it is enabled
        hurrican().setKftTestMode(false);
        hurrican().setWbciPhoneticSearchFeature(false);
        hurrican().setElektraRuemVaProcessingEnabled(true);
        hurrican().setElektraRrnpProcessingEnabled(true);
        hurrican().setElektraAkmTrProcessingEnabled(true);
        hurrican().setElektraTVProcessingEnabled(true);
        disableMessageScheduling();

        // purge all JMS Queues
        atlasEsbTestRole.purgeAllJmsQueues();
        kftTestRole.purgeAllJmsQueues();
        elektraTestRole.purgeAllJmsQueues();
    }

    /**
     * Disables scheduling for all messages
     */
    protected void disableMessageScheduling() {
        hurrican().setWbciMinutesWhileMessageOnHold(getWbciMinutesWhileMessageOnHold());
    }

    protected int getWbciMinutesWhileMessageOnHold() {
        return -1;
    }

    /**
     * Gets the atlas esb test actor which provides specific test actions.
     *
     * @return
     */
    protected AtlasEsbTestRole atlas() {
        return atlasEsbTestRole;
    }

    /**
     * Gets the atlas esb test actor which provides specific test actions.
     *
     * @return
     */
    protected HurricanTestRole hurrican() {
        return hurricanTestRole;
    }

    /**
     * Gets the elektra test actor which provides specific test actions for simulating elektra services.
     *
     * @return
     */
    protected ElektraTestRole elektra() {
        return elektraTestRole;
    }

    /**
     * Gets the kft test role which provides very special kft related test activities.
     *
     * @return
     */
    protected KftTestRole kft() {
        return kftTestRole;
    }

    /**
     * Sets the WBCI simulator use case as test variable. Other test actions will make use of this value in order to
     * trigger a use case scenario on simulator in a end-to-end acceptance test scenario (e.g. KFT test)
     *
     * @param useCase
     * @param wbciCdmVersion
     * @param wbciVersion
     * @param geschaeftsfallTyp
     */
    protected void simulatorUseCase(WbciSimulatorUseCase useCase, WbciCdmVersion wbciCdmVersion, WbciVersion wbciVersion,
            GeschaeftsfallTyp geschaeftsfallTyp) {
        atlasEsbTestRole.setWbciSimulatorUseCase(useCase);
        hurricanTestRole.setWbciSimulatorUseCase(useCase);
        elektraTestRole.setWbciSimulatorUseCase(useCase);
        kftTestRole.setWbciSimulatorUseCase(useCase);

        atlasEsbTestRole.setWbciCdmVersion(wbciCdmVersion);
        hurricanTestRole.setWbciCdmVersion(wbciCdmVersion);
        elektraTestRole.setWbciCdmVersion(wbciCdmVersion);
        kftTestRole.setWbciCdmVersion(wbciCdmVersion);

        // set testbuilder specific parameters
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.wbciCdmVersion = wbciCdmVersion;

        // these variables are added for convenience to the test context for ALL tests.
        // Whether they are used or not depends on the actual test itself
        variable(VariableNames.CDM_VERSION, wbciCdmVersion.getVersion());
        variable(VariableNames.WBCI_VERSION, wbciVersion.getVersion());
        variable(VariableNames.SIMULATOR_USE_CASE, useCase.getName());
        variable(VariableNames.UNKNOWN_ONKZ, VariableValues.UNKNOWN_ONKZ);

    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;

            abstractTestBehavior.setTestBuilder(this);
            abstractTestBehavior.setHurricanTestRole(hurricanTestRole);
            abstractTestBehavior.setAtlasEsbTestRole(atlasEsbTestRole);
            abstractTestBehavior.setElektraTestRole(elektraTestRole);
            abstractTestBehavior.setKftTestRole(kftTestRole);
        }

        super.applyBehavior(behavior);

        // reset test builder for role to this builder instance
        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        elektraTestRole.setTestBuilder(this);
        kftTestRole.setTestBuilder(this);
    }

    /**
     * @return returns the current Version of this test, the version will be set through {@link
     * #simulatorUseCase(WbciSimulatorUseCase, WbciCdmVersion, de.mnet.wbci.model.WbciVersion,
     * de.mnet.wbci.model.GeschaeftsfallTyp)}
     */
    public WbciCdmVersion getWbciCdmVersion() {
        return wbciCdmVersion;
    }

    /**
     * @return returns the current Geschäftsfall of this test, the version will be set through {@link
     * #simulatorUseCase(WbciSimulatorUseCase, WbciCdmVersion, de.mnet.wbci.model.WbciVersion,
     * de.mnet.wbci.model.GeschaeftsfallTyp)}
     */
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public WbciTaifunDatafactory getNewTaifunDataFactory() {
        return new WbciTaifunDatafactory(taifunDataSource, hurricanDataSource, getWbciCdmVersion(), getGeschaeftsfallTyp());
    }
}
