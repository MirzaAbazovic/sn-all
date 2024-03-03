/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH

 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2004 11:30:14
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface definiert spezielle Methoden fuer Service-Implementierungen, die Objekte vom Typ <code>PurchaseOrder</code>
 * verwalten.
 *
 *
 */
public interface PurchaseOrderService extends IBillingService {

    /**
     * Sucht nach allen PurchaseOrders ueber die (original) Auftragsnummer.
     *
     * @param auftragNoOrig (original) Auftragsnummer
     * @return List<PurchaseOrder> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<PurchaseOrder> findPurchaseOrders4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Ermittelt eine bestimmte {@link PurchaseOrder} ueber den PrimaryKey.
     * @param purchaseOrderNo
     * @return
     * @throws FindException
     */
    public PurchaseOrder findPurchaseOrder(Long purchaseOrderNo) throws FindException;

}
