/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.2016
 */
package de.mnet.hurrican.webservice.customerorder.services;

import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;

/**
 * Service für erfassung von VerbindungsBezeichnung für einen Auftrag / Taifun Auftrag Id
 */
@Service
public class VerbindungsBezeichnungService {
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private PhysikService physikService;

    public VerbindungsBezeichnungService() {
    }

    public VerbindungsBezeichnungService(CCAuftragService ccAuftragService, PhysikService physikService) {
        this.ccAuftragService = ccAuftragService;
        this.physikService = physikService;
    }

    public List<VerbindungsBezeichnung> getVerbindungsBezeichnung(Long orderNoOrig) {
        final List<AuftragDaten> auftragDataList = getRelefantAuftragDatenList(orderNoOrig);
        final ImmutableList.Builder<VerbindungsBezeichnung> resultBuilder = ImmutableList.builder();
        auftragDataList.stream().map(this::getVerbindungsBezeichnungByAuftragId)
                .filter(vbz -> vbz != null)
                .forEach(resultBuilder::add);
        return resultBuilder.build();
    }

    private List<AuftragDaten> getRelefantAuftragDatenList(Long orderNoOrig) {
        final List<AuftragDaten> auftragDatenList = getAuftragDaten4OrderNoOrig(orderNoOrig);
        return filterAuftragDaten(auftragDatenList);
    }

    private List<AuftragDaten> getAuftragDaten4OrderNoOrig(Long orderNoOrig) {
        try {
            final List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);
            return auftragDaten4OrderNoOrig != null ? auftragDaten4OrderNoOrig : Collections.EMPTY_LIST;
        }
        catch (FindException e) {
            throw new RuntimeException(
                    String.format("Error by finding order by customer order number [%d]", orderNoOrig), e);
        }
    }

    private VerbindungsBezeichnung getVerbindungsBezeichnungByAuftragId(AuftragDaten auftragDaten) {
        try {
            return physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
        }
        catch (FindException e) {
            throw new RuntimeException(String.format("Error by finding VerbindungsBezeichnung by order number [%d]",
                    auftragDaten.getAuftragId()), e);
        }
    }

    private List<AuftragDaten> filterAuftragDaten(List<AuftragDaten> auftragDaten) {
        return auftragDaten.stream()
                .filter(AuftragDaten::isAuftragActiveAndInBetrieb)
                .collect(Collectors.toList());
    }

}
