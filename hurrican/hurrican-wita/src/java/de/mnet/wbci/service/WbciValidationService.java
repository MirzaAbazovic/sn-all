/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.mnet.wbci.service;

import java.util.*;
import javax.validation.*;

import de.augustakom.common.tools.lang.Pair;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public interface WbciValidationService extends WbciService {

    /**
     * Checks the {@link WbciMessage} object for ERROR violation constrains.
     *
     * @param wbciCdmVersion based {@link WbciCdmVersion} for the violation checks
     * @param entity     which should be checked
     * @return a Set of {@link ConstraintViolation}s
     */
    <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForErrors(WbciCdmVersion wbciCdmVersion, T entity);

    /**
     * Checks the {@link de.mnet.wbci.model.WbciMessage} object for ERROR violation constrains in consideration of the
     * partner carrier}.
     *
     * @param partnerCarrier {@link de.mnet.wbci.model.CarrierCode} of the M-Net partner EKP, which will be used to
     *                       determine the matching {@link WbciCdmVersion}.
     * @param entity         which should be checked
     * @return a Set of {@link javax.validation.ConstraintViolation}s
     */
    <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForErrors(CarrierCode partnerCarrier, T entity);

    /**
     * Checks the {@link WbciMessage} object for WARNING violation constrains.
     *
     * @param wbciCdmVersion based {@link WbciCdmVersion} for the violation checks
     * @param entity     which should be checked
     * @return returning a list of constraint violation errors, if any found. If not, returns null.
     */
    <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForWarnings(WbciCdmVersion wbciCdmVersion, T entity);


    /**
     * Checks the {@link de.mnet.wbci.model.WbciMessage} object for WARNING violation constrains in consideration of the
     * partner carrier.
     *
     * @param partnerCarrier {@link de.mnet.wbci.model.CarrierCode} of the M-Net partner EKP, which will be used to
     *                       determine the matching {@link WbciCdmVersion}.
     * @param entity         which should be checked
     * @return a Set of {@link javax.validation.ConstraintViolation}s
     */
    <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForWarnings(CarrierCode partnerCarrier, T entity);


    /**
     * When creating a new Vorabstimmung, which is linked to a cancelled Vorabstimmung, a check is done to ensure that
     * the new Vorabstimmung uses the same AbgebenderEKP as the linked Vorabstimmung. If this is not the case a {@link
     * de.mnet.wbci.exception.WbciValidationException} is thrown.
     *
     * @param abgebenderEKP
     * @param linkedVorabstimmungsId
     */
    void assertAbgebenderEKPMatchesLinkedAbgebenderEKP(CarrierCode abgebenderEKP, String linkedVorabstimmungsId);

    /**
     * Asserts that all automation tasks have been completed successfully. If any faulty automation tasks marked as
     * 'automatable' exist then a {@link de.mnet.wbci.exception.WbciValidationException} is thrown.
     *
     * @param vorabstimmungsId the vorabstimmungsId of the linked (or cancelled) VA
     */
    void assertLinkedVaHasNoFaultyAutomationTasks(String vorabstimmungsId);

    /**
     * Asserts that the Wechseltermin of the {@link ErledigtmeldungTerminverschiebung} matches to the requested date of
     * the prior {@link de.mnet.wbci.model.TerminverschiebungsAnfrage}.
     *
     * @param erlmTv the Erledigtmeldung matching to a prior TV
     */
    void assertErlmTvTermin(ErledigtmeldungTerminverschiebung erlmTv);

    /**
     * Returns true if a VA request with the same vorabstimmungsId has already been received.
     *
     * @param vorabstimmungsAnfrage the va request to check
     */
    boolean isDuplicateVaRequest(VorabstimmungsAnfrage<?> vorabstimmungsAnfrage);

    /**
     * Returns true if a tv request with the same change id has already been received.
     *
     * @param tv the tv request to check
     */
    boolean isDuplicateTvRequest(TerminverschiebungsAnfrage<?> tv);

    /**
     * Checks if a RUEM-VA has already been sent for the geschaeftsfall. If not then it's too early for a TV to be
     * received.
     *
     * @return true if TV cannot be processed at this time, otherwise false
     */
    boolean isTooEarlyForTvRequest(TerminverschiebungsAnfrage<?> tv);

    /**
     * Prueft, ob die Frist fuer die Terminverschiebung eingehalten wurde. <br/>
     * Dies ist dann der Fall, wenn der abgestimmte Wechseltermin mindestens X Tage in der Zukunft liegt (wobei die
     * Anzahl der Tage ueber eine Datenbank-Konfiguration definiert ist.
     * @param tv eingehende {@link TerminverschiebungsAnfrage}
     * @return true, wenn die TV-Frist eingehalten wurde; sonst false
     */
    boolean isTvFristUnterschritten(TerminverschiebungsAnfrage<?> tv);

    /**
     * Requests (like TV) can only be sent by the receiving carrier.
     *
     * @return true if M-Net is the receiving carrier, otherwise false.
     */
    boolean isMNetReceivingCarrier(WbciRequest<?> request);

    /**
     * According to the technical WBCI specification (page 14), only the receiving carrier may send a {@link
     * de.mnet.wbci.model.StornoAnfrage} before a {@link de.mnet.wbci.model.RueckmeldungVorabstimmung} has been sent.
     *
     * @param stornoAnfrage
     * @return
     */
    boolean isTooEarlyForReceivingStornoRequest(StornoAnfrage stornoAnfrage);

    /**
     * Returns true if a storno request with the same change id has already been received.
     *
     * @param storno the storno request to check
     */
    boolean isDuplicateStornoRequest(StornoAnfrage storno);

    /**
     * Prueft, ob die Frist fuer die Storno-Anfrage (nur STR-AUF!) eingehalten wurde. <br/>
     * Dies ist dann der Fall, wenn der abgestimmte Wechseltermin mindestens X Tage in der Zukunft liegt (wobei die
     * Anzahl der Tage ueber eine Datenbank-Konfiguration definiert ist.)
     * @param storno eingehende {@link StornoAnfrage}
     * @return true, wenn die Storno-Frist eingehalten wurde; sonst false
     */
    boolean isStornoFristUnterschritten(StornoAnfrage storno);

    /**
     * Asserts that the geschaeftsfall has no active storno or tv requests. Before creating or processing a new
     * change request its important to ensure that no other change requests are active.
     * If an active change request is detected a {@link WbciServiceException} is thrown.
     * @param vorabstimmungsId the geschaeftsfall vorabstimmungs id
     */
    void assertGeschaeftsfallHasNoActiveChangeRequests(String vorabstimmungsId);

    /**
     * Asserts that the geschaeftsfall has no STR-ERLM message attached.
     * If an STR-ERLM message is detected, a {@link WbciServiceException} is thrown.
     * @param vorabstimmungsId
     */
    void assertGeschaeftsfallHasNoStornoErlm(String vorabstimmungsId);

    /**
     * Checks if the list of constraint violation errors has an error matching the supplied validationConstraint.
     * @param violations
     * @param constraintViolation
     * @param <T>
     * @return true if an error was found in the list matching the validationConstraint
     */
    <T extends WbciMessage> boolean hasConstraintViolation(Set<ConstraintViolation<T>> violations, Class constraintViolation);

    /**
     * Iterates through the supplied list of constraint violation errors, searching for an error matching the supplied
     * validationConstraint.
     * @param violations
     * @param constraintViolation
     * @param <T>
     * @return the matching
     */
    <T extends WbciMessage> ConstraintViolation<T> getConstraintViolation(Set<ConstraintViolation<T>> violations, Class constraintViolation);

    /**
     * Returns the list of active change requests (STORNOs and TVs) for the geschaeftsfall, excluding the change request
     * matching the supplied changeIdToExclude.
     *
     * @param vorabstimmungsId the geschaeftsfall's vaId
     * @param changeIdToExclude the changeIdToExclude or null if all change requests should be returned
     * @return the list of active change requests or empty
     */
    Set<Pair<RequestTyp, WbciRequestStatus>> getActiveChangeRequests(String vorabstimmungsId, String changeIdToExclude);

    /**
     * Checks if for the assigned {@link WbciGeschaeftsfall#vorabstimmungsId} has any active {@link
     * TerminverschiebungsAnfrage} of {@link StornoAnfrage}.
     *
     * @param vorabstimmungsId {@link WbciGeschaeftsfall#vorabstimmungsId}
     * @return return true if an active TV or Strono have been found
     */
    boolean isTvOrStornoActive(String vorabstimmungsId);
}
