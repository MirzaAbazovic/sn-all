/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2010 09:47:37
 */
package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 *
 */
public class StandortHvtBuilder implements HigherOrderBuilder<HigherOrderBuilder.Results> {

    public static class StandortHvt implements Results {
        public HVTStandortBuilder standort;
        public HWDslamBuilder dslamRack;
        public HWDluBuilder dluRack;
        public HWSubrackBuilder dslamSubrack;
        public HWBaugruppeBuilder adslBaugruppe;
        public HWBaugruppeBuilder sdslBaugruppe;
        public HWBaugruppeBuilder abBaugruppe;
        public HWBaugruppeBuilder uk0Baugruppe;
        public EquipmentBuilder[] adslInEquipment;
        public EquipmentBuilder[] adslOutEquipment;
        public EquipmentBuilder[] sdslEquipment;
        public EquipmentBuilder[] abEquipment;
        public EquipmentBuilder[] uk0Equipment;
    }

    private Long hvtTechnikDlu = HVTTechnik.SIEMENS;
    private Long hvtTechnikDslam = HVTTechnik.ALCATEL;
    private String adslModulnummer = "R1/S1-LT01";
    private String sdslModulnummer = "R1/S1-LT09";
    private String sdslHwEqnInPrefix = "1-1-9-";
    private String adslHwEqnInPrefix = "1-1-1-";
    private String adslHwEqnOutPrefix = "1-1-1-";
    private String abRangVerteiler = "01";
    private String abRangBucht = "01";
    private String abRangLeiste1 = "01";
    private String abRangStift1 = "%02d";
    private String uk0RangVerteiler = "01";
    private String uk0RangBucht = "01";
    private String uk0RangLeiste1 = "02";
    private String uk0RangStift1 = "%02d";


    public StandortHvtBuilder withHvtTechnikDlu(Long hvtTechnikDlu) {
        this.hvtTechnikDlu = hvtTechnikDlu;
        return this;
    }

    public StandortHvtBuilder withHvtTechnikDslam(Long hvtTechnikDslam) {
        this.hvtTechnikDslam = hvtTechnikDslam;
        return this;
    }

    public StandortHvtBuilder withAdslModulnummer(String adslModulnummer) {
        this.adslModulnummer = adslModulnummer;
        return this;
    }

    public StandortHvtBuilder withSdslModulnummer(String sdslModulnummer) {
        this.sdslModulnummer = sdslModulnummer;
        return this;
    }

    public StandortHvtBuilder withSdslHwEqnInPrefix(String sdslHwEqnInPrefix) {
        this.sdslHwEqnInPrefix = sdslHwEqnInPrefix;
        return this;
    }

    public StandortHvtBuilder withAdslHwEqnInPrefix(String adslHwEqnInPrefix) {
        this.adslHwEqnInPrefix = adslHwEqnInPrefix;
        return this;
    }

    public StandortHvtBuilder withAdslHwEqnOutPrefix(String adslHwEqnOutPrefix) {
        this.adslHwEqnOutPrefix = adslHwEqnOutPrefix;
        return this;
    }

    public StandortHvtBuilder withAbRangVerteiler(String abRangVerteiler) {
        this.abRangVerteiler = abRangVerteiler;
        return this;
    }

    public StandortHvtBuilder withAbRangBucht(String abRangBucht) {
        this.abRangBucht = abRangBucht;
        return this;
    }

    public StandortHvtBuilder withAbRangLeiste1(String abRangLeiste1) {
        this.abRangLeiste1 = abRangLeiste1;
        return this;
    }

    public StandortHvtBuilder withAbRangStift1(String abRangStift1) {
        this.abRangStift1 = abRangStift1;
        return this;
    }

    public StandortHvtBuilder withUk0RangVerteiler(String uk0RangVerteiler) {
        this.uk0RangVerteiler = uk0RangVerteiler;
        return this;
    }

    public StandortHvtBuilder withUk0RangBucht(String uk0RangBucht) {
        this.uk0RangBucht = uk0RangBucht;
        return this;
    }

    public StandortHvtBuilder withUk0RangLeiste1(String uk0RangLeiste1) {
        this.uk0RangLeiste1 = uk0RangLeiste1;
        return this;
    }

    public StandortHvtBuilder withUk0RangStift1(String uk0RangStift1) {
        this.uk0RangStift1 = uk0RangStift1;
        return this;
    }

