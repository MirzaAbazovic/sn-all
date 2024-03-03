package de.mnet.migration.hurrican.evn;

import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.EvnService;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceException;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

/**
 * Migration EVN {@see ANF-595}
 */
public class AccountEvnTransformer extends HurricanTransformator<AccountEvn> {

    private static final Logger LOGGER = Logger.getLogger(AccountEvnTransformer.class);

    public static class MigMessages extends HurricanMessages {
        Message SUCCESS = new Message(
                TransformationStatus.OK, 0x01L,
                "Migration der Zeitfenster erfolgreich durchgefuehrt: %s");
        Message ERROR_FINDER = new Message(
                TransformationStatus.ERROR, 0x02L,
                "Aggregation der Daten fehlgeschlagen: %s");
        Message ERROR_STORE = new Message(
                TransformationStatus.ERROR, 0x04L,
                "Aktualisieren der Daten fehlgeschlagen: %s");
        Message ERROR_EVN_SERVICE = new Message(
                TransformationStatus.ERROR, 0x06L,
                "Evn Service fehlgeschlagen: %s");
        Message ERROR_UNEXPECTED = new Message(
                TransformationStatus.ERROR, 0x08L,
                "Unspezifizierter Fehler: %s");
        Message INFO = new Message(
                TransformationStatus.INFO, 0x10L,
                "Bitte Hinweis beachten: %s");
    }

    static MigMessages messages = new MigMessages();
    static HurricanMigrationStarter migrationStarter;

    private Long sessionId;

    @Inject
    private EvnService evnService;
    @Inject
    private AccountService accountService;
    @Inject
    private AccountEvnMigIdHolder migIdHolder;
    @Inject
    private Environment springEnvironment;


    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    public static void main(String[] args) {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/evn/account-evn-migration.xml").finish();
        System.exit(0); // Close VM, destroy atlas connection
    }

    @Override
    public TransformationResult transform(AccountEvn row) {
        final String message = String.format("Migriere Account [%s] mit EVN Status [%s]", row.accountNumber, row.evnStatus);
        messages.prepare(message);
        LOGGER.info(message);

        final SourceIdList sourceIdList = new SourceIdList(Arrays.asList(new SourceTargetId("Account", row.accountNumber)));
        return messages.evaluate(sourceIdList, execute(row));
    }

