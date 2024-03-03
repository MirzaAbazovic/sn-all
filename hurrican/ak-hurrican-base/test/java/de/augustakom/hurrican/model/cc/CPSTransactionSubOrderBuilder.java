/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 13:55:12
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;


/**
 * EntityBuilder fuer {@link CPSTransactionSubOrder}
 */
@SuppressWarnings("unused")
public class CPSTransactionSubOrderBuilder extends EntityBuilder<CPSTransactionSubOrderBuilder, CPSTransactionSubOrder> {

    private Long cpsTxId = null;
    private Long auftragId = null;
    private Long verlaufId = null;

    public CPSTransactionSubOrderBuilder withVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
        return this;
    }

}


