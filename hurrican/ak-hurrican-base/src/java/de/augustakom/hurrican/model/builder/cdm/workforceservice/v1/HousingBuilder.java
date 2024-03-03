/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class HousingBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Housing> {

    private String building;
    private String street;
    private String zipCode;
    private String floor;
    private String room;
    protected String plot;
    protected String rack;
    protected String rackUnits;
    protected String circuitCount;
    protected String wattage;
    protected String fuse;
    private List<OrderTechnicalParams.Housing.ElectricMeter> electricMeter = Lists.newArrayList();
    private List<OrderTechnicalParams.Housing.Transponder> transponder = Lists.newArrayList();


    @Override
    public OrderTechnicalParams.Housing build() {
        OrderTechnicalParams.Housing housing = new OrderTechnicalParams.Housing();
        housing.setBuilding(this.building);
        housing.setStreet(this.street);
        housing.setZipCode(this.zipCode);
        housing.setFloor(this.floor);
        housing.setRoom(this.room);
        housing.setPlot(this.plot);
        housing.setRack(this.rack);
        housing.setRackUnits(this.rackUnits);
        housing.setCircuitCount(this.circuitCount);
        housing.setWattage(this.wattage);
        housing.setFuse(this.fuse);
        if (electricMeter != null) {
            housing.getElectricMeter().addAll(electricMeter);
        }
        if (transponder != null) {
            housing.getTransponder().addAll(transponder);
        }
        return housing;
    }


    public HousingBuilder withBuilding(String building) {
        this.building = building;
        return this;
    }

    public HousingBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public HousingBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public HousingBuilder withFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public HousingBuilder withRoom(String room) {
        this.room = room;
        return this;
    }

    public HousingBuilder withPlot(String plot) {
        this.plot = plot;
        return this;
    }

    public HousingBuilder withRack(String rack) {
        this.rack = rack;
        return this;
    }

    public HousingBuilder withRackUnits(String rackUnits) {
        this.rackUnits = rackUnits;
        return this;
    }

    public HousingBuilder withCircuitCount(String circuitCount) {
        this.circuitCount = circuitCount;
        return this;
    }

    public HousingBuilder withWattage(String wattage) {
        this.wattage = wattage;
        return this;
    }

    public HousingBuilder withFuse(String fuse) {
        this.fuse = fuse;
        return this;
    }

    public HousingBuilder withElectricMeter(List<OrderTechnicalParams.Housing.ElectricMeter> electricMeter) {
        this.electricMeter = electricMeter;
        return this;
    }

    public HousingBuilder addElectricMeter(OrderTechnicalParams.Housing.ElectricMeter toAdd) {
        if (this.electricMeter == null) {
            this.electricMeter = new ArrayList<>();
        }
        this.electricMeter.add(toAdd);
        return this;
    }

    public HousingBuilder withTransponder(List<OrderTechnicalParams.Housing.Transponder> transponder) {
        this.transponder = transponder;
        return this;
    }

}
