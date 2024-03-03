package de.mnet.migration.hurrican.evn;

import de.mnet.migration.common.util.ColumnName;

/**
 * Entity for {@link AccountEvnTransformer} migration transformer
 *
 */
public class AccountEvn {

    public AccountEvn() {
    }

    public AccountEvn(String accountNumber, Boolean evnStatus) {
        this.accountNumber = accountNumber;
        this.evnStatus = evnStatus;
    }

    @ColumnName("ACCOUNT")
    public String accountNumber;

    @ColumnName("EVN_STATUS")
    public Boolean evnStatus;

}
