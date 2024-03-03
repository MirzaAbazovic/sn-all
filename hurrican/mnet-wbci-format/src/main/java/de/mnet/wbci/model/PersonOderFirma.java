package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_PERSON_ODER_FIRMA")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_PERSON_ODER_FIRMA_0", allocationSize = 1)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typ", discriminatorType = DiscriminatorType.STRING)
public abstract class PersonOderFirma extends WbciEntity {

    private static final long serialVersionUID = 8836071985055273189L;
    public static final int MAX_CHARS_OF_NAME_FIELDS = 30;

    private Anrede anrede;
    private KundenTyp kundenTyp;

    @Transient
    public abstract PersonOderFirmaTyp getTyp();

    @Enumerated(EnumType.STRING)
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "KUNDEN_TYP")
    public KundenTyp getKundenTyp() {
        return kundenTyp;
    }

    public void setKundenTyp(KundenTyp kundenTyp) {
        this.kundenTyp = kundenTyp;
    }

    @Transient
    public String getNameOrFirma() {
        return (this instanceof Person)
                ? ((Person) this).getNachname()
                : ((Firma) this).getFirmenname();
    }

    @Transient
    public String getVornameOrZusatz() {
        return (this instanceof Person)
                ? ((Person) this).getVorname()
                : ((Firma) this).getFirmennamenZusatz();
    }


    @Override
    public String toString() {
        return "PersonOderFirma{" +
                "anrede=" + anrede +
                ", type='" + getTyp() + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
