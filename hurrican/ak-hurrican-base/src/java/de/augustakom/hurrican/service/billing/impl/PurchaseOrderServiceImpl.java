/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH

 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2013 11:35:45
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;

/**
 * Service-Implementierung, um Objekte vom Typ <code>de.augustakom.hurrican.model.billing.PurchaseOrder</code> zu
 * verwalten.
 *
 *
 */
@BillingTx
public class PurchaseOrderServiceImpl extends DefaultBillingService implements PurchaseOrderService {

    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderServiceImpl.class);

    @Override
    public List<PurchaseOrder> findPurchaseOrders4Auftrag(Long auftragNoOrig) throws FindException {
        if (auftragNoOrig == null) {
            return null;
        }

        try {
            PurchaseOrder example = new PurchaseOrder();
            example.setServiceNoOrig(auftragNoOrig);

            return ((ByExampleDAO) getDAO()).queryByExample(example, PurchaseOrder.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public PurchaseOrder findPurchaseOrder(Long purchaseOrderNo) throws FindException {
        if (purchaseOrderNo == null) {
            return null;
        }

        try {
            return ((FindDAO) getDAO()).findById(purchaseOrderNo, PurchaseOrder.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

}
