/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 17:24:09
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;

/**
 * Builder fuer Carrierbestellung-Objekte
 */
@SuppressWarnings("unused")
public class CarrierbestellungBuilder extends AbstractCCIDModelBuilder<CarrierbestellungBuilder, Carrierbestellung> {
    private Carrierbestellung2EndstelleBuilder cb2EsBuilder = null;
    private Long carrier = Carrier.ID_DTAG;
    private Date vorgabedatum = null;
    private Date bestelltAm = null;
    private Date zurueckAm = null;
    private Date bereitstellungAm = null;
    private Boolean kundeVorOrt = null;
    private String lbz = null;
    private String vtrNr = null;
    private String aqs = randomString(70);
    private String ll = randomString(70);
    private String negativeRm = null;
    private Date wiedervorlage = null;
    private Date kuendigungAnCarrier = null;
    private Date kuendBestaetigungCarrier = null;
    @ReferencedEntityId("auftragId4TalNA")
    private AuftragBuilder auftragId4TalNABuilder = null;
    private Long talNATyp = null;
    private Long aiAddressId = null;
    private String maxBruttoBitrate = null;
    private Long eqOutId = null;
    @DontCreateBuilder
    private CarrierbestellungVormieterBuilder carrierbestellungVormieterBuilder = null;
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster = null;

    public CarrierbestellungBuilder withAuftrag4TalNaBuilder(AuftragBuilder auftragId4TalNABuilder) {
        this.auftragId4TalNABuilder = auftragId4TalNABuilder;
        return this;
    }

    public Carrierbestellung2EndstelleBuilder getCb2EsBuilder() {
        return cb2EsBuilder;
    }

    public CarrierbestellungBuilder withCarrier(Long carrier) {
        this.carrier = carrier;
        return this;
    }

    public CarrierbestellungBuilder withLbz(String lbz) {
        this.lbz = lbz;
        return this;
    }

    public CarrierbestellungBuilder withKundeVorOrt(Boolean kundeVorOrt) {
        this.kundeVorOrt = kundeVorOrt;
        return this;
    }

    public CarrierbestellungBuilder withVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
        return this;
    }

    public CarrierbestellungBuilder withZurueckAm(Date zurueckAm) {
        this.zurueckAm = zurueckAm;
        return this;
    }

    public CarrierbestellungBuilder withBereitstellungAm(Date bereitstellungAm) {
        this.bereitstellungAm = bereitstellungAm;
        return this;
    }

    public CarrierbestellungBuilder withCb2EsBuilder(Carrierbestellung2EndstelleBuilder cb2EsBuilder) {
        this.cb2EsBuilder = cb2EsBuilder;
        return this;
    }

    public String getAqs() {
        return aqs;
    }

    public CarrierbestellungBuilder withAqs(String aqs) {
        this.aqs = aqs;
        return this;
    }

    public String getLl() {
        return ll;
    }

    public CarrierbestellungBuilder withLl(String ll) {
        this.ll = ll;
        return this;
    }

    public CarrierbestellungBuilder withMaxBruttoBitrate(String maxBruttoBitrate) {
        this.maxBruttoBitrate = maxBruttoBitrate;
        return this;
    }

    public CarrierbestellungBuilder withCarrierbestellungVormieterBuilder(CarrierbestellungVormieterBuilder carrierbestellungVormieterBuilder) {
        this.carrierbestellungVormieterBuilder = carrierbestellungVormieterBuilder;
        return this;
    }

    public CarrierbestellungBuilder withKuendigungAnCarrier(Date kuendigungAnCarrier) {
        this.kuendigungAnCarrier = kuendigungAnCarrier;
        return this;
    }

    public CarrierbestellungBuilder withKuendBestaetigungCarrier(Date kuendBestaetigungCarrier) {
        this.kuendBestaetigungCarrier = kuendBestaetigungCarrier;
        return this;
    }

    public CarrierbestellungBuilder withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
        return this;
    }
}
