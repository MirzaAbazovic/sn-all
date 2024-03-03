/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.13
 */
package de.mnet.wbci.service;

import java.time.*;

import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * This service is responsible for updating the status of a {@link de.mnet.wbci.model.WbciRequest}. It verifies that the
 * new status is a legal status, according to the request type and current request status.
 */
public interface WbciRequestStatusUpdateService extends WbciService {

    /**
     * Locates the VorabstimmungsAnfrage matching the supplied {@code vorabstimmungsId} and updates the status
     * accordingly.
     *
     * @param vorabstimmungsId
     * @param newStatus
     * @param meldungCodes
     * @param meldungTyp
     * @param meldungDate
     */
    void updateVorabstimmungsAnfrageStatus(String vorabstimmungsId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungTyp, LocalDateTime meldungDate);

    /**
     * Locates the TV Anfrage matching the supplied {@code vorabstimmungsId} and {@code changeId} and updates the status
     * accordingly.
     *
     * @param vorabstimmungsId
     * @param changeId
     * @param newStatus
     * @param meldungCodes
     * @param meldungTyp
     * @param meldungDate
     */
    void updateTerminverschiebungAnfrageStatus(String vorabstimmungsId, String changeId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungTyp, LocalDateTime meldungDate);

    /**
     * Locates the Storno Anfrage matching the supplied {@code vorabstimmungsId} and {@code changeId} and updates the
     * status accordingly.
     *
     * @param vorabstimmungsId
     * @param stornoId
     * @param newStatus
     * @param meldungCodes
     * @param meldungTyp
     * @param meldungDate
     */
    void updateStornoAnfrageStatus(String vorabstimmungsId, String stornoId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungTyp, LocalDateTime meldungDate);
}
