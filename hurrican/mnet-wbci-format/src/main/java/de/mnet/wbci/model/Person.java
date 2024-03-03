package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@DiscriminatorValue(PersonOderFirmaTyp.PERSON_NAME)
public class Person extends PersonOderFirma {
    private static final long serialVersionUID = -3428318882507495054L;

    private String vorname;
    private String nachname;

    @Override
    @Transient
    public PersonOderFirmaTyp getTyp() {
        return PersonOderFirmaTyp.PERSON;
    }

    @Size(min = 1, max = MAX_CHARS_OF_NAME_FIELDS, groups = { V1RequestVa.class })
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = MAX_CHARS_OF_NAME_FIELDS, groups = { V1RequestVa.class })
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Override
    public String toString() {
        return "Person{" +
                "vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
