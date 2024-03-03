/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2010 15:48:56
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;


/**
 * EntityBuilder fuer {@link EG} Objekte.
 */
@SuppressWarnings("unused")
public class EGBuilder extends AbstractCCIDModelBuilder<EGBuilder, EG> {

    private Long egInterneId;
    private String egName = randomString(10);
    private String egBeschreibung;
    private String lsText;
    private Long egTyp;
    private Long extLeistungNo;
    private Date egVerfuegbarVon;
    private Date egVerfuegbarBis;
    private String garantiezeit;
    private String produktcode;
    private Boolean isConfigurable;
    private Boolean confPortforwarding;
    private Boolean confS0backup;
    private Boolean cpsProvisioning;
    private List<EGType> egTypes;

    public EGBuilder withEgName(String egName) {
        this.egName = egName;
        return this;
    }

}