    @Override
    public StandortHvt prepare(AbstractHurricanBaseServiceTest test, Results resultsVoid) {
        HVTTechnikBuilder technikBuilder1 = test.getBuilder(HVTTechnikBuilder.class)
                .withId(hvtTechnikDlu);
        HVTTechnikBuilder technikBuilder2 = test.getBuilder(HVTTechnikBuilder.class)
                .withId(hvtTechnikDslam);

        // Standort & Racks
        StandortHvt standort = new StandortHvt();
        standort.standort = test.getBuilder(HVTStandortBuilder.class);
        standort.dslamRack = test.getBuilder(HWDslamBuilder.class)
                .withHvtStandortBuilder(standort.standort)
                .withHwProducerBuilder(technikBuilder2);
        standort.dluRack = test.getBuilder(HWDluBuilder.class)
                .withHvtStandortBuilder(standort.standort)
                .withHwProducerBuilder(technikBuilder1);

        standort.dslamSubrack = test.getBuilder(HWSubrackBuilder.class)
                .withRackBuilder(standort.dslamRack);

        // ADSL Baugruppe
        HWBaugruppenTypBuilder adslBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setAdslValues()
                .withHvtTechnikBuilder(technikBuilder2);
        standort.adslBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withSubrackBuilder(standort.dslamSubrack)
                .withBaugruppenTypBuilder(adslBuilder)
                .withModNumber(adslModulnummer);

        // SDSL Baugruppe
        HWBaugruppenTypBuilder sdslBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setSdslValues()
                .withHvtTechnikBuilder(technikBuilder2);
        standort.sdslBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withSubrackBuilder(standort.dslamSubrack)
                .withBaugruppenTypBuilder(sdslBuilder)
                .withModNumber(sdslModulnummer);

        // DLU Baugruppen
        HWBaugruppenTypBuilder abBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setAbValues()
                .withHvtTechnikBuilder(technikBuilder1);
        standort.abBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(standort.dluRack)
                .withBaugruppenTypBuilder(abBuilder);
        HWBaugruppenTypBuilder uk0Builder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setUk0Values()
                .withHvtTechnikBuilder(technikBuilder1);
        standort.uk0Baugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(standort.dluRack)
                .withBaugruppenTypBuilder(uk0Builder);

        // ADSL Equipment
        standort.adslInEquipment = new EquipmentBuilder[adslBuilder.getPortCount()];
        standort.adslOutEquipment = new EquipmentBuilder[adslBuilder.getPortCount()];
        for (int i = 1; i <= adslBuilder.getPortCount(); ++i) {
            standort.adslInEquipment[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.adslBaugruppe)
                    .withHvtStandortBuilder(standort.standort)
                    .withHwEQN(adslHwEqnInPrefix + i)
                    .withHwSchnittstelle("ADSL-OUT")
                    .withRangSSType("ADSL-OUT")
                    .withStatus(EqStatus.frei)
                    .withManualConfiguration(Boolean.FALSE);
            standort.adslOutEquipment[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withBaugruppeBuilder(standort.adslBaugruppe)
                    .withHvtStandortBuilder(standort.standort)
                    .withHwEQN(adslHwEqnOutPrefix + i)
                    .withHwSchnittstelle("ADSL-IN")
                    .withRangSSType("ADSL-IN");
        }

        // SDSL Equipment
        standort.sdslEquipment = new EquipmentBuilder[sdslBuilder.getPortCount()];
        for (int i = 1; i <= sdslBuilder.getPortCount(); ++i) {
            standort.sdslEquipment[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withHvtStandortBuilder(standort.standort)
                    .withBaugruppeBuilder(standort.sdslBaugruppe)
                    .withHwEQN(sdslHwEqnInPrefix + i)
                    .withHwSchnittstelle("SDSL-OUT")
                    .withRangSSType("SDSL-OUT")
                    .withStatus(EqStatus.frei);
        }

        // DLU Equipment
        standort.abEquipment = new EquipmentBuilder[abBuilder.getPortCount()];
        for (int i = 1; i <= abBuilder.getPortCount(); ++i) {
            standort.abEquipment[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withHvtStandortBuilder(standort.standort)
                    .withBaugruppeBuilder(standort.abBaugruppe)
                    .withHwEQN(standort.dluRack.getDluNumber() + "-" + standort.abBaugruppe.getModNumber() + "-" + String.format("%02d", i))
                    .withHwSchnittstelle("AB")
                    .withStatus(EqStatus.frei)
                    .withRangSSType((i <= abBuilder.getPortCount()) ? "ADSL-AB" : "AB")
                    .withRangVerteiler(abRangVerteiler)
                    .withRangBucht(abRangBucht)
                    .withRangLeiste1(abRangLeiste1)
                    .withRangStift1(String.format(abRangStift1, i));
        }
        standort.uk0Equipment = new EquipmentBuilder[uk0Builder.getPortCount()];
        for (int i = 1; i <= uk0Builder.getPortCount(); ++i) {
            standort.uk0Equipment[i - 1] = test.getBuilder(EquipmentBuilder.class)
                    .withHvtStandortBuilder(standort.standort)
                    .withBaugruppeBuilder(standort.uk0Baugruppe)
                    .withHwEQN(standort.dluRack.getDluNumber() + "-" + standort.uk0Baugruppe.getModNumber() + "-" + String.format("%02d", i))
                    .withHwSchnittstelle("UK0")
                    .withStatus(EqStatus.frei)
                    .withRangSSType((i <= uk0Builder.getPortCount()) ? "ADSL-UK0" : "UK0")
                    .withRangVerteiler(uk0RangVerteiler)
                    .withRangBucht(uk0RangBucht)
                    .withRangLeiste1(uk0RangLeiste1)
                    .withRangStift1(String.format(uk0RangStift1, i));
        }

        return standort;
    }

    public void build(StandortHvt standort) {
        standort.adslBaugruppe.build();
        standort.abBaugruppe.build();
        standort.uk0Baugruppe.build();
        for (EquipmentBuilder adslIn : standort.adslInEquipment) {
            adslIn.build();
        }
        for (EquipmentBuilder adslOut : standort.adslOutEquipment) {
            adslOut.build();
        }
        for (EquipmentBuilder abIn : standort.abEquipment) {
            abIn.build();
        }
        for (EquipmentBuilder uk0In : standort.uk0Equipment) {
            uk0In.build();
        }
    }
}
