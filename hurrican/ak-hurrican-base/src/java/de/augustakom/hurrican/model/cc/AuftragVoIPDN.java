/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2008 09:02:02
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.mnet.common.guava.base.FunctionWithNonNullArgument;

/**
 * Modell beinhaltet Zusatzdaten Passwort und Rufnummer für einen VoIP-Auftrag.
 */
@Entity
@Table(name = "T_AUFTRAG_VOIP_DN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_VOIP_DN_0", allocationSize = 1)
public class AuftragVoIPDN extends AbstractCCIDModel implements CCAuftragModel {
    private static final long serialVersionUID = 6860338650127888373L;

    /**
     * Konstante definiert die notwendige Laenge vom SIP-Passwort.
     */
    public static final int PASSWORD_LENGTH = 12;

    private static final Ordering<VoipDnPlan> GUELTIG_AB_ASC_ORDERING = Ordering.natural()
            .onResultOf(new FunctionWithNonNullArgument<VoipDnPlan, Comparable>() {
                @Override
                public Comparable apply(@Nonnull final VoipDnPlan input) {
                    return input.getGueltigAb();
                }
            });

    private Long auftragId;
    private String sipPassword;
    private Long dnNoOrig;
    private Reference sipDomain;
    private List<VoipDnPlan> rufnummernplaene = Lists.newArrayList();

    @CheckForNull
    public VoipDnPlan getActiveRufnummernplan(@Nonnull final Date when) {
        VoipDnPlan currentlyActivePlan = null;
        for (final VoipDnPlan voipDnPlan : GUELTIG_AB_ASC_ORDERING.sortedCopy(this.rufnummernplaene)) {
            if (voipDnPlan.getGueltigAb().getTime() <= when.getTime()) {
                currentlyActivePlan = voipDnPlan;
            }
        }
        return currentlyActivePlan;
    }

    @Nonnull
    public List<VoipDnPlan> getActiveAndFutureRufnummernplaene(@Nonnull Date when) {
        List<VoipDnPlan> returnValues = new ArrayList<>();
        for (final VoipDnPlan voipDnPlan : GUELTIG_AB_ASC_ORDERING.reverse().sortedCopy(this.rufnummernplaene)) {
            if (voipDnPlan.getGueltigAb().getTime() > when.getTime()) {
                returnValues.add(voipDnPlan);
            }
            else if (voipDnPlan.getGueltigAb().getTime() <= when.getTime()) {
                returnValues.add(voipDnPlan);
                break;
            }
        }
        return returnValues;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "auftrag_voip_dn_id", referencedColumnName = "id", nullable = false)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<VoipDnPlan> getRufnummernplaene() {
        return rufnummernplaene;
    }

    protected void setRufnummernplaene(final List<VoipDnPlan> rufnummernplaene) {
        this.rufnummernplaene = rufnummernplaene;
    }

    public void addRufnummernplan(final VoipDnPlan rufnrPlan) {
        rufnummernplaene.add(rufnrPlan);
    }

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

    @Column(name = "SIP_PASSWORD")
    @NotNull
    public String getSipPassword() {
        return sipPassword;
    }

    public void setSipPassword(String sipPassword) {
        this.sipPassword = sipPassword;
    }

    @Column(name = "DN__NO")
    @NotNull
    public Long getDnNoOrig() {
        return dnNoOrig;
    }

    public void setDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SIP_DOMAIN_REF_ID", nullable = true)
    public Reference getSipDomain() {
        return sipDomain;
    }

    public void setSipDomain(Reference sipDomain) {
        setChanged();
        this.sipDomain = sipDomain;
        notifyObservers();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((auftragId == null) ? 0 : auftragId.hashCode());
        result = (prime * result) + ((dnNoOrig == null) ? 0 : dnNoOrig.hashCode());
        result = (prime * result) + ((sipDomain == null) ? 0 : sipDomain.hashCode());
        result = (prime * result) + ((sipPassword == null) ? 0 : sipPassword.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AuftragVoIPDN other = (AuftragVoIPDN) obj;
        if (auftragId == null) {
            if (other.auftragId != null) {
                return false;
            }
        }
        else if (!auftragId.equals(other.auftragId)) {
            return false;
        }
        if (dnNoOrig == null) {
            if (other.dnNoOrig != null) {
                return false;
            }
        }
        else if (!dnNoOrig.equals(other.dnNoOrig)) {
            return false;
        }
        if (sipDomain == null) {
            if (other.sipDomain != null) {
                return false;
            }
        }
        else if (!sipDomain.equals(other.sipDomain)) {
            return false;
        }
        if (sipPassword == null) {
            if (other.sipPassword != null) {
                return false;
            }
        }
        else if (!sipPassword.equals(other.sipPassword)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format("AuftragVoIPDN [auftragId=%s, sipPassword=%s, dnNoOrig=%s, sipDomain=%s]",
                        auftragId, sipPassword, dnNoOrig, sipDomain);
    }

} // end
