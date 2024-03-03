/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public class AntwortfristBuilder implements WbciBuilder<Antwortfrist> {

    protected Long fristInStunden;
    protected RequestTyp typ;
    protected WbciRequestStatus requestStatus;

    @Override
    public Antwortfrist build() {
        Antwortfrist antwortfrist = new Antwortfrist();
        antwortfrist.setFristInStunden(fristInStunden);
        antwortfrist.setTyp(typ);
        antwortfrist.setRequestStatus(requestStatus);
        return antwortfrist;
    }

    public AntwortfristBuilder withFristInStunden(Long fristInStunden) {
        this.fristInStunden = fristInStunden;
        return this;
    }

    public AntwortfristBuilder withRequestStatus(WbciRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public AntwortfristBuilder withRequestTyp(RequestTyp typ) {
        this.typ = typ;
        return this;
    }

}
