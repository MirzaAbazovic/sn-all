/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2005 16:13:03
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * View-Modell zur Anzeige der freizugebenden Physik
 *
 *
 */
public class PhysikFreigebenView extends AbstractCCModel implements DebugModel, CCAuftragModel {

    private Long auftragId;
    private Long auftragNoOrig;
    /**
     * Flag bestimmt, ob die zugehoerige Rangierung freigegeben werden soll.
     */
    private Boolean freigeben;
    private String hvt;
    private String rangierungBemerkung;
    private Long rangierId;
    private Long endstelleId;
    private Long kundenNo;
    private String techProduct;
    private String billingProduct;
    private String auftragStatus;
    private String baStatus;
    private String niederlassung;
    private Date cbKuendigung;
    private String clarifyInfo;
    private String portGesamtStatus;
    private Long auftragStatusId;
    private Boolean inBearbeitung;
    private Boolean isLocked;


    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public Long getRangierId() {
        return rangierId;
    }

    public void setRangierId(Long rangierungId) {
        this.rangierId = rangierungId;
    }

    public Long getEndstelleId() {
        return endstelleId;
    }

    public void setEndstelleId(Long endstelleID) {
        this.endstelleId = endstelleID;
    }

    public Boolean getFreigeben() {
        return freigeben;
    }

    public void setFreigeben(Boolean freigeben) {
        this.freigeben = freigeben;
    }

    public String getHvt() {
        return hvt;
    }

    public void setHvt(String hvt) {
        this.hvt = hvt;
    }

    public String getRangierungBemerkung() {
        return rangierungBemerkung;
    }

    public void setRangierungBemerkung(String rangierungBemerkung) {
        this.rangierungBemerkung = rangierungBemerkung;
    }

    public Long getKundenNo() {
        return kundenNo;
    }

    public void setKundenNo(Long kundenNo) {
        this.kundenNo = kundenNo;
    }

    public String getTechProduct() {
        return techProduct;
    }

    public void setTechProduct(String produkt) {
        this.techProduct = produkt;
    }

    public String getBillingProduct() {
        return billingProduct;
    }

    public void setBillingProduct(String produktBilling) {
        this.billingProduct = produktBilling;
    }

    public String getAuftragStatus() {
        return auftragStatus;
    }

    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

    public String getBaStatus() {
        return baStatus;
    }

    public void setBaStatus(String baStatus) {
        this.baStatus = baStatus;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public Date getCbKuendigung() {
        return cbKuendigung;
    }

    public void setCbKuendigung(Date cbKuendigung) {
        this.cbKuendigung = cbKuendigung;
    }

    public String getClarifyInfo() {
        return clarifyInfo;
    }

    public void setClarifyInfo(String clarifyInfo) {
        this.clarifyInfo = clarifyInfo;
    }

    public String getPortGesamtStatus() {
        return portGesamtStatus;
    }

    public void setPortGesamtStatus(String portGesamtStatus) {
        this.portGesamtStatus = portGesamtStatus;
    }

    public Long getAuftragStatusId() {
        return auftragStatusId;
    }

    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    public Boolean getInBearbeitung() {
        return inBearbeitung;
    }

    public void setInBearbeitung(Boolean inBearbeitung) {
        this.inBearbeitung = inBearbeitung;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + PhysikFreigebenView.class.getName());
            logger.debug("  AuftragDatenID  : " + getAuftragId());
            logger.debug("  RangierungID  : " + getRangierId());
        }
    }

}
