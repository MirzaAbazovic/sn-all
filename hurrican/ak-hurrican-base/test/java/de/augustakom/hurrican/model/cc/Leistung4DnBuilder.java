/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 10:17:44
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;


/**
 * Entity Builder for Leistung4Dn objects.
 *
 *
 */
@SuppressWarnings("unused")
public class Leistung4DnBuilder extends AbstractCCIDModelBuilder<Leistung4DnBuilder, Leistung4Dn> {

    private String leistung;
    private String beschreibung;
    private Long externLeistungNo;
    private Long externSonstigesNo;
    private String provisioningName;

    public Leistung4DnBuilder withLeistung(String leistung) {
        this.leistung = leistung;
        return this;
    }

    public Leistung4DnBuilder withProvisioningName(String provisioningName) {
        this.provisioningName = provisioningName;
        return this;
    }

}


