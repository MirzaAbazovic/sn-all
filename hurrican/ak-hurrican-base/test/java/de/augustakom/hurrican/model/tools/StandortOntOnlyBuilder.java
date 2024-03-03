package de.augustakom.hurrican.model.tools;

import java.util.*;

import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * StandortOntOnlyBuilder: Baut einen FTTH Standort, eine OLT und eine ONT (ohne Baugruppen, Ports und Rangierungen)
 */
public class StandortOntOnlyBuilder implements HigherOrderBuilder<HigherOrderBuilder.Results> {

    public static class StandortOntOnly implements Results {
        public HVTStandortBuilder standortOnt;
        public HVTStandortBuilder standortOlt;
        public HWOntBuilder hwOntBuilder;
        public HWOltBuilder hwOltBuilder;
        public HWBaugruppeBuilder oltBaugruppe;
        public HWBaugruppenTypBuilder ontEthTypBuilder;
    }

    private String ontType = "ONT";
    private String ontSerialNo = "1234567890";
    private String oltModNumber = "00-00";
    private Date ontGueltigVon = null;
    private Date ontGueltigBis = null;

    public StandortOntOnlyBuilder withOntType(String ontType) {
        this.ontType = ontType;
        return this;
    }

    public StandortOntOnlyBuilder withOntSerialNo(String ontSerialNo) {
        this.ontSerialNo = ontSerialNo;
        return this;
    }

    public StandortOntOnlyBuilder withOltModNumber(String oltModNumber) {
        this.oltModNumber = oltModNumber;
        return this;
    }

    public StandortOntOnlyBuilder withOntGueltigVon(Date ontGueltigVon) {
        this.ontGueltigVon = ontGueltigVon;
        return this;
    }

    public StandortOntOnlyBuilder withOntGueltigBis(Date ontGueltigBis) {
        this.ontGueltigBis = ontGueltigBis;
        return this;
    }

    public StandortOntOnly prepare(AbstractHurricanBaseServiceTest test, Results resultsVoid) {
        final HVTTechnikBuilder huaweiTechnikBuilder = test.getBuilder(HVTTechnikBuilder.class)
                .withId(HVTTechnik.HUAWEI);
        final StandortOntOnly standort = new StandortOntOnly();

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
        if (ontGueltigVon != null) {
            standort.hwOntBuilder.withGueltigVon(ontGueltigVon);
        }
        if (ontGueltigBis != null) {
            standort.hwOntBuilder.withGueltigBis(ontGueltigBis);
        }

        // OLT Baugruppe
        final HWBaugruppenTypBuilder oltBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOltValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);
        standort.oltBaugruppe = test.getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(oltBuilder)
                .withRackBuilder(standort.hwOltBuilder)
                .withModNumber(oltModNumber);

        // ONT_ETH Baugruppe
        standort.ontEthTypBuilder = test.getBuilder(HWBaugruppenTypBuilder.class)
                .setOntEthValues()
                .withHvtTechnikBuilder(huaweiTechnikBuilder);

        return standort;
    }

    public void build(StandortOntOnly standort) {
        standort.oltBaugruppe.build();
        standort.ontEthTypBuilder.build();
        standort.hwOntBuilder.build();
    }
}
