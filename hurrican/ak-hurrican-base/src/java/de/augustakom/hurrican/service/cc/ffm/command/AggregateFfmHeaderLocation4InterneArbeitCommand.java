/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.AddressBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.esb.cdm.resource.workforceservice.v1.Address;

/**
 *
 */
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderLocation4InterneArbeitCommand")
@Scope("prototype")
public class AggregateFfmHeaderLocation4InterneArbeitCommand extends AggregateFfmHeaderLocationCommand {

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Resource(name = "de.augustakom.hurrican.service.cc.AuftragInternService")
    private AuftragInternService auftragInternService;

    /**
     * Ermittelt die Kundenadresse aus Hurrican. Falls diese nicht gefunden werden kann, wird die Adresse aus Taifun
     * ermittelt. Falls auch in Taifun keine Adresse vorhanden ist, wird <code>null</code> zurückgegeben.
     * @throws FindException
     */
    protected Address getAddress() throws FindException {
        Address address = null;
        AuftragDaten auftragDaten = getAuftragDaten();
        AuftragIntern auftragIntern = auftragInternService.findByAuftragId(auftragDaten.getAuftragId());
        if (auftragIntern != null && auftragIntern.getHvtStandortId() != null) {
            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(auftragIntern.getHvtStandortId());
            if(StringUtils.isNotBlank(hvtGruppe.getOrt())
                    && StringUtils.isNotBlank(hvtGruppe.getPlz())
                    && StringUtils.isNotBlank(hvtGruppe.getStrasse())
                    && StringUtils.isNotBlank(hvtGruppe.getHausNr())) {
                address = new AddressBuilder()
                        .withCity(hvtGruppe.getOrt())
                        .withZipCode(hvtGruppe.getPlz())
                        .withStreet(hvtGruppe.getStrasse())
                        .withHouseNumber(hvtGruppe.getHausNr())
                        .build();
            }
        }
        return address != null ? address : super.getAddress();
    }

}
