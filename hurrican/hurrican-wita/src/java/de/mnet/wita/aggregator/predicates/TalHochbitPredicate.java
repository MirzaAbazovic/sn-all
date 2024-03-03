/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:32:41
 */
package de.mnet.wita.aggregator.predicates;

import de.mnet.wita.message.Auftrag;

/**
 * Predicate-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine hochbit-ratige TAL enthalten ist.
 */
public class TalHochbitPredicate extends AbstractTalHochOrNiederbitPredicate {

    public TalHochbitPredicate() {
        super(false);
    }

}
