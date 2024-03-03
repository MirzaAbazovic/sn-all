/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.14
 */
package de.mnet.hurrican.ffm.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Created by glinkjo on 15.09.14.
 */
public class DeleteOrderServiceAction extends AbstractFFMAction {

    private final Verlauf bauauftrag;

    public DeleteOrderServiceAction(FFMService ffmService, Verlauf bauauftrag) {
        super("deleteOrder", ffmService);
        this.bauauftrag = bauauftrag;
    }

    @Override
    public void doExecute(TestContext context) {
        if (bauauftrag == null) {
            throw new CitrusRuntimeException("No proper Bauauftrag defined for delete order action");
        }

        try {
            String orderId = bauauftrag.getWorkforceOrderId();
            context.setVariable(VariableNames.WORKFROCE_ORDER_ID, orderId);
            ffmService.deleteOrder(bauauftrag);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to call ffm service operation", e);
        }
    }

}
