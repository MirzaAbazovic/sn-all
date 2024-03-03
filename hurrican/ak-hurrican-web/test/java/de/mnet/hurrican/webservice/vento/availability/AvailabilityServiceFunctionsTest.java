/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.2012 17:02:11
 */
package de.mnet.hurrican.webservice.vento.availability;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.h2.util.StringUtils;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.vento.availability.VentoAvailabilityInformationType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoConnectionType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.model.cc.vento.availability.VentoTechnologyType;
import de.mnet.hurrican.vento.availability.ConnectionType;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationRequest;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationResponse;
import de.mnet.hurrican.vento.availability.HurricanIncommingAvailabilityInformationType;
import de.mnet.hurrican.vento.availability.TechnologyType;

@Test(groups = BaseTest.UNIT)
public class AvailabilityServiceFunctionsTest extends BaseTest {

    @Test
    public void testToVentoGetAvailabilityInformationRequest() {
        GetAvailabilityInformationRequest in = new GetAvailabilityInformationRequest();
        in.setGeoId(Long.valueOf(1L));
        in.setOnkz("089");
        in.setAsb("123001");
        in.setKvz("A001");

        VentoGetAvailabilityInformationRequest out = AvailabilityServiceFunctions.toVentoGetAvailabilityInformationRequest
                .apply(in);

        assertTrue(NumberTools.equal(out.getGeoId(), in.getGeoId()));
        assertTrue(StringUtils.equals(out.getOnkz(), in.getOnkz()));
        assertTrue(StringUtils.equals(out.getAsb(), in.getAsb()));
        assertTrue(StringUtils.equals(out.getKvz(), in.getKvz()));
    }

    @Test
    public void testToGetAvailabilityInformationResponse() {
        VentoGetAvailabilityInformationResponse in = new VentoGetAvailabilityInformationResponse();
        List<VentoAvailabilityInformationType> ventoAvailabilityInformationTypes = in.getAvailabilityInformationTypes();
        VentoAvailabilityInformationType typeIn = new VentoAvailabilityInformationType();
        typeIn.setDistanceInMeters(Integer.valueOf(100));
        typeIn.setDistanceApproved(Boolean.TRUE);
        typeIn.setMaxDownstreamBandwidthInKB(null);
        typeIn.setConnection(VentoConnectionType.FTTB);
        typeIn.setTechnology(VentoTechnologyType.VDSL2);
        typeIn.setStart(LocalDate.now());
        typeIn.setTermination(DateTools.getHurricanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        ventoAvailabilityInformationTypes.add(typeIn);

        GetAvailabilityInformationResponse out = AvailabilityServiceFunctions.toGetAvailabilityInformationResponse
                .apply(in);

        assertTrue(!CollectionUtils.isEmpty(out.getAvailabilityInformation()));
        assertTrue(out.getAvailabilityInformation().size() == 1);
        HurricanIncommingAvailabilityInformationType typeOut = out.getAvailabilityInformation().get(0);
        assertTrue(NumberTools.equal(typeOut.getDistanceInMeters(), typeIn.getDistanceInMeters()));
        assertTrue(BooleanTools.nullToFalse(typeOut.isDistanceApproved()) == BooleanTools.nullToFalse(typeIn
                .getDistanceApproved()));
        assertTrue(NumberTools.equal(typeOut.getMaxDownstreamBandwidthInKB(), typeIn.getMaxDownstreamBandwidthInKB()));
        assertTrue(typeOut.getConnection() == ConnectionType.fromValue(typeIn.getConnection().name()));
        assertTrue(typeOut.getTechnology() == TechnologyType.fromValue(typeIn.getTechnology().name()));
        assertTrue(typeOut.getStart().equals(typeIn.getStart()));
        assertTrue(typeOut.getTermination().equals(typeIn.getTermination()));
    }
}


