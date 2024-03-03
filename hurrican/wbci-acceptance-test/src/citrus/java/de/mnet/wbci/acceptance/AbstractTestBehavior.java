/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance;

import com.consol.citrus.dsl.CitrusTestBehavior;
import com.consol.citrus.dsl.TestBehavior;
import com.consol.citrus.dsl.TestBuilder;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.util.StringUtils;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.common.role.AtlasEsbTestRole;
import de.mnet.wbci.acceptance.common.role.ElektraTestRole;
import de.mnet.wbci.acceptance.common.role.HurricanTestRole;
import de.mnet.wbci.acceptance.kft.KftTestRole;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 * Abstract base test behavior provides access to test roles.
 */
public abstract class AbstractTestBehavior extends CitrusTestBehavior {

    /**
     * Test roles
     */
    private HurricanTestRole hurricanTestRole;
    private AtlasEsbTestRole atlasEsbTestRole;
    private ElektraTestRole elektraTestRole;
    private KftTestRole kftTestRole;

    /**
     * Builder that uses this behavior - used for correct template package evaluation
     */
    private TestBuilder testBuilder;

    /**
     * Explicit vorabstimmungsId to use in this behavior
     */
    private String preAgreementId;

    protected Class expectedExceptionClass;
    protected String expectedExceptionMessage;
    
    private GeschaeftsfallTyp explicitGeschaeftsfallTyp;

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
     * Gets the elektra test actor with provides specific test actions for simulating elektra services.
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

    public void setHurricanTestRole(HurricanTestRole hurricanTestRole) {
        this.hurricanTestRole = hurricanTestRole;
        this.hurricanTestRole.setTestBuilder(this);
    }

    public void setAtlasEsbTestRole(AtlasEsbTestRole atlasEsbTestRole) {
        this.atlasEsbTestRole = atlasEsbTestRole;
        this.atlasEsbTestRole.setTestBuilder(this);
    }

    public void setElektraTestRole(ElektraTestRole elektraTestRole) {
        this.elektraTestRole = elektraTestRole;
        this.elektraTestRole.setTestBuilder(this);
    }

    public void setKftTestRole(KftTestRole kftTestRole) {
        this.kftTestRole = kftTestRole;
        this.kftTestRole.setTestBuilder(this);
    }

    public void setTestBuilder(TestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public TestBuilder getTestBuilder() {
        return testBuilder;
    }

    public AbstractTestBehavior withPreAgreementId(String preAgreementId) {
        this.preAgreementId = preAgreementId;
        return this;
    }

    public String getPreAgreementId() {
        return preAgreementId;
    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;

            abstractTestBehavior.setTestBuilder(testBuilder);
            abstractTestBehavior.setHurricanTestRole(hurricanTestRole);
            abstractTestBehavior.setAtlasEsbTestRole(atlasEsbTestRole);
            abstractTestBehavior.setElektraTestRole(elektraTestRole);
            abstractTestBehavior.setKftTestRole(kftTestRole);

            // also put explicit preAgreementId to nested behavior
            if (StringUtils.hasText(getPreAgreementId())) {
                abstractTestBehavior.withPreAgreementId(preAgreementId);
            }
        }

        super.applyBehavior(behavior);

        // reset test testBuilder for role to this testBuilder instance
        hurricanTestRole.setTestBuilder(this);
        atlasEsbTestRole.setTestBuilder(this);
        kftTestRole.setTestBuilder(this);
    }
    
    protected void setExplicitGeschaeftsfallTyp(GeschaeftsfallTyp explicitGeschaeftsfallTyp) {
        this.explicitGeschaeftsfallTyp = explicitGeschaeftsfallTyp;
    }

