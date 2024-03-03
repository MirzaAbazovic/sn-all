/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 09:03:32
 */
package de.augustakom.hurrican.model.cc;


/**
 * Builder fuer Objekte des Typs {@link CarrierKennung}
 */
@SuppressWarnings("unused")
public class CarrierKennungBuilder extends AbstractCCIDModelBuilder<CarrierKennungBuilder, CarrierKennung> {

    private Long carrierId = randomLong(1, 100);
    private String bezeichnung = randomString(10);
    private String portierungsKennung = randomString(4);
    private String kundenNr = "0123456789";
    private String witaLeistungsNr = "1234";
    private String name = null;
    private String strasse = null;
    private String plz = null;
    private String ort = null;
    private String elTalAbsenderId = null;
    private String userW = null;
    private String bktoNummer = "5883000320";

    public CarrierKennungBuilder withCarrierId(Long carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public CarrierKennungBuilder withKundenNr(String kundenNr) {
        this.kundenNr = kundenNr;
        return this;
    }

    public CarrierKennungBuilder withElTalAbsenderId(String elTalAbsenderId) {
        this.elTalAbsenderId = elTalAbsenderId;
        return this;
    }

    public CarrierKennungBuilder withPortierungsKennung(String portierungsKennung) {
        this.portierungsKennung = portierungsKennung;
        return this;
    }
}
