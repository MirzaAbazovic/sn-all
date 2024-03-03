/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.2017
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "T_WBCI_VORABSTIMMUNG_FAX")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_VORABSTIMMUNG_FAX", allocationSize = 1)
public class WBCIVorabstimmungFax extends AbstractCCIDModel {
    private Long auftragId;
    private String vorabstimmungsId;
    private Date created;

    public WBCIVorabstimmungFax() {
        super();
    }

    public WBCIVorabstimmungFax(Long auftragId, String vorabstimmungsId) {
        super();
        this.auftragId = auftragId;
        this.vorabstimmungsId = vorabstimmungsId;
        this.created = new Date();
    }

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "VORABSTIMMUNG_ID")
    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    @Column(name = "CREATED")
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}