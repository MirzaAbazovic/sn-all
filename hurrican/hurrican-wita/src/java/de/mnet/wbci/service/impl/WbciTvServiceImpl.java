/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.Abbruchmeldung.*;
import static de.mnet.wbci.model.MeldungsCode.*;
import static org.apache.commons.collections.CollectionUtils.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.ProcessingError;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.constraints.CheckTvTerminNotBroughtForward;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 * @param <GF>
 */
@CcTxRequired
public class WbciTvServiceImpl<GF extends WbciGeschaeftsfall> implements WbciTvService<GF> {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WbciTvServiceImpl.class);

    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciDeadlineService wbciDeadlineService;
    @Autowired
    private WbciMeldungService wbciMeldungService;
    @Autowired
    private WbciSchedulerService wbciSchedulerService;
    @Autowired
    private WbciValidationService validationService;
    @Autowired
    private ConstraintViolationHelper constraintViolationHelper;

    /**
     * {@inheritDoc}*
     */
    @Override
    public TerminverschiebungsAnfrage<GF> createWbciTv(@NotNull TerminverschiebungsAnfrage tv,
            @NotNull String vorabstimmungsId) {

        // to secure that the attached wbciGesaeftsfall won't be overwritten with a senseless date.
        if (tv.getTvTermin() == null) {
            throw new WbciServiceException("Der Wechseltermin in einer Terminverschiebung wurde nicht angegeben!");
        }

        final LocalDateTime creationDate = LocalDateTime.now();
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);

        validationService.assertGeschaeftsfallHasNoActiveChangeRequests(vorabstimmungsId);
        validationService.assertGeschaeftsfallHasNoStornoErlm(vorabstimmungsId);

        wbciGeschaeftsfall.setKundenwunschtermin(tv.getTvTermin());

        tv.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        if (tv.getAenderungsId() == null) {
            tv.setAenderungsId(wbciCommonService.getNextPreAgreementId(RequestTyp.TV));
        }

        tv.setVorabstimmungsIdRef(vorabstimmungsId);
        tv.setIoType(IOType.OUT);
        tv.setCreationDate(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
        tv.setUpdatedAt(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
        tv.setRequestStatus(WbciRequestStatus.TV_VORGEHALTEN);

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> errors = validationService.checkWbciMessageForErrors(
                tv.getEKPPartner(), tv);
        if (!isEmpty(errors)) {
            throw new WbciValidationException(constraintViolationHelper.generateErrorMsg(errors));
        }

        wbciDao.store(tv);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("WBCI Terminverschiebung created successfully: %s", tv));
        }

        wbciSchedulerService.scheduleRequest(tv);

        return tv;
    }

    @Override
    public void processIncomingTv(MessageProcessingMetadata metadata, TerminverschiebungsAnfrage tv) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(tv.getVorabstimmungsIdRef());
        tv.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        tv.setRequestStatus(WbciRequestStatus.TV_EMPFANGEN);

        ProcessingError processingError = validateIncomingTv(tv, wbciGeschaeftsfall);
        if (processingError == null) {
            tv.getWbciGeschaeftsfall().setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        } else if (processingError.isTechnical()) {
            // immediately abort message processing with automatic error response
            autoProcessError(tv, processingError);
            metadata.setPostProcessMessage(false);
            return;
        }

        wbciDao.store(tv);
        metadata.setPostProcessMessage(true);

        if (processingError != null) {
            // send out automatic error response for non technical error - error response is stored to io archive
            autoProcessError(tv, processingError);
        } else {
            // set new Kundenwunschtermin to the wbciGeschaeftsfall
            tv.getWbciGeschaeftsfall().setKundenwunschtermin(tv.getTvTermin());
            wbciGeschaeftsfallService.checkCbVorgangAndMarkForClarification(tv.getWbciGeschaeftsfall());
            wbciDeadlineService.updateAnswerDeadline(tv);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("WBCI Terminverschiebung received successfully: %s", tv));
        }
    }

    /**
     * Validates incoming storno request in multiple steps. First checks on high severity errors that cause the storno
     * to be handled with error response automatically. Secondly checks on validation errors which mark for clarification.
     * Returns null if no processing error is found.
     * @param tv
     * @param wbciGeschaeftsfall
     * @return
     */
    private ProcessingError validateIncomingTv(TerminverschiebungsAnfrage tv, WbciGeschaeftsfall wbciGeschaeftsfall) {
        ProcessingError processingError = checkForHighSeverityError(tv);

        if (processingError != null) {
            return processingError;
        }

        Set<ConstraintViolation<TerminverschiebungsAnfrage<GF>>> validationErrors = validateIncommingTv(tv);
        if (CollectionUtils.isNotEmpty(validationErrors)) {
            if (validationService.hasConstraintViolation(validationErrors, CheckTvTerminNotBroughtForward.class)) {
                ConstraintViolation<TerminverschiebungsAnfrage<GF>> cv = validationService.getConstraintViolation(validationErrors, CheckTvTerminNotBroughtForward.class);
                processingError = new ProcessingError(TV_ABG, cv.getMessage(), false);
            } else {
                String errorMsg = constraintViolationHelper.generateErrorMsgForInboundMsg(validationErrors);
                wbciGeschaeftsfallService.markGfForClarification(wbciGeschaeftsfall.getId(), errorMsg, null);
            }
        }

        return processingError;
    }

    /**
     * Before processing the request checks to see if any errors exist which would go against processing the request.
     *
     * @param tv
     * @return null if no error detected, otherwise the error.
     */
    private ProcessingError checkForHighSeverityError(TerminverschiebungsAnfrage<GF> tv) {
        ProcessingError highSeverityError = null;

        if (tv.getWbciGeschaeftsfall() == null) {
            highSeverityError = new ProcessingError(VAID_ABG, BEGRUENDUNG_TV_UNBEKANNTE_VA_REFID);
        }
        else {
            Set<Pair<RequestTyp, WbciRequestStatus>> activeChangeRequests =
                    validationService.getActiveChangeRequests(tv.getVorabstimmungsId(), tv.getAenderungsId());

            if (validationService.isMNetReceivingCarrier(tv)) {
                highSeverityError = new ProcessingError(TV_ABG, BEGRUENDUNG_ABBM_TV_DONATING_CARRIER, false);
            }
            else if (validationService.isTooEarlyForTvRequest(tv)) {
                highSeverityError = new ProcessingError(TV_ABG, BEGRUENDUNG_ABBM_TV_KEINE_RUEMVA, false);
            }
            else if (validationService.isDuplicateTvRequest(tv)) {
                highSeverityError = new ProcessingError(BVID, BEGRUENDUNG_DOPPELTE_TV);
            }
            else if (CollectionUtils.isNotEmpty(activeChangeRequests)) {
                Pair<RequestTyp, WbciRequestStatus> activeChangeRequest = activeChangeRequests.iterator().next();
                WbciRequestStatus reqStatus = activeChangeRequest.getSecond();

                RequestTyp reqTyp = activeChangeRequest.getFirst();
                if(reqStatus.isVorgehalten()) {
                    highSeverityError = new ProcessingError(TV_ABG,
                            String.format(BEGRUENDUNG_AENDERUNG_VORGEHALTEN, reqTyp.getLongName()));
                }
                else {
                    highSeverityError = new ProcessingError(TV_ABG,
                            String.format(BEGRUENDUNG_AENDERUNG_AKTIV, reqTyp.getLongName()));
                }
            }
            else if (validationService.isTvFristUnterschritten(tv)) {
                highSeverityError = new ProcessingError(TV_ABG, BEGRUENDUNG_FRIST_UNTERSCHRITTEN, false);
            }
        }

        if (highSeverityError != null) {
            String logMsg = String.format("Could not process incoming TV with changeId:'%s' for VA-Id:'%s' because: %s",
                    tv.getVorabstimmungsIdRef(),
                    tv.getAenderungsId(),
                    highSeverityError.getErrorMessage());
            LOGGER.info(logMsg);
        }

        return highSeverityError;
    }

    /**
     * Automatically processes the error by creating and sending an ABBM Meldung to the partner EKP. A record of the
     * ABBM will be stored in the IO-Archive but the ABBM will <b>NOT</b> be persisted in the WBCI database.
     *
     * @param tv
     * @param processingError
     */
    private void autoProcessError(@NotNull TerminverschiebungsAnfrage<GF> tv, ProcessingError processingError) {
        String logMsg = String.format("Could not process incoming TV with changeId:'%s' for VA-Id:'%s' because: %s",
                tv.getVorabstimmungsIdRef(),
                tv.getAenderungsId(),
                processingError.getErrorMessage());
        LOGGER.info(logMsg);

        if (VAID_ABG.equals(processingError.getMeldungsCode())) {
            // here a dummy gf has to be added so the ABBM can be generated correctly
            addTempGfForTvWithUnknownVaRefId(tv);
        }

        LocalDate wechselTermin;
        if (tv.getWbciGeschaeftsfall().getWechseltermin() != null) {
            wechselTermin = tv.getWbciGeschaeftsfall().getWechseltermin();
        }
        else {
            wechselTermin = tv.getWbciGeschaeftsfall().getKundenwunschtermin();
        }

        MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        AbbruchmeldungTerminverschiebung abbm = new AbbruchmeldungBuilder()
                .withAenderungsIdRef(tv.getAenderungsId())
                .withWechseltermin(wechselTermin)
                .withAbsender(CarrierCode.MNET)
                .withIoType(IOType.OUT)
                .withWbciGeschaeftsfall(tv.getWbciGeschaeftsfall())
                .addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                        .withMeldungsCode(processingError.getMeldungsCode())
                        .withMeldungsText(processingError.getMeldungsCode().getStandardText())
                        .build())
                .withBegruendung(processingError.getErrorMessage())
                .buildForTv();

        if (processingError.isTechnical()) {
            metadata.setPostProcessMessage(false);
        } else {
            wbciDao.store(abbm);
        }

        wbciMeldungService.sendErrorResponse(metadata, abbm);
    }

    /**
     * Need to add temporary geschaeftsfall on tv anfrage in order to meet automatic validation mechanisms when sending
     * out messages. This geschaeftsfall is neither stored nor included in message data sent out.
     *
     * @param tv
     */
    private void addTempGfForTvWithUnknownVaRefId(TerminverschiebungsAnfrage<GF> tv) {
        WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrnBuilder()
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withAbgebenderEKP(tv.getAbsender())
                .withKundenwunschtermin(tv.getTvTermin())
                .withVorabstimmungsId(tv.getVorabstimmungsIdRef())
                .build();
        tv.setWbciGeschaeftsfall((GF) gf);
    }

    private Set<ConstraintViolation<TerminverschiebungsAnfrage<GF>>> validateIncommingTv(TerminverschiebungsAnfrage<GF> tv) {
        cleanseIncomingTv(tv);

        CarrierCode partnerCarrierCode = tv.getEKPPartner();
        return validationService.checkWbciMessageForErrors(partnerCarrierCode, tv);
    }

    protected void cleanseIncomingTv(TerminverschiebungsAnfrage<GF> tv) {
        // remove endkunde as this is should not be used in the electronic WBCI interface. (refer to FAQ lfd.Nr. 9)
        tv.setEndkunde(null);
    }

    @Override
    public void postProcessIncomingTv(TerminverschiebungsAnfrage<GF> terminverschiebungsAnfrage) {
        // don't delete this method even if it does nothing right now - once the WITA automation is added this method
        // will become useful
    }

}
