/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.14
 */

package de.mnet.wbci.service;

import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Responsible for setting the deadline and responsible partner for all incoming and outgoing requests and
 * notifications
 */
public interface WbciDeadlineService extends WbciService {
    /**
     * Updates the answer deadline and set the responsibile partner (Mnet other the other partner).
     *
     * @param wbciRequest wbci request which has to be updated
     */
    void updateAnswerDeadline(WbciRequest wbciRequest);

    /**
     * Liefert die Antwortfrist fuer den angegebenen RequestStatus.
     *
     * @param typ the type of request
     * @param wbciRequestStatus request status
     * @return die Antwortfrist
     */
    Antwortfrist findAntwortfrist(RequestTyp typ, WbciRequestStatus wbciRequestStatus);

}
