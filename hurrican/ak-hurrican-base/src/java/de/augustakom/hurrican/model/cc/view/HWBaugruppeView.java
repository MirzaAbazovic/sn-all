/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2010 10:55:07
 */
package de.augustakom.hurrican.model.cc.view;

import java.io.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.augustakom.common.model.AbstractObservable;


/**
 * View-Modell fuer die Abbildung von relevanten Baugruppen-Informationen.
 */
@Entity
@Table(name = "V_HW_BAUGRUPPEN")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class HWBaugruppeView extends AbstractObservable implements Serializable {

    public static final String BAUGRUPPEN_ID = "baugruppenId";
    private Long baugruppenId;
    public static final String RACK_ID = "rackId";
    private Long rackId;
    public static final String HVT_ID_STANDORT = "hvtIdStandort";
    private Long hvtIdStandort;
    public static final String BAUGRUPPEN_TYP = "baugruppenTyp";
    private String baugruppenTyp;
    public static final String HERSTELLER = "hersteller";
    private String hersteller;
    public static final String HW_RACK_GERAETEBEZ = "hwRackGeraetebez";
    private String hwRackGeraetebez;
    public static final String HW_RACK_ANLAGENBEZ = "hwRackAnlagenbez";
    private String hwRackAnlagenbez;
    public static final String HW_SUBRACK_TYP = "hwSubrackTyp";
    private String hwSubrackTyp;
    public static final String HW_SUBRACK_MOD_NUMBER = "hwSubrackModNumber";
    private String hwSubrackModNumber;
    public static final String HW_BAUGRUPPEN_MOD_NUMBER = "baugruppenModNumber";
    private String baugruppenModNumber;
    public static final String BAUGRUPPE_EINGEBAUT = "baugruppeEingebaut";
    private Boolean baugruppeEingebaut;

    public static final String[] TABLE_COLUMN_NAMES = new String[] {
            "BG-ID", "Geräte-Bez", "Anlagen-Bez",
            "Subrack-Typ", "Subrack Mod-Nr",
            "BG-Typ", "BG Mod-Nr", "eingebaut" };
    public static final String[] TABLE_PROPERTY_NAMES = new String[] {
            BAUGRUPPEN_ID, HW_RACK_GERAETEBEZ, HW_RACK_ANLAGENBEZ,
            HW_SUBRACK_TYP, HW_SUBRACK_MOD_NUMBER,
            BAUGRUPPEN_TYP, HW_BAUGRUPPEN_MOD_NUMBER, BAUGRUPPE_EINGEBAUT };
    public static final Class<?>[] TABLE_CLASS_TYPES = new Class[] {
            Integer.class, String.class, String.class,
            String.class, String.class, String.class,
            String.class, Boolean.class };
    public static final int[] TABLE_FIT = new int[] { 70, 90, 90, 90, 90, 90, 90, 65 };

    @Id
    @Column(name = "HW_BAUGRUPPEN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getBaugruppenId() {
        return baugruppenId;
    }

    public void setBaugruppenId(Long baugruppenId) {
        this.baugruppenId = baugruppenId;
    }

    @Column(name = "HW_BAUGRUPPEN_TYP")
    public String getBaugruppenTyp() {
        return baugruppenTyp;
    }

    public void setBaugruppenTyp(String baugruppenTyp) {
        this.baugruppenTyp = baugruppenTyp;
    }

    @Column(name = "HERSTELLER")
    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    @Column(name = "GERAETEBEZ")
    public String getHwRackGeraetebez() {
        return hwRackGeraetebez;
    }

    public void setHwRackGeraetebez(String hwRackGeraetebez) {
        this.hwRackGeraetebez = hwRackGeraetebez;
    }

    @Column(name = "ANLAGENBEZ")
    public String getHwRackAnlagenbez() {
        return hwRackAnlagenbez;
    }

    public void setHwRackAnlagenbez(String hwRackAnlagenbez) {
        this.hwRackAnlagenbez = hwRackAnlagenbez;
    }

    @Column(name = "SUBRACK_TYP")
    public String getHwSubrackTyp() {
        return hwSubrackTyp;
    }

    public void setHwSubrackTyp(String hwSubrackTyp) {
        this.hwSubrackTyp = hwSubrackTyp;
    }

    @Column(name = "BG_MOD_NUMBER")
    public String getBaugruppenModNumber() {
        return baugruppenModNumber;
    }

    public void setBaugruppenModNumber(String baugruppenModNumber) {
        this.baugruppenModNumber = baugruppenModNumber;
    }

    @Column(name = "HVT_ID_STANDORT")
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    @Column(name = "EINGEBAUT")
    public Boolean getBaugruppeEingebaut() {
        return baugruppeEingebaut;
    }

    public void setBaugruppeEingebaut(Boolean baugruppeEingebaut) {
        this.baugruppeEingebaut = baugruppeEingebaut;
    }

    @Column(name = "RACK_ID")
    public Long getRackId() {
        return rackId;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }

    @Column(name = "SUBRACK_MOD_NUMBER")
    public String getHwSubrackModNumber() {
        return hwSubrackModNumber;
    }

    public void setHwSubrackModNumber(String hwSubrackModNumber) {
        this.hwSubrackModNumber = hwSubrackModNumber;
    }

}


