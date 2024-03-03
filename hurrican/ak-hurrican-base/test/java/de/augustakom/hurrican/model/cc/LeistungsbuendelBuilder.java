/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 10:58:01
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;


/**
 *
 */
@SuppressWarnings("unused")
public class LeistungsbuendelBuilder extends AbstractCCIDModelBuilder<LeistungsbuendelBuilder, Leistungsbuendel> {

    private String name = randomString(20);
    private String beschreibung = randomString(100);

}


