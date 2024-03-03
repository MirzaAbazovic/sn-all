/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;
import static de.mnet.wbci.model.AutomationTask.TaskName.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import javax.validation.constraints.*;
import com.google.common.base.Throwables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.converter.MeldungsCodeConverter;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.InvalidRequestStatusChangeException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.model.helper.MeldungsCodeHelper;
import de.mnet.wbci.model.helper.RueckmeldungVorabstimmungHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciRequestStatusUpdateService;
import de.mnet.wbci.service.WbciSendMessageService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;
import de.mnet.wita.service.WitaConfigService;

/**
 * Default meldung service implementation.
 *
 *
 */
@CcTxRequired
public class WbciMeldungServiceImpl implements WbciMeldungService {
    protected static final String REASON_DOUBLE_RUEM_VA = "Doppelte RUEM-VA";
    protected static final String REASON_ABBM_TR_SEND = "ABBM-TR versendet";
    protected static final String REASON_RUEM_VA_AFTER_ABBM = "RUEM-VA direkt nach ABBM";
    protected static final String REASON_RUEM_VA_DIFFERENT_PORTING_DATE = "Der neue Wechseltermin %1$td.%1$tm.%1$tY" +
            " weicht mehr als %2$s Arbeitstage vom urspruenglichen Wechseltermin %3$td.%3$tm.%3$tY ab.";
    protected static final String REASON_UNKNOWN_VAID = "Die VorabstimmungsId (%s) in der eingehenden Meldung ist nicht" +
            " bekannt und kann deshalb nicht zu einem Geschaeftsfall zugeordnet werden.";
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WbciMeldungServiceImpl.class);
    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WbciCommonService wbciCommonService;
    @Autowired
    protected WbciRequestStatusUpdateService requestStatusUpdateService;
    @Autowired
    protected WbciDeadlineService wbciDeadlineService;
    @Autowired
    protected WbciValidationService validationService;
    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    protected WbciGeschaeftsfallStatusUpdateService gfStatusUpdateService;
    @Autowired
    protected WbciWitaServiceFacade wbciWitaServiceFacade;
    @Autowired
    private ConstraintViolationHelper constraintViolationHelper;
    @Autowired
    private WbciSendMessageService wbciSendMessageService;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private CCAuftragService ccAuftragService;

    @Override
    public <M extends Meldung<?>> void processIncomingMeldung(MessageProcessingMetadata results, M wbciMeldung) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(wbciMeldung.getVorabstimmungsId());
        if (wbciGeschaeftsfall != null) {
            wbciMeldung.setWbciGeschaeftsfall(wbciGeschaeftsfall);

            try {
                validateIncomingMeldung(wbciMeldung);

                // update the correlating request for the Meldung. Do this first before storing the meldung just
                // in case the Meldung is not vaild at this time (i.e. out of sequence or redelivered Meldung)
                updateCorrelatingRequestForMeldung(wbciMeldung);

                if (MeldungTyp.ABBM_TR == wbciMeldung.getTyp()) {
                    processIncomingAbbmTr((AbbruchmeldungTechnRessource) wbciMeldung);
                }
                else if (MeldungTyp.RUEM_VA == wbciMeldung.getTyp()) {
                    processIncomingRuemVa((RueckmeldungVorabstimmung) wbciMeldung);
                }

                storeMeldung(wbciMeldung);
                results.setPostProcessMessage(true);
            }
            catch (InvalidRequestStatusChangeException e) {
                handleInvalidRequestStatusChange(wbciMeldung, e);
            }
        }
        else {
            String errMsg = String.format(REASON_UNKNOWN_VAID, wbciMeldung.getVorabstimmungsId());
            throw new MessageProcessingException(errMsg);
        }
    }

    /**
     * Used for cleansing or cleaning up an incoming Meldung. Some EKPs may send data that is technically correct
     * (according to the XSD) but still contains invalid data. To keep the WBCI database clean and consistent this
     * invalid data is silently removed before the Meldung is persisted.
     *
     * @param wbciMeldung
     * @param <M>
     */
    protected <M extends Meldung<?>> void cleanseIncomingMeldung(M wbciMeldung) {
        if (wbciMeldung instanceof RueckmeldungVorabstimmung) {
            RueckmeldungVorabstimmung ruemva = (RueckmeldungVorabstimmung) wbciMeldung;
            Set<MeldungPositionRueckmeldungVa> meldungsPositionen = ruemva.getMeldungsPositionen();

            if (GeschaeftsfallTyp.VA_RRNP.equals(ruemva.getWbciGeschaeftsfall().getTyp())) {
                // remove ADA codes if present
                MeldungsCodeHelper.removeMeldungsCodes(meldungsPositionen, MeldungsCode.getADACodes());

                // remove technische resource if present
                ruemva.setTechnischeRessourcen(null);

                // remove technology if present
                ruemva.setTechnologie(null);
            }

            // remove ZWA code if NAT present
            if (MeldungsCodeHelper.containsMeldungsCode(meldungsPositionen, MeldungsCode.NAT)) {
                MeldungsCodeHelper.removeMeldungsCodes(meldungsPositionen, MeldungsCode.ZWA);
            }
        }
        else if (wbciMeldung instanceof UebernahmeRessourceMeldung) {
            UebernahmeRessourceMeldung akmtr = (UebernahmeRessourceMeldung) wbciMeldung;

            if (GeschaeftsfallTyp.VA_KUE_ORN.equals(akmtr.getWbciGeschaeftsfall().getTyp())) {
                // remove PKIauf if set in AKM-TR, as this should not be set for KUE-ORN (see FachSpec Seite 15)
                akmtr.setPortierungskennungPKIauf(null);
            }
        }
    }

    /**
     * Validates the incoming meldung. If any minor validation errors are detected the Geschaeftsfall is marked as a
     * klaerfall and the errors are stored as a Bemerkung. <br /> The Meldung is also checked for warnings, however in
     * this case any detected warnings are stored as a Bemerkung, without marking the Geschaeftsfall as a klaerfall.
     *
     * @param wbciMeldung
     * @param <M>
     */
    private <M extends Meldung<?>> void validateIncomingMeldung(M wbciMeldung) {

        cleanseIncomingMeldung(wbciMeldung);

        CarrierCode partnerCarrierCode = wbciMeldung.getAbsender();
        Set<ConstraintViolation<M>> errors = validationService.checkWbciMessageForErrors(partnerCarrierCode, wbciMeldung);
        if (CollectionUtils.isNotEmpty(errors)) {
            LOGGER.info(String.format("Found '%s' violations for incoming meldung '%s'", errors.size(), wbciMeldung));
            WbciGeschaeftsfall wbciGeschaeftsfall = wbciMeldung.getWbciGeschaeftsfall();
            String errorMsg = constraintViolationHelper.generateErrorMsgForInboundMsg(errors);
            wbciGeschaeftsfallService.markGfForClarification(wbciGeschaeftsfall.getId(), errorMsg, null);
        }
        else {
            LOGGER.info(String.format("Error-Validation for incoming meldung '%s' successful", wbciMeldung.getTyp()));
        }

        Set<ConstraintViolation<M>> warnings = validationService.checkWbciMessageForWarnings(partnerCarrierCode, wbciMeldung);
        if (CollectionUtils.isNotEmpty(warnings)) {
            LOGGER.info(String.format("Found '%s' warnings for incoming meldung '%s'", warnings.size(), wbciMeldung));
            WbciGeschaeftsfall wbciGeschaeftsfall = wbciMeldung.getWbciGeschaeftsfall();
            String warningMsg = constraintViolationHelper.generateWarningForInboundMsg(warnings);
            wbciCommonService.addComment(wbciGeschaeftsfall.getVorabstimmungsId(), warningMsg, null);
        }
        else {
            LOGGER.info(String.format("Warning-Validation for incoming meldung '%s' successful", wbciMeldung.getTyp()));
        }
    }

    private void processIncomingAbbmTr(AbbruchmeldungTechnRessource abbmTr) {
        final Set<MeldungsCode> meldungsCodes = MeldungsCodeConverter.retrieveMeldungCodes(abbmTr.getMeldungsPositionen());
        // GF als Klaerfall markieren, wenn ABBM-TR nicht(!) den Code UETN_BB enthaelt
        if (meldungsCodes == null || meldungsCodes.size() != 1 || !meldungsCodes.contains(MeldungsCode.UETN_BB)) {
            wbciGeschaeftsfallService.markGfForClarification(abbmTr.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
        }
    }

    /**
     * Bei einer RuemVa nach bereits vorangegangener VA, die mit ERLM auf eine STR-AEN storniert wurde, muss der
     * Wechseltermin nicht später als der ursprügliche Wechseltermin + "eine konfigurierbare Abweichung" sein. Z.B.
     * Urspünglicher Wechseltermin    |    Abweichung     |     Der spätmöglichste Wechseltermin aus RuemVA 09.01.2014 5
     * Arbeitstage        16.01.2014
     */
    private void processIncomingRuemVa(RueckmeldungVorabstimmung newRuemVa) {
        WbciGeschaeftsfall wbciGeschaeftsfall = newRuemVa.getWbciGeschaeftsfall();
        final String strAenVorabstimmungsId = wbciGeschaeftsfall.getStrAenVorabstimmungsId();
        if (strAenVorabstimmungsId != null) {
            WbciGeschaeftsfall cancelledWbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(strAenVorabstimmungsId);
            if (cancelledWbciGeschaeftsfall.getWechseltermin() != null) {
                final int ruemVaPortingDateDifference = witaConfigService.getWbciRuemVaPortingDateDifference();
                final LocalDateTime originalPortingDate = cancelledWbciGeschaeftsfall.getWechseltermin().atStartOfDay();
                final LocalDate originalPortingDatePlusDifference = DateCalculationHelper.addWorkingDays(
                        originalPortingDate.toLocalDate(), ruemVaPortingDateDifference);
                if (newRuemVa.getWechseltermin().isAfter(originalPortingDatePlusDifference)) {
                    wbciGeschaeftsfallService.markGfForClarification(wbciGeschaeftsfall.getId(),
                            String.format(REASON_RUEM_VA_DIFFERENT_PORTING_DATE, Date.from(newRuemVa.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                    ruemVaPortingDateDifference, Date.from(originalPortingDate.atZone(ZoneId.systemDefault()).toInstant())), null
                    );
                }
            }
            else {
                throw new WbciServiceException(String.format("Der Geschäftsfall mit der VorabstimmungsId '%s' befindet sich in einem" +
                                " inkonsistenten Zustand. Der Wechseltermin für die vorangegangene Vorabstimmung ist nicht " +
                                "gesetzt obwohl die Vorabstimmung mit Storno-Aenderung erfolgreich storniert wurde.",
                        wbciGeschaeftsfall.getVorabstimmungsId()
                ));
            }
        }

        synchChangeDateWithOrder(newRuemVa.getWechseltermin(), newRuemVa.getWbciGeschaeftsfall().getAuftragId());
    }

    private <M extends Meldung<?>> void handleInvalidRequestStatusChange(M wbciMeldung, InvalidRequestStatusChangeException e) {
        if (e.getCurrentStatus().equals(AKM_TR_EMPFANGEN) && e.getNewStatus().equals(AKM_TR_EMPFANGEN)) {
            final AbbruchmeldungTechnRessource abbruchmeldungTr = new AbbruchmeldungTechnRessourceBuilder()
                    .buildOutgoingForVa(wbciMeldung.getWbciGeschaeftsfall(), MeldungsCode.UETN_BB);
            final MessageProcessingMetadata metadata = new MessageProcessingMetadata();
            metadata.setPostProcessMessage(false);
            wbciSendMessageService.sendAndProcessMessage(metadata, abbruchmeldungTr);
            wbciCommonService.addComment(wbciMeldung.getVorabstimmungsId(),
                    "Weitere AKM-TR empfangen; wurde automatisch mit ABBM-TR abgewiesen!", null);
            return;
        }
        else if (e.getCurrentStatus().equals(RUEM_VA_EMPFANGEN) && e.getNewStatus().equals(RUEM_VA_EMPFANGEN)) {
            wbciGeschaeftsfallService.markGfForClarification(wbciMeldung.getWbciGeschaeftsfall().getId(), REASON_DOUBLE_RUEM_VA, null);
            return;
        }
        else if (e.getCurrentStatus().equals(ABBM_EMPFANGEN) && e.getNewStatus().equals(RUEM_VA_EMPFANGEN)) {
            wbciGeschaeftsfallService.markGfForClarification(wbciMeldung.getWbciGeschaeftsfall().getId(), REASON_RUEM_VA_AFTER_ABBM, null);
            return;
        }

        // if no special handling exists for the InvalidStatusChangeException then rethrow the exception
        // this ensures that the tx is rolled-back and that the error service is invoked
        throw new WbciServiceException(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Meldung<?>> void updateCorrelatingRequestForMeldung(M wbciMeldung) {
        WbciRequestStatus newStatus;
        switch (wbciMeldung.getTyp()) {
            case ABBM:
                updateCorrelatingRequestForAbbmMeldung(wbciMeldung);
                return;
            case ERLM:
                updateCorrelatingRequestForErlmMeldung(wbciMeldung);
                return;
            case ABBM_TR:
                newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), ABBM_TR_EMPFANGEN, ABBM_TR_VERSENDET);
                updateVaAnfrage(newStatus, wbciMeldung);
                return;
            case AKM_TR:
                newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), AKM_TR_EMPFANGEN, AKM_TR_VERSENDET);
                updateVaAnfrage(newStatus, wbciMeldung);
                return;
            case RUEM_VA:
                updateCorrelatingRequestForRuemVa(wbciMeldung);
                return;
            default:
                throw new WbciServiceException(String.format("Unsupported Meldung Type: '%s'", wbciMeldung.getClass()));
        }
    }

    /**
     * Creates and sends immediately the provided Meldung
     */
    @Override
    public <M extends Meldung<?>> void createAndSendWbciMeldung(M wbciMeldung, String vorabstimmungsId) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        wbciMeldung.setIoType(IOType.OUT);
        wbciMeldung.setAbsender(CarrierCode.MNET);
        wbciMeldung.setWbciGeschaeftsfall(wbciGeschaeftsfall);

        if (wbciMeldung instanceof ErledigtmeldungTerminverschiebung) {
            // throws a WbciValidationException if an error occurs
            validationService.assertErlmTvTermin((ErledigtmeldungTerminverschiebung) wbciMeldung);
        }

        Set<ConstraintViolation<M>> errors = validationService.checkWbciMessageForErrors(wbciMeldung.getEKPPartner(), wbciMeldung);
        if (CollectionUtils.isNotEmpty(errors)) {
            throw new WbciValidationException(constraintViolationHelper.generateErrorMsg(errors));
        }

        if (wbciMeldung instanceof AbbruchmeldungTechnRessource
                && !wbciCommonService.isResourceUebernahmeRequested(wbciGeschaeftsfall.getVorabstimmungsId())
                && (wbciMeldung.containsMeldungsCodes(MeldungsCode.UETN_NM)
                    || wbciMeldung.containsMeldungsCodes(MeldungsCode.UETN_BB))) {
            throw new WbciServiceException("Da in der AKM-TR keine Ressourcenübernahme angefordert wurde, ist " +
                    "das Versenden einer ABBM-TR mit dem MeldungsCode UETN_NM oder UETN_BB nicht möglich.");
        }

        if (wbciMeldung instanceof UebernahmeRessourceMeldung) {
            UebernahmeRessourceMeldung akmTr = (UebernahmeRessourceMeldung) wbciMeldung;
            if (Boolean.TRUE.equals(akmTr.isUebernahme())) {
                RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
                if (ruemVa.hasADAMeldungsCode()) {
                    throw new WbciServiceException("Eine Ressourcenübernahme ist nicht möglich, da in der erhaltene RUEM-VA ein oder mehrere ADA MeldungsCodes vorkommt.");
                }
            }
        }

        storeMeldung(wbciMeldung);
        wbciSendMessageService.sendAndProcessMessage(wbciMeldung);

        if (wbciMeldung instanceof Abbruchmeldung) {
            Abbruchmeldung abbruchmeldung = (Abbruchmeldung) wbciMeldung;
            if (abbruchmeldung.isAbbruchmeldungForVorabstimmung()) {
                wbciGeschaeftsfallService.closeGeschaeftsfall(wbciMeldung.getWbciGeschaeftsfall().getId());
            }
        }
        else if (wbciMeldung instanceof AbbruchmeldungTechnRessource) {
            wbciGeschaeftsfallService.issueClarified(wbciMeldung.getWbciGeschaeftsfall().getId(), null, REASON_ABBM_TR_SEND);
        }
    }

    @Override
    public <M extends Meldung<?>> void sendErrorResponse(MessageProcessingMetadata metadata, M responseMeldung) {
        responseMeldung.setIoType(IOType.OUT);
        responseMeldung.setAbsender(CarrierCode.MNET);
        responseMeldung.setProcessedAt(new Date());

        Set<ConstraintViolation<M>> errors = validationService.checkWbciMessageForErrors(responseMeldung.getEKPPartner(), responseMeldung);
        if (CollectionUtils.isNotEmpty(errors)) {
            throw new WbciValidationException(constraintViolationHelper.generateErrorMsg(errors));
        }
        wbciSendMessageService.sendAndProcessMessage(metadata, responseMeldung);
    }

    @Override
    public void postProcessIncomingAkmTr(UebernahmeRessourceMeldung akmTr) {
        if (!akmTr.isUebernahme()) {
            return;
        }

        RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
        if (ruemVa == null) {
            throw new WbciServiceException(String.format("RUEM-VA zu Vorabstimmung '%s' konnte nicht ermittelt werden!", akmTr.getVorabstimmungsId()));
        }

        if (!ruemVa.getTechnologie().isRessourcenUebernahmePossible(IOType.IN, akmTr.getAbsender())) {
            createAndSendAbbmTr(MeldungsCode.UETN_NM, akmTr.getVorabstimmungsId());
            return;
        }

        if (akmTr.getLeitungen() != null) {
            for (Leitung leitung : akmTr.getLeitungen()) {
                if (leitung.getLineId() != null && !RueckmeldungVorabstimmungHelper.isLineIdPresent(ruemVa, leitung.getLineId())) {
                    createAndSendAbbmTr(MeldungsCode.LID_OVAID, akmTr.getVorabstimmungsId());
                    return;
                }
                else if (leitung.getVertragsnummer() != null && !RueckmeldungVorabstimmungHelper.isWitaVertragsnummerPresent(ruemVa, leitung.getVertragsnummer())) {
                    createAndSendAbbmTr(MeldungsCode.WVNR_OVAID, akmTr.getVorabstimmungsId());
                    return;
                }
            }
        }

        wbciWitaServiceFacade.updateWitaVorabstimmungAbgebend(akmTr);
    }


    /**
     * Aendert das {@link AuftragDaten#vorgabeSCV} Datum auf {@code changeDate}. <br></br>
     * Voraussetzung: Auftrag ist noch in Erfassung!
     * @param changeDate
     * @param auftragId
     */
    void synchChangeDateWithOrder(LocalDate changeDate, Long auftragId) {
        if (changeDate == null || auftragId == null) {
            return;
        }

        try {
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);
            if (auftragDaten != null && NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG)) {
                auftragDaten.setVorgabeSCV(Date.from(changeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                ccAuftragService.saveAuftragDaten(auftragDaten, false);
            }
        }
        catch (Exception e) {
            LOGGER.warn("Error occured during synch of WBCI change date with AuftragDaten#vorgabeSCV", e);
        }
    }


    @Override
    @CcTxRequiresNew
    public void sendAutomatedAkmTr(String vorabstimmungsId, AKUser user) {
        WbciGeschaeftsfall wbciGeschaeftsfall = null;
        try {
            wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
            final RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
            final List<String> witaVtrNrs = RueckmeldungVorabstimmungHelper.getWitaVtrNrs(ruemVa);
            if (CollectionUtils.size(witaVtrNrs) > 1) {
                throw new WbciServiceException("Eine automatisierte AMK-TR kann nur geschickt werden, wenn hoechstens" +
                        " eine WITA-Vertragsnummer in der RuemVA uebermittelt wurde!");
            }
            boolean leitungUebernahme = isLeitungUebernahmePossible(ruemVa) && !CollectionUtils.isEmpty(witaVtrNrs);

            final String tnbKennung = wbciCommonService.getTnbKennung(wbciGeschaeftsfall.getAuftragId());
            UebernahmeRessourceMeldungBuilder meldungBuilder = new UebernahmeRessourceMeldungBuilder()
                    .withSichererhafen(!leitungUebernahme)
                    .withUebernahme(leitungUebernahme)
                    .withPortierungskennungPKIauf(tnbKennung)
                    .addMeldungPosition(
                            new MeldungPositionUebernahmeRessourceMeldungBuilder()
                                    .withMeldungsCode(MeldungsCode.AKMTR_CODE)
                                    .withMeldungsText(MeldungsCode.AKMTR_CODE.getStandardText())
                                    .build()
                    );

            if (leitungUebernahme) {
                meldungBuilder.addLeitung(
                        new LeitungBuilder()
                                .withVertragsnummer(witaVtrNrs.get(0))
                                .build()
                );
            }
            
            UebernahmeRessourceMeldung meldung = meldungBuilder.build();
            createAndSendWbciMeldung(meldung, vorabstimmungsId);
            wbciGeschaeftsfallService.createOrUpdateAutomationTask(wbciGeschaeftsfall, meldung, WBCI_SEND_AKMTR, COMPLETED,
                    "Die AKM-TR wurde automatisiert und erfolgreich verschickt.", user);
        }
        catch (Exception e) {
            wbciGeschaeftsfallService.createOrUpdateAutomationTask(wbciGeschaeftsfall, WBCI_SEND_AKMTR, ERROR,
                    Throwables.getStackTraceAsString(e), user);
        }
    }


    @Override
    @CcTxRequiresNew
    public
    @NotNull
    Collection<UebernahmeRessourceMeldung> findAutomatableAkmTRsForWitaProcesing() {
        // Careful! make sure this executes in a new tx, otherwise an optimistic ex is thrown by hibernate, when this
        // method is called from the automation service.
        List<UebernahmeRessourceMeldung> akmTRs = wbciDao.findAutomatableAkmTRsForWitaProcesing(Technologie.getWitaOrderRelevantTechnologies());
        return akmTRs != null ? akmTRs : new ArrayList<>();
    }

    @Override
    @CcTxRequiresNew
    public
    @NotNull Collection<UebernahmeRessourceMeldung> findAutomatableIncomingAkmTRsProcesing() {
        // Careful! make sure this executes in a new tx, otherwise an optimistic ex is thrown by hibernate, when this
        // method is called from the automation service.
        List<UebernahmeRessourceMeldung> akmTRs = wbciDao.findAutomatableIncomingAkmTRsForWitaProcesing();
        return akmTRs != null ? akmTRs : new ArrayList<>();
    }


    @Override
    @CcTxRequiresNew
    public @NotNull Collection<ErledigtmeldungTerminverschiebung> findAutomatableTvErlmsForWitaProcessing() {
        List<ErledigtmeldungTerminverschiebung> erlmTvs = wbciDao.findAutomateableTvErlmsForWitaProcessing(Technologie.getWitaOrderRelevantTechnologies());
        return erlmTvs != null ? erlmTvs : new ArrayList<>();
    }

    @Override
    @CcTxRequiresNew
    public Collection<ErledigtmeldungStornoAuf> findAutomatableStrAufhErlmsForWitaProcessing() {
        List<ErledigtmeldungStornoAuf> strErlms = wbciDao.findAutomateableStrAufhErlmsForWitaProcessing(Technologie.getWitaOrderRelevantTechnologies());
        return strErlms != null ? strErlms : new ArrayList<>();
    }

    @Override
    @CcTxRequiresNew
    public Collection<ErledigtmeldungStornoAuf> findAutomatableStrAufhErlmsDonatingProcessing() {
        List<ErledigtmeldungStornoAuf> strErlms = wbciDao.findAutomateableStrAufhErlmsDonatingProcessing();
        return strErlms != null ? strErlms : new ArrayList<>();
    }

    protected boolean isLeitungUebernahmePossible(RueckmeldungVorabstimmung ruemVa) {
        if (ruemVa.getTechnologie().isRessourcenUebernahmePossible(IOType.OUT, ruemVa.getAbsender())
                && ruemVa.getTechnologie().isCompatibleTo(ruemVa.getWbciGeschaeftsfall().getMnetTechnologie())
                && !ruemVa.hasADAMeldungsCode()) {
            if (Technologie.SONSTIGES.equals(ruemVa.getTechnologie())) {
                return ruemVa.hasWitaVtrNr();
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Creates and sends a ABBM-TR with the assigned {@link de.mnet.wbci.model.MeldungsCode}
     *
     * @param vorabstimmungsId orginial VA-ID
     * @param meldungsCode     valid {@link de.mnet.wbci.model.MeldungsCode} for the ABBM-TR.
     */
    private void createAndSendAbbmTr(MeldungsCode meldungsCode, String vorabstimmungsId) {
        createAndSendWbciMeldung(new AbbruchmeldungTechnRessourceBuilder()
                        .addMeldungPosition(
                                new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                                        .withMeldungsCode(meldungsCode)
                                        .withMeldungsText(meldungsCode.getStandardText())
                                        .build()
                        ).build(),
                vorabstimmungsId
        );
    }

    /**
     * Stores meldung to database.
     *
     * @param wbciMeldung
     */
    private <M extends Meldung<?>> void storeMeldung(M wbciMeldung) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("Saving new WBCI Meldung: %s", wbciMeldung.toString()));
        }

        wbciDao.store(wbciMeldung);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("Successfully created WBCI Meldung: %s", wbciMeldung.toString()));
        }
    }

    private <M extends Meldung<?>> void updateCorrelatingRequestForAbbmMeldung(M wbciMeldung) {
        WbciRequestStatus newStatus;
        if (wbciMeldung instanceof AbbruchmeldungStornoAen || wbciMeldung instanceof AbbruchmeldungStornoAuf) {
            Abbruchmeldung stornoAbbm = (Abbruchmeldung) wbciMeldung;
            newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), STORNO_ABBM_EMPFANGEN, STORNO_ABBM_VERSENDET);
            updateStornoAnfrage(stornoAbbm.getStornoIdRef(), newStatus, wbciMeldung);
        }
        else if (wbciMeldung instanceof AbbruchmeldungTerminverschiebung) {
            AbbruchmeldungTerminverschiebung abbmTv = (AbbruchmeldungTerminverschiebung) wbciMeldung;
            newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), TV_ABBM_EMPFANGEN, TV_ABBM_VERSENDET);
            updateTvAnfrage(abbmTv.getAenderungsIdRef(), newStatus, wbciMeldung);
        }
        else if (wbciMeldung instanceof Abbruchmeldung) {
            newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), ABBM_EMPFANGEN, ABBM_VERSENDET);
            updateVaAnfrage(newStatus, wbciMeldung);
        }
        else {
            throw new WbciServiceException(String.format("Unsupported ABBM Meldung Type: '%s'", wbciMeldung.getClass()));
        }
    }

    private void updateCorrelatingRequestForErlmMeldung(Meldung<?> wbciMeldung) {
        WbciRequestStatus newStatus;
        if (wbciMeldung instanceof ErledigtmeldungStornoAen || wbciMeldung instanceof ErledigtmeldungStornoAuf) {
            Erledigtmeldung stornoErlm = (Erledigtmeldung) wbciMeldung;
            newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), STORNO_ERLM_EMPFANGEN, STORNO_ERLM_VERSENDET);
            updateStornoAnfrage(stornoErlm.getStornoIdRef(), newStatus, wbciMeldung);
        }
        else if (wbciMeldung instanceof ErledigtmeldungTerminverschiebung) {
            ErledigtmeldungTerminverschiebung erlmTV = (ErledigtmeldungTerminverschiebung) wbciMeldung;
            newStatus = getRequestStatusForIOType(wbciMeldung.getIoType(), TV_ERLM_EMPFANGEN, TV_ERLM_VERSENDET);
            updateVaAnfrageWechseltermin(erlmTV, erlmTV.getWbciGeschaeftsfall().getKundenwunschtermin());
            updateTvAnfrage(erlmTV.getAenderungsIdRef(), newStatus, wbciMeldung);
            synchChangeDateWithOrder(erlmTV.getWechseltermin(), erlmTV.getWbciGeschaeftsfall().getAuftragId());
        }
        else {
            throw new WbciServiceException(String.format("Unsupported Erledigt Meldung Type: '%s'",
                    wbciMeldung.getClass()));
        }
    }

    private void updateCorrelatingRequestForRuemVa(Meldung<?> wbciMeldung) {
        if (wbciMeldung instanceof RueckmeldungVorabstimmung) {
            RueckmeldungVorabstimmung ruemVa = (RueckmeldungVorabstimmung) wbciMeldung;
            updateVaAnfrageWechseltermin(ruemVa, ruemVa.getWechseltermin());
            updateVaAnfrage(getRequestStatusForIOType(wbciMeldung.getIoType(), RUEM_VA_EMPFANGEN, RUEM_VA_VERSENDET),
                    wbciMeldung);
        }
        else {
            throw new WbciServiceException(String.format("Unsupported RUEM_VA Meldung Type: '%s'",
                    wbciMeldung.getClass()));
        }
    }

    private WbciRequestStatus getRequestStatusForIOType(IOType ioType, WbciRequestStatus incoming,
            WbciRequestStatus outgoing) {
        if (IOType.IN.equals(ioType)) {
            return incoming;
        }
        else {
            return outgoing;
        }
    }

    private void updateStornoAnfrage(String stornoId, WbciRequestStatus newStatus, Meldung<?> wbciMeldung) {
        requestStatusUpdateService.updateStornoAnfrageStatus(wbciMeldung.getVorabstimmungsId(), stornoId, newStatus,
                wbciMeldung.getMeldungsCodes(), wbciMeldung.getTyp(), DateConverterUtils.asLocalDateTime(wbciMeldung.getProcessedAt()));
        updateGeschaeftsfallStatus(newStatus, getVaRequestStatus(wbciMeldung.getVorabstimmungsId()), wbciMeldung);
    }

    private void updateTvAnfrage(String changeId, WbciRequestStatus newStatus, Meldung<?> wbciMeldung) {
        requestStatusUpdateService.updateTerminverschiebungAnfrageStatus(wbciMeldung.getVorabstimmungsId(), changeId,
                newStatus, wbciMeldung.getMeldungsCodes(), wbciMeldung.getTyp(), DateConverterUtils.asLocalDateTime(wbciMeldung.getProcessedAt()));
        updateGeschaeftsfallStatus(newStatus, getVaRequestStatus(wbciMeldung.getVorabstimmungsId()), wbciMeldung);
    }

    private void updateVaAnfrage(WbciRequestStatus newStatus, Meldung<?> wbciMeldung) {
        requestStatusUpdateService.updateVorabstimmungsAnfrageStatus(wbciMeldung.getVorabstimmungsId(), newStatus,
                wbciMeldung.getMeldungsCodes(), wbciMeldung.getTyp(), DateConverterUtils.asLocalDateTime(wbciMeldung.getProcessedAt()));
        updateGeschaeftsfallStatus(newStatus, newStatus, wbciMeldung);
    }

    private void updateGeschaeftsfallStatus(WbciRequestStatus newRequestStatus, WbciRequestStatus vaRequestStatus, Meldung<?> wbciMeldung) {
        WbciGeschaeftsfallStatus gfStatus = gfStatusUpdateService.lookupStatusBasedOnRequestStatusChange(newRequestStatus, vaRequestStatus, wbciMeldung);
        gfStatusUpdateService.updateGeschaeftsfallStatus(wbciMeldung.getWbciGeschaeftsfall().getId(), gfStatus);
    }

    private void updateVaAnfrageWechseltermin(WbciMessage wbciMessage, LocalDate wechseltermin) {
        wbciGeschaeftsfallService.updateVorabstimmungsAnfrageWechseltermin(wbciMessage.getVorabstimmungsId(), wechseltermin);
    }

    private WbciRequestStatus getVaRequestStatus(String vorabstimmungsId) {
        return wbciCommonService.findVorabstimmungsAnfrage(vorabstimmungsId).getRequestStatus();
    }
}
