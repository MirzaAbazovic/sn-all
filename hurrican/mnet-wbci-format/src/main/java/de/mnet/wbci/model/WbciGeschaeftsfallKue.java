package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;

@MappedSuperclass
public abstract class WbciGeschaeftsfallKue extends WbciGeschaeftsfall {

    private static final long serialVersionUID = 8955044114090124440L;

    private Standort standort;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "STANDORT_ID")
    @Valid
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public Standort getStandort() {
        return standort;
    }

    public void setStandort(Standort standort) {
        this.standort = standort;
    }

    @Override
    public String toString() {
        return "WbciGeschaeftsfallKue{" +
                "standort=" + standort +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
