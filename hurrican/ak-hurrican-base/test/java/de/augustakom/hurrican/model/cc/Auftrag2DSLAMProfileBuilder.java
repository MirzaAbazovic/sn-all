/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 09:18:08
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.tools.lang.DateTools;


/**
 * Entity-Builder fuer Auftrag2DSLAMProfile Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class Auftrag2DSLAMProfileBuilder extends AbstractCCIDModelBuilder<Auftrag2DSLAMProfileBuilder, Auftrag2DSLAMProfile> implements IServiceObject {

    private AuftragBuilder auftragBuilder = null;
    @ReferencedEntityId("dslamProfileId")
    private DSLAMProfileBuilder dslamProfileBuilder;
    private final String bemerkung = null;
    private Long changeReasonId;
    private String userW;
    private Date gueltigVon = DateTools.minusWorkDays(5);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;


    @Override
    protected void beforeBuild() {
        if (userW == null) {
            userW = randomString(8);
        }

        if (changeReasonId == null) {
            changeReasonId = DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT;
        }
    }


    public Auftrag2DSLAMProfileBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withDSLAMProfileBuilder(DSLAMProfileBuilder dslamProfileBuilder) {
        this.dslamProfileBuilder = dslamProfileBuilder;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withChangeReasonId(Long changeReasonId) {
        this.changeReasonId = changeReasonId;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
        return this;
    }

    public Auftrag2DSLAMProfileBuilder withAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
        return this;
    }

}
