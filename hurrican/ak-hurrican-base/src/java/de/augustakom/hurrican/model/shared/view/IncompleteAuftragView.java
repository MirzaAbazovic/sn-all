/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2005 09:43:30
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Klasse zur Abbildung von unvollstaendigen Auftraegen.
 *
 *
 */
public class IncompleteAuftragView extends DefaultSharedAuftragView implements CCAuftragModel, KundenModel {

    private Long auftragNoOrig = null;
    private String produktName = null;
    private String produktGruppe = null;
    private String auftragsart = null;
    private Date vorgabeSCV = null;
    private String baAnlass = null;
    private Date baRealTermin = null;
    private Date baAnDispo = null;
    private Date cbBestelltAm = null;
    private Date cbZurueckAm = null;
    private Date cbKuendigungAm = null;
    private Date cbKuendigungZurueck = null;
    private String cbLbz = null;
    private String bearbeiter = null;

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the auftragsart.
     */
    public String getAuftragsart() {
        return auftragsart;
    }

    /**
     * @param auftragsart The auftragsart to set.
     */
    public void setAuftragsart(String auftragsart) {
        this.auftragsart = auftragsart;
    }

    /**
     * @return Returns the baAnDispo.
     */
    public Date getBaAnDispo() {
        return baAnDispo;
    }

    /**
     * @param baAnDispo The baAnDispo to set.
     */
    public void setBaAnDispo(Date baAnDispo) {
        this.baAnDispo = baAnDispo;
    }

    /**
     * @return Returns the baAnlass.
     */
    public String getBaAnlass() {
        return baAnlass;
    }

    /**
     * @param baAnlass The baAnlass to set.
     */
    public void setBaAnlass(String baAnlass) {
        this.baAnlass = baAnlass;
    }

    /**
     * @return Returns the baRealTermin.
     */
    public Date getBaRealTermin() {
        return baRealTermin;
    }

    /**
     * @param baRealTermin The baRealTermin to set.
     */
    public void setBaRealTermin(Date baRealTermin) {
        this.baRealTermin = baRealTermin;
    }

    /**
     * @return Returns the cbBestelltAm.
     */
    public Date getCbBestelltAm() {
        return cbBestelltAm;
    }

    /**
     * @param cbBestelltAm The cbBestelltAm to set.
     */
    public void setCbBestelltAm(Date cbBestelltAm) {
        this.cbBestelltAm = cbBestelltAm;
    }

    /**
     * @return Returns the cbKuendigungAm.
     */
    public Date getCbKuendigungAm() {
        return cbKuendigungAm;
    }

    /**
     * @param cbKuendigungAm The cbKuendigungAm to set.
     */
    public void setCbKuendigungAm(Date cbKuendigungAm) {
        this.cbKuendigungAm = cbKuendigungAm;
    }

    /**
     * @return Returns the cbKuendigungZurueck.
     */
    public Date getCbKuendigungZurueck() {
        return cbKuendigungZurueck;
    }

    /**
     * @param cbKuendigungZurueck The cbKuendigungZurueck to set.
     */
    public void setCbKuendigungZurueck(Date cbKuendigungZurueck) {
        this.cbKuendigungZurueck = cbKuendigungZurueck;
    }

    /**
     * @return Returns the cbLbz.
     */
    public String getCbLbz() {
        return cbLbz;
    }

    /**
     * @param cbLbz The cbLbz to set.
     */
    public void setCbLbz(String cbLbz) {
        this.cbLbz = cbLbz;
    }

    /**
     * @return Returns the cbZurueckAm.
     */
    public Date getCbZurueckAm() {
        return cbZurueckAm;
    }

    /**
     * @param cbZurueckAm The cbZurueckAm to set.
     */
    public void setCbZurueckAm(Date cbZurueckAm) {
        this.cbZurueckAm = cbZurueckAm;
    }

    /**
     * @return Returns the produktGruppe.
     */
    public String getProduktGruppe() {
        return produktGruppe;
    }

    /**
     * @param produktGruppe The produktGruppe to set.
     */
    public void setProduktGruppe(String produktGruppe) {
        this.produktGruppe = produktGruppe;
    }

    /**
     * @return Returns the produktName.
     */
    public String getProduktName() {
        return produktName;
    }

    /**
     * @param produktName The produktName to set.
     */
    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    /**
     * @return Returns the vorgabeSCV.
     */
    public Date getVorgabeSCV() {
        return vorgabeSCV;
    }

    /**
     * @param vorgabeSCV The vorgabeSCV to set.
     */
    public void setVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
    }

    /**
     * @return Returns the bearbeiter.
     */
    public String getBearbeiter() {
        return bearbeiter;
    }

    /**
     * @param bearbeiter The bearbeiter to set.
     */
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }
}


