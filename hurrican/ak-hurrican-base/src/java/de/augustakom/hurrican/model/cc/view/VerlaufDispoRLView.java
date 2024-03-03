/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 09:52:42
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * Verlaufs-View fuer die Dispo-Ruecklaeufer.
 *
 *
 */
public class VerlaufDispoRLView extends AbstractBauauftragView implements TimeSlotAware {

    private String bearbeiterAm = null;
    private String verteiler = null;
    private Boolean erledigt = null;
    private String hvtAnschlussart = null;
    private String ntMontage = null;
    private Date inbetriebnahme = null;
    private boolean abgeschlossen = false;
    private boolean storniert = false;
    private String projectResponsible = null;
    private Long projectResponsibleId = null;
    private TimeSlotHolder timeSlot = new TimeSlotHolder();

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
     * @return Returns the hvtAnschlussart.
     */
    public String getHvtAnschlussart() {
        return hvtAnschlussart;
    }

    /**
     * @param hvtAnschlussart The hvtAnschlussart to set.
     */
    public void setHvtAnschlussart(String hvtAnschlussart) {
        this.hvtAnschlussart = hvtAnschlussart;
    }

    /**
     * @return Returns the ntMontage.
     */
    public String getNtMontage() {
        return ntMontage;
    }

    /**
     * @param ntMontage The ntMontage to set.
     */
    public void setNtMontage(String ntMontage) {
        this.ntMontage = ntMontage;
    }

    /**
     * @return Returns the inbetriebnahme.
     */
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme The inbetriebnahme to set.
     */
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return Returns the abgeschlossen.
     */
    public boolean isAbgeschlossen() {
        return abgeschlossen;
    }

    /**
     * @param abgeschlossen The abgeschlossen to set.
     */
    public void setAbgeschlossen(boolean abgeschlossen) {
        this.abgeschlossen = abgeschlossen;
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
     * @return Returns the verteiler.
     */
    public String getVerteiler() {
        return this.verteiler;
    }

    /**
     * @param verteiler The verteiler to set.
     */
    public void setVerteiler(String verteiler) {
        this.verteiler = verteiler;
    }

    /**
     * @return the projectResponsible
     */
    public String getProjectResponsible() {
        return projectResponsible;
    }

    /**
     * @param projectResponsible the projectResponsible to set
     */
    public void setProjectResponsible(String projectResponsible) {
        this.projectResponsible = projectResponsible;
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

    public TimeSlotHolder getTimeSlot() {
        return timeSlot;
    }
}


