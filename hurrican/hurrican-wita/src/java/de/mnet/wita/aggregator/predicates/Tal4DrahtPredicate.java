/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:35:43
 */
package de.mnet.wita.aggregator.predicates;

import de.mnet.wita.message.Auftrag;

/**
 * Matcher-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine 'TAL 4-Draht' enthalten ist.
 */
public class Tal4DrahtPredicate extends AbstractTalNDrahtPredicate {

    public Tal4DrahtPredicate() {
        super(2, true);
    }

}


