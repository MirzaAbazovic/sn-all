/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2012 08:57:06
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.AuftragAktionAwareModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modelklasse zur Protokollierung von Wholesale-Aktionen.
 */
@Entity
@Table(name = "T_AUFTRAG_AKTION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_AKTION_0", allocationSize = 1)
public class AuftragAktion extends AbstractCCIDModel implements CCAuftragModel {

    private static final long serialVersionUID = 5087372071685235613L;

    public static enum AktionType {
        // Achtung: Moegliche Werte per Constraint in DB aufgezaehlt und max. 20 Zeichen
        MODIFY_PORT,
        CANCEL_MODIFY_PORT
    }

    /**
     * Auftrag fuer diese Aktion
     */
    private Long auftragId;

    /**
     * Art der Action
     */
    private AktionType action;

    /**
     * Wunschtermin für Schaltung
     */
    private LocalDate desiredExecutionDate;

    /**
     * Aktion wurde abgesagt (gecancelt) gemacht
     */
    private Boolean cancelled = Boolean.FALSE;

    /**
     * Id des vorherigen Auftrags im Falle eines Portwechsel (Portwechsel fuehrt zu neuem Auftrag)
     */
    private Long previousAuftragId;


    public boolean isAuftragAktionAddFor(AuftragAktionAwareModel awareModel) {
        return awareModel != null && NumberTools.equal(getId(), awareModel.getAuftragAktionsIdAdd());
    }

    public boolean isAuftragAktionRemoveFor(AuftragAktionAwareModel awareModel) {
        return awareModel != null && NumberTools.equal(getId(), awareModel.getAuftragAktionsIdRemove());
    }


    @Column(name = "PREV_AUFTRAG_ID", columnDefinition = "number(19)")
    @Nullable
    public Long getPreviousAuftragId() {
        return previousAuftragId;
    }

    public void setPreviousAuftragId(@Nullable Long previousAuftragId) {
        this.previousAuftragId = previousAuftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "AUFTRAG_ID")
    @NotNull
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAction(AktionType action) {
        this.action = action;
    }

    @Column(name = "AKTION", length = 20, columnDefinition = "varchar2(20)")
    @Enumerated(EnumType.STRING)
    @NotNull
    public AktionType getAction() {
        return action;
    }

    public void setDesiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
    }

    @Column(name = "DESIRED_EXECUTION_DATE")
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getDesiredExecutionDate() {
        return desiredExecutionDate;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Column(name = "CANCELLED")
    public Boolean isCancelled() {
        return cancelled;
    }

    @Transient
    public boolean isPortChanged() {
        return this.previousAuftragId != null;
    }
}
