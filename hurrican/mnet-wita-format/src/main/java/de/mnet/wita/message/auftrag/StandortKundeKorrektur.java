/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2011 14:47:40
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.validators.groups.Workflow;

@Entity
@Table(name = "T_MWF_STANDORT_KUNDE_KORR")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_STANDORT_KUNDE_KORR_0", allocationSize = 1)
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class StandortKundeKorrektur extends AbstractStandort {

    private static final long serialVersionUID = -2194721500001861157L;

    private String gebaeudeteilName;
    private String gebaeudeteilZusatz;

    @Column(name = "GEBAEUDETEIL_NAME")
    @Size(min = 1, max = 25, groups = Workflow.class)
    public String getGebaeudeteilName() {
        return gebaeudeteilName;
    }

    public void setGebaeudeteilName(String gebaeudeteilName) {
        this.gebaeudeteilName = gebaeudeteilName;
    }

    @Column(name = "GEBAEUDETEIL_ZUSATZ")
    @Size(min = 1, max = 25, groups = Workflow.class)
    public String getGebaeudeteilZusatz() {
        return gebaeudeteilZusatz;
    }

    public void setGebaeudeteilZusatz(String gebaeudeteilZusatz) {
        this.gebaeudeteilZusatz = gebaeudeteilZusatz;
    }

    @Override
    public String toString() {
        return "StandortKundeKorrektur: [gebaeudeteilName=" + gebaeudeteilName + ", gebaeudeteilZusatz="
                + gebaeudeteilZusatz + ", toString()=" + super.toString() + "]";
    }

}
