package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * AuftragSdslBuilder
 */
public class AuftragSdslBuilder implements HigherOrderBuilder<StandortHvtBuilder.StandortHvt> {

    public class AuftragSingleSdsl extends AuftragWithCb {
        public RangierungBuilder rangierungBuilder;
    }

    public class AuftragSdsl implements Results {
        public AuftragSingleSdsl[] auftraege;
    }

    private Long prodId = 64L;
    private Long statusId = AuftragStatus.IN_BETRIEB;

    public AuftragSdslBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        return this;
    }

    public AuftragSdslBuilder withStatusId(Long statusId) {
        this.statusId = statusId;
        return this;
    }

    @Override
    public AuftragSdsl prepare(AbstractHurricanBaseServiceTest test, StandortHvtBuilder.StandortHvt standortHvt) {
        final AuftragSdsl auftragSdsl = new AuftragSdsl();
        auftragSdsl.auftraege = new AuftragSingleSdsl[] { new AuftragSingleSdsl(), new AuftragSingleSdsl() };

        // SDSL
        auftragSdsl.auftraege[0].rangierungBuilder = test.getBuilder(RangierungBuilder.class)
                .withEqInBuilder(standortHvt.sdslEquipment[0])
                .withHvtStandortBuilder(standortHvt.standort);
        auftragSdsl.auftraege[0].endstelleBuilder = test.getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(standortHvt.standort)
                .withRangierungBuilder(auftragSdsl.auftraege[0].rangierungBuilder);
        auftragSdsl.auftraege[0].auftragDatenBuilder = test.getBuilder(AuftragDatenBuilder.class)
                .withStatusId(statusId)
                .withProdId(prodId);
        auftragSdsl.auftraege[0].auftragTechnikBuilder = test.getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(auftragSdsl.auftraege[0].endstelleBuilder);
        auftragSdsl.auftraege[0].auftragBuilder = test.getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragSdsl.auftraege[0].auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragSdsl.auftraege[0].auftragTechnikBuilder);

        // N-Draht
        auftragSdsl.auftraege[1].rangierungBuilder = test.getBuilder(RangierungBuilder.class)
                .withEqInBuilder(standortHvt.sdslEquipment[1])
                .withHvtStandortBuilder(standortHvt.standort);
        auftragSdsl.auftraege[1].endstelleBuilder = test.getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(standortHvt.standort)
                .withRangierungBuilder(auftragSdsl.auftraege[1].rangierungBuilder);
        auftragSdsl.auftraege[1].auftragDatenBuilder = test.getBuilder(AuftragDatenBuilder.class)
                .withStatusId(statusId)
                .withAuftragNoOrig(auftragSdsl.auftraege[0].auftragDatenBuilder.getAuftragNoOrig())
                .withProdId(99L);
        auftragSdsl.auftraege[1].auftragTechnikBuilder = test.getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(auftragSdsl.auftraege[1].endstelleBuilder);
        auftragSdsl.auftraege[1].auftragBuilder = test.getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragSdsl.auftraege[1].auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragSdsl.auftraege[1].auftragTechnikBuilder);

        return auftragSdsl;
    }

    public void build(AuftragSdsl auftrag) {
        auftrag.auftraege[0].auftragBuilder.build();
        auftrag.auftraege[1].auftragBuilder.build();
    }
}
