/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2012 13:58:26
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import javax.xml.transform.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurrican.e2e.common.EkpDataBuilder;
import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.common.ProduktDataBuilder;
import de.mnet.hurrican.e2e.common.StandortDataBuilder;
import de.mnet.hurrican.e2e.common.StandortDataBuilder.StandortData;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.common.Fault;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.VLAN;

@Test(groups = BaseTest.E2E)
public class BaseWholesaleE2ETest extends BaseHurricanE2ETest {

    protected static LocalDate today() {
        return LocalDate.now();
    }

    protected static final LocalDate tomorrow() {
        return today().plusDays(1);
    }

    @Autowired
    protected Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    private Provider<ProduktDataBuilder> produktDataBuilderProvider;
    @Autowired
    protected Provider<StandortDataBuilder> standortDataBuilderProvider;
    @Autowired
    protected Provider<EkpDataBuilder> ekpDataBuilderProvider;
    @Autowired
    protected CCAuftragService auftragService;

    protected StandortData standortData;
    protected EkpData ekpData;

    @Resource(name = "wholesaleWebServiceTemplate")
    protected WebServiceTemplate webServiceTemplate;

    @Autowired
    protected Provider<WholesaleOrderState> stateProvider;

    protected <T extends Fault> T extractSoapFaultDetail(SoapFaultClientException e) {
        SoapFault fault = e.getSoapFault();
        SoapFaultDetail detail = fault.getFaultDetail();
        assertNotNull(detail);
        Iterator<SoapFaultDetailElement> detailEntries = detail.getDetailEntries();
        Source faultDetailSource = detailEntries.next().getSource();
        assertFalse(detailEntries.hasNext());
        @SuppressWarnings("unchecked")
        T faultDetail = (T) jaxb2Marshaller.unmarshal(faultDetailSource);
        return faultDetail;
    }

    @BeforeMethod(groups = BaseTest.E2E)
    protected void initData() throws Exception {
        // @formatter:off
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .getStandortData();

        ekpData = ekpDataBuilderProvider.get().getEkpData();
        // @formatter:on
    }

    protected ProduktDataBuilder.ProduktData createProduktData() throws Exception {
        return produktDataBuilderProvider.get().getProduktData();
    }

    protected void modifyData4Auftrag(String lineId, Date inbetriebnahme, Long prodId) throws Exception {
        Auftrag auftrag = auftragService.findActiveOrderByLineId(lineId, today());
        assertNotNull(auftrag, String.format("Auftrag zu LineID %s nicht gefunden!", lineId));
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId());
        assertNotNull(
                auftragDaten,
                String.format("AuftragDaten zu LineID %s und AuftragID %d nicht gefunden!", lineId,
                        auftrag.getAuftragId())
        );
        if (prodId != null) {
            auftragDaten.setProdId(prodId);
        }

        if (inbetriebnahme != null) {
            auftragDaten.setInbetriebnahme(inbetriebnahme);
        }

        auftragService.saveAuftragDaten(auftragDaten, false);
    }

    protected WholesaleOrderState getNewWholesaleOrderState() {
        return stateProvider.get().standortData(standortData).ekpData(ekpData);
    }

    protected static LocalDate hurricanEndDate() {
        return LocalDate.from(Instant.ofEpochMilli(DateTools.getHurricanEndDate().getTime()).atZone(ZoneId.systemDefault()));
    }

    protected void assertOrderParamatersContainsVlanOfService(WholesaleOrderState state, final LocalDate when,
            final String lineId, final CvlanServiceTyp vlanService, final int count) {
        final String lineIdTmp = state.lineId;
        state.lineId(lineId);
        GetOrderParametersResponse orderParamsAfterChangeOfPort = getOrderParametersResponse(state, when, lineId);
        assertThat(CollectionUtils.countMatches(orderParamsAfterChangeOfPort.getVlans(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                if (object instanceof VLAN) {
                    VLAN vlan = (VLAN) object;
                    return vlan.getService().equals(vlanService.toString());
                }
                return false;
            }

        }), equalTo(count));
        state.lineId(lineIdTmp);
    }

    protected void assertOrderParamatersContainsVlanOfService(WholesaleOrderState state, final LocalDate when,
            final String lineId, final CvlanServiceTyp vlanService) {
        assertOrderParamatersContainsVlanOfService(state, when, lineId, vlanService, 1);
    }

    protected void assertOrderParamatersContainsNotVlanOfService(WholesaleOrderState state, final LocalDate when,
            final String lineId, final CvlanServiceTyp vlanService) {
        assertOrderParamatersContainsVlanOfService(state, when, lineId, vlanService, 0);
    }

    protected GetOrderParametersResponse getOrderParametersResponse(WholesaleOrderState state, final LocalDate when,
            final String lineId) {
        GetOrderParametersResponse orderParamsAfterChangeOfPort = state
                .lineId(lineId)
                .getOrderParameters(when)
                .getOrderParametersResponse;
        return orderParamsAfterChangeOfPort;
    }
}
