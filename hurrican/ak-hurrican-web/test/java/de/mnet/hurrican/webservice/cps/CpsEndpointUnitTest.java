/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.07.2012 17:27:26
 */
package de.mnet.hurrican.webservice.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.mnet.hurrican.cps.OrderQuery;
import de.mnet.hurrican.cps.ProvisionRequest;
import de.mnet.hurrican.cps.ProvisionResponse;
import de.mnet.hurrican.cps.SubscriberType;

/**
 * Unit-Tests fuer SoDataEndpoint. Hier soll die gesammte Logic von SoDataEndpoint geprueft werden. Der E2E-Test
 * GetSoDataTest prueft nur noch die Serialisierung.
 *
 *
 */
@Test(groups = BaseTest.UNIT)
public class CpsEndpointUnitTest extends BaseTest {
    @Spy
    @InjectMocks
    CpsEndpoint sut;

    @Mock
    CCAuftragStatusService auftragService;

    @Mock
    CPSService cpsService;

    @Mock
    XStreamMarshaller xStreamMarshaller;

    public CpsEndpointUnitTest() {
    }

    @BeforeMethod
    public void initTest() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] provisionProvider() {
        return new Object[][] {
                { SubscriberType.INIT,  null },
                { SubscriberType.CREATE, Boolean.FALSE },
                { SubscriberType.MODIFY, Boolean.TRUE},
                { SubscriberType.DELETE, null },
        };
    }

    @Test(dataProvider = "provisionProvider")
    public void provisionByBillingOrderNo(SubscriberType type, Boolean isAsyn) throws Exception {
        final Random random = new Random();
        final long billingOrderNo = random.nextLong();
        final long hurricanOrderNo = random.nextLong();

        when(cpsService.getAuftragIdByAuftragNoOrig(eq(billingOrderNo), any(LocalDate.class),
                eq(type == SubscriberType.DELETE))).thenReturn(hurricanOrderNo);
        CPSTransaction tx = new CPSTransactionBuilder().withRandomId().setPersist(false).build();
        AKWarnings warnings = new AKWarnings();
        CPSTransactionResult result = new CPSTransactionResult(ImmutableList.of(tx), warnings);
        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(result);

        ProvisionRequest request = new ProvisionRequest();
        request.setSubscribe(type);
        request.setAsynch(isAsyn);
        OrderQuery query = new OrderQuery();
        query.setBillingOrderNo(billingOrderNo);
        request.getQuery().add(query);

        ProvisionResponse response = sut.provision(request);

        assertThat(response, notNullValue());
        assertThat(response.getResult(), notNullValue());
        assertThat(response.getResult(), hasSize(1));
        assertThat(response.getResult().get(0).getCpsTxId(), equalTo(tx.getId()));
        verify(cpsService, times(1)).getAuftragIdByAuftragNoOrig(eq(billingOrderNo), any(LocalDate.class),
                eq(type == SubscriberType.DELETE));
        verify(cpsService, times(1)).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        verify(cpsService, times(1)).sendCPSTx2CPS(eq(tx), anyLong(), eq((isAsyn == null)? true: !isAsyn));
    }
}


