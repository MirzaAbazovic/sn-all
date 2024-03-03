/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2016
 */
package de.mnet.hurrican.webservice.ngn;

import static com.google.common.collect.Lists.*;
import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.DNTNBBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.ngn.portierungservice.BillingOrderNumberWeb;
import de.mnet.hurrican.webservice.ngn.model.PortierungRequest;
import de.mnet.hurrican.webservice.ngn.model.PortierungResponse;
import de.mnet.hurrican.webservice.ngn.model.PortierungStatus;
import de.mnet.hurrican.webservice.ngn.model.PortierungStatusEnum;
import de.mnet.hurrican.webservice.ngn.service.PortierungService;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungHelperService;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungServiceImpl;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungskennungMigrationService;
import de.mnet.hurrican.webservice.ngn.service.impl.dn.DNLeistungMigration;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;

/**
 * Service test fuer {@link PortierungService} <br/> Created by zolleiswo on 24.02.2016.
 * <p/>
 */
@SuppressWarnings("Duplicates")
@Test(groups = BaseTest.SERVICE)
public class PortierungMigrationHasErrorStatusServiceTest extends AbstractPortierungServiceTest {

    @Mock
    protected WbciCommonService wbciCommonService;

    @Mock
    protected WbciWitaServiceFacade wbciWitaServiceFacade;

    @Mock
    protected FeatureService featureService;

    @Mock
    protected RufnummerService rufnummerService;

    protected PortierungService portierungService;


    @BeforeMethod
    public void initTest() throws FindException {
        MockitoAnnotations.initMocks(this);

        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        PortierungHelperService portierungHelperService = new PortierungHelperService(auftragService, wbciCommonService, wbciWitaServiceFacade, rufnummerService);
        PortierungskennungMigrationService migrationService = (PortierungskennungMigrationService) getBean("portierungskennungMigrationService");
        final DNLeistungMigration dnLeistungMigration = Mockito.mock(DNLeistungMigration.class);
        portierungService = new PortierungServiceImpl(featureService, portierungHelperService, migrationService, dnLeistungMigration);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERING_WEB_SERVICE);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED);

        Rufnummer rn1 = new Rufnummer();
        Rufnummer rn2 = new Rufnummer();
        Rufnummer rn3 = new Rufnummer();
        rn1.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rn2.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rn3.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rn1.setRealDate(new Date());

        rn1.setActCarrierDnTnb(new DNTNBBuilder().withPortKennung("TEST1").withTnb(TNB.MNET_MOBIL.carrierName).setPersist(false).build());
        rn2.setActCarrierDnTnb(new DNTNBBuilder().withPortKennung("TEST2").withTnb(TNB.MNET.carrierName).setPersist(false).build());
        rn3.setLastCarrierDnTnb(new DNTNBBuilder().withPortKennung("TEST3").withTnb(TNB.NEFKOM.carrierName).setPersist(false).build());
        doReturn(newArrayList(rn1, rn2, rn3)).when(rufnummerService).findRNs4Auftrag(anyLong());
    }

    public void thatOrderMigrationStatusIsError() throws FindException, PortierungskennungMigrationException {
        prepareAuftrag(wbciCommonService, wbciWitaServiceFacade,
                Collections.emptyList(), Collections.emptyList());
        PortierungResponse response = executeAuftragMigration();
        Set<Long> orderNumbers = response.getResponses().keySet();
        assertThat(orderNumbers.size()).isEqualTo(1);
        for (Long orderNumber : orderNumbers) {
            dumpEntry(getAuftragNoOrig(), response.getResponses().get(orderNumber));
        }

        for (PortierungStatus portierungStatus : response.getResponses().values()) {
            assertThat(portierungStatus.getPortierungStatusEnum()).isEqualTo(PortierungStatusEnum.ERROR);
        }
    }

    private PortierungResponse executeAuftragMigration() throws PortierungskennungMigrationException {
        Long auftragNo = taifunData.getBillingAuftrag().getAuftragNo();
        BillingOrderNumberWeb billingOrderNumber = new BillingOrderNumberWeb();
        billingOrderNumber.setBillingOrderNumber(auftragNo);
        List<Long> billingOrderNumbers = Collections.singletonList(auftragNo);
        PortierungRequest portierungRequest = new PortierungRequest(billingOrderNumbers);
        return portierungService.migratePortierungskennung(portierungRequest);
    }
}

