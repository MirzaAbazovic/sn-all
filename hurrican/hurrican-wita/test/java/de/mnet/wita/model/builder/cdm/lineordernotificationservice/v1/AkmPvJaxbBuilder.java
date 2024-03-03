/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;
import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AufnehmenderProviderAKMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DokumenttypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StandortPersonType;
import de.mnet.wita.marshal.v1.AnredeConverter;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;

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
                                .withRnrPortierung(buildRufnummernPortierung())
                                .withVorabstimmungsID(vorabstimmungsId)
                                .build()
                )
                .build();
    }

    private RufnummernportierungMeldungType buildRufnummernPortierung() {
        if (portierung instanceof RufnummernPortierungAnlagenanschluss) {
            RufnummernPortierungAnlagenanschluss anlagenPortierung = (RufnummernPortierungAnlagenanschluss) portierung;

            PortierungRufnummernbloeckeMeldungType rufnummernBlockPortierung = new PortierungRufnummernbloeckeMeldungTypeBuilder()
                    .withOnkzDurchwahlAbfragestelle(
                            new OnkzDurchwahlAbfragestelleTypeBuilder()
                                    .withAbfragestelle(anlagenPortierung.getAbfragestelle())
                                    .withDurchwahlnummer(anlagenPortierung.getDurchwahl())
                                    .withOnkz(anlagenPortierung.getOnkz())
                                    .build()
                    )
                    .build();

            return new RufnummernportierungMeldungTypeBuilder()
                        .withPortierungRufnummernbloecke(rufnummernBlockPortierung)
                        .build();
        }
        if (portierung instanceof RufnummernPortierungEinzelanschluss) {
            RufnummernPortierungEinzelanschluss einzelPortierung = (RufnummernPortierungEinzelanschluss) portierung;

            PortierungRufnummernMeldungTypeBuilder portierungRufnummernMeldungTypeBuilder = new PortierungRufnummernMeldungTypeBuilder();

            for (EinzelanschlussRufnummer rufNr : einzelPortierung.getRufnummern()) {
                OnkzRufNrType onkzRufNrType = new OnkzRufNrTypeBuilder()
                        .withOnkz(rufNr.getOnkz())
                        .withRufnummer(rufNr.getRufnummer())
                        .build();
                portierungRufnummernMeldungTypeBuilder.addZuPortierendeOnkzRnr(onkzRufNrType);
            }

            return new RufnummernportierungMeldungTypeBuilder()
                        .withPortierungRufnummern(portierungRufnummernMeldungTypeBuilder.build())
                        .build();
        }
        return null;
    }

    private AufnehmenderProviderAKMType buildAufnehmenderProvider() {
        if (aufnehmenderProvider == null) {
            aufnehmenderProvider = new AufnehmenderProvider();
            aufnehmenderProvider.setProvidernameAufnehmend("o2");
            aufnehmenderProvider.setAntwortFrist(LocalDate.now().plusDays(7));
            aufnehmenderProvider.setUebernahmeDatumGeplant(LocalDate.now().plusDays(14));
            aufnehmenderProvider.setUebernahmeDatumVerbindlich(LocalDate.now().plusDays(14));
        }
        AufnehmenderProviderAKMType aufnehmenderProviderAKMType = new AufnehmenderProviderAKMTypeBuilder()
                .withAntwortfrist(aufnehmenderProvider.getAntwortFrist())
                .withUebernahmeDatumGeplant(aufnehmenderProvider.getUebernahmeDatumGeplant())
                .withProvidernameAufnehmend(aufnehmenderProvider.getProvidernameAufnehmend())
                .build();
        return aufnehmenderProviderAKMType;
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
        person.setAnrede(AnredeConverter.toWita(input.getAnrede(), false));
        person.setNachname(input.getNachname());
        person.setVorname(input.getVorname());
        return person;
    }

    private FirmaType firma(Firmenname input) {
        FirmaType firma = new FirmaType();
        firma.setAnrede(AnredeConverter.toWita(input.getAnrede(), true));
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

    public AkmPvJaxbBuilder withRufnummernPortierung(RufnummernPortierung portierung) {
        this.portierung = portierung;
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

    AkmPvJaxbBuilder addAnlage(String dateiname, DokumenttypType dateityp, String beschreibung, byte[] inhalt,
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
