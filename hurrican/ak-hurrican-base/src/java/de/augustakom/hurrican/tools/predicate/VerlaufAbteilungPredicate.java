/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 15:38:26
 */
package de.augustakom.hurrican.tools.predicate;

import java.util.*;
import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;

/**
 * Predicate, um innerhalb einer {@link Collection} von Objekten des Typs {@link Abteilung}, {@link VerlaufAbteilung}
 * oder {@link Integer} nach den Objekten zu filtern, die einer oder mehreren bestimmten Abteilungen zugeordnet sind.
 */
public class VerlaufAbteilungPredicate implements Predicate {

    private List<Long> abtIds2Filter = new ArrayList<Long>();

    /**
     * Konstruktor mit Angabe einer oder mehrere Abteilungs-IDs, nach der gefiltert werden soll.
     *
     * @param abtIds2Filter die AbteilungsId(s), nach der/denen gefiltert werden soll
     */
    public VerlaufAbteilungPredicate(Long... abtIds2Filter) {
        super();
        addAbtIds(abtIds2Filter);
    }

    /**
     * Fuegt dem Predicate eine oder mehrere Abteilungs-ID hinzu, nach der/denen gefilter werden soll.
     *
     * @param abtIds die Abteilung(en), die hinzugefügt werden soll(en)
     */
    public void addAbtIds(Long... abtIds) {
        for (Long abtId : abtIds) {
            abtIds2Filter.add(abtId);
        }
    }

    @Override
    public boolean evaluate(Object value) {
        Long abtIdToCheck = null;
        if (value instanceof VerlaufAbteilung) {
            abtIdToCheck = ((VerlaufAbteilung) value).getAbteilungId();
        }
        else if (value instanceof Abteilung) {
            abtIdToCheck = ((Abteilung) value).getId();
        }
        else if (value instanceof Long) {
            abtIdToCheck = (Long) value;
        }

        if (abtIdToCheck != null) {
            for (Long abtId : abtIds2Filter) {
                if (NumberTools.equal(abtIdToCheck, abtId)) { return true; }
            }
        }
        return false;
    }
}
