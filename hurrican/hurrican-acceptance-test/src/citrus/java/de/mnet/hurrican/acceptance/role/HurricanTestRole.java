/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.mnet.hurrican.acceptance.role;

import static org.testng.Assert.*;

import java.io.*;
import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import javax.sql.*;
import com.consol.citrus.TestAction;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.definition.RepeatOnErrorUntilTrueDefinition;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.testng.Assert;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.dao.cc.FeatureDAO;
import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.impl.HWSwitchServiceImpl;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.actions.AssignPortToAuftragTestAction;
import de.mnet.hurrican.acceptance.actions.CallFindOrCreateGeoIdTestAction;
import de.mnet.hurrican.acceptance.actions.CreateBundledTvAuftraegeTestAction;
import de.mnet.hurrican.acceptance.actions.CreateHvtStandortTestAction;
import de.mnet.hurrican.acceptance.actions.CreateTvAuftragTestAction;
import de.mnet.hurrican.acceptance.actions.MapLocationDataToGeoIdsTestAction;
import de.mnet.hurrican.acceptance.actions.PrepareCustomerDataTestAction;
import de.mnet.hurrican.acceptance.actions.PrepareOltStandortTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyCpsTxTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyEquipmentTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyHvtStandortTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyLocationCreatedTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyLocationReplacedTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyLocationUpdatedTestAction;
import de.mnet.hurrican.acceptance.actions.VerifyOltChildTestAction;
import de.mnet.hurrican.acceptance.actions.scanview.CreateArchiveDocumentRequestAction;
import de.mnet.hurrican.acceptance.actions.scanview.CreateGetDocumentRequestAction;
import de.mnet.hurrican.acceptance.actions.scanview.CreateSearchDocumentsRequestAction;
import de.mnet.hurrican.acceptance.builder.GeoIdBuilder;
import de.mnet.hurrican.acceptance.builder.HurricanAuftragBuilder;
import de.mnet.hurrican.acceptance.builder.HvtStandortTestBuilder;
import de.mnet.hurrican.acceptance.builder.OltChildTestBuilder;
import de.mnet.hurrican.acceptance.builder.RangierungTestBuilder;
import de.mnet.hurrican.acceptance.builder.StandortDataBuilder;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.builder.WbciTestBuilder;
import de.mnet.hurrican.acceptance.utils.HurricanUtils;
import de.mnet.hurrican.acceptance.utils.TestUtils;
import de.mnet.hurrican.ffm.citrus.VariableNames;
import de.mnet.hurrican.ffm.citrus.actions.AbstractFFMAction;
import de.mnet.hurrican.ffm.citrus.actions.CreateAndSendOrderServiceAction;
import de.mnet.hurrican.ffm.citrus.actions.UpdateAndSendOrderServiceAction;
import de.mnet.hurrican.wholesale.ws.inbound.WholesaleOrderServiceImpl;
import de.mnet.hurrican.wholesale.ws.inbound.processor.UpdateOrderProcessor;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;


/**
 *
 */
public class HurricanTestRole extends AbstractTestRole {

    @Autowired
    @Qualifier("hurricanDataSource")
    protected DataSource hurricanDataSource;
    @Autowired
    @Qualifier("taifunDataSource")
    protected DataSource taifunDataSource;
    @Autowired
    private FFMService ffmService;
    @Autowired
    private BAService baService;
    @Autowired
    private CCAuftragDAO ccAuftragDAO;
    @Autowired
    private HardwareDAO hardwareDAO;
    @Autowired
    private GeoIdDAO geoIdDAO;
    @Autowired
    private StandortDataBuilder standortDataBuilder;
    @Autowired
    private OltChildTestBuilder oltChildTestBuilder;
    @Autowired
    private RangierungTestBuilder rangierungTestBuilder;
    @Autowired
    private HvtStandortTestBuilder hvtStandortTestBuilder;
    @Autowired
    private HVTService hvtService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private HWService hwService;
    @Autowired
    private DSLAMService dslamService;
    @Autowired
    private HurricanUtils hurricanUtils;
    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private HWSwitchService hwSwitchService;
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private VerlaufDAO verlaufDAO;
    @Autowired
    private VerlaufAbteilungDAO verlaufAbteilungDAO;
    @Autowired
    private WholesaleOrderOutboundService wholesaleOrderOutboundService;
    @Autowired
    private FeatureDAO featureDAO;
    @Autowired
    private WholesaleOrderServiceImpl wholesaleOrderServiceImpl;

