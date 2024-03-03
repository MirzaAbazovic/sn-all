/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.route.handler;

import java.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.helper.ExchangeHelper;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciMeldungService;

/**
 * Handler, um "alle" Aktionen durchzufuehren, die nach einem erfolgreichen Versand eines WBCI-Requests notwendig sind.
 */
@Component
public class AfterWbciMessageSentHandler {

    @Autowired
    private ExchangeHelper exchangeHelper;

    @Autowired
    private WbciDao wbciDao;

    @Autowired
    private WbciMeldungService wbciMeldungService;

    @Autowired
    private WbciGeschaeftsfallStatusUpdateService gfStatusUpdateService;

    @Autowired
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @Autowired
    private WbciDeadlineService wbciDeadlineService;

    @Autowired
    private WbciCommonService wbciCommonService;

    @Handler
    public void handleSuccessfulWbciRequest(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = exchangeHelper.getOriginalMessageFromOutMessage(exchange);

        if (wbciMessage instanceof WbciRequest) {
            // update the gf status here for outgoing Requests
            WbciRequest request = (WbciRequest) wbciMessage;
            WbciRequestStatus wbciRequestStatus = request.getRequestStatus();

            if (WbciRequestStatus.VA_VORGEHALTEN.equals(wbciRequestStatus)) {
                wbciRequestStatus = WbciRequestStatus.VA_VERSENDET;

                if (isVaRequestLinkedToStrAenGeschaeftsfall(request)) {
                    wbciGeschaeftsfallService.closeLinkedStrAenGeschaeftsfall(request);
                }
            }
            else if (WbciRequestStatus.TV_VORGEHALTEN.equals(wbciRequestStatus)) {
                wbciRequestStatus = WbciRequestStatus.TV_VERSENDET;
            }
            else if (WbciRequestStatus.STORNO_VORGEHALTEN.equals(wbciRequestStatus)) {
                wbciRequestStatus = WbciRequestStatus.STORNO_VERSENDET;
            }
            request.setRequestStatus(wbciRequestStatus);

            WbciRequestStatus vaRequestStatus = wbciRequestStatus;
            if (request instanceof TerminverschiebungsAnfrage || request instanceof StornoAnfrage) {
                vaRequestStatus = wbciCommonService.findVorabstimmungsAnfrage(request.getWbciGeschaeftsfall().getVorabstimmungsId()).getRequestStatus();
            }

            WbciGeschaeftsfallStatus gfStatus = gfStatusUpdateService.lookupStatusBasedOnRequestStatusChange(wbciRequestStatus, vaRequestStatus, request);
            gfStatusUpdateService.updateGeschaeftsfallStatus(request.getWbciGeschaeftsfall().getId(), gfStatus);
            wbciDeadlineService.updateAnswerDeadline(request);
        }
        wbciMessage.setProcessedAt(new Date());

        //update the request and gf status, if the message is from type Meldung.
        if (wbciMessage instanceof Meldung) {
            wbciMeldungService.updateCorrelatingRequestForMeldung((Meldung<?>) wbciMessage);
        }

    }

    /**
     * Checks to see if the VA request was created as a result of another WBCI Geschaeftsfall being cancelled via a
     * STR-AEN request.
     *
     * @param request
     * @return true if the request is linked to geschaeftsfall cancelled with STR-AEN. Otherwise false.
     */
    private boolean isVaRequestLinkedToStrAenGeschaeftsfall(WbciRequest request) {
        return StringUtils.isNotEmpty(request.getWbciGeschaeftsfall().getStrAenVorabstimmungsId());
    }

}
