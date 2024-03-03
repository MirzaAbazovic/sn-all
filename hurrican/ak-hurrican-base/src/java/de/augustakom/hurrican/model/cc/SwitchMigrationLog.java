/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2015
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 * Kopfzeile fuer eine Switch Umstellung.
 *
 *
 */
@Entity
@Table(name = "T_SWITCH_MIGRATION_LOG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_SWITCH_MIGRATION_LOG_0", allocationSize = 1)
public class SwitchMigrationLog extends AbstractCCIDModel {

    private static final long serialVersionUID = -6306077690487097942L;

    private String bearbeiter;
    private Date migrateTime;
    private HWSwitch oldSwitch;
    private HWSwitch newSwitch;
    private Date cpsExecTime;

    @Column(name = "BEARBEITER", nullable = false)
    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Column(name = "MIGRATE_TIME", nullable = false)
    public Date getMigrateTime() {
        return migrateTime;
    }

    public void setMigrateTime(Date migrateTime) {
        this.migrateTime = migrateTime;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "OLD_HW_SWITCH_ID")
    public HWSwitch getOldSwitch() {
        return oldSwitch;
    }

    public void setOldSwitch(HWSwitch oldSwitch) {
        this.oldSwitch = oldSwitch;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "NEW_HW_SWITCH_ID")
    public HWSwitch getNewSwitch() {
        return newSwitch;
    }

    public void setNewSwitch(HWSwitch newSwitch) {
        this.newSwitch = newSwitch;
    }

    @Column(name = "CPS_EXEC_TIME", nullable = false)
    public Date getCpsExecTime() {
        return cpsExecTime;
    }

    public void setCpsExecTime(Date cpsExecTime) {
        this.cpsExecTime = cpsExecTime;
    }
}