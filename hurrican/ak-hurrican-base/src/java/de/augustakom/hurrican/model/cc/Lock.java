/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2009 15:55:39
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Ueber dieses Modell werden Sperren realisiert. Unter jedem Lock haengen evtl. mehrere LockDetails
 *
 *
 */
@SuppressWarnings("serial")
public class Lock extends AbstractCCIDModel implements DebugModel, CCAuftragModel, KundenModel {
    /**
     * Reference-ID fuer die Angabe einer abgehenden Sperre.
     */
    public static final Long REF_ID_LOCK_MODE_OUTGOING = Long.valueOf(1500);
    /**
     * Reference-ID fuer die Angabe einer Voll-Sperre.
     */
    public static final Long REF_ID_LOCK_MODE_FULL = Long.valueOf(1501);
    /**
     * Reference-ID fuer die Angabe einer Wandlung einer Teil- in eine Voll-Sperre.
     */
    public static final Long REF_ID_LOCK_MODE_CHANGE_TO_FULL = Long.valueOf(1502);
    /**
     * Reference-ID fuer die Angabe einer Entsperrung.
     */
    public static final Long REF_ID_LOCK_MODE_UNLOCK = Long.valueOf(1503);
    /**
     * Reference-ID fuer die Angabe einer Demontage.
     */
    public static final Long REF_ID_LOCK_MODE_DEMONTAGE = Long.valueOf(1504);

    /**
     * Reference-ID fuer den Sperr Status "active".
     */
    public static final Long REF_ID_LOCK_STATE_ACTIVE = Long.valueOf(1510);
    /**
     * Reference-ID fuer den Sperr Status "finished".
     */
    public static final Long REF_ID_LOCK_STATE_FINISHED = Long.valueOf(1511);

    /**
     * Reference-ID fuer den Sperr Status "finished".
     */
    public static final Long REF_ID_LOCK_REASON_INSOLVENZ = Long.valueOf(1520);
    public static final Long REF_ID_LOCK_REASON_SONSTIGES = Long.valueOf(1524);

    private Long lockModeRefId = null;
    private Long lockStateRefId = null;
    private Date createdAt = null;
    private String createdFrom = null;
    private Long kundeNo = null;
    private String debId = null;
    private Long auftragNoOrig = null;
    private Long auftragId = null;
    private Long lockReasonRefId = null;
    private String lockReasonText = null;
    private Boolean manualProvisioning = null;
    private Long parentLockId = null;

    /**
     * Prueft, ob es sich bei der Sperre um eine 'Demontage' handelt.
     *
     * @return
     */
    public boolean isDemontage() {
        return NumberTools.equal(getLockModeRefId(), REF_ID_LOCK_MODE_DEMONTAGE);
    }

    /**
     * @return the lockModeRefId
     */
    public Long getLockModeRefId() {
        return lockModeRefId;
    }

    /**
     * @param lockModeRefId the lockModeRefId to set
     */
    public void setLockModeRefId(Long lockModeRefId) {
        this.lockModeRefId = lockModeRefId;
    }

    /**
     * @return the lockStateRefId
     */
    public Long getLockStateRefId() {
        return lockStateRefId;
    }

    /**
     * @param lockStateRefId the lockStateRefId to set
     */
    public void setLockStateRefId(Long lockStateRefId) {
        this.lockStateRefId = lockStateRefId;
    }


    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }


    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the createdFrom
     */
    public String getCreatedFrom() {
        return createdFrom;
    }

    /**
     * @param createdFrom the createdFrom to set
     */
    public void setCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
    }

    /**
     * @return the customerNo
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo the kundeNo to set
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return the debId
     */
    public String getDebId() {
        return debId;
    }

    /**
     * @param debId the debId to set
     */
    public void setDebId(String debId) {
        this.debId = debId;
    }

    /**
     * @return the auftragNoOrig
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig the auftragNoOrig to set
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return the auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId the auftragId to set
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return the lockReasonRefId
     */
    public Long getLockReasonRefId() {
        return lockReasonRefId;
    }

    /**
     * @param lockReasonRefId the lockReasonRefId to set
     */
    public void setLockReasonRefId(Long lockReasonRefId) {
        this.lockReasonRefId = lockReasonRefId;
    }

    /**
     * @return the lockReasonText
     */
    public String getLockReasonText() {
        return lockReasonText;
    }

    /**
     * @param lockReasonText the lockReasonText to set
     */
    public void setLockReasonText(String lockReasonText) {
        this.lockReasonText = lockReasonText;
    }

    /**
     * @return the manualProvisioning
     */
    public Boolean getManualProvisioning() {
        return manualProvisioning;
    }

    /**
     * @param manualProvisioning the manualProvisioning to set
     */
    public void setManualProvisioning(Boolean manualProvisioning) {
        this.manualProvisioning = manualProvisioning;
    }

    /**
     * @return the parentLock
     */
    public Long getParentLockId() {
        return parentLockId;
    }

    /**
     * @param parentLock the parentLock to set
     */
    public void setParentLockId(Long parentLockId) {
        this.parentLockId = parentLockId;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Lock.class.getName());
            logger.debug("  ID           : " + getId());
            logger.debug("  Lock Mode    : " + getLockModeRefId());
            logger.debug("  Lock State   : " + getLockStateRefId());
            logger.debug("  Created From : " + getCreatedFrom());
            logger.debug("  Kundennummer : " + getKundeNo());
            logger.debug("  Debitor Id   : " + getDebId());
            logger.debug("  Taifun Order No. : " + getAuftragNoOrig());
            logger.debug("  Auftrag Id : " + getAuftragId());
            logger.debug("  Lock Reason : " + getLockReasonRefId());
            logger.debug("  Lock Reason Text : " + getLockReasonText());
            logger.debug("  Manual Provisioning : " + getManualProvisioning());
            logger.debug("  Parent Lock Id : " + getParentLockId());
        }
    }

}
