/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.acceptance.common.role;

import java.time.*;
import javax.sql.*;
import com.consol.citrus.dsl.TestBuilder;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.augustakom.hurrican.dao.cc.CarrierbestellungDAO;
import de.augustakom.hurrican.dao.cc.MailDAO;
import de.augustakom.hurrican.model.cc.Feature;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.VorabstimmungsAnfrageKftBuilder;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.actions.StoreWbciEntityTestAction;
import de.mnet.wbci.citrus.actions.StoreWitaCbVorgangTestAction;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;
import de.mnet.wita.dao.VorabstimmungAbgebendDao;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaConfig;

/**
 * Abstract role implementation offers generic template access. Role instance is constructed for each test exclusively
 * by Spring bean scope prototype. This means we can hold the test state here (e.g. cdm version, wbci simulator use
 * case).
 *
 *
 */
public abstract class AbstractTestRole {
    public final Logger LOGGER = Logger.getLogger(getClass());

    /** native SQL statements for feature flag */
    private static final String SQL_IS_FEATURE_ENABLED =
            "SELECT FLAG FROM T_FEATURE WHERE NAME = '%s'";
    private static final String SQL_SET_FEATURE =
            "UPDATE T_FEATURE SET FLAG = '%s' WHERE NAME = '%s'";

    /** native SQL statements for config */
    private static final String SQL_UPDATE_WITA_CONFIG =
            "UPDATE T_WITA_CONFIG set CONFIG_VALUE = '%s' where CONFIG_KEY = '%s'";
    private static final String SQL_SELECT_WITA_CONFIG_VALUE =
            "SELECT CONFIG_VALUE FROM T_WITA_CONFIG where CONFIG_KEY = '%s'";

    public final String DEFAULT_CARRIER_ESCALATION_EMAIL = "WBCI-Developer@m-net.de";

    /** Currently loaded simulator use case - important for template path generation */
    protected WbciSimulatorUseCase wbciSimulatorUseCase;

    /** Test builder that receives all building method calls */
    protected TestBuilder testBuilder;

    /** CdmVersion used for this role */
    protected WbciCdmVersion wbciCdmVersion;

    /** Feature flags used */
    protected boolean elektraRuemVaProcessing = true;
    protected boolean elektrarRrnpProcessing = true;
    protected boolean elektraAkmTrProcessing = true;
    protected boolean kftTestMode = false;
    protected boolean phoneticSearch = false;

    @Autowired
    @Qualifier("hurricanDataSource")
    protected DataSource hurricanDataSource;
    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WitaCBVorgangDao witaCBVorgangDao;
    @Autowired
    protected MailDAO mailDAO;
    @Autowired
    protected CarrierbestellungDAO carrierbestellungDAO;
    @Autowired
    protected VorabstimmungAbgebendDao vorabstimmungAbgebendDao;

    /**
     * Gets XML template file resource from template package.
     *
     * @param fileName
     * @return
     */
    protected Resource getXmlTemplate(String fileName) {
        return getXmlTemplate(getXmlTemplateBasePackage(), fileName, ".xml");
    }

    /**
     * Gets the base package for xml template files.
     * @return
     */
    protected String getXmlTemplateBasePackage() {
        return getTestBuilderPackage() + "/cdm/" + wbciCdmVersion.name().toLowerCase() + "/" + wbciSimulatorUseCase;
    }

    /**
     * Gets a file resource from this builders resource package. Uses file path naming conventions:
     * <p/>
     * {test_builder_package}/cdm/{cdm_version}/{sim_usecase_name}/{filename}.xml
     *
     * @param fileName
     * @param fileExtension
     * @return
     */
    protected Resource getXmlTemplate(String resourcePackage, String fileName, String fileExtension) {
        return new ClassPathResource(resourcePackage + "/" + fileName + fileExtension);
    }

