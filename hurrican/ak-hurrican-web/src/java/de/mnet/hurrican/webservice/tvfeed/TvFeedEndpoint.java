/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2012 14:48:04
 */
package de.mnet.hurrican.webservice.tvfeed;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.view.TvFeedView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragTvService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.tvfeed.GeoIdResponseType;
import de.mnet.hurrican.tvfeed.GeoIdResultSetType;
import de.mnet.hurrican.tvfeed.GeoIdType;
import de.mnet.hurrican.tvfeed.GetTVFeedData4GeoIdsRequest;
import de.mnet.hurrican.tvfeed.GetTVFeedData4GeoIdsResponse;
import de.mnet.hurrican.tvfeed.GetTVFeedData4TechLocationsRequest;
import de.mnet.hurrican.tvfeed.GetTVFeedData4TechLocationsResponse;
import de.mnet.hurrican.tvfeed.TechLocationQueryType;
import de.mnet.hurrican.tvfeed.TechLocationResponseType;
import de.mnet.hurrican.tvfeed.TechLocationResultSetType;

/**
 * SOAP 1.1 endpoint fuer die TV-Versorgung.
 */
@Endpoint
@ObjectsAreNonnullByDefault
public class TvFeedEndpoint {

    private static final Logger LOGGER = Logger.getLogger(TvFeedEndpoint.class);

    private static final String CPS_NAMESPACE = "http://www.mnet.de/hurrican/tvfeed/1.0/";
    public static final int MAX_REQUEST_ITEMS = 1000;

    private Function<List<TechLocationQueryType>, List<String>> techLocationNameExtractor = in -> {
        List<String> out = new ArrayList<>(in.size());
        for (TechLocationQueryType query : in) {
            out.add(query.getTechLocationName());
        }
        return out;
    };

