/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2009 14:49:40
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 *
 */
@SuppressWarnings("unused")
public class HVTGruppeBuilder extends AbstractCCIDModelBuilder<HVTGruppeBuilder, HVTGruppe> {

    private String onkz = "089";
    private String ortsteil = randomString(20);
    private String strasse = "HVT-Gruppe-Strasse";
    private String hausNr = "123";
    private String plz = "01234";
    private String ort = "HVT-Gruppe-Ort";
    private String ortZusatz = null; //currently not in use with the WITA interface
    private HWSwitch hwSwitch = null;
    private Long niederlassungId = Niederlassung.ID_MUENCHEN;
    private String kostenstelle = null;
    private String innenauftrag = null;
    private String telefon = null;
    private Boolean montag = null;
    private Boolean dienstag = null;
    private Boolean mittwoch = null;
    private Boolean donnerstag = null;
    private Boolean freitag = null;
    private Date dslAnalogTermin = null;
    private Date dslIsdnTermin = null;
    private Date dsl2pAnalogTermin = null;
    private Date dsl2pIsdnTermin = null;
    private Date sdslTermin = null;
    private Date connectTermin = null;
    private Date dsl2pOnlyTermin = null;
    private Date voipTermin = null;
    private Boolean export4Portal = false;

    public String getOnkz() {
        return onkz;
    }

    public HVTGruppeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public HVTGruppeBuilder withOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
        return this;
    }

    public HVTGruppeBuilder withNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
        return this;
    }

    public HVTGruppeBuilder withHausNr(String hausNr) {
        this.hausNr = hausNr;
        return this;
    }

    public HVTGruppeBuilder withStrasse(String strasse) {
        this.strasse = strasse;
        return this;
    }

    public HVTGruppeBuilder withPlz(String plz) {
        this.plz = plz;
        return this;
    }

    public HVTGruppeBuilder withOrt(String ort) {
        this.ort = ort;
        return this;
    }

    public HVTGruppeBuilder withSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }
}
