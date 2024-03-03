/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service;

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * This service is responsible for updating the status of a {@link de.mnet.wbci.model.WbciGeschaeftsfall}. It verifies
 * that the new status is a legal status, according current status and updates the status accordingly.
 */
public interface WbciGeschaeftsfallStatusUpdateService extends WbciService {
    /**
     * Locates the Geschaeftsfall matching the supplied {@code geschaeftsfallId} and updates the status according to the
     * supplied {@code newStatus}.
     *
     * @param geschaeftsfallId primary key of geschaeftsfall
     * @param newStatus        the new gf status
     */
    void updateGeschaeftsfallStatus(Long geschaeftsfallId, WbciGeschaeftsfallStatus newStatus);

    /**
     * Setzt den Status des WBCI GF auf den angegebenen Wert - ohne Ueberpruefung, ob der Uebergang
     * gueltig ist!
     * Hier wird der WBCI Geschaeftsfall allerdings NICHT gespeichert!!!
     * Die Speicherung des {@link WbciGeschaeftsfall}s muss vom Aufrufer selbst erfolgen!
     *
     * @param wbciGeschaeftsfall
     * @param newStatus
     */
    void updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(WbciGeschaeftsfall wbciGeschaeftsfall, WbciGeschaeftsfallStatus newStatus);

    /**
     * Used for looking up the correct {@link WbciGeschaeftsfallStatus} based on the incoming or outgoing {@link
     * de.mnet.wbci.model.WbciMessage} and the new {@link WbciRequestStatus} set as a result of the message. <br /> The
     * geschaeftsfall status returned here is intended as a suggestion.
     *
     * @param newRequestStatus the new request status, correlating to the WbciMessage
     * @param vaRequestStatus the old VA request status, used when TV or STR status is set
     * @param wbciMessage the incoming or outgoing message. The {@code wbciMessage}'s {@code WbciGeschaeftsfall} must be set.
     * @return the correct geschaeftsfall status to set
     */
    WbciGeschaeftsfallStatus lookupStatusBasedOnRequestStatusChange(WbciRequestStatus newRequestStatus,
            WbciRequestStatus vaRequestStatus, WbciMessage wbciMessage);
}
