/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2006 08:39:22
 */
package de.augustakom.hurrican.model.billing;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Modell zur Abbildung eines Accounts, der einem Billing-Auftrag zugeordnet ist.
 *
 *
 */
public class Account extends AbstractBillingModel {

    private Long auftragNo = null;
    private String accountId = null;
    private String accountName = null;
    private String password = null;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Account acc = (Account) obj;
        if (getAuftragNo() != null) {
            if (!getAuftragNo().equals(acc.getAuftragNo())) {
                return false;
            }
        }
        else if (acc.getAuftragNo() != null) {
            return false;
        }

        if (getAccountId() != null) {
            if (!getAccountId().equals(acc.getAccountId())) {
                return false;
            }
        }
        else if (acc.getAccountId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(319, 53)
                .append(auftragNo)
                .append(accountId)
                .toHashCode();
    }

    /**
     * @return Returns the accountId.
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return Returns the accountName.
     */
    public String getAccountName() {
        return this.accountName;
    }

    /**
     * @param accountName The accountName to set.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * @return Returns the auftragNo.
     */
    public Long getAuftragNo() {
        return this.auftragNo;
    }

    /**
     * @param auftragNo The auftragNo to set.
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}


