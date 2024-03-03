/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2012 16:02:47
 */
package de.mnet.wita.model;

import java.time.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.model.SessionFactoryAware;
import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Carrier;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class VorabstimmungAbgebendBuilder extends
        AbstractCCIDModelBuilder<VorabstimmungAbgebendBuilder, VorabstimmungAbgebend> {

    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder;
    private Long auftragId;
    private String endstelleTyp;

    private Carrier carrier;
    private LocalDate abgestimmterProdiverwechselTermin;
    private Boolean rueckmeldung;
    private String bemerkung;

    public VorabstimmungAbgebendBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public VorabstimmungAbgebendBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public VorabstimmungAbgebendBuilder withEndstelleTyp(String endstellenTyp) {
        this.endstelleTyp = endstellenTyp;
        return this;
    }

    public VorabstimmungAbgebendBuilder withCarrier(Carrier carrier) {
        this.carrier = carrier;
        return this;
    }

    /**
     * ATTENTION: do not use when persisting entities therefore just use within "unit"-tests
     */
    public VorabstimmungAbgebendBuilder withCarrierDtag() {
        carrier = new Carrier();
        carrier.setId(Carrier.ID_DTAG);
        carrier.setOrderNo(Integer.valueOf(2));
        carrier.setCbNotwendig(Boolean.TRUE);
        carrier.setElTalEmpfId("D001-033");
        carrier.setCompanyName("Deutsche Telekom AG");
        carrier.setHasWitaInterface(Boolean.TRUE);
        return this;
    }

    public VorabstimmungAbgebendBuilder withAbgestimmterProdiverwechselTermin(
            LocalDate abgestimmterProdiverwechselTermin) {
        this.abgestimmterProdiverwechselTermin = abgestimmterProdiverwechselTermin;
        return this;
    }

    public VorabstimmungAbgebendBuilder withRueckmeldung(Boolean rueckmeldung) {
        this.rueckmeldung = rueckmeldung;
        return this;
    }

    public VorabstimmungAbgebendBuilder withBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }
}
