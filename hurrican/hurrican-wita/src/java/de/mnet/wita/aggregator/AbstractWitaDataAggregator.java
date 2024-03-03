/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 14:58:09
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaWbciServiceFacade;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * Basis-Klasse fuer Daten-Aggregatoren. Die Aggregatoren sind dafuer zustaendig, die unterschiedlichen Daten (z.B.
 * Port-Informationen, Rufnummern etc.) zu ermitteln, die fuer einen WITA Vorgang notwendig sind. Dabei ist jeder
 * Aggregator fuer ein bestimmtes Objekt (eine bestimmte Objekt-Gruppe) zustaendig.
 */
public abstract class AbstractWitaDataAggregator<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractWitaDataAggregator.class);

    private final Class<T> aggregationType;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    RangierungsService rangierungsService;
    @Resource(name = "de.mnet.wita.service.impl.WitaDataService")
    WitaDataService witaDataService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    CCKundenService ccKundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    AvailabilityService availabilityService;
    @Resource(name = "de.mnet.wita.service.WitaConfigService")
    WitaConfigService witaConfigService;
    @Resource(name = "de.mnet.wita.service.WitaWbciServiceFacade")
    WitaWbciServiceFacade witaWbciServiceFacade;

    public AbstractWitaDataAggregator() {
        aggregationType = ReflectionTools.getTypeArgument(AbstractWitaDataAggregator.class, this.getClass());
    }

    protected CCAddress findAiAddress(Carrierbestellung cb) {
        if (cb.getAiAddressId() != null) {
            try {
                return ccKundenService.findCCAddress(cb.getAiAddressId());
            }
            catch (FindException e) {
                throw new WitaDataAggregationException("Anschlussinhaberadresse konnte nicht ermittelt werden!");
            }
        }
        return null;
    }

    public AddressModel loadAnschlussadresse(WitaCBVorgang cbVorgang) throws FindException {
        Endstelle endstelle = loadEndstelle(cbVorgang);
        AddressModel adresseStandort = endstellenService.findAnschlussadresse4Auftrag(cbVorgang.getAuftragId(),
                endstelle.getEndstelleTyp());
        if (adresseStandort != null) {
            adresseStandort = loadDtagNameFailSave(endstelle.getGeoId(), adresseStandort);
        }
        return adresseStandort;
    }

    public CCAddress loadAnschlussinhaberAdresse(WitaCBVorgang cbVorgang) {
        Carrierbestellung cb = witaDataService.loadCarrierbestellung(cbVorgang);
        return findAiAddress(cb);
    }

    /**
     * Ermittelt ueber den Availability-Service die DTAG-Schreibweise des Strassennamens.
     *
     * @param geoId   GeoID, zu der die DTAG Schreibweise ermittelt werden soll
     * @param address bisheriges Adress-Objekt
     * @return Adress-Objekt mit den Daten aus {@code address} und korrigiertem Strassen- bzw. Ortsnamen (@see
     * AvailabilityService#getDtagAddressForCb(..))
     */
    protected AddressModel loadDtagNameFailSave(Long geoId, AddressModel address) {
        try {
            return availabilityService.getDtagAddressForCb(geoId, address);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return address;
        }
    }

    public Endstelle loadEndstelle(WitaCBVorgang cbVorgang) {
        List<Endstelle> endstellen = witaDataService.loadEndstellen(cbVorgang);
        if (endstellen.isEmpty()) {
            throw new WitaDataAggregationException("Es konnten keine Endstellen ermittelt werden.");
        }
        Endstelle endstelle = endstellen.get(0);
        return endstelle;
    }

    /** Ermittelt zum cbVorgang den Endstellentyp für die Carrierbestellung
     *
     * @param carrierbestellung
     * @param cbVorgang
     * @return
     * @throws FindException
     */
    String loadEndstelleTyp(Carrierbestellung carrierbestellung, WitaCBVorgang cbVorgang) throws  FindException{
        List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellung);
        for (Endstelle endstelle : endstellen) {
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
            if ((auftragDaten != null) && cbVorgang.getAuftragId().equals(auftragDaten.getAuftragId())) {
                return endstelle.getEndstelleTyp();
            }
        }
        throw new FindException("Konnte EndstellenTyp für die Carrierbestellung mit der id "
                + carrierbestellung.getId() + " nicht ermitteln!");
    }

    /**
     * Fuehrt die Ermittlung der notwendigen Daten fuer das WITA-Objekt {@code T} durch.
     *
     * @param cbVorgang Angabe des zugehoerigen {@link CBVorgang}s, um die notwendigen Daten (z.B. Auftragsnummern(n),
     *                  Bestellungsvorgang etc.) zu ermitteln.
     * @return ermitteltes WITA-Objekt
     */
    public abstract T aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException;

    public Class<T> getAggregationType() {
        return aggregationType;
    }

    /**
     * Ermittelt weitere Hurrican-Auftraege, die zu dem angegebenen Auftrag gehoeren (Dies ist notwendig, da z.B. bei
     * Connect-Auftraegen mit mehreren Leitungen immer nur auf einem Hurrican-Auftrag der Ansprechpartner, die
     * Kundenübergabe etc. definiert wird.)
     */
    List<AuftragDatenView> loadAuftragFromWholeOrder(Long auftragId) throws FindException {
        List<AuftragDatenView> auftragDatenViews = new ArrayList<AuftragDatenView>();

        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);
        if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
            AuftragDatenQuery query = new AuftragDatenQuery();
            query.setAuftragNoOrig(auftragDaten.getAuftragNoOrig());
            query.setAuftragStatusMax(AuftragStatus.KUENDIGUNG);

            auftragDatenViews.addAll(ccAuftragService.findAuftragDatenViews(query, false));
            if (CollectionTools.isNotEmpty(auftragDatenViews) && (auftragDatenViews.size() > 1)) {
                Number[] ignoreStates = new Number[] { AuftragStatus.ABSAGE, AuftragStatus.STORNO };

                for (Iterator<AuftragDatenView> iterator = auftragDatenViews.iterator(); iterator.hasNext(); ) {
                    AuftragDatenView adView = iterator.next();
                    if (NumberTools.equal(adView.getAuftragId(), auftragId)
                            || NumberTools.isIn(adView.getAuftragStatusId(), ignoreStates)) {
                        iterator.remove();
                    }
                }
            }
        }
        return auftragDatenViews;
    }
}
