/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 08:02:56
 */
package de.augustakom.authentication.model;


/**
 * Modell zur Abbildung der Beziehung zwischen AKUser- und AKAccount-Objekten. <br>
 *
 *
 */
public class AKUserAccount extends AbstractAuthenticationModel {

    private Long id = null;
    private Long userId = null;
    private Long accountId = null;
    private Long dbId = null;

    /**
     * @return Returns the accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the userId.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId The userId to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the dbId.
     */
    public Long getDbId() {
        return dbId;
    }

    /**
     * @param dbId The dbId to set.
     */
    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }
}