    /**
     * Triggers the {@link FFMService#createAndSendOrder(Verlauf)} service call with the given {@link Verlauf}.
     *
     * @param bauauftrag
     * @return
     */
    public CreateAndSendOrderServiceAction createAndSendOrder(Verlauf bauauftrag) {
        CreateAndSendOrderServiceAction action = new CreateAndSendOrderServiceAction(ffmService, bauauftrag);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Triggers the {@link FFMService#updateAndSendOrder(Verlauf)} service call with the given {@link Verlauf}.
     *
     * @param bauauftrag
     * @return
     */
    public UpdateAndSendOrderServiceAction updateAndSendOrder(Verlauf bauauftrag) {
        UpdateAndSendOrderServiceAction action = new UpdateAndSendOrderServiceAction(ffmService, bauauftrag);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public FFMService getFFMService() {
        return ffmService;
    }

    /**
     * Saves entity to database using Hurrican DAO.
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T extends Serializable> T save(T entity) {
        return ccAuftragDAO.store(entity);
    }

    public CCAuftragDAO getAuftragDAO() {
        return ccAuftragDAO;
    }

    public VerlaufDAO getVerlaufDAO() {
        return verlaufDAO;
    }

    public HurricanAuftragBuilder getHurricanAuftragBuilder() {
        return applicationContext.getBean(HurricanAuftragBuilder.class);
    }

    public VerlaufTestBuilder getVerlaufTestBuilder() {
        return applicationContext.getBean(VerlaufTestBuilder.class).configure((AbstractHurricanTestBuilder) testBuilder);
    }

    public WbciTestBuilder getWbciTestBuilder() {
        return applicationContext.getBean(WbciTestBuilder.class);
    }

    public BAService getBaService() {
        return applicationContext.getBean(BAService.class);
    }

    public VerlaufAbteilungDAO getVerlaufAbteilungDAO() {
        return verlaufAbteilungDAO;
    }

    public WholesaleOrderOutboundService getWholesleOrderOutboundService() {
        return applicationContext.getBean(WholesaleOrderOutboundService.class);
    }

/*
    public WholesaleOrderServiceImpl getWholesaleOrderServiceImpl() {
        return applicationContext.getBean(WholesaleOrderServiceImpl.class);
    }
*/

    /**
     * CleanUp von Daten, die bei mehrfacher Ausfuehrung von Tests Probleme verursachen
     */
    public void cleanUpTestData() {
        // add finally block for testdata cleanup to all test cases
        testBuilder.doFinally(
                testBuilder.catchException(
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/01_cleanup_ips.sql")
                                .ignoreErrors(true),
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/02_cleanup_geoIds.sql")
                                .ignoreErrors(true),
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/03_cleanup_vpn.sql")
                                .ignoreErrors(true),
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/04_cleanup_wbciGeschaeftsfall.sql")
                                .ignoreErrors(true),
                        testBuilder.sql(hurricanDataSource)
                                .sqlResource("classpath:database/05_cleanup_WholesaleAudit.sql")
                                .ignoreErrors(true)
                )
        );
    }

    /**
     * Reads order status from Hurrican database and verifies with expected status. Also reads FFM Abteilung status and
     * verifies this state with expected ffm state.
     */
    public void assertOrderState(String workforceOrderId, Long orderStatus, Long ffmOrderStatus) {
        assertOrderState(workforceOrderId, orderStatus, ffmOrderStatus, null, null, null);
    }

    /**
     * Reads order state from Hurrican database and verifies with expected state. Also reads FFM Abteilung state and
     * verifies it with the expected ffm state.
     *
     * @param workforceOrderId       {@link Verlauf#workforceOrderId}
     * @param verlaufState           is equals to the {@link Verlauf#verlaufStatusId}.
     * @param verlaufAbteilungState  is equals to the {@link VerlaufAbteilung#verlaufStatusId}
     * @param isFlagNotPossibleSet   describes if an workforce order have been finished with success.
     * @param notPossibleReasenRefId Reference-ID for {@link Reference}  table.
     */
    public AbstractFFMAction assertOrderState(final String workforceOrderId, final Long verlaufState,
            final Long verlaufAbteilungState, final Boolean isFlagNotPossibleSet, final Long notPossibleReasenRefId,
            final String bearbeiter) {
        AbstractFFMAction action = new AbstractFFMAction("validate BA-State") {
            @Override
            public void doExecute(TestContext context) {
                Verlauf bauauftrag = baService.findVerlaufByWorkforceOrder(workforceOrderId);
                assertNotNull(bauauftrag, String.format("no valid bauauftrag found for workforceId '%s'", workforceOrderId));
                assertEquals(bauauftrag.getVerlaufStatusId(), verlaufState,
                        String.format("state of Bauauftrag '%s'", bauauftrag.getId()));
                try {
                    VerlaufAbteilung verlaufAbteilung = baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM);
                    assertEquals(verlaufAbteilung.getVerlaufStatusId(), verlaufAbteilungState,
                            String.format("state of Abteilung-Bauauftrag '%s'", verlaufAbteilung.getId()));
                    assertEquals(verlaufAbteilung.getNotPossible(), isFlagNotPossibleSet,
                            String.format("flag 'not possible' of Abteilung-Bauauftrag '%s'", verlaufAbteilung.getId()));

                    assertEquals(verlaufAbteilung.getNotPossibleReasonRefId(), notPossibleReasenRefId,
                            String.format("Abschlussgrund  'notPossibleReasenRefId' of Abteilung-Bauauftrag '%s'", verlaufAbteilung.getId()));
                    assertEquals(verlaufAbteilung.getBearbeiter(), bearbeiter,
                            String.format("Bearbeiter '%s' von Abteilung-Bauauftrag nicht wie erwartet '%s'",
                                    verlaufAbteilung.getBearbeiter(), bearbeiter));
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException(String.format("cannot load VerlaufAbteilung for verlaufID='%s' and abteilungID='%s'", bauauftrag.getId(), Abteilung.FFM), e);

                }
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.repeatOnError(action).until("i gt 15");
        return action;
    }


    /**
     * Checks that the {@link VerlaufAbteilung#bemerkung} of the assigned verlaufId contains the assigned String. If
     * expectedBemerkung is NULL, the {@link VerlaufAbteilung#bemerkung} will be checked against NULL.
     *
     * @param expectedBemerkung expected String which should be contained
     * @param verlaufId         ID des {@link Verlauf}s
     */
    public AbstractFFMAction assertAbteilungBemerkung(final String expectedBemerkung, final Long verlaufId) {
        AbstractFFMAction action = new AbstractFFMAction("validate BA-Abteilungs-Bemerkung") {
            @Override
            public void doExecute(TestContext context) {
                try {
                    VerlaufAbteilung verlaufAbteilung = baService.findVerlaufAbteilung(verlaufId, Abteilung.FFM);
                    String bemerkung = verlaufAbteilung.getBemerkung();
                    if (expectedBemerkung == null) {
                        Assert.assertNull(bemerkung, "BA-Abteilungs-Bemerkung should be null!");
                    }
                    else {
                        if (bemerkung == null) {
                            throw new CitrusRuntimeException("Bemerkung should not be null!");
                        }
                        Assert.assertTrue(bemerkung.contains(expectedBemerkung),
                                String.format("BA-Abteilungs-Bemerkung '%s' doesn't contain '%s'", bemerkung, expectedBemerkung));
                    }
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException(String.format("cannot load VerlaufAbteilung for verlaufID='%s' and abteilungID='%s'", verlaufId, Abteilung.FFM), e);

                }
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.repeatOnError(action).until("i gt 15");
        return action;
    }

    public TestAction createHurricanFttxTelefonieAuftrag() {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                Long billingOrderNoOrig = (Long) context.getVariableObject(VariableNames.BILLING_ORDER_NO_ORIG);
                Long billingKundeNo = (Long) context.getVariableObject(VariableNames.BILLING_KUNDE_NO);
                Long billingDnNoOrig = (Long) context.getVariableObject(VariableNames.BILLING_DN_NO_ORIG);
                if (billingOrderNoOrig == null || billingKundeNo == null || billingDnNoOrig == null) {
                    throw new CitrusRuntimeException("No billingOrderNoOrig or billingKundeNo or billingDnNoOrig set "
                            + "within the test context!. To be able to create a new hurrican order, you need to set a "
                            + "billingOrderNoOrig, billingKundeNo and billingDnNoOrig first.");
                }
                AuftragDaten auftragDaten = getHurricanAuftragBuilder().buildAuftragFttxTelefonie(billingOrderNoOrig,
                        billingKundeNo, billingDnNoOrig);
                context.setVariable(VariableNames.AUFTRAG_ID, auftragDaten.getAuftragId());
            }
        };
        action.setName("createHurricanFttxTelefonieAuftrag");
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public TestAction createTaifunAuftragMaxiGlasfaserDsl(final int dnCount) {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                GeneratedTaifunData generatedTaifunData =
                        context.getApplicationContext().getBean(TaifunDataFactory.class)
                                .maxiGlasfaserDsl(dnCount, false, true, null)
                                .persist();
                context.setVariable(VariableNames.BILLING_ORDER_NO_ORIG, generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
                context.setVariable(VariableNames.BILLING_KUNDE_NO, generatedTaifunData.getKunde().getKundeNo());
                context.setVariable(VariableNames.BILLING_DN_NO_ORIG, generatedTaifunData.getDialNumbers().get(0).getDnNoOrig());
            }
        };

        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateTvAuftragTestAction createTvAuftrag(final Long prodId, final Long standortTypRefId) {
        CreateTvAuftragTestAction action =
                new CreateTvAuftragTestAction(taifunDataSource, ccAuftragDAO, prodId, standortTypRefId);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateBundledTvAuftraegeTestAction createBuendledTvAuftraege(final Long prodId, final Long standortTypRefId) {
        CreateBundledTvAuftraegeTestAction action =
                new CreateBundledTvAuftraegeTestAction(taifunDataSource, ccAuftragDAO, prodId, standortTypRefId);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateHvtStandortTestAction createHvtStandort() {
        CreateHvtStandortTestAction action = new CreateHvtStandortTestAction(hvtStandortTestBuilder);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public VerifyHvtStandortTestAction verifyHvtStandort(Long standortTypRefId) {
        VerifyHvtStandortTestAction action = new VerifyHvtStandortTestAction(hvtService, standortTypRefId);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public VerifyEquipmentTestAction verifyEquipment(String hwEqn, String leiste, String stift) {
        VerifyEquipmentTestAction action = new VerifyEquipmentTestAction(rangierungsService, hwEqn, leiste, stift);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public PrepareOltStandortTestAction prepareOltStandort(Long hvtStandortTyp, String hwRackType) {
        PrepareOltStandortTestAction action = new PrepareOltStandortTestAction(standortDataBuilder, hvtStandortTyp, hwRackType);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public PrepareCustomerDataTestAction prepareCustomerData(String hwRackType) {
        PrepareCustomerDataTestAction action = new PrepareCustomerDataTestAction(hwRackType, standortDataBuilder,
                oltChildTestBuilder, rangierungTestBuilder);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public AbstractFFMAction createOntBezeichnung() {
        return createGeraeteBezeichnung("ONT");
    }

    public AbstractFFMAction createDpoBezeichnung() {
        return createGeraeteBezeichnung("DPO");
    }

    private AbstractFFMAction createGeraeteBezeichnung(String prefix) {
        String value = prefix + "-" + RandomStringUtils.randomAlphanumeric(9).toUpperCase();
        return saveVariableAction(VariableNames.GERAETE_BEZEICHNUNG, value);
    }

    private AbstractFFMAction saveVariableAction(final String name, final String value) {
        AbstractFFMAction action = new AbstractFFMAction(String.format("saveVariable:%s", name)) {
            @Override
            public void doExecute(TestContext context) {
                context.setVariable(name, value);
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    private String getOltBezeichnung(TestContext context) {
        return TestUtils.getOltBezeichnung(context);
    }

    public AbstractFFMAction verifySerialNumber(final String hwRackType, final String expectedSerialNumber) {
        if (hwRackType.equals(HWRack.RACK_TYPE_DPO)) {
            return verifyDpoSerialNumber(expectedSerialNumber);
        }
        else if (hwRackType.equals(HWRack.RACK_TYPE_ONT)) {
            return verifyOntSerialNumber(expectedSerialNumber);
        }

        throw new CitrusRuntimeException(String.format("Unsupported HWRack type '%s'", hwRackType));
    }

    public AbstractFFMAction verifyOntSerialNumber(final String expectedSerialNumber) {
        return verifyOltChildSerialNumber(expectedSerialNumber, VariableNames.GERAETE_BEZEICHNUNG, HWOnt.class);
    }

    public AbstractFFMAction verifyDpoSerialNumber(final String expectedSerialNumber) {
        return verifyOltChildSerialNumber(expectedSerialNumber, VariableNames.GERAETE_BEZEICHNUNG, HWDpo.class);
    }

    private <T extends HWOltChild> AbstractFFMAction verifyOltChildSerialNumber(final String expectedSerialNumber, final String oltChildBezeichungVariableName, final Class<T> clazz) {
        AbstractFFMAction action = new AbstractFFMAction(String.format("verifyOltChildSerialNumber:%s", oltChildBezeichungVariableName)) {
            @Override
            public void doExecute(TestContext context) {
                final String oltChildBezeichung = readMandatoryVariableFromTestContext(context, oltChildBezeichungVariableName);
                final String oltBezeichung = getOltBezeichnung(context);
                T oltChild = findOltChild(oltBezeichung, oltChildBezeichung, clazz);
                assertEquals(oltChild.getSerialNo(), expectedSerialNumber);
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public AbstractFFMAction verifyOntFreigabe(final Date expectedFreigabe) {
        return verifyOltChildFreigabe(expectedFreigabe, VariableNames.GERAETE_BEZEICHNUNG, HWOnt.class);
    }

    public AbstractFFMAction verifyDpoFreigabe(final Date expectedFreigabe) {
        return verifyOltChildFreigabe(expectedFreigabe, VariableNames.GERAETE_BEZEICHNUNG, HWDpo.class);
    }

    public <T extends HWOltChild> AbstractFFMAction verifyOltChildFreigabe(final Date expectedFreigabe, final String oltChildBezeichungVariableName, final Class<T> clazz) {
        AbstractFFMAction action = new AbstractFFMAction(String.format("verifyOltChildSerialNumber:%s", oltChildBezeichungVariableName)) {
            @Override
            public void doExecute(TestContext context) {
                final String oltChildBezeichung = readMandatoryVariableFromTestContext(context, oltChildBezeichungVariableName);
                final String oltBezeichung = getOltBezeichnung(context);
                T oltChild = findOltChild(oltBezeichung, oltChildBezeichung, clazz);
                assertTrue(DateTools.isDateEqual(oltChild.getFreigabe(), expectedFreigabe),
                        "Freigabedatum von OLT/DPO nicht gesetzt oder ungueltig!");
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }


    public AbstractFFMAction verifyHwRackCreated(final String hwRackType) {
        if (hwRackType.equals(HWRack.RACK_TYPE_DPO)) {
            return verifyDpoCreated();
        }
        else if (hwRackType.equals(HWRack.RACK_TYPE_ONT)) {
            return verifyOntCreated();
        }

        throw new CitrusRuntimeException(String.format("Unsupported HWRack type '%s'", hwRackType));
    }

    public AbstractFFMAction verifyOntCreated() {
        return verifyOltChildCreated(VariableNames.GERAETE_BEZEICHNUNG, HWOnt.class);
    }

    public AbstractFFMAction verifyDpoCreated() {
        return verifyOltChildCreated(VariableNames.GERAETE_BEZEICHNUNG, HWDpo.class);
    }

    public AbstractFFMAction verifyDpoCreated(String chassisBezeichnung, String chassisPosition) {
        Map<String, Object> properties = new HashMap<String, Object>() {{
            put("chassisIdentifier", chassisBezeichnung);
            put("chassisSlot", chassisPosition);
        }};
        return verifyOltChildCreated(VariableNames.GERAETE_BEZEICHNUNG, HWDpo.class, properties);
    }

    public AssignPortToAuftragTestAction assignPortToAuftrag(String leiste, String stift) {
        AssignPortToAuftragTestAction action = new AssignPortToAuftragTestAction(ccAuftragDAO, endstellenService, rangierungsService, leiste, stift);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    private <T extends HWOltChild> AbstractFFMAction verifyOltChildCreated(final String oltChildBezeichungVariableName, final Class<T> clazz) {
        return verifyOltChildCreated(oltChildBezeichungVariableName, clazz, Collections.emptyMap());
    }

    private <T extends HWOltChild> AbstractFFMAction verifyOltChildCreated(final String oltChildBezeichungVariableName,
            final Class<T> clazz, final Map<String, Object> properties) {
        AbstractFFMAction action = new AbstractFFMAction(String.format("verifyOltChildCreated:%s", oltChildBezeichungVariableName)) {
            @Override
            public void doExecute(TestContext context) {
                final String oltChildBezeichung = readMandatoryVariableFromTestContext(context, oltChildBezeichungVariableName);
                final String oltBezeichung = getOltBezeichnung(context);
                T oltChild = findOltChild(oltBezeichung, oltChildBezeichung, clazz);
                if (!CollectionUtils.isEmpty(properties)) {
                    for (String property : properties.keySet()) {
                        assertEquals(getFieldValue(clazz, oltChild, property), properties.get(property));
                    }
                }
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    private Object getFieldValue(Class<?> clazz, Object target, String property) {
        Field field = ReflectionUtils.findField(clazz, property);
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }

    private <T extends HWOltChild> T findOltChild(final String oltBezeichung, final String oltChildBezeichung, final Class<T> clazz) {
        try {
            T oltChild = hurricanUtils.findCreatedOltChild(oltBezeichung, oltChildBezeichung, clazz);
            assertNotNull(oltChild);
            return oltChild;
        }
        catch (FindException e) {
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

    public AbstractFFMAction verifyOntPortCreated() {
        return verifyOltChildPortCreated(VariableNames.GERAETE_BEZEICHNUNG, "ETH", "1-2");
    }

    public AbstractFFMAction verifyDpoPortCreated() {
        return verifyOltChildPortCreated(VariableNames.GERAETE_BEZEICHNUNG, "VDSL2", "1-1");
    }

    private AbstractFFMAction verifyOltChildPortCreated(final String oltChildBezeichungVariableName, final String schnitstelle, final String hwEqnNr) {
        AbstractFFMAction action = new AbstractFFMAction(String.format("verifyOltChildPortCreated:%s", oltChildBezeichungVariableName)) {
            @Override
            public void doExecute(TestContext context) {
                final String oltChildBezeichung = readMandatoryVariableFromTestContext(context, oltChildBezeichungVariableName);
                try {
                    HWRack oltChild = hwService.findActiveRackByBezeichnung(oltChildBezeichung);
                    assertNotNull(oltChild);
                    List<HWBaugruppe> hwBaugruppen = hwService.findBaugruppen4Rack(oltChild.getId());
                    assertNotNull(hwBaugruppen);
                    assertEquals(hwBaugruppen.size(), 1);
                    HWBaugruppe ontChildBaugruppe = hwBaugruppen.get(0);
                    assertEquals(ontChildBaugruppe.getModNumber(), "0-1");

                    List<Equipment> equipments = rangierungsService.findEquipments4HWBaugruppe(ontChildBaugruppe.getId());
                    assertNotNull(equipments);
                    assertEquals(equipments.size(), 1);
                    Equipment portCreated = equipments.get(0);
                    assertEquals(portCreated.getHwEQN(), hwEqnNr);
                    assertEquals(portCreated.getHwSchnittstelle(), schnitstelle);
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException(e.getMessage(), e);
                }
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public VerifyOltChildTestAction verifyOntChild() {
        return verifyOltChild(VariableNames.GERAETE_BEZEICHNUNG, HWOnt.class);
    }

    public VerifyOltChildTestAction verifyDpoChild() {
        return verifyOltChild(VariableNames.GERAETE_BEZEICHNUNG, HWDpo.class);
    }

    private VerifyOltChildTestAction verifyOltChild(String oltChildBezeichnungVariableName, Class<? extends HWOltChild> clazz) {
        VerifyOltChildTestAction action = new VerifyOltChildTestAction(hurricanUtils, oltChildBezeichnungVariableName, clazz);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateArchiveDocumentRequestAction createAndSendArchiveDocumentRequest(String vertragsnummer,
            ArchiveDocumentType archiveDocumentType) {
        CreateArchiveDocumentRequestAction action =
                new CreateArchiveDocumentRequestAction(archiveService, vertragsnummer, archiveDocumentType);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateGetDocumentRequestAction createAndSendGetDocumentRequest(String vertragsnummer,
            ArchiveDocumentType archiveDocumentType, String documentId) {
        CreateGetDocumentRequestAction action =
                new CreateGetDocumentRequestAction(archiveService, vertragsnummer, archiveDocumentType, documentId);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CreateSearchDocumentsRequestAction createAndSendSearchDocumentsRequest(String vertragsnummer,
            ArchiveDocumentType archiveDocumentType) {
        CreateSearchDocumentsRequestAction action =
                new CreateSearchDocumentsRequestAction(archiveService, vertragsnummer, archiveDocumentType);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public VerifyCpsTxTestAction verifyCpsTx(Long serviceOrderType, Long txState, boolean searchByOrderId) {
        VerifyCpsTxTestAction verifyCpsTxTestAction =
                new VerifyCpsTxTestAction(ccAuftragDAO, hardwareDAO, serviceOrderType, txState, searchByOrderId);
        verifyCpsTxTestAction.setActor(hurricanTestActor);
        testBuilder.repeatOnError(verifyCpsTxTestAction)
                .autoSleep(2000L)
                .until("i gt 20");

        return verifyCpsTxTestAction;
    }

    /**
     * assign the default configured DSLAM profile to the current hurrican order
     */
    public AbstractFFMAction assignDefaultDslamProfil(final Long auftragId) {
        AbstractFFMAction action = new AbstractFFMAction("createDefaultDslamProfile") {
            @Override
            public void doExecute(TestContext context) {
                try {
                    DSLAMProfile dslamProfile = dslamService.assignDSLAMProfile(auftragId, Date.from(ZonedDateTime.now().minusDays(1).toInstant()), null, null);
                    LOGGER.info(String.format("DSLAM-Profile '%s' for technical order '%s' assigned.", dslamProfile.getName(), auftragId));
                }
                catch (FindException | StoreException e) {
                    throw new CitrusRuntimeException("Cannot create default DSLAM-Profile for techincal order " + auftragId, e);
                }
            }
        };
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public GeoIdBuilder.GeoIdBuilderResult createGeoId() {
        return new GeoIdBuilder().build(ccAuftragDAO);
    }

    public TestAction mapLocationDataToGeoIdsTestAction(String street, String houseNum, String houseNumExt,
            String zipCode, String city, String district) {
        MapLocationDataToGeoIdsTestAction action = new MapLocationDataToGeoIdsTestAction(
                availabilityService, street, houseNum, houseNumExt, zipCode, city, district);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public CallFindOrCreateGeoIdTestAction callFindOrCreateGeoId(Long geoId) {
        CallFindOrCreateGeoIdTestAction action = new CallFindOrCreateGeoIdTestAction(availabilityService, geoId);
        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public RepeatOnErrorUntilTrueDefinition verifyLocationCreated(Long geoId) {
        VerifyLocationCreatedTestAction action = new VerifyLocationCreatedTestAction(geoIdDAO, geoId);
        action.setActor(hurricanTestActor);
        return testBuilder.repeatOnError(action)
                .autoSleep(1000L)
                .until("i gt 10");
    }

    public RepeatOnErrorUntilTrueDefinition verifyLocationReplaced(Long oldGeoId, Long newGeoId) {
        VerifyLocationReplacedTestAction action = new VerifyLocationReplacedTestAction(geoIdDAO, oldGeoId, newGeoId);
        action.setActor(hurricanTestActor);
        return testBuilder.repeatOnError(action)
                .autoSleep(1000L)
                .until("i gt 10");
    }

    public RepeatOnErrorUntilTrueDefinition verifyLocationStreetName(Long geoId, String updatedStreetName) {
        VerifyLocationUpdatedTestAction action = new VerifyLocationUpdatedTestAction(geoIdDAO, geoId, updatedStreetName);
        action.setActor(hurricanTestActor);
        return testBuilder.repeatOnError(action)
                .autoSleep(1000L)
                .until("i gt 10");
    }

    /**
     * assign switch to auftragTechnik for the provided auftragId using the implemented business logic
     */
    public void assignSwitchToAuftragTechnik(Long auftragId) {
        try {
            final HWSwitch hwSwitch = ccAuftragService.calculateSwitch4VoipAuftrag(auftragId).getLeft().calculatedHwSwitch;
            ccAuftragService.updateSwitchForAuftrag(auftragId, hwSwitch);
        }
        catch (FindException e) {
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

    public TestAction migrateOrdersToSwitch(final List<SwitchMigrationView> switchMigrationViews) {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                new SimpleAsyncTaskExecutor().execute(() -> {
                    byte[] xlsData = null;
                    try {
                        xlsData = hwSwitchService.moveOrdersToSwitch(switchMigrationViews, hwSwitchService.findSwitchByName("MUC07"),
                                hwSwitchService.findSwitchByName("MUC06"),
                                9999L, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
                        Workbook workbook = XlsPoiTool.loadExcelFile(xlsData);
                        Sheet auftragSheet = workbook.getSheetAt(1);
                        Map<Long, String> migrateMessages = new HashMap<>();
                        for(int i = 1; i <= auftragSheet.getLastRowNum(); i++) {
                            Row row = auftragSheet.getRow(i);
                            Optional.ofNullable(row.getCell(HWSwitchServiceImpl.ROW_MESSAGE)).ifPresent(c -> {
                                Long auftragId = (long) row.getCell(HWSwitchServiceImpl.ROW_AUFTRAG_NR).getNumericCellValue();
                                Optional.ofNullable(StringUtils.stripToNull(c.getStringCellValue())).ifPresent(s -> migrateMessages.put(auftragId, s));
                            });
                        }
                        LOGGER.warn(migrateMessages);
                        testContext.setVariable(VariableNames.MIGRATE_ORDER_WARNINGS, migrateMessages);
                    }
                    catch (ServiceNotFoundException | IOException | InvalidFormatException e) {
                        throw new CitrusRuntimeException(e.getMessage(), e);
                    }
                });
            }
        };

        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public TestAction verifyMigrateOrderWarnings(int expectedWarningCount) {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                if (testContext.getVariables().containsKey(VariableNames.MIGRATE_ORDER_WARNINGS)) {
                    Map<Long, String> migrateMessages = (Map<Long, String>) testContext.getVariableObject(VariableNames.MIGRATE_ORDER_WARNINGS);
                    if(migrateMessages.size() != expectedWarningCount) {
                        throw new CitrusRuntimeException(String.format("Failed to validate number of migrate order warnings, "
                                + "expected %s but was %s", expectedWarningCount, migrateMessages.size()));
                    }
                }
                else {
                    throw new CitrusRuntimeException("Unable to read migrate order warnings - missing variable object");
                }
            }
        };

        action.setActor(hurricanTestActor);
        testBuilder.repeatOnError(action).until("i gt 15");
        return action;
    }

    public VerlaufTestBuilder.CreatedData createFttxDslFonWithVoipAccountsAuftrag(AbstractHurricanTestBuilder testBuilder) {
        GeneratedTaifunData generatedTaifunData = testBuilder.getTaifunDataFactory()
                .withCreateDeviceFritzBox()
                .surfAndFonWithDns(1).persist();

        //build technical order and Bauauftrag for the creaded billing order
        VerlaufTestBuilder.CreatedData createdData = getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_DSL_FON)
                .withVoIP(true)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withGeneratedTaifunData(generatedTaifunData)
                .buildBauauftrag();

        Long auftragId = createdData.auftrag.getAuftragId();
        assignSwitchToAuftragTechnik(auftragId);

        EG2Auftrag eg2Auftrag = new EG2AuftragBuilder()
                .setPersist(false).build();
        eg2Auftrag.setEgId(12L);
        eg2Auftrag.setAuftragId(auftragId);
        ccAuftragDAO.store(eg2Auftrag);

        EGConfig egConfig = new EGConfigBuilder()
                .withSerialNumber("123-456-789")
                .setPersist(false).build();
        egConfig.setEg2AuftragId(eg2Auftrag.getId());
        ccAuftragDAO.store(egConfig);

        //create VOIP accounts
        generatedTaifunData.getDialNumbers().stream().forEach(
                dn -> getHurricanAuftragBuilder().createVoip(dn.getDnNoOrig(), auftragId));

        //set Niederlassung in AuftragTechnik
        List<AuftragTechnik> auftraegeTech = ccAuftragDAO.findByProperty(AuftragTechnik.class, AuftragTechnik.AUFTRAG_ID, auftragId);
        AuftragTechnik auftragTechnik = auftraegeTech.stream().findFirst().get();
        auftragTechnik.setNiederlassungId(Niederlassung.ID_MUENCHEN);
        ccAuftragDAO.store(auftragTechnik);

        //set CPS provisioning true for HVTStandort
        List<Endstelle> endstellen = ccAuftragDAO.findByProperty(Endstelle.class, Endstelle.ENDSTELLE_GRUPPE_ID, auftragTechnik.getAuftragTechnik2EndstelleId());
        Optional<Endstelle> endstelle = endstellen.stream().filter(Endstelle::isEndstelleB).findFirst();
        HVTStandort hvtStandort = ccAuftragDAO.findById(endstelle.get().getHvtIdStandort(), HVTStandort.class);
        hvtStandort.setCpsProvisioning(true);
        ccAuftragDAO.store(hvtStandort);

        //assign a default DSLAM profile
        assignDefaultDslamProfil(auftragId);

        return createdData;
    }

    /**
     * Verify that HwSwitch with given name is assigned to order.
     *
     * @param auftragId
     * @param hwSwitchName
     */
    public TestAction verifyHwSwitch(Long auftragId, String hwSwitchName) {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                try {
                    AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(auftragId);
                    HWSwitch expectedSwitch = hwSwitchService.findSwitchByName(hwSwitchName);
                    if (!auftragTechnik.getHwSwitch().equals(expectedSwitch)) {
                        throw new CitrusRuntimeException(String.format("Invalid switch assigned to order '%s' expected '%s', but was '%s'",
                                auftragId, hwSwitchName, auftragTechnik.getHwSwitch().getName()));
                    }
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException("Falied to get AuftragTechnik for order: " + auftragId);
                }
            }
        };

        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    public TestAction changeAuftragTechnik(Long auftragId, EntityCallback<AuftragTechnik> entityCallback) {
        AbstractTestAction action = new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                AuftragTechnik auftragTechnik;
                try {
                    auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(auftragId);
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException(String.format("Failed to retrieve entity of type %s for auftragId %s",
                            AuftragTechnik.class, auftragId));
                }
                entityCallback.doWithEntity(auftragTechnik);
                save(auftragTechnik);
            }
        };

        action.setActor(hurricanTestActor);
        testBuilder.action(action);
        return action;
    }

    /**
     * Generic entity callback used when changing database entities. Test role takes care on entity query and save
     * operation. Callback just adds change operations on entity object.
     *
     * @param <T>
     */
    public interface EntityCallback<T> {
        void doWithEntity(T entity);
    }

    public CCAuftragService getCcAuftragService() {
        return ccAuftragService;
    }

    public FeatureDAO getFeatureDAO() {
        return featureDAO;
    }
}
