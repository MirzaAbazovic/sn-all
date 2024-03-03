/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 16:33:25
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Endstelle;


/**
 * Command-Klasse, um zu pruefen, ob der Endstelle 'A' des Ziel-Auftrags bereits eine Physik zugeordnet ist. <br> Ist
 * dies nicht der Fall, wird eine Exception erzeugt.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AssertNoPhysikOnEsADest")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssertNoPhysikOnEsADest extends AssertPhysikOnEsDest {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        if (hasPhysik(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_A)) {
            throw generateException(Endstelle.ENDSTELLEN_TYP_A, false);
        }
        return null;
    }

}


