package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_RUFNUMMER_ONKZ")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_RUFNUMMER_0", allocationSize = 1)
public class RufnummerOnkz extends WbciEntity {

    private static final long serialVersionUID = 5641082356136155291L;

    private String onkz;
    private String rufnummer;
    private String portierungskennungPKIabg;

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
    @Pattern(regexp = "\\d{1,14}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 14 nummerische Zeichen")
    public String getRufnummer() {
        return rufnummer;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    /**
     * Nur bei RUEM-VA Meldungen gesetzt!
     *
     * @return
     */
    @Column(name = "PKI_ABG")
    public String getPortierungskennungPKIabg() {
        return portierungskennungPKIabg;
    }

    /**
     * Nur fuer RUEM-VA Meldungen gedacht!
     *
     * @param portierungskennungPKIabg
     */
    public void setPortierungskennungPKIabg(String portierungskennungPKIabg) {
        this.portierungskennungPKIabg = portierungskennungPKIabg;
    }

    @Override
    public String toString() {
        return "RufnummerOnkz{" +
                "onkz='" + onkz + '\'' +
                ", rufnummer='" + rufnummer + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
