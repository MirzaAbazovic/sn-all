/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 09:11:14
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>CPSTransaction</code>
 *
 *
 */
public interface CPSTransactionDAO extends FindDAO, StoreDAO, ByExampleDAO, DeleteDAO {

    /**
     * Ermittelt den naechsten Sequence-Wert fuer den ServiceOrder-Stack ueber die DB-Sequence
     * 'S_T_CPS_TX_STACK_SEQUENCE_0'
     *
     * @return naechster Sequence-Wert
     *
     */
    Long getNextCPSStackSequence();

    /**
     * Aktualisiert eine CPSTransaction
     *
     * @param cpsTransaction
     */
    void updateCPSTransaction(CPSTransaction cpsTransaction);

    /**
     * Ermittelt alle offenen Transaktionen
     *
     * @param limit maximale Anzahl Datensätze
     * @return Liste mit allen offenen Transaktionen
     */
    List<CPSTransactionExt> findOpenTransactions(Integer limit);

    /**
     * Ermittelt alle abgelaufenen Transaktionen
     *
     * @param expiredDate alle fehlgeschlagenen und abgelaufenen Transaktionen vor diesem Datum (Stunden, Minuten und
     *                    Sekunden genau)
     * @return Liste mit allen zutreffenden Transaktionen
     */
    List<CPSTransactionExt> findExpiredTransactions(Date expiredDate);

    /**
     * Ermittelt alle CPS-Transactions zu einem bestimmten techn. Auftrag.
     *
     * @param auftragId
     * @return
     */
    List<CPSTransaction> findCPSTransactions4TechOrder(Long auftragId);

    /**
     * Ermittelt alle CPS-Transactions zu einem bestimmten techn. Auftrag., States und ServiceOrders
     *
     * @param auftragId
     * @param txStates optional
     * @param serviceOrderTypes optional
     * @return
     */
    List<CPSTransaction> findCPSTransactions4TechOrder(Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes);

    /**
     * Ermittelt alle CPS-Transactions zu einem bestimmten techn. Auftrag., States und ServiceOrders
     * with asc / desc soring and max resuts amount limitation
     *
     * @param auftragId
     * @param txStates optional
     * @param serviceOrderTypes optional
     * @param ascIdOrder asc sorting
     * @param descIdOrder desc sorting, is evaluated if ascIdOrder == false
     * @param maxRecordsLimit setMaxResults(#) is used if positive
     * @return
     */
    List<CPSTransaction> findCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes,
            boolean ascIdOrder, boolean descIdOrder, Integer maxRecordsLimit);

    /**
     * Ermittelt alle CPS-Transactions zu einer Billing-Auftragsnummer.
     *
     * @param billingOrderNoOrig
     * @return
     */
    List<CPSTransaction> findCPSTransactions4BillingOrder(Long billingOrderNoOrig);

    /**
     * Ermittelt AKTIVE CPS-Transactions zu einer Billing-Auftragsnummer.
     *
     * @param billingOrderNoOrig
     * @return
     */
    List<CPSTransactionExt> findActiveCPSTransactions4BillingOrder(final Long billingOrderNoOrig);

}


