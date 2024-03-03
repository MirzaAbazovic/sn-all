/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 08:15:22
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.RNModel;


/**
 * View-Klasse fuer die Standard-Daten einer Projektierung.
 *
 *
 */
public class ProjektierungsView extends AbstractVerlaufView implements RNModel {

    private Long vpnNr = null;
    private Date vorgabeAm = null;
    private Date projAnDispo = null;
    private String bearbeiter = null;
    private String bearbeiterAm = null;
    private String verlaufAbtStatus = null;
    private String hauptRN = null;
    private Boolean erledigt = null;
    private boolean storniert = false;
    private Long niederlassungId = null;
    private String niederlassung = null;
    private Boolean preventCPSProvOrder = null;
    private String projectResponsibleName = null;
    private Long projectResponsibleId = null;
    private Long buendelNr;
    private String geraeteBez;

    /**
     * @return Returns the vpnNr.
     */
    public Long getVpnNr() {
        return vpnNr;
    }

    /**
     * @param vpnNr The vpnNr to set.
     */
    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    /**
     * @return Returns the vorgabeAm.
     */
    public Date getVorgabeAm() {
        return vorgabeAm;
    }

    /**
     * @param vorgabeAm The vorgabeAm to set.
     */
    public void setVorgabeAm(Date vorgabeAm) {
        this.vorgabeAm = vorgabeAm;
    }


    /**
     * @return Returns the projAnDispo.
     */
    public Date getProjAnDispo() {
        return projAnDispo == null ? null : new Date(projAnDispo.getTime());
    }

    /**
     * @param projAnDispo The projAnDispo to set.
     */
    public void setProjAnDispo(Date projAnDispo) {
        this.projAnDispo = projAnDispo == null ? null : new Date(projAnDispo.getTime());
    }

    /**
     * @return Returns the storniert.
     */
    public boolean isStorniert() {
        return storniert;
    }

    /**
     * @param storniert The storniert to set.
     */
    public void setStorniert(boolean storniert) {
        this.storniert = storniert;
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

    /**
     * @return Returns the verlaufAbtStatus.
     */
    public String getVerlaufAbtStatus() {
        return verlaufAbtStatus;
    }

    /**
     * @param verlaufAbtStatus The verlaufAbtStatus to set.
     */
    public void setVerlaufAbtStatus(String verlaufAbtStatus) {
        this.verlaufAbtStatus = verlaufAbtStatus;
    }

    /**
     * @return Returns the hauptRN.
     */
    public String getHauptRN() {
        return hauptRN;
    }

    /**
     * @param hauptRN The hauptRN to set.
     */
    public void setHauptRN(String hauptRN) {
        this.hauptRN = hauptRN;
    }

    /**
     * @return Returns the bearbeiterAm.
     */
    public String getBearbeiterAm() {
        return bearbeiterAm;
    }

    /**
     * @param bearbeiterAm The bearbeiterAm to set.
     */
    public void setBearbeiterAm(String bearbeiterAm) {
        this.bearbeiterAm = bearbeiterAm;
    }

    /**
     * @return Returns the erledigt.
     */
    public Boolean getErledigt() {
        return erledigt;
    }

    /**
     * @param erledigt The erledigt to set.
     */
    public void setErledigt(Boolean erledigt) {
        this.erledigt = erledigt;
    }

    /**
     * @return niederlassung
     */
    public String getNiederlassung() {
        return niederlassung;
    }

    /**
     * @param niederlassung Festzulegender niederlassung
     */
    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    /**
     * @return niederlassungId
     */
    public Long getNiederlassungId() {
        return niederlassungId;
    }

    /**
     * @param niederlassungId Festzulegender niederlassungId
     */
    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    /**
     * @return Returns the preventCPSProvOrder.
     */
    public Boolean getPreventCPSProvOrder() {
        return preventCPSProvOrder;
    }

    /**
     * @param preventCPSProvOrder The preventCPSProvOrder to set.
     */
    public void setPreventCPSProvOrder(Boolean preventCPSProvOrder) {
        this.preventCPSProvOrder = preventCPSProvOrder;
    }

    /**
     * @return the projectResponsibleName
     */
    public String getProjectResponsibleName() {
        return projectResponsibleName;
    }

    /**
     * @param projectResponsibleName the projectResponsibleName to set
     */
    public void setProjectResponsibleName(String projectResponsibleName) {
        this.projectResponsibleName = projectResponsibleName;
    }

    /**
     * @return the projectResponsibleId
     */
    public Long getProjectResponsibleId() {
        return projectResponsibleId;
    }

    /**
     * @param projectResponsibleId the projectResponsibleId to set
     */
    public void setProjectResponsibleId(Long projectResponsibleId) {
        this.projectResponsibleId = projectResponsibleId;
    }

    public Long getBuendelNr() {
        return buendelNr;
    }

    public void setBuendelNr(Long buendelNr) {
        this.buendelNr = buendelNr;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }


}
