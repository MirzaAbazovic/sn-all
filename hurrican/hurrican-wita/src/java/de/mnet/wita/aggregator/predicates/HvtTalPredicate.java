/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:32:41
 */
package de.mnet.wita.aggregator.predicates;

import com.google.common.base.Predicate;

import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.SchaltungKupfer;

/**
 * Predicate-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine 'HVT TAL' enthalten ist.
 */
public class HvtTalPredicate implements Predicate<Auftrag> {

    @Override
    public boolean apply(Auftrag input) {
        Object schaltungKupfer = PropertyTools.getNestedPropertyIgnoreNestedNulls(input, SchaltungKupfer.SCHALTUNG_KUPFER_PROPERTY_PATH);
        if (schaltungKupfer != null) {
            return true;
        }

        return false;
    }

}
