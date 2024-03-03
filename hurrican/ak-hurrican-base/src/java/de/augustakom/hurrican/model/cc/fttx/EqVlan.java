/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.04.2012 16:48:08
 */
package de.augustakom.hurrican.model.cc.fttx;

import static de.augustakom.common.tools.lang.DateTools.*;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.builder.EqualsBuilder;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.AuftragAktionAwareModel;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * VLAN Konfiguration eines Equipments für Fttx.
 *
 *
 */
@Entity
@Table(name = "T_EQ_VLAN")
@ObjectsAreNonnullByDefault
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EQ_VLAN_0", allocationSize = 1)
public class EqVlan extends AbstractCCIDModel implements HistoryModel, AuftragAktionAwareModel {

    public static final String EQ_ID = "equipmentId";

    private Long equipmentId;
    private CvlanServiceTyp cvlanTyp;
    private Integer cvlan;
    private Integer svlanEkp;
    private Integer svlanOlt;
    private Integer svlanMdu;
    private Date gueltigVon;
    private Date gueltigBis;
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;

    @NotNull
    @Column(name = "EQUIPMENT_ID", nullable = false)
    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "CVLAN_TYP")
    public CvlanServiceTyp getCvlanTyp() {
        return cvlanTyp;
    }

    @Transient
    @Nullable
    public CvlanServiceTyp.CVlanType getType() {
        return cvlanTyp == null ? null : cvlanTyp.getType();
    }

    public void setCvlanTyp(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
    }

    @NotNull
    @Column(name = "CVLAN", nullable = false)
    public Integer getCvlan() {
        return cvlan;
    }

    public void setCvlan(Integer cvlan) {
        this.cvlan = cvlan;
    }

    @Column(name = "SVLAN_EKP")
    @Nullable
    public Integer getSvlanEkp() {
        return svlanEkp;
    }

    public void setSvlanEkp(Integer svlanEkp) {
        this.svlanEkp = svlanEkp;
    }

    @NotNull
    @Column(name = "SVLAN_OLT", nullable = false)
    public Integer getSvlanOlt() {
        return svlanOlt;
    }

    public void setSvlanOlt(Integer svlanOlt) {
        this.svlanOlt = svlanOlt;
    }

    @NotNull
    @Column(name = "SVLAN_MDU", nullable = false)
    public Integer getSvlanMdu() {
        return svlanMdu;
    }

    public void setSvlanMdu(Integer svlanMdu) {
        this.svlanMdu = svlanMdu;
    }

    @NotNull
    @Column(name = "GUELTIG_VON", nullable = false)
    @Override
    public Date getGueltigVon() {
        return gueltigVon;
    }

    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    @NotNull
    @Column(name = "GUELTIG_BIS", nullable = false)
    @Override
    public Date getGueltigBis() {
        return gueltigBis;
    }

    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    @Column(name = "AKTIONS_ID_ADD")
    @Nullable
    @Override
    public Long getAuftragAktionsIdAdd() {
        return auftragAktionsIdAdd;
    }

    @Override
    public void setAuftragAktionsIdAdd(@Nullable Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
    }

    @Column(name = "AKTIONS_ID_REMOVE")
    @Nullable
    @Override
    public Long getAuftragAktionsIdRemove() {
        return auftragAktionsIdRemove;
    }

    @Override
    public void setAuftragAktionsIdRemove(@Nullable Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
    }

    @Transient
    public boolean isValidAt(final Date when) {
        return DateTools.isDateBeforeOrEqual(this.getGueltigVon(), when)
                && DateTools.isDateAfter(this.getGueltigBis(), when);
    }

    public boolean equalsAll(EqVlan other) {
        if (other == null) {
            return false;
        }
        return new EqualsBuilder()
                .append(equipmentId, other.equipmentId)
                .append(cvlanTyp, other.cvlanTyp)
                .append(cvlan, other.cvlan)
                .append(svlanEkp, other.svlanEkp)
                .append(svlanOlt, other.svlanOlt)
                .append(svlanMdu, other.svlanMdu)
                .isEquals();
    }

    @Override
    public String toString() {
        return "EqVlan [id=" + getId() + ", equipmentId=" + equipmentId + ", cvlanTyp=" + cvlanTyp + ", cvlan=" + cvlan
                + ", svlanEkp="
                + svlanEkp + ", svlanOlt=" + svlanOlt + ", svlanMdu=" + svlanMdu + ", gueltigVon=" + gueltigVon
                + ", gueltigBis=" + gueltigBis + ", auftragAktionsIdAdd=" + auftragAktionsIdAdd
                + ", auftragAktionsIdRemove=" + auftragAktionsIdRemove + "]";
    }

    public static class ValidFromValidToComparator implements Comparator<EqVlan>, Serializable {
        @Override
        public int compare(@Nonnull final EqVlan o1, @Nonnull final EqVlan o2) {
            if (isDateEqual(o1.getGueltigVon(), o2.getGueltigVon())) {
                if (isDateEqual(o1.getGueltigBis(), o2.getGueltigBis())) {
                    return 0;
                }
                else {
                    return isDateBefore(o1.getGueltigBis(), o2.getGueltigBis()) ? -1 : 1;
                }
            }
            else {
                return isBefore(o1.getGueltigVon(), o2.getGueltigVon()) ? -1 : 1;
            }
        }
    }
}
