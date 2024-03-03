/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 16:26:34
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * Abstrakte View-Klasse fuer Bauauftrag-Views.
 *
 *
 */
public class AbstractBauauftragView extends AbstractVerlaufView {

    private Long anlassId = null;
    private Long installationRefId = null;
    private String anlass = null;
    private Long esIdA = null;
    private Long esIdB = null;
    private Boolean verschoben = null;
    private Long niederlassungId = null;
    private String niederlassung = null;
    private Date gesamtrealisierungstermin = null;
    private Date wiedervorlage = null;
    private String verlaufAbteilungStatus = null;
    private Long verlaufAbteilungStatusId = null;
    private String endstelleOrtB = null;
    private String endstelleB = null;
    private String hvtClusterId = null;

    public String getEndstelleOrtB() {
        return endstelleOrtB;
    }

    public void setEndstelleOrtB(String endstelleOrtB) {
        this.endstelleOrtB = endstelleOrtB;
    }

    public String getEndstelleB() {
        return endstelleB;
    }

    public void setEndstelleB(String endstelleB) {
        this.endstelleB = endstelleB;
    }

    public Long getAnlassId() {
        return anlassId;
    }

    public void setAnlassId(Long anlassId) {
        this.anlassId = anlassId;
    }

    public String getAnlass() {
        return anlass;
    }

    public void setAnlass(String anlass) {
        this.anlass = anlass;
    }

    public Long getEsIdA() {
        return esIdA;
    }

    public void setEsIdA(Long esIdA) {
        this.esIdA = esIdA;
    }

    public Long getEsIdB() {
        return esIdB;
    }

    public void setEsIdB(Long esIdB) {
        this.esIdB = esIdB;
    }

    public Boolean getVerschoben() {
        return verschoben;
    }

    public boolean isVerschoben() {
        return (verschoben != null) ? verschoben.booleanValue() : false;
    }

    public void setVerschoben(Boolean verschoben) {
        this.verschoben = verschoben;
    }

    public Long getInstallationRefId() {
        return installationRefId;
    }

    public void setInstallationRefId(Long installationRefId) {
        this.installationRefId = installationRefId;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Date getGesamtrealisierungstermin() {
        return gesamtrealisierungstermin;
    }

    public void setGesamtrealisierungstermin(Date gesamtrealisierungstermin) {
        this.gesamtrealisierungstermin = gesamtrealisierungstermin;
    }

    public Date getWiedervorlage() {
        return wiedervorlage;
    }

    public void setWiedervorlage(Date wiedervorlage) {
        this.wiedervorlage = wiedervorlage;
    }

    public String getVerlaufAbteilungStatus() {
        return verlaufAbteilungStatus;
    }

    public void setVerlaufAbteilungStatus(String verlaufAbteilungStatus) {
        this.verlaufAbteilungStatus = verlaufAbteilungStatus;
    }

    public Long getVerlaufAbteilungStatusId() {
        return verlaufAbteilungStatusId;
    }

    public void setVerlaufAbteilungStatusId(Long verlaufAbteilungStatusId) {
        this.verlaufAbteilungStatusId = verlaufAbteilungStatusId;
    }

    public String getHvtClusterId() {
        return hvtClusterId;
    }

    public void setHvtClusterId(String hvtClusterId) {
        this.hvtClusterId = hvtClusterId;
    }
}


