/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.11.13 07:43
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
@Entity
@Table(name = "T_AUFTRAG_VOIP_DN_PLAN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_VOIP_DN_PLAN", allocationSize = 1)
@ObjectsAreNonnullByDefault
public class VoipDnPlan extends AbstractCCIDModel {

    private static final long serialVersionUID = 74207201315932650L;

    @CheckForNull
    private Date gueltigAb;
    private List<VoipDnBlock> voipDnBlocks = Lists.newArrayList();

    private String sipLogin;
    private String sipHauptrufnummer;

    @Column(name = "GUELTIG_AB", nullable = false)
    @NotNull
    public Date getGueltigAb() {
        return (gueltigAb == null) ? null : new Date(gueltigAb.getTime());
    }

    public void setGueltigAb(final Date gueltigAb) {
        this.gueltigAb = new Date(gueltigAb.getTime());
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "T_VOIP_DNPLAN_BLOCK", joinColumns = @JoinColumn(name = "voip_dn_plan_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    @NotEmpty
    public List<VoipDnBlock> getVoipDnBlocks() {
        return voipDnBlocks;
    }

    protected void setVoipDnBlocks(final List<VoipDnBlock> voipDnBlocks) {
        this.voipDnBlocks = voipDnBlocks;
    }

    public void addVoipDnBlock(final VoipDnBlock voipDnBlock) {
        this.voipDnBlocks.add(voipDnBlock);
    }

    protected void clearVoipDnBlocks() {
        this.voipDnBlocks = Lists.newArrayList();
    }

    @Column(name = "SIP_LOGIN")
    public String getSipLogin() {
        return sipLogin;
    }

    public void setSipLogin(String sipLogin) {
        this.sipLogin = sipLogin;
    }

    @Column(name = "SIP_HAUPTRUFNUMMER")
    public String getSipHauptrufnummer() {
        return sipHauptrufnummer;
    }

    public void setSipHauptrufnummer(String sipHauptrufnummer) {
        this.sipHauptrufnummer = sipHauptrufnummer;
    }
}
