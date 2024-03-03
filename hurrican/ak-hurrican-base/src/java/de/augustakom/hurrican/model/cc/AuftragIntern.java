/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2009 16:49:50
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Abbildung eines internen Arbeitsauftrags.
 *
 *
 */
public class AuftragIntern extends AbstractCCHistoryModel implements CCAuftragModel {

    private Long auftragId = null;
    private Long hvtStandortId = null;
    private Long workingTypeRefId = null;
    private String contactName = null;
    private String contactPhone = null;
    private String contactMail = null;
    private Long extServiceProviderId = null;
    private Date extOrderDate = null;
    private String bedarfsnummer = null;
    private Float workingHours = null;
    private String description = null;
    private String projectDirectory = null;

    /**
     * @return Returns the hvtStandortId.
     */
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    /**
     * @param hvtStandortId The hvtStandortId to set.
     */
    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    /**
     * @return Returns the workingTypeRefId.
     */
    public Long getWorkingTypeRefId() {
        return workingTypeRefId;
    }

    /**
     * @param workingTypeRefId The workingTypeRefId to set.
     */
    public void setWorkingTypeRefId(Long workingTypeRefId) {
        this.workingTypeRefId = workingTypeRefId;
    }

    /**
     * @return Returns the contactName.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName The contactName to set.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return Returns the contactPhone.
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * @param contactPhone The contactPhone to set.
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * @return Returns the contactMail.
     */
    public String getContactMail() {
        return contactMail;
    }

    /**
     * @param contactMail The contactMail to set.
     */
    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    /**
     * @return Returns the extServiceProviderId.
     */
    public Long getExtServiceProviderId() {
        return extServiceProviderId;
    }

    /**
     * @param extServiceProviderId The extServiceProviderId to set.
     */
    public void setExtServiceProviderId(Long extServiceProviderId) {
        this.extServiceProviderId = extServiceProviderId;
    }

    /**
     * @return Returns the extOrderDate.
     */
    public Date getExtOrderDate() {
        return extOrderDate;
    }

    /**
     * @param extOrderDate The extOrderDate to set.
     */
    public void setExtOrderDate(Date extOrderDate) {
        this.extOrderDate = extOrderDate;
    }

    /**
     * @return Returns the bedarfsnummer.
     */
    public String getBedarfsnummer() {
        return bedarfsnummer;
    }

    /**
     * @param bedarfsnummer The bedarfsnummer to set.
     */
    public void setBedarfsnummer(String bedarfsnummer) {
        this.bedarfsnummer = bedarfsnummer;
    }

    /**
     * @return Returns the workingHours.
     */
    public Float getWorkingHours() {
        return workingHours;
    }

    /**
     * @param workingHours The workingHours to set.
     */
    public void setWorkingHours(Float workingHours) {
        this.workingHours = workingHours;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#getAuftragId()
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragModel#setAuftragId(java.lang.Integer)
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return the projectDirectory
     */
    public String getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * @param projectDirectory the projectDirectory to set
     */
    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

}


