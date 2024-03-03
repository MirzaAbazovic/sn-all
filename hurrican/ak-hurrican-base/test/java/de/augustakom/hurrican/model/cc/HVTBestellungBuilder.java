/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2012 14:32:43
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 * EntityBuilder for HVTBestellung objects
 */
@SuppressWarnings("unused")
public class HVTBestellungBuilder extends AbstractCCIDModelBuilder<HVTBestellungBuilder, HVTBestellung> {

    private UEVTBuilder uevtBuilder;
    private Long uevtId;
    private String physiktyp = "H";
    private Date angebotDatum = new Date();
    private Integer anzahlCuDA = Integer.valueOf(100);
    private Date bestelldatum = new Date();
    private Date realDTAGBis;
    private String bestellNrAKom;
    private String bestellNrDTAG;
    private Date bereitgestellt;
    private EqVerwendung eqVerwendung = EqVerwendung.STANDARD;

    public HVTBestellungBuilder withUevtBuilder(UEVTBuilder uevtBuilder) {
        this.uevtBuilder = uevtBuilder;
        return this;
    }

}