    private Function<TvFeedView, GeoIdType> geoIdTypeTransformator = in -> {
        if (in.getGeoId() == null) {
            return null;
        }
        GeoIdType out = new GeoIdType();
        out.setGeoId(in.getGeoId());
        out.setValidFrom(Instant.ofEpochMilli(in.getGeoIdGueltigVon().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        out.setValidTo(Instant.ofEpochMilli(in.getGeoIdGueltigBis().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        return out;
    };

    private Function<List<TvFeedView>, TechLocationResultSetType> partitionTransformator = in -> {
        TechLocationResultSetType out = new TechLocationResultSetType();
        boolean commensSet = false;
        for (TvFeedView view : in) {
            if (!commensSet) {
                out.setTechOrder((view.getAuftragsId() != null) ? view.getAuftragsId() : 0L);
                out.setTechOrderState(view.getAuftragsStatus());
                out.setBillingOrder((view.getBillingAuftragsId() != null) ? view.getBillingAuftragsId() : 0L);
                out.setTechLocationName(view.getTechStandortName());
                out.setDeviceIdentifier(view.getGeraetebezeichnung());
                out.setPortIdentifier(view.getPortBezeichner());
                out.setOntIdentifier(view.getOntBezeichner());
                commensSet = true;
            }
            out.getGeoIds().add(geoIdTypeTransformator.apply(view));
        }
        return out;
    };

    private Function<TvFeedView, GeoIdResultSetType> geoIdViewTransformator = in -> {
        GeoIdResultSetType out = new GeoIdResultSetType();
        out.setTechOrder((in.getAuftragsId() != null) ? in.getAuftragsId() : 0L);
        out.setTechOrderState(in.getAuftragsStatus());
        out.setBillingOrder((in.getBillingAuftragsId() != null) ? in.getBillingAuftragsId() : 0L);
        out.setTechLocationName(in.getTechStandortName());
        out.setDeviceIdentifier(in.getGeraetebezeichnung());
        out.setPortIdentifier(in.getPortBezeichner());
        out.setOntIdentifier(in.getOntBezeichner());
        out.setGeoId(geoIdTypeTransformator.apply(in));
        return out;
    };

    @Autowired
    private AuftragTvService auftragTvService;

    @Autowired
    private HVTService hvtService;

    @PayloadRoot(localPart = "getTVFeedData4GeoIdsRequest", namespace = CPS_NAMESPACE)
    @ResponsePayload
    public GetTVFeedData4GeoIdsResponse getTVFeedData4GeoIdsRequest(@RequestPayload GetTVFeedData4GeoIdsRequest request) {
        if ((request == null) || (request.getGeoIds() == null) || (request.getGeoIds().isEmpty())) {
            throw new IllegalArgumentException("Bitte mindestens eine Geo ID im Request angeben!");
        }
        if (request.getGeoIds().size() > MAX_REQUEST_ITEMS) {
            throw new IllegalArgumentException(String.format(
                    "Maximal sind %d Geo IDs pro Request erlaubt! Aktuell sind %d angefragt!",
                    MAX_REQUEST_ITEMS, request.getGeoIds().size()));
        }

        try {
            Map<Long, List<TvFeedView>> views = getViews4GeoIds(request);

            GetTVFeedData4GeoIdsResponse response = new GetTVFeedData4GeoIdsResponse();
            for (Long geoId : request.getGeoIds()) {
                GeoIdResponseType geoIdResponse = new GeoIdResponseType();
                geoIdResponse.setQueriedGeoId(geoId);
                List<TvFeedView> tvFeedViews = views.get(geoId);
                if ((tvFeedViews != null) && (!tvFeedViews.isEmpty())) {
                    List<GeoIdResultSetType> geoIdResultSets = geoIdResponse.getResultSets();
                    for (TvFeedView tvFeedView : tvFeedViews) {
                        geoIdResultSets.add(geoIdViewTransformator.apply(tvFeedView));
                    }
                }
                response.getGeoIdResponses().add(geoIdResponse);
            }
            return response;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw e; //throwing it results in soapfault
        }
    }

    private Map<Long, List<TvFeedView>> getViews4GeoIds(@RequestPayload GetTVFeedData4GeoIdsRequest request) {
        Map<Long, List<TvFeedView>> views;
        try {
            views = auftragTvService.findTvFeed4GeoIdViews(request.getGeoIds());
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return views;
    }

    @PayloadRoot(localPart = "getTVFeedData4TechLocationsRequest", namespace = CPS_NAMESPACE)
    @ResponsePayload
    public GetTVFeedData4TechLocationsResponse getTVFeedData4TechLocationsRequest(
            @RequestPayload GetTVFeedData4TechLocationsRequest request) {
        if ((request == null)
                || (request.getTechLocationQuerys() == null)
                || (request.getTechLocationQuerys().isEmpty())) {
            throw new IllegalArgumentException("Bitte mindestens einen Technikkürzel (Ortsteil) im Request angeben!");
        }
        if (request.getTechLocationQuerys().size() > MAX_REQUEST_ITEMS) {
            throw new IllegalArgumentException(String.format(
                    "Maximal sind %d Technikkürzel pro Request erlaubt! Aktuell sind %d angefragt!", MAX_REQUEST_ITEMS,
                    request.getTechLocationQuerys().size()));
        }

        try {
            Map<String, List<TvFeedView>> views = getViews4TechLocations(request);

            GetTVFeedData4TechLocationsResponse response = new GetTVFeedData4TechLocationsResponse();
            for (TechLocationQueryType query : request.getTechLocationQuerys()) {
                TechLocationResponseType techLocationResponse = new TechLocationResponseType();
                techLocationResponse.setQueriedTechLocation(query);
                List<TvFeedView> tvFeedViews = views.get(query.getTechLocationName());
                if ((tvFeedViews != null) && (!tvFeedViews.isEmpty())) {
                    List<TechLocationResultSetType> techLocationResultSet = techLocationResponse.getResultSets();
                    List<List<TvFeedView>> partitions = partitionTvFeedViews(tvFeedViews, query.getDeviceIdentifier());
                    for (List<TvFeedView> partition : partitions) {
                        techLocationResultSet.add(partitionTransformator.apply(partition));
                    }
                }
                response.getTechLocationResponses().add(techLocationResponse);
            }
            return response;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw e; //throwing it results in soapfault
        }
    }

    private Map<String, List<TvFeedView>> getViews4TechLocations(@RequestPayload GetTVFeedData4TechLocationsRequest request) {
        Map<String, List<TvFeedView>> views = null;
        try {
            views = auftragTvService.findTvFeed4TechLocationNameViews(techLocationNameExtractor.apply(request
                    .getTechLocationQuerys()));
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return views;
    }

    /**
     * Partitioniert (zerlegt) eine Liste von Entitaeten (Geraetebezeichnung, AuftragId, etc.) fuer einen Ortsteil in
     * Segmente. Jedes Segement haelt die Daten fuer genau einen Ortsteil, eine Geraetebezeichnung, einen versogenden Auftrag aber 1
     * bis n GeoIDs. Da diese Daten aus der DB flach ermittelt werden, muss die Struktur via Code zerlegt werden.
     *
     * @param tvFeedViews              die Liste aller Entitaeten fuer genau einen Ortsteil!
     * @param optionalDeviceIdentifier ist ein optionaler Filter auf die Geraetebezeichnung (Filter wirkt nur, wenn
     *                                 Parameter != {@code null} ist)
     * @return jedes Segment besteht aus einer Liste von {@link TvFeedView} Entitaeten. Alle Eintraege zu einem Segment
     * unterscheiden sich lediglich in der Geo ID!
     */
    static List<List<TvFeedView>> partitionTvFeedViews(List<TvFeedView> tvFeedViews, @Nullable String optionalDeviceIdentifier) {
        final String techStandort = getNameOfVersorgendenstandort(tvFeedViews);
        final List<TvFeedView> filteredViews = tvFeedViews.stream()
                .map(tvFeedView -> {
                    tvFeedView.setTechStandortName(techStandort);
                    return tvFeedView;
                })
                .filter(tvFeedView -> optionalDeviceIdentifier == null ||
                        StringUtils.equals(optionalDeviceIdentifier, tvFeedView.getGeraetebezeichnung()))
                .collect(Collectors.toList());

        return CollectionTools.chunkBy2(filteredViews, (prev, curr) ->
                !StringUtils.equals(prev.getGeraetebezeichnung(), curr.getGeraetebezeichnung()) ||
                        !NumberTools.equal(prev.getAuftragsId(), curr.getAuftragsId()));
    }

    private static String getNameOfVersorgendenstandort(List<TvFeedView> tvFeedViews) {
        for (TvFeedView tvFeedView : tvFeedViews) {
            if (tvFeedView.getTechStandortName() != null) {
                return tvFeedView.getTechStandortName();
            }
        }
        return "unbekannt";
    }
}