    protected TargetIdList execute(AccountEvn row) {
        try {
            if (StringUtils.isNotEmpty(row.accountNumber) && row.evnStatus != null) {
                if (migIdHolder.checkIdAndMigrate(row.accountNumber)) {

                    final IntAccount account = getIntAccount(row.accountNumber);
                    if (account != null) {
                        final AuftragDaten auftragDaten = getAuftragByRadiusAccount(row.accountNumber);
                        if (auftragDaten != null) {
                            if (!hasActiveCpsTransaction(auftragDaten)) {
                                if (evnService.verifyCpsTransactionsForProvisionierung(auftragDaten)) {

                                    LOGGER.info(String.format("Aktualisieren Account [%s] von tech. Auftrag [%s] "
                                            + "mit PENDING EVN Status [%s]", row.accountNumber, auftragDaten.getAuftragId(), row.evnStatus));
                                    IntAccount accountCloned = (IntAccount) SerializationUtils.clone(account);
                                    accountCloned.setEvnStatusPending(row.evnStatus);
                                    saveIntAccountInSeparateTransaction(accountCloned, false); // withOUT history

                                    if (neededSyncCpsProvisionierung()) {
                                        // sync cps provisionierung
                                        LOGGER.info(String.format("Starting Synchron CPS Provisionierung für techn. Auftrag [%s]", auftragDaten.getAuftragId()));
                                        doCpsProvisionierung(auftragDaten, true);  // sync

                                        // update status here when provisionierung is done
                                        LOGGER.info(String.format("Aktualisieren Account [%s] von tech. Auftrag [%s] "
                                                + "mit EVN Status [%s]", row.accountNumber, auftragDaten.getAuftragId(), row.evnStatus));
                                        accountCloned = (IntAccount) SerializationUtils.clone(account);
                                        accountCloned.setEvnStatus(row.evnStatus);
                                        accountCloned.setEvnStatusPending(null);
                                        saveIntAccountInSeparateTransaction(accountCloned, true); // with history
                                    }
                                    else {
                                        // async provisionierung by default
                                        LOGGER.info(String.format("Starting ASYNCHRON CPS Provisionierung für techn. Auftrag [%s]", auftragDaten.getAuftragId()));
                                        doCpsProvisionierung(auftragDaten, false);  // async
                                        // do not update status here, is done in async cps callback
                                    }

                                    // migration is done
                                    final String msg = String.format("Migration für Account [%s] und EVN [%s] durchgefuehrt",
                                            row.accountNumber, row.evnStatus);
                                    messages.SUCCESS.add(msg);
                                    LOGGER.info(msg);
                                    return buildTargetIdList(row, auftragDaten);
                                } else {
                                    // verifyCpsTransactionsForProvisionierung == false
                                    final String msg = String.format("CPS Transaktion check failed für techn. Auftrag [%d]. See log for details. ", auftragDaten.getAuftragId());
                                    messages.SKIPPED.add(msg);
                                    LOGGER.warn(msg);
                                }
                            }
                            else {
                                // EVN Status wird gerade geändert. Eine aktive CPS Transaktion gefunden
                                final String msg = String.format("EVN Status wird gerade geändert. Eine aktive CPS Transaktion "
                                        + "gefunden für tech. Auftrag [%s] und Account [%s].", auftragDaten.getAuftragId(), row.accountNumber);
                                messages.SKIPPED.add(msg);
                                LOGGER.info(msg);
                            }
                        } else {
                            final String msg = String.format("Für Account [%s] (neu EVN=%s) wurde kein Auftrag gefunden", row.accountNumber, row.evnStatus);
                            messages.SKIPPED.add(msg);
                            LOGGER.warn(msg);
                        }
                    }
                    else {
                        // Account nicht gefunden oder kein Evn Status gegeben
                        final String msg = String.format("Account [%s] nicht gefunden oder kein EVN Status [%s] gegeben", row.accountNumber, row.evnStatus);
                        messages.BAD_DATA.add(msg);
                        LOGGER.warn(msg);
                    }

                }
                else {
                    //could be already migrated
                    final String msg = String.format("Account [%s] is already migrated", row.accountNumber);
                    messages.SKIPPED.add(msg);
                    LOGGER.warn(msg);
                }
            }
            else {
                // empty row.accountNumber or row.evnStatus
                final String msg = String.format("Source account [%s] or EVN Status [%s] is empty", row.accountNumber, row.evnStatus);
                messages.BAD_DATA.add(msg);
                LOGGER.warn(msg);
            }

        }
        catch (FindException e) {
            messages.ERROR_FINDER.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        catch (StoreException e) {
            messages.ERROR_STORE.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        catch (EvnServiceException e) {
            messages.ERROR_EVN_SERVICE.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return buildTargetIdList(row, null);
    }

    private TargetIdList buildTargetIdList(AccountEvn row, AuftragDaten auftragDaten) {
        final List<SourceTargetId> ids = new ArrayList<>();
        ids.add(new SourceTargetId("Account", row.accountNumber));
        if (auftragDaten != null) {
            ids.add(new SourceTargetId("AuftragId", auftragDaten.getAuftragId()));
            ids.add(new SourceTargetId("AuftragNoOrig", auftragDaten.getAuftragNoOrig()));
        }
        return new TargetIdList(ids);
    }

    private IntAccount getIntAccount(String radiusAccountId) throws FindException {
        return accountService.findIntAccount(radiusAccountId);
    }

    public AuftragDaten getAuftragByRadiusAccount(String radiusAccount) throws EvnServiceException {
        return evnService.getAuftragByRadiusAccount(radiusAccount);
    }

    private boolean hasActiveCpsTransaction(AuftragDaten auftragDaten) {
        return evnService.hasActiveCpsTransaction(auftragDaten);
    }

    private void saveIntAccountInSeparateTransaction(IntAccount account, boolean makeHistory) throws StoreException {
        accountService.saveIntAccountInSeparateTransaction(account, makeHistory);
    }

    private void doCpsProvisionierung(AuftragDaten auftragDaten, boolean sync) throws EvnServiceException {
        evnService.doCpsProvisionierung(auftragDaten, sessionId, sync);
    }

    private boolean neededSyncCpsProvisionierung() {
        return springEnvironment.acceptsProfiles("sync-cps-provisionierung");
    }

}
