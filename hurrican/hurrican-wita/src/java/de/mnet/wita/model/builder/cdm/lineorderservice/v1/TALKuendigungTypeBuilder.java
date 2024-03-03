/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALKuendigungTypeBuilder implements LineOrderTypeBuilder<TALKuendigungType> {

    private String vorabstimmungsID;
    private LeitungsbezeichnungType leitungsbezeichnungType;

    @Override
    public TALKuendigungType build() {
        TALKuendigungType talKuendigungType = new TALKuendigungType();
        talKuendigungType.setVorabstimmungsID(vorabstimmungsID);
        talKuendigungType.setBestandsvalidierung2(leitungsbezeichnungType);
        return talKuendigungType;
    }

    public TALKuendigungTypeBuilder withVorabstimmungsID(String vorabstimmungsID) {
        this.vorabstimmungsID = vorabstimmungsID;
        return this;
    }

    public TALKuendigungTypeBuilder withLeitungsbezeichnungType(LeitungsbezeichnungType leitungsbezeichnungType) {
        this.leitungsbezeichnungType = leitungsbezeichnungType;
        return this;
    }

}
