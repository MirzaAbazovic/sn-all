/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2011 17:35:43
 */
package de.mnet.wita.aggregator.predicates;

import java.util.*;
import com.google.common.base.Predicate;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;

/**
 * Matcher-Implementierung ueberprueft, ob in dem {@link Auftrag} Objekt eine 'TAL N-Draht' enthalten ist.
 */
public abstract class AbstractTalNDrahtPredicate implements Predicate<Auftrag> {

    private int countOfExpectedSchaltangaben;
    private boolean isHvt = true;

    protected AbstractTalNDrahtPredicate(int countOfExpectedSchaltangaben, boolean isHvt) {
        this.countOfExpectedSchaltangaben = countOfExpectedSchaltangaben;
        this.isHvt = isHvt;
    }

    @Override
    public boolean apply(Auftrag input) {
        String propertyPath = (isHvt) ? SchaltungKupfer.SCHALTUNG_KUPFER_PROPERTY_PATH : SchaltungKvzTal.SCHALTUNG_KVZ_PROPERTY_PATH;

        List<?> schaltung = (List<?>) PropertyTools.getNestedPropertyIgnoreNestedNulls(input, propertyPath);
        if (CollectionTools.hasExpectedSize(schaltung, countOfExpectedSchaltangaben)) {
            return true;
        }

        return false;
    }
}


