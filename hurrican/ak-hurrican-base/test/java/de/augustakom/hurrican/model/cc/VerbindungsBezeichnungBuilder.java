/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 13:59:13
 */
package de.augustakom.hurrican.model.cc;


/**
 *
 */
@SuppressWarnings("unused")
public class VerbindungsBezeichnungBuilder extends AbstractCCIDModelBuilder<VerbindungsBezeichnungBuilder, VerbindungsBezeichnung> {

    private String vbz = null;
    private String wbciLineId = null;
    private String customerIdent = null;
    private String kindOfUseProduct = null;
    private String kindOfUseType = null;
    private Integer uniqueCode = null;
    private Boolean overwritten = null;

    /**
     * @see de.augustakom.common.model.EntityBuilder#beforeBuild()
     */
    @Override
    protected void beforeBuild() {
        if ((vbz == null) && (uniqueCode == null)) {
            vbz = randomString(50);
        }
    }


    public VerbindungsBezeichnungBuilder withCustomerIdent(String customerIdent) {
        this.customerIdent = customerIdent;
        return this;
    }

    public VerbindungsBezeichnungBuilder withVbz(String vbz) {
        this.vbz = vbz;
        return this;
    }

    public VerbindungsBezeichnungBuilder withUniqueCode(Integer uniqueCode) {
        this.uniqueCode = uniqueCode;
        calculateVbz();
        return this;
    }

    public VerbindungsBezeichnungBuilder withRandomUniqueCode() {
        this.uniqueCode = randomInt(90000000, 99999999);
        calculateVbz();
        return this;
    }

    public VerbindungsBezeichnungBuilder withKindOfUseProduct(String kindOfUseProduct) {
        this.kindOfUseProduct = kindOfUseProduct;
        calculateVbz();
        return this;
    }

    public VerbindungsBezeichnungBuilder withKindOfUseType(String kindOfUseType) {
        this.kindOfUseType = kindOfUseType;
        calculateVbz();
        return this;
    }

    void calculateVbz() {
        vbz = VerbindungsBezeichnung.createVbzValue(null, customerIdent, uniqueCode, kindOfUseProduct, kindOfUseType);
    }

    public VerbindungsBezeichnungBuilder withOverwritten(Boolean overwritten) {
        this.overwritten = overwritten;
        return this;
    }

    public VerbindungsBezeichnungBuilder withWbciLineId(String wbciLineId) {
        this.wbciLineId = wbciLineId;
        return this;
    }

}
