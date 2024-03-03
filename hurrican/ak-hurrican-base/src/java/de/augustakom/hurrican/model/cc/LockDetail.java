/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2009 16:35:15
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;

/**
 * Ueber dieses Modell werden die Details einer Sperre realisiert.
 *
 *
 */
public class LockDetail extends AbstractCCIDModel implements DebugModel {

    private Long lockId = null;
    private Long abteilungId = null;
    private Date executedAt = null;
    private String executedFrom = null;
    private Long cpsTxId = null;

    /**
     * @return the lockId
     */
    public Long getLockId() {
        return lockId;
    }

    /**
     * @param lockId the lockId to set
     */
    public void setLockId(Long lockId) {
        this.lockId = lockId;
    }

    /**
     * @return the abteilungId
     */
    public Long getAbteilungId() {
        return abteilungId;
    }

    /**
     * @param abteilungId the abteilungId to set
     */
    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    /**
     * @return the executedAt
     */
    public Date getExecutedAt() {
        return executedAt;
    }

    /**
     * @param executedAt the executedAt to set
     */
    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    /**
     * @return the executedFrom
     */
    public String getExecutedFrom() {
        return executedFrom;
    }

    /**
     * @param executedFrom the executedFrom to set
     */
    public void setExecutedFrom(String executedFrom) {
        this.executedFrom = executedFrom;
    }

    /**
     * @return Returns the cpsTxId.
     */
    public Long getCpsTxId() {
        return cpsTxId;
    }

    /**
     * @param cpsTxId The cpsTxId to set.
     */
    public void setCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + LockDetail.class.getName());
            logger.debug("  ID            : " + getId());
            logger.debug("  Lock-ID       : " + getLockId());
            logger.debug("  Abteilungs-ID : " + getAbteilungId());
            logger.debug("  Executed At   : " + getExecutedAt());
            logger.debug("  Executed From : " + getExecutedFrom());
        }
    }

}
