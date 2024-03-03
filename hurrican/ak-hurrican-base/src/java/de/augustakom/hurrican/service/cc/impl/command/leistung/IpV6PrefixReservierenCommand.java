/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 13:37:09
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

@CcTxRequired
@ObjectsAreNonnullByDefault
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.IpV6PrefixReservierenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IpV6PrefixReservierenCommand extends AbstractIpCommand {

    @Override
    protected List<IPAddress> getIpAddresses(final Long auftragNoOrig) throws FindException {
        return ipAddressService.findV6PrefixesByBillingOrderNumber(auftragNoOrig);
    }

    @Override
    protected IPAddress assignIP(final Long auftragId, final Reference purpose,
            final Integer netmaskSize, final Reference site, final Long sessionId)
            throws StoreException {
        return ipAddressService.assignIPV6(getAuftragId(), purpose, netmaskSize, site, getSessionId());
    }

    @Override
    protected Reference getPurpose(final Produkt produkt) throws FindException {
        List<Reference> references = referenceService.findReferencesByType(Reference.REF_TYPE_IP_PURPOSE_TYPE_V6,
                Boolean.FALSE);
        Reference dhcpV6PdPurpose = Iterables.getOnlyElement(references);
        return dhcpV6PdPurpose;
    }

    @Override
    protected boolean isV4() {
        return false;
    }
}
