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
 * AuftragFtthSuFBuilder
 */
public class AuftragFtthSuFBuilder implements HigherOrderBuilder<StandortFtthBuilder.StandortFtth> {

    public class AuftragFtthSuF extends AuftragWithCb {
        public RangierungBuilder rangierungBuilder;
    }

    private Long prodId = 513L;
    private Long statusId = AuftragStatus.IN_BETRIEB;

    public AuftragFtthSuFBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        return this;
    }

    public AuftragFtthSuFBuilder withStatusId(Long statusId) {
        this.statusId = statusId;
        return this;
    }

    @Override
    public AuftragFtthSuF prepare(AbstractHurricanBaseServiceTest test, StandortFtthBuilder.StandortFtth standortFtth) {
        final AuftragFtthSuF auftrag = new AuftragFtthSuF();
        auftrag.rangierungBuilder = test.getBuilder(RangierungBuilder.class)
                .withEqInBuilder(standortFtth.ontEthPorts[0])
                .withHvtStandortBuilder(standortFtth.standortOnt);
        auftrag.endstelleBuilder = test.getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withHvtStandortBuilder(standortFtth.standortOnt)
                .withRangierungBuilder(auftrag.rangierungBuilder);
        auftrag.auftragDatenBuilder = test.getBuilder(AuftragDatenBuilder.class)
                .withStatusId(statusId)
                .withProdId(prodId);
        auftrag.auftragTechnikBuilder = test.getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(auftrag.endstelleBuilder);
        auftrag.auftragBuilder = test.getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftrag.auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftrag.auftragTechnikBuilder);

        return auftrag;
    }

    public void build(AuftragFtthSuF auftrag) {
        auftrag.auftragBuilder.build();
    }
}
