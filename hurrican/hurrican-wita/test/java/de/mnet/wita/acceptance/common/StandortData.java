/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 11:47:01
 */
package de.mnet.wita.acceptance.common;

import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;

public class StandortData {
    private final HVTGruppe hvtGruppe;
    private final HVTStandort hvtStandort;
    private final GeoId geoId;
    private final GeoId2TechLocation geoId2TechLocation;
    private final String uevt;

    public StandortData(HVTGruppe hvtGruppe, HVTStandort hvtStandort, GeoId geoId,
            GeoId2TechLocation geoId2TechLocation, String uevt) {
        this.hvtGruppe = hvtGruppe;
        this.hvtStandort = hvtStandort;
        this.geoId = geoId;
        this.geoId2TechLocation = geoId2TechLocation;
        this.uevt = uevt;
    }

    public HVTGruppe getHvtGruppe() {
        return hvtGruppe;
    }

    public HVTStandort getHvtStandort() {
        return hvtStandort;
    }

    public GeoId getGeoId() {
        return geoId;
    }

    public GeoId2TechLocation getGeoId2TechLocation() {
        return geoId2TechLocation;
    }

    public String getUevt() {
        return uevt;
    }
}
