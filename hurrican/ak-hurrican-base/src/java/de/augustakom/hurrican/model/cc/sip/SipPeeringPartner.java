package de.augustakom.hurrican.model.cc.sip;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Die SIP Trunk Peering Partner
 */
@Entity
@Table(name = "T_SIP_PEERING_PARTNER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_SIP_PEERING_PARTNER_0", allocationSize = 1)
public class SipPeeringPartner extends AbstractCCIDModel {

    private static final long serialVersionUID = 5684202209925800460L;

    public static final String NAME = "name";

    private String name;
    private Boolean isActive;
    private List<SipSbcIpSet> sbcIpSets = Lists.newArrayList();

    /**
     * Gibt das zum Zeitpunkt {@code at} gueltige {@link SipSbcIpSet} des PeeringPartners zurueck.
     * @param at
     * @return
     */
    public Optional<SipSbcIpSet> getCurrentSbcIpSetAt(Date at) {
        final Optional<SipSbcIpSet> actualSet = getSbcIpSets()
                .stream()
                .filter(set -> DateTools.isDateBeforeOrEqual(set.getGueltigAb(), at))
                .max(Comparator.comparing(set -> set.getGueltigAb()));
        return actualSet;
    }


    @NotNull
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Column(name = "IS_ACTIVE")
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SIP_PP_ID", nullable = false)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<SipSbcIpSet> getSbcIpSets() {
        return sbcIpSets;
    }

    protected void setSbcIpSets(final List<SipSbcIpSet> sbcIpSets) {
        this.sbcIpSets = sbcIpSets;
    }

    @Transient
    public void addSbcIpSet(final SipSbcIpSet sbcIpSet) {
        if (sbcIpSets == null) {
            sbcIpSets = new ArrayList<>();
        }
        sbcIpSets.add(sbcIpSet);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((isActive == null) ? 0 : isActive.hashCode());
        return result;
    }

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

        SipPeeringPartner other = (SipPeeringPartner) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        if (isActive == null) {
            if (other.isActive != null) {
                return false;
            }
        }
        else if (!isActive.equals(other.isActive)) {
            return false;
        }
        return true;
    }
}
