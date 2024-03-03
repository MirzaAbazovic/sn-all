/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.ProcessingError;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoMitEndkundeStandortAnfrage;
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
import de.mnet.wbci.service.WbciStornoService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 * @param <GF>
 */
@CcTxRequired
public class WbciStornoServiceImpl<GF extends WbciGeschaeftsfall> implements WbciStornoService<GF> {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WbciStornoServiceImpl.class);
    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WbciCommonService wbciCommonService;
    @Autowired
    protected WbciDeadlineService wbciDeadlineService;
    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private WbciSchedulerService wbciSchedulerService;
    @Autowired
    private WbciMeldungService wbciMeldungService;
    @Autowired
    private WbciValidationService validationService;
    @Autowired
    private ConstraintViolationHelper constraintViolationHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public StornoAnfrage<GF> createWbciStorno(@NotNull StornoAnfrage stornoAnfrage, @NotNull String vorabstimmungsId) {
        final LocalDateTime creationDate = LocalDateTime.now();
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);

        validationService.assertGeschaeftsfallHasNoActiveChangeRequests(vorabstimmungsId);
        validationService.assertGeschaeftsfallHasNoStornoErlm(vorabstimmungsId);

        if (StringUtils.isEmpty(stornoAnfrage.getVorabstimmungsIdRef())) {
            stornoAnfrage.setVorabstimmungsIdRef(vorabstimmungsId);
        }

        if (StringUtils.isEmpty(stornoAnfrage.getAenderungsId())) {
            stornoAnfrage.setAenderungsId(wbciCommonService.getNextPreAgreementId(stornoAnfrage.getTyp()));
        }

        stornoAnfrage.setWbciGeschaeftsfall(wbciGeschaeftsfall);

        stornoAnfrage.setCreationDate(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
        stornoAnfrage.setUpdatedAt(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
        stornoAnfrage.setIoType(IOType.OUT);
        stornoAnfrage.setRequestStatus(WbciRequestStatus.STORNO_VORGEHALTEN);

        Set<ConstraintViolation<StornoAnfrage>> errors = validationService.checkWbciMessageForErrors(
                stornoAnfrage.getEKPPartner(), stornoAnfrage);
        if (!isEmpty(errors)) {
            throw new WbciValidationException(constraintViolationHelper.generateErrorMsg(errors));
        }

        wbciDao.store(stornoAnfrage);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("WBCI Storno created successfully: %s", stornoAnfrage));
        }

        wbciSchedulerService.scheduleRequest(stornoAnfrage);

        return stornoAnfrage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processIncomingStorno(@NotNull MessageProcessingMetadata metadata, @NotNull StornoAnfrage stornoAnfrage) {
        final String vorabstimmungsIdRef = stornoAnfrage.getVorabstimmungsIdRef();
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsIdRef);
        stornoAnfrage.setWbciGeschaeftsfall(wbciGeschaeftsfall);
        stornoAnfrage.setRequestStatus(WbciRequestStatus.STORNO_EMPFANGEN);

        ProcessingError processingError = validateIncommingStorno(stornoAnfrage);
        if (processingError == null) {
            stornoAnfrage.getWbciGeschaeftsfall().setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        } else if (processingError.isTechnical()) {
            // immediately abort message processing with automatic error response
            autoProcessError(stornoAnfrage, processingError);
            metadata.setPostProcessMessage(false);
            return;
        }

        wbciDao.store(stornoAnfrage);
        metadata.setPostProcessMessage(true);

        if (processingError != null) {
            // send out automatic error response for non technical error - error response is stored to io archive
            autoProcessError(stornoAnfrage, processingError);
        } else {
            wbciDeadlineService.updateAnswerDeadline(stornoAnfrage);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("WBCI Storno received successfully: %s", stornoAnfrage));
        }
    }

    /**
     * Before processing the request checks to see if any errors exist which would go against processing the request.
     *
     * @param storno
     * @return null if no error detected, otherwise the error.
     */
    private ProcessingError checkForHighSeverityError(StornoAnfrage storno) {
        ProcessingError processingError = null;

        if (storno.getWbciGeschaeftsfall() == null) {
            processingError = new ProcessingError(VAID_ABG, BEGRUENDUNG_STORNO_UNBEKANNTE_VA_REFID);
        }
        else {
            Set<Pair<RequestTyp, WbciRequestStatus>> activeChangeRequests =
                    validationService.getActiveChangeRequests(storno.getVorabstimmungsId(), storno.getAenderungsId());

            if (validationService.isTooEarlyForReceivingStornoRequest(storno)) {
                processingError = new ProcessingError(STORNO_ABG, BEGRUENDUNG_ABBM_STORNO_KEINE_RUEMVA, false);
            }
            else if (validationService.isDuplicateStornoRequest(storno)) {
                processingError = new ProcessingError(BVID, BEGRUENDUNG_DOPPELTE_STORNO);
            }
            else if (CollectionUtils.isNotEmpty(activeChangeRequests)) {
                Pair<RequestTyp, WbciRequestStatus> activeChangeRequest = activeChangeRequests.iterator().next();
                WbciRequestStatus reqStatus = activeChangeRequest.getSecond();

                RequestTyp reqTyp = activeChangeRequest.getFirst();
                if(reqStatus.isVorgehalten()) {
                    processingError = new ProcessingError(STORNO_ABG,
                            String.format(BEGRUENDUNG_AENDERUNG_VORGEHALTEN, reqTyp.getLongName()));
                }
                else {
                    processingError = new ProcessingError(STORNO_ABG,
                            String.format(BEGRUENDUNG_AENDERUNG_AKTIV, reqTyp.getLongName()));
                }
            }
            else if (validationService.isStornoFristUnterschritten(storno)) {
                processingError = new ProcessingError(STORNO_ABG, BEGRUENDUNG_FRIST_UNTERSCHRITTEN, false);
            }
        }

        if (processingError != null) {
            String logMsg = String.format("Could not process incoming Storno with changeId:'%s' for VA-Id:'%s' because: %s",
                    storno.getVorabstimmungsIdRef(),
                    storno.getAenderungsId(),
                    processingError.getErrorMessage());
            LOGGER.info(logMsg);
        }

        return processingError;
    }

    /**
     * Creates and sends an ABBM Meldung to the partner EKP. A record of the ABBM will be stored in the IO-Archive but
     * the ABBM will <b>NOT</b> be persisted in the WBCI database.
     *
     * @param stornoAnfrage
     * @param processingError
     */
    private void autoProcessError(@NotNull StornoAnfrage stornoAnfrage, ProcessingError processingError) {
        String logMsg = String.format("Could not process incoming Storno with changeId:'%s' for VA-Id:'%s' because: %s",
                stornoAnfrage.getVorabstimmungsIdRef(),
                stornoAnfrage.getAenderungsId(),
                processingError.getErrorMessage());
        LOGGER.info(logMsg);

        if (VAID_ABG.equals(processingError.getMeldungsCode())) {
            // here a dummy gf has to be added so the ABBM can be generated correctly
            addTempGfForStornoWithUnknownVaRefId(stornoAnfrage);
        }

        MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        Abbruchmeldung abbm = new AbbruchmeldungBuilder()
                .withStornoIdRef(stornoAnfrage.getAenderungsId())
                .withAbsender(CarrierCode.MNET)
                .withIoType(IOType.OUT)
                .withWbciGeschaeftsfall(stornoAnfrage.getWbciGeschaeftsfall())
                .addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                        .withMeldungsCode(processingError.getMeldungsCode())
                        .withMeldungsText(processingError.getMeldungsCode().getStandardText())
                        .build())
                .withBegruendung(processingError.getErrorMessage())
                .buildForStorno(stornoAnfrage.getTyp());

        if (processingError.isTechnical()) {
            metadata.setPostProcessMessage(false);
        } else {
            wbciDao.store(abbm);
        }

        wbciMeldungService.sendErrorResponse(metadata, abbm);
    }

    /**
     * Need to add temporary geschaeftsfall on storno anfrage in order to meet automatic validation mechanisms when
     * sending out messages. This geschaeftsfall is neither stored nor included in message data sent out.
     *
     * @param stornoAnfrage
     */
    private void addTempGfForStornoWithUnknownVaRefId(StornoAnfrage stornoAnfrage) {
        WbciGeschaeftsfallKueMrn gf = new WbciGeschaeftsfallKueMrnBuilder()
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withAbgebenderEKP(stornoAnfrage.getAbsender())
                .withKundenwunschtermin(LocalDate.now().plusDays(30))
                .withVorabstimmungsId(stornoAnfrage.getVorabstimmungsIdRef())
                .build();
        stornoAnfrage.setWbciGeschaeftsfall(gf);
    }

    /**
     * Validates incoming storno request in multiple steps. First checks on high severity errors that cause the storno
     * to be handled with error response automatically. Secondly checks on validation errors which mark for clarification.
     * Returns null if no processing error is found.
     * @param stornoAnfrage
     * @return
     */
    private ProcessingError validateIncommingStorno(StornoAnfrage stornoAnfrage) {
        ProcessingError processingError = checkForHighSeverityError(stornoAnfrage);

        if (processingError != null) {
            return processingError;
        }

        cleanseIncommingStorno(stornoAnfrage);

        CarrierCode partnerCarrierCode = stornoAnfrage.getEKPPartner();
        Set<ConstraintViolation<StornoAnfrage>> errors = validationService.checkWbciMessageForErrors(partnerCarrierCode, stornoAnfrage);
        if (CollectionUtils.isNotEmpty(errors)) {
            WbciGeschaeftsfall wbciGeschaeftsfall = stornoAnfrage.getWbciGeschaeftsfall();
            String errorMsg = constraintViolationHelper.generateErrorMsgForInboundMsg(errors);
            wbciGeschaeftsfallService.markGfForClarification(wbciGeschaeftsfall.getId(), errorMsg, null);
        }

        return processingError;
    }

    protected void cleanseIncommingStorno(StornoAnfrage stornoAnfrage) {
        // remove endkunde and standort as this is should not be used in the electronic WBCI interface. It is
        // only intended for the manual fax process (refer to FAQ lfd.Nr. 9)
        if (stornoAnfrage instanceof StornoMitEndkundeStandortAnfrage) {
            ((StornoMitEndkundeStandortAnfrage) stornoAnfrage).setEndkunde(null);
            ((StornoMitEndkundeStandortAnfrage) stornoAnfrage).setStandort(null);
        }
    }

}
