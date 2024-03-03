/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:32:41
 */
package de.mnet.wita.aggregator.predicates;

import java.util.*;
import com.google.common.base.Predicate;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.SchaltungKupfer;

/**
 * Predicate-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine hochbit-ratige TAL enthalten ist.
 */
public abstract class AbstractTalHochOrNiederbitPredicate implements Predicate<Auftrag> {

    private boolean expectNiederbit;

    public AbstractTalHochOrNiederbitPredicate(boolean expectNiederbit) {
        this.expectNiederbit = expectNiederbit;
    }

    @Override
    public boolean apply(Auftrag input) {
        @SuppressWarnings("unchecked")
        List<SchaltungKupfer> schaltungKupfer = (List<SchaltungKupfer>) PropertyTools.getNestedPropertyIgnoreNestedNulls(input, SchaltungKupfer.SCHALTUNG_KUPFER_PROPERTY_PATH);
        if (CollectionTools.isNotEmpty(schaltungKupfer)) {
            for (SchaltungKupfer sk : schaltungKupfer) {
                if (expectNiederbit) {
                    if (sk.getUebertragungsverfahren() != null) {
                        return false;
                    }
                }
                else {
                    if (sk.getUebertragungsverfahren() == null) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

}
