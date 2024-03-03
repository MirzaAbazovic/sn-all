/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2005 15:41:54
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.Verlauf;


/**
 * Predicate, um stornierte Verlaeufe zu filtern.
 *
 *
 */
public class VerlaufStornoPredicate implements Predicate {

    private boolean onlyBA = false;

    /**
     * Konstruktor.
     *
     * @param onlyBA Angabe, ob nur Bauauftraege (true) oder Bauauftraege UND Projektierungen (false) beruecksichtigt
     *               werden sollen.
     */
    public VerlaufStornoPredicate(boolean onlyBA) {
        super();
        this.onlyBA = onlyBA;
    }

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object obj) {
        if (obj instanceof Verlauf) {
            Verlauf v = (Verlauf) obj;
            if (this.onlyBA && BooleanTools.nullToFalse(v.getProjektierung())) {
                return false;
            }

            if (v.isStorno()) {
                return true;
            }
        }
        return false;
    }

}


