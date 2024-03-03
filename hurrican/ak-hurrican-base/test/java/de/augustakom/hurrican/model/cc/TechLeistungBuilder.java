/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2009 09:05:10
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;


/**
 * Entity Builder fuer TechLeistung Objekte
 *
 *
 */
@SuppressWarnings("unused")
public class TechLeistungBuilder extends AbstractCCIDModelBuilder<TechLeistungBuilder, TechLeistung> {

    {id = getLongId();}

    private Long externLeistungNo = null;
    private Long externMiscNo = null;
    private String name = randomString(10);
    private String typ = randomString(10);
    private Long longValue = null;
    private String strValue = null;
    private String parameter = null;
    private String prodNameStr = randomString(10);
    private String description = null;
    private Boolean checkQuantity = null;
    private Boolean snapshotRel = null;
    private Date gueltigVon = DateTools.minusWorkDays(5);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private Boolean preventAutoDispatch = Boolean.FALSE;
    private Boolean autoExpire = Boolean.FALSE;

    public TechLeistungBuilder withRandomStrValue() {
        this.strValue = randomString(10);
        return this;
    }

    public TechLeistungBuilder withRandomName() {
        this.name = randomString(10);
        return this;
    }

    public TechLeistungBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TechLeistungBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public TechLeistungBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public TechLeistungBuilder withTyp(String typ) {
        this.typ = typ;
        return this;
    }

    public TechLeistungBuilder withLongValue(Long longValue) {
        this.longValue = longValue;
        return this;
    }

    public TechLeistungBuilder withProdNameStr(String prodNameStr) {
        this.prodNameStr = prodNameStr;
        return this;
    }

    public TechLeistungBuilder withExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
        return this;
    }

    public TechLeistungBuilder withPreventAutoDispatch(Boolean preventAutoDispatch) {
        this.preventAutoDispatch = preventAutoDispatch;
        return this;
    }

    public TechLeistungBuilder withAutoExpire(Boolean autoExpire) {
        this.autoExpire = autoExpire;
        return this;
    }
}
