/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeERLMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALERLMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERLMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProduktpositionType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeERLMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERLMType.Meldungsattribute> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;
    private LocalDate erledigungstermin;
    private List<ProduktpositionType> positionen;
    private MeldungsattributeTALERLMType tal;

    @Override
    public MeldungstypERLMType.Meldungsattribute build() {
        MeldungstypERLMType.Meldungsattribute meldungsattribute = new MeldungstypERLMType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        meldungsattribute.setErledigungstermin(DateConverterUtils.toXmlGregorianCalendar(erledigungstermin));
        if (positionen != null) {
            meldungsattribute.setProduktpositionen(buildProdukpositionen(positionen));
        }
        return meldungsattribute;
    }

    public MeldungsattributeERLMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder withErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder withProduktPositionen(List<ProduktpositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungsattributeERLMTypeBuilder addProduktPosition(ProduktpositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungsattributeERLMType.Produktpositionen buildProdukpositionen(List<ProduktpositionType> produktpositionTypes) {
        MeldungsattributeERLMType.Produktpositionen positionen = new MeldungsattributeERLMType.Produktpositionen();
        positionen.getPosition().addAll(produktpositionTypes);
        return positionen;
    }

    public MeldungsattributeERLMTypeBuilder withTal(MeldungsattributeTALERLMType tal) {
        this.tal = tal;
        return this;
    }

}
