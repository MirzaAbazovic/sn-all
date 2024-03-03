/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 15:38:36
 */
package de.mnet.wita.model;

import javax.persistence.*;
import com.google.common.base.Function;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Mapping Objekt zwischen UserTask und AuftragDaten - verwendet, um wiederholtes laden der AuftragDaten zu vermeiden.
 */
@Entity
@Table(name = "T_USER_TASK_2_AUFTRAGDATEN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_USER_TASK_2_AUFTRAGDATEN_0", allocationSize = 1)
public class UserTask2AuftragDaten extends AbstractCCIDModel {

    private static final long serialVersionUID = -2780695133763580231L;

    public static final Function<UserTask2AuftragDaten, Long> GET_CB_ID = new Function<UserTask2AuftragDaten, Long>() {

        @Override
        public Long apply(UserTask2AuftragDaten input) {
            return input.getCbId();
        }
    };

    public static final Function<UserTask2AuftragDaten, Long> GET_AUFTRAG_ID = new Function<UserTask2AuftragDaten, Long>() {

        @Override
        public Long apply(UserTask2AuftragDaten input) {
            return input.getAuftragId();
        }
    };

    private Long auftragId;
    private Long cbId;

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "CB_ID")
    public Long getCbId() {
        return cbId;
    }

    public void setCbId(Long cbId) {
        this.cbId = cbId;
    }

    @Override
    public String toString() {
        return "UserTask2AuftragDaten [auftragId=" + auftragId + ", cbId=" + cbId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((auftragId == null) ? 0 : auftragId.hashCode());
        result = (prime * result) + ((cbId == null) ? 0 : cbId.hashCode());
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
        UserTask2AuftragDaten other = (UserTask2AuftragDaten) obj;
        if (auftragId == null) {
            if (other.auftragId != null) {
                return false;
            }
        }
        else if (!auftragId.equals(other.auftragId)) {
            return false;
        }
        if (cbId == null) {
            if (other.cbId != null) {
                return false;
            }
        }
        else if (!cbId.equals(other.cbId)) {
            return false;
        }
        return true;
    }

}
