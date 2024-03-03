/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 09:27:35
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;

/**
 * EntityBuilder fuer {@link LeistungParameter} Objekte.
 */
@SuppressWarnings("unused")
public class LeistungParameterBuilder extends EntityBuilder<LeistungParameterBuilder, LeistungParameter> {

    private String leistungParameterBeschreibung = null;
    private Integer leistungParameterMehrfach = null;
    private Integer leistungParameterTyp = randomInt(100, 99999);

}


