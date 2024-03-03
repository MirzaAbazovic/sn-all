/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 15:48:12
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.shared.AbstractSharedModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.model.shared.iface.LongIdModel;


/**
 * View fuer VPN-Vertraege.
 *
 *
 */
public class VPNVertragView extends AbstractSharedModel implements KundenModel, LongIdModel {

    private Long id = null;
    private Long vpnNr = null;
    private String vpnName = null;
    private String vpnType = null;
    private String projektleiter = null;
    private Date vpnDatum = null;
    private Long kundeNo = null;
    private String kundeName = null;
    private String kundeVorname = null;
    private String bemerkung = null;
    private String vpnEinwahl = null;
    private Niederlassung niederlassung = null;
    private String salesRep = null;
    private Boolean qos = null;
    private boolean cancelled;

    /**
     * @return Returns the bemerkung.
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * @param bemerkung The bemerkung to set.
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * @return Returns the id.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the projektleiter.
     */
    public String getProjektleiter() {
        return projektleiter;
    }

    /**
     * @param projektleiter The projektleiter to set.
     */
    public void setProjektleiter(String projektleiter) {
        this.projektleiter = projektleiter;
    }

    /**
     * @return Returns the vpnDatum.
     */
    public Date getVpnDatum() {
        return vpnDatum;
    }

    /**
     * @param vpnDatum The vpnDatum to set.
     */
    public void setVpnDatum(Date vpnDatum) {
        this.vpnDatum = vpnDatum;
    }

    /**
     * @return Returns the vpnEinwahl.
     */
    public String getVpnEinwahl() {
        return vpnEinwahl;
    }

    /**
     * @param vpnEinwahl The vpnEinwahl to set.
     */
    public void setVpnEinwahl(String vpnEinwahl) {
        this.vpnEinwahl = vpnEinwahl;
    }

    /**
     * Die vpnNr entspricht der Mistral Auftrags-No.
     *
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
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the kundeName.
     */
    public String getKundeName() {
        return kundeName;
    }

    /**
     * @param kundeName The kundeName to set.
     */
    public void setKundeName(String kundeName) {
        this.kundeName = kundeName;
    }

    /**
     * @return Returns the kundeVorname.
     */
    public String getKundeVorname() {
        return kundeVorname;
    }

    /**
     * @param kundeVorname The kundeVorname to set.
     */
    public void setKundeVorname(String kundeVorname) {
        this.kundeVorname = kundeVorname;
    }

    /**
     * @return vpnType
     */
    public String getVpnType() {
        return vpnType;
    }

    /**
     * @param vpnType Festzulegender vpnType
     */
    public void setVpnType(String vpnType) {
        this.vpnType = vpnType;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public Niederlassung getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(Niederlassung niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(String salesRep) {
        this.salesRep = salesRep;
    }

    public Boolean getQos() {
        return qos;
    }

    public void setQos(Boolean qos) {
        this.qos = qos;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}


