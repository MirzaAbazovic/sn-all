package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVaRrnp;

@Entity
@DiscriminatorValue(GeschaeftsfallTyp.VA_RRNP_NAME)
public class WbciGeschaeftsfallRrnp extends WbciGeschaeftsfall implements RufnummernportierungAware {

    private static final long serialVersionUID = -608611350505474050L;

    private Rufnummernportierung rufnummernportierung;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "RUFNUMMERPORTIERUNG_ID")
    @Valid
    @NotNull(groups = { V1RequestVaRrnp.class }, message = "darf nicht leer sein")
    public Rufnummernportierung getRufnummernportierung() {
        return rufnummernportierung;
    }

    public void setRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
    }

    @Override
    @Transient
    public GeschaeftsfallTyp getTyp() {
        return GeschaeftsfallTyp.VA_RRNP;
    }

    @Override
    public String toString() {
        return "WbciGeschaeftsfallRrnp{" +
                "rufnummernportierung=" + rufnummernportierung +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
