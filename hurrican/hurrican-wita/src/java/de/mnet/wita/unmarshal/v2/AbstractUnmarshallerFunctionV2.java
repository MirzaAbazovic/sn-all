/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.lang.reflect.*;
import java.util.*;
import com.google.common.base.Function;
import org.apache.commons.lang.StringUtils;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderAKMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AufnehmenderProviderType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungProduktType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypAbstractType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProduktpositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.RufnummernblockType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortPersonType;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.ProduktPosition;

@SuppressWarnings("Duplicates")
public abstract class AbstractUnmarshallerFunctionV2<T> implements Function<MeldungstypAbstractType, WitaMessage> {

    @SuppressWarnings("unchecked")
    @Override
    public WitaMessage apply(MeldungstypAbstractType input) {
        return unmarshal((T) input);
    }

    private final Class<T> classToUnmarshall;

    public AbstractUnmarshallerFunctionV2() {
        @SuppressWarnings("unchecked")
        // safe cast for direct concrete subclasses
                Class<T> v = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        classToUnmarshall = v;
    }

    public AbstractUnmarshallerFunctionV2(Class<T> classToUnmarshall) {
        this.classToUnmarshall = classToUnmarshall;
    }

    public abstract WitaMessage unmarshal(T input);

    public Class<T> getClassToUnmarshall() {
        return classToUnmarshall;
    }

    protected LeitungsBezeichnung unmarshalLeitungsbezeichnung(LeitungsbezeichnungType inLbz) {
        if (inLbz == null) {
            return null;
        }

        return new LeitungsBezeichnung(
                inLbz.getLeitungsschluesselzahl(),
                inLbz.getOnkzA(),
                inLbz.getOnkzB(),
                inLbz.getOrdnungsnummer());
    }

    protected Set<MeldungsPosition> unmarshalMeldungspositionen(List<MeldungspositionType> inPositions) {
        Set<MeldungsPosition> returnSet = new HashSet<>();
        for (MeldungspositionType inPosition : inPositions) {
            MeldungsPosition meldungspostion = new MeldungsPosition(inPosition.getMeldungscode(), inPosition.getMeldungstext());
            returnSet.add(meldungspostion);
        }
        return returnSet;
    }

    protected AufnehmenderProvider unmarshalAufnehmenderProvider(AufnehmenderProviderType in) {
        AufnehmenderProvider out = new AufnehmenderProvider();
        if (in instanceof AufnehmenderProviderABMType) {
            AufnehmenderProviderABMType inAbm = (AufnehmenderProviderABMType) in;
            out.setProvidernameAufnehmend(inAbm.getProvidernameAufnehmend());
            out.setUebernahmeDatumVerbindlich(DateConverterUtils.toLocalDate(inAbm.getUebernahmeDatumVerbindlich()));
        }
        if (in instanceof AufnehmenderProviderAKMType) {
            AufnehmenderProviderAKMType inAkm = (AufnehmenderProviderAKMType) in;
            out.setProvidernameAufnehmend(inAkm.getProvidernameAufnehmend());
            out.setUebernahmeDatumGeplant(DateConverterUtils.toLocalDate(inAkm.getUebernahmeDatumGeplant()));
            out.setAntwortFrist(DateConverterUtils.toLocalDate(inAkm.getAntwortfrist()));
        }

        return out;
    }

    protected void mapMeldungPosition(MeldungspositionType inPosition, MeldungsPosition outPosition) {
        outPosition.setMeldungsCode(inPosition.getMeldungscode());
        outPosition.setMeldungsText(inPosition.getMeldungstext());
    }

    protected RufnummernPortierung unmarshalRufnummerPortierung(RufnummernportierungMeldungType in) {
        if (in == null) {
            return null;
        }

        if ((in.getPortierungRufnummern() != null)
                && (in.getPortierungRufnummern().getZuPortierendeOnkzRnr() != null)) {
            RufnummernPortierungEinzelanschluss portierungEinzelanschluss = new RufnummernPortierungEinzelanschluss();
            for (OnkzRufNrType onkzRnr : in.getPortierungRufnummern().getZuPortierendeOnkzRnr()) {
                EinzelanschlussRufnummer einzelanschlussRufnr = new EinzelanschlussRufnummer();
                einzelanschlussRufnr.setOnkz(onkzRnr.getONKZ());
                einzelanschlussRufnr.setRufnummer(onkzRnr.getRufnummer());
                portierungEinzelanschluss.addRufnummer(einzelanschlussRufnr);
            }
            return portierungEinzelanschluss;
        }
        else if ((in.getPortierungRufnummernbloecke() != null)
                && (in.getPortierungRufnummernbloecke().getOnkzDurchwahlAbfragestelle() != null)) {

            RufnummernPortierungAnlagenanschluss portierungAnlagenanschluss = new RufnummernPortierungAnlagenanschluss();

            OnkzDurchwahlAbfragestelleType onkzDirectDial = in.getPortierungRufnummernbloecke()
                    .getOnkzDurchwahlAbfragestelle();
            portierungAnlagenanschluss.setOnkz(onkzDirectDial.getONKZ());
            portierungAnlagenanschluss.setDurchwahl(onkzDirectDial.getDurchwahlnummer());
            portierungAnlagenanschluss.setAbfragestelle(onkzDirectDial.getAbfragestelle());

            if (in.getPortierungRufnummernbloecke().getZuPortierenderRufnummernblock() != null) {
                for (RufnummernblockType block : in.getPortierungRufnummernbloecke()
                        .getZuPortierenderRufnummernblock()) {
                    RufnummernBlock rufnummerBlock = new RufnummernBlock();
                    rufnummerBlock.setVon(block.getRnrBlockVon());
                    rufnummerBlock.setBis(block.getRnrBlockBis());
                    portierungAnlagenanschluss.addRufnummernBlock(rufnummerBlock);
                }
            }
            return portierungAnlagenanschluss;
        }
        return null;
    }

