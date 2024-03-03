/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:36
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cascade;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.validators.groups.V1;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_GF_PRODUKT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_GF_PRODUKT_0", allocationSize = 1)
public class GeschaeftsfallProdukt extends MwfEntity {

    private static final long serialVersionUID = 2294475096476037763L;

    private StandortKunde standortKunde;
    private StandortKollokation standortKollokation;
    private Schaltangaben schaltangaben;
    private Montageleistung montageleistung;
    private LeitungsBezeichnung leitungsBezeichnung;
    private BestandsSuche bestandsSuche;
    private RufnummernPortierung rufnummernPortierung;
    private Vormieter vormieter;
    private String vorabstimmungsId;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public StandortKunde getStandortKunde() {
        return standortKunde;
    }

    public void setStandortKunde(StandortKunde standortKunde) {
        this.standortKunde = standortKunde;
    }

    @Valid
    @ManyToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public StandortKollokation getStandortKollokation() {
        return standortKollokation;
    }

    public void setStandortKollokation(StandortKollokation standortKollokation) {
        this.standortKollokation = standortKollokation;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Schaltangaben getSchaltangaben() {
        return schaltangaben;
    }

    public void setSchaltangaben(Schaltangaben schaltangaben) {
        this.schaltangaben = schaltangaben;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Montageleistung getMontageleistung() {
        return montageleistung;
    }

    public void setMontageleistung(Montageleistung montageleistung) {
        this.montageleistung = montageleistung;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public LeitungsBezeichnung getLeitungsBezeichnung() {
        return leitungsBezeichnung;
    }

    public void setLeitungsBezeichnung(LeitungsBezeichnung leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
    }

    public void setBestandsSuche(BestandsSuche bestandsSuche) {
        this.bestandsSuche = bestandsSuche;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public BestandsSuche getBestandsSuche() {
        return bestandsSuche;
    }

    public void setRufnummernPortierung(RufnummernPortierung rufnummernPortierung) {
        this.rufnummernPortierung = rufnummernPortierung;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public RufnummernPortierung getRufnummernPortierung() {
        return rufnummernPortierung;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Vormieter getVormieter() {
        return vormieter;
    }

    public void setVormieter(Vormieter vormieter) {
        this.vormieter = vormieter;
    }

    @Size(groups = V1.class, min = 16, max = 21)
    @Column(name = "VORABSTIMMUNGSID")
    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    @Override
    public String toString() {
        return "GeschaeftsfallProdukt [standortKunde=" + standortKunde + ", standortKollokation=" + standortKollokation
                + ", schaltangaben=" + schaltangaben
                + ", montageleistung=" + montageleistung + ", leitungsBezeichnung=" + leitungsBezeichnung
                + ", bestandsSuche=" + bestandsSuche + ", rufnummernPortierung=" + rufnummernPortierung
                + ", vormieter=" + vormieter + ", vorabstimmungsId=" + vorabstimmungsId + "]";
    }

}
