/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.12.2010
 */

package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Definition von zusätzlichen IP Routen zu einem Auftrag.
 */
@Entity
@Table(name = "T_IP_ROUTE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IP_ROUTE_0", allocationSize = 1)
public class IpRoute extends AbstractCCIDModel implements CCAuftragModel {

    private Long auftragId;
    private Long metrik;
    private String description;
    private Boolean deleted;
    private String userW;
    private IPAddress ipAddressRef;

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "METRIK")
    public Long getMetrik() {
        return metrik;
    }

    public void setMetrik(Long metrik) {
        this.metrik = metrik;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "DELETED")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "IP_ADDRESS_ID", nullable = false)
    public IPAddress getIpAddressRef() {
        return ipAddressRef;
    }

    public void setIpAddressRef(IPAddress ipAddressRef) {
        this.ipAddressRef = ipAddressRef;
    }
}
