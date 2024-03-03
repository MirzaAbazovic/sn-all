/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 17:24:09
 */
package de.mnet.wita.model;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.model.SessionFactoryAware;
import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.mnet.wbci.model.ProduktGruppe;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class VorabstimmungBuilder extends AbstractCCIDModelBuilder<VorabstimmungBuilder, Vorabstimmung> {

    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder;
    private Long auftragId;
    private String endstelleTyp;

    private Carrier carrier;
    private ProduktGruppe produktGruppe = ProduktGruppe.TAL;
    private String providerVtrNr = "12312312";
    private String providerLbz = "96X/82100/82100/0000000";
    private String bestandssucheOnkz;
    private String bestandssucheDn;
    private String bestandssucheDirectDial;
    private CCAddress previousLocationAddress;

    public VorabstimmungBuilder() {
        carrier = new Carrier();
        carrier.setId(getLongId());
        carrier.setOrderNo(Integer.valueOf(3));
        carrier.setCbNotwendig(Boolean.TRUE);
        carrier.setElTalEmpfId("D" + randomInt(100, 999) + "-" + +randomInt(100, 999));
        carrier.setCompanyName("Test-Company");
        carrier.setHasWitaInterface(Boolean.TRUE);
    }

    public VorabstimmungBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public VorabstimmungBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public VorabstimmungBuilder withEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
        return this;
    }

    public VorabstimmungBuilder withCarrier(Carrier carrier) {
        this.carrier = carrier;
        return this;
    }

    /**
     * ATTENTION: do not use when persisting entities therefore just use within "unit"-tests
     */
    public VorabstimmungBuilder withCarrierDtag() {
        carrier = new Carrier();
        carrier.setId(Carrier.ID_DTAG);
        carrier.setOrderNo(Integer.valueOf(2));
        carrier.setCbNotwendig(Boolean.TRUE);
        carrier.setElTalEmpfId("D001-033");
        carrier.setCompanyName("Deutsche Telekom AG");
        carrier.setHasWitaInterface(Boolean.TRUE);

        produktGruppe = ProduktGruppe.DTAG_ANY;
        return this;
    }

    /**
     * ATTENTION: do not use when persisting entities therefore just use within "unit"-tests
     */
    public VorabstimmungBuilder withCarrierO2() {
        carrier = new Carrier();
        carrier.setId(Carrier.ID_O2);
        carrier.setOrderNo(Integer.valueOf(3));
        carrier.setCbNotwendig(Boolean.TRUE);
        carrier.setCompanyName("O2 (Germany) GmbH & Co. OHG");
        return this;
    }

    /**
     * ATTENTION: do not use when persisting entities therefore just use within "unit"-tests
     */
    public VorabstimmungBuilder withCarrierNotOnWita() {
        carrier = new Carrier();
        carrier.setId(Carrier.ID_O2);
        carrier.setOrderNo(Integer.valueOf(3));
        carrier.setCbNotwendig(Boolean.TRUE);
        carrier.setCompanyName("O2 (Germany) GmbH & Co. OHG");
        carrier.setHasWitaInterface(Boolean.FALSE);
        return this;
    }

    public VorabstimmungBuilder withBestandssucheEinzelanschluss() {
        bestandssucheOnkz = "89";
        bestandssucheDn = "123456";
        bestandssucheDirectDial = null;
        return this;
    }

    public VorabstimmungBuilder withBestandssucheAnlagenanschluss() {
        return withBestandssucheAnlagenanschluss("123456");
    }

    public VorabstimmungBuilder withBestandssucheAnlagenanschluss(String bestandssucheDn) {
        bestandssucheOnkz = "89";
        this.bestandssucheDn = bestandssucheDn;
        bestandssucheDirectDial = "1234";
        return this;
    }

    public VorabstimmungBuilder withProduktGruppe(ProduktGruppe produktGruppe) {
        this.produktGruppe = produktGruppe;
        return this;
    }

    public VorabstimmungBuilder withPreviousLocationAdress(CCAddress previousLocationAddress) {
        this.previousLocationAddress = previousLocationAddress;
        return this;
    }

    public VorabstimmungBuilder withProviderLbz(String providerLbz) {
        this.providerLbz = providerLbz;
        return this;
    }
}
