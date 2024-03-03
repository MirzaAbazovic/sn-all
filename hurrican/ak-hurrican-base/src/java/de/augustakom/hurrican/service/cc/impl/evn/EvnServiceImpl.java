package de.augustakom.hurrican.service.cc.impl.evn;

import static de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceFault.FaultEnum.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.EvnService;
import de.augustakom.hurrican.service.cc.impl.DefaultCCService;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnEnum;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceException;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceFault;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Evn service implementation of {@link EvnService}
 *
 */
@CcTxRequired
public class EvnServiceImpl extends DefaultCCService implements EvnService {
    private static final Logger LOGGER = Logger.getLogger(EvnServiceImpl.class);

    @Autowired
    private CPSService cpsService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CCAuftragService auftragService;

    @Override
    public EvnEnum getEvnData(AuftragDaten auftragDaten, IntAccount account) {
        if (!isEvnStatusChangeInProgress(auftragDaten, account)) {
            return EvnEnum.fromBoolean(account.getEvnStatus());
        } else {
            // active cps transaction found
            return EvnEnum.CHANGE_IN_PROGRESS;
        }
    }

    @Override
    @CcTxRequiresNew
    public void activateEvn(String radiusAccountId, Long sessionId) throws EvnServiceException {
        changeEvn(radiusAccountId, true, sessionId);
    }

    @Override
    @CcTxRequiresNew
    public void deactivateEvn(String radiusAccountId, Long sessionId) throws EvnServiceException {
        changeEvn(radiusAccountId, false, sessionId);
    }

    private void changeEvn(String radiusAccountId, boolean newValue, Long sessionId) throws EvnServiceException {
        final IntAccount account = getIntAccount(radiusAccountId);
        if (account != null) {
            final AuftragDaten auftragDaten = getAuftragByRadiusAccount(radiusAccountId);
            if (!isEvnStatusChangeInProgress(auftragDaten, account)) {
                if (verifyCpsTransactionsForProvisionierung(auftragDaten)) {
                    if (account.getEvnStatus() != null && account.getEvnStatus().equals(newValue)) {
                        // EVN Status hat bereits gewünschte Wert
                        // do not throw exception
                    } else {
                        // setting pending status only. real status is changed after provisonierung in cps callback method
                        // doing a clone because original entity is managed and if we change it and store in separate trx at the same time, we got a conflict
                        final IntAccount accountCloned = (IntAccount) SerializationUtils.clone(account);
                        accountCloned.setEvnStatusPending(newValue);
                        saveIntAccountInSeparateTransaction(accountCloned, false); // TODO do we need history?
                        if (auftragDaten != null && auftragDaten.isAuftragActiveAndInBetrieb()) {
                            // Es darf nur dann provisioniert werden, wenn Auftrag bereits angelegt
                            doCpsProvisionierung(auftragDaten, sessionId, false);  // do async provisionierung only!
                        }
                    }
                }
            } else {
                // EVN Status wird gerade geändert, but do not throw exception
                final String msg = String.format("EVN Status wird gerade geändert. Eine aktive CPS Transaktion oder pending EVN Status gefunden. "
                        + "Taifun Auftrag Id [%s] Account Nr. [%s]", auftragDaten.getAuftragNoOrig(), account.getAccount());
                LOGGER.warn(msg);
            }
        } else {
            final String msg = String.format("Account [%s] nicht gefunden", radiusAccountId);
            throw new EvnServiceException(msg,
                    EvnServiceFault.create(EVN_STATUS_ACCOUNT_NOT_FOUND, null, radiusAccountId));
        }
    }

