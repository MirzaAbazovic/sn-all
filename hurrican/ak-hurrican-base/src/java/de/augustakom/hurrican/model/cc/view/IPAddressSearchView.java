/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 14:21:50
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * View-Modell, fuer die Anzeige von IPs inklusive bestimmter Auftragsdaten mit Fokus auf die IP-Suche
 */
public class IPAddressSearchView extends Observable implements CCAuftragModel, KundenModel {

    private Long billingOrderNo;
    private IPAddress ipAddress;
    private String tdn;
    private Long auftragId;
    private String purpose;
    private Date gueltigVon;
    private Date gueltigBis;
    private String ipType;
    private Long kundeNo;

    public Long getBillingOrderNo() {
        return billingOrderNo;
    }

    public void setBillingOrderNo(Long billingOrderNo) {
        this.billingOrderNo = billingOrderNo;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTdn() {
        return tdn;
    }

    public void setTdn(String tdn) {
        this.tdn = tdn;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Date getGueltigVon() {
        return gueltigVon;
    }

    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    public Date getGueltigBis() {
        return gueltigBis;
    }

    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = (prime * result) + ((billingOrderNo == null) ? 0 : billingOrderNo.hashCode());
        result = (prime * result) + ((auftragId == null) ? 0 : auftragId.hashCode());
        result = (prime * result) + ((gueltigBis == null) ? 0 : gueltigBis.hashCode());
        result = (prime * result) + ((gueltigVon == null) ? 0 : gueltigVon.hashCode());
        result = (prime * result) + ((ipType == null) ? 0 : ipType.hashCode());
        result = (prime * result) + ((purpose == null) ? 0 : purpose.hashCode());
        result = (prime * result) + ((tdn == null) ? 0 : tdn.hashCode());
        result = (prime * result) + ((kundeNo == null) ? 0 : kundeNo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IPAddressSearchView other = (IPAddressSearchView) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null) {
                return false;
            }
        }
        else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (billingOrderNo == null) {
            if (other.billingOrderNo != null) {
                return false;
            }
        }
        else if (!billingOrderNo.equals(other.billingOrderNo)) {
            return false;
        }
        if (auftragId == null) {
            if (other.auftragId != null) {
                return false;
            }
        }
        else if (!auftragId.equals(other.auftragId)) {
            return false;
        }
        if (gueltigBis == null) {
            if (other.gueltigBis != null) {
                return false;
            }
        }
        else if (!gueltigBis.equals(other.gueltigBis)) {
            return false;
        }
        if (gueltigVon == null) {
            if (other.gueltigVon != null) {
                return false;
            }
        }
        else if (!gueltigVon.equals(other.gueltigVon)) {
            return false;
        }
        if (!ipType.equals(other.ipType)) {
            return false;
        }
        if (purpose == null) {
            if (other.purpose != null) {
                return false;
            }
        }
        else if (!purpose.equals(other.purpose)) {
            return false;
        }
        if (tdn == null) {
            if (other.tdn != null) {
                return false;
            }
        }
        else if (!tdn.equals(other.tdn)) {
            return false;
        }

        if (kundeNo == null) {
            if (other.kundeNo != null) {
                return false;
            }
        }
        else if (!kundeNo.equals(other.kundeNo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("IPAddressSearchView [billingOrderNo=%s, address=%s, tdn=%s, auftragId=%d, purpose=%s, gueltigVon=%s, gueltigBis=%s, ipType=%s, kundeNo=%d]",
                        billingOrderNo, (ipAddress != null) ? ipAddress.getAbsoluteAddress() : null, tdn, auftragId,
                        purpose, gueltigVon, gueltigBis, ipType, kundeNo);
    }

}


