/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 15:38:36
 */
package de.mnet.wita.model;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.Carrier;

@Entity
@DiscriminatorValue("KueDt")
public class KueDtUserTask extends AbgebendeLeitungenUserTask {

    private static final long serialVersionUID = -2780695133763580231L;

    @Override
    @Transient
    public String getAufnehmenderProvider() {
        return Carrier.CARRIER_DTAG;
    }

    @Override
    @Transient
    public boolean kuendigeCarrierbestellung() {
        return true;
    }

    @Override
    @Transient
    public boolean getPrio() {
        return true;
    }

}
