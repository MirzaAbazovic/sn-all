/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2015
 */
package de.augustakom.hurrican.model.cc;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;


public class HvtUmzugBuilder extends EntityBuilder<HvtUmzugBuilder, HvtUmzug> {

    private Long id;
    @ReferencedEntityId("hvtStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    @ReferencedEntityId("hvtStandortDestination")
    private HVTStandortBuilder hvtStandortDestinationBuilder;
    private String kvzNr = "A012";
    private String bearbeiter = randomString(10);
    private HvtUmzugStatus status = HvtUmzugStatus.OFFEN;
    private LocalDate schalttag = LocalDateTime.now().plusDays(30).toLocalDate();
    private Date importAm = new Date();
    private Blob exelBlob = null;
    private Set<HvtUmzugDetail> hvtUmzugDetails = new HashSet<>();
    private KvzSperre kvzSperre;

    public HvtUmzugBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public HvtUmzugBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public HvtUmzugBuilder withHvtStandortDestinationBuilder(HVTStandortBuilder hvtStandortDestinationBuilder) {
        this.hvtStandortDestinationBuilder = hvtStandortDestinationBuilder;
        return this;
    }

    public HvtUmzugBuilder withKvzNr(String kvzNr) {
        this.kvzNr = kvzNr;
        return this;
    }

    public HvtUmzugBuilder withBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
        return this;
    }

    public HvtUmzugBuilder withStatus(HvtUmzugStatus status) {
        this.status = status;
        return this;
    }

    public HvtUmzugBuilder withSchalttag(LocalDate schalttag) {
        this.schalttag = schalttag;
        return this;
    }

    public HvtUmzugBuilder withImportAm(LocalDateTime importAm) {
        this.importAm = importAm != null ? Date.from(importAm.atZone(ZoneId.systemDefault()).toInstant()) : null;
        return this;
    }

    public HvtUmzugBuilder withHvtUmzugDetails(Set<HvtUmzugDetail> hvtUmzugDetails) {
        this.hvtUmzugDetails = hvtUmzugDetails;
        return this;
    }

    public HvtUmzugBuilder withKvzSperre(KvzSperre kvzSperre) {
        this.kvzSperre = kvzSperre;
        return this;
    }

}
