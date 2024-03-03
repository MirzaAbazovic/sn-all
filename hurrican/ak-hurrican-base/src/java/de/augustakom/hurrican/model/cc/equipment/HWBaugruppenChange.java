/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 11:11:20
 */
package de.augustakom.hurrican.model.cc.equipment;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;


/**
 * Modell fuer die Planung einer Baugruppen-Aenderung.
 */
@Entity
@Table(name = "T_HW_BG_CHANGE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_0", allocationSize = 1)
public class HWBaugruppenChange extends AbstractCCIDModel {

    public enum ChangeType {
        // refID, mehrfachauswahlQuellbaugruppe, mehrfachauswahlZielbaugruppe

        /**
         * Reference-ID fuer den Typ "Baugruppentyp aendern".
         */
        CHANGE_BG_TYPE(22100L, false, false),
        /**
         * Reference-ID fuer den Typ "einfacher Kartenwechsel".
         */
        CHANGE_CARD(22101L, false, false),
        /**
         * Reference-ID fuer den Typ "Karten zusammenlegen".
         */
        MERGE_CARDS(22102L, true, true),
        /**
         * Reference-ID fuer den Typ "DSLAM Splitt".
         */
        DSLAM_SPLIT(22103L, false, false),
        /**
         * Reference-ID fuer den Typ "DLU Umzug".
         */
        DLU_CHANGE(22104L, false, false),
        /**
         * Reference-ID fuer den Typ "Portkonzentration"
         */
        PORT_CONCENTRATION(22105L, true, true);

        private final Long refId;

        private final boolean mehrfachauswahlQuellbaugruppe;
        private final boolean mehrfachauswahlZielbaugruppe;

        ChangeType(Long refId, boolean mehrfachauswahlQuellbaugruppe, boolean mehrfachauswahlZielbaugruppe) {
            this.refId = refId;
            this.mehrfachauswahlQuellbaugruppe = mehrfachauswahlQuellbaugruppe;
            this.mehrfachauswahlZielbaugruppe = mehrfachauswahlZielbaugruppe;
        }

        public Long refId() {
            return refId;
        }

        public boolean isMehrfachauswahlQuellbaugruppe() {
            return mehrfachauswahlQuellbaugruppe;
        }

        public boolean isMehrfachauswahlZielbaugruppe() {
            return mehrfachauswahlZielbaugruppe;
        }
    }

    public enum ChangeState {
        /**
         * Reference-ID fuer den Status "in Planung".
         */
        CHANGE_STATE_PLANNING(22150L),
        /**
         * Reference-ID fuer den Status "vorbereitet".
         */
        CHANGE_STATE_PREPARED(22151L),
        /**
         * Reference-ID fuer den Status "storniert".
         */
        CHANGE_STATE_CANCELLED(22152L),
        /**
         * Reference-ID fuer den Status "ausgefuehrt".
         */
        CHANGE_STATE_EXECUTED(22153L),
        /**
         * Reference-ID fuer den Status "geschlossen".
         */
        CHANGE_STATE_CLOSED(22154L);

        private final Long refId;

        ChangeState(Long refId) {
            this.refId = refId;
        }

        public Long refId() {
            return refId;
        }
    }

    public static final String PLANNED_DATE = "plannedDate";
    private Date plannedDate;

    public static final String HVT_STANDORT = "hvtStandort";
    public static final String HVT_STANDORT_GRUPPEN_ID = HVT_STANDORT + "." + HVTStandort.HVT_GRUPPE_ID;
    private HVTStandort hvtStandort;

    public static final String CHANGE_TYPE = "changeType";
    public static final String CHANGE_TYPE_NAME = CHANGE_TYPE + "." + Reference.STR_VALUE;
    private Reference changeType;

    public static final String CHANGE_STATE = "changeState";
    public static final String CHANGE_STATE_NAME = CHANGE_STATE + "." + Reference.STR_VALUE;
    private Reference changeState;

    public static final String PLANNED_FROM = "plannedFrom";
    private String plannedFrom;

    public static final String EXECUTED_AT = "executedAt";
    private Date executedAt;

    public static final String EXECUTED_FROM = "executedFrom";
    private String executedFrom;

    public static final String CANCELLED_AT = "cancelledAt";
    private Date cancelledAt;

    public static final String CANCELLED_FROM = "cancelledFrom";
    private String cancelledFrom;

    public static final String CLOSED_AT = "closedAt";
    private Date closedAt;

    public static final String CLOSED_FROM = "closedFrom";
    private String closedFrom;

    public static final String PHYSIKTYP_NEW = "physikTypNew";
    public static final String PHYSIKTYP_NEW_ID = PHYSIKTYP_NEW + "." + PhysikTyp.ID;
    private PhysikTyp physikTypNew;

