/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.2004 07:55:34
 */
package de.augustakom.hurrican.model.billing.view;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.augustakom.common.model.AbstractObservable;


/**
 * Modell fuer die Abbildung einer Internet-Domain.
 */
@Entity
@Table(name = "V_DOMAIN_HURRICAN")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class IntDomain extends AbstractObservable implements Serializable {

    private Long domainId = null;
    private String domainName = null;
    private Date confirmDate = null;
    private Date deleteDate = null;
    public static final String TAIFUN_AUFTRAG_ID = "taifunAuftragId";
    private Long taifunAuftragId = null;

    @Column(name = "DOMID")
    @Id
    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * @return Returns the domainName.
     */
    @Column(name = "DOMAIN")
    public String getDomainName() {
        return domainName;
    }

    /**
     * @param domainName The domainName to set.
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Column(name = "AKTIVIERT")
    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    @Column(name = "DEAKTIVIERT")
    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    @Column(name = "TAIFUN_AUF_ID")
    public Long getTaifunAuftragId() {
        return taifunAuftragId;
    }

    public void setTaifunAuftragId(Long taifunAuftragId) {
        this.taifunAuftragId = taifunAuftragId;
    }


}


