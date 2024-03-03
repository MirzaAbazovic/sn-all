/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2014
 */
package de.mnet.wita.service.impl;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;

import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.integration.LineOrderService;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaSendMessageService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 *
 */
@CcTxRequired
public class WitaSendMessageServiceImpl implements WitaSendMessageService {

    private static final Logger LOGGER = Logger.getLogger(WitaSendMessageServiceImpl.class);
    private static final String MODIFY_STANDORT_KOLLOKATION_ERROR = "Die Prüfung der Standort-Kollokationsadresse ist fehlgeschlagen. Grund: %s (MnetWitaRequest Id: %s)";
    private static final String COULD_NOT_LOAD_REQUEST_ERROR = "Could not load already scheduled MnetWitaRequest with id '%s'";

    @Autowired
    private CamelProxyLookupService camelProxyLookupService;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private TalOrderWorkflowService talOrderWorkflowService;
    @Autowired
    private MwfEntityDao mwfEntityDao;

    @Override
    public <T extends WitaMessage> void sendAndProcessMessage(@NotNull T message) {
        LineOrderService lineOrderService = camelProxyLookupService.lookupCamelProxy(PROXY_LINE_ORDER, LineOrderService.class);
        if (!(message instanceof MnetWitaRequest) || canRequestBeSentNow((MnetWitaRequest) message)) {
            LOGGER.info("Sending and processing WITA Message (Request or Notification) to LineOrderService");
            lineOrderService.sendToWita(message);
        }
        else {
            LOGGER.trace("Take the WITA message (Request or Notification) on hold, message will be sent later");
        }
    }

    /**
     * Checks whether the provided request can be sent to WITA(Atlas-ESB) or not. All messages will be send
     * immediately.
     *
     * @param request
     * @return
     */
    private boolean canRequestBeSentNow(@NotNull MnetWitaRequest request) {
        return witaConfigService.isSendAllowed(request);
    }

    @CcTxRequiresNew
    @Override
    public boolean sendScheduledRequest(@NotNull Long unsentWitaRequestId) throws WitaBaseException {
        MnetWitaRequest unsentWitaRequest = mwfEntityDao.findById(unsentWitaRequestId, MnetWitaRequest.class);
        if (unsentWitaRequest == null) {
            throw new WitaBaseException(String.format(COULD_NOT_LOAD_REQUEST_ERROR, unsentWitaRequestId));
        }

        modifyStandortKollokation(unsentWitaRequest);
        if (unsentWitaRequest instanceof Auftrag) {
            try {
                sendAndProcessMessage(unsentWitaRequest);
            }
            catch (ValidationException validationException) {
                // In case of an invalid request, propagate the exception up to the caller
                throw new WitaBaseException("Invalid MnetWitaRequest: " + unsentWitaRequest, validationException);
            }
        }
        else if (unsentWitaRequest instanceof TerminVerschiebung || unsentWitaRequest instanceof Storno) {
            //process TV or Storno's over the talOrderWorkflowService, to ensure that the activity workflow is in the correct state.
            talOrderWorkflowService.sendTvOrStornoRequest(unsentWitaRequest);
        }
        return unsentWitaRequest.getSentAt() != null;
    }

    private void modifyStandortKollokation(MnetWitaRequest unsentRequest) {
        try {
            witaTalOrderService.modifyStandortKollokation(unsentRequest);
        }
        catch (Exception e) {
            LOGGER.warn(String.format(MODIFY_STANDORT_KOLLOKATION_ERROR, e.getMessage(), unsentRequest.getId()));
        }
    }

}
