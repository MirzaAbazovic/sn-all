/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.06.2006 09:37:20
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell bildet eine Endgeraete-Gruppe zur Kundenueberlassung ab
 *
 *
 */
@Entity
@Table(name = "T_EG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EG_0", allocationSize = 1)
public class EG extends AbstractCCUserModel {

    /**
     * Typ-Definition fuer ein sog. Kauf-Geraet.
     */
    public static final Long TYP_KAUFGERAET = Long.valueOf(1);

    /**
     * Typ-Definition fuer eine Paketdefinition.
     */
    public static final Long TYP_KAUFPAKET = Long.valueOf(2);

    /**
     * Typ-Definition fuer eine Versandleistung  - wird benoetigt um zu ermitteln ob EG noch versendet werden muss oder
     * ob es dem Kunden bereits ausgehaendigt wurde.
     */
    public static final Long TYP_VERSANDLEISTUNG = Long.valueOf(3);

    /**
     * Typ-Definition fuer Standard-Endgeraete, die einem Produkt zugeordnet werden koennen - kein Kaufgeraet.
     */
    public static final Long TYP_ENDGERAET_4_PRODUKT = Long.valueOf(4);

    /**
     * Typ-Definition fuer Paketdefinitionen fuer Standard-Endgeraete. Bei Zuordnung von Paketen dieses Typs zu einem
     * Auftrag werden lediglich die Endgeraete des Pakets ermittelt und diese dem Auftrag zugeordnet.
     */
    public static final Long TYP_DEFAULT_PAKET = Long.valueOf(5);

    public static final Long EG_IPSEC_ROUTER_AKTIV = Long.valueOf(80);

    public static final Long EG_IPSEC_ROUTER_PASSIV = Long.valueOf(81);

    /**
     * Generischer Router fuer Router aus der Monline.
     */
    public static final Long EG_ROUTER = Long.valueOf(176);

    private Long egInterneId = null;
    private String egName = null;
    private String egBeschreibung = null;
    private String lsText = null;
    private Long egTyp = null;
    private Long extLeistungNo = null;
    private Date egVerfuegbarVon = null;
    private Date egVerfuegbarBis = null;
    private String garantiezeit = null;
    private String produktcode = null;
    private Boolean isConfigurable = null;
    private Boolean confPortforwarding = null;
    private Boolean confS0backup = null;
    private Boolean cpsProvisioning = null;

    private List<EGType> egTypes = null;

    /**
     * Prueft, ob es sich bei der EG-Definition um ein Paket handelt.
     *
     * @return true, wenn das Endgeraet ein Paket darstellt.
     *
     */
    @Transient
    public boolean isPaket() {
        return (NumberTools.isIn(getEgTyp(),
                new Number[] { TYP_DEFAULT_PAKET, TYP_KAUFPAKET })) ? true : false;
    }

    /**
     * Prueft, ob es sich bei der EG-Definition um ein Kaufgeraet handelt.
     *
     * @return true, wenn das Endgeraet ein Kaufgeraet ist.
     *
     */
    @Transient
    public boolean isKaufEG() {
        return (NumberTools.isIn(getEgTyp(),
                new Number[] { TYP_KAUFGERAET, TYP_KAUFPAKET, TYP_VERSANDLEISTUNG })) ? true : false;
    }

    @Column(name = "BESCHREIBUNG")
    public String getEgBeschreibung() {
        return egBeschreibung;
    }

    public void setEgBeschreibung(String egBeschreibung) {
        this.egBeschreibung = egBeschreibung;
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_EG_2_EG_TYPE",
            joinColumns = @JoinColumn(name = "EG_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "EG_TYPE_ID", referencedColumnName = "ID"))
    @Fetch(value = FetchMode.SUBSELECT)
    public List<EGType> getEgTypes() {
        return this.egTypes;
    }

    public void setEgTypes(List<EGType> egTypes) {
        this.egTypes = egTypes;
    }

    public void addEgType(EGType egType) {
        if (egType != null) {
            if (this.egTypes == null) {
                this.egTypes = new ArrayList<EGType>();
            }
            this.egTypes.add(egType);
        }
    }

    public void removeEgType(EGType egType) {
        if ((egType != null) && (this.egTypes != null)) {
            this.egTypes.remove(egType);
        }
    }

    @Column(name = "NAME")
    public String getEgName() {
        return egName;
    }

    public void setEgName(String egName) {
        this.egName = egName;
    }

    @Column(name = "TYPE")
    public Long getEgTyp() {
        return egTyp;
    }

    public void setEgTyp(Long egTyp) {
        this.egTyp = egTyp;
    }


    @Column(name = "VERFUEGBAR_BIS")
    public Date getEgVerfuegbarBis() {
        return egVerfuegbarBis;
    }

    public void setEgVerfuegbarBis(Date egVerfuegbarBis) {
        this.egVerfuegbarBis = egVerfuegbarBis;
    }

    @Column(name = "VERFUEGBAR_VON")
    public Date getEgVerfuegbarVon() {
        return egVerfuegbarVon;
    }

    public void setEgVerfuegbarVon(Date egVerfuegbarVon) {
        this.egVerfuegbarVon = egVerfuegbarVon;
    }

    @Column(name = "GARANTIEZEIT")
    public String getGarantiezeit() {
        return garantiezeit;
    }

    public void setGarantiezeit(String garantiezeit) {
        this.garantiezeit = garantiezeit;
    }

    @Column(name = "INTERNE__ID")
    public Long getEgInterneId() {
        return egInterneId;
    }

    public void setEgInterneId(Long egInterneId) {
        this.egInterneId = egInterneId;
    }

    @Column(name = "PRODUKTCODE")
    public String getProduktcode() {
        return produktcode;
    }

    public void setProduktcode(String produktcode) {
        this.produktcode = produktcode;
    }

    @Column(name = "EXT_LEISTUNG__NO")
    public Long getExtLeistungNo() {
        return this.extLeistungNo;
    }

    public void setExtLeistungNo(Long extLeistungNo) {
        this.extLeistungNo = extLeistungNo;
    }

    @Column(name = "CONFIGURABLE")
    public Boolean getIsConfigurable() {
        return this.isConfigurable;
    }

    public void setIsConfigurable(Boolean isConfigurable) {
        this.isConfigurable = isConfigurable;
    }

    @Column(name = "CONF_PORTFORWARDING")
    public Boolean getConfPortforwarding() {
        return confPortforwarding;
    }

    public void setConfPortforwarding(Boolean confPortforwarding) {
        this.confPortforwarding = confPortforwarding;
    }

    @Column(name = "CONF_S0BACKUP")
    public Boolean getConfS0backup() {
        return confS0backup;
    }

    public void setConfS0backup(Boolean confS0backup) {
        this.confS0backup = confS0backup;
    }

    @Column(name = "LS_TEXT")
    public String getLsText() {
        return lsText;
    }

    public void setLsText(String lsText) {
        this.lsText = lsText;
    }

    @Column(name = "CPS_PROVISIONING")
    public Boolean getCpsProvisioning() {
        return cpsProvisioning;
    }

    public void setCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
    }

}


