/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

/**
 * View-Klasse fuer einen el. Verlauf.
 */
public class VerlaufUniversalView extends AbstractBauauftragView implements TimeSlotAware {

    private String hvtAnschlussart = null;
    private Date datumAnAbteilung = null;
    private Long projectResponsibleId = null;
    private String projectResponsibleName = null;
    private Boolean kreuzung = null;
    private TimeSlotHolder timeSlot = new TimeSlotHolder();
    private Long statusId = null;
    private String bearbeiterAbteilung = null;
    private boolean erledigt = false;
    private Long vpnNr;
    private String dpoChassis;
    private String geraeteBez;

    public String getHvtAnschlussart() {
        return hvtAnschlussart;
    }

    public void setHvtAnschlussart(String hvtAnschlussart) {
        this.hvtAnschlussart = hvtAnschlussart;
    }

    public Date getDatumAnAbteilung() {
        return datumAnAbteilung;
    }

    public void setDatumAnAbteilung(Date datumAnAbteilung) {
        this.datumAnAbteilung = datumAnAbteilung;
    }

    public Long getProjectResponsibleId() {
        return projectResponsibleId;
    }

    public void setProjectResponsibleId(Long projectResponsibleId) {
        this.projectResponsibleId = projectResponsibleId;
    }

    public String getProjectResponsibleName() {
        return projectResponsibleName;
    }

    public void setProjectResponsibleName(String projectResponsibleName) {
        this.projectResponsibleName = projectResponsibleName;
    }

    public Boolean getKreuzung() {
        return kreuzung;
    }

    public void setKreuzung(Boolean kreuzung) {
        this.kreuzung = kreuzung;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getBearbeiterAbteilung() {
        return bearbeiterAbteilung;
    }

    public void setBearbeiterAbteilung(String bearbeiterAbteilung) {
        this.bearbeiterAbteilung = bearbeiterAbteilung;
    }

    public boolean isErledigt() {
        return erledigt;
    }

    public void setErledigt(boolean erledigt) {
        this.erledigt = erledigt;
    }

    public Long getVpnNr() {
        return vpnNr;
    }

    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }

    public String getDpoChassis() {
        return dpoChassis;
    }

    public void setDpoChassis(String dpoChassis) {
        this.dpoChassis = dpoChassis;
    }

    @Override
    public TimeSlotHolder getTimeSlot() {
        return timeSlot;
    }
}
