/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2012 15:52:44
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.AuftragTvDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.shared.view.TvFeedView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragTvService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Service-Implementierung von AuftragService.
 */
@CcTxRequired
public class AuftragTvServiceImpl extends DefaultCCService implements AuftragTvService {

    @Resource(name = "auftragTvDAO")
    private AuftragTvDAO auftragTvDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    protected CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    protected EndstellenService endstellenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;


    @Override
    @Nonnull
    public Map<Long, List<TvFeedView>> findTvFeed4GeoIdViews(@Nonnull List<Long> geoIds) throws FindException {
        try {
            final Map<Long, List<TvFeedView>> tvFeed = new HashMap<>();
            for (final Long geoId : geoIds) {
                final List<AuftragDaten> versorgendeAuftraege = findVersorgendeAuftraege(geoId);
                final List<Long> auftragIds = AuftragDaten.AUFTRAG_ID_EXTRACTOR_4_AUFTRAG_DATEN.apply(versorgendeAuftraege);
                final List<Integer> buendelNrs = AuftragDaten.BUENDEL_NR_EXTRACTOR_4_AUFTRAG_DATEN.apply(versorgendeAuftraege);
                final List<TvFeedView> tvFeed4Auftraege = getAuftragTvDAO().findTvFeed4Auftraege(auftragIds, buendelNrs);
                tvFeed.put(geoId, tvFeed4Auftraege);
            }
            return tvFeed;
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findVersorgendeAuftraege(Long geoId) throws FindException {
        List<AuftragDaten> tvAuftraege = findTvAuftraege(geoId);
        List<AuftragDaten> versorgendeAuftraege = new ArrayList<>();
        for (AuftragDaten auftragDaten : tvAuftraege) {
            if (isVersorgenderAuftrag(auftragDaten)) {
                versorgendeAuftraege.add(auftragDaten);
            }
        }
        return versorgendeAuftraege;
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findTvAuftraege(Long geoId) throws FindException {
        List<AuftragDaten> tvAuftraege = new ArrayList<>();
        List<AuftragDaten> auftragDaten4GeoId = ccAuftragService.findAuftragDatenByGeoIdProduktIds(geoId,
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG, Produkt.PROD_ID_TV_SIGNALLIEFERUNG_MV);
        for (AuftragDaten ad : AuftragDaten.RETURN_ACTIVE_AUFTRAG_DATEN.apply(auftragDaten4GeoId)) {
            tvAuftraege.addAll(findAssociatedActiveAuftraege(ad));
            tvAuftraege.add(ad);
        }
        return tvAuftraege;
    }

    /**
     * Iterates through the supplied list of {@code auftraege}, checking whether the auftrag is a versorgender-auftrag.
     * If it is, its added to the {@code versorgenderAuftraege} list.
     * @param versorgenderAuftraege
     * @param auftraege
     * @throws FindException
     */
    protected void addVersorgenderAuftraege(List<AuftragDaten> versorgenderAuftraege, List<AuftragDaten> auftraege) throws FindException {
        for(AuftragDaten auftrag : auftraege) {
            if(isVersorgenderAuftrag(auftrag)) {
                versorgenderAuftraege.add(auftrag);
            }
        }
    }

    /**
     * Checks to see if the passed auftrag is a versorgender-auftrag. The check is done by examining the endstellen of
     * the auftrag. If an endstelle has a rangierung, then it is considered to be a versorgender-auftrag. A
     * Mit-Versorgtenauftrag will never have an endstelle with a rangierung.
     *
     * @param auftragDaten the auftrag to check
     * @return true for versorgender-auftrag
     * @throws FindException
     */
    protected boolean isVersorgenderAuftrag(AuftragDaten auftragDaten) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        return endstelle != null && endstelle.hasRangierung();
    }

    /**
     * Using the supplied {@code auftragDaten} the associated (bundled) auftraege are retrieved. Only bundled
     * auftraege with an active status are considered. If the supplied {@code auftragDaten} has no bundled auftraege
     * then this auftrag is returned.
     *
     * @param auftragDaten
     * @return
     * @throws FindException
     */
    protected List<AuftragDaten> findAssociatedActiveAuftraege(AuftragDaten auftragDaten) throws FindException {
        if(auftragDaten.getBuendelNr() != null && auftragDaten.getBuendelNrHerkunft() != null) {
            List<AuftragDaten> bundledAuftraege = ccAuftragService.findAuftragDaten4BuendelTx(auftragDaten.getBuendelNr(), auftragDaten.getBuendelNrHerkunft());
            for (Iterator<AuftragDaten> iterator = bundledAuftraege.iterator(); iterator.hasNext();) {
                AuftragDaten bundledAuftrag = iterator.next();
                if (bundledAuftrag.getStatusId() >= AuftragStatus.AUFTRAG_GEKUENDIGT || NumberTools.isIn(bundledAuftrag.getStatusId(),
                        new Number[] { AuftragStatus.ABSAGE, AuftragStatus.STORNO })) {
                    iterator.remove();
                }
            }
            return bundledAuftraege;
        }
        // if not bundled, just return the auftrag
        return Lists.newArrayList(auftragDaten);
    }

    @Override
    public Map<String, List<TvFeedView>> findTvFeed4TechLocationNameViews(final List<String> techLocationNames)
            throws FindException {

        try {
            final String tvProduktGruppe = produktService.findProduktGruppe(ProduktGruppe.TV).getProduktGruppe();
            final Map<String, List<TvFeedView>> tvFeed = new HashMap<>();
            for (final String techLocationName : techLocationNames) {
                final List<AuftragDaten> aktiveAuftragDaten =
                        ccAuftragService.findAktiveAuftragDatenByOrtsteilAndProduktGroup(techLocationName, tvProduktGruppe);
                final List<AuftragDaten> versorgenderAuftraege = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(aktiveAuftragDaten)) {
                    for(AuftragDaten aktiveAuftrag: aktiveAuftragDaten) {
                        List<AuftragDaten> relevanteAuftraege = findAssociatedActiveAuftraege(aktiveAuftrag);
                        relevanteAuftraege.add(aktiveAuftrag);
                        addVersorgenderAuftraege(versorgenderAuftraege, relevanteAuftraege);
                    }
                }
                final List<Long> auftragIds = AuftragDaten.AUFTRAG_ID_EXTRACTOR_4_AUFTRAG_DATEN.apply(versorgenderAuftraege);
                final List<Integer> buendelNrs = AuftragDaten.BUENDEL_NR_EXTRACTOR_4_AUFTRAG_DATEN.apply(versorgenderAuftraege);
                final List<TvFeedView> tvFeed4Auftraege = getAuftragTvDAO().findTvFeed4Auftraege(auftragIds, buendelNrs);
                tvFeed.put(techLocationName, tvFeed4Auftraege);
            }
            return tvFeed;
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    // Getter und Setter
    protected AuftragTvDAO getAuftragTvDAO() {
        return auftragTvDAO;
    }

    public void setAuftragTvDAO(AuftragTvDAO auftragTvDAO) {
        this.auftragTvDAO = auftragTvDAO;
    }

}


