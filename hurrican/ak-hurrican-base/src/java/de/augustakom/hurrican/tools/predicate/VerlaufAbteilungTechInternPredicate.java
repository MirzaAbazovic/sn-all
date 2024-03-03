/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 15:37:41
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;


/**
 * Predicate, um eine Collection mit Objekten des Typs <code>VerlaufAbteilung</code> nur die 'internen' technischen
 * Abteilungen (also z.B. ST-Voice, ST-Online) heraus zu filtern. <br> Abteilungen wie AM, FieldService sind
 * ausgeschlossen. <br> (Abteilungen wie PM oder IT muessen nicht ausgeschlossen werden, da diese nicht fuer Verlaeufe
 * relevant sind.)
 *
 *
 */
public class VerlaufAbteilungTechInternPredicate implements Predicate {

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object value) {
        if ((value instanceof VerlaufAbteilung) &&
                !NumberTools.isIn(((VerlaufAbteilung) value).getAbteilungId(),
                        new Number[] {
                                Abteilung.AM,
                                Abteilung.DISPO,
                                Abteilung.NP,
                                Abteilung.FIELD_SERVICE })) {
            return true;
        }
        return false;
    }

}


