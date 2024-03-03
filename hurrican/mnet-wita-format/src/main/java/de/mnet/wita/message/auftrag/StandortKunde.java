/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:39
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.StandortWithPerson;
import de.mnet.wita.validators.groups.V1;

/**
 * Abbildung des WITA Objekts 'StandortA' (Kundenstandort, an dem die Leistung ausgefuehrt wird).
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_STANDORT_KUNDE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_STANDORT_KUNDE_0", allocationSize = 1)
public class StandortKunde extends StandortWithPerson {

    private static final long serialVersionUID = 8981177390551045764L;

    private String gebaeudeteilName;
    private String gebaeudeteilZusatz;
    private String lageTAEDose;

    @Override
    public String toString() {
        return "Standort [person=" + super.toString() + ", lageTAEDose=" + lageTAEDose + "]";
    }

    @Size(min = 1, max = 25)
    public String getGebaeudeteilName() {
        return gebaeudeteilName;
    }

    public void setGebaeudeteilName(String gebaeudeteilName) {
        this.gebaeudeteilName = gebaeudeteilName;
    }

    @Size(min = 1, max = 25)
    public String getGebaeudeteilZusatz() {
        return gebaeudeteilZusatz;
    }

    public void setGebaeudeteilZusatz(String gebaeudeteilZusatz) {
        this.gebaeudeteilZusatz = gebaeudeteilZusatz;
    }

    @Size.List({
            @Size(groups = V1.class, min = 1, max = 160),
    })
    public String getLageTAEDose() {
        return lageTAEDose;
    }

    public void setLageTAEDose(String lageTAEDose) {
        this.lageTAEDose = lageTAEDose;
    }

}
