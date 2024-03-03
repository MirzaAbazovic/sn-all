/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 11:02:10
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;


@SuppressWarnings("unused")
public class AuftragHousingBuilder extends EntityBuilder<AuftragHousingBuilder, AuftragHousing> {

    private AuftragBuilder auftragBuilder;
    private AuftragDatenBuilder auftragDatenBuilder;

    private HousingBuilding building;

    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    private Long auftragId;
    private Long buildingId;
    private Long floorId;
    private Long roomId;
    private Long parcelId;
    private String rack = randomString(20);
    private Long rackUnits = randomLong(1000);
    private Long electricCircuitCount = randomLong(1000);
    private Float electricCircuitCapacity = Float.valueOf(randomLong(1000).longValue());
    private Long electricSafeguard = randomLong(1000);

    private String electricCounterNumber = randomString(20);
    private Double electricCounterStart = randomLong(1000).doubleValue();
    private Double electricCounterEnd = randomLong(1000).doubleValue();

    private String electricCounterNumber2 = randomString(20);
    private Double electricCounterStart2 = randomLong(1000).doubleValue();
    private Double electricCounterEnd2 = randomLong(1000).doubleValue();

    private String electricCounterNumber3 = randomString(20);
    private Double electricCounterStart3 = randomLong(1000).doubleValue();
    private Double electricCounterEnd3 = randomLong(1000).doubleValue();

    private String electricCounterNumber4 = randomString(20);
    private Double electricCounterStart4 = randomLong(1000).doubleValue();
    private Double electricCounterEnd4 = randomLong(1000).doubleValue();


    public AuftragHousingBuilder withAuftragDatenBuilder(AuftragDatenBuilder auftragDatenBuilder) {
        this.auftragDatenBuilder = auftragDatenBuilder;
        return this;
    }

    public AuftragHousingBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragHousingBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public AuftragHousingBuilder withBuilding(HousingBuilding building) {
        this.building = building;
        return this;
    }

    public AuftragHousingBuilder withFloorId(Long floorId) {
        this.floorId = floorId;
        return this;
    }

    public AuftragHousingBuilder withRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public AuftragHousingBuilder withParcelId(Long parcelId) {
        this.parcelId = parcelId;
        return this;
    }

    public AuftragHousingBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigBis = gueltigVon;
        return this;
    }

    public AuftragHousingBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

}


