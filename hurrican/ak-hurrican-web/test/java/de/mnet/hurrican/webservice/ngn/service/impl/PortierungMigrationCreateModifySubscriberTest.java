/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;

@Test
public class PortierungMigrationCreateModifySubscriberTest {
    @Mock
    private CPSService cpsService;

    @Mock
    private CCAuftragService auftragService;

    @Mock
    private PortierungHelperService portierungHelperService;

    @Mock
    private WitaGfMigration witaGfMigration;

    @InjectMocks
    private PortierungskennungMigrationService migrationService = new PortierungskennungMigrationService();

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void thatWarningIsCreatedWhenCpsProvisioningIsNotAllowed() throws Exception {
        CPSTransactionResult cpsResult = new CPSTransactionResult();
        doReturn(cpsResult).when(cpsService).createCPSTransaction(any());
        doNothing().when(cpsService).sendCPSTx2CPS(any(), any());
        CPSProvisioningAllowed cpsProvisioningAllowed = new CPSProvisioningAllowed(false, "test");
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().build();
        Optional<String> warnings = migrationService.createAndSendModifySubscriber(auftragDaten, cpsProvisioningAllowed);
        String msg = String.format("Der techn. Auftrag [%d] wurde nicht CPS-provisioniert.", auftragDaten.getAuftragId());
        assertThat(warnings.isPresent());
        //noinspection OptionalGetWithoutIsPresent
        assertThat(warnings.get().equals(msg));
    }

    public void thatWarningIsCreatedWhenOneAuftragIsMigratedAndCpsProvisioningIsNotAllowed() throws Exception {
        CPSTransactionResult cpsResult = new CPSTransactionResult();
        doReturn(cpsResult).when(cpsService).createCPSTransaction(any());
        doNothing().when(cpsService).sendCPSTx2CPS(any(), any());

        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().withAuftragNoOrig(1234567L).build();
        List<AuftragDaten> auftragDatenList = Lists.newArrayList(auftragDaten);
        doReturn(auftragDatenList).when(auftragService).findAuftragDaten4OrderNoOrig(auftragDaten.getAuftragNoOrig());
        doReturn(auftragDatenList.stream()).when(portierungHelperService).filterActiveAuftraegeStream(any());
        doNothing().when(witaGfMigration).performMigrationWitaGf(any());

        CPSProvisioningAllowed cpsProvisioningAllowed = new CPSProvisioningAllowed(false, "test");
        doReturn(cpsProvisioningAllowed).when(cpsService).isCPSProvisioningAllowed(
                anyLong(), any(), anyBoolean(), anyBoolean(), anyBoolean());
        migrationService.executeWitaGfMigration(Arrays.asList(auftragDaten));
        final List<String> warnings = migrationService.executeCpsProvisonierung(Arrays.asList(auftragDaten));
        assertThat(CollectionUtils.isNotEmpty(warnings));
    }
}

