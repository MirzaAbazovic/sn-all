/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2014 10:50
 */
package de.mnet.hurrican.webservice.taifun.order.workflow.endpoint;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.mnet.hurrican.webservice.taifun.order.workflow.exceptions.TerminateTechOrdersException;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersType;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeAcknowledgement;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeAcknowledgementDocument;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeDocument;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeFault;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeFaultDocument;

/**
 *  Endpoint fuer Kuendigung von Hurrican-Auftraegen aus Taifun
 */
@CcTxRequired
public class TerminateTechOrdersRequestEndpoint extends AbstractOrderWorkflowEndpoint {

    private static final Logger LOGGER = Logger.getLogger(TerminateTechOrdersRequestEndpoint.class);

    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        final TerminateTechOrdersTypeDocument doc = (TerminateTechOrdersTypeDocument) requestObject;
        final TerminateTechOrdersType terminateTechOrders = doc.getTerminateTechOrdersType();

        final CCAuftragService auftragService = getCCService(CCAuftragService.class);
        final CCAuftragStatusService auftragStatusService = getCCService(CCAuftragStatusService.class);

        try {
            final List<AuftragDaten> auftragDatens =
                    auftragService.findAuftragDaten4OrderNoOrigTx(terminateTechOrders.getOrderNo());
            final List<Long> gekuendigteHurricanAuftrgsNos = Lists.newArrayListWithCapacity(auftragDatens.size());

            final AKWarnings warningsForInfo = new AKWarnings();

            for (final AuftragDaten auftragDaten : auftragDatens) {
                if (auftragDaten.isAuftragActive() && auftragDaten.getStatusId() >= AuftragStatus.IN_BETRIEB) {
                    final AKWarnings warnings =
                            auftragStatusService.kuendigeAuftragUndPhysik(auftragDaten.getAuftragId(),
                                    terminateTechOrders.getTerminationDate().getTime(), getSessionId());
                    gekuendigteHurricanAuftrgsNos.add(auftragDaten.getAuftragId());
                    warningsForInfo.addAKWarnings(warnings);
                }
            }

            final TerminateTechOrdersTypeAcknowledgementDocument ackDoc =
                    TerminateTechOrdersTypeAcknowledgementDocument.Factory.newInstance();
            final TerminateTechOrdersTypeAcknowledgement ack = ackDoc.addNewTerminateTechOrdersTypeAcknowledgement();
            ack.setOrderNo(terminateTechOrders.getOrderNo());
            ack.setHurricanServiceNos(gekuendigteHurricanAuftrgsNos);
            if (warningsForInfo.isNotEmpty()) {
                ack.setInfo(warningsForInfo.getWarningsAsText());
            }

            return ack;
        }
        catch (Exception e) {
            LOGGER.error(String.format("Fehler beim Kündigen des Auftrags mit OrderNo %s", terminateTechOrders.getOrderNo()), e);
            final TerminateTechOrdersTypeFaultDocument ctoTypeFauDoc = TerminateTechOrdersTypeFaultDocument.Factory.newInstance();
            final TerminateTechOrdersTypeFault ctoTypeFault = ctoTypeFauDoc.addNewTerminateTechOrdersTypeFault();
            ctoTypeFault.setOrderNo(terminateTechOrders.getOrderNo());
            ctoTypeFault.setErrorMessage(e.getMessage());
            throw new TerminateTechOrdersException(e.getMessage(), e, ctoTypeFauDoc);
        }
    }
}
