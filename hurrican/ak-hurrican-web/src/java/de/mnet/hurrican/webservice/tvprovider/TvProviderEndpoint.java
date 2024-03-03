/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2013 09:57:36
 */
package de.mnet.hurrican.webservice.tvprovider;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragTvService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationRequest;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationResponse;
import de.mnet.hurrican.tvprovider.OrderStateType;
import de.mnet.hurrican.tvprovider.TvAvailabilityInformationType;

/**
 * SOAP 1.1 endpoint fuer TV-verfuegbarkeitsrelevante Daten (Vento)
 */
@Endpoint
@ObjectsAreNonnullByDefault
@CcTxRequired
public class TvProviderEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TvProviderEndpoint.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AuftragTvService")
    private AuftragTvService auftragTvService;

    @PayloadRoot(localPart = "getTvAvailabilityInformationRequest", namespace = "http://www.mnet.de/hurrican/tvprovider/")
    @ResponsePayload
    public GetTvAvailabilityInformationResponse getTvAvailabilityInformation(
            @RequestPayload GetTvAvailabilityInformationRequest request) throws TvProviderTechnicalException {
        LOGGER.debug(String.format(
                "GetTvAvailabilityInformationRequest: Geo ID %d, ONKZ %s, ASB %s, KVZ Nr %s",
                request.getGeoId(), request.getOnkz(), request.getAsb(), request.getKvz()));

        try {
            List<AuftragEndstelle> auftragEndstellen = getAuftragEndstellen(request.getGeoId());

            final GetTvAvailabilityInformationResponse response = new GetTvAvailabilityInformationResponse();
            for (AuftragEndstelle ae : auftragEndstellen) {
                TvAvailabilityInformationType tvAvailabilityInfo = createTvAvailabilityInfo(ae);
                response.getTvAvailabilityInformation().add(tvAvailabilityInfo);
            }

            return response;
        }
        catch (Exception e) {
            LOGGER.error("Unerwarteter Fehler.", e);
            throw new TvProviderTechnicalException(String.format("%s: %s", e.getClass().getName(), e.getMessage()));
        }
    }

    List<AuftragEndstelle> getAuftragEndstellen(long geoId) throws FindException {
        final List<AuftragDaten> tvAuftrage = auftragTvService.findTvAuftraege(geoId);
        final List<AuftragEndstelle> auftragEndstellen = Lists.newLinkedList();
        for (AuftragDaten auftragDaten : tvAuftrage) {
            if ((auftragDaten != null) && !auftragDaten.isHistorised()) {
                Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
                if (endstelle != null) {
                    auftragEndstellen.add(new AuftragEndstelle(auftragDaten, endstelle));
                }
            }
        }

        // juengste Auftrag (= hoechste AuftragNummern) nach vorne sortieren,
        // da per Schnittstelle immer nur ein Auftrag pro GeoId und Status geliefert wird
        Collections.sort(auftragEndstellen, new Comparator<AuftragEndstelle>() {
            final Ordering<AuftragDaten> ordering = Ordering.natural().reverse().nullsFirst()
                    .onResultOf(AuftragDaten.AUFTRAG_NO_ORIG);

            @Override
            public int compare(AuftragEndstelle left, AuftragEndstelle right) {
                return ordering.compare(left.auftrag, right.auftrag);
            }
        });

        return ImmutableList.copyOf(Iterables.filter(auftragEndstellen, new AuftragEndstelleFilter()));
    }

    TvAvailabilityInformationType createTvAvailabilityInfo(AuftragEndstelle ae) throws FindException {
        final OrderStateType state = getStatus(ae.auftrag);
        final Produkt produkt = produktService.findProdukt(ae.auftrag.getProdId());
        final ProduktGruppe produktGruppe = produktService.findProduktGruppe(produkt.getProduktGruppeId());

        final TvAvailabilityInformationType tvAvailabilityInformationType = new TvAvailabilityInformationType();
        tvAvailabilityInformationType.setBillingOrderNo(ae.auftrag.getAuftragNoOrig());
        if (ae.endstelle.getGeoId() == null) {
            throw new IllegalStateException("Endstelle von Hurrican-Vertrag hat keine GeoId, Technische-AuftragsNr=" + ae.auftrag.getAuftragId());
        }
        tvAvailabilityInformationType.setGeoId(ae.endstelle.getGeoId());
        tvAvailabilityInformationType.setOrderState(state);
        tvAvailabilityInformationType.setProductGroup(produktGruppe.getProduktGruppe());
        tvAvailabilityInformationType.setProductName(produkt.getAnschlussart());
        return tvAvailabilityInformationType;
    }

    @Nullable
    static final OrderStateType getStatus(final AuftragDaten auftragDaten) {
        final long auftragStatus = auftragDaten.getStatusId().longValue();
        if (auftragStatus < AuftragStatus.IN_BETRIEB.longValue()) {
            if (auftragDaten.isAuftragActive()) {
                return OrderStateType.IN_ERFASSUNG;
            }
            else {
                return null;
            }
        }
        else if (auftragStatus < AuftragStatus.AUFTRAG_GEKUENDIGT.longValue()) {
            return OrderStateType.AKTIV;
        }
        else {
            return OrderStateType.GEKUENDIGT;
        }
    }

    static final class AuftragEndstelleFilter implements Predicate<AuftragEndstelle> {
        Set<ProduktState> tvs = Sets.newHashSet();

        @Override
        public boolean apply(AuftragEndstelle input) {
            // keine internen Hurrican-Auftraege ohne Taifun-Auftrag
            if (input.auftrag.getAuftragNoOrig() == null) {
                return false;
            }
            OrderStateType orderStatus = getStatus(input.auftrag);
            if (orderStatus == null) {
                return false;
            }
            Long produkt = input.auftrag.getProdId();
            ProduktState produktState = new ProduktState(produkt, orderStatus);
            return tvs.add(produktState);
        }
    }

    static final class AuftragEndstelle {
        final AuftragDaten auftrag;
        final Endstelle endstelle;

        public AuftragEndstelle(AuftragDaten auftrag, Endstelle endstelle) {
            this.auftrag = auftrag;
            this.endstelle = endstelle;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(auftrag, endstelle);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof AuftragEndstelle)) {
                return false;
            }
            AuftragEndstelle that = (AuftragEndstelle) object;
            return Objects.equal(this.auftrag, that.auftrag)
                    && Objects.equal(this.endstelle, that.endstelle);
        }

        @Override
        public String toString() {
            return "auftrag=" + auftrag.getAuftragNoOrig();
        }
    }

    static final class ProduktState {
        final Long produkt;
        final OrderStateType state;

        public ProduktState(Long produkt, OrderStateType state) {
            this.produkt = produkt;
            this.state = state;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(produkt, state);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof ProduktState)) {
                return false;
            }
            ProduktState that = (ProduktState) object;
            return Objects.equal(this.produkt, that.produkt)
                    && Objects.equal(this.state, that.state);
        }
    }

}
