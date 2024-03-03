/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2012 17:39:17
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import de.augustakom.hurrican.model.cc.TechLeistung;

public class WholesaleTechLeistungen {
    public final Long downstream;
    public final Long upstream;
    public final Set<Long> extLeistungNos;

    public WholesaleTechLeistungen(List<TechLeistung> techLeistungen) {
        Long locDownstream = null;
        Long locUpstream = null;
        Builder<Long> extLeistungNosBuilder = ImmutableSet.<Long>builder();
        for (TechLeistung techLeistung : techLeistungen) {
            if (TechLeistung.TYP_DOWNSTREAM.equals(techLeistung.getTyp())) {
                locDownstream = techLeistung.getLongValue();
            }
            else if (TechLeistung.TYP_UPSTREAM.equals(techLeistung.getTyp())) {
                locUpstream = techLeistung.getLongValue();
            }
            else {
                extLeistungNosBuilder.add(techLeistung.getExternLeistungNo());
            }
        }
        downstream = locDownstream;
        upstream = locUpstream;
        extLeistungNos = extLeistungNosBuilder.build();
        assertThat(extLeistungNos, hasSize(techLeistungen.size() - 2));
    }
}

