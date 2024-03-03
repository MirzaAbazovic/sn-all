/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Definition von Housing-Daten.
 */
@Entity
@Table(name = "T_AUFTRAG_HOUSING")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_HOUSING_0", allocationSize = 1)
public class AuftragHousing extends AbstractCCHistoryModel implements CCAuftragModel {

    private static final long serialVersionUID = 8759496047011391950L;
    private Long auftragId;
    private Long buildingId;
    private Long floorId;
    private Long roomId;
    private Long parcelId;
    private String rack;
    private Long rackUnits;
    private Long electricCircuitCount;
    private Float electricCircuitCapacity;
    private Long electricSafeguard;

    private String electricCounterNumber;
    private Double electricCounterStart;
    private Double electricCounterEnd;

    private String electricCounterNumber2;
    private Double electricCounterStart2;
    private Double electricCounterEnd2;

    private String electricCounterNumber3;
    private Double electricCounterStart3;
    private Double electricCounterEnd3;

    private String electricCounterNumber4;
    private Double electricCounterStart4;
    private Double electricCounterEnd4;


    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "BUILDING_ID")
    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    @Column(name = "FLOOR_ID")
    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    @Column(name = "ROOM_ID")
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Column(name = "PARCELL_ID")
    public Long getParcelId() {
        return parcelId;
    }

    public void setParcelId(Long parcelId) {
        this.parcelId = parcelId;
    }

    @Column(name = "RACK")
    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    @Column(name = "RACK_UNITS")
    public Long getRackUnits() {
        return rackUnits;
    }

    public void setRackUnits(Long rackUnits) {
        this.rackUnits = rackUnits;
    }

    @Column(name = "ELECTRIC_CIRCUIT_COUNT")
    public Long getElectricCircuitCount() {
        return electricCircuitCount;
    }

    public void setElectricCircuitCount(Long electricCircuitCount) {
        this.electricCircuitCount = electricCircuitCount;
    }

    @Column(name = "ELECTRIC_CAPACITY")
    public Float getElectricCircuitCapacity() {
        return electricCircuitCapacity;
    }

    public void setElectricCircuitCapacity(Float electricCircuitCapacity) {
        this.electricCircuitCapacity = electricCircuitCapacity;
    }

    @Column(name = "ELECTRIC_SAFEGUARD")
    public Long getElectricSafeguard() {
        return electricSafeguard;
    }

    public void setElectricSafeguard(Long electricSafeguard) {
        this.electricSafeguard = electricSafeguard;
    }

    @Column(name = "ELECTRIC_COUNTER_START", precision = 12, scale = 2)
    public Double getElectricCounterStart() {
        return electricCounterStart;
    }

    public void setElectricCounterStart(Double electricCounterStart) {
        this.electricCounterStart = electricCounterStart;
    }

    @Column(name = "ELECTRIC_COUNTER_END", precision = 12, scale = 2)
    public Double getElectricCounterEnd() {
        return electricCounterEnd;
    }

    public void setElectricCounterEnd(Double electricCounterEnd) {
        this.electricCounterEnd = electricCounterEnd;
    }

    @Column(name = "ELECTRIC_COUNTER_START_2", precision = 12, scale = 2)
    public Double getElectricCounterStart2() {
        return electricCounterStart2;
    }

    public void setElectricCounterStart2(Double electricCounterStart2) {
        this.electricCounterStart2 = electricCounterStart2;
    }

    @Column(name = "ELECTRIC_COUNTER_END_2", precision = 12, scale = 2)
    public Double getElectricCounterEnd2() {
        return electricCounterEnd2;
    }

    public void setElectricCounterEnd2(Double electricCounterEnd2) {
        this.electricCounterEnd2 = electricCounterEnd2;
    }

    @Column(name = "ELECTRIC_COUNTER_START_3", precision = 12, scale = 2)
    public Double getElectricCounterStart3() {
        return electricCounterStart3;
    }

    public void setElectricCounterStart3(Double electricCounterStart3) {
        this.electricCounterStart3 = electricCounterStart3;
    }

    @Column(name = "ELECTRIC_COUNTER_END_3", precision = 12, scale = 2)
    public Double getElectricCounterEnd3() {
        return electricCounterEnd3;
    }

    public void setElectricCounterEnd3(Double electricCounterEnd3) {
        this.electricCounterEnd3 = electricCounterEnd3;
    }

    @Column(name = "ELECTRIC_COUNTER_START_4", precision = 12, scale = 2)
    public Double getElectricCounterStart4() {
        return electricCounterStart4;
    }

    public void setElectricCounterStart4(Double electricCounterStart4) {
        this.electricCounterStart4 = electricCounterStart4;
    }

    @Column(name = "ELECTRIC_COUNTER_END_4", precision = 12, scale = 2)
    public Double getElectricCounterEnd4() {
        return electricCounterEnd4;
    }

    public void setElectricCounterEnd4(Double electricCounterEnd4) {
        this.electricCounterEnd4 = electricCounterEnd4;
    }


    @Column(name = "ELECTRIC_COUNTER_NUMBER")
    public String getElectricCounterNumber() {
        return electricCounterNumber;
    }

    public void setElectricCounterNumber(String electricCounterNumber) {
        this.electricCounterNumber = electricCounterNumber;
    }

    @Column(name = "ELECTRIC_COUNTER_NUMBER_2")
    public String getElectricCounterNumber2() {
        return electricCounterNumber2;
    }

    public void setElectricCounterNumber2(String electricCounterNumber2) {
        this.electricCounterNumber2 = electricCounterNumber2;
    }

    @Column(name = "ELECTRIC_COUNTER_NUMBER_3")
    public String getElectricCounterNumber3() {
        return electricCounterNumber3;
    }

    public void setElectricCounterNumber3(String electricCounterNumber3) {
        this.electricCounterNumber3 = electricCounterNumber3;
    }

    @Column(name = "ELECTRIC_COUNTER_NUMBER_4")
    public String getElectricCounterNumber4() {
        return electricCounterNumber4;
    }

    public void setElectricCounterNumber4(String electricCounterNumber4) {
        this.electricCounterNumber4 = electricCounterNumber4;
    }


}


