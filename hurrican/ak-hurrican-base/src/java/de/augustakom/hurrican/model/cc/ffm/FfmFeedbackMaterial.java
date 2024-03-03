/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import java.math.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotEmpty;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell-Klasse zur Abbildung einer FFM-Materialrueckmeldung.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterial")
@Table(name = "T_FFM_FEEDBACK_MATERIAL")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FFM_FEEDBACK_MATERIAL_0", allocationSize = 1)
public class FfmFeedbackMaterial extends AbstractCCIDModel {

    private static final long serialVersionUID = -5740340835278135124L;
    private String workforceOrderId;
    private String materialId;
    private String serialNumber;
    private String summary;
    private String description;
    private BigDecimal quantity;
    private Boolean processed;

    @Column(name = "WORKFORCE_ORDER_ID")
    @NotEmpty
    public String getWorkforceOrderId() {
        return workforceOrderId;
    }
    public void setWorkforceOrderId(String workforceOrderId) {
        this.workforceOrderId = workforceOrderId;
    }

    @Column(name = "MATERIAL_ID")
    @NotEmpty
    public String getMaterialId() {
        return materialId;
    }
    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    @Column(name = "SERIAL_NUMBER")
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(name = "SUMMARY")
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "QUANTITY")
    @NotNull
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name = "PROCESSED")
    @NotNull
    public Boolean getProcessed() {
        return processed;
    }
    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
