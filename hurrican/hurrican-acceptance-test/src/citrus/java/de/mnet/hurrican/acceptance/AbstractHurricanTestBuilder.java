/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.mnet.hurrican.acceptance;

import java.util.*;
import javax.sql.*;
import com.consol.citrus.dsl.TestBehavior;
import com.consol.citrus.dsl.TestNGCitrusTestBuilder;
import com.consol.citrus.functions.core.RandomNumberFunction;
import com.consol.citrus.ws.client.WebServiceClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.mnet.common.webservice.ServiceModelVersison;
import de.mnet.hurrican.acceptance.behavior.AbstractTestBehavior;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.acceptance.ffm.FfmTestVersion;
import de.mnet.hurrican.acceptance.role.AbstractTestRole;
import de.mnet.hurrican.acceptance.role.AtlasEsbTestRole;
import de.mnet.hurrican.acceptance.role.CpsTestRole;
import de.mnet.hurrican.acceptance.role.HurricanTestRole;
import de.mnet.hurrican.acceptance.role.ResourceInventoryTestRole;
import de.mnet.hurrican.acceptance.role.TvFeedTestRole;
import de.mnet.hurrican.acceptance.role.TvProviderTestRole;
import de.mnet.hurrican.acceptance.role.WorkforceDataTestRole;
import de.mnet.hurrican.ffm.citrus.TaifunVariables;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Basic hurrican acceptance test builder provides access to test roles and basic simulator use case setup.
 *
 *
 */
public abstract class AbstractHurricanTestBuilder extends TestNGCitrusTestBuilder {

    @Autowired
    @Qualifier("tvProvider")
    protected WebServiceClient tvProviderWebClient;
    @Autowired
    @Qualifier("tvFeed")
    protected WebServiceClient tvFeedWebClient;
    @Autowired
    private List<AbstractTestRole> testRoles;
    @Autowired
    @Qualifier("hurricanDataSource")
    private DataSource hurricanDataSource;

    /**
     * Currently used version of the tested service
     */
    private ServiceModelVersison serviceModelVersison;

    @Override
    public void init() {
        super.init();

        for (AbstractTestRole testRole : testRoles) {
            testRole.setTestBuilder(this);
        }

        // purge all JMS Queues
        atlas().purgeAllJmsQueues();
        hurrican().cleanUpTestData();
    }

    public AtlasEsbTestRole atlas() {
        return getTestRole(AtlasEsbTestRole.class);
    }

    public HurricanTestRole hurrican() {
        return getTestRole(HurricanTestRole.class);
    }

    public TvProviderTestRole tvProvider() {
        return getTestRole(TvProviderTestRole.class);
    }

    public TvFeedTestRole tvFeed() {
        return getTestRole(TvFeedTestRole.class);
    }

    public ResourceInventoryTestRole resourceInventory() {
        return getTestRole(ResourceInventoryTestRole.class);
    }

    public CpsTestRole cps() {
        return getTestRole(CpsTestRole.class);
    }

    public WorkforceDataTestRole workforceData() {
        return getTestRole(WorkforceDataTestRole.class);
    }

    @Override
    public void applyBehavior(TestBehavior behavior) {
        if (behavior instanceof AbstractTestBehavior) {
            AbstractTestBehavior abstractTestBehavior = (AbstractTestBehavior) behavior;

            abstractTestBehavior.setTestBuilder(this);
            abstractTestBehavior.setTestRoles(testRoles);
        }

        super.applyBehavior(behavior);

        // reset test builder for role to this builder instance
        for (AbstractTestRole testRole : testRoles) {
            testRole.setTestBuilder(this);
        }
    }

    /**
     * Sets the FFM simulator use case as test variable. Other test actions will make use of this value in order to
     * trigger a use case scenario on simulator in a end-to-end acceptance test scenario.
     *
     * @param useCase
     * @param serviceModelVersison model version of the testCase like the {@link FfmTestVersion} or other types
     */
    protected void simulatorUseCase(SimulatorUseCase useCase, ServiceModelVersison serviceModelVersison) {
        for (AbstractTestRole testRole : testRoles) {
            testRole.setSimulatorUseCase(useCase);
            testRole.setServiceModelVersison(serviceModelVersison);
        }

        this.serviceModelVersison = serviceModelVersison;

        variable(VariableNames.SERVICE_MODEL_VERSION, this.serviceModelVersison.getVersion());
        variable(VariableNames.SIMULATOR_USE_CASE, useCase.name());
    }

