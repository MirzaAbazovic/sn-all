/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2012 11:13:22
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.AuftragAktionAwareModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Zuordnung eines (Hurrican-)Auftrags zu einem {@link EkpFrameContract}s.
 */
@Entity
@Table(name = "T_AUFTRAG_2_EKP_FRAME_CONTRACT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_2_EKPFRAMECONTR_0", allocationSize = 1)
public class Auftrag2EkpFrameContract extends AbstractCCIDModel implements CCAuftragModel, AuftragAktionAwareModel {

    private Long auftragId;
    public static final String EKP_FRAME_CONTRACT = "ekpFrameContract";
    private EkpFrameContract ekpFrameContract;
    public static final String ASSIGNED_FROM = "assignedFrom";
    private LocalDate assignedFrom;
    public static final String ASSIGNED_TO = "assignedTo";
    private LocalDate assignedTo;
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;

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

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EKP_FRAME_CONTRACT_ID")
    public EkpFrameContract getEkpFrameContract() {
        return ekpFrameContract;
    }

    public void setEkpFrameContract(EkpFrameContract ekpFrameContract) {
        this.ekpFrameContract = ekpFrameContract;
    }

    @Column(name = "ASSIGNED_FROM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull
    public LocalDate getAssignedFrom() {
        return assignedFrom;
    }

    public void setAssignedFrom(LocalDate assignedFrom) {
        this.assignedFrom = assignedFrom;
    }

    @Column(name = "ASSIGNED_TO")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull
    public LocalDate getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(LocalDate assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    @Column(name = "AKTIONS_ID_ADD")
    public Long getAuftragAktionsIdAdd() {
        return auftragAktionsIdAdd;
    }

    @Override
    public void setAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
    }

    @Override
    @Column(name = "AKTIONS_ID_REMOVE")
    public Long getAuftragAktionsIdRemove() {
        return auftragAktionsIdRemove;
    }

    @Override
    public void setAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
    }

}


