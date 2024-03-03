/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:49:13
 */
package de.mnet.wita.message.common.portierung;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.groups.Workflow;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_PORT_RUFNUMMERNBLOCK")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_PORT_RBLOCK_0", allocationSize = 1)
public class RufnummernBlock extends MwfEntity {

    private static final long serialVersionUID = -1434151986721950923L;

    public static final Comparator<RufnummernBlock> FACHLICHER_COMPARATOR = new Comparator<RufnummernBlock>() {
        @Override
        public int compare(RufnummernBlock arg0, RufnummernBlock arg1) {
            int compareVon = arg0.getVon().compareTo(arg1.getVon());
            if (compareVon != 0) {
                return compareVon;
            }
            return arg0.getBis().compareTo(arg1.getBis());
        }
    };

    private String von;
    private String bis;

    public void setVon(String von) {
        this.von = von;
    }

    @NotNull
    @Size(max = 6, groups = Workflow.class)
    public String getVon() {
        return von;
    }

    public void setBis(String bis) {
        this.bis = bis;
    }

    @NotNull
    @Size(max = 6, groups = Workflow.class)
    public String getBis() {
        return bis;
    }

    @Override
    public String toString() {
        return "Rufnummernblock [Bereich von: " + von + ", Bereich bis: " + bis + "]";
    }

}
