/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2009 12:31:38
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.IdFieldName;
import de.augustakom.common.model.ReferencedEntityId;


/**
 *
 */
@SuppressWarnings("unused")
public class BAuftragBuilder extends BillingEntityBuilder<BAuftragBuilder, BAuftrag> {

    private Long auftragNo = randomLong(100000000);
    private Long auftragNoOrig;
    private String oldAuftragNoOrig;
    private String sapId;
    private String histStatus;
    private Integer histCnt;
    private Boolean histLast;
    private String atyp;
    private Integer astatus;
    private Long kundeNo;
    private Long oeNoOrig;
    private Date wunschTermin;
    private Date vertragsdatum;
    private String user;
    private Integer bundleOrderNo;
    private Long rechInfoNoOrig;
    @ReferencedEntityId("apAddressNo")
    @IdFieldName("adresseNo")
    @DontCreateBuilder
    private AdresseBuilder apAddressBuilder;
    private Long apAddressNo;
    private Long haendlerNo;
    private Long vertragsLaufzeit;
    private Date eingangsdatum;
    private Date tatsaechlicherTermin;
    private Date ausfuehrZeit;
    private Date gueltigVon;
    private Date gueltigBis;
    private Date kuendigungsdatum;
    private Date vertragsendedatum;
    private String bearbeiterKundeName;
    private String bearbeiterKundeRN;
    private String bearbeiterKundeFax;
    private String bearbeiterKundeEmail;
    private Long hauptAuftragNo;

    public BAuftragBuilder withAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
        return this;
    }

    public BAuftragBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public BAuftragBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public BAuftragBuilder withApAddressBuilder(AdresseBuilder apAddressBuilder) {
        this.apAddressBuilder = apAddressBuilder;
        return this;
    }

    public BAuftragBuilder withApAddressNo(Long apAddressNo) {
        this.apAddressNo = apAddressNo;
        return this;
    }

    public BAuftragBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public BAuftragBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public BAuftragBuilder withVertragsdatum(Date vertragsdatum) {
        this.vertragsdatum = vertragsdatum;
        return this;
    }

    public BAuftragBuilder withHistStatus(String histStatus) {
        this.histStatus = histStatus;
        return this;
    }

    public BAuftragBuilder withHistCnt(Integer histCnt) {
        this.histCnt = histCnt;
        return this;
    }

    public BAuftragBuilder withHistLast(Boolean histLast) {
        this.histLast = histLast;
        return this;
    }

    public BAuftragBuilder withAtyp(String atyp) {
        this.atyp = atyp;
        return this;
    }

    public BAuftragBuilder withAstatus(Integer astatus) {
        this.astatus = astatus;
        return this;
    }

    public BAuftragBuilder withKuendigungsdatum(Date kuendigungsdatum) {
        this.kuendigungsdatum = kuendigungsdatum;
        return this;
    }

    public BAuftragBuilder withVertragsendedatum(Date vertragsendedatum) {
        this.vertragsendedatum = vertragsendedatum;
        return this;
    }

    public BAuftragBuilder withOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
        return this;
    }

    public BAuftragBuilder withVertragsLaufzeit(Long vertragsLaufzeit) {
        this.vertragsLaufzeit = vertragsLaufzeit;
        return this;
    }

    public BAuftragBuilder withBearbeiterKundeName(String bearbeiterKundeName) {
        this.bearbeiterKundeName = bearbeiterKundeName;
        return this;
    }

    public BAuftragBuilder withBearbeiterKundeRN(String bearbeiterKundeRN) {
        this.bearbeiterKundeRN = bearbeiterKundeRN;
        return this;
    }

    public BAuftragBuilder withBearbeiterKundeFax(String bearbeiterKundeFax) {
        this.bearbeiterKundeFax = bearbeiterKundeFax;
        return this;
    }

    public BAuftragBuilder withBearbeiterKundeEmail(String bearbeiterKundeEmail) {
        this.bearbeiterKundeEmail = bearbeiterKundeEmail;
        return this;
    }

    public BAuftragBuilder withHauptAuftragNo(Long hauptAuftragNo) {
        this.hauptAuftragNo = hauptAuftragNo;
        return this;
    }

    public BAuftragBuilder withRechInfoNoOrig(Long rechInfoNoOrig) {
        this.rechInfoNoOrig = rechInfoNoOrig;
        return this;
    }

    public BAuftragBuilder withSAPId(String sapId) {
        this.sapId = sapId;
        return this;
    }
}
