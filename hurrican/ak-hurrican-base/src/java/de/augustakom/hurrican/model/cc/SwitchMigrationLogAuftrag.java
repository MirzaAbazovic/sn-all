/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2015
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

/**
 * Detail einer Switch Umstellung (pro Auftrag).
 *
 *
 */
@Entity
@Table(name = "T_SWITCH_MIGRATION_LOG_AUFTRAG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_SWITCH_MIG_LOG_AUFTRAG_0", allocationSize = 1)
public class SwitchMigrationLogAuftrag extends AbstractCCIDModel {

    public static final String SWITCH_MIGRATION_LOG_ID = "switchMigrationLogId";
    private static final long serialVersionUID = -2185891678908982788L;

    private Long switchMigrationLogId;
    private Long auftragId;
    private boolean migrated;
    private boolean cpsTxRequired;
    private Long cpsTxId;
    private String message;

    @Column(name = "SWITCH_MIGRATION_LOG_ID", nullable = false)
    public Long getSwitchMigrationLogId() {
        return switchMigrationLogId;
    }

    public void setSwitchMigrationLogId(Long switchMigrationLogId) {
        this.switchMigrationLogId = switchMigrationLogId;
    }

    @Column(name = "AUFTRAG_ID", nullable = false)
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "MIGRATED")
    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    @Column(name = "CPS_TX_REQUIRED")
    public boolean isCpsTxRequired() {
        return cpsTxRequired;
    }

    public void setCpsTxRequired(boolean cpsTxRequired) {
        this.cpsTxRequired = cpsTxRequired;
    }

    @Column(name = "CPS_TX_ID")
    public Long getCpsTxId() {
        return cpsTxId;
    }

    public void setCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
    }

    @Column(name = "MESSAGE")
    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public void addMessage(String msg) {
        if (StringUtils.isBlank(msg)) {
            return;
        }
        if (this.message == null) {
            this.message = msg;
        }
        else {
            this.message = new StringBuilder(this.message).append("; ").append(msg).toString();
        }
    }
}