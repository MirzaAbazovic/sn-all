package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1RequestVa;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_RUFNUMMERNBLOCK")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_RUFNUMMERNBLOCK_0", allocationSize = 1)
public class Rufnummernblock extends WbciEntity {

    private static final long serialVersionUID = 230745644447308929L;

    private String rnrBlockVon;
    private String rnrBlockBis;
    private String portierungskennungPKIabg;

    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{1,6}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 6 nummerische Zeichen")
    public String getRnrBlockVon() {
        return rnrBlockVon;
    }

    public void setRnrBlockVon(String rnrBlockVon) {
        this.rnrBlockVon = rnrBlockVon;
    }

    @NotNull(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{1,6}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 6 nummerische Zeichen")
    public String getRnrBlockBis() {
        return rnrBlockBis;
    }

    public void setRnrBlockBis(String rnrBlockBis) {
        this.rnrBlockBis = rnrBlockBis;
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
        return "Rufnummernblock{" +
                "rnrBlockVon='" + rnrBlockVon + '\'' +
                ", rnrBlockBis='" + rnrBlockBis + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
