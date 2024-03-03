package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@DiscriminatorValue(PersonOderFirmaTyp.FIRMA_NAME)
public class Firma extends PersonOderFirma {
    private static final long serialVersionUID = -3428318882507495054L;

    private String firmenname;
    private String firmennamenZusatz;

    @Override
    @Transient
    public PersonOderFirmaTyp getTyp() {
        return PersonOderFirmaTyp.FIRMA;
    }


    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = MAX_CHARS_OF_NAME_FIELDS, groups = { V1RequestVa.class })
    public String getFirmenname() {
        return firmenname;
    }

    public void setFirmenname(String firmenname) {
        this.firmenname = firmenname;
    }

    @Size(min = 1, max = MAX_CHARS_OF_NAME_FIELDS, groups = { V1RequestVa.class })
    public String getFirmennamenZusatz() {
        return firmennamenZusatz;
    }

    public void setFirmennamenZusatz(String firmennamenZusatz) {
        this.firmennamenZusatz = firmennamenZusatz;
    }

    @Override
    public String toString() {
        return "Firma{" +
                "firmenname='" + firmenname + '\'' +
                ", firmennamenZusatz='" + firmennamenZusatz + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
