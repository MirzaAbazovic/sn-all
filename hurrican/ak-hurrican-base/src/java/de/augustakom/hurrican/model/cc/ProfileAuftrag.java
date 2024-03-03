/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 28.11.2016

 */

package de.augustakom.hurrican.model.cc;


import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Mapping von Profilwerten zu einem Auftrag/Equipment mit Historisierung
 */
@Entity
@Table(name = "T_PROFILE_AUFTRAG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROFILE_AUFTRAG_0")
@ObjectsAreNonnullByDefault
public class ProfileAuftrag extends AbstractCCHistoryUserModel implements CCAuftragModel {

    protected static final long serialVersionUID = 1L;

    private Long auftragId;
    private long equipmentId;
    private DSLAMProfileChangeReason changeReason;
    private String bemerkung;
    private Set<ProfileAuftragValue> profileAuftragValues = Sets.newHashSet();


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "T_PROFILE_AUFTRAG_VALUE", joinColumns = @JoinColumn(name = "PROFILE_AUFTRAG_ID"))
    @Fetch(FetchMode.SUBSELECT)
    public Set<ProfileAuftragValue> getProfileAuftragValues() {
        return this.profileAuftragValues;
    }

    protected void setProfileAuftragValues(Set<ProfileAuftragValue> profileAuftragValues) {
        this.profileAuftragValues = profileAuftragValues;
    }

    public void addProfileAuftragValue(final ProfileAuftragValue profileAuftragValue) {
        this.profileAuftragValues.add(profileAuftragValue);
    }

    public void removeProfileAuftragValue(final ProfileAuftragValue profileAuftragValue) {
        this.profileAuftragValues.remove(profileAuftragValue);
    }

    @Override
    @Column(name = "AUFTRAG_ID")
    @Nullable
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "EQUIPMENT_ID", nullable = false)
    @NotNull
    public long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @ManyToOne
    @JoinColumn(name = "change_reason_id", referencedColumnName = "id")
    @NotNull
    public DSLAMProfileChangeReason getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(DSLAMProfileChangeReason changeReason) {
        this.changeReason = changeReason;
    }

    @Column(name = "BEMERKUNG")
    @Nullable
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("auftragId", auftragId).
                append("equipmentId", equipmentId).
                append("changeReason", changeReason).
                append("bemerkung", bemerkung).
                append("profileAuftragValues", profileAuftragValues).
                append("gültigVon", getGueltigVon()).
                append("gültigBis", getGueltigBis()).
                append("userW", getUserW()).
                append("version", getVersion()).
                toString();
    }
}
