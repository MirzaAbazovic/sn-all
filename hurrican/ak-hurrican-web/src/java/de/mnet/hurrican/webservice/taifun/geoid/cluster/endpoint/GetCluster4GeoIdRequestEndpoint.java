/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2011 15:22:50
 */

package de.mnet.hurrican.webservice.taifun.geoid.cluster.endpoint;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.WebServiceFaultException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.view.HVTClusterView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdRequest;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdRequestDocument;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdResponse;
import de.mnet.hurricanweb.geoid.cluster.types.GetCluster4GeoIdResponseDocument;


/**
 * Endpoint-Implementierung, um Cluster ID/technische Niederlassung zu ermitteln und dem Caller zur Verfuegung zu
 * stellen. Die Ermittlung der Daten laeuft nach folgendem Schema: <ul> <li> nach erstem HVT Standort mit gesetzter
 * Cluster ID suchen, wenn <ul> <li> Cluster ID gesetzt, Cluster ID, ONKZ und AreaNo aus Standort zurueckmelden <li>
 * Cluster ID NICHT gesetzt, nach erstem HVT Standort mit gesetzter ONKZ suchen, wenn <ul> <li> ONKZ gesetzt, Cluster ID
 * mit NULL, ONKZ und AreaNo aus Standort zurueckmelden <li> ONKZ NICHT gesetzt, Cluster ID und ONKZ mit NULL und AreaNo
 * mit 0 zurueckmelden </ul> </ul> </ul>
 */
public class GetCluster4GeoIdRequestEndpoint extends MnetAbstractMarshallingPayloadEndpoint {
    private static final Logger LOGGER = Logger.getLogger(GetCluster4GeoIdRequestEndpoint.class);

    private HVTService hvtService;
    private AvailabilityService availabilityService;

    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("GetCluster4GeoIdRequestEndpoint called");
        initServices();

        try {
            return execute(requestObject);
        }
        catch (WebServiceFaultException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WebServiceFaultException(e.getMessage());
        }
    }

    private void initServices() throws Exception {
        try {
            hvtService = getCCService(HVTService.class);
            availabilityService = getCCService(AvailabilityService.class);
        }
        catch (Exception e) {
            LOGGER.error("Could not initialize services", e);
            throw e;
        }
    }

    private Object execute(Object requestObject) throws FindException {
        // Input: Request POJO und Geo ID ermitteln
        GetCluster4GeoIdRequestDocument requestDocument = (GetCluster4GeoIdRequestDocument) requestObject;
        GetCluster4GeoIdRequest requestData = requestDocument.getGetCluster4GeoIdRequest();
        Long geoId = Long.valueOf(requestData.getGeoId());
        String clusterId = null;
        String onkz = null;
        long areaNo = 0;

        List<GeoId2TechLocation> locations = availabilityService.findGeoId2TechLocations(geoId);
        List<HVTClusterView> clusterViews = findHVTClusterViews4Locations(locations);
        if (CollectionTools.isNotEmpty(clusterViews)) {
            // Cluster ID suchen
            HVTClusterView clusterView = findFirstCluster(clusterViews);
            if (clusterView != null) {
                areaNo = clusterView.getAreaNo().longValue();
                clusterId = clusterView.getClusterId();
                onkz = clusterView.getOnkz();
            }
            else {
                // Wenn keine Cluster Id verfuegbar, dann nach einer ONKZ suchen
                clusterView = findFirstOnkz(clusterViews);
                if (clusterView != null) {
                    areaNo = clusterView.getAreaNo().longValue();
                    onkz = clusterView.getOnkz();
                }
            }
        }

        // Output: Response POJO bauen und setzen
        GetCluster4GeoIdResponseDocument responseDocument = GetCluster4GeoIdResponseDocument.Factory.newInstance();
        GetCluster4GeoIdResponse responseData = responseDocument.addNewGetCluster4GeoIdResponse();
        responseData.setClusterId(clusterId);
        responseData.setAreaNo(areaNo);
        responseData.setOnkz(onkz);
        return responseData;
    }

    private HVTClusterView findFirstOnkz(List<HVTClusterView> clusterViews) {
        if (CollectionTools.isEmpty(clusterViews)) {return null;}
        for (HVTClusterView hvtClusterView : clusterViews) {
            if (StringUtils.isNotBlank(hvtClusterView.getOnkz())) {
                return hvtClusterView;
            }
        }
        return null;
    }

    private HVTClusterView findFirstCluster(List<HVTClusterView> clusterViews) {
        if (CollectionTools.isEmpty(clusterViews)) {return null;}
        for (HVTClusterView hvtClusterView : clusterViews) {
            if (StringUtils.isNotBlank(hvtClusterView.getClusterId())) {
                return hvtClusterView;
            }
        }
        return null;
    }

    private List<HVTClusterView> findHVTClusterViews4Locations(List<GeoId2TechLocation> locations) {
        if (CollectionTools.isEmpty(locations)) { return Collections.emptyList(); }
        try {
            List<Long> hvtStandortIds = new ArrayList<Long>();
            for (GeoId2TechLocation location : locations) {
                if (location.getHvtIdStandort() != null) {
                    hvtStandortIds.add(location.getHvtIdStandort());
                }
            }
            return hvtService.findHVTClusterViews(hvtStandortIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

}
