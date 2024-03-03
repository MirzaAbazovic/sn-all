/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.ProduktpositionType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeENTMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypENTMType.Meldungsattribute> {

    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;
    private LocalDate entgelttermin;
    private List<ProduktpositionType> positionen;
    private List<AnlageMitTypType> anlagen;
    private String vertragsnummer;

    @Override
    public MeldungstypENTMType.Meldungsattribute build() {
        MeldungstypENTMType.Meldungsattribute meldungsattribute = new MeldungstypENTMType.Meldungsattribute();
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        meldungsattribute.setEntgelttermin(DateConverterUtils.toXmlGregorianCalendar(entgelttermin));
        if (positionen != null) {
            meldungsattribute.setProduktpositionen(buildProdukpositionen(positionen));
        }
        if (anlagen != null) {
            meldungsattribute.setAnlagen(buildAnlagen(anlagen));
        }
        return meldungsattribute;
    }

    public MeldungsattributeENTMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder withEntgelttermin(LocalDate entgelttermin) {
        this.entgelttermin = entgelttermin;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder withProduktPositionen(List<ProduktpositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder addProduktPosition(ProduktpositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungsattributeENTMType.Produktpositionen buildProdukpositionen(List<ProduktpositionType> produktpositionTypes) {
        MeldungsattributeENTMType.Produktpositionen positionen = new MeldungsattributeENTMType.Produktpositionen();
        positionen.getPosition().addAll(produktpositionTypes);
        return positionen;
    }

    public MeldungsattributeENTMTypeBuilder withAnlagen(List<AnlageMitTypType> anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public MeldungsattributeENTMTypeBuilder addAnlage(AnlageMitTypType anlage) {
        if (this.anlagen == null) {
            this.anlagen = new ArrayList<>();
        }
        this.anlagen.add(anlage);
        return this;
    }

    private MeldungsattributeENTMType.Anlagen buildAnlagen(List<AnlageMitTypType> anlagenList) {
        MeldungsattributeENTMType.Anlagen anlagen = new MeldungsattributeENTMType.Anlagen();
        anlagen.getAnlage().addAll(anlagenList);
        return anlagen;
    }

}
