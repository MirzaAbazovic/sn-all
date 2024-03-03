/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2012 12:57:31
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.time.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.mnet.common.tools.DateConverterUtils;


@SuppressWarnings("unused")
public class Auftrag2EkpFrameContractBuilder extends EntityBuilder<Auftrag2EkpFrameContractBuilder, Auftrag2EkpFrameContract> {

    private Long id;
    private AuftragBuilder auftragBuilder;
    private EkpFrameContractBuilder ekpFrameContractBuilder;
    private LocalDate assignedFrom = LocalDate.now();
    private LocalDate assignedTo = DateConverterUtils.asLocalDate(DateTools.getHurricanEndDate());
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;

    public Auftrag2EkpFrameContractBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withEkpFrameContractBuilder(EkpFrameContractBuilder ekpFrameContractBuilder) {
        this.ekpFrameContractBuilder = ekpFrameContractBuilder;
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withAssignedFrom(LocalDate assignedFrom) {
        this.assignedFrom = assignedFrom;
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withAssignedTo(LocalDate assignedTo) {
        this.assignedTo = assignedTo;
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withRandomId() {
        this.id = RandomTools.createLong();
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
        return this;
    }

    public Auftrag2EkpFrameContractBuilder withAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
        return this;
    }

}


