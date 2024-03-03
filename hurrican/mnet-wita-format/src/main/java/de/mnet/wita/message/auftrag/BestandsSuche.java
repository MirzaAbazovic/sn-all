/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2011 16:53:53
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.AbfragestelleValid;
import de.mnet.wita.validators.DurchwahlValid;
import de.mnet.wita.validators.OnkzValid;
import de.mnet.wita.validators.RufnummerValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_BESTANDSSUCHE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_BESTANDSSUCHE_0", allocationSize = 1)
public class BestandsSuche extends MwfEntity {

    private static final long serialVersionUID = -6880996770751861531L;

    /**
     * ONKZ (for Einzelanschluss in case of extended BestandssSuche).
     */
    private String onkz;
    /**
     * Rufnummer (for Einzelanschluss in case of extended BestandssSuche).
     */
    private String rufnummer;

    private String anlagenOnkz;
    private String anlagenDurchwahl;
    private String anlagenAbfrageStelle;

    public BestandsSuche() {
        // required by Hibernate
    }

    public BestandsSuche(String onkz, String rufnummer, String anlagenOnkz, String anlagenDurchwahl,
            String anlagenAbfrageStelle) {
        super();
        this.onkz = onkz;
        this.rufnummer = rufnummer;
        this.anlagenOnkz = anlagenOnkz;
        this.anlagenDurchwahl = anlagenDurchwahl;
        this.anlagenAbfrageStelle = anlagenAbfrageStelle;
    }

    @OnkzValid
    @Column(name = "ONKZ")
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    @RufnummerValid
    @Column(name = "RUFNUMMER")
    public String getRufnummer() {
        return rufnummer;
    }

    public void setAnlagenAbfrageStelle(String anlagenAbfrageStelle) {
        this.anlagenAbfrageStelle = anlagenAbfrageStelle;
    }

    @AbfragestelleValid
    @Column(name = "ANLAGEN_ABFRAGE_STELLE")
    public String getAnlagenAbfrageStelle() {
        return anlagenAbfrageStelle;
    }

    public void setAnlagenDurchwahl(String anlagenDurchwahl) {
        this.anlagenDurchwahl = anlagenDurchwahl;
    }

    @DurchwahlValid
    @Column(name = "ANLAGEN_DURCHWAHL")
    public String getAnlagenDurchwahl() {
        return anlagenDurchwahl;
    }

    public void setAnlagenOnkz(String anlagenOnkz) {
        this.anlagenOnkz = anlagenOnkz;
    }

    @OnkzValid
    @Column(name = "ANLAGEN_ONKZ")
    public String getAnlagenOnkz() {
        return anlagenOnkz;
    }

}
