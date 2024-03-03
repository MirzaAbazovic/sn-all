/*
* Copyright (c) 2014 - M-net Telekommunikations GmbH
* All rights reserved.
* -------------------------------------------------------
* File created: 26.09.2014
*/
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AbgebenderProviderMitAbgabedatumType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProduktpositionType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeABMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABMType.Meldungsattribute> {

    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;
    private LocalDate verbindlicherLiefertermin;
    private AbgebenderProviderMitAbgabedatumType abgebenderProvider;
    private List<ProduktpositionType> positionen;
    private MeldungsattributeTALABMType tal;

    @Override
    public MeldungstypABMType.Meldungsattribute build() {
        MeldungstypABMType.Meldungsattribute meldungsattribute = new MeldungstypABMType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setAbgebenderProvider(abgebenderProvider);
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        meldungsattribute.setVerbindlicherLiefertermin(DateConverterUtils.toXmlGregorianCalendar(verbindlicherLiefertermin));
        if (positionen != null) {
            meldungsattribute.setProduktpositionen(buildProdukpositionen(positionen));
        }
        return meldungsattribute;
    }

    public MeldungsattributeABMTypeBuilder withProduktPositionen(List<ProduktpositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungsattributeABMTypeBuilder addProduktPosition(ProduktpositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    public MeldungsattributeABMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeABMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeABMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public MeldungsattributeABMTypeBuilder withVerbindlicherLiefertermin(LocalDate verbindlicherLiefertermin) {
        this.verbindlicherLiefertermin = verbindlicherLiefertermin;
        return this;
    }

    public MeldungsattributeABMTypeBuilder withAbgebenderProvider(AbgebenderProviderMitAbgabedatumType abgebenderProvider) {
        this.abgebenderProvider = abgebenderProvider;
        return this;
    }

    private MeldungsattributeABMType.Produktpositionen buildProdukpositionen(List<ProduktpositionType> produktpositionTypes) {
        MeldungsattributeABMType.Produktpositionen positionen = new MeldungsattributeABMType.Produktpositionen();
        positionen.getPosition().addAll(produktpositionTypes);
        return positionen;
    }

    public MeldungsattributeABMTypeBuilder withTal(MeldungsattributeTALABMType tal) {
        this.tal = tal;
        return this;
    }

}
