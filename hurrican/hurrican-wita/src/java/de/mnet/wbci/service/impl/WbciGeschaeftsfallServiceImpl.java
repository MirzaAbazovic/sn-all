/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.WbciAction.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;
import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;
import de.mnet.wbci.model.helper.WbciRequestHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciCustomerService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

@CcTxRequired
public class WbciGeschaeftsfallServiceImpl implements WbciGeschaeftsfallService {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WbciGeschaeftsfallServiceImpl.class);

    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WbciCommonService wbciCommonService;
    @Autowired
    protected WbciMeldungService wbciMeldungService;
    @Autowired
    protected WbciWitaServiceFacade wbciWitaServiceFacade;
    @Autowired
    private WbciGeschaeftsfallStatusUpdateService wbciGeschaeftsfallStatusUpdateService;
    @Autowired
    private WbciCustomerService wbciCustomerService;
    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public void closeGeschaeftsfall(Long geschaeftsfallId) {
        wbciGeschaeftsfallStatusUpdateService.updateGeschaeftsfallStatus(geschaeftsfallId, WbciGeschaeftsfallStatus.COMPLETE);
    }

    @Override
    public void closeLinkedStrAenGeschaeftsfall(WbciRequest request) {
        WbciGeschaeftsfall cancelledGf = wbciDao.findWbciGeschaeftsfall(request.getWbciGeschaeftsfall()
                .getStrAenVorabstimmungsId());
        closeGeschaeftsfall(cancelledGf.getId());
    }

    @Override
    public boolean isLinkedToStrAenGeschaeftsfall(@NotNull String vorabstimmungsId, @NotNull Long geschaeftsfallId) {
        WbciGeschaeftsfall strAenGf = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        WbciGeschaeftsfall newVaGf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);

        if (!WbciGeschaeftsfallStatus.NEW_VA.equals(strAenGf.getStatus())
                && !WbciGeschaeftsfallStatus.NEW_VA_EXPIRED.equals(strAenGf.getStatus())) {
            return false;
        }

        if (CarrierRole.AUFNEHMEND.equals(CarrierRole.lookupMNetCarrierRole(newVaGf)) ||
                CarrierRole.AUFNEHMEND.equals(CarrierRole.lookupMNetCarrierRole(strAenGf))) {
            return false;
        }

        return newVaGf.getAufnehmenderEKP().equals(strAenGf.getAufnehmenderEKP());

    }

    @Override
    public void assignTaifunOrderAndCloseStrAenGeschaeftsfall(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId,
            @NotNull Long strAenGeschaeftsfallId, boolean addCustomerCommunication) throws FindException {
        WbciGeschaeftsfall strAenGf = wbciDao.findById(strAenGeschaeftsfallId, WbciGeschaeftsfall.class);
        if (WbciGeschaeftsfallStatus.NEW_VA.equals(strAenGf.getStatus())) {
            WbciGeschaeftsfall newVaGf = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
            if (isGeschaeftsfallWechselRrnpToMrnOrn(strAenGf, newVaGf)) {
                throw new WbciServiceException("GF-Wechsel von RRNP zu KUE-MRN/KUE-ORN ueber STR-AEN ist nicht erlaubt!");
            }

            newVaGf.setStrAenVorabstimmungsId(strAenGf.getVorabstimmungsId());
            wbciDao.store(newVaGf);

            closeGeschaeftsfall(strAenGeschaeftsfallId);

            wbciCommonService.assignTaifunOrder(vorabstimmungsId, taifunOrderId, addCustomerCommunication);
        }
        else {
            throw new WbciServiceException(String.format("Geschaeftsfall is not closable after new VA, expected GF to be in status '%s', but was '%s'.", WbciGeschaeftsfallStatus.NEW_VA, strAenGf.getStatus()));
        }
    }

    @Override
    public boolean isGeschaeftsfallWechselRrnpToMrnOrn(WbciGeschaeftsfall originalGf, WbciGeschaeftsfall actualGf) {
        return originalGf.getTyp().equals(GeschaeftsfallTyp.VA_RRNP) && !actualGf.getTyp().equals(GeschaeftsfallTyp.VA_RRNP);
    }

    @Override
    public void assignTaifunOrderAndRejectVA(@NotNull String vorabstimmungsId,
            @NotNull Long taifunOrderId) throws FindException {
        wbciCommonService.assignTaifunOrder(vorabstimmungsId, taifunOrderId, true);
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        final Abbruchmeldung abbruchmeldung = new AbbruchmeldungBuilder()
                .buildOutgoingForVa(wbciGeschaeftsfall, null, MeldungsCode.VAE);
        wbciMeldungService.createAndSendWbciMeldung(abbruchmeldung, vorabstimmungsId);
    }

    @Override
    public boolean isGfAssignedToTaifunOrder(@NotNull String vorabstimmungsId, @NotNull Long taifunOrderId) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        return wbciGeschaeftsfall != null
                && NumberTools.equal(wbciGeschaeftsfall.getBillingOrderNoOrig(), taifunOrderId);
    }

    @Override
    public void markGfForClarification(Long geschaeftsfallId, String reason, AKUser user) {
        WbciGeschaeftsfall gf = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        gf.setKlaerfall(Boolean.TRUE);
        String fallbackReason = "Geschaeftsfall als Klärfall markiert, aber kein Grund angegeben.";
        final String reasonNullAware = reason != null ? "Klärfall: " + reason : fallbackReason;
        wbciCommonService.addComment(gf.getVorabstimmungsId(), reasonNullAware, user);
    }

    @Override
    public void markGfAsAutomatable(Long geschaeftsfallId, boolean automatable) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        List<WbciRequestStatus> requestStatuses = WbciRequestHelper.getAllGeschaeftsfallRequestStatuses(
                wbciDao.findWbciRequestByType(wbciGeschaeftsfall.getVorabstimmungsId(), WbciRequest.class));
        if (EDIT_AUTOMATED_FLAG.isActionPermitted(
                CarrierRole.lookupMNetCarrierRole(wbciGeschaeftsfall), requestStatuses,
                wbciGeschaeftsfall.getStatus(), null, null)) {
            wbciGeschaeftsfall.setAutomatable(automatable);
            wbciDao.store(wbciGeschaeftsfall);
        }
        else {
            throw new WbciServiceException(String.format(
                    "WBCI Geschäftsfall '%s' darf im aktuellen Status nicht modifiziert werden.",
                    wbciGeschaeftsfall.getVorabstimmungsId()));
        }
    }

    @Override
    public void issueClarified(Long geschaeftsfallId, AKUser user, String comment) {
        if (StringUtils.isBlank(comment)) {
            throw new IllegalArgumentException("A comment must be provided when resolving an Issue");
        }

        WbciGeschaeftsfall gf = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        if (Boolean.TRUE.equals(gf.getKlaerfall())) {
            gf.setKlaerfall(Boolean.FALSE);
            wbciCommonService.addComment(gf.getVorabstimmungsId(), comment, user);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateVorabstimmungsAnfrageWechseltermin(String vorabstimmungsId, LocalDate wechseltermin) {
        WbciGeschaeftsfall geschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        if (geschaeftsfall != null) {
            geschaeftsfall.setWechseltermin(wechseltermin);
            wbciDao.store(geschaeftsfall);
        }
        else {
            throw new WbciServiceException(String.format("Couldn't determine a valid wbciGeschaeftsfall for VA-ID '%s'", vorabstimmungsId));
        }
    }

    @Override
    public void updateInternalStatus(Long geschaeftsfallId, String internalStatus) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        wbciGeschaeftsfall.setInternalStatus(internalStatus);
        wbciDao.store(wbciGeschaeftsfall);
    }

    @Override
    public void deleteGeschaeftsfall(Long geschaeftsfallId) {
        // locking gf and requests -> since we are planning on deleting them we don't want to allow any changes
        // or requests to be transmitted
        WbciGeschaeftsfall gf = wbciDao.byIdWithLockMode(geschaeftsfallId, LockMode.PESSIMISTIC_WRITE, WbciGeschaeftsfall.class);

        List<WbciRequest> reqs = wbciDao.findWbciRequestByType(gf.getVorabstimmungsId(), WbciRequest.class, LockMode.PESSIMISTIC_WRITE);
        for (WbciRequest req : reqs) {
            if (req.getRequestStatus().isVorgehalten()) {
                wbciDao.delete(req);
            }
            else {
                throw new WbciServiceException(String.format("Geschaeftsfall (%s) cannot be deleted! One of its requests (id:%s, status:%s) has already been transmitted", gf.getVorabstimmungsId(), req.getId(), req.getRequestStatus()));
            }
        }

        wbciDao.delete(gf);
    }

    @Override
    public void deleteStornoOrTvRequest(Long wbciRequestId) {
        // locking request -> since we are planning on deleting it we don't want to allow request to be transmitted by scheduler
        WbciRequest request = wbciDao.byIdWithLockMode(wbciRequestId, LockMode.PESSIMISTIC_WRITE, WbciRequest.class);
        RequestTyp typ = request.getTyp();
        if (typ.isStorno() || typ.isTerminverschiebung()) {
            if (request.getRequestStatus().isVorgehalten()) {
                wbciDao.delete(request);
            }
            else {
                throw new WbciServiceException(String.format("Request cannot be deleted as it has already been transmitted (id:%s, status:%s)", request.getId(), request.getRequestStatus()));
            }
        }
        else {
            throw new WbciServiceException(String.format("Invalid service invocation. This service cannot be used with requests of type '%s'", typ));
        }
    }

    @Override
    public AutomationTask completeAutomationTask(AutomationTask automationTask, AKUser user) {
        if (!automationTask.isDone()) {
            automationTask.setCompletedAt(new Date());
            Long userId = user != null ? user.getId() : null;
            automationTask.setUserId(userId);
            String username = user != null ? user.getName() : HurricanConstants.SYSTEM_USER;
            automationTask.setUserName(username);
            automationTask.setStatus(AutomationStatus.COMPLETED);
            addExecutionLog(automationTask, "Der Automatisierungsschritt wurde manuell abgeschlossen!", username);
            return wbciDao.store(automationTask);
        }
        return automationTask;
    }

    @Override
    public int autoCompleteEligiblePreagreements() {
        int autoClosedPreagreements = 0;

        final int restrictPreagreementsProcessed = 0; // 0 = no restriction
        List<WbciGeschaeftsfall> elapsedCandidates = wbciDao.findElapsedPreagreements(restrictPreagreementsProcessed);
        for (WbciGeschaeftsfall gf : elapsedCandidates) {
            if (!hasActiveStornoOrTv(gf)) {
                gf.setStatus(WbciGeschaeftsfallStatus.COMPLETE);
                wbciDao.store(gf);
                autoClosedPreagreements++;
            }
        }

        return autoClosedPreagreements;
    }

    @Override
    public int updateExpiredPreagreements() {
        LocalDate wechselTerminInNext7WorkingDays = DateCalculationHelper.getDateInWorkingDaysFromNow(8).toLocalDate();
        List<WbciGeschaeftsfall> expiredCandidates = wbciDao.findPreagreementsWithStatusAndWechselTerminBefore(NEW_VA, wechselTerminInNext7WorkingDays);
        for (WbciGeschaeftsfall gf : expiredCandidates) {
            gf.setStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);
            wbciDao.store(gf);

            ErledigtmeldungStornoAen erlmStrAen = wbciCommonService.findLastForVaId(gf.getVorabstimmungsId(), ErledigtmeldungStornoAen.class);
            if (erlmStrAen != null) {
                wbciCustomerService.sendCustomerServiceProtocol(erlmStrAen);
            }
        }

        return expiredCandidates.size();
    }

    private boolean hasActiveStornoOrTv(WbciGeschaeftsfall gf) {
        List<WbciRequest> wbciRequests = wbciDao.findWbciRequestByType(gf.getVorabstimmungsId(), WbciRequest.class);
        for (WbciRequest wbciRequest : wbciRequests) {
            if (wbciRequest.getTyp().isStorno() && wbciRequest.getRequestStatus().isActiveStornoRequestStatus()) {
                return true;
            }
            else if (wbciRequest.getTyp().isTerminverschiebung() && wbciRequest.getRequestStatus().isActiveTvRequestStatus()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkCbVorgangAndMarkForClarification(final WbciGeschaeftsfall wbciGeschaeftsfall) {
        final List<WitaCBVorgang> witaCbVorgaenge =
                wbciWitaServiceFacade.findWitaCbVorgaenge(wbciGeschaeftsfall.getVorabstimmungsId());
        if (!CollectionUtils.isEmpty(witaCbVorgaenge)) {
            final Set<String> carrierRefNrs = extractCarrierRefNrs(witaCbVorgaenge);
            StringBuilder carrierRefNrsAsString = new StringBuilder();
            for (String carrierRefNr : carrierRefNrs) {
                if (carrierRefNrsAsString.length() != 0) {
                    carrierRefNrsAsString.append(", ");
                }
                carrierRefNrsAsString.append(carrierRefNr);
            }
            markGfForClarification(wbciGeschaeftsfall.getId(),
                    String.format(REASON_WITA_CB_VORGANG_PRESENT,
                            wbciGeschaeftsfall.getVorabstimmungsId(),
                            carrierRefNrsAsString.toString()), null
            );
        }
    }

    @Override
    public void assignCustomerTypeToEndCustomer(Long geschaeftsfallId, KundenTyp kundenTyp) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        wbciGeschaeftsfall.getEndkunde().setKundenTyp(kundenTyp);
        wbciDao.store(wbciGeschaeftsfall);
    }

    @Override
    @CcTxRequiresNew
    public void createOrUpdateAutomationTaskNewTx(String vorabstimmungsId, Meldung meldung, TaskName taskName,
            AutomationStatus automationStatus, String modifications, AKUser user) {

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
        createOrUpdateAutomationTask(wbciGeschaeftsfall, meldung, taskName, automationStatus, modifications, user);
    }

    @Override
    public void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall, TaskName taskName,
            ElektraResponseDto elektraResponse, AKUser user) {
        final AutomationStatus automationStatus = getAutomationStatus(elektraResponse.getStatus());
        createOrUpdateAutomationTask(wbciGeschaeftsfall, null, taskName, automationStatus, elektraResponse.getModifications(), user);
    }

    @Override
    public void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall, TaskName taskName,
            AutomationStatus automationStatus, String modifications, AKUser user) {
        createOrUpdateAutomationTask(wbciGeschaeftsfall, null, taskName, automationStatus, modifications, user);
    }

    @Override
    public void createOrUpdateAutomationTask(WbciGeschaeftsfall wbciGeschaeftsfall, Meldung meldung, TaskName taskName, AutomationStatus automationStatus, String modifications, AKUser user) {
        try {
            AutomationTask automationTask = null;
            if (taskName.isAutomatable()) {
                if (taskName.isMultipleTask()) {
                    automationTask = wbciGeschaeftsfall.getAutomationTask(taskName, AutomationStatus.ERROR);
                }
                else {
                    automationTask = wbciGeschaeftsfall.getAutomationTask(taskName);
                }
            }

            final Long userId = user != null ? user.getId() : null;
            final String userName = user != null ? user.getName() : HurricanConstants.SYSTEM_USER;
            LOGGER.info(String.format("create new automation task entry %s for the VA-ID '%s' and the user '%s' - %s: %s ",
                    taskName.name(), wbciGeschaeftsfall.getVorabstimmungsId(), userName, automationStatus.name(), modifications));

            if (automationTask == null) {
                automationTask = new AutomationTaskBuilder()
                        .withName(taskName)
                        .withCreatedAt(LocalDateTime.now())
                        .withCompletedAt(automationStatus == AutomationStatus.COMPLETED ? LocalDateTime.now() : null)
                        .withStatus(automationStatus)
                        .withUserId(userId)
                        .withUserName(userName)
                        .withMeldung(meldung)
                        .build();
                wbciGeschaeftsfall.addAutomationTask(automationTask);
            }
            else {
                automationTask.setUserId(userId);
                automationTask.setUserName(userName);
                automationTask.setCompletedAt(automationStatus == AutomationStatus.COMPLETED ? new Date() : null);
                automationTask.setStatus(automationStatus);
            }
            addExecutionLog(automationTask, modifications, userName);
            activateWbciGeschaeftsfallDuringAutomationError(wbciGeschaeftsfall, automationTask);
            wbciDao.store(wbciGeschaeftsfall);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * try to change the state to active, if this is not possible add a log entry to the automation task
     *
     * @param wbciGeschaeftsfall modifiable {@link WbciGeschaeftsfall}
     * @param automationTask     modifiable {@link AutomationTask}
     */
    protected void activateWbciGeschaeftsfallDuringAutomationError(WbciGeschaeftsfall wbciGeschaeftsfall, AutomationTask automationTask) {
        if (AutomationStatus.ERROR.equals(automationTask.getStatus())) {
            try {
                wbciGeschaeftsfallStatusUpdateService.updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(wbciGeschaeftsfall, ACTIVE);
            }
            catch (Exception e) {
                addExecutionLog(automationTask, Throwables.getStackTraceAsString(e), automationTask.getUserName());
            }
        }
    }

    @Override
    @CcTxRequiresNew
    public Collection<String> findPreagreementsWithAutomatableRuemVa() {
        // Careful! make sure this executes in a new tx, otherwise an optimistic ex is thrown by hibernate, when this
        // method is called from the automation service.
        Collection<String> vaIds = wbciDao.findPreagreementsWithAutomatableRuemVa();
        Collection<String> filteredVaIds = new ArrayList<>();
        if (vaIds != null) {
            for (String vaId : vaIds) {
                WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vaId);
                if (isWechselTerminOffsetInRange(wbciGeschaeftsfall)) {
                    filteredVaIds.add(vaId);
                }
            }
        }
        return filteredVaIds;
    }


    @Override
    @CcTxRequiresNew
    public
    @NotNull
    Collection<WbciGeschaeftsfall> findAutomateableOutgoingRuemVaForKuendigung() {
        List<WbciGeschaeftsfall> gfsForKuendigung = wbciDao.findAutomateableOutgoingRuemVaForKuendigung();
        Collection<WbciGeschaeftsfall> filteredGfs = new ArrayList<>();
        if (gfsForKuendigung != null) {
            for (WbciGeschaeftsfall gf : gfsForKuendigung) {
                if (gf.getWechseltermin() != null) {
                    filteredGfs.add(gf);
                }
            }
        }

        return filteredGfs;

    }

    protected boolean isWechselTerminOffsetInRange(WbciGeschaeftsfall wbciGeschaeftsfall) {
        LocalDateTime kundenWunschtermin = wbciGeschaeftsfall.getKundenwunschtermin().atStartOfDay();
        LocalDateTime wechselTermin = wbciGeschaeftsfall.getWechseltermin().atStartOfDay();
        if (wechselTermin.isEqual(kundenWunschtermin)) {
            return true;
        }
        else {
            if (wechselTermin.isAfter(kundenWunschtermin)) {
                int offset = witaConfigService.getWbciRequestedAndConfirmedDateOffset();
                LocalDate permittedOffset = DateCalculationHelper.addWorkingDays(kundenWunschtermin.toLocalDate(), offset + 1);
                return permittedOffset.isAfter(wechselTermin.toLocalDate());
            }
            else {
                return false;
            }
        }
    }

    protected void addExecutionLog(AutomationTask automationTask, String log, String username) {
        String prefix;
        if (StringUtils.isEmpty(automationTask.getExecutionLog())) {
            prefix = "";
        }
        else {
            prefix = automationTask.getExecutionLog() + "\n\n";
        }
        automationTask.setExecutionLog(
                String.format(
                        "%1$s------------------------------ durchgefuehrt von'%2$s' am %3$td.%3$tm.%3$tY %3$tT%n%4$s", prefix, username,
                        new Date(), log));
    }

    protected AutomationStatus getAutomationStatus(ElektraResponseDto.ResponseStatus responseStatusType) {
        switch (responseStatusType) {
            case OK:
                return AutomationStatus.COMPLETED;
            case ERROR:
                return AutomationStatus.ERROR;
            default:
                throw new IllegalStateException(String.format("Unerwarteter ResponseStatus '%s'", responseStatusType));
        }
    }

    private Set<String> extractCarrierRefNrs(List<WitaCBVorgang> witaCBVorgangList) {
        Set<String> carrierRefNrs = new HashSet<>();
        for (WitaCBVorgang witaCBVorgang : witaCBVorgangList) {
            carrierRefNrs.add(witaCBVorgang.getCarrierRefNr());
        }
        return carrierRefNrs;
    }

}
