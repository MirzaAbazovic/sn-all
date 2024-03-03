/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2011 13:25:05
 */
package de.mnet.wita.aggregator.predicates;

import com.google.common.base.Predicate;

import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

/**
 * Predicate-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine Einzelrufnummer enthalten ist.
 */
public class RexMkEinzelrufnummerPredicate implements Predicate<Auftrag> {

    @Override
    public boolean apply(Auftrag input) {
        RufnummernPortierung portierung = (RufnummernPortierung) PropertyTools.getNestedPropertyIgnoreNestedNulls(input, RufnummernPortierung.RUFNUMMERN_PORTIERUNG_PROPERTY_PATH);
        if ((portierung instanceof RufnummernPortierungEinzelanschluss) && ((RufnummernPortierungEinzelanschluss) portierung).isSingleDn()) {
            return true;
        }

        return false;
    }
}
