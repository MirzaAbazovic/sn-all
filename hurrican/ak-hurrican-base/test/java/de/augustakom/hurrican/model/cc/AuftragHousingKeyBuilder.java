/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2011 12:21:20
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.housing.TransponderBuilder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;


/**
 * Hilfsklasse um AuftragHousingKey Entitaeten fuer Tests zu bauen
 */
@SuppressWarnings("unused")
public class AuftragHousingKeyBuilder extends EntityBuilder<AuftragHousingKeyBuilder, AuftragHousingKey> {

    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder;
    @ReferencedEntityId("auftragHousingId")
    private AuftragHousingBuilder auftragHousingBuilder;

    private Long auftragId = null;
    @ReferencedEntityId("transponderId")
    private TransponderBuilder transponderBuilder = new TransponderBuilder();
    private TransponderGroup transponderGroup;
    private String customerFirstName = randomString(20);
    private String customerLastName = randomString(20);
    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public AuftragHousingKeyBuilder withTransponderBuilder(TransponderBuilder transponderBuilder) {
        this.transponderBuilder = transponderBuilder;
        return this;
    }

    public AuftragHousingKeyBuilder withAuftragId(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragHousingKeyBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public AuftragHousingKeyBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AuftragHousingBuilder getAuftragHousingBuilder() {
        return auftragHousingBuilder;
    }

    public AuftragHousingKeyBuilder withAuftragHousingBuilder(AuftragHousingBuilder auftragHousingBuilder) {
        this.auftragHousingBuilder = auftragHousingBuilder;
        return this;
    }

    public AuftragHousingKeyBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigBis = gueltigVon;
        return this;
    }

    public AuftragHousingKeyBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public AuftragHousingKeyBuilder withTransponderGroup(TransponderGroup transponderGroup) {
        this.transponderGroup = transponderGroup;
        return this;
    }

}


