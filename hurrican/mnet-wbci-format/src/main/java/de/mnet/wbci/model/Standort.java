package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@Table(name = "T_WBCI_STANDORT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_STANDORT_0", allocationSize = 1)
public class Standort extends WbciEntity {
    private static final long serialVersionUID = -2251020237402066482L;

    private Strasse strasse;
    private String postleitzahl;
    private String ort;

    @Transient
    public String getCombinedStreetData() {
        return StringUtils.join(
                new String[] { getStrasse().getStrassenname(),
                        getStrasse().getHausnummer(),
                        getStrasse().getHausnummernZusatz() }, " "
        );
    }

    @Embedded
    @Valid
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    public Strasse getStrasse() {
        return strasse;
    }

    public void setStrasse(Strasse strasse) {
        this.strasse = strasse;
    }

    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{5}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 5 nummerische Zeichen")
    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = 40, groups = { V1RequestVa.class })
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public String toString() {
        return "Standort{" +
                "strasse=" + strasse +
                ", postleitzahl='" + postleitzahl + '\'' +
                ", ort='" + ort + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
