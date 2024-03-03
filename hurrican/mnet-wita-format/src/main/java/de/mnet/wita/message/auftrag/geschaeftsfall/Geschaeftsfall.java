/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:23
 */
package de.mnet.wita.message.auftrag.geschaeftsfall;

import static com.google.common.collect.Lists.*;

import java.lang.reflect.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallAnsprechpartner;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.validators.getters.HasVertragsNummer;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_GESCHAEFTSFALL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "geschaeftsfalltyp", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_GESCHAEFTSFALL_0", allocationSize = 1)
public abstract class Geschaeftsfall extends MwfEntity implements HasVertragsNummer {

    private static final long serialVersionUID = 1254355254771338406L;

    public static final Method VERTRAGS_NUMMER_SETTER;

    static {
        try {
            VERTRAGS_NUMMER_SETTER = Geschaeftsfall.class.getMethod("setVertragsNummer", String.class);
        }
        catch (Exception e) {
            throw new RuntimeException("Setter for vertragsNummer not found.");
        }
    }

    private GeschaeftsfallAnsprechpartner gfAnsprechpartner;
    private Kundenwunschtermin kundenwunschtermin;
    private Auftragsposition auftragsPosition;
    private String vertragsNummer;
    private Carrier abgebenderProvider;
    private List<Anlage> anlagen = newArrayList();
    private String bktoFatkura;

    @Valid
    @NotNull
    @Transient
    public abstract GeschaeftsfallTyp getGeschaeftsfallTyp();

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public GeschaeftsfallAnsprechpartner getGfAnsprechpartner() {
        return gfAnsprechpartner;
    }

    public void setGfAnsprechpartner(GeschaeftsfallAnsprechpartner gfAnsprechpartner) {
        this.gfAnsprechpartner = gfAnsprechpartner;
    }

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Kundenwunschtermin getKundenwunschtermin() {
        return kundenwunschtermin;
    }

    public void setKundenwunschtermin(Kundenwunschtermin kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
    }

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Auftragsposition getAuftragsPosition() {
        return auftragsPosition;
    }

    public void setAuftragsPosition(Auftragsposition auftragsPosition) {
        this.auftragsPosition = auftragsPosition;
    }

    @Override
    @Size(min = 1, max = 10)
    public String getVertragsNummer() {
        return vertragsNummer;
    }

    /**
     * Falls der Methodennamen geändert wird, den statischen Initializer anpassen.
     */
    public void setVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ABGEBENDER_PROVIDER", length = 30, columnDefinition = "varchar2(30)")
    public Carrier getAbgebenderProvider() {
        return abgebenderProvider;
    }

    public void setAbgebenderProvider(Carrier abgebenderProvider) {
        this.abgebenderProvider = abgebenderProvider;
    }

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "GESCHAEFTSFALL_ID")
    public List<Anlage> getAnlagen() {
        if (anlagen == null) {
            return Collections.emptyList();
        }
        return anlagen;
    }

    public void setAnlagen(List<Anlage> anlagen) {
        this.anlagen = anlagen;
    }

    @Column(name = "BKTO_FAKTURA", length = 10, columnDefinition = "varchar2(30)")
    public String getBktoFatkura() {
        return bktoFatkura;
    }

    public void setBktoFatkura(String bktoFatkura) {
        this.bktoFatkura = bktoFatkura;
    }

    @Override
    public String toString() {
        return "Geschaeftsfall [gfAnsprechpartner=" + gfAnsprechpartner + ", kundenwunschtermin=" + kundenwunschtermin
                + ", auftragsPosition=" + auftragsPosition + ", vertragsNummer=" + vertragsNummer
                + ", abgebenderProvider=" + abgebenderProvider + ", anlagen=" + anlagen + ", bktoFatkura="
                + bktoFatkura + "]";
    }
}