    /**
     * Creates a new workforce order id.
     *
     * @return
     */
    protected String createWorkforceOrderId() {
        String workforceOrderId = "HUR-" + new RandomNumberFunction().execute(Collections.singletonList("10"), null);
        variable(VariableNames.WORKFROCE_ORDER_ID, workforceOrderId);
        return workforceOrderId;
    }

    public ServiceModelVersison getServiceModelVersison() {
        return serviceModelVersison;
    }

    public DataSource getHurricanDataSource() {
        return hurricanDataSource;
    }

    private <T extends AbstractTestRole> T getTestRole(Class<T> clazz) {
        for (AbstractTestRole testRole : testRoles) {
            if (clazz.isAssignableFrom(testRole.getClass())) {
                return (T) testRole;
            }
        }

        return null;
    }

    /**
     * Durch diese Methode werden einige Parameter aus {@link GeneratedTaifunData} als Citrus-Variable exportiert.
     *
     * @param generatedTaifunData
     */
    public void exportTaifunDataToVariables(GeneratedTaifunData generatedTaifunData) {
        if (generatedTaifunData != null) {
            if (generatedTaifunData.getBillingAuftrag() != null) {
                variable(TaifunVariables.ORDER_NO, generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
            }

            variable(TaifunVariables.CUSTOMER_NO, generatedTaifunData.getKunde().getKundeNo());
            variable(TaifunVariables.ENDKUNDE_NACHNAME, generatedTaifunData.getKunde().getName());
            if (generatedTaifunData.getKunde().getVorname() != null) {
                variable(TaifunVariables.ENDKUNDE_VORNAME, generatedTaifunData.getKunde().getVorname());
            }

            variable(TaifunVariables.STANDORT_STRASSE, generatedTaifunData.getAddress().getStrasse());
            variable(TaifunVariables.STANDORT_HNR, generatedTaifunData.getAddress().getNummer());
            variable(TaifunVariables.STANDORT_HNR_ZUSATZ, generatedTaifunData.getAddress().getHausnummerZusatz());
            variable(TaifunVariables.STANDORT_PLZ, generatedTaifunData.getAddress().getPlz());
            variable(TaifunVariables.STANDORT_ORT, generatedTaifunData.getAddress().getOrt());
            variable(TaifunVariables.STANDORT_GEOID, generatedTaifunData.getAddress().getGeoId());

            if (generatedTaifunData.getDialNumbers() != null) {
                int count = 0;
                for (Rufnummer dn : generatedTaifunData.getDialNumbers()) {
                    variable(String.format(TaifunVariables.RUFNUMMER_ONKZ, count), dn.getOnKz());
                    variable(String.format(TaifunVariables.RUFNUMMER_DNBASE, count), dn.getDnBase());
                    if (dn.isBlock()) {
                        variable(String.format(TaifunVariables.RUFNUMMER_DIRECT_DIAL, count), dn.getDirectDial());
                        variable(String.format(TaifunVariables.RUFNUMMER_RANGE_FROM, count), dn.getRangeFrom());
                        variable(String.format(TaifunVariables.RUFNUMMER_RANGE_TO, count), dn.getRangeTo());
                    }
                    count++;
                }
            }

            if (CollectionUtils.isNotEmpty(generatedTaifunData.getDevices())) {
                Device device = generatedTaifunData.getDevices().get(0);
                variable(TaifunVariables.DEVICE_TYPE, device.getDevType());
                variable(TaifunVariables.DEVICE_MODEL, device.getTechName());
                variable(TaifunVariables.DEVICE_MANUFACTURER, device.getManufacturer());
                variable(TaifunVariables.DEVICE_SERIAL_NO, device.getSerialNumber());
            }
            if (CollectionUtils.isNotEmpty(generatedTaifunData.getDevicesFritzBox())) {
                DeviceFritzBox device = generatedTaifunData.getDevicesFritzBox().get(0);
                variable(TaifunVariables.DEVICE_FB_CWMP_ID, device.getCwmpId());
            }
        }
    }

    /**
     * Durch diese Methode werden einige Parameter aus {@link AuftragDaten} als Citrus-Variable exportiert.
     *
     * @param auftragDaten
     */
    public void exportHurricanDataToVariables(AuftragDaten auftragDaten) {
        if (auftragDaten != null) {
            variable(VariableNames.AUFTRAG_ID, auftragDaten.getAuftragId());
        }
    }

    public TaifunDataFactory getTaifunDataFactory() {
        return applicationContext.getBean(TaifunDataFactory.class);
    }
}
