/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 13:39:44
 */
package de.mnet.migration.common;


public interface MigrationControllerTransactionStatusAware {
    public void transactionIsCommit();

    public void transactionIsRollback();

}
