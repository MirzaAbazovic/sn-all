/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2005 13:55:49
 */
package de.augustakom.hurrican.tools.predicate;

import org.apache.commons.collections.Predicate;

import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel;


/**
 * Predicate, um nach aktiven Auftraegen zu filtern. <br> Als Evaluierungs-Objekt wird eine Instanz von
 * <code>CCAuftragStatusModel</code> erwartet.
 *
 *
 */
public class ActiveAuftragPredicate implements Predicate {

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    public boolean evaluate(Object obj) {
        if (obj instanceof CCAuftragStatusModel) {
            Long status = ((CCAuftragStatusModel) obj).getAuftragStatusId();
            if ((status != null) && (status.longValue() >= AuftragStatus.IN_BETRIEB.longValue())
                    && (status.longValue() < AuftragStatus.KUENDIGUNG.longValue())) {
                return true;
            }
        }
        return false;
    }

}


