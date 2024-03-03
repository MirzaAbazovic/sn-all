/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2010 11:06:04
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.DateTools;


/**
 *
 */
@SuppressWarnings("unused")
public class AuftragUMTSBuilder extends EntityBuilder<AuftragUMTSBuilder, AuftragUMTS> {

    private AuftragBuilder auftragBuilder;
    private String userW = randomString(8);
    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public AuftragUMTSBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AuftragUMTSBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigBis = gueltigVon;
        return this;
    }

    public AuftragUMTSBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }
}
