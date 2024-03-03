/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2017 08:57:06
 */
package de.mnet.hurrican.wholesale.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modelklasse zur Auditierung von Wholesale Request Informationen.
 */
@Entity
@Table(name = "T_WHOLESALE_AUDIT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WHOLESALE_AUDIT_0", allocationSize = 1)
public class WholesaleAudit extends AbstractCCIDModel {

    private static final long serialVersionUID = 7087372071685235614L;

    private String vorabstimmungsId;
    private String beschreibung;
    private String bearbeiter;
    private LocalDateTime datum;
    private PvStatus status;
    private String requestXml;

    @NotNull
    @Column(name = "VORABSTIMMUNGSID")
    public String getVorabstimmungsId() {
        return this.vorabstimmungsId;
    }
    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    @Column(name = "BESCHREIBUNG")
    public String getBeschreibung() {
        return this.beschreibung;
    }
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @Column(name = "BEARBEITER")
    public String getBearbeiter() {
        return this.bearbeiter;
    }
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    @Column(name = "DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    public LocalDateTime getDatum() {return this.datum; }
    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    public PvStatus getStatus() {
        return this.status;
    }

    public void setStatus(PvStatus status) {
        this.status = status;
    }

    @Lob
    @Column(name = "REQUEST_XML")
    public String getRequestXml() {
        return requestXml;
    }
    public void setRequestXml(String requestXml) {
        this.requestXml = requestXml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WholesaleAudit))
            return false;
        if (!super.equals(o))
            return false;

        WholesaleAudit that = (WholesaleAudit) o;

        if (vorabstimmungsId != null ? !vorabstimmungsId.equals(that.vorabstimmungsId) : that.vorabstimmungsId != null)
            return false;
        return requestXml != null ? requestXml.equals(that.requestXml) : that.requestXml == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (vorabstimmungsId != null ? vorabstimmungsId.hashCode() : 0);
        result = 31 * result + (requestXml != null ? requestXml.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WholesaleAudit{" +
                "vorabstimmungsId='" + vorabstimmungsId + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", bearbeiter='" + bearbeiter + '\'' +
                ", datum='" + datum + '\'' +
                ", status='" + status + '\'' +
                ", requestXml='" + requestXml + '\'' +
                '}';
    }
}