    public void setTestBuilder(TestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public void setWbciSimulatorUseCase(WbciSimulatorUseCase wbciSimulatorUseCase) {
        this.wbciSimulatorUseCase = wbciSimulatorUseCase;
    }

    public void setWbciCdmVersion(WbciCdmVersion wbciCdmVersion) {
        this.wbciCdmVersion = wbciCdmVersion;
    }

    public String getTestBuilderPackage() {
        String packageName;

        if (testBuilder instanceof AbstractTestBehavior) {
            // get original test builder package from test behavior
            packageName = ((AbstractTestBehavior) testBuilder).getTestBuilder().getClass().getPackage().getName();
        }
        else {
            packageName = testBuilder.getClass().getPackage().getName();
        }

        return packageName.replace('.', '/');
    }

    /**
     * enables or disables the Elektra RUEM-VA AutoProcessing during the execution of the test
     *
     * @param enable
     */
    public void setElektraRuemVaProcessingEnabled(boolean enable) {
        this.elektraRuemVaProcessing = enable;
        setFeature(enable, Feature.FeatureName.WBCI_RUEMVA_AUTO_PROCESSING.name(), VariableNames.RUEMVA_AUTO_PROCESSING_FEATURE);
    }

    /**
     * enables or disables the Elektra RRNP AutoProcessing during the execution of the test
     *
     * @param enable
     */
    public void setElektraRrnpProcessingEnabled(boolean enable) {
        this.elektrarRrnpProcessing = enable;
        setFeature(enable, Feature.FeatureName.WBCI_RRNP_AUTO_PROCESSING.name(), VariableNames.RRNP_AUTO_PROCESSING_FEATURE);
    }

    /**
     * enables or disables the Elektra AKM-TR AutoProcessing during the execution of the test
     *
     * @param enable
     */
    public void setElektraAkmTrProcessingEnabled(boolean enable) {
        this.elektraAkmTrProcessing = enable;
        setFeature(enable, Feature.FeatureName.WBCI_AKMTR_AUTO_PROCESSING.name(), VariableNames.AKMTR_AUTO_PROCESSING_FEATURE);
    }

    /**
     * enables or disables the Elektra TVS-VA AutoProcessing during the execution of the test
     *
     * @param enable
     */
    public void setElektraTVProcessingEnabled(boolean enable) {
        this.elektraAkmTrProcessing = enable;
        setFeature(enable, Feature.FeatureName.WBCI_TVS_VA_AUTO_PROCESSING.name(), VariableNames.TVS_VA_AUTO_PROCESSING_FEATURE);
    }

    /**
     * enables or disables the Wbci phonetic search feature during the execution of the test
     *
     * @param enable
     */
    public void setWbciPhoneticSearchFeature(boolean enable) {
        this.phoneticSearch = enable;
        setFeature(enable, Feature.FeatureName.WBCI_PHONETIC_SEARCH.name(), VariableNames.PHONETIC_SEARCH_FEATURE);
    }

    /**
     * Generic method to first exctract a feature flag from database to a test variable. Then set the feature with new
     * value and in finally block restore old value from test variable.
     *
     * @param enable
     * @param featureName
     * @param featureVariableName
     */
    private void setFeature(boolean enable, String featureName, String featureVariableName) {
        //extract values to the variable
        testBuilder.query(hurricanDataSource)
                .statement(String.format(SQL_IS_FEATURE_ENABLED, featureName))
                .extract("FLAG", featureVariableName);

        String sqlValue = enable ? "1" : "0";
        String updateSQLStatement = String.format(SQL_SET_FEATURE, sqlValue, featureName);
        testBuilder.sql(hurricanDataSource).statement(updateSQLStatement);

        // restore the auto processing flag to the previous value which has been stored within the test context
        String restoreSQLStatement = String.format(SQL_SET_FEATURE,
                "${" + featureVariableName + "}", featureName);

        testBuilder.doFinally(
                testBuilder.sql(hurricanDataSource).statement(restoreSQLStatement)
        );
    }

    /**
     * Sets the minutes which a message will be on hold to the provided value
     */
    public void setWbciMinutesWhileMessageOnHold(int minutes) {
        updateConfig(WitaConfig.WBCI_MINUTES_WHILE_REQUESTS_ON_HOLD, String.valueOf(minutes));
    }

    private void updateConfig(String key, String value) {
        //extract config value and add it to the test context
        testBuilder.query(hurricanDataSource)
                .statement(String.format(SQL_SELECT_WITA_CONFIG_VALUE, key))
                .extract("CONFIG_VALUE", key);

        String updateSQLStatement = String.format(SQL_UPDATE_WITA_CONFIG, value, key);
        testBuilder.sql(hurricanDataSource).statement(updateSQLStatement);

        // restore the config value which has been stored within the test context
        String restoreSQLStatement = String.format(SQL_UPDATE_WITA_CONFIG, "${" + key + "}", key);
        testBuilder.doFinally(
                testBuilder.sql(hurricanDataSource).statement(restoreSQLStatement)
        );
    }

    /**
     * Stores WitaCBVorgang directly to the database.
     *
     * @param witaCBVorgang
     * @return
     */
    public void storeWitaCBVorgang(WitaCBVorgang witaCBVorgang, boolean withVorabstimmungsId) {
        StoreWitaCbVorgangTestAction testAction =
                new StoreWitaCbVorgangTestAction(witaCBVorgangDao, witaCBVorgang)
                        .withVorabstimmungsId(withVorabstimmungsId);
        testBuilder.action(testAction);
    }

    /**
     * Stores wbci entity directly to the database.
     *
     * @param wbciEntity
     * @return
     */
    public void storeWbciEntity(WbciEntity wbciEntity) {
        StoreWbciEntityTestAction testAction = new StoreWbciEntityTestAction(wbciDao, wbciEntity);
        testBuilder.action(testAction);
    }

    /**
     * Creates new WbciGeschaeftsfall instance using the provided builder and stores it to the database.
     *
     * @param wbciGeschaeftsfallBuilder
     * @return the persisted GF
     */
    public <GF extends WbciGeschaeftsfall> GF createAndStoreGeschaeftsfall(
            WbciGeschaeftsfallBuilder<GF> wbciGeschaeftsfallBuilder) {
        GF wbciGeschaeftsfall = wbciGeschaeftsfallBuilder
                .build();

        storeWbciEntity(wbciGeschaeftsfall);
        return wbciGeschaeftsfall;
    }

    /**
     * Creates new VorabstimmungsAnfrage instance and stores it to the database. The supplied {@code wbciGeschaeftsfall}
     * should already have been created and persisted.
     *
     * @param requestStatus
     * @param wbciGeschaeftsfall
     * @param ioType
     * @return the persisted VA
     */
    public <GF extends WbciGeschaeftsfall> VorabstimmungsAnfrage<GF> createAndStoreVa(
            final WbciRequestStatus requestStatus, GF wbciGeschaeftsfall, final IOType ioType) {
        if (requestStatus.equals(WbciRequestStatus.RUEM_VA_VERSENDET) || requestStatus.equals(WbciRequestStatus.RUEM_VA_EMPFANGEN)) {
            wbciGeschaeftsfall.setWechseltermin(wbciGeschaeftsfall.getKundenwunschtermin());
        }
        VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage = new VorabstimmungsAnfrageKftBuilder<>(wbciCdmVersion,
                wbciGeschaeftsfall, ioType)
                .withRequestStatus(requestStatus)
                .build();
        storeWbciEntity(vorabstimmungsAnfrage);
        return vorabstimmungsAnfrage;
    }

    /**
     * Creates new VorabstimmungsAnfrage instance, attach some information for the last Meldung and stores it to the
     * database. The supplied {@code wbciGeschaeftsfall} should already have been created and persisted.
     *
     * @param requestStatus
     * @param wbciGeschaeftsfall
     * @param meldungsCode
     * @param meldungTyp
     * @param ioType
     * @return the persisted VA
     */
    public <GF extends WbciGeschaeftsfall> VorabstimmungsAnfrage<GF> createAndStoreVa(
            final WbciRequestStatus requestStatus, GF wbciGeschaeftsfall, final MeldungsCode meldungsCode,
            final MeldungTyp meldungTyp, final IOType ioType) {
        if (requestStatus.equals(WbciRequestStatus.RUEM_VA_EMPFANGEN) || requestStatus.equals(WbciRequestStatus.RUEM_VA_VERSENDET)) {
            wbciGeschaeftsfall.setWechseltermin(wbciGeschaeftsfall.getKundenwunschtermin());
        }
        VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage = new VorabstimmungsAnfrageKftBuilder<>(wbciCdmVersion,
                wbciGeschaeftsfall, ioType)
                .withLastMeldung(meldungTyp, LocalDateTime.now(), meldungsCode)
                .withRequestStatus(requestStatus)
                .build();
        storeWbciEntity(vorabstimmungsAnfrage);
        return vorabstimmungsAnfrage;
    }

    /**
     * Creates new RUEM-VA instance and stores it to the database. The supplied {@code wbciGeschaeftsfall} as well as a
     * valid VorabstimmungsAnfrage should already have been created and persisted.
     *
     * @param wbciGeschaeftsfall
     * @param ioType
     * @return the persisted RUEM-VA
     */
    public <GF extends WbciGeschaeftsfall> RueckmeldungVorabstimmung createAndStoreRuemVa(GF wbciGeschaeftsfall, final IOType ioType) {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungKftBuilder(wbciCdmVersion, wbciGeschaeftsfall.getTyp(), ioType)
                .withWechseltermin(wbciGeschaeftsfall.getKundenwunschtermin())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withProcessedAt(LocalDateTime.now())
                .build();
        storeWbciEntity(ruemVa);
        return ruemVa;
    }

    /**
     * Return kft test mode.
     *
     * @return
     */
    public boolean isKftTestMode() {
        return this.kftTestMode;
    }

    /**
     * Enables or disables the KFT Test Mode during the execution of the test
     *
     * @param kftTestMode
     */
    public void setKftTestMode(boolean kftTestMode) {
        this.kftTestMode = kftTestMode;
        setFeature(kftTestMode, Feature.FeatureName.WBCI_KFT_TEST_MODE.name(), VariableNames.KFT_TEST_MODE);
    }

    /**
     * Return phonetic search flag.
     *
     * @return
     */
    public boolean isPhoneticSearch() {
        return this.phoneticSearch;
    }

    public void purgeJmsQueue(JmsEndpoint jmsEndpoint) {
        LOGGER.info(String.format("Purge JMS queue %s", jmsEndpoint.getEndpointConfiguration().getDefaultDestinationName()));
        testBuilder.purgeQueues(jmsEndpoint.getEndpointConfiguration().getConnectionFactory())
                .queueNames(jmsEndpoint.getEndpointConfiguration().getDefaultDestinationName());
    }
}
