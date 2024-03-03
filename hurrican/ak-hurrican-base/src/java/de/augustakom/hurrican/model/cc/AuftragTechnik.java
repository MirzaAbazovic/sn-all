/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 09:24:47
 */
package de.augustakom.hurrican.model.cc;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell fuer die technischen Daten eines Auftrags.
 *
 *
 */
public class AuftragTechnik extends AbstractCCHistoryModel implements CCAuftragModel, DebugModel {

    private static final long serialVersionUID = -2314871766771388272L;
    private Long vbzId = null;
    private Long auftragId = null;
    private Long auftragTechnik2EndstelleId = null;
    private Long intAccountId = null;
    private Long vpnId = null;
    private Boolean projektierung = null;
    private Long auftragsart = null;
    private Long niederlassungId = null;
    private Boolean preventCPSProvisioning = null;
    private Long projectResponsibleUserId = null;
    private Long projectLeadUserId = null;
    private String vplsId = null;
    private HWSwitch hwSwitch;


    /**
     * Ueberprueft, ob sich der Auftrag in einem VPN befindet.
     *
     * @return
     */
    public boolean isVPNAuftrag() {
        return vpnId != null;
    }

    /**
     * @see DebugModel#debugModel(Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + AuftragTechnik.class.getName());
            logger.debug("  ID      : " + getId());
            logger.debug("  ES-Group: " + getAuftragTechnik2EndstelleId());
            logger.debug("  IntAcc. : " + getIntAccountId());
        }
    }

    public Long getIntAccountId() {
        return intAccountId;
    }

    public void setIntAccountId(Long intAccountId) {
        this.intAccountId = intAccountId;
    }

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragTechnik2EndstelleId() {
        return auftragTechnik2EndstelleId;
    }

    public void setAuftragTechnik2EndstelleId(Long auftragTechnik2EndstelleId) {
        this.auftragTechnik2EndstelleId = auftragTechnik2EndstelleId;
    }

    /**
     * Gibt die ID der technischen Dokumentationsnr. zurueck.
     */
    public Long getVbzId() {
        return vbzId;
    }

    /**
     * Setzt die ID der technischen Dokumentationsnr.
     */
    public void setVbzId(Long vbzId) {
        this.vbzId = vbzId;
    }

    public Long getVpnId() {
        return vpnId;
    }

    public void setVpnId(Long vpnId) {
        this.vpnId = vpnId;
    }

    public Boolean getProjektierung() {
        return projektierung;
    }

    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    public Long getAuftragsart() {
        return auftragsart;
    }

    public void setAuftragsart(Long auftragsart) {
        this.auftragsart = auftragsart;
    }

    public Boolean getPreventCPSProvisioning() {
        return preventCPSProvisioning;
    }

    public void setPreventCPSProvisioning(Boolean preventCPSProvisioning) {
        this.preventCPSProvisioning = preventCPSProvisioning;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Long getProjectResponsibleUserId() {
        return projectResponsibleUserId;
    }

    public void setProjectResponsibleUserId(Long projectResponsibleUserId) {
        this.projectResponsibleUserId = projectResponsibleUserId;
    }

    public Long getProjectLeadUserId() {
        return projectLeadUserId;
    }

    public void setProjectLeadUserId(Long projectLeadUserId) {
        this.projectLeadUserId = projectLeadUserId;
    }

    public String getVplsId() {
        return vplsId;
    }

    public void setVplsId(String vplsId) {
        this.vplsId = vplsId;
    }

    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

}
