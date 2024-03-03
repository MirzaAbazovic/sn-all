package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;

@Entity
@DiscriminatorValue(GeschaeftsfallTyp.VA_KUE_MRN_NAME)
public class WbciGeschaeftsfallKueMrn extends WbciGeschaeftsfallKue implements RufnummernportierungAware {

    private static final long serialVersionUID = -9089127463703905491L;

    private Rufnummernportierung rufnummernportierung;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "RUFNUMMERPORTIERUNG_ID")
    @Valid
    @NotNull(groups = { V1RequestVaKueMrn.class }, message = "darf nicht leer sein")
    public Rufnummernportierung getRufnummernportierung() {
        return rufnummernportierung;
    }

    public void setRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
    }

    @Override
    @Transient
    public GeschaeftsfallTyp getTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

    @Override
    public String toString() {
        return "WbciGeschaeftsfallKueMrn{" +
                "rufnummernportierung=" + rufnummernportierung +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