    protected Collection<? extends ProduktPosition> unmarshalProduktpositionen(List<ProduktpositionType> inPositions) {
        List<ProduktPosition> outProduktPositions = new ArrayList<>();

        for (ProduktpositionType inPosition : inPositions) {
            MeldungProduktType inProdukt = inPosition.getProdukt();
            ProduktPosition outProduktPosition = new ProduktPosition(
                    aktionsCode(inPosition),
                    Auftragsposition.ProduktBezeichner.getByProduktName(inProdukt.getBezeichner())
            );
            if (inProdukt.getUebertragungsVerfahren() != null) {
                Uebertragungsverfahren mappedUebertragungsverfahren = Uebertragungsverfahren.valueOf(inProdukt.getUebertragungsVerfahren().value());
                outProduktPosition.setUebertragungsVerfahren(mappedUebertragungsverfahren);
            }
            outProduktPositions.add(outProduktPosition);
        }
        return outProduktPositions;
    }

    protected static AktionsCode aktionsCode(ProduktpositionType input) {
        switch (input.getAktionscode()) {
            case A:
                return AktionsCode.AENDERUNG;
            case W:
                return AktionsCode.WEGFALL;
            case Z:
                return AktionsCode.ZUGANG;
            default:
                throw new IllegalArgumentException("Unknown action code " + input.getAktionscode());
        }
    }

    protected AnsprechpartnerTelekom unmarshallAnsprechpartnerTelekom(AnsprechpartnerBaseType in) {
        if (in == null) {
            return null;
        }
        AnsprechpartnerTelekom out = new AnsprechpartnerTelekom(
                AnredeConverterV2.toMwf(in.getAnrede()),
                in.getVorname(),
                in.getNachname(),
                in.getTelefonnummer()
        );

        String emailAdresse = in.getEmailadresse();
        if (StringUtils.isNotBlank(emailAdresse)) {
            out.setEmailAdresse(emailAdresse);
        }
        return out;
    }

    protected Kundenname unmarshalStandortPerson(StandortPersonType input) {
        if (input == null) {
            return null;
        }

        FirmaType firmaType = input.getFirma();
        if (firmaType != null) {
            Firmenname firmenname = new Firmenname();
            firmenname.setAnrede(AnredeConverterV2.toMwf(firmaType.getAnrede()));
            firmenname.setErsterTeil(firmaType.getFirmenname());
            firmenname.setZweiterTeil(firmaType.getFirmennameZweiterTeil());
            return firmenname;
        }
        PersonType personType = input.getPerson();
        if (personType != null) {
            Personenname personenname = new Personenname();
            personenname.setAnrede(AnredeConverterV2.toMwf(personType.getAnrede()));
            personenname.setNachname(personType.getNachname());
            personenname.setVorname(personType.getVorname());
            return personenname;
        }
        return null;
    }

    protected Collection<? extends Anlage> unmarshalAnlagen(List<AnlageMitTypType> inAnlagen) {
        List<Anlage> out = new ArrayList<>();

        if (inAnlagen != null) {
            for (AnlageMitTypType inAnlage : inAnlagen) {
                String dateiname = inAnlage.getDateiname();
                Dateityp dateityp = Dateityp.fromMimeTyp(inAnlage.getDateityp().value());
                String beschreibung = inAnlage.getBeschreibung();
                byte[] inhalt = inAnlage.getInhalt();
                Anlagentyp anlagentyp = Anlagentyp.from(inAnlage.getAnlagentyp().value());

                out.add(new Anlage(dateiname, dateityp, beschreibung, inhalt, anlagentyp));
            }
        }
        return out;
    }


}
