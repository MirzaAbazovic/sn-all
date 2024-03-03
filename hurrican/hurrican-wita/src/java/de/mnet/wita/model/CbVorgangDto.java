/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2011 16:55:01
 */
package de.mnet.wita.model;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;

/**
 * DTO für Wita-Geschaeftsfaelle, die bei Stornierung oder Terminverschiebung auszuwaehlen sind.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
public class CbVorgangDto implements Serializable {

    private static final long serialVersionUID = -2195725511541712702L;

    private Boolean selected;
    private String vorgangTyp;
    private Date realisierungsDate;
    private Date vorgabeMnet;
    private Long cbVorgangId;

    public CbVorgangDto(Boolean selected, CBVorgang cbVorgang, String vorgangTyp) {
        this.selected = selected;
        this.vorgangTyp = vorgangTyp;
        this.cbVorgangId = cbVorgang.getId();
        this.realisierungsDate = cbVorgang.getReturnRealDate();
        this.vorgabeMnet = cbVorgang.getVorgabeMnet();
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getVorgangTyp() {
        return vorgangTyp;
    }

    public void setVorgangTyp(String vorgangTyp) {
        this.vorgangTyp = vorgangTyp;
    }

    public Date getRealisierungsDate() {
        return realisierungsDate;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setRealisierungsDate(Date realierungsDate) {
        this.realisierungsDate = realierungsDate;
    }

    public Long getCbVorgangId() {
        return cbVorgangId;
    }

    public void setCbVorgangId(Long cbVorgangId) {
        this.cbVorgangId = cbVorgangId;
    }

    public Date getVorgabeMnet() {
        return vorgabeMnet;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setVorgabeMnet(Date vorgabeMnet) {
        this.vorgabeMnet = vorgabeMnet;
    }
}

