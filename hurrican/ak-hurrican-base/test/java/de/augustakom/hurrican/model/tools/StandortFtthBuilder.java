package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * StandortFtthBuilder
 */
public class StandortFtthBuilder implements HigherOrderBuilder<HigherOrderBuilder.Results> {

    public static class StandortFtth implements Results {
        public HVTStandortBuilder standortOnt;
        public HVTStandortBuilder standortOlt;
        public HWOntBuilder hwOntBuilder;
        public HWOltBuilder hwOltBuilder;
        public HWBaugruppeBuilder oltBaugruppe;
        public HWBaugruppeBuilder ontEthBaugruppe;
        public HWBaugruppeBuilder ontPotsBaugruppe;
        public HWBaugruppeBuilder ontRfBaugruppe;
        public EquipmentBuilder[] ontEthPorts;
        public EquipmentBuilder[] ontPotsPorts;
        public EquipmentBuilder[] ontRfPorts;
    }

    private String ontType = "ONT";
    private String ontSerialNo = "1234567890";
    private String oltModNumber = "00-00";
    private String ontETHModNumber = "0-1";
    private String ontPOTSModNumber = "0-2";
    private String ontRFModNumber = "0-3";
    private String ontEthHwEqnIn = "1-";
    private String ontEthHwSs = "ETH";
    private String ontPotsHwEqnIn = "2-";
    private String ontPotsHwSs = "POTS";
    private String ontRfHwEqnIn = "3-";
    private String ontRfHwSs = "RF";

    public StandortFtthBuilder withOntType(String ontType) {
        this.ontType = ontType;
        return this;
    }

    public StandortFtthBuilder withOntSerialNo(String ontSerialNo) {
        this.ontSerialNo = ontSerialNo;
        return this;
    }

    public StandortFtthBuilder withOltModNumber(String oltModNumber) {
        this.oltModNumber = oltModNumber;
        return this;
    }

    public StandortFtthBuilder withOntETHModNumber(String ontETHModNumber) {
        this.ontETHModNumber = ontETHModNumber;
        return this;
    }

    public StandortFtthBuilder withOntPOTSModNumber(String ontPOTSModNumber) {
        this.ontPOTSModNumber = ontPOTSModNumber;
        return this;
    }

    public StandortFtthBuilder withOntRFModNumber(String ontRFModNumber) {
        this.ontRFModNumber = ontRFModNumber;
        return this;
    }

    public StandortFtthBuilder withOntEthHwEqnIn(String ontEthHwEqnIn) {
        this.ontEthHwEqnIn = ontEthHwEqnIn;
        return this;
    }

    public StandortFtthBuilder withOntEthHwSs(String ontEthHwSs) {
        this.ontEthHwSs = ontEthHwSs;
        return this;
    }

    public StandortFtthBuilder withOntPotsHwEqnIn(String ontPotsHwEqnIn) {
        this.ontPotsHwEqnIn = ontPotsHwEqnIn;
        return this;
    }

    public StandortFtthBuilder withOntPotsHwSs(String ontPotsHwSs) {
        this.ontPotsHwSs = ontPotsHwSs;
        return this;
    }


    public StandortFtthBuilder withOntRfHwEqnIn(String ontRfHwEqnIn) {
        this.ontRfHwEqnIn = ontRfHwEqnIn;
        return this;
    }

    public StandortFtthBuilder withOntRfHwSs(String ontRfHwSs) {
        this.ontRfHwSs = ontRfHwSs;
        return this;
    }

    public StandortFtth prepare(AbstractHurricanBaseServiceTest test, Results resultsVoid) {
        final HVTTechnikBuilder huaweiTechnikBuilder = test.getBuilder(HVTTechnikBuilder.class)
                .withId(HVTTechnik.HUAWEI);
        final StandortFtth standort = new StandortFtth();

        // Standort & Racks
        standort.standortOlt = test.getBuilder(HVTStandortBuilder.class);
        standort.hwOltBuilder = test.getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(standort.standortOlt)
                .withHwProducerBuilder(huaweiTechnikBuilder);
        standort.standortOnt = test.getBuilder(HVTStandortBuilder.class)
                .withCpsProvisioning(Boolean.TRUE);
        standort.hwOntBuilder = test.getBuilder(HWOntBuilder.class)
                .withHvtStandortBuilder(standort.standortOnt)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withSerialNo(ontSerialNo)
                .withOntType(ontType);

        // OLT Baugruppe
        final HWBaugruppenTypBuilder oltBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOltValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.oltBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(oltBuilder)
                .withRackBuilder(standort.hwOltBuilder)
                .withModNumber(oltModNumber);

        // ONT_ETH Baugruppe
        final HWBaugruppenTypBuilder ontEthBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOntEthValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.ontEthBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(ontEthBuilder)
                .withRackBuilder(standort.hwOntBuilder)
                .withModNumber(ontETHModNumber);

        // ONT_POTS Baugruppe
        final HWBaugruppenTypBuilder ontPotsBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOntPotsValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.ontPotsBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(ontPotsBuilder)
                .withRackBuilder(standort.hwOntBuilder)
                .withModNumber(ontPOTSModNumber);

        // ONT_POTS Baugruppe
        final HWBaugruppenTypBuilder ontRfBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOntRfValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.ontRfBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(ontRfBuilder)
                .withRackBuilder(standort.hwOntBuilder)
                .withModNumber(ontRFModNumber);

        // ETH Ports
        standort.ontEthPorts = new EquipmentBuilder[ontEthBuilder.getPortCount()];
        for (int i = 1; i <= standort.ontEthPorts.length; ++i) {
            standort.ontEthPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.ontEthBaugruppe)
                    .withHvtStandortBuilder(standort.standortOnt)
                    .withHwEQN(ontEthHwEqnIn + i)
                    .withHwSchnittstelle(ontEthHwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }
        // POTS Ports
        standort.ontPotsPorts = new EquipmentBuilder[ontPotsBuilder.getPortCount()];
        for (int i = 1; i <= standort.ontPotsPorts.length; ++i) {
            standort.ontPotsPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.ontPotsBaugruppe)
                    .withHvtStandortBuilder(standort.standortOnt)
                    .withHwEQN(ontPotsHwEqnIn + i)
                    .withHwSchnittstelle(ontPotsHwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }
        // ETH Ports
        standort.ontRfPorts = new EquipmentBuilder[ontRfBuilder.getPortCount()];
        for (int i = 1; i <= standort.ontRfPorts.length; ++i) {
            standort.ontRfPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.ontRfBaugruppe)
                    .withHvtStandortBuilder(standort.standortOnt)
                    .withHwEQN(ontRfHwEqnIn + i)
                    .withHwSchnittstelle(ontRfHwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }

        return standort;
    }

    public void build(StandortFtth standort) {
        standort.oltBaugruppe.build();
        standort.ontEthBaugruppe.build();
        standort.ontPotsBaugruppe.build();
        standort.ontRfBaugruppe.build();
        for (EquipmentBuilder ontEthPort : standort.ontEthPorts) {
            ontEthPort.build();
        }
        for (EquipmentBuilder ontPotsPort : standort.ontPotsPorts) {
            ontPotsPort.build();
        }
        for (EquipmentBuilder ontRfPort : standort.ontRfPorts) {
            ontRfPort.build();
        }
    }
}
