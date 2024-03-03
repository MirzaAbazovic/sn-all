package de.mnet.migration.hurrican.evn;

import java.util.*;

/**
 * Singleton Id holder for {@link AccountEvnTransformer} migration
 * Used to control migration flows and avoid double migrations
 *
 */
public class AccountEvnMigIdHolder {

    private final List<String> migratedAccountNumbers= new ArrayList<>();

    public AccountEvnMigIdHolder() {
    }

    private boolean isAlreadyMigrated(String accountNumber) {
        return this.migratedAccountNumbers.contains(accountNumber);
    }

    /**
     * Synchronized checker for account ids
     * @param accountNumber
     * @return true if account NOT migrated
     */
    public synchronized boolean checkIdAndMigrate(String accountNumber) {
        if (!isAlreadyMigrated(accountNumber)) {
            this.migratedAccountNumbers.add(accountNumber);
            return true;
        } else {
            return false;
        }
    }

}