    protected GeschaeftsfallTyp getGeschaeftsfallTyp() {
        //set and check if necessary Variables have been set
        GeschaeftsfallTyp geschaeftsfallTyp = null;

        if (explicitGeschaeftsfallTyp != null) {
            geschaeftsfallTyp = explicitGeschaeftsfallTyp;
        }
        else {
            TestBuilder testBuilder = getTestBuilder();
            if (testBuilder instanceof AbstractWbciAcceptanceTestBuilder) {
                geschaeftsfallTyp = ((AbstractWbciAcceptanceTestBuilder) testBuilder).getGeschaeftsfallTyp();
            }
        }
        if (geschaeftsfallTyp == null) {
            throw new CitrusRuntimeException("Current GeschaeftsfallType has not be set! Please set the variable " +
                    "using TestBuilder.simulatorUseCase(...) in your test case! You can use inside of a " +
                    "TestBehavior the getWbciCdmVersion() function.");
        }
        return geschaeftsfallTyp;
    }

    /**
     * Final Fields - for the currentBehavior - read out from the variables *
     */
    protected WbciCdmVersion getCdmVersion() {
        //set and check if necessary Variables have been set
        WbciCdmVersion wbciCdmVersion = null;
        TestBuilder testBuilder = getTestBuilder();
        if (testBuilder instanceof AbstractWbciAcceptanceTestBuilder) {
            wbciCdmVersion = ((AbstractWbciAcceptanceTestBuilder) testBuilder).getWbciCdmVersion();
        }
        if (wbciCdmVersion == null) {
            throw new CitrusRuntimeException("Current GeschaeftsfallType has not be set! Please set the variable " +
                    "using TestBuilder.simulatorUseCase(...) in your test case! You can use inside of a " +
                    "TestBehavior the getWbciCdmVersion() function.");
        }
        return wbciCdmVersion;
    }

    public AbstractTestBehavior withExpectedException(Class expectedExceptionClass, String expectedExceptionMessage) {
        assert expectedExceptionClass != null;
        this.expectedExceptionClass = expectedExceptionClass;
        this.expectedExceptionMessage = expectedExceptionMessage;
        return this;
    }

    protected boolean isExceptionExpected() {
        return expectedExceptionClass != null;
    }


    protected void writeToVariables(GeneratedTaifunData generatedTaifunData) {
        if (generatedTaifunData != null) {
            variable(VariableNames.ENDKUNDE_NACHNAME, generatedTaifunData.getKunde().getName());
            if (generatedTaifunData.getKunde().getVorname() != null) {
                variable(VariableNames.ENDKUNDE_VORNAME, generatedTaifunData.getKunde().getVorname());
            }

            variable(VariableNames.STANDORT_STRASSE, generatedTaifunData.getAddress().getStrasse());
            variable(VariableNames.STANDORT_HNR, generatedTaifunData.getAddress().getNummer());
            variable(VariableNames.STANDORT_PLZ, generatedTaifunData.getAddress().getPlz());
            variable(VariableNames.STANDORT_ORT, generatedTaifunData.getAddress().getOrt());
            variable(VariableNames.STANDORT_GEOID, generatedTaifunData.getAddress().getGeoId());

            if (generatedTaifunData.getDialNumbers() != null) {
                int count = 0;
                for (Rufnummer dn : generatedTaifunData.getDialNumbers()) {
                    variable(String.format(VariableNames.PORTIERUNG_ONKZ, count), dn.getOnKzWithoutLeadingZeros());
                    variable(String.format(VariableNames.PORTIERUNG_RUFNUMMER, count), dn.getDnBase());
                    if (dn.isBlock()) {
                        variable(String.format(VariableNames.PORTIERUNG_DIRECT_DIAL, count), dn.getDirectDial());
                        variable(String.format(VariableNames.PORTIERUNG_RANGE_FROM, count), dn.getRangeFrom());
                        variable(String.format(VariableNames.PORTIERUNG_RANGE_TO, count), dn.getRangeTo());
                    }
                    count++;
                }
            }

            variable(VariableNames.CUSTOMER_ID, generatedTaifunData.getKunde().getKundeNo());

            if (generatedTaifunData.getBillingAuftrag() != null) {
                variable(VariableNames.BILLING_ORDER_NO_ORIG, generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
            }
        }
    }

}
