package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_RUFNUMMERPORTIERUNG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYP", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_RUFNUMMERPORTIERUNG_0", allocationSize = 1)
public abstract class Rufnummernportierung extends WbciEntity {

    private static final long serialVersionUID = 7281773523533203912L;

    private Portierungszeitfenster portierungszeitfenster;
    private String portierungskennungPKIauf;

    @Transient
    public abstract RufnummernportierungTyp getTyp();

    @Enumerated(EnumType.STRING)
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public Portierungszeitfenster getPortierungszeitfenster() {
        return portierungszeitfenster;
    }

    public void setPortierungszeitfenster(Portierungszeitfenster portierungszeitfenster) {
        this.portierungszeitfenster = portierungszeitfenster;
    }

    /**
     * Nur bei Vorabstimmungsanfragen, z.B. VA-KUE-RRNP!
     *
     * @return
     */
    @NotNull(groups = { V1RequestVaRrnp.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "D[0-9A-F]{3}", groups = { V1RequestVaRrnp.class },
            message = "ungültiges Format: erwartet D[NNN], wo N - eine Hexadezimalziffer (0-9 oder A-F). Beispiel: D001.")
    public String getPortierungskennungPKIauf() {
        return portierungskennungPKIauf;
    }

    public void setPortierungskennungPKIauf(String portierungskennungPKIauf) {
        this.portierungskennungPKIauf = portierungskennungPKIauf;
    }

    @Override
    public String toString() {
        return "Rufnummernportierung{" +
                "portierungszeitfenster=" + portierungszeitfenster +
                ", portierungskennungPKIauf=" + portierungskennungPKIauf +
                ", type='" + getTyp() + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