    private Set<HWBaugruppenChangePort2Port> port2Port = new HashSet<HWBaugruppenChangePort2Port>();
    private Set<HWBaugruppenChangeBgType> hwBgChangeBgType = new HashSet<HWBaugruppenChangeBgType>();
    private Set<HWBaugruppenChangeCard> hwBgChangeCard = new HashSet<HWBaugruppenChangeCard>();
    private Set<HWBaugruppenChangeDslamSplit> hwBgChangeDslamSplit = new HashSet<HWBaugruppenChangeDslamSplit>();
    private Set<HWBaugruppenChangeDlu> hwBgChangeDlu = new HashSet<HWBaugruppenChangeDlu>();


    /**
     * Gibt an, ob fuer den aktuellen HWBgChange-Typ ein Port-2-Port Mapping notwendig ist. <br> Dies ist z.B. beim
     * RDLU-Schwenk nicht notwendig.
     *
     * @return {@code true} wenn ein Port-2-Port Mapping notwendig ist, sonst {@code false}
     */
    public boolean needsPort2PortMapping() {
        return (isChangeType(ChangeType.DLU_CHANGE)) ? false : true;
    }


    /**
     * Prueft, ob die Planung in den Status {@code PREPARED} ueberfuehrt werden darf. Dies ist dann der Fall, wenn der
     * aktuelle Status {@code PLANNING} ist.
     *
     * @return
     */
    @Transient
    public boolean isPreparingAllowed() {
        return NumberTools.equal(getChangeState().getId(), ChangeState.CHANGE_STATE_PLANNING.refId);
    }


    /**
     * Prueft, ob die Planung ausgefuehrt werden darf. Dies ist dann der Fall, wenn der aktuelle Status {@code PREPARED}
     * ist.
     *
     * @return
     */
    @Transient
    public boolean isExecuteAllowed() {
        return NumberTools.equal(getChangeState().getId(), ChangeState.CHANGE_STATE_PREPARED.refId);
    }


    /**
     * Prueft, ob die Planung abgebrochen / storniert werden darf. Dies ist dann der Fall, wenn der aktuelle Status
     * {@code PLANNING} oder {@code PREPARED} ist.
     *
     * @return
     */
    @Transient
    public boolean isCancelAllowed() {
        return NumberTools.isIn(getChangeState().getId(), new Number[] {
                ChangeState.CHANGE_STATE_PLANNING.refId,
                ChangeState.CHANGE_STATE_PREPARED.refId });
    }


    /**
     * Prueft, ob die Planung geschlossen werden darf. Dies ist dann der Fall, wenn der aktuelle Status {@code EXECUTED}
     * ist.
     *
     * @return
     */
    @Transient
    public boolean isCloseAllowed() {
        return NumberTools.equal(getChangeState().getId(), ChangeState.CHANGE_STATE_EXECUTED.refId);
    }


    /**
     * Ueberprueft, ob der aktuelle Aenderungstyp mit dem angegebenen Typ {@code changeType} uebereinstimmt.
     *
     * @param changeType
     * @return
     */
    public boolean isChangeType(ChangeType changeType) {
        return (changeType != null) ? NumberTools.equal(changeType.refId, getChangeType().getId()) : false;
    }

    @Transient
    public ChangeType getChangeTypeValue() {
        for (ChangeType type : ChangeType.values()) {
            if (changeType.getId().equals(type.refId())) {
                return type;
            }
        }
        throw new IllegalStateException("Keine gueltiger ChangeTyp für Reference id=" + changeType.getId());
    }

    @Column(name = "PLANNED_DATE")
    @NotNull
    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HVT_STANDORT_ID", nullable = false)
    public HVTStandort getHvtStandort() {
        return hvtStandort;
    }

