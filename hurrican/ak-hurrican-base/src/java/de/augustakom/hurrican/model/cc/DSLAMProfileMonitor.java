/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 11:37:43
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modell fuer die Tabelle T_DSLAM_PROFILE_MONITOR. Dort werden Informationen zu Auftraegen persistiert, die eine
 * gewisse Zeit ueberwacht werden muessen, um dem Auftrag das richtige DSLAM Profil zuweisen zu koennen.
 *
 *
 * @since Release 11
 */
@Entity
@Table(name = "T_DSLAM_PROFILE_MONITOR")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_DSLAM_PROFILE_MONITOR_0", allocationSize = 1)
public class DSLAMProfileMonitor extends AbstractCCIDModel implements CCAuftragModel {

    private Long auftragId;
    private Date monitoringSince;
    public static final String MONITORING_ENDS = "monitoringEnds";
    private Date monitoringEnds;
    public static final String DELETED = "deleted";
    private Boolean deleted;

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "MONITORING_SINCE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getMonitoringSince() {
        return monitoringSince;
    }

    public void setMonitoringSince(Date monitoringSince) {
        this.monitoringSince = monitoringSince;
    }

    @Column(name = "MONITORING_ENDS")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getMonitoringEnds() {
        return monitoringEnds;
    }

    public void setMonitoringEnds(Date monitoringEnds) {
        this.monitoringEnds = monitoringEnds;
    }

    @Column(name = "DELETED")
    @NotNull
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((auftragId == null) ? 0 : auftragId.hashCode());
        result = (prime * result) + ((deleted == null) ? 0 : deleted.hashCode());
        result = (prime * result) + ((monitoringEnds == null) ? 0 : monitoringEnds.hashCode());
        result = (prime * result) + ((monitoringSince == null) ? 0 : monitoringSince.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DSLAMProfileMonitor other = (DSLAMProfileMonitor) obj;
        if (auftragId == null) {
            if (other.auftragId != null) {
                return false;
            }
        }
        else if (!auftragId.equals(other.auftragId)) {
            return false;
        }
        if (deleted == null) {
            if (other.deleted != null) {
                return false;
            }
        }
        else if (!deleted.equals(other.deleted)) {
            return false;
        }
        if (monitoringEnds == null) {
            if (other.monitoringEnds != null) {
                return false;
            }
        }
        else if (!monitoringEnds.equals(other.monitoringEnds)) {
            return false;
        }
        if (monitoringSince == null) {
            if (other.monitoringSince != null) {
                return false;
            }
        }
        else if (!monitoringSince.equals(other.monitoringSince)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("AutragMonitor [auftragId=%s, monitoringSince=%s, monitoringEnds=%s, deleted=%s]", auftragId,
                monitoringSince, monitoringEnds, deleted);
    }

}
