/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.AddressBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.mnet.esb.cdm.resource.workforceservice.v1.Address;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>Standort-Information (=Endstelle B aus Hurrican)</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderLocationCommand")
@Scope("prototype")
public class AggregateFfmHeaderLocationCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderLocationCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService ccKundenService;

    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService billingKundenService;

    @Override
    public Object execute() throws Exception {
        try {
            getWorkforceOrder().setLocation(getAddress());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Location Data: " + e.getMessage(), this.getClass());
        }
    }

    /**
     * Ermittelt die Kundenadresse aus Hurrican. Falls diese nicht gefunden werden kann, wird die Adresse aus Taifun
     * ermittelt. Falls auch in Taifun keine Adresse vorhanden ist, wird <code>null</code> zurückgegeben.
     * @throws FindException
     */
    Address getAddress() throws FindException {
        Endstelle endstelleB = getEndstelleB(false);
        AddressModel addressModel = null;
        if (endstelleB != null && endstelleB.getAddressId() != null) {
            addressModel = ccKundenService.findCCAddress(endstelleB.getAddressId());
        }
        else {
            // Fallback: Kundenadresse aus Taifun ermitteln!
            Auftrag auftrag = ccAuftragService.findAuftragById(getAuftragId());
            if (auftrag != null && auftrag.getKundeNo() != null) {
                addressModel = billingKundenService.getAdresse4Kunde(auftrag.getKundeNo());
            }
        }
        return addressModel != null ? new AddressBuilder().withAddressModel(addressModel).build() : null;
    }

}
