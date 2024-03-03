/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.testng.Assert.*;

import java.util.*;
import javax.sql.*;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunkBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSIPInterTrunkData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Unit Test for {@link CPSGetSIPInterTrunkDataCommandTest}
 *
 *
 */
public class CPSGetSIPInterTrunkDataCommandTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CPSGetSIPInterTrunkDataCommandTest.class);
    private final String SWITCH = "AUG09";
    private List<Rufnummer> dns4Order;
    private Kunde kunde;
    private Kunde reseller;
    private Long customerNo;
    private Long resellerCustomerNo;
    private Long taifunOrderNo;
    private String onkz;
    private String actDnBase;
    private CPSTransaction cpsTx;

    private AuftragBuilder auftragBuilder;
    private AuftragDatenBuilder auftragDatenBuilder;
    private AuftragBuilder auftragSipInterTrunkBuilder;
    private AuftragDatenBuilder auftragDatenSipInterTrunkBuilder;

    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
        DataSource taifunDataSource = (DataSource) getBean("taifunDataSource");
        TaifunDataFactory dataFactory = getTaifunDataFactory();

        // create taifun reseller (i.e. Haupt Kunde)
        GeneratedTaifunData resellerData = dataFactory.createKunde(false, null, false);
        resellerData.persistCustomerDataOnly();
        reseller = resellerData.getKunde();
        resellerCustomerNo = reseller.getKundeNo();
        LOGGER.info("created reseller: " + reseller);

        // create taifun customer and taifun auftrag
        GeneratedTaifunData taifunData = dataFactory.surfAndFonWithDns(1);
        taifunData.getKunde().setHauptKundenNo(resellerCustomerNo);
        taifunData.persist();

        kunde = taifunData.getKunde();
        customerNo = kunde.getKundeNo();
        BAuftrag billingAuftrag = taifunData.getBillingAuftrag();
        taifunOrderNo = billingAuftrag.getAuftragNoOrig();
        dns4Order = taifunData.getDialNumbers();
        onkz = dns4Order.get(0).getOnKz();
        actDnBase = dns4Order.get(0).getDnBase();

        LOGGER.info("Created customer: " + kunde);
        LOGGER.info(String.format("Created billing order (id: %s, origId: %s)", billingAuftrag.getAuftragNo(), billingAuftrag.getAuftragNoOrig()));

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("test")
                .withMaxDnCount(1)
                .withDNBlock(Boolean.FALSE)
                .withProduktGruppeId(ProduktGruppe.SIP_INTER_TRUNK_ENDKUNDE);

        auftragBuilder = getBuilder(AuftragBuilder.class).withKundeNo(customerNo);
        auftragDatenBuilder = auftragBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(taifunOrderNo)
                .withProdBuilder(produktBuilder);

        ProduktBuilder produktSipInterTrunkBuilder = getBuilder(ProduktBuilder.class)
                .withAnschlussart("sip_inter_trunk")
                .withProduktGruppeId(ProduktGruppe.SIP_INTER_TRUNK);

        auftragSipInterTrunkBuilder = getBuilder(AuftragBuilder.class).withKundeNo(resellerCustomerNo);
        auftragDatenSipInterTrunkBuilder = auftragSipInterTrunkBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragSipInterTrunkBuilder)
                .withAuftragNoOrig(taifunOrderNo)
                .withProdBuilder(produktSipInterTrunkBuilder);

        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withOrderNoOrig(taifunOrderNo)
                .withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .build();

        buildSIPInterTrunk();
    }

    private void buildSIPInterTrunk() {
        getBuilder(AuftragSIPInterTrunkBuilder.class)
                .withAuftragBuilder(auftragSipInterTrunkBuilder)
                .withHwSwitch(getBuilder(HWSwitchBuilder.class)
                                .withType(HWSwitchType.EWSD)
                                .withName(SWITCH)
                                .build()
                )
                .withTrunkGroup("trunkGroup")
                .build();
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecute() throws Exception {
        CCAuftragService auftragService = (CCAuftragService) getBean("de.augustakom.hurrican.service.cc.CCAuftragService");

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetSIPInterTrunkDataCommand command = (CPSGetSIPInterTrunkDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetSIPInterTrunkDataCommand");
        command.prepare(CPSGetSIPInterTrunkDataCommand.KEY_AUFTRAG_DATEN, auftragDatenBuilder.get());
        command.setRufnummerService((RufnummerService) getBean("de.augustakom.hurrican.service.billing.RufnummerService"));
        command.setKundenService((KundenService) getBean("de.augustakom.hurrican.service.billing.KundenService"));
        command.setCcAuftragService(auftragService);

        command.prepare(CPSGetSIPInterTrunkDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetSIPInterTrunkDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertTrue(((ServiceCommandResult) result).isOk(), "Result is not valid!");

        // analyse result
        List<CPSSIPInterTrunkData> sipInterTrunkData = cpsServiceOrderData.getSipInterTrunk();
        assertNotEmpty(sipInterTrunkData, "No SIP InterTrunk data found!");
        assertEquals(sipInterTrunkData.size(), 1);

        CPSSIPInterTrunkData sipInterTrunk = sipInterTrunkData.get(0);
        assertEquals(sipInterTrunk.getResellerId(), String.format("%s", resellerCustomerNo));
        assertNotEmpty(sipInterTrunk.getSwitche(), "no switches defined");
        assertEquals(sipInterTrunk.getSwitche().size(), 1);
        assertEquals(sipInterTrunk.getSwitche().get(0), SWITCH);
        assertEquals(sipInterTrunk.getDn(), actDnBase);
        assertEquals(sipInterTrunk.getLac(), onkz);
    }

}
