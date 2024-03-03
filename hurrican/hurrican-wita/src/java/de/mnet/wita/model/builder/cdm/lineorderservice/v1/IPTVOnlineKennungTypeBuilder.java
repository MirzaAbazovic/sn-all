/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.IPTVOnlineKennungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class IPTVOnlineKennungTypeBuilder implements LineOrderTypeBuilder<IPTVOnlineKennungType> {

    private String providerKennung;
    private String basisUser;

    @Override
    public IPTVOnlineKennungType build() {
        IPTVOnlineKennungType iptvOnlineKennungType = new IPTVOnlineKennungType();
        iptvOnlineKennungType.setBasisUser(basisUser);
        iptvOnlineKennungType.setProviderKennung(providerKennung);
        return iptvOnlineKennungType;
    }

    public IPTVOnlineKennungTypeBuilder withProviderKennung(String providerKennung) {
        this.providerKennung = providerKennung;
        return this;
    }

    public IPTVOnlineKennungTypeBuilder withBasisUser(String basisUser) {
        this.basisUser = basisUser;
        return this;
    }

}
