/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

public class KuendigungFristBuilder extends EntityBuilder<KuendigungFristBuilder, KuendigungFrist> {

    private Boolean mitMvlz = Boolean.FALSE;
    private Long fristInWochen;
    private KuendigungFrist.FristAuf fristAuf;
    private Long autoVerlaengerung = Long.valueOf(12);
    private Long vertragsabschlussJahr;
    private Long vertragsabschlussMonat;
    private String description = "description";

    public KuendigungFristBuilder withMitMvlz() {
        this.mitMvlz = Boolean.TRUE;
        return this;
    }

    public KuendigungFristBuilder withFristInWochen(Long fristInWochen) {
        this.fristInWochen = fristInWochen;
        return this;
    }

    public KuendigungFristBuilder withFristAuf(KuendigungFrist.FristAuf fristAuf) {
        this.fristAuf = fristAuf;
        return this;
    }

    public KuendigungFristBuilder withVertragsabschlussJahr(Long vertragsabschlussJahr) {
        this.vertragsabschlussJahr = vertragsabschlussJahr;
        return this;
    }

    public KuendigungFristBuilder withVertragsabschlussMonat(Long vertragsabschlussMonat) {
        this.vertragsabschlussMonat = vertragsabschlussMonat;
        return this;
    }

    public KuendigungFristBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public KuendigungFristBuilder withAutoVerlaengerung(Long autoVerlaengerung) {
        this.autoVerlaengerung = autoVerlaengerung;
        return this;
    }

}
