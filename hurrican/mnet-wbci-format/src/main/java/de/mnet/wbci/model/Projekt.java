package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "wbci.Projekt")
@Table(name = "T_WBCI_PROJEKT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_PROJEKT_0", allocationSize = 1)
public class Projekt extends WbciEntity {

    private static final long serialVersionUID = -1116265098548834948L;

    private String projektKenner;
    private String kopplungsKenner;

    @Size(min = 1, max = 30, groups = { V1RequestVa.class })
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public String getProjektKenner() {
        return projektKenner;
    }

    public void setProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
    }

    @Size(min = 1, max = 30, groups = { V1RequestVa.class })
    public String getKopplungsKenner() {
        return kopplungsKenner;
    }

    public void setKopplungsKenner(String kopplungsKenner) {
        this.kopplungsKenner = kopplungsKenner;
    }

    @Override
    public String toString() {
        return "Projekt{" +
                "projektKenner='" + projektKenner + '\'' +
                ", kopplungsKenner='" + kopplungsKenner + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
