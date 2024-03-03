/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.13
 */
package de.augustakom.hurrican.model.cc;

@SuppressWarnings("unused")
public class CarrierBuilder extends AbstractCCIDModelBuilder<CarrierBuilder, Carrier> {

    private String name = randomString(30);
    private Integer orderNo = null;
    private Boolean cbNotwendig = null;
    private String elTalEmpfId = null;
    private String companyName = null;
    private String userW = null;
    private Boolean hasWitaInterface = Boolean.TRUE;
    private String portierungskennung = null;
    private String witaProviderNameAufnehmend = null;
    private Boolean cudaKuendigungNotwendig = Boolean.TRUE;
    private CarrierVaModus vorabstimmungsModus = CarrierVaModus.WBCI;
    private String iTUCarrierCode = null;

    public CarrierBuilder withPortierungskennung(String portierungskennung) {
        this.portierungskennung = portierungskennung;
        return this;
    }

    public CarrierBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CarrierBuilder withITUCarrierCode(String iTUCarrierCode) {
        this.iTUCarrierCode = iTUCarrierCode;
        return this;
    }

    public CarrierBuilder withVorabstimmungsModus(CarrierVaModus vorabstimmungsModus) {
        this.vorabstimmungsModus = vorabstimmungsModus;
        return this;
    }

}
