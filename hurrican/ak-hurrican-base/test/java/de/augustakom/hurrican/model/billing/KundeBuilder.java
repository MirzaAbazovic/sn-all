/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 13:50:55
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unused")
public class KundeBuilder extends BillingEntityBuilder<KundeBuilder, Kunde> {

    private Long kundeNo = null;
    private Long resellerKundeNo = null;
    private Long areaNo = null;
    private Integer brancheNo = null;
    private String name = null;
    private String vorname = null;
    private String hauptRufnummer = null;
    private String rnGeschaeft = null;
    private String rnFax = null;
    private String rnMobile = null;
    private String email = null;
    private String kundenTyp = null;
    private Long hauptKundenNo = null;
    private Long kundenbetreuerNo = null;
    private String vip = null;
    private Boolean hauptkunde = null;
    private Boolean kunde = null;
    private Boolean fernkatastrophe = null;
    private Long postalAddrNo = null;
    private Date validFrom = null;
    private Date createdAt = null;

    public KundeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public KundeBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public KundeBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public KundeBuilder withKundeTyp(String kundenTyp) {
        this.kundenTyp = kundenTyp;
        return this;
    }

    public KundeBuilder withHauptKundeNo(Long hauptKundenNo) {
        this.hauptKundenNo = hauptKundenNo;
        return this;
    }

    public KundeBuilder withResellerKundeNo(Long resellerKundeNo) {
        this.resellerKundeNo = resellerKundeNo;
        return this;
    }

    public KundeBuilder withHauptRufnummer(String hauptRufnummer) {
        this.hauptRufnummer = hauptRufnummer;
        return this;
    }

    public KundeBuilder withRnFax(String rnFax) {
        this.rnFax = rnFax;
        return this;
    }

    public KundeBuilder withRnMobile(String rnMobile) {
        this.rnMobile = rnMobile;
        return this;
    }

    public KundeBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
}


