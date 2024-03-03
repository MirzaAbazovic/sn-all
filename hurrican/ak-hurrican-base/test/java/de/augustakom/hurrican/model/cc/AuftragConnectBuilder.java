/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 11:02:10
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.DateTools;


@SuppressWarnings("unused")
public class AuftragConnectBuilder extends EntityBuilder<AuftragConnectBuilder, AuftragConnect> {

    private AuftragBuilder auftragBuilder;
    private String produktcode = "xyz";
    private String projektleiter = "Max Mustermann";
    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public AuftragConnectBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AuftragConnectBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigBis = gueltigVon;
        return this;
    }

    public AuftragConnectBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public AuftragConnectBuilder withProduktcode(String produktcode) {
        this.produktcode = produktcode;
        return this;
    }

    public AuftragConnectBuilder withProjektleiter(String projektleiter) {
        this.projektleiter = projektleiter;
        return this;
    }
}


