/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2016
 */
package de.mnet.hurrican.webservice.ngn;

import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
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
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;

/**
 * Service test fuer {@link PortierungService} <br/> Created by zolleiswo on 24.02.2016.
 * <p/>
 */
@SuppressWarnings("Duplicates")
@Test(groups = BaseTest.SERVICE)
public class PortierungMigrationHasWarningsServiceTest extends AbstractPortierungServiceTest {

    @Mock
    protected WbciCommonService wbciCommonService;

    @Mock
    protected WbciWitaServiceFacade wbciWitaServiceFacade;

    @Mock
    protected FeatureService featureService;

    protected PortierungService portierungService;


    @BeforeMethod
    public void initTest() {
        MockitoAnnotations.initMocks(this);

        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        RufnummerService rufnummerService = (RufnummerService) getBean("de.augustakom.hurrican.service.billing.RufnummerService");

        PortierungHelperService portierungHelperService = new PortierungHelperService(auftragService, wbciCommonService, wbciWitaServiceFacade, rufnummerService);
        PortierungskennungMigrationService migrationService = (PortierungskennungMigrationService) getBean("portierungskennungMigrationService");
        final DNLeistungMigration dnLeistungMigration = Mockito.mock(DNLeistungMigration.class);
        portierungService = new PortierungServiceImpl(featureService, portierungHelperService, migrationService, dnLeistungMigration);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERING_WEB_SERVICE);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED);
    }

    public void whenWbciGfExistsThenOrderMigrationIsNotPossible() throws PortierungskennungMigrationException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallRrnp();
        wbciGeschaeftsfall.setId(WBCI_GF_ID);
        wbciGeschaeftsfall.setVorabstimmungsId(VORABSTIMMUNGS_ID);

        UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldung();
        akmtr.setPortierungskennungPKIauf(VORABSTIMMUNGS_ID);
        Mockito.when(wbciCommonService.findLastForVaId(anyString())).thenReturn(akmtr);
        prepareAuftrag(wbciCommonService, wbciWitaServiceFacade,
                Collections.singletonList(wbciGeschaeftsfall), Collections.emptyList());

        PortierungResponse response = executeAuftragMigration();
        Long auftragNo = taifunData.getBillingAuftrag().getAuftragNo();
        PortierungStatus entry = response.getResponses().get(auftragNo);
        assertThat(entry).isNotNull();

        dumpEntry(getAuftragNoOrig(), entry);

        assertThat(entry.getPortierungStatusEnum()).isEqualTo(PortierungStatusEnum.SUCCESSFUL);
        assertThat(entry.getPortierungWarnings().isPresent()).isTrue();
        assertThat(entry.getPortierungWarnings().get().getWarnings()).isNotEmpty();
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

