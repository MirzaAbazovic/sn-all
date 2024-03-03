/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2011 09:23:16
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.SIPDomainDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;

@CcTxRequired
public class SIPDomainServiceImpl extends DefaultCCService implements SIPDomainService {
    private static final Logger LOGGER = Logger.getLogger(SIPDomainServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;

    @Resource(name = "sipDomainDAO")
    private SIPDomainDAO sipDomainDAO;

    @Override
    public void saveProdukt2SIPDomain(Produkt2SIPDomain toSave, Long sessionId) throws StoreException {
        if ((toSave == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            toSave.setUserW(getUserNameAndFirstNameSilent(sessionId));
            sipDomainDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteProdukt2SIPDomain(Produkt2SIPDomain toDelete) {
        if (toDelete == null) {return;}
        sipDomainDAO.deleteProdukt2SIPDomain(toDelete);
    }

    @Override
    @Nonnull
    public List<Produkt2SIPDomain> findProdukt2SIPDomains(Long prodId, HWSwitch hwSwitch, Boolean defaultDomainsOnly) {
        return findSIPDomains4Produkt(prodId, hwSwitch, null, defaultDomainsOnly);
    }

    @Override
    @Nonnull
    public List<EGType2SIPDomain> findEGType2SIPDomains(EGType egType, HWSwitch hwSwitch) {
        return findSipDomains4EgTyp(egType, hwSwitch);
    }

    @Override
    public CalculatedSipDomain4VoipAuftrag calculateSipDomain4VoipAuftrag(Long auftragId) throws FindException {
        CalculatedSipDomain4VoipAuftrag result = new CalculatedSipDomain4VoipAuftrag();
        result.calculatedSipDomain = findSipDomain4EgsByAuftragId(auftragId);
        result.isOverride = true;
        if (result.calculatedSipDomain == null) {
            result.calculatedSipDomain = findDefaultSIPDomain4Auftrag(auftragId);
            result.isOverride = false;
        }
        return result;
    }
    @Override
    public Reference findDefaultSIPDomain4Auftrag(Long auftragId) throws FindException {
        List<Reference> references = findPossibleSIPDomains4Auftrag(auftragId, true);
        return !references.isEmpty() ? references.get(0) : null;
    }

    @Override
    public List<Reference> findPossibleSIPDomains4Auftrag(Long auftragId, boolean sipDomain4EgOnly)
            throws FindException {
        if (auftragId == null) {return Collections.emptyList();}
        // Switch Kennung ermitteln
        AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
        if (auftragTechnik == null || auftragTechnik.getHwSwitch() == null) {return Collections.emptyList();}
        HWSwitch hwSwitch = auftragTechnik.getHwSwitch();

        // Produkt ID ermitteln
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (auftragDaten == null || auftragDaten.getProdId() == null) {
            return Collections.emptyList();
        }
        return findPossibleSIPDomains(auftragDaten.getProdId(), auftragId, hwSwitch, sipDomain4EgOnly);
    }

    @Override
    public List<Reference> findPossibleSIPDomains(Long prodId, Long auftragId, HWSwitch hwSwitch,
            boolean sipDomain4EgOnly) throws FindException {
        final Deque<Reference> resultProdukt = new LinkedList<>();
        // Overrides 4 Endgeraet
        if (sipDomain4EgOnly) {
            // Add Endgeräte SIP-Domain as first entry if it present!
            findSipDomain4Egs(auftragId, hwSwitch).ifPresent(resultProdukt::addFirst);
        }
        else {
            // Add all SIP-Domains for Switch as first entries
            Set<Reference> sipDomains4EgTyp = findSipDomains4EgTyp(null, hwSwitch).stream()
                    .map(EGType2SIPDomain::getSipDomainRef)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(HashSet::new));
            sipDomains4EgTyp.stream().forEach(resultProdukt::addFirst);
        }

        // Produkt Konfig
        // Add additional produkt domains as minor priority (-> higher value), if present
        Comparator<Produkt2SIPDomain> defaultAndIndexComparator = (p2sd1, p2sd2) -> {
            final int result;
            if (p2sd1.getDefaultDomain().equals(p2sd2.getDefaultDomain())) {
                result = (int) (p2sd1.getId() - p2sd2.getId());
            }
            else
                result = p2sd1.getDefaultDomain() ? -1 : 1;
            return result;
        };
        findSIPDomains4Produkt(prodId, hwSwitch, null, null).stream()
                .sorted(defaultAndIndexComparator)
                .map(Produkt2SIPDomain::getSipDomainRef)
                .filter(Objects::nonNull)
                        //add only entries which aren't included
                .filter(sipRef -> !resultProdukt.stream().anyMatch(r -> r.getId().equals(sipRef.getId())))
                .forEach(resultProdukt::addLast);

        return (LinkedList<Reference>) resultProdukt;
    }

    @Override
    public Reference findSipDomain4EgsByAuftragId(@NotNull Long auftragId) throws FindException {
        if (auftragId == null) {
            return null;
        }
        // Switch Kennung ermitteln
        AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
        if (auftragTechnik == null || auftragTechnik.getHwSwitch() == null) {
            return null;
        }
        HWSwitch hwSwitch = auftragTechnik.getHwSwitch();

        return findSipDomain4Egs(auftragId, hwSwitch).orElse(null);
    }

    Optional<Reference> findSipDomain4Egs(@NotNull Long auftragId, @NotNull HWSwitch hwSwitch) throws FindException {
        List<EGConfig> egConfigs = endgeraeteService.findEgConfigs4Auftrag(auftragId);
        List<EGType2SIPDomain> produkt2SIPDomains = egConfigs.stream()
                .filter(Objects::nonNull)
                .flatMap(egc -> findSipDomains4EgTyp(egc.getEgType(), hwSwitch).stream())
                .collect(Collectors.toList());
        Set<Reference> sipDomainsCondensed = produkt2SIPDomains.stream()
                .map(EGType2SIPDomain::getSipDomainRef)
                .collect(Collectors.toCollection(HashSet::new));
        if (sipDomainsCondensed.size() > 1) {
            throw new FindException(String.format("Die Endgeräte des Auftrags %s für den Switch %s liefern mehr als eine"
                    + " SIP-Domäne zurück!", auftragId, hwSwitch.getName()));
        }
        return (sipDomainsCondensed.size() == 1) ? Optional.of(sipDomainsCondensed.iterator().next()) : Optional.empty();
    }

    @Override
    public Reference migrateSIPDomain(Long prodId, Reference currentSIPDomain, HWSwitch destSwitch) throws FindException {
        if ((prodId == null) || (destSwitch == null)) {
            throw new FindException("Produkt ID und Switch Referenz (Ziel) müssen gesetzt sein!");
        }
        // 1) SIP Domäne nicht gesetzt?
        if (currentSIPDomain == null) {return null;}
        // 2) existierende SIP Domäne ist ohne Switch konfiguriert?
        List<Produkt2SIPDomain> produkt2SIPDomains = findSIPDomains4Produkt(prodId, null, currentSIPDomain, null);
        if (CollectionTools.isNotEmpty(produkt2SIPDomains)) {
            return currentSIPDomain;
        }
        // 3) existierende SIP Domäne ist auch auf neuem Switch konfiguriert?
        produkt2SIPDomains = findSIPDomains4Produkt(prodId, destSwitch, currentSIPDomain, null);
        if (CollectionTools.isNotEmpty(produkt2SIPDomains)) {
            return currentSIPDomain;
        }
        // 4) existierende SIP Domäne ist nicht auf neuem Switch konfiguriert?
        // 4.1) default Domäne des neuen Switches verfuegbar
        produkt2SIPDomains = findSIPDomains4Produkt(prodId, destSwitch, null, Boolean.TRUE);
        if (CollectionTools.isNotEmpty(produkt2SIPDomains)) {
            return produkt2SIPDomains.get(0).getSipDomainRef();
        }
        // 4.2) default Domäne des neuen Switches nicht verfuegbar
        return null;
    }

    /**
     * Sucht in allen Konfiguration, die Produkt und Switch konfiguriert haben
     */
    List<Produkt2SIPDomain> findSIPDomains4Produkt(Long prodId, HWSwitch hwSwitch, Reference sipDomain,
            Boolean defaultDomain) {
        Produkt2SIPDomain example = new Produkt2SIPDomain();
        example.setProdId(prodId);
        example.setSipDomainRef(sipDomain);
        example.setHwSwitch(hwSwitch);
        example.setDefaultDomain(defaultDomain);
        return sipDomainDAO.querySIPDomain4Produkt(example);
    }

    /**
     * Sucht in allen Konfiguration, die Switch und Endgeraet konfiguriert haben
     */
    List<EGType2SIPDomain> findSipDomains4EgTyp(EGType egType, HWSwitch hwSwitch) {
        EGType2SIPDomain example = new EGType2SIPDomain();
        example.setEgType(egType);
        example.setHwSwitch(hwSwitch);
        return sipDomainDAO.querySIPDomain4Eg(example);
    }

    /**
     * Injected
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setSipDomainDAO(SIPDomainDAO sipDomainDAO) {
        this.sipDomainDAO = sipDomainDAO;
    }

    /**
     * Injected
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

}
