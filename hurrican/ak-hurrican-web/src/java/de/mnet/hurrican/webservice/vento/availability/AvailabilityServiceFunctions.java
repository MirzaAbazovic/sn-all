/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2012 10:15:29
 */
package de.mnet.hurrican.webservice.vento.availability;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.util.CollectionUtils;

import de.augustakom.hurrican.model.cc.vento.availability.VentoAvailabilityInformationType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoCablePart;
import de.augustakom.hurrican.model.cc.vento.availability.VentoConnectionType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.model.cc.vento.availability.VentoTechnologyType;
import de.mnet.hurrican.vento.availability.CablePart;
import de.mnet.hurrican.vento.availability.ConnectionType;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationRequest;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationResponse;
import de.mnet.hurrican.vento.availability.HurricanIncommingAvailabilityInformationType;
import de.mnet.hurrican.vento.availability.TechnologyType;

/**
 * Transformatoren, um WebService Object (via JaxB erstellt) in Hurrican Service Object und vice versa zu
 * transformieren
 */
public class AvailabilityServiceFunctions {
    public final static Function<GetAvailabilityInformationRequest, VentoGetAvailabilityInformationRequest> toVentoGetAvailabilityInformationRequest = new Function<GetAvailabilityInformationRequest, VentoGetAvailabilityInformationRequest>() {
        @Override
        public VentoGetAvailabilityInformationRequest apply(GetAvailabilityInformationRequest in) {
            VentoGetAvailabilityInformationRequest out = new VentoGetAvailabilityInformationRequest();
            out.setGeoId(Long.valueOf(in.getGeoId()));
            out.setOnkz(in.getOnkz());
            out.setAsb(in.getAsb());
            out.setKvz(in.getKvz());
            return out;
        }
    };

    public interface NonNullFunction<F, T> extends Function<F, T> {
        @Override
        @Nonnull
        T apply(@Nullable F input);

        @Override
        boolean equals(@Nullable Object object);
    }

    public final static NonNullFunction<VentoGetAvailabilityInformationResponse, GetAvailabilityInformationResponse> toGetAvailabilityInformationResponse = new NonNullFunction<VentoGetAvailabilityInformationResponse, GetAvailabilityInformationResponse>() {
        @Nonnull
        @Override
        public GetAvailabilityInformationResponse apply(@Nullable VentoGetAvailabilityInformationResponse in) {
            GetAvailabilityInformationResponse out = new GetAvailabilityInformationResponse();
            if ((in != null) && !CollectionUtils.isEmpty(in.getAvailabilityInformationTypes())) {
                for (VentoAvailabilityInformationType ventoAvailabilityInformationType : in
                        .getAvailabilityInformationTypes()) {
                    HurricanIncommingAvailabilityInformationType typeOut = new HurricanIncommingAvailabilityInformationType();
                    typeOut.getCableParts()
                            .addAll(toCableParts.apply(ventoAvailabilityInformationType.getCableParts()));
                    typeOut.setConnection(toConnectionType.apply(ventoAvailabilityInformationType.getConnection()));
                    typeOut.setTechnology(toTechnologyType.apply(ventoAvailabilityInformationType.getTechnology()));
                    typeOut.setDistanceInMeters(ventoAvailabilityInformationType.getDistanceInMeters());
                    typeOut.setDistanceApproved(ventoAvailabilityInformationType.getDistanceApproved());
                    typeOut.setMaxDownstreamBandwidthInKB(ventoAvailabilityInformationType
                            .getMaxDownstreamBandwidthInKB());
                    typeOut.setStart(ventoAvailabilityInformationType.getStart());
                    typeOut.setTermination(ventoAvailabilityInformationType.getTermination());
                    out.getAvailabilityInformation().add(typeOut);
                }
            }
            return out;
        }
    };

    public final static Function<List<VentoCablePart>, List<CablePart>> toCableParts = new Function<List<VentoCablePart>, List<CablePart>>() {
        @Override
        public List<CablePart> apply(List<VentoCablePart> in) {
            List<CablePart> out = new ArrayList<CablePart>();
            if (!CollectionUtils.isEmpty(in)) {
                for (VentoCablePart ventoCablePart : in) {
                    out.add(toCablePart.apply(ventoCablePart));
                }
            }
            return out;
        }
    };

    public final static Function<VentoCablePart, CablePart> toCablePart = new Function<VentoCablePart, CablePart>() {
        @Override
        public CablePart apply(VentoCablePart in) {
            CablePart out = new CablePart();
            if (in != null) {
                out.setDiameterInMillimeter(in.getDiameterInMillimeter());
                out.setLengthInMeter((in.getLengthInMeter() != null) ? in.getLengthInMeter().intValue() : 0);
                return out;
            }
            return null;
        }
    };

    public final static Function<VentoConnectionType, ConnectionType> toConnectionType = new Function<VentoConnectionType, ConnectionType>() {
        @Override
        public ConnectionType apply(VentoConnectionType in) {
            if (in != null) {
                return ConnectionType.fromValue(in.name());
            }
            return null;
        }
    };

    public final static Function<VentoTechnologyType, TechnologyType> toTechnologyType = new Function<VentoTechnologyType, TechnologyType>() {
        @Override
        public TechnologyType apply(VentoTechnologyType in) {
            if (in != null) {
                return TechnologyType.fromValue(in.name());
            }
            return null;
        }
    };
}


