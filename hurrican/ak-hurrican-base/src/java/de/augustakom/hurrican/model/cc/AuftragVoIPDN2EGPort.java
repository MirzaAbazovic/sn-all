    /*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.13 07:31
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 *
 */
@Entity
@Table(name = "T_AUFTRAG_VOIPDN_2_EG_PORT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_VOIPDN_2_EG_PORT_0", allocationSize = 1)
public class AuftragVoIPDN2EGPort extends AbstractCCIDModel {

    private EndgeraetPort egPort;
    private Long auftragVoipDnId;
    private Date validFrom;
    private Date validTo;

    @ManyToOne
    @JoinColumn(name = "egport_id", nullable = false)
    @NotNull
    public EndgeraetPort getEgPort() {
        return egPort;
    }

    public void setEgPort(EndgeraetPort egPort) {
        this.egPort = egPort;
    }

    @Column(name = "auftragvoipdn_id", nullable = false)
    @NotNull
    public Long getAuftragVoipDnId() {
        return auftragVoipDnId;
    }

    public void setAuftragVoipDnId(Long auftragVoipDnId) {
        this.auftragVoipDnId = auftragVoipDnId;
    }

    @Column(name = "aktiv_von", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Column(name = "aktiv_bis", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
}
