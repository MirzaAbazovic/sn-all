/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2016
 */
package de.mnet.hurrican.webservice.ngn;

import static de.augustakom.hurrican.model.cc.Feature.FeatureName.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.webservice.ngn.model.ValidationRequest;
import de.mnet.hurrican.webservice.ngn.model.ValidationResponse;
import de.mnet.hurrican.webservice.ngn.model.ValidationStatusEnum;
import de.mnet.hurrican.webservice.ngn.service.PortierungService;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungHelperService;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungServiceImpl;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungskennungMigrationService;
import de.mnet.hurrican.webservice.ngn.service.impl.dn.DNLeistungMigration;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Service test fuer {@link PortierungServiceImpl} <br/> Created by zolleiswo on 24.02.2016.
 * <p/>
 * <p/>
 */
@Test(groups = { BaseTest.SERVICE })
public class PortierungValidationServiceTest extends AbstractPortierungServiceTest {
    private static final Logger LOGGER = Logger.getLogger(PortierungValidationServiceTest.class);

    @Mock
    protected WbciCommonService wbciCommonService;

    @Mock
    protected WbciWitaServiceFacade wbciWitaServiceFacade;

    @Mock
    protected FeatureService featureService;

    protected PortierungService portierungService;

    @SuppressWarnings("Duplicates")
    @BeforeMethod
    public void initTest() {
        MockitoAnnotations.initMocks(this);
        PortierungskennungMigrationService migrationService = (PortierungskennungMigrationService) getBean("portierungskennungMigrationService");
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        RufnummerService rufnummerService = getBillingService(RufnummerService.class);
        PortierungHelperService portierungHelperService = new PortierungHelperService(auftragService, wbciCommonService, wbciWitaServiceFacade, rufnummerService);
        final DNLeistungMigration dnLeistungMigration = Mockito.mock(DNLeistungMigration.class);
        portierungService = new PortierungServiceImpl(featureService, portierungHelperService, migrationService, dnLeistungMigration);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERING_WEB_SERVICE);
        doReturn(true).when(featureService).isFeatureOnline(NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED);
    }

    public void thatValidateMigrationReturnsMigrationPossible()
            throws PortierungskennungMigrationException, FindException {
        prepareAuftrag(wbciCommonService, wbciWitaServiceFacade, Collections.emptyList(), Collections.emptyList());
        ValidationResponse response = validateAuftragMigration();
        long billingOrderNumberFromResponse = response.getOrderNoOrig();
        long auftragNo = taifunData.getBillingAuftrag().getAuftragNo();

        dumpValidation(response);

        Assert.assertEquals(auftragNo, billingOrderNumberFromResponse);
        Assert.assertEquals(response.getStatus().getMsg(),
                ValidationStatusEnum.MIGRATION_POSSIBLE, response.getStatus().getStatusEnum());
    }

    private void dumpValidation(ValidationResponse response) {
        StringBuilder sb = new StringBuilder();
        String EOL = System.getProperty("line.separator");
        sb.append("Auftrag Validation").append(EOL)
                .append("---> Auftrag ").append(response.getOrderNoOrig()).append(EOL)
                .append("---> with status ").append(response.getStatus().getStatusEnum()).append(EOL)
                .append("------> msg = ").append(response.getStatus().getMsg());

        LOGGER.info(sb.toString());
    }

    public void whenWbciGfExistsThenOrderMigrationIsNotPossible() throws PortierungskennungMigrationException {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallRrnp();
        wbciGeschaeftsfall.setId(WBCI_GF_ID);
        prepareAuftrag(wbciCommonService, wbciWitaServiceFacade,
                Collections.singletonList(wbciGeschaeftsfall), Collections.emptyList());

        ValidationResponse response = validateAuftragMigration();
        long billingOrderNumberFromResponse = response.getOrderNoOrig();
        Assert.assertEquals(ValidationStatusEnum.MIGRATION_NOT_POSSIBLE, response.getStatus().getStatusEnum());
        final String msg = String.format("Zu Billing-Auftrag %s wurden noch aktive WBCI-Geschäftsfälle gefunden: %s",
                billingOrderNumberFromResponse, WBCI_GF_ID);
        dumpValidation(response);
        String statusMessage = response.getStatus().getMsg();
        Assert.assertTrue(statusMessage, statusMessage.contains(msg));
    }

    public void whenWitaCbVorgangExistsThenOrderMigrationIsNotPossible() throws PortierungskennungMigrationException {

        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setId(WITA_CB_ID);
        witaCBVorgang.setStatus(CBVorgang.STATUS_SUBMITTED);
        prepareAuftrag(wbciCommonService, wbciWitaServiceFacade, Collections.emptyList(), Collections.singletonList(witaCBVorgang));

        ValidationResponse response = validateAuftragMigration();
        long billingOrderNumberFromResponse = response.getOrderNoOrig();
        dumpValidation(response);

        Assert.assertEquals(ValidationStatusEnum.MIGRATION_NOT_POSSIBLE, response.getStatus().getStatusEnum());
        final String msg = String.format("Zu Billing-Auftrag %s wurden noch aktive WITA-Vorgänge gefunden: %s",
                billingOrderNumberFromResponse, WITA_CB_ID);
        String statusMessage = response.getStatus().getMsg();
        Assert.assertTrue(statusMessage, statusMessage.contains(msg));
    }


    private ValidationResponse validateAuftragMigration() throws PortierungskennungMigrationException {
        long auftragNo = taifunData.getBillingAuftrag().getAuftragNo();
        ValidationRequest validationRequest = new ValidationRequest(auftragNo);
        return portierungService.validatePortierungskennung(validationRequest);
    }


}

