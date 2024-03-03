/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 09:51:55
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.util.*;
import com.google.common.collect.Sets;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;

/**
 *
 */
public class A10NspPortBuilder extends AbstractCCIDModelBuilder<A10NspPortBuilder, A10NspPort> {
    protected A10NspBuilder a10NspBuilder;
    protected VerbindungsBezeichnungBuilder vbzBuilder;
    protected Set<HWOlt> olt = Sets.newHashSet();
    //    protected Boolean isDefault4Ekp = Boolean.TRUE;

    public A10NspPortBuilder withA10NspBuilder(A10NspBuilder a10NspBuilder) {
        this.a10NspBuilder = a10NspBuilder;
        return this;
    }

    public A10NspPortBuilder withVbzBuilder(VerbindungsBezeichnungBuilder vbzBuilder) {
        this.vbzBuilder = vbzBuilder;
        return this;
    }

    public A10NspPortBuilder withOlts(Set<HWOlt> olt) {
        this.olt = olt;
        return this;
    }

}
