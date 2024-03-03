/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 09:26:28
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;


/**
 * Entity Builder fuer Leistung2DN Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class Leistung2DNBuilder extends AbstractCCIDModelBuilder<Leistung2DNBuilder, Leistung2DN> {

    private Long dnNo = null;
    @ReferencedEntityId("lbId")
    private LeistungsbuendelBuilder leistungsbuendelBuilder;
    @ReferencedEntityId("leistung4DnId")
    private Leistung4DnBuilder leistung4DnBuilder;
    private Long leistung4DnId = null;
    private String leistungParameter = null;
    private String scvUserRealisierung = null;
    private Date scvRealisierung = null;
    private String scvUserKuendigung = null;
    private Date scvKuendigung = null;
    private String ewsdUserRealisierung = null;
    private Date ewsdRealisierung = null;
    private String ewsdUserKuendigung = null;
    private Date ewsdKuendigung = null;
    private Boolean billingCheck = null;
    private Long parameterId = null;
    private Long cpsTxIdCreation = null;
    @ReferencedEntityId("cpsTxIdCreation")
    private CPSTransactionBuilder cpsTransactionCreationBuilder;
    private Long cpsTxIdCancel = null;
    @ReferencedEntityId("cpsTxIdCancel")
    private CPSTransactionBuilder cpsTransactionCancelBuilder;

    public Leistung2DNBuilder withDnNo(Long dnNo) {
        this.dnNo = dnNo;
        return this;
    }

    public Leistung2DNBuilder withLeistungsbuendelBuilder(LeistungsbuendelBuilder leistungsbuendelBuilder) {
        this.leistungsbuendelBuilder = leistungsbuendelBuilder;
        return this;
    }

    public Leistung2DNBuilder withLeistung4DnBuilder(Leistung4DnBuilder leistung4DnBuilder) {
        this.leistung4DnBuilder = leistung4DnBuilder;
        return this;
    }

    public Leistung2DNBuilder withLeistung4DnId(Long leistung4DnId) {
        this.leistung4DnId = leistung4DnId;
        return this;
    }

    public Leistung2DNBuilder withLeistungParameter(String leistungParameter) {
        this.leistungParameter = leistungParameter;
        return this;
    }

    public Leistung2DNBuilder withScvRealisierung(Date scvRealisierung) {
        this.scvRealisierung = scvRealisierung;
        return this;
    }

    public Leistung2DNBuilder withScvKuendigung(Date scvKuendigung) {
        this.scvKuendigung = scvKuendigung;
        return this;
    }

    public Leistung2DNBuilder withParameterId(Long parameterId) {
        this.parameterId = parameterId;
        return this;
    }

    public Leistung2DNBuilder withCpsTxCreationBuilder(CPSTransactionBuilder cpsTransactionCreationBuilder) {
        this.cpsTransactionCreationBuilder = cpsTransactionCreationBuilder;
        return this;
    }

    public Leistung2DNBuilder withCpsTxCancelBuilder(CPSTransactionBuilder cpsTransactionCancelBuilder) {
        this.cpsTransactionCancelBuilder = cpsTransactionCancelBuilder;
        return this;
    }

    public Leistung2DNBuilder withEwsdRealisierung(Date ewsdRealisierung) {
        this.ewsdRealisierung = ewsdRealisierung;
        return this;
    }

    public Leistung2DNBuilder withEwsdKuendigung(Date ewsdKuendigung) {
        this.ewsdKuendigung = ewsdKuendigung;
        return this;
    }

    /**
     * @see de.augustakom.common.model.EntityBuilder#beforeBuild()
     */
    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        if (this.dnNo == null) {
            this.dnNo = randomLong(100000000);
        }
        if (this.scvRealisierung == null) {
            this.scvRealisierung = new Date();
        }
    }

}


