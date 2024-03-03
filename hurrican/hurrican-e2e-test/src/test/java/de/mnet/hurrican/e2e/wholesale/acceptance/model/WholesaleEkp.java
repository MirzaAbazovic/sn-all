/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 16:19:14
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import de.mnet.hurrican.wholesale.workflow.Ekp;

/**
 *
 */
public class WholesaleEkp {

    public String id;
    public String frameContractId;

    public WholesaleEkp id(String id) {
        this.id = id;
        return this;
    }

    public WholesaleEkp frameContractId(String frameContractId) {
        this.frameContractId = frameContractId;
        return this;
    }

    public Ekp toXmlBean() {
        Ekp ekp = new Ekp();
        ekp.setId(id);
        ekp.setFrameContractId(frameContractId);
        return ekp;
    }
}


