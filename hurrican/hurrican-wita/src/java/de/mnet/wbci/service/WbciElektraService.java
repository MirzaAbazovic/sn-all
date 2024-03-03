/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 11.06.2014 
 */
package de.mnet.wbci.service;

import javax.annotation.*;

import org.springframework.transaction.annotation.Propagation;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.service.elektra.ElektraCancelOrderResponseDto;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummernportierungVO;

/**
 * WBCI Elektra service for encapsulating all high level Elektra / RQW autoprocessing functionality. These services are
 * typically invoked from the GUI or automatically on sending or receipt of a WBCI message.
 */
public interface WbciElektraService extends WbciService {

    /**
     * Performs automated tasks with Elektra according to RUEMVA information of given VA.
     *
     * @param vorabstimmungsId
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto processRuemVa(String vorabstimmungsId, AKUser user);

    /**
     * Performs automated tasks with Elektra according to RUEMVA information of given VA. The action will be executed
     * within a new transaction.
     *
     * @param vorabstimmungsId
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto processRuemVaNewTx(String vorabstimmungsId, AKUser user);

    /**
     * Performs automated tasks with Elektra according to AKM-TR information of given VA.
     *
     * @param vorabstimmungsId
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto processAkmTr(String vorabstimmungsId, AKUser user);

    /**
     * Performs automated tasks with Elektra according to the received ERLM for a TV.
     *
     * @param vorabstimmungsId
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto processTvErlm(String vorabstimmungsId, AKUser user);

    /**
     * Performs automated tasks with Elektra according to the received RRNP VA.
     *
     * @param vorabstimmungsId
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto processRrnp(String vorabstimmungsId, AKUser user);


    /**
     * Calls Elektra to add a given dial number.
     *
     * @param vorabstimmungsId
     * @param toAdd
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto addDialNumber(String vorabstimmungsId, RufnummernportierungVO toAdd, AKUser user);

    /**
     * Calls Elektra to delete a given dial number.
     *
     * @param vorabstimmungsId
     * @param toDelete
     * @return Info, was angepasst wurde
     */
    ElektraResponseDto deleteDialNumber(String vorabstimmungsId, RufnummernportierungVO toDelete, AKUser user);

    /**
     * Calls Elektra to cancel the given billing order and generates and print the necessary customer documents.
     * @param orderNoOrig
     * @param ruemVa the WBCI {@link RueckmeldungVorabstimmung} object with the necessary cancellation parameters
     * @return
     */
    ElektraCancelOrderResponseDto cancelBillingOrder(Long orderNoOrig, RueckmeldungVorabstimmung ruemVa);

    /**
     * Calls Elektra to undo a planned order cancellation and generate/print the necessary customer documents.
     * calls Elektra to undo cancellation of the given billing order
     */
    ElektraCancelOrderResponseDto undoCancellation(@Nonnull Long billingOrderNo);

    /**
     * Updates the future carrier details in Taifun. This service should be called only after the AKM-TR has been
     * sent/received, since the future carrier details are first communicated within the AKM-TR. <br />
     * Note: This service runs within a new transaction context ({@link Propagation#REQUIRES_NEW})
     *
     * @param vorabstimmungsId the vorabstimmungsId
     * @param user the GUI user performing the action or null if called during WBCI automation
     * @return
     */
    ElektraResponseDto updatePortKennungTnbTx(String vorabstimmungsId, AKUser user);
}
