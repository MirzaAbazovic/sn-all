/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2013 08:26:50
 */
package de.augustakom.hurrican.model.billing.view;

import java.util.*;
import javax.persistence.*;

import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * View-Modell mit Basis-Informationen zu einem Billing-Auftrag.
 */
@Entity
public class BAuftragView extends AbstractBillingModel implements KundenModel {

    private Long auftragNo;
    public static final String AUFTRAG_NO_ORIG = "auftragNoOrig";
    private Long auftragNoOrig;
    private Long kundeNo;
    public static final String PRODUCT_NAME = "productName";
    private String productName;
    public static final String LAST_MODIFIED_BY = "lastModifiedBy";
    private String lastModifiedBy;
    public static final String LAST_MODIFIED_AT = "lastModifiedAt";
    private Date lastModifiedAt;
    public static final String HIST_STATUS = "histStatus";
    private String histStatus;
    public static final String ATYP = "atyp";
    private String atyp;

    @Override
    @Column(name = "CUST_NO")
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    @Id
    @Column(name = "AUFTRAG_NO")
    public Long getAuftragNo() {
        return auftragNo;
    }

    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    @Column(name = "AUFTRAG__NO")
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    @Column(name = "OE_NAME")
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Column(name = "USERW")
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Column(name = "DATEW")
    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Date lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Column(name = "HIST_STATUS")
    public String getHistStatus() {
        return histStatus;
    }

    public void setHistStatus(String histStatus) {
        this.histStatus = histStatus;
    }

    @Column(name = "ATYP")
    public String getAtyp() {
        return atyp;
    }

    public void setAtyp(String atyp) {
        this.atyp = atyp;
    }

}


