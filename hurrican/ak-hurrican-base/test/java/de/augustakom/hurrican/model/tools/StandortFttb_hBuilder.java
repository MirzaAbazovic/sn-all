package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * StandortFtth_bBuilder
 */
public class StandortFttb_hBuilder implements HigherOrderBuilder<HigherOrderBuilder.Results> {

    public static class StandortFttb_h implements Results {
        public HVTStandortBuilder standort;
        public HVTStandortBuilder standortOlt;
        public HWOntBuilder hwOntBuilder;
        public HWDpoBuilder hwDpoBuilder;
        public HWOltBuilder hwOltBuilder;
        public HWBaugruppeBuilder oltBaugruppe;
        public HWBaugruppeBuilder ontEthBaugruppe;
        public HWBaugruppeBuilder ontPotsBaugruppe;
        public HWBaugruppeBuilder ontRfBaugruppe;
        public HWBaugruppeBuilder[] dpoVdslBaugruppen;
        public EquipmentBuilder[] ontEthPorts;
        public EquipmentBuilder[] ontPotsPorts;
        public EquipmentBuilder[] ontRfPorts;
        public EquipmentBuilder[] dpoVdslPorts;
    }

    private int countDpos = 8;
    private String ontType = "ONT";
    private String dpoType = "DPO";
    private String ontSerialNo = "1234567890";
    private String dpoSerialNo = "0987654321";
    private String oltModNumber = "00-00";
    private String ontETHModNumber = "0-1";
    private String ontPOTSModNumber = "0-2";
    private String ontRFModNumber = "0-3";
    private String dpoVdsl2ModNumber = "0-1";
    private String ontEthHwEqnIn = "1-";
    private String ontEthHwSs = "ETH";
    private String ontPotsHwEqnIn = "2-";
    private String ontPotsHwSs = "POTS";
    private String ontRfHwEqnIn = "3-";
    private String ontRfHwSs = "RF";
    private String dpoVdsl2HwEqnIn = "1-";
    private String dpoVdsl2HwSs = "VDSL2";

    public StandortFttb_hBuilder withOntType(String ontType) {
        this.ontType = ontType;
        return this;
    }

    public StandortFttb_hBuilder withDpoType(String dpoType) {
        this.dpoType = dpoType;
        return this;
    }

    public StandortFttb_hBuilder withOntSerialNo(String ontSerialNo) {
        this.ontSerialNo = ontSerialNo;
        return this;
    }

    public StandortFttb_hBuilder withDpoSerialNo(String dpoSerialNo) {
        this.dpoSerialNo = dpoSerialNo;
        return this;
    }

    public StandortFttb_hBuilder withOltModNumber(String oltModNumber) {
        this.oltModNumber = oltModNumber;
        return this;
    }

    public StandortFttb_hBuilder withOntETHModNumber(String ontETHModNumber) {
        this.ontETHModNumber = ontETHModNumber;
        return this;
    }

    public StandortFttb_hBuilder withOntPOTSModNumber(String ontPOTSModNumber) {
        this.ontPOTSModNumber = ontPOTSModNumber;
        return this;
    }

    public StandortFttb_hBuilder withOntRFModNumber(String ontRFModNumber) {
        this.ontRFModNumber = ontRFModNumber;
        return this;
    }

    public StandortFttb_hBuilder withOntEthHwEqnIn(String ontEthHwEqnIn) {
        this.ontEthHwEqnIn = ontEthHwEqnIn;
        return this;
    }

    public StandortFttb_hBuilder withOntEthHwSs(String ontEthHwSs) {
        this.ontEthHwSs = ontEthHwSs;
        return this;
    }

    public StandortFttb_hBuilder withOntPotsHwEqnIn(String ontPotsHwEqnIn) {
        this.ontPotsHwEqnIn = ontPotsHwEqnIn;
        return this;
    }

    public StandortFttb_hBuilder withOntPotsHwSs(String ontPotsHwSs) {
        this.ontPotsHwSs = ontPotsHwSs;
        return this;
    }

    public StandortFttb_hBuilder withOntRfHwEqnIn(String ontRfHwEqnIn) {
        this.ontRfHwEqnIn = ontRfHwEqnIn;
        return this;
    }

    public StandortFttb_hBuilder withOntRfHwSs(String ontRfHwSs) {
        this.ontRfHwSs = ontRfHwSs;
        return this;
    }

