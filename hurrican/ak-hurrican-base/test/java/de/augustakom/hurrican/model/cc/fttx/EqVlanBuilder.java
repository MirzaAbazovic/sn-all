/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2012 09:06:07
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;

/**
 *
 */
public class EqVlanBuilder extends AbstractCCIDModelBuilder<EqVlanBuilder, EqVlan> {
    protected EquipmentBuilder equipmentBuilder;

    protected CvlanServiceTyp cvlanTyp = CvlanServiceTyp.HSI;
    protected Integer cvlan = 40;
    protected Integer svlanEkp = 100;
    protected Integer svlanOlt = 101;
    protected Integer svlanMdu = 102;
    protected Date gueltigVon = Date.from(DateTools.minusWorkDays(5).toInstant());
    protected Date gueltigBis = DateTools.getHurricanEndDate();
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;

    public EqVlanBuilder withEquipmentBuilder(EquipmentBuilder equipmentBuilder) {
        this.equipmentBuilder = equipmentBuilder;
        return this;
    }

    public EqVlanBuilder withCvlanTyp(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
        return this;
    }

    public EqVlanBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public EqVlanBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public EqVlanBuilder withSvlanEkp(Integer svlanEkp) {
        this.svlanEkp = svlanEkp;
        return this;
    }

    public EqVlanBuilder withAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
        return this;
    }

    public EqVlanBuilder withAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
        return this;
    }

}
