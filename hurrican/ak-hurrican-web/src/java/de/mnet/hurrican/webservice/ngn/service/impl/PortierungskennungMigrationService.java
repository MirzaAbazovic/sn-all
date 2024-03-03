/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl;

import static de.augustakom.common.tools.collections.CollectionTools.*;

import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;

/**
 * Migration von dem Einzelauftrag
 * <p>
 * Es wird in eine separate transaktion ausgefuert um die Migration von dem Einzelauftrag zu commiten
 */
@CcTxRequiresNew
@Service
public class PortierungskennungMigrationService {
    private static final Logger LOGGER = Logger.getLogger(PortierungskennungMigrationService.class);

    private static final String FEHLER_BEI_DER_PROVISIONIERUNG = "Fehler bei der Provisionierung aufgetreten";

    @Autowired
    private CPSService cpsService;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private PortierungHelperService portierungHelperService;
    @Autowired
    private WitaGfMigration witaGfMigration;

    public void executeWitaGfMigration(List<AuftragDaten> activeAurftaege) throws PortierungskennungMigrationException {
            for (AuftragDaten auftragDaten : activeAurftaege) {
                witaGfMigration.performMigrationWitaGf(auftragDaten);
            }
    }

    public List<String> executeCpsProvisonierung(List<AuftragDaten> activeAurftaege) throws PortierungskennungMigrationException {
        final List<String> messages = new LinkedList<>();
        for (AuftragDaten auftragDaten : activeAurftaege) {
            final CPSProvisioningAllowed cpsProvisioningAllowed = findProvisioningAllowed(auftragDaten);
            final Optional<String> cpsWarning = createAndSendModifySubscriber(auftragDaten, cpsProvisioningAllowed);
            if (cpsWarning.isPresent()) {
                messages.add(cpsWarning.get());
            }
        }
        return messages;
    }

    public List<AuftragDaten> findAuftraegeForMigration(Long billingOrderNumber) throws PortierungskennungMigrationException {
        try {
            final List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(billingOrderNumber);
            return auftragDaten4OrderNoOrig != null ? portierungHelperService.filterActiveAuftraegeStream(auftragDaten4OrderNoOrig.stream()).collect(Collectors.toList())
                    : Collections.EMPTY_LIST;
        }
        catch (FindException e) {
            final String msg = String.format("Fehler bei erfassung von BillingAuftrag [%d] aufgetreten  ", billingOrderNumber);
            LOGGER.error(msg, e);
            throw new PortierungskennungMigrationException(msg);
        }
    }

    Optional<String> createAndSendModifySubscriber(AuftragDaten auftragDaten, CPSProvisioningAllowed cpsProvisioningAllowed)
            throws PortierungskennungMigrationException {

        if (!cpsProvisioningAllowed.isProvisioningAllowed()) {
            String msg = String.format("Der techn. Auftrag [%d] wurde nicht CPS-provisioniert.", auftragDaten.getAuftragId());
            LOGGER.warn(msg);
            return Optional.of(msg);
        }

        try {
            executeCpsProvisioning(auftragDaten);
            return Optional.empty();
        }
        catch (Exception e) {
            final String msg = String.format("Fehler bei der Migration von techn. Auftrag [%d]. Provisionierungsfehler.", auftragDaten.getAuftragId());
            LOGGER.error(msg, e);
            throw new PortierungskennungMigrationException(FEHLER_BEI_DER_PROVISIONIERUNG);
        }
    }

    private void executeCpsProvisioning(AuftragDaten auftragDaten) throws PortierungskennungMigrationException, StoreException {
        final Date now = new Date();
        final Long sessionId = HurricanScheduler.getSessionId();
        final CreateCPSTransactionParameter cpsTransactionParameter = new CreateCPSTransactionParameter(
                auftragDaten.getAuftragId(), null,
                CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                now, null, null, null, null, false, false,
                sessionId);

        final CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(cpsTransactionParameter);

        if (hasExpectedSize(cpsTxResult.getCpsTransactions(), 1)) {
            cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId);
        }

        if (cpsTxResult.getWarnings().isNotEmpty()) {
            final String msg = String.format("Fehler bei der Migration von techn. Auftrag [%d]. Provisionierung führt zu folgendem Fehler [%s].",
                    auftragDaten.getAuftragId(), cpsTxResult.getWarnings().getWarningsAsText());
            LOGGER.error(msg);
            throw new PortierungskennungMigrationException(FEHLER_BEI_DER_PROVISIONIERUNG);
        }
    }

    private CPSProvisioningAllowed findProvisioningAllowed(AuftragDaten auftragDaten) throws PortierungskennungMigrationException {
        try {
            return cpsService.isCPSProvisioningAllowed(
                    auftragDaten.getAuftragId(), null, false, false, true);
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Migration von techn. Auftrag [%d]. Fehler bei der Provisionierung.", auftragDaten.getAuftragId()), e);
            throw new PortierungskennungMigrationException(FEHLER_BEI_DER_PROVISIONIERUNG, e);
        }
    }

}
