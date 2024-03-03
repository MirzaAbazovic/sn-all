/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2016
 */
package de.augustakom.hurrican.model.cc;

import com.google.common.collect.Sets;

import java.util.Date;
import java.util.Set;

/**
 * Created by marteleu on 13.12.2016.
 */
public class ProfileAuftragBuilder extends AbstractCCIDModelBuilder<ProfileAuftragBuilder, ProfileAuftrag> {
    private Long auftragId;
    private Long equipmentId;
    private String bemerkung;
    private Set<ProfileAuftragValue> profileAuftragValues = Sets.newHashSet();
    private DSLAMProfileChangeReason changeReason;
    private Date gueltigVon;
    private Date gueltigBis;
    private String userW;
    private Long version;

    public ProfileAuftragBuilder withRandomId() {
        this.id = randomLong(Integer.MAX_VALUE - 100000, Integer.MAX_VALUE);
        return this;
    }

    public ProfileAuftragBuilder withRandomEquipmentId() {
        this.equipmentId = randomLong(Integer.MAX_VALUE - 100000, Integer.MAX_VALUE);
        return this;
    }

    public ProfileAuftragBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public ProfileAuftragBuilder withBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }

    public ProfileAuftragBuilder withDSLAMProfileChangeReason(DSLAMProfileChangeReason changeReason) {
        this.changeReason = changeReason;
        return this;
    }

    public ProfileAuftragBuilder withProfileAuftragValues(Set<ProfileAuftragValue> profileAuftragValues) {
        this.profileAuftragValues = profileAuftragValues;
        return this;
    }

    public ProfileAuftragBuilder withGueltigDaten(Date gueltigVon, Date gueltigBis) {
        this.gueltigVon = gueltigVon;
        this.gueltigBis = gueltigBis;
        return this;
    }

    public ProfileAuftragBuilder withOtherDefaultValues() {
        this.userW = "testuser";
        this.version = 0L;
        return this;
    }




}