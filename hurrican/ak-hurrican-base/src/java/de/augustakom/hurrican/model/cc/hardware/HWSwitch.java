/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 14:33:42
 */
package de.augustakom.hurrican.model.cc.hardware;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Repraesentiert einen Switch.
 *
 *
 * @since Release 10
 */
@Entity
@Table(name = "T_HW_SWITCH")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_SWITCH_0", allocationSize = 1)
public class HWSwitch extends AbstractCCIDModel {

    public static final String SWITCH_MUC01 = "MUC01";

    public static final String SWITCH_MUC02 = "MUC02";

    public static final String SWITCH_MUC03 = "MUC03";

    public static final String SWITCH_MUC06 = "MUC06";

    public static final String SWITCH_AUG01 = "AUG01";

    public static final String SWITCH_AUG02 = "AUG02";

    public static final String SWITCH_NBG01 = "NBG01";

    /**
     * Name des Switches z.B. AUG01 oder MUC06
     */
    private String name;

    private HWSwitchType type;

    @Column(name = "NAME")
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 50)
    @NotNull
    public HWSwitchType getType() {
        return type;
    }

    public void setType(HWSwitchType type) {
        this.type = type;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (!(obj instanceof HWSwitch)) {
            return false;
        }
        HWSwitch other = (HWSwitch) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        return type == other.type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("HWSwitch [name=%s, type=%s]", name, type);
    }

}
