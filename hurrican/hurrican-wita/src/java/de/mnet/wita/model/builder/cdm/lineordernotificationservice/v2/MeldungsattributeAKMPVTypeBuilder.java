/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderAKMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortPersonType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
@SuppressWarnings("Duplicates")
public class MeldungsattributeAKMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypAKMPVType.Meldungsattribute> {

    private String vertragsnummer;
    private String kundennummer;
    private AufnehmenderProviderAKMType aufnehmenderProvider;
    private StandortPersonType endkunde;
    private List<AnlageMitTypType> anlagen;
    private MeldungsattributeTALAKMPVType tal;

    @Override
    public MeldungstypAKMPVType.Meldungsattribute build() {
        MeldungstypAKMPVType.Meldungsattribute meldungsattribute = new MeldungstypAKMPVType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setAufnehmenderProvider(aufnehmenderProvider);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setEndkunde(endkunde);
        if (anlagen != null) {
            meldungsattribute.setAnlagen(buildAnlagen(anlagen));
        }
        return meldungsattribute;
    }

    public MeldungsattributeAKMPVTypeBuilder withTal(MeldungsattributeTALAKMPVType tal) {
        this.tal = tal;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder withAufnehmderProvider(AufnehmenderProviderAKMType aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder withEndkunde(StandortPersonType endkunde) {
        this.endkunde = endkunde;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder withAnlagen(List<AnlageMitTypType> anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public MeldungsattributeAKMPVTypeBuilder addAnlage(AnlageMitTypType anlage) {
        if (this.anlagen == null) {
            this.anlagen = new ArrayList<>();
        }
        this.anlagen.add(anlage);
        return this;
    }

    private MeldungsattributeAKMPVType.Anlagen buildAnlagen(List<AnlageMitTypType> anlagenList) {
        MeldungsattributeAKMPVType.Anlagen anlagen = new MeldungsattributeAKMPVType.Anlagen();
        anlagen.getAnlage().addAll(anlagenList);
        return anlagen;
    }

}
