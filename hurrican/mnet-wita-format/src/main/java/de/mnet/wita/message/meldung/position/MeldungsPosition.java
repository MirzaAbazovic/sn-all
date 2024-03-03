package de.mnet.wita.message.meldung.position;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.groups.V1;

/**
 * Basisklasse für Meldungspositionen. Enthält den Meldungscode und den Meldungstext.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_MELDUNGS_POSITION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "meldungspositionstyp", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("other")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_MELDUNGS_POSITION_0", allocationSize = 1)
public class MeldungsPosition extends MwfEntity {

    private static final long serialVersionUID = -8343587231845767734L;

    private String meldungsCode;
    private String meldungsText;
    private Positionsattribute positionsattribute;

    public MeldungsPosition() {
        // required by Hibernate
    }

    public MeldungsPosition(String code, String text) {
        this.meldungsCode = code;
        this.meldungsText = text;
    }

    @NotNull
    @Column(name = "MELDUNGS_CODE")
    @Size(min = 1, max = 10)
    public String getMeldungsCode() {
        return meldungsCode;
    }

    public void setMeldungsCode(String meldungsCode) {
        this.meldungsCode = meldungsCode;
    }

    @NotNull
    @Column(name = "MELDUNGS_TEXT")
    @Size.List({
            @Size(groups = V1.class, min = 1, max = 255, message = "Der Meldungstext muss zwischen {min} und {max} Zeichen haben.")
    })
    public String getMeldungsText() {
        return meldungsText;
    }

    public void setMeldungsText(String meldungsText) {
        this.meldungsText = meldungsText;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Positionsattribute getPositionsattribute() {
        return positionsattribute;
    }

    public void setPositionsattribute(Positionsattribute positionsattribute) {
        this.positionsattribute = positionsattribute;
    }

    @Override
    public String toString() {
        return "Meldungsposition [meldungscode=" + meldungsCode + ", meldungstext=" + meldungsText + "]";
    }

}