    public void setHvtStandort(HVTStandort hvtStandort) {
        this.hvtStandort = hvtStandort;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANGE_TYPE_REF_ID", nullable = false)
    public Reference getChangeType() {
        return changeType;
    }

    public void setChangeType(Reference changeType) {
        this.changeType = changeType;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANGE_STATE_REF_ID", nullable = false)
    public Reference getChangeState() {
        return changeState;
    }

    public void setChangeState(Reference changeState) {
        this.changeState = changeState;
    }

    @Column(name = "PLANNED_FROM")
    @NotNull
    public String getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(String plannedFrom) {
        this.plannedFrom = plannedFrom;
    }

    @Column(name = "EXECUTED_AT")
    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    @Column(name = "EXECUTED_FROM")
    public String getExecutedFrom() {
        return executedFrom;
    }

    public void setExecutedFrom(String executedFrom) {
        this.executedFrom = executedFrom;
    }

    @Column(name = "CANCELLED_AT")
    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    @Column(name = "CANCELLED_FROM")
    public String getCancelledFrom() {
        return cancelledFrom;
    }

    public void setCancelledFrom(String cancelledFrom) {
        this.cancelledFrom = cancelledFrom;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BG_CHANGE_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppenChangePort2Port> getPort2Port() {
        return port2Port;
    }

    public void setPort2Port(Set<HWBaugruppenChangePort2Port> port2Port) {
        this.port2Port = port2Port;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BG_CHANGE_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppenChangeBgType> getHwBgChangeBgType() {
        return hwBgChangeBgType;
    }

    public void setHwBgChangeBgType(Set<HWBaugruppenChangeBgType> hwBgChangeBgType) {
        this.hwBgChangeBgType = hwBgChangeBgType;
    }

    public void addHwBgChangeBgTyp(HWBaugruppenChangeBgType toAdd) {
        hwBgChangeBgType.add(toAdd);
    }

    /**
     * Entfernt die Baugruppe mit der angegebenen ID aus der Liste der verlinkten Quell-Baugruppen.
     *
     * @param hwBaugruppenId
     */
    public HWBaugruppenChangeBgType removeHWBaugruppenChangeBgType(Long hwBaugruppenId) {
        HWBaugruppenChangeBgType removedObject = null;
        if (hwBgChangeBgType != null) {
            Iterator<HWBaugruppenChangeBgType> iterator = hwBgChangeBgType.iterator();
            while (iterator.hasNext()) {
                HWBaugruppenChangeBgType toRemove = iterator.next();
                if (NumberTools.equal(toRemove.getHwBaugruppe().getId(), hwBaugruppenId)) {
                    removedObject = toRemove;
                    hwBgChangeBgType.remove(toRemove);
                    break;
                }
            }
        }
        return removedObject;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BG_CHANGE_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppenChangeCard> getHwBgChangeCard() {
        return hwBgChangeCard;
    }

    public void setHwBgChangeCard(Set<HWBaugruppenChangeCard> hwBgChangeCard) {
        this.hwBgChangeCard = hwBgChangeCard;
    }

    public void addHWBaugruppenChangeCard(HWBaugruppenChangeCard toAdd) {
        hwBgChangeCard.add(toAdd);
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BG_CHANGE_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppenChangeDslamSplit> getHwBgChangeDslamSplit() {
        return hwBgChangeDslamSplit;
    }

    public void setHwBgChangeDslamSplit(Set<HWBaugruppenChangeDslamSplit> hwBgChangeDslamSplit) {
        this.hwBgChangeDslamSplit = hwBgChangeDslamSplit;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BG_CHANGE_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppenChangeDlu> getHwBgChangeDlu() {
        return hwBgChangeDlu;
    }

    public void setHwBgChangeDlu(Set<HWBaugruppenChangeDlu> hwBgChangeDlu) {
        this.hwBgChangeDlu = hwBgChangeDlu;
    }

    @Column(name = "CLOSED_AT")
    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    @Column(name = "CLOSED_FROM")
    public String getClosedFrom() {
        return closedFrom;
    }

    public void setClosedFrom(String closedFrom) {
        this.closedFrom = closedFrom;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PHYSIKTYP_ID_NEW")
    public PhysikTyp getPhysikTypNew() {
        return physikTypNew;
    }

    public void setPhysikTypNew(PhysikTyp physikTypNew) {
        this.physikTypNew = physikTypNew;
    }


    /**
     * Ermittelt die Baugruppen, die abgeloest werden sollen.
     */
    @Transient
    public List<HWBaugruppe> getHWBaugruppen4ChangeCard() throws IllegalArgumentException {
        if (!isChangeType(ChangeType.CHANGE_CARD) && !isChangeType(ChangeType.MERGE_CARDS) && !isChangeType(ChangeType.PORT_CONCENTRATION)) {
            throw new IllegalArgumentException(
                    "Bei dem aktuellen Typ koennen keine Baugruppen fuer einen Kartenwechsel ermittelt werden!");
        }

        List<HWBaugruppe> baugruppen = new ArrayList<HWBaugruppe>();
        if (getHwBgChangeCard() != null) {
            HWBaugruppenChangeCard changeCard = getHwBgChangeCard().iterator().next();
            if ((changeCard.getHwBaugruppenSource() != null) && !changeCard.getHwBaugruppenSource().isEmpty()) {
                baugruppen.addAll(changeCard.getHwBaugruppenSource());
                return baugruppen;
            }
            else {
                throw new IllegalArgumentException("Es sind keine Baugruppen angegeben, die abgeloest werden sollen!");
            }
        }
        else {
            throw new IllegalArgumentException("Es sind keine Baugruppen angegeben!");
        }
    }

}


