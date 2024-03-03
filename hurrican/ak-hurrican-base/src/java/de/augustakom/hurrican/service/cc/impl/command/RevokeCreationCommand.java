/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2010 11:51:56
 */
package de.augustakom.hurrican.service.cc.impl.command;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse, um eine Inbetriebnahme fuer einen Auftrag rueckgaengig zu machen.
 *
 *
 */
@CcTxRequired
public class RevokeCreationCommand extends AbstractRevokeCommand {

    private RevokeCreationModel revokeCreation = null;

    @Override
    public Object execute() throws Exception {
        try {
            revokeCreation = (RevokeCreationModel) getPreparedValue(KEY_REVOKE_MODEL);
            checkValues(revokeCreation);
            doRevoke();
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }

        return getWarnings();
    }


    @Override
    protected void doRevoke() throws HurricanServiceCommandException {
        revokeProvisioningOrder(revokeCreation);

        changeOrderState(revokeCreation);

        // Auftragsart umschreiben
        changeOrderType(revokeCreation);

        sendCpsTransaction(revokeCreation);
    }


}


