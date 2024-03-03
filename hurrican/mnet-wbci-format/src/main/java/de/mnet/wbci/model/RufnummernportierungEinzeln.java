package de.mnet.wbci.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wbci.validation.groups.V1RequestVa;

@Entity
@DiscriminatorValue(RufnummernportierungTyp.EINZEL_ANSCHLUSS)
public class RufnummernportierungEinzeln extends Rufnummernportierung {

    private static final long serialVersionUID = 4173945380784167965L;

    private Boolean alleRufnummernPortieren;
    private List<RufnummerOnkz> rufnummerOnkzs;

    @Override
    @Transient
    public RufnummernportierungTyp getTyp() {
        return RufnummernportierungTyp.EINZEL;
    }

    public Boolean getAlleRufnummernPortieren() {
        return alleRufnummernPortieren;
    }

    public void setAlleRufnummernPortieren(Boolean alleRufnummernPortieren) {
        this.alleRufnummernPortieren = alleRufnummernPortieren;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "T_WBCI_RNP_EINZELN_RN_ONKZ",
            joinColumns = @JoinColumn(name = "RNP_EINZELN_ID"),
            inverseJoinColumns = @JoinColumn(name = "RUFNUMMER_ONKZ_ID")
    )
    @Fetch(FetchMode.SUBSELECT)
    @Valid
    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = 10, groups = { V1RequestVa.class })
    public List<RufnummerOnkz> getRufnummernOnkz() {
        return rufnummerOnkzs;
    }

    public void setRufnummernOnkz(List<RufnummerOnkz> rufnummernOnkzs) {
        this.rufnummerOnkzs = rufnummernOnkzs;
    }

    @Transient
    public void addRufnummerOnkz(RufnummerOnkz toAdd) {
        if (getRufnummernOnkz() == null) {
            rufnummerOnkzs = new ArrayList<>();
        }
        rufnummerOnkzs.add(toAdd);
    }

    @Override
    public String toString() {
        return "RufnummernportierungEinzeln{" +
                "alleRufnummernPortieren=" + alleRufnummernPortieren +
                ", rufnummernListe=" + rufnummerOnkzs +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
