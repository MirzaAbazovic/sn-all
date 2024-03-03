/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.2006 08:42:45
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View-Modell, um wichtige Daten eine Endgeraete-Zuordnung zu einem (CC-)Auftrag darzustellen.
 *
 *
 */
public class EG2AuftragView extends AbstractCCModel implements CCAuftragModel {

    private Long auftragId = null;
    private Long egId = null;
    private String egName = null;
    private Long montageartId = null;
    private String montageart = null;
    private Long eg2AuftragId = null;
    private Boolean deactivated = null;
    private Boolean isConfigurable = null;
    private Boolean hasConfiguration = null;
    private Boolean cpsProvisioning = null;
    private String seriennummer = null;
    private String bemerkung = null;
    private String raum = null;
    private String etage = null;
    private String esTyp = null;

    public Long getEg2AuftragId() {
        return this.eg2AuftragId;
    }

    public void setEg2AuftragId(Long eg2AuftragId) {
        this.eg2AuftragId = eg2AuftragId;
    }

    public Long getEgId() {
        return this.egId;
    }

    public void setEgId(Long egId) {
        this.egId = egId;
    }

    public String getEgName() {
        return this.egName;
    }

    public void setEgName(String egName) {
        this.egName = egName;
    }

    public Boolean getHasConfiguration() {
        return this.hasConfiguration;
    }

    public void setHasConfiguration(Boolean hasConfiguration) {
        this.hasConfiguration = hasConfiguration;
    }

    public Boolean getIsConfigurable() {
        return this.isConfigurable;
    }

    public void setIsConfigurable(Boolean isConfigurable) {
        this.isConfigurable = isConfigurable;
    }

    public String getMontageart() {
        return this.montageart;
    }

    public void setMontageart(String montageart) {
        this.montageart = montageart;
    }

    public Long getAuftragId() {
        return this.auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getMontageartId() {
        return this.montageartId;
    }

    public void setMontageartId(Long montageartId) {
        this.montageartId = montageartId;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public String getEtage() {
        return etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }

    public String getEsTyp() {
        return esTyp;
    }

    public void setEsTyp(String esTyp) {
        this.esTyp = esTyp;
    }

    public Boolean getCpsProvisioning() {
        return cpsProvisioning;
    }

    public void setCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
    }

    public Boolean getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public String getSeriennummer() {
        return seriennummer;
    }

    public void setSeriennummer(String seriennummer) {
        this.seriennummer = seriennummer;
    }

}