    @Override
    public AuftragDaten getAuftragByRadiusAccount(String radiusAccount) throws EvnServiceException {
        final LocalDate when = LocalDate.now();
        try {
            IntAccount account = accountService.findIntAccount(radiusAccount, IntAccount.LINR_EINWAHLACCOUNT, when);
            if (account == null) {
                account = accountService.findIntAccount(radiusAccount, IntAccount.LINR_EINWAHLACCOUNT_KONFIG, when);
                if (account == null) {
                    final String msg = String.format("Fehler bei der Erfassung von Auftrag Id für radiusAccountId [%s]. "
                            + "IntAcount nicht gefunden.", radiusAccount);
                    LOGGER.error(msg);
                    throw new EvnServiceException(msg, EvnServiceFault.create(EVN_STATUS_CPS_ERROR, null, radiusAccount));
                }
            }
            final List<AuftragTechnik> auftragTechniken = auftragService.findAuftragTechnik4IntAccount(account.getId());
            final List<AuftragDaten> auftragDaten = Lists.transform(auftragTechniken,
                    new Function<AuftragTechnik, AuftragDaten>() {
                        @Override
                        @Nonnull
                        public AuftragDaten apply(AuftragTechnik technik) {
                            Preconditions.checkNotNull(technik, "Finder duerfen keine Null-Elemente liefern");
                            try {
                                return auftragService.findAuftragDatenByAuftragIdTx(technik.getAuftragId());
                            }
                            catch (FindException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        }
                    }
            );

            if (auftragDaten != null) {
                final Optional<AuftragDaten> active = auftragDaten.stream()
                        .filter(ad -> ad.isValid())  // valid from / valid to
                        .filter(ad -> ad.isInBetrieb() || ad.isInAenderung())  // 6000 6100 oder 6200
                        .findAny();
                final Optional<AuftragDaten> gekuendigtInFuture = auftragDaten.stream()
                        .filter(ad -> ad.isValid())  // valid from / valid to
                        .filter(ad -> (ad.isInKuendigungEx() || ad.isCancelled()) && ad.getKuendigung() != null && DateConverterUtils.asLocalDate(ad.getKuendigung()).isAfter(LocalDate.now()))
                        .findAny();

                if (active.isPresent()) {
                    return active.get();
                } else if (gekuendigtInFuture.isPresent()) {
                    return gekuendigtInFuture.get();
                }
            }

            // nichts gefunden
            return null;

        }
        catch (Exception e) {
            final String msg = String.format("Fehler bei der Erfassung von Auftrag Id für radiusAccountId [%s] ", radiusAccount);
            LOGGER.error(msg, e);
            throw new EvnServiceException(msg, e, EvnServiceFault.create(EVN_STATUS_TECHNICAL_ERROR, null, radiusAccount));
        }
    }

    private void saveIntAccountInSeparateTransaction(IntAccount account, boolean makeHistory)  {
        try {
            accountService.saveIntAccountInSeparateTransaction(account, makeHistory);
        }
        catch (StoreException e) {
            final String accId = account.getId() != null ? account.getId().toString() : "";
            final String accNr = account.getAccount() != null ? account.getAccount() : "";
            LOGGER.error(String.format("Fehler beim Speichern von IntAccout mit ID [%s] Account [%s]", accId, accNr), e);
        }
    }

    private IntAccount getIntAccount(String radiusAccountId) {
        try {
            return accountService.findIntAccount(radiusAccountId);
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Erfassung von IntAccout für radiusAccountId [%s] ", radiusAccountId), e);
            return null;
        }
    }

    @Override
    public List<IntAccount> getIntAccounts4Auftrag(Long auftragId) {
        try {
            return accountService.findIntAccounts4Auftrag(auftragId);
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Erfassung von IntAccout für techn. Auftrag [%d] ", auftragId), e);
            return Collections.EMPTY_LIST;
        }
    }

    private boolean isEvnStatusChangeInProgress(AuftragDaten auftragDaten, IntAccount account) {
        final boolean isPendingStatusSet = account.getEvnStatusPending() != null;
        return isPendingStatusSet || hasActiveCpsTransaction(auftragDaten);
    }

    @Override
    public boolean hasActiveCpsTransaction(AuftragDaten auftragDaten) {
        try {
            final List<CPSTransactionExt> cpsTransactionList = cpsService.findActiveCPSTransactions(auftragDaten.getAuftragNoOrig());
            final boolean hasActiveCpsTrx = cpsTransactionList != null && !cpsTransactionList.isEmpty();
            return hasActiveCpsTrx;
        }
        catch (FindException e) {
            LOGGER.error(String.format("Fehler bei der Erfassung von CPSTransactionExt für techn. Auftrag [%d] ", auftragDaten.getAuftragId()), e);
        }

        return false;
    }

    @Override
    public boolean verifyCpsTransactionsForProvisionierung(AuftragDaten auftragDaten) {
        final String messageText = "Keine EVN anpassung möglich";
        final List<Long> soCreateModCancelTypes = Arrays.asList(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);

        final Optional<CPSTransaction> latestCPS =  cpsService.findLatestCPSTransactions4TechOrder(auftragDaten.getAuftragId(),
                Collections.singletonList(CPSTransaction.TX_STATE_SUCCESS), soCreateModCancelTypes);

        if (!latestCPS.isPresent()) {
            LOGGER.warn(String.format("Keine erfolgreiche CPSTransaction von Typ [CREATE_SUB,MODIFY_SUB,CANCEL_SUB] für techn. Auftrag [%d] gefunden. "
                    + messageText, auftragDaten.getAuftragId()));
            return false;
        } else if (CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB.equals(latestCPS.get().getServiceOrderType() )) {
            LOGGER.warn(String.format("Die neuste erfolgreiche CPSTransaction ist von Typ [CANCEL_SUB == %d] für techn. Auftrag [%d] gefunden. "
                    + messageText, latestCPS.get().getServiceOrderType(), auftragDaten.getAuftragId()));
            return false;
        } else {
            LOGGER.debug(String.format("Transaktion check fuer EVN is done. Transaktion von Typ [%d] und State [%d] für techn. Auftrag [%d] gefunden. "
                    + messageText, latestCPS.get().getServiceOrderType(), CPSTransaction.TX_STATE_SUCCESS, auftragDaten.getAuftragId()));
            return true;
        }
    }

    @Override
    public void doCpsProvisionierung(AuftragDaten auftragDaten, Long sessionId, boolean isSync) throws EvnServiceException {
        final CPSProvisioningAllowed cpsProvisioningAllowed;
        try {
            cpsProvisioningAllowed = cpsService.isCPSProvisioningAllowed(
                    auftragDaten.getAuftragId(), null, false, false, true);
            if (!cpsProvisioningAllowed.isProvisioningAllowed()) {
                final String msg = String.format("Techn. Auftrag [%d] Provisionierung nicht erlaubt wegen [%s].",
                        auftragDaten.getAuftragId(), cpsProvisioningAllowed.getNoCPSProvisioningReason());
                LOGGER.error(msg);
                throw new EvnServiceException(msg, EvnServiceFault.create(EVN_STATUS_CPS_ERROR, auftragDaten, null));
            }
        }
        catch (FindException e) {
            final String msg = String.format("Techn. Auftrag [%d] Fehler bei der Provisionierung.", auftragDaten.getAuftragId());
            LOGGER.error(msg, e);
            throw new EvnServiceException(msg, e, EvnServiceFault.create(EVN_STATUS_CPS_ERROR, auftragDaten, null));
        }

        final Date now = new Date();
        final CreateCPSTransactionParameter cpsTransactionParameter = new CreateCPSTransactionParameter(
                auftragDaten.getAuftragId(), null,
                CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                now, null, null, null, null, false, false,
                sessionId);
        try {
            final CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(cpsTransactionParameter);

            if (CollectionTools.hasExpectedSize(cpsTxResult.getCpsTransactions(), 1)) {
                cpsService.sendCPSTx2CPS(cpsTxResult.getCpsTransactions().get(0), sessionId, isSync);
            }

            if (cpsTxResult.getWarnings() != null && cpsTxResult.getWarnings().isNotEmpty()) {
                final String warningsAsText = cpsTxResult.getWarnings().getWarningsAsText();
                final String msg = String.format("Techn. Auftrag [%d] Provisionierung führt zu folgendem Fehler [%s].",
                        auftragDaten.getAuftragId(), warningsAsText);
                LOGGER.error(msg);
                throw new EvnServiceException(msg, EvnServiceFault.create(EVN_STATUS_CPS_ERROR, auftragDaten, null));
            }
        }
        catch (Exception e) {
            final String msg = String.format("Techn. Auftrag [%d] Provisionierungsfehler.", auftragDaten.getAuftragId());
            LOGGER.error(msg, e);
            throw new EvnServiceException(msg, e, EvnServiceFault.create(EVN_STATUS_CPS_ERROR, auftragDaten, null));
        }
    }

    /**
     * CPS provisonierung callback method, is triggered when asynchronous cps provisonierung is completed
     * See {@link EvnService#completeEvnChange(Long)}
     *      * @param auftragId
     */
    @Override
    @CcTxRequiresNew
    public void completeEvnChange(Long auftragId) {
        final List<IntAccount> accounts = getIntAccounts4Auftrag(auftragId);
        if (accounts != null) {
            for (IntAccount acc : accounts) {
                final Boolean pendingStatus = acc.getEvnStatusPending();
                if (pendingStatus != null) {
                    // doing a clone because original entity is managed and if we change it and store in separate trx at the same time, we got a conflict
                    final IntAccount accountCloned = (IntAccount) SerializationUtils.clone(acc);
                    accountCloned.setEvnStatus(pendingStatus);
                    accountCloned.setEvnStatusPending(null);
                    saveIntAccountInSeparateTransaction(accountCloned, true); // TODO really need history?
                }
            }
        }
    }

}
