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
import de.mnet.wita.message.auftrag.SchaltungKvzTal;

/**
 * Predicate-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine 'HVT TAL' enthalten ist.
 */
public class KvzTalPredicate implements Predicate<Auftrag> {

    @Override
    public boolean apply(Auftrag input) {
        Object schaltungKvz = PropertyTools.getNestedPropertyIgnoreNestedNulls(input, SchaltungKvzTal.SCHALTUNG_KVZ_PROPERTY_PATH);
        if (schaltungKvz != null) {
            return true;
        }

        return false;
    }

}
