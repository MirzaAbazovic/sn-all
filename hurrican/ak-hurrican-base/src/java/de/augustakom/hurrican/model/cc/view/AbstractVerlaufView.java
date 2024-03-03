/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2005 16:05:31
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.model.shared.iface.PortierungsartModel;


/**
 * Abstrakte View-Klasse fuer die Verlauf-Views (Bauauftrag sowie Projektierung).
 *
 *
 */
public abstract class AbstractVerlaufView extends AbstractCCModel implements CCAuftragModel,
        KundenModel, PortierungsartModel {

    private Long verlaufId = null;
    private Long verlaufAbtId = null;
    private Long verlaufAbtStatusId = null;
    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private String auftragStatus = null;
    private String verlaufStatus = null;
    private String oeName = null; //= Produktname Mistral
    private String produktName = null;
    private Long kundeNo = null;
    private String kundenName = null;
    private Long produktId = null;
    private String vbz = null;
    private Date realisierungstermin = null;
    private Long portierungsartId = null;
    private String portierungsart = null;
    private Boolean observeProcess = null;
    private Long amResponsibility = null;
    private Boolean verlaufNotPossible = null;
    private Long cpsTxState = null;
    private Boolean hasSubOrders = null;

    private boolean guiFinished = false;
    private Boolean cpsProvisioning = null;
    private String baHinweise;

    /**
     * Ueberprueft, ob der Abteilungs-Status auf 'CPS-Provisionierung' bzw. 'CPS erledigt' gesetzt ist. Alternativ:
     * cpsTxState ist gesetzt!
     *
     * @return true, wenn es sich um eine CPS-Provisionierung handelt
     *
     */
    public Boolean getCpsProvisioning() {
        cpsProvisioning = NumberTools.isIn(getVerlaufAbtStatusId(),
                new Number[] { VerlaufStatus.STATUS_CPS_BEARBEITUNG, VerlaufStatus.STATUS_ERLEDIGT_CPS });
        if (!cpsProvisioning) {
            cpsProvisioning = (cpsTxState != null) ? Boolean.TRUE : Boolean.FALSE;
        }
        return cpsProvisioning;
    }

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public String getOeName() {
        return oeName;
    }

    public void setOeName(String oeName) {
        this.oeName = oeName;
    }

    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    public void setRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Long getVerlaufId() {
        return verlaufId;
    }

    public Long getVerlaufAbtId() {
        return verlaufAbtId;
    }

    public void setVerlaufAbtId(Long verlaufAbtId) {
        this.verlaufAbtId = verlaufAbtId;
    }

    public Long getVerlaufAbtStatusId() {
        return verlaufAbtStatusId;
    }

    public void setVerlaufAbtStatusId(Long verlaufStatusId) {
        this.verlaufAbtStatusId = verlaufStatusId;
    }

    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    public Long getProduktId() {
        return produktId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public String getKundenName() {
        return kundenName;
    }

    public void setKundenName(String kundenName) {
        this.kundenName = kundenName;
    }

    public String getPortierungsart() {
        return portierungsart;
    }

    public void setPortierungsart(String portierungsart) {
        this.portierungsart = portierungsart;
    }

    @Override
    public Long getPortierungsartId() {
        return portierungsartId;
    }

    public void setPortierungsartId(Long portierungsartId) {
        this.portierungsartId = portierungsartId;
    }

    public String getProduktName() {
        return produktName;
    }

    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    /**
     * Gibt an, ob der Bauauftrag von der GUI bereits erledigt (z.B. von einer Abteilung abgeschlossen) wurde.
     *
     * @return Returns the guiFinished.
     */
    public boolean isGuiFinished() {
        return guiFinished;
    }

    public void setGuiFinished(boolean guiFinished) {
        this.guiFinished = guiFinished;
        this.setVerlaufStatus("erledigt");
    }

    public Boolean getObserveProcess() {
        return this.observeProcess;
    }

    public void setObserveProcess(Boolean observeProcess) {
        this.observeProcess = observeProcess;
    }

    public String getAuftragStatus() {
        return auftragStatus;
    }

    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

    public String getVerlaufStatus() {
        return verlaufStatus;
    }

    public void setVerlaufStatus(String verlaufStatus) {
        this.verlaufStatus = verlaufStatus;
    }

    public Long getAmResponsibility() {
        return amResponsibility;
    }

    public void setAmResponsibility(Long amResponsibility) {
        this.amResponsibility = amResponsibility;
    }

    public Boolean getVerlaufNotPossible() {
        return verlaufNotPossible;
    }

    public void setVerlaufNotPossible(Boolean verlaufNotPossible) {
        this.verlaufNotPossible = verlaufNotPossible;
    }

    public Long getCpsTxState() {
        return cpsTxState;
    }

    public void setCpsTxState(Long cpsTxState) {
        this.cpsTxState = cpsTxState;
    }

    public Boolean getHasSubOrders() {
        return hasSubOrders;
    }

    public void setHasSubOrders(Boolean hasSubOrders) {
        this.hasSubOrders = hasSubOrders;
    }

    public String getBaHinweise() {
        return baHinweise;
    }

    /**
     * ", " delimited String.
     *
     * @param baHinweise
     */
    public void setBaHinweise(String baHinweise) {
        this.baHinweise = baHinweise;
    }
}


