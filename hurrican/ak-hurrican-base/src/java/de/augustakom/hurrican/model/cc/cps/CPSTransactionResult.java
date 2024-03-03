/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2009 13:19:24
 */
package de.augustakom.hurrican.model.cc.cps;

import java.util.*;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * DataTransfer-Object, um generierte CPS-Transactions sowie die bei der Generierung aufgetretenen Warnungen zurueck zu
 * geben.
 *
 *
 */
public class CPSTransactionResult extends AbstractHurricanModel {

    private List<CPSTransaction> cpsTransactions = null;
    private AKWarnings warnings = null;

    /**
     * Default-Const.
     */
    public CPSTransactionResult() {
        super();
    }

    /**
     * Konstruktor mit Angabe der CPS-Transactions und Warnings.
     *
     * @param cpsTransactions
     * @param warnings
     */
    public CPSTransactionResult(List<CPSTransaction> cpsTransactions, AKWarnings warnings) {
        super();
        this.cpsTransactions = cpsTransactions;
        this.warnings = warnings;
    }

    /**
     * @return the cpsTransactions
     */
    public List<CPSTransaction> getCpsTransactions() {
        return cpsTransactions;
    }

    /**
     * @param cpsTransactions the cpsTransactions to set
     */
    public void setCpsTransactions(List<CPSTransaction> cpsTransactions) {
        this.cpsTransactions = cpsTransactions;
    }

    /**
     * @return the warnings
     */
    public AKWarnings getWarnings() {
        return warnings;
    }

    /**
     * @param warnings the warnings to set
     */
    public void setWarnings(AKWarnings warnings) {
        this.warnings = warnings;
    }

}


