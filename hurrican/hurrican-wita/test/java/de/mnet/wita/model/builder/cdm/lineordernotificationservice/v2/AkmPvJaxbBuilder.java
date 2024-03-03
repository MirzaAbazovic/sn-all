/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderAKMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DokumenttypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortPersonType;
import de.mnet.wita.marshal.v2.AnredeConverterV2;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;

@SuppressWarnings({ "Duplicates", "unused" })
public class AkmPvJaxbBuilder extends MeldungWithProduktpositionJaxbBuilder<AkmPvJaxbBuilder> {

    private Leitung leitung;
    private String anschlussOnkz;
    private String anschlussRufnummer;
    private AufnehmenderProvider aufnehmenderProvider;
    private RufnummernPortierung portierung;
    private Kundenname endkunde;
    private final Set<AnlageMitTypType> anlagen = new HashSet<>();
    private String vorabstimmungsId;

    public MeldungstypAKMPVType build() {
        return new MeldungstypAKMPVTypeBuilder()
                .withMeldungsattribute(buildMeldungsAttribute())
                .withPositionen(meldungspositionen)
                .build();
    }

    private MeldungstypAKMPVType.Meldungsattribute buildMeldungsAttribute() {
        return new MeldungsattributeAKMPVTypeBuilder()
                .withKundennummer(kundenNummer)
                .withVertragsnummer(vertragsNummer)
                .withAufnehmderProvider(buildAufnehmenderProvider())
                .withEndkunde(buildEndkunde())
                .withAnlagen(new ArrayList<>(anlagen))
                .withTal(
                        new MeldungsattributeTALAKMPVTypeBuilder()
                                .withLeitung(buildLeitung(leitung))
                                .withAnschluss(buildAnschluss(anschlussOnkz, anschlussRufnummer))
                                .withVorabstimmungsID(vorabstimmungsId)
                                .build()
                )
                .build();
    }

    private AufnehmenderProviderAKMType buildAufnehmenderProvider() {
        if (aufnehmenderProvider == null) {
            aufnehmenderProvider = new AufnehmenderProvider();
            aufnehmenderProvider.setProvidernameAufnehmend("o2");
            aufnehmenderProvider.setAntwortFrist(LocalDate.now().plusDays(7));
            aufnehmenderProvider.setUebernahmeDatumGeplant(LocalDate.now().plusDays(14));
            aufnehmenderProvider.setUebernahmeDatumVerbindlich(LocalDate.now().plusDays(14));
        }
        return new AufnehmenderProviderAKMTypeBuilder()
                .withAntwortfrist(aufnehmenderProvider.getAntwortFrist())
                .withUebernahmeDatumGeplant(aufnehmenderProvider.getUebernahmeDatumGeplant())
                .withProvidernameAufnehmend(aufnehmenderProvider.getProvidernameAufnehmend())
                .build();
    }

    private StandortPersonType buildEndkunde() {
        if (endkunde == null) {
            return null;
        }

        StandortPersonTypeBuilder result = new StandortPersonTypeBuilder();
        if (endkunde instanceof Firmenname) {
            result.withFirma(firma((Firmenname) endkunde));
        }
        else {
            result.withPerson(person((Personenname) endkunde));
        }
        return result.build();
    }

    private PersonType person(Personenname input) {
        PersonType person = new PersonType();
        person.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), false));
        person.setNachname(input.getNachname());
        person.setVorname(input.getVorname());
        return person;
    }

    private FirmaType firma(Firmenname input) {
        FirmaType firma = new FirmaType();
        firma.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), true));
        firma.setFirmenname(input.getErsterTeil());
        firma.setFirmennameZweiterTeil(input.getZweiterTeil());
        return firma;
    }

    public AkmPvJaxbBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }

    public AkmPvJaxbBuilder withAnschluss(String onkz, String rufnummer) {
        this.anschlussOnkz = onkz;
        this.anschlussRufnummer = rufnummer;
        return this;
    }

    public AkmPvJaxbBuilder withAufnehmenderProvider(AufnehmenderProvider aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
        return this;
    }

    public AkmPvJaxbBuilder withEndkunde(Kundenname endkunde) {
        this.endkunde = endkunde;
        return this;
    }

    public AkmPvJaxbBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public AkmPvJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String beschreibung, String inhalt,
            AnlagentypType anlagentyp) {
        return addAnlage(dateiname, dateityp, beschreibung, inhalt.getBytes(), anlagentyp);
    }

    private AkmPvJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String beschreibung, byte[] inhalt,
            AnlagentypType anlagentyp) {
        AnlageMitTypType anlageMitTyp = new AnlageMitTypTypeBuilder()
                .withAnlagentyp(anlagentyp)
                .withDateiname(dateiname)
                .withDateityp(dateityp)
                .withBeschreibung(beschreibung)
                .withInhalt(inhalt)
                .build();
        anlagen.add(anlageMitTyp);
        return this;
    }

}