    public StandortFttb_h prepare(AbstractHurricanBaseServiceTest test, Results resultsVoid) {
        final HVTTechnikBuilder huaweiTechnikBuilder = test.getBuilder(HVTTechnikBuilder.class)
                .withId(HVTTechnik.HUAWEI);
        final StandortFttb_h standort = new StandortFttb_h();

        // Standort & Racks
        standort.standortOlt = test.getBuilder(HVTStandortBuilder.class);
        standort.hwOltBuilder = test.getBuilder(HWOltBuilder.class)
                .withHvtStandortBuilder(standort.standortOlt)
                .withHwProducerBuilder(huaweiTechnikBuilder);
        standort.standort = test.getBuilder(HVTStandortBuilder.class)
                .withCpsProvisioning(Boolean.TRUE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB_H);
        standort.hwOntBuilder = test.getBuilder(HWOntBuilder.class)
                .withHvtStandortBuilder(standort.standort)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withSerialNo(ontSerialNo)
                .withOntType(ontType);
        standort.hwDpoBuilder = test.getBuilder(HWDpoBuilder.class)
                .withHvtStandortBuilder(standort.standort)
                .withHwProducerBuilder(huaweiTechnikBuilder)
                .withSerialNo(dpoSerialNo)
                .withDpoType(dpoType);

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

        final HWBaugruppenTypBuilder dpoVdslBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setDpoVdslValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.dpoVdslBaugruppen = new HWBaugruppeBuilder[countDpos];
        for (int i = 1; i <= standort.dpoVdslBaugruppen.length; ++i) {
            standort.dpoVdslBaugruppen[i - 1] = test.getBuilder(HWBaugruppeBuilder.class)
                    .withBaugruppenTypBuilder(dpoVdslBuilder)
                    .withRackBuilder(standort.hwDpoBuilder)
                    .withModNumber(dpoVdsl2ModNumber);
        }

        // ETH Ports
        standort.ontEthPorts = new EquipmentBuilder[ontEthBuilder.getPortCount()];
        for (int i = 1; i <= standort.ontEthPorts.length; ++i) {
            standort.ontEthPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.ontEthBaugruppe)
                    .withHvtStandortBuilder(standort.standort)
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
                    .withHvtStandortBuilder(standort.standort)
                    .withHwEQN(ontPotsHwEqnIn + i)
                    .withHwSchnittstelle(ontPotsHwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }
        // RF Ports
        standort.ontRfPorts = new EquipmentBuilder[ontRfBuilder.getPortCount()];
        for (int i = 1; i <= standort.ontRfPorts.length; ++i) {
            standort.ontRfPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.ontRfBaugruppe)
                    .withHvtStandortBuilder(standort.standort)
                    .withHwEQN(ontRfHwEqnIn + i)
                    .withHwSchnittstelle(ontRfHwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }
        // DPO VDSL2 Ports
        standort.dpoVdslPorts = new EquipmentBuilder[countDpos];
        for (int i = 1; i <= standort.dpoVdslPorts.length; ++i) {
            standort.dpoVdslPorts[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.dpoVdslBaugruppen[i - 1])
                    .withHvtStandortBuilder(standort.standort)
                    .withHwEQN(dpoVdsl2HwEqnIn + i)
                    .withHwSchnittstelle(dpoVdsl2HwSs)
                    .withRangSSType(null)
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
        }

        return standort;
    }

    public void build(StandortFttb_h standort) {
        standort.oltBaugruppe.build();
        standort.ontEthBaugruppe.build();
        standort.ontPotsBaugruppe.build();
        standort.ontRfBaugruppe.build();
        for (HWBaugruppeBuilder dpoBaugruppe : standort.dpoVdslBaugruppen) {
            dpoBaugruppe.build();
        }
        for (EquipmentBuilder ontEthPort : standort.ontEthPorts) {
            ontEthPort.build();
        }
        for (EquipmentBuilder ontPotsPort : standort.ontPotsPorts) {
            ontPotsPort.build();
        }
        for (EquipmentBuilder ontRfPort : standort.ontRfPorts) {
            ontRfPort.build();
        }
        for (EquipmentBuilder dpoVdsl2Port : standort.dpoVdslPorts) {
            dpoVdsl2Port.build();
        }
    }

}
