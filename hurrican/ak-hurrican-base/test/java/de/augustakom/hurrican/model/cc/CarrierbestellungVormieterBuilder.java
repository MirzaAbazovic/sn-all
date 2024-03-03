/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2011
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

/**
 * {@link EntityBuilder} for {@link CarrierbestellungVormieter} objects
 */
@SuppressWarnings("unused")
public class CarrierbestellungVormieterBuilder extends
        AbstractCCIDModelBuilder<CarrierbestellungVormieterBuilder, CarrierbestellungVormieter> {

    private String vorname;
    private String nachname;
    private String onkz;
    private String rufnummer;
    private String ufaNummer;

    public CarrierbestellungVormieterBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public CarrierbestellungVormieterBuilder withNachname(String nachname) {
        this.nachname = nachname;
        return this;
    }

    public CarrierbestellungVormieterBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public CarrierbestellungVormieterBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

    public CarrierbestellungVormieterBuilder withUfaNummer(String ufaNummer) {
        this.ufaNummer = ufaNummer;
        return this;
    }
}
