/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2011 21:56:23
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Modell-Klasse zur Abbildung der Klärfälle, die durch die automatische Rangierungfreigabe erstellt werden.
 */
@Entity
@Table(name = "T_RANGIERUNG_FREIGABE_INFO")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_RANGIERUNG_FREIGABE_INFO_0", allocationSize = 1)
public class RangierungFreigabeInfo extends AbstractCCIDModel {

    private Long rangierId;
    private Long auftragId;
    private String info;
    private Boolean inBearbeitung;
    private Date dateW;

    @Column(name = "RANGIER_ID")
    @NotNull
    public Long getRangierId() {
        return rangierId;
    }

    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "INFO")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "IN_BEARBEITUNG")
    public Boolean getInBearbeitung() {
        return inBearbeitung;
    }

    public void setInBearbeitung(Boolean inBearbeitung) {
        this.inBearbeitung = inBearbeitung;
    }

    @Column(name = "DATEW")
    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

}


