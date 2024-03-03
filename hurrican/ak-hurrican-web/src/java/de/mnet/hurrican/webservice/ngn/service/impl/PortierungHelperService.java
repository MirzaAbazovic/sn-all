/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2016
 */
package de.mnet.hurrican.webservice.ngn.service.impl;

import java.util.*;
import java.util.stream.*;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.WitaCBVorgang;

@Service
public class PortierungHelperService {

    private CCAuftragService auftragService;
    private WbciCommonService wbciCommonService;
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    private RufnummerService rufnummerService;

    public PortierungHelperService(CCAuftragService auftragService,
            WbciCommonService wbciCommonService,
            WbciWitaServiceFacade wbciWitaServiceFacade,
            RufnummerService rufnummerService) {
        this.auftragService = auftragService;
        this.wbciCommonService = wbciCommonService;
        this.wbciWitaServiceFacade = wbciWitaServiceFacade;
        this.rufnummerService = rufnummerService;
    }

    public List<AuftragDaten> findAuftragDaten(long orderNoOrig) {
        try {
            return auftragService.findAuftragDaten4OrderNoOrigTx(orderNoOrig);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
    }

    Stream<AuftragDaten> filterActiveAuftraegeStream(Stream<AuftragDaten> auftragDatenStream) {
        return auftragDatenStream.filter(AuftragDaten::isAuftragActiveAndInBetrieb);
    }

    public boolean hasActiveAuftraege(Collection<AuftragDaten> auftragDate) {
        return filterActiveAuftraegeStream(auftragDate.stream()).findAny().isPresent();
    }

    public Stream<AuftragDaten> filterInactiveAuftraegeStream(Stream<AuftragDaten> auftragDatenStream) {
        return auftragDatenStream.filter(auftrag -> !auftrag.isAuftragActiveAndInBetrieb());
    }

    public boolean hasInactiveAuftraege(Collection<AuftragDaten> auftragDate) {
        return filterInactiveAuftraegeStream(auftragDate.stream()).findAny().isPresent();
    }

    public List<WbciGeschaeftsfall> findActiveWbciGeschaeftsfaelle(long orderNoOrig) {
        return wbciCommonService.findActiveGfByTaifunId(orderNoOrig, true);
    }

    public List<WitaCBVorgang> findNonClosedWitaCbVorgaenge(Collection<AuftragDaten> auftragDaten) {
        return auftragDaten
                .stream()
                .flatMap(ad -> wbciWitaServiceFacade.findWitaCbVorgaengeByAuftrag(ad.getAuftragId()).stream())
                .filter(witaCBVorgang -> !witaCBVorgang.isClosed())
                .collect(Collectors.toList());
    }

    public List<WbciGeschaeftsfall> findCompleteWbciGeschaeftsfaelle(long orderNoOrig) {
        return wbciCommonService.findCompleteGfByTaifunId(orderNoOrig);
    }

    public List<WitaCBVorgang>findWitaCbVorgaenge(WbciGeschaeftsfall wbciGeschaeftsfall){
        return wbciWitaServiceFacade.findWitaCbVorgaenge(wbciGeschaeftsfall.getVorabstimmungsId());
    }

    public Set<String> findPortierungsKennungBilling(final long orderNoOrig) {
        try {
            List<Rufnummer> rNs4Auftrag = rufnummerService.findRNs4Auftrag(orderNoOrig);
            return rNs4Auftrag.stream().map(Rufnummer::getActCarrierPortKennung).collect(Collectors.toSet());
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
    }

    public String findPortierungsKennungVa(WbciGeschaeftsfall wbciGeschaeftsfall)  {
        UebernahmeRessourceMeldung akmtr =
                wbciCommonService.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), UebernahmeRessourceMeldung.class);
        return akmtr != null ? akmtr.getPortierungskennungPKIauf() : "";
    }
}
