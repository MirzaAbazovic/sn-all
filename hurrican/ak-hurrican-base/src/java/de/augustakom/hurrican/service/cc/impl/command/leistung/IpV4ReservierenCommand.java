/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2013 15:27:40
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.IpV4ReservierenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IpV4ReservierenCommand extends AbstractIpCommand {

    @Override
    @Nonnull
    protected List<IPAddress> getIpAddresses(@Nonnull final Long auftragNoOrig) throws FindException {
        return ipAddressService.findV4AddressByBillingOrderNumber(auftragNoOrig);
    }

    @Override
    @Nonnull
    protected IPAddress assignIP(@Nonnull final Long auftragId, @Nonnull final Reference purpose,
            @Nonnull final Integer netmaskSize, @Nonnull final Reference site, @Nonnull final Long sessionId)
            throws StoreException {
        return ipAddressService.assignIPV4(getAuftragId(), purpose, netmaskSize, site, getSessionId());
    }

    @Override
    protected boolean isV4() {
        return true;
    }

    @Override
    @CheckForNull
    protected Reference getPurpose(@Nonnull final Produkt produkt) throws FindException {
        return produkt.getIpPurposeV4();
    }
}
