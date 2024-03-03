package de.augustakom.hurrican.model.cc.sip;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * Das Set an IP-Adressen mit gleichem Startdatum.
 */
@Entity
@Table(name = "T_SIP_SBC_IP_SET")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_SIP_SBC_IP_SET_0", allocationSize = 1)
public class SipSbcIpSet extends AbstractCCIDModel {

    private static final long serialVersionUID = 5069469195508101507L;

    private Date gueltigAb;
    private List<IPAddress> sbcIps = Lists.newArrayList();

    @NotNull
    @Column(name = "GUELTIG_AB")
    public Date getGueltigAb() {
        return gueltigAb;
    }

    public void setGueltigAb(Date gueltigAb) {
        this.gueltigAb = gueltigAb;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "T_SIP_SBC_IP",
            joinColumns = @JoinColumn(name = "SIP_SBC_IP_SET_ID", referencedColumnName = "ID", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "IP_ADDRESS_ID", referencedColumnName = "ID", nullable = false))
    @Fetch(value = FetchMode.SUBSELECT)
    public List<IPAddress> getSbcIps() {
        return sbcIps;
    }

    protected void setSbcIps(final List<IPAddress> sbcIps) {
        this.sbcIps = sbcIps;
    }

    @Transient
    public void addSbcIp(final IPAddress sbcIp) {
        if (sbcIps == null) {
            sbcIps = new ArrayList<>();
        }
        sbcIps.add(sbcIp);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((gueltigAb == null) ? 0 : gueltigAb.hashCode());
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

        SipSbcIpSet other = (SipSbcIpSet) obj;
        if (gueltigAb == null) {
            if (other.gueltigAb != null) {
                return false;
            }
        }
        else if (!gueltigAb.equals(other.gueltigAb)) {
            return false;
        }
        return true;
    }
}
