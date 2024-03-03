package de.mnet.wbci.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@DiscriminatorValue(RufnummernportierungTyp.ANLAGEN_ANSCHLUSS)
public class RufnummernportierungAnlage extends Rufnummernportierung {

    private static final long serialVersionUID = -7388761749586500081L;

    private String onkz;
    private String durchwahlnummer;
    private String abfragestelle;
    private List<Rufnummernblock> rufnummernbloecke;

    @Override
    @Transient
    public RufnummernportierungTyp getTyp() {
        return RufnummernportierungTyp.ANLAGE;
    }

    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "[1-9]\\d{1,4}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 2 - 5 nummerische Zeichen, ohne '0'")
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = stripLeadingZero(onkz);
    }

    @Transient
    public String getOnkzWithLeadingZero() {
        return addLeadingZero(onkz);
    }

    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{1,8}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 8 nummerische Zeichen")
    public String getDurchwahlnummer() {
        return durchwahlnummer;
    }

    public void setDurchwahlnummer(String durchwahlnummer) {
        this.durchwahlnummer = durchwahlnummer;
    }

    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{1,6}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 6 nummerische Zeichen")
    public String getAbfragestelle() {
        return abfragestelle;
    }

    public void setAbfragestelle(String abfragestelle) {
        this.abfragestelle = abfragestelle;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "RUFNUMMER_BLOCK_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @Valid
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = 5, groups = { V1RequestVa.class })
    public List<Rufnummernblock> getRufnummernbloecke() {
        return rufnummernbloecke;
    }

    public void setRufnummernbloecke(List<Rufnummernblock> rufnummernbloecke) {
        this.rufnummernbloecke = rufnummernbloecke;
    }

    @Transient
    public void addRufnummernblock(Rufnummernblock toAdd) {
        if (rufnummernbloecke == null) {
            rufnummernbloecke = new ArrayList<>();
        }
        rufnummernbloecke.add(toAdd);
    }

    @Override
    public String toString() {
        return "RufnummernportierungAnlage{" +
                "onkz='" + onkz + '\'' +
                ", durchwahlnummer='" + durchwahlnummer + '\'' +
                ", abfragestelle='" + abfragestelle + '\'' +
                ", rufnummernbloecke=" + rufnummernbloecke +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
