package de.augustakom.hurrican.model.tools;

import java.util.*;

import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * CarrierbestellungBuilder
 */
public class CarrierbestellungBuilder implements HigherOrderBuilder<HigherOrderBuilder.AuftragWithCb> {

    private Date kuendigungAm = new Date();

    public CarrierbestellungBuilder withKuendigungAm(Date kuendigungAm) {
        this.kuendigungAm = kuendigungAm;
        return this;
    }

    @Override
    public AuftragWithCb prepare(AbstractHurricanBaseServiceTest test, AuftragWithCb auftragWithCb) {
        auftragWithCb.carrierbestellung2EndstelleBuilder = test.getBuilder(Carrierbestellung2EndstelleBuilder.class);
        auftragWithCb.endstelleBuilder.withCb2EsBuilder(auftragWithCb.carrierbestellung2EndstelleBuilder);
        auftragWithCb.carrierbestellungBuilder = test.getBuilder(de.augustakom.hurrican.model.cc.CarrierbestellungBuilder.class)
                .withCb2EsBuilder(auftragWithCb.carrierbestellung2EndstelleBuilder)
                .withKuendBestaetigungCarrier(kuendigungAm);
        return auftragWithCb;
    }

    public void build(AuftragWithCb auftragWithCb) {
        auftragWithCb.carrierbestellungBuilder.build();
    }
}
