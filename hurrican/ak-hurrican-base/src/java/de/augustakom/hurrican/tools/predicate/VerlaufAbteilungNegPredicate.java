/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2005 14:44:29
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;


/**
 * Predicate, um innerhalb einer Collection von Objekten des Typs <code>VerlaufAbteilung</code> nach den Objekten zu
 * filtern, die NICHT einer best. Abteilung zugeordnet sind.
 *
 *
 */
public class VerlaufAbteilungNegPredicate implements Predicate {

    private Long abtId2Filter = null;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID, nach der gefiltert werden soll.
     *
     * @param abtId2Filter
     */
    public VerlaufAbteilungNegPredicate(Long abtId2Filter) {
        super();
        this.abtId2Filter = abtId2Filter;
    }

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object value) {
        if ((value instanceof VerlaufAbteilung) &&
                !NumberTools.equal(((VerlaufAbteilung) value).getAbteilungId(), abtId2Filter)) {
            return true;
        }
        return false;
    }

}


