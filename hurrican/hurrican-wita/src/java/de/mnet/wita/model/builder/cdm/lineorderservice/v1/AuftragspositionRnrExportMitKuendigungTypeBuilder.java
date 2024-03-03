/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionRnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalPositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALRnrExportMitKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragspositionRnrExportMitKuendigungTypeBuilder implements LineOrderTypeBuilder<AuftragspositionRnrExportMitKuendigungType> {

    private ProduktType produkt;
    private List<AuftragspositionType> position;
    private TALRnrExportMitKuendigungType tal;

    @Override
    public AuftragspositionRnrExportMitKuendigungType build() {
        AuftragspositionRnrExportMitKuendigungType produktgruppenwechsel =
                new AuftragspositionRnrExportMitKuendigungType();
        produktgruppenwechsel.setProdukt(produkt);
        if (position != null) {
            produktgruppenwechsel.getPosition().addAll(position);
        }
        if (tal != null) {
            AuftragspositionRnrExportMitKuendigungType.GeschaeftsfallProdukt geschaeftsfallProdukt =
                    new AuftragspositionRnrExportMitKuendigungType.GeschaeftsfallProdukt();
            geschaeftsfallProdukt.setTAL(tal);
            produktgruppenwechsel.setGeschaeftsfallProdukt(geschaeftsfallProdukt);
        }
        return produktgruppenwechsel;
    }

    public AuftragspositionRnrExportMitKuendigungTypeBuilder withProdukt(ProduktType produkt) {
        this.produkt = produkt;
        return this;
    }

    public AuftragspositionRnrExportMitKuendigungTypeBuilder withPosition(List<AuftragspositionType> position) {
        this.position = position;
        return this;
    }

    public AuftragspositionRnrExportMitKuendigungTypeBuilder withTal(TALRnrExportMitKuendigungType tal) {
        this.tal = tal;
        return this;
    }

    public AuftragspositionRnrExportMitKuendigungTypeBuilder addPosition(LeistungsmerkmalPositionType position) {
        if (this.position == null) {
            this.position = new ArrayList<>();
        }
        this.position.add(position);
        return this;
    }

}
