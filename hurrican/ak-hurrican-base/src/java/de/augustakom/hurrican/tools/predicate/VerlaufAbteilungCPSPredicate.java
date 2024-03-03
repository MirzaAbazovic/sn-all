/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2010 08:12:12
 */
package de.augustakom.hurrican.tools.predicate;

import java.util.*;
import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;

/**
 * Predicate, um aus einer {@link Collection} mit Objekten des Typs {@link Integer}, {@link VerlaufAbteilung} oder
 * {@link Abteilung} nur die Abteilungen heraus zu filtern, deren Bearbeitung der CPS uebernimmt. Dies sind aktuell:
 * <ul> <li>ST Online <li>ST Voice <li>M-Queue
 */
public class VerlaufAbteilungCPSPredicate implements Predicate {

    @Override
    public boolean evaluate(Object value) {
        Long abtId2Check = null;
        if (value instanceof VerlaufAbteilung) {
            abtId2Check = ((VerlaufAbteilung) value).getAbteilungId();
        }
        else if (value instanceof Long) {
            abtId2Check = ((Long) value);
        }
        else if (value instanceof Abteilung) {
            abtId2Check = ((Abteilung) value).getId();
        }

        if (abtId2Check != null) {
            return NumberTools.isIn(abtId2Check, new Number[] { Abteilung.ST_ONLINE, Abteilung.ST_VOICE, Abteilung.MQUEUE });
        }
        return false;
    }
}
