/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.math.*;
import java.util.*;
import com.thoughtworks.xstream.XStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPortBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.EndgeraetPortBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Leistung2DNBuilder;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.Leistungsbuendel2ProduktBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.SIPDomainReference;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNServiceData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPIADPortsData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPSIPAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsCallScreening;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Unit Test for {@link CPSGetVoIPDataCommandTest}
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class CPSGetVoIPDataCommandTest extends AbstractHurricanBaseServiceTest {

    private RufnummerService rufnummerService;
    private LeistungService leistungService;
    private List<Rufnummer> dns4Order;

    private static final int EXPECTED_SIP_ACCOUNT_COUNT = 1;

    private static final Long TAIFUN_ORDER__NO = AuftragBuilder.getLongId();
    private static final String EWSD_EQN = "0010-01-07-09";
    private static final String ONKZ = "0821";
    private static final String ACT_DN_BASE = "456789";
    private static final String DN_SERVICE_PROV_NAME_ACTIVE = "CPS1";
    private static final String DN_SERVICE_VALUE_ACTIVE = "01-49&02-49-821&03-49-821-45000&04-39-055";
    private static final String DN_SERVICE_VALUE_ACTIVE_RESULT_OLD = "01-49;02-49-821;03-49-821-45000;04-39-055";

    private Rufnummer aktDN;
    private Long dnExtLeistungNo;
    private AuftragVoIPDN auftragVoIPDN;
    private List<AuftragVoIPDN2EGPort> auftragVoIPdn2EgPorts;
    private CPSTransaction cpsTx;

    private AuftragBuilder auftragBuilder;

    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    private HWSwitch prepareTest(HWSwitchType switchType) throws FindException {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withAnschlussart("test")
                .withMaxDnCount(1).withDNBlock(Boolean.FALSE);

        auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = auftragBuilder.getAuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder).withAuftragNoOrig(TAIFUN_ORDER__NO).withProdBuilder(produktBuilder);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class).withHvtGruppeBuilder(
                hvtGruppeBuilder).withEwsdOr1(999);

        PhysikTypBuilder ptBuilder = getBuilder(PhysikTypBuilder.class).withCpsTransferMethod("ADSL2+");

        HWSwitch hwSwitch = getBuilder(HWSwitchBuilder.class)
                .withType(switchType)
                .build();
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class).withHwSwitch(hwSwitch))
                .withPhysikTypBuilder(ptBuilder).withHvtStandortBuilder(hvtStandortBuilder);

        RangierungBuilder rangAddBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class).withHwEQN(EWSD_EQN))
                .withPhysikTypBuilder(ptBuilder).withHvtStandortBuilder(hvtStandortBuilder);

        // AuftragTechnik + Endstelle
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).withHvtStandortBuilder(hvtStandortBuilder)
                .withRangierungBuilder(rangierungBuilder).withRangierungAdditionalBuilder(rangAddBuilder);
        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder).build();

        cpsTx = getBuilder(CPSTransactionBuilder.class).withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withOrderNoOrig(TAIFUN_ORDER__NO).withEstimatedExecTime(new Date())
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB).build();

        buildDNs();
        buildDNServices();

        createBillingMocks();

        return hwSwitch;
    }

    private void buildVoIPData(Long dnNoOrig) throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference sipDomain = referenceService.findReference(SIPDomainReference.PRIVATE.getRefId());
        List<EndgeraetPort> egPorts = new ArrayList<>(2);
        Collections
                .addAll(egPorts,
                        getBuilder(EndgeraetPortBuilder.class).withNumber(Integer.MAX_VALUE - 1).withName("testport 1")
                                .build(), getBuilder(EndgeraetPortBuilder.class).withNumber(Integer.MAX_VALUE)
                                .withName("testport 2").build()
                );
        auftragVoIPDN = getBuilder(AuftragVoIPDNBuilder.class).withAuftragBuilder(auftragBuilder)
                .withSipDomain(sipDomain)
                .withDnNoOrig(dnNoOrig)
                .withRufnummernplaene(Collections.singletonList(new VoipDnPlanBuilder()
                                .withSipHauptrufnummer("+49891890980")
                                .withSipLogin("+49891890980@business.m-call.de")
                                .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", null, true)))
                                .build()
                ))
                .build();
        auftragVoIPdn2EgPorts = new ArrayList<>();
        auftragVoIPdn2EgPorts.add(getBuilder(AuftragVoIPDN2EGPortBuilder.class).withAuftragVoipDnId(auftragVoIPDN.getId()).withEgPort(egPorts.get(0))
                .withValidFrom(new Date()).withValidTo(DateTools.getHurricanEndDate()).build());
        auftragVoIPdn2EgPorts.add(getBuilder(AuftragVoIPDN2EGPortBuilder.class).withAuftragVoipDnId(auftragVoIPDN.getId()).withEgPort(egPorts.get(1))
                .withValidFrom(new Date()).withValidTo(DateTools.getHurricanEndDate()).build());
    }

    /* Generiert die DN-Services */
    private void buildDNServices() {
        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class);

        Leistungsbuendel2ProduktBuilder l2pBuilder = getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder).withRandomLeistungNoOrig();
        Leistungsbuendel2Produkt l2p = l2pBuilder.build();
        dnExtLeistungNo = l2p.getLeistungNoOrig();

        // aktive Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(
                        getBuilder(Leistung4DnBuilder.class).withProvisioningName(DN_SERVICE_PROV_NAME_ACTIVE))
                .withLeistungParameter(DN_SERVICE_VALUE_ACTIVE)
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -1)).build();
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(
                        getBuilder(Leistung4DnBuilder.class).withProvisioningName("CS_OUT_WHITE"))
                .withLeistungParameter(DN_SERVICE_VALUE_ACTIVE)
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -1)).build();

        // beendete Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class).withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(getBuilder(Leistung4DnBuilder.class).withProvisioningName("CPS2"))
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -10))
                .withScvKuendigung(DateTools.changeDate(new Date(), Calendar.DATE, -1)).build();

        // zukuenftige (noch nicht aktive) Rufnummernleistung
        getBuilder(Leistung2DNBuilder.class).withDnNo(aktDN.getDnNo())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withLeistung4DnBuilder(getBuilder(Leistung4DnBuilder.class).withProvisioningName("CPS3"))
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, 20)).build();
    }

    /* Billing-Services mocken */
    private void createBillingMocks() throws FindException {
        rufnummerService = mock(RufnummerService.class);
        when(rufnummerService.findRNs4Auftrag(cpsTx.getOrderNoOrig(), cpsTx.getEstimatedExecTime())).thenReturn(
                dns4Order);
        when(rufnummerService.findLastRN(aktDN.getDnNoOrig())).thenReturn(aktDN);

        Leistung phoneService = getBuilder(LeistungBuilder.class).withLeistungNoOrig(dnExtLeistungNo).build();

        leistungService = mock(LeistungService.class);
        when(leistungService.findProductLeistung4Auftrag(cpsTx.getAuftragId(), ProduktMapping.MAPPING_PART_TYPE_PHONE))
                .thenReturn(phoneService);
    }

    /* Generiert die Rufnummern */
    private void buildDNs() throws FindException {
        dns4Order = new ArrayList<>();
        aktDN = getBuilder(RufnummerBuilder.class).withRandomDnNo().withRandomDnNoOrig()
                .withAuftragNoOrig(TAIFUN_ORDER__NO).withOnKz(ONKZ).withDnBase(ACT_DN_BASE).withMainNumber(true)
                .withHistLast(Boolean.TRUE).withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withHistCnt(0).withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate()).withRangeFrom("7").withRangeTo("104").build();
        dns4Order.add(aktDN);
        buildVoIPData(aktDN.getDnNoOrig());
    }

    @Test
    public void testExecuteWithSoftSwitchProducingOldCallScreening() throws Exception {
        HWSwitch hwSwitch = prepareTest(HWSwitchType.EWSD);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetVoIPDataCommand command = (CPSGetVoIPDataCommand) this
                .getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetVoIPDataCommand");
        command.setRufnummerService(rufnummerService);
        command.setLeistungService(leistungService);

        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");

        // analyse result
        CPSVoIPData voipData = cpsServiceOrderData.getVoip();
        assertNotNull(voipData.getSipAccounts(), "No SIP accounts found!");
        assertNotEmpty(voipData.getSipAccounts(), "No SIP accounts found!");

        List<CPSVoIPSIPAccountData> sipAccounts = voipData.getSipAccounts();
        assertEquals(sipAccounts.size(), EXPECTED_SIP_ACCOUNT_COUNT);

        CPSVoIPSIPAccountData sipAccount = sipAccounts.get(0);
        assertEquals(sipAccount.getLac(), ONKZ, "ONKZ not as expected");
        assertEquals(sipAccount.getDn(), ACT_DN_BASE, "DN-Base not as expected");
        assertEquals(sipAccount.getDirectDial(), null, "DirectDial not as expected");
        assertEquals(sipAccount.getPassword(), auftragVoIPDN.getSipPassword(), "VoIP password not as expected");
        assertEquals(sipAccount.getCountryCode(), "+49", "country code not as expected");
        assertEquals(sipAccount.getSwitchKennung(), hwSwitch.getName());

        VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(new Date());

        Assert.assertNotNull(plan, String.format("Missing voipDn plan for voipDn: %s", auftragVoIPDN.getDnNoOrig()));
        assertEquals(sipAccount.getMainNumber(), plan.getSipHauptrufnummer());
        assertEquals(sipAccount.getSipLogin(), plan.getSipLogin());

        assertTrue(checkPorts(sipAccount.getIadPorts()));

        checkBlockData(sipAccount);

        List<CPSDNServiceData> dnServices = sipAccount.getDnServices();
        assertNotEmpty(dnServices, "No DN-Services found!");
        assertEquals(dnServices.size(), 2, "count of DN-Services is unexpected!");
        verifyServiceContainedInList(dnServices, DN_SERVICE_PROV_NAME_ACTIVE, DN_SERVICE_VALUE_ACTIVE_RESULT_OLD);
        verifyServiceContainedInList(dnServices, "CS_OUT_WHITE", DN_SERVICE_VALUE_ACTIVE_RESULT_OLD);
        assertNull(sipAccount.getCallScreenings());
    }

    private void verifyServiceContainedInList(List<CPSDNServiceData> dnServices, String serviceName, String serviceValue) {
        boolean found = false;
        for (CPSDNServiceData dnService : dnServices) {
            if (dnService.getServiceName().equals(serviceName) && dnService.getServiceValue().equals(serviceValue)) {
                found = true;
                break;
            }
        }
        assertTrue(found, String.format("No service with name '%s' and value '%s' could be found.", serviceName, serviceValue));
    }

    @Test
    public void testExecuteWithImsProducingNewCallScreening() throws Exception {
        HWSwitch hwSwitch = prepareTest(HWSwitchType.IMS);

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetVoIPDataCommand command = (CPSGetVoIPDataCommand) this
                .getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetVoIPDataCommand");
        command.setRufnummerService(rufnummerService);
        command.setLeistungService(leistungService);

        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(CPSGetDNPortationGoingDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");

        // analyse result
        CPSVoIPData voipData = cpsServiceOrderData.getVoip();
        assertNotNull(voipData.getSipAccounts(), "No SIP accounts found!");
        assertNotEmpty(voipData.getSipAccounts(), "No SIP accounts found!");

        List<CPSVoIPSIPAccountData> sipAccounts = voipData.getSipAccounts();
        assertEquals(sipAccounts.size(), EXPECTED_SIP_ACCOUNT_COUNT);

        CPSVoIPSIPAccountData sipAccount = sipAccounts.get(0);
        assertEquals(sipAccount.getLac(), ONKZ, "ONKZ not as expected");
        assertEquals(sipAccount.getDn(), ACT_DN_BASE, "DN-Base not as expected");
        assertEquals(sipAccount.getDirectDial(), null, "DirectDial not as expected");
        assertEquals(sipAccount.getPassword(), auftragVoIPDN.getSipPassword(), "VoIP password not as expected");
        assertEquals(sipAccount.getCountryCode(), "+49", "country code not as expected");
        assertEquals(sipAccount.getSwitchKennung(), hwSwitch.getName());

        VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(new Date());

        Assert.assertNotNull(plan, String.format("Missing voipDn plan for voipDn: %s", auftragVoIPDN.getDnNoOrig()));
        assertEquals(sipAccount.getMainNumber(), plan.getSipHauptrufnummer());
        assertEquals(sipAccount.getSipLogin(), plan.getSipLogin());

        assertTrue(checkPorts(sipAccount.getIadPorts()));

        checkBlockData(sipAccount);

        List<CPSDNServiceData> dnServices = sipAccount.getDnServices();
        assertNotEmpty(dnServices, "No DN-Services found!");
        assertEquals(dnServices.size(), 1, "count of DN-Services is unexpected!");
        assertEquals(dnServices.get(0).getServiceName(), DN_SERVICE_PROV_NAME_ACTIVE);
        assertEquals(dnServices.get(0).getServiceValue(), DN_SERVICE_VALUE_ACTIVE_RESULT_OLD);
        assertEquals(sipAccount.getCallScreenings().size(), 4);
        CpsCallScreening cs0 = new CpsCallScreening(CpsCallScreening.Type.OUTGOING_WHITELIST, new BigInteger("49"));
        CpsCallScreening cs1 = new CpsCallScreening(CpsCallScreening.Type.OUTGOING_WHITELIST, new BigInteger("49"),"821");
        CpsCallScreening cs2 = new CpsCallScreening(CpsCallScreening.Type.OUTGOING_WHITELIST, new BigInteger("49"),"821",
                new BigInteger("45000"));
        CpsCallScreening cs3 = new CpsCallScreening(CpsCallScreening.Type.OUTGOING_WHITELIST, new BigInteger("39"),"055");
        assertEquals(sipAccount.getCallScreenings().get(0), cs0);
        assertEquals(sipAccount.getCallScreenings().get(1), cs1);
        assertEquals(sipAccount.getCallScreenings().get(2), cs2);
        assertEquals(sipAccount.getCallScreenings().get(3), cs3);

        // XStream XML Serialisierung pruefen
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        String xml = xstream.toXML(sipAccount).replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
        // XStream gibt "__" statt "_" hier im Test aus. CPS XML funktioniert ;)
        assertThat(xml, containsString("</SERVICES><CALL__SCREENING><ITEM><COUNTRYCODE>"));
    }

    private boolean checkPorts(CPSVoIPIADPortsData found) {
        for (final AuftragVoIPDN2EGPort port : auftragVoIPdn2EgPorts) {
            if (!found.getPorts().contains(port.getEgPort().getNumber())) {
                return false;
            }
        }
        return true;
    }

    private void checkBlockData(CPSVoIPSIPAccountData sipAccount) {
        for (Rufnummer rufnr : dns4Order) {
            checkLeadingNulls(rufnr, sipAccount);
        }
    }

    private void checkLeadingNulls(Rufnummer rufnr, CPSVoIPSIPAccountData sipAccount) {
        int diff = rufnr.getRangeFrom().length() - rufnr.getRangeTo().length();
        StringBuilder leadingZeros = new StringBuilder();
        for (int i = 0; i < Math.abs(diff); i++) {
            leadingZeros.append("0");
        }
        if (diff < 0) {
            assertTrue(sipAccount.getBlockStart().startsWith(leadingZeros.toString()));
        }
        else if (diff > 0) {
            assertTrue(sipAccount.getBlockEnd().startsWith(leadingZeros.toString()));
        }
    }
}
