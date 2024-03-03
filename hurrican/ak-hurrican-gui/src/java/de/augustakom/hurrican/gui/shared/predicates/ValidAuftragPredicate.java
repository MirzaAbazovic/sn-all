/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.04.2006 11:02:51
 */
package de.augustakom.hurrican.gui.shared.predicates;

import org.apache.commons.collections.Predicate;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;


/**
 * Predicate, um nach gueltigen Auftraegen zu filtern.
 *
 *
 */
public class ValidAuftragPredicate implements Predicate {

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(Object obj) {
        if (obj instanceof AuftragDNView) {
            AuftragDNView dnView = (AuftragDNView) obj;
            Long status = dnView.getAuftragStatusId();
            if ((status != null) &&
                    !NumberTools.isIn(status, new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE }) &&
                    (status.longValue() < AuftragStatus.KUENDIGUNG.longValue())) {
                return true;
            }
        }
        return false;
    }
}


