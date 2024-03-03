/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */

package de.mnet.wbci.service.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.helper.WbciRequestHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ValidationHelper;
import de.mnet.wita.service.WitaConfigService;

@Component
public class WbciValidationServiceImpl implements WbciValidationService {

    @Autowired
    protected WbciDao wbciDao;
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private ValidationHelper validationHelper;
    @Autowired
    private WitaConfigService configService;
    @Autowired
    private WbciCommonService wbciCommonService;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForErrors(WbciCdmVersion wbciCdmVersion, T entity) {
        if (entity == null) {
            throw new WbciServiceException("Entity is not allowed to be null.");
        }
        if (wbciCdmVersion == null) {
            throw new WbciServiceException("To execute the validation of the WBCI message '"
                    + entity.getVorabstimmungsId() + "' the underlying CDM version has to be not null.");
        }

        Class<?>[] validationGroups = validationHelper.getErrorValidationGroups(wbciCdmVersion, entity);
        if (validationGroups != null) {
            return validator.validate(entity, validationGroups);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForErrors(CarrierCode partnerCarrier,
            T entity) {
        return checkWbciMessageForErrors(configService.getWbciCdmVersion(partnerCarrier), entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForWarnings(WbciCdmVersion wbciCdmVersion,
            T entity) {
        if (entity == null) {
            throw new WbciServiceException("Entity is not allowed to be null.");
        }
        if (wbciCdmVersion == null) {
            throw new WbciServiceException("To execute the validation of the WBCI message '"
                    + entity.getVorabstimmungsId() + "' the underlying CDM version has to be not null.");
        }

        Class<?>[] validationGroups = validationHelper.getWarningValidationGroups(wbciCdmVersion, entity);
        if (validationGroups != null) {
            return validator.validate(entity, validationGroups);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkWbciMessageForWarnings(CarrierCode partnerCarrier,
            T entity) {
        return checkWbciMessageForWarnings(configService.getWbciCdmVersion(partnerCarrier), entity);
    }

    @Override
    @CcTxRequired
    public void assertAbgebenderEKPMatchesLinkedAbgebenderEKP(CarrierCode abgebenderEKP, String linkedVorabstimmungsId) {
        WbciGeschaeftsfall linkedGf = wbciDao.findWbciGeschaeftsfall(linkedVorabstimmungsId);
        if (!abgebenderEKP.equals(linkedGf.getAbgebenderEKP())) {
            String errorMsg = String
                    .format("Der abgebende EKP der stornierten Vorabstimmung (%s) stimmt nicht mit dem abgebenden EKP der neuen Vorabstimmung (%s) ueberein",
                            linkedGf.getAbgebenderEKP(), abgebenderEKP);
            throw new WbciValidationException(errorMsg);
        }
    }

    @Override
    @CcTxRequired
    public void assertLinkedVaHasNoFaultyAutomationTasks(String vorabstimmungsId) {
        if (wbciDao.hasFaultyAutomationTasks(vorabstimmungsId)) {
            throw new WbciValidationException(
                    String.format(
                            "Eine neue Vorabstimmung kann erst erzeugt werden nachdem alle Automatisierungsschritte," +
                                    " die als automatisierbar gekennzeichnet sind, der stornierten Vorabstimmung (%s) abgeschlossen sind",
                            vorabstimmungsId));
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    @CcTxRequired
    public void assertErlmTvTermin(ErledigtmeldungTerminverschiebung erlmTv) {
        TerminverschiebungsAnfrage tv = wbciDao.findWbciRequestByChangeId(
                erlmTv.getVorabstimmungsId(), erlmTv.getAenderungsIdRef(), TerminverschiebungsAnfrage.class);
        if (tv == null) {
            throw new WbciServiceException(
                    "Die Aenderungs-ID der Erledigtmeldung zur Terminverschiebung ist nicht gültig.");
        }
        if (!erlmTv.getWechseltermin().equals(tv.getTvTermin())) {
            throw new WbciValidationException("Der Termin in der Erledigtmeldung zur Terminverschiebung "
                    + erlmTv.getAenderungsIdRef()
                    + " stimmt nicht mit dem Termin der Terminverschiebungsanfrage überein!");
        }
    }

    @Override
    public boolean isDuplicateVaRequest(VorabstimmungsAnfrage<?> vorabstimmungsAnfrage) {
        String vaId = vorabstimmungsAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId();
        WbciGeschaeftsfall existingWbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vaId);
        return existingWbciGeschaeftsfall != null;
    }

    private boolean isDuplicateChangeRequest(String vaId, String changeId) {
        WbciRequest request = wbciCommonService.findWbciRequestByChangeId(vaId, changeId);
        return request != null;
    }

    @Override
    public boolean isDuplicateTvRequest(TerminverschiebungsAnfrage<?> tv) {
        return isDuplicateChangeRequest(tv.getVorabstimmungsIdRef(), tv.getAenderungsId());
    }

    @Override
    public boolean isTooEarlyForTvRequest(TerminverschiebungsAnfrage<?> tv) {
        return (!isRuemVaProcessed(tv.getVorabstimmungsIdRef()));
    }

    @Override
    public boolean isTvFristUnterschritten(TerminverschiebungsAnfrage<?> tv) {
        return isFristUnterschritten(
                configService.getWbciTvFristEingehend(),
                DateConverterUtils.asLocalDateTime(tv.getCreationDate()),
                //check if current acknowledged Wechseltermin is inside the current configured TV-Vorlauffrist
                DateConverterUtils.asLocalDateTime(tv.getWbciGeschaeftsfall().getWechseltermin())
        );
    }

    @Override
    public boolean isStornoFristUnterschritten(StornoAnfrage storno) {
        return (storno instanceof StornoAufhebungAufAnfrage || storno instanceof StornoAufhebungAbgAnfrage)
                //check if current acknowledged Wechseltermin is inside the current configured Storno-Vorlauffrist
                && isFristUnterschritten(
                configService.getWbciStornoFristEingehend(),
                DateConverterUtils.asLocalDateTime(storno.getCreationDate()),
                DateConverterUtils.asLocalDateTime(storno.getWbciGeschaeftsfall().getWechseltermin()));
    }

    @Override
    public void assertGeschaeftsfallHasNoActiveChangeRequests(String vorabstimmungsId) {
        List<WbciRequest> wbciRequests = wbciCommonService.findWbciRequestByType(vorabstimmungsId, WbciRequest.class);
        for (WbciRequest request : wbciRequests) {
            if ((request.getTyp().isStorno() || request.getTyp().isTerminverschiebung())
                    && WbciRequestStatus.getActiveChangeRequestStatuses().contains(request.getRequestStatus())) {
                throw new WbciServiceException(String.format(
                        "The Geschaeftsfall (%s) contains an active %s (%s). This must be completed before " +
                                "creating a new change request",
                        vorabstimmungsId,
                        request.getTyp().getLongName(),
                        request.getId()));
            }
        }
    }


    @Override
    public void assertGeschaeftsfallHasNoStornoErlm(String vorabstimmungsId) {
        List<WbciRequest> wbciRequests = wbciCommonService.findWbciRequestByType(vorabstimmungsId, WbciRequest.class);
        for (WbciRequest request : wbciRequests) {
            if (request.getRequestStatus().isStornoErledigtRequestStatus()) {
                throw new WbciServiceException(
                        String.format("Der Geschäftsfall (%s) besitzt bereits eine STR-ERLM Meldung und ist somit" +
                                "abgeschlossen. Eine weitere Aktion auf dieser Vorabstimmung ist nicht möglich!",
                                vorabstimmungsId));
            }
        }
    }

    @Override
    public <T extends WbciMessage> boolean hasConstraintViolation(Set<ConstraintViolation<T>> violations, Class constraintViolation) {
        return getConstraintViolation(violations, constraintViolation) != null;
    }

    @Override
    public <T extends WbciMessage> ConstraintViolation<T> getConstraintViolation(Set<ConstraintViolation<T>> violations, Class validationConstraint) {
        if (CollectionUtils.isNotEmpty(violations)) {
            for (ConstraintViolation<T> constraintViolation : violations) {
                if(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().equals(validationConstraint)) {
                    return constraintViolation;
                }
            }
        }
        return null;
    }

    private boolean isFristUnterschritten(int frist, @NotNull LocalDateTime requestIncome, @Nullable LocalDateTime deadlineDate) {
        boolean error = false;
        if (deadlineDate != null) {
            int daysBetween = DateCalculationHelper.getDaysBetween(
                    requestIncome.toLocalDate(),
                    deadlineDate.toLocalDate(),
                    DateCalculationHelper.DateCalculationMode.WORKINGDAYS);
            error = daysBetween < frist;
        }
        return error;
    }

    @Override
    public boolean isMNetReceivingCarrier(WbciRequest<?> request) {
        return request.getWbciGeschaeftsfall().isMNetReceivingCarrier();
    }

    @Override
    public boolean isTooEarlyForReceivingStornoRequest(StornoAnfrage stornoAnfrage) {
        // a storno can be received as soon as the RuemVa has been sent
        return !isRuemVaProcessed(stornoAnfrage.getVorabstimmungsIdRef())
                // If Storno is sent by donating carrier --> true (too early)
                // If Storno is sent by receiving carrier --> false (not too early)
                && isMNetReceivingCarrier(stornoAnfrage);
    }

    @Override
    public boolean isDuplicateStornoRequest(StornoAnfrage storno) {
        return isDuplicateChangeRequest(storno.getVorabstimmungsIdRef(), storno.getAenderungsId());
    }

    @Override
    public Set<Pair<RequestTyp, WbciRequestStatus>> getActiveChangeRequests(String vorabstimmungsId, String changeIdToExclude) {
        Set<Pair<RequestTyp, WbciRequestStatus>> activeChangeRequests = new HashSet<>();
        List<WbciRequest> requests = wbciCommonService.findWbciRequestByType(vorabstimmungsId, WbciRequest.class);

        if (CollectionUtils.isNotEmpty(requests)) {
            for (WbciRequest request : requests) {
                String changeId = null;
                boolean isActiveRequest = false;
                if(request.getTyp().isStorno()) {
                    StornoAnfrage storno = (StornoAnfrage) request;
                    isActiveRequest = storno.getRequestStatus().isActiveStornoRequestStatus();
                    changeId = storno.getAenderungsId();
                }
                else if(request.getTyp().isTerminverschiebung()) {
                    TerminverschiebungsAnfrage tv = (TerminverschiebungsAnfrage) request;
                    isActiveRequest = tv.getRequestStatus().isActiveTvRequestStatus();
                    changeId = tv.getAenderungsId();
                }
                // check if request is in state where no response has been sent or received
                if (isActiveRequest && (changeIdToExclude == null || !changeIdToExclude.equals(changeId))) {
                        activeChangeRequests.add(new Pair<RequestTyp, WbciRequestStatus>(request.getTyp(), request.getRequestStatus()));
                }
            }
        }
        return activeChangeRequests;
    }

    @Override
    public boolean isTvOrStornoActive(String vorabstimmungsId) {
        return WbciRequestHelper.isActiveStornoOrTvRequestIncluded(wbciCommonService.findWbciRequestByType(vorabstimmungsId, WbciRequest.class));
    }

    /**
     * Checks that a RUEM-VA has been sent or received for the geschaeftsfall.
     * @param vaId
     * @return
     */
    boolean isRuemVaProcessed(String vaId) {
        Meldung ruemVa = wbciCommonService.findLastForVaId(vaId, RueckmeldungVorabstimmung.class);
        return (ruemVa != null && ruemVa.getProcessedAt() != null);
    }

}
