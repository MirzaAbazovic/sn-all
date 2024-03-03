package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;

@Entity
@DiscriminatorValue(GeschaeftsfallTyp.VA_KUE_ORN_NAME)
public class WbciGeschaeftsfallKueOrn extends WbciGeschaeftsfallKue {

    private static final long serialVersionUID = -608611350505474050L;

    private RufnummerOnkz anschlussIdentifikation;

    @Override
    @Transient
    public boolean hasRufnummerIdentification() {
        return (anschlussIdentifikation != null);
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "ANSCHLUSSIDENTIFIKATION_ID")
    @Valid
    public RufnummerOnkz getAnschlussIdentifikation() {
        return anschlussIdentifikation;
    }

    public void setAnschlussIdentifikation(RufnummerOnkz anschlussIdentifikation) {
        this.anschlussIdentifikation = anschlussIdentifikation;
    }

    @Override
    @Transient
    public GeschaeftsfallTyp getTyp() {
        return GeschaeftsfallTyp.VA_KUE_ORN;
    }

    @Override
    public String toString() {
        return "WbciGeschaeftsfallKueOrn{" +
                "anschlussIdentifikation=" + anschlussIdentifikation +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
