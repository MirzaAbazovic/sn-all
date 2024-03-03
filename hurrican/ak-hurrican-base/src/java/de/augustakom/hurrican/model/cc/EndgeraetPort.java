/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2011 17:09:14
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Dieses Model repraesentiert einen Port an einem Endgeraet.
 *
 *
 * @since Release 10
 */
@Entity
@Table(name = "T_EG_PORT", uniqueConstraints = { @UniqueConstraint(columnNames = { "EG_TYPE_ID", "NO" }) })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EG_PORT_0", allocationSize = 1)
public class EndgeraetPort extends AbstractCCIDModel {

    private EGType endgeraetTyp;
    private Integer number;
    private String name;

    @Transient
    static public int getMaxDNsPerDefaultPort() {
        return 10;
    }

    /**
     * @return Returns the endgeraetTyp.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EG_TYPE_ID", nullable = true)
    public EGType getEndgeraetTyp() {
        return endgeraetTyp;
    }

    /**
     * @param endgeraetTyp The endgeraetTyp to set.
     */
    public void setEndgeraetTyp(EGType endgeraetTyp) {
        this.endgeraetTyp = endgeraetTyp;
    }

    /**
     * @return Returns the number.
     */
    @Column(name = "NO")
    @NotNull
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number The number to set.
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * @return Returns the name.
     */
    @Column(name = "NAME")
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((endgeraetTyp == null) ? 0 : endgeraetTyp.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((number == null) ? 0 : number.hashCode());
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
        EndgeraetPort other = (EndgeraetPort) obj;
        if (endgeraetTyp == null) {
            if (other.endgeraetTyp != null) {
                return false;
            }
        }
        else if (!endgeraetTyp.equals(other.endgeraetTyp)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        if (number == null) {
            if (other.number != null) {
                return false;
            }
        }
        else if (!number.equals(other.number)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("EndgeraetPort [endgeraetTyp=%s, number=%s, name=%s]", endgeraetTyp, number, name);
    }

} // end
