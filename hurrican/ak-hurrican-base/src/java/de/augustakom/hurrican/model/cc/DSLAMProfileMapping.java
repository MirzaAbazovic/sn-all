/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2015 11:02:43
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Modell fuer die Tabelle T_DSLAM_PROFILE_MAPPING. Dort werden Informationen persistiert, die DSLAM Profile (alt/neu)
 * einander zuweisen. Optional kann das UETV Uebertragungsverfahren im Mapping angegeben sein.
 *
 *
 */
@Entity
@Table(name = "T_DSLAM_PROFILE_MAPPING")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_DSLAM_PROFILE_MAPPING_0", allocationSize = 1)
public class DSLAMProfileMapping extends AbstractCCIDModel {

    private String profilNameAlt;
    private String profilNameNeu;
    private Uebertragungsverfahren uetv = null;

    @Column(name = "PROFIL_NAME_ALT")
    @NotNull
    public String getProfilNameAlt() {
        return profilNameAlt;
    }

    public void setProfilNameAlt(String profilNameAlt) {
        this.profilNameAlt = profilNameAlt;
    }

    @Column(name = "PROFIL_NAME_NEU")
    @NotNull
    public String getProfilNameNeu() {
        return profilNameNeu;
    }

    public void setProfilNameNeu(String profilNameNeu) {
        this.profilNameNeu = profilNameNeu;
    }

    @Column(name = "UETV")
    @Enumerated(EnumType.STRING)
    public Uebertragungsverfahren getUetv() {
        return uetv;
    }

    public void setUetv(Uebertragungsverfahren uetv) {
        this.uetv = uetv;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((profilNameAlt == null) ? 0 : profilNameAlt.hashCode());
        result = (prime * result) + ((profilNameNeu == null) ? 0 : profilNameNeu.hashCode());
        result = (prime * result) + ((uetv == null) ? 0 : uetv.hashCode());
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
        DSLAMProfileMapping other = (DSLAMProfileMapping) obj;
        if (profilNameAlt == null) {
            if (other.profilNameAlt != null) {
                return false;
            }
        }
        else if (!profilNameAlt.equals(other.profilNameAlt)) {
            return false;
        }

        if (profilNameNeu == null) {
            if (other.profilNameNeu != null) {
                return false;
            }
        }
        else if (!profilNameNeu.equals(other.profilNameNeu)) {
            return false;
        }

        if (uetv == null) {
            if (other.uetv != null) {
                return false;
            }
        }
        else if (!uetv.equals(other.uetv)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("DSLAMProfileMapping [profilNameAlt=%s, profilNameNeu=%s, uetv=%s]",
                profilNameAlt, profilNameNeu, uetv);
    }

}
