/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.14
 */
package de.mnet.wita.acceptance.common;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.helper.WbciRequestStatusHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.LeitungTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

/**
 *
 */
public class WbciTestDataBuilder {

    private WbciDao wbciDao;
    private String vorabstimmungsId;
    private CarrierCode abgebenderEKP;
    private Set<Leitung> akmTrLeitungen;
    private Set<TechnischeRessource> ruemVaTechnicheRessourcen;
    private LocalDate wechseltermin;
    private Technologie ruemVaTechnologie = Technologie.TAL_ISDN;

    private RueckmeldungVorabstimmung ruemVa;
    private UebernahmeRessourceMeldung akmTr;

    public WbciTestDataBuilder(WbciDao wbciDao) {
        this.wbciDao = wbciDao;
        this.vorabstimmungsId = createNextWbciVorabstimmungsId();
        akmTrLeitungen = new HashSet<>();
        ruemVaTechnicheRessourcen = new HashSet<>();
    }

    public String getWbciVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public LocalDate getWechseltermin() {
        return wechseltermin;
    }

    public WbciTestDataBuilder withWbciVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public WbciTestDataBuilder withWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public WbciTestDataBuilder addAkmTrLeitung(Leitung leitung) {
        akmTrLeitungen.add(leitung);
        return this;
    }

    public WbciTestDataBuilder withAbgebenderEKP(CarrierCode carrierCode) {
        abgebenderEKP = carrierCode;
        return this;
    }

    public WbciTestDataBuilder withRuemVaTechnologie(Technologie ruemVaTechnologie) {
        this.ruemVaTechnologie = ruemVaTechnologie;
        return this;
    }

    private String createPreAgreementId(RequestTyp requestTyp) {
        String nextValueWithLeadingZeros = String.format("%08d", wbciDao.getNextSequenceValue(requestTyp));

        // z.B. 'DEU.MNET.VH12345678'
        return String.format("%s.%s%s%s",
                CarrierCode.MNET.getITUCarrierCode(),
                requestTyp.getPreAgreementIdCode(),
                WbciConstants.VA_ROUTING_PREFIX_HURRICAN,
                nextValueWithLeadingZeros);
    }

    /**
     * @return die nächste gültige WBCI-Vorabstimmungs-ID
     */
    protected String createNextWbciVorabstimmungsId() {
        return createPreAgreementId(RequestTyp.VA);
    }

    /**
     * Setzt die Taifun-Auftrags-Nr. und die Hurrican-Auftrags-Nr. für alle betroffenen WBCI-Vorabstimmung auf NULL
     * zurück.
     *
     * @param billingOderNo
     */
    public void cleanActiveWbciVorabstimmung(long billingOderNo) {
        List<WbciGeschaeftsfall> gfList = wbciDao.findActiveGfByOrderNoOrig(billingOderNo, true);
        for (WbciGeschaeftsfall gf : gfList) {
            gf.setBillingOrderNoOrig(null);
            gf.setAuftragId(null);
            wbciDao.store(gf);
        }
    }

    /**
     * Erstellt in Abhängigkeit des Builder Objekts, eine gültige WBCI-Vorabstimmung.
     *
     * @param builder vorbefüllter Acc-Test-Builder
     * @param wbciCdmVersion CDM-Version für die WBCI-VA
     * @param wbciGfTyp Geschäftsfall für die WBCI-VA
     * @param witaGfTyp WITA-Geschäftsfall
     * @return die erzeugte WBCI-Vorabstimmung
     */
    public VorabstimmungsAnfrage createWbciVorabstimmungForAccpetanceTestBuilder(AcceptanceTestDataBuilder builder,
            final WbciCdmVersion wbciCdmVersion, final GeschaeftsfallTyp wbciGfTyp,
            final de.mnet.wita.message.GeschaeftsfallTyp witaGfTyp) {
        final Long auftragNoOrig = builder.getTaifunData().getBillingAuftrag().getAuftragNoOrig();

        cleanActiveWbciVorabstimmung(auftragNoOrig);
        VorabstimmungsAnfrage<? extends WbciGeschaeftsfall> wbciVorabstimmung = new VorabstimmungsAnfrageTestBuilder<>()
                .buildValid(wbciCdmVersion, wbciGfTyp);
        WbciGeschaeftsfall wbciGf = wbciVorabstimmung.getWbciGeschaeftsfall();
        wbciGf.setVorabstimmungsId(vorabstimmungsId);
        wbciGf.setBillingOrderNoOrig(auftragNoOrig);
        wbciVorabstimmung.setRequestStatus(WbciRequestStatusHelper.getActiveWbciRequestStatus(witaGfTyp));

        if (wechseltermin == null) {
            wechseltermin = builder.getVorgabeMnet().toLocalDate();
        }
        wbciGf.setWechseltermin(wechseltermin);

        if (abgebenderEKP != null) {
            wbciGf.setAbgebenderEKP(abgebenderEKP);
        }

        wbciDao.store(wbciGf);
        wbciDao.store(wbciVorabstimmung);

        // create RUEM-VA and AKM-TR if Rufummern have been set
        List<Rufnummer> wbciRufnummern4Portierung = builder.getTaifunData().getDialNumbers();
        if (wbciRufnummern4Portierung != null) {
            // RUEM-VA
            Iterator<Rufnummer> iterator = wbciRufnummern4Portierung.iterator();
            Rufnummernportierung rufnummernportierung = null;
            if (iterator.hasNext()) {
                Rufnummer firstRn = iterator.next();
                if (firstRn.isBlock()) {
                    RufnummernportierungAnlageTestBuilder anlagenRnpBuilder = new RufnummernportierungAnlageTestBuilder()
                            .withOnkz(firstRn.getOnKzWithoutLeadingZeros())
                            .withAbfragestelle(firstRn.getDnBase())
                            .withDurchwahlnummer(firstRn.getDirectDial())
                            .addRufnummernblock(createRufnummernBlock(firstRn, wbciCdmVersion, wbciGfTyp));
                    while (iterator.hasNext()) {
                        anlagenRnpBuilder.addRufnummernblock(createRufnummernBlock(iterator.next(), wbciCdmVersion,
                                wbciGfTyp));
                    }
                    rufnummernportierung = anlagenRnpBuilder.buildValid(wbciCdmVersion, wbciGfTyp);
                }
                else {
                    rufnummernportierung = new RufnummernportierungEinzelnTestBuilder()
                            .withRufnummerOnkzs(
                                    createRufnummernEinzeln(wbciRufnummern4Portierung, wbciCdmVersion, wbciGfTyp))
                            .buildValid(wbciCdmVersion, wbciGfTyp);
                }
            }
            ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                    .withWbciGeschaeftsfall(wbciGf)
                    .withRufnummernportierung(rufnummernportierung)
                    .withWechseltermin(wbciGf.getWechseltermin())
                    .withTechnologie(ruemVaTechnologie)
                    .withTechnischeRessourcen(ruemVaTechnicheRessourcen)
                    .buildValid(wbciCdmVersion, wbciGfTyp);
            ruemVa.setAbsender(CarrierCode.DTAG);
            wbciDao.store(ruemVa);
            if (wbciGf instanceof RufnummernportierungAware) {
                ((RufnummernportierungAware) wbciGf)
                        .setRufnummernportierung(rufnummernportierung);
                wbciDao.store(wbciGf);
            }

            // AKM-TR
            if (akmTrLeitungen.isEmpty()) {
                akmTrLeitungen.add(new LeitungTestBuilder().withVertragsnummer(builder.getVorabstimmungsVertragsNr())
                        .build());
            }
            akmTr = new UebernahmeRessourceMeldungTestBuilder()
                    .withWbciGeschaeftsfall(wbciGf)
                    .withUebernahme(true)
                    .withLeitungen(akmTrLeitungen)
                    .buildValid(wbciCdmVersion, wbciGfTyp);
            wbciDao.store(akmTr);
        }
        wbciDao.flushSession();

        return wbciVorabstimmung;
    }

    private List<RufnummerOnkz> createRufnummernEinzeln(List<Rufnummer> rufnummerList, WbciCdmVersion wbciCdmVersion,
            de.mnet.wbci.model.GeschaeftsfallTyp gfTyp) {
        List<RufnummerOnkz> result = new ArrayList<>();
        for (Rufnummer rn : rufnummerList) {
            result.add(new RufnummerOnkzTestBuilder().withOnkz(rn.getOnKzWithoutLeadingZeros())
                    .withRufnummer(rn.getDnBase()).buildValid(wbciCdmVersion, gfTyp));
        }
        return result;
    }

    private Rufnummernblock createRufnummernBlock(Rufnummer rn, WbciCdmVersion wbciCdmVersion,
            de.mnet.wbci.model.GeschaeftsfallTyp gfTyp) {
        return new RufnummernblockTestBuilder().withRnrBlockVon(rn.getRangeFrom()).withRnrBlockBis(rn.getRangeTo())
                .buildValid(wbciCdmVersion, gfTyp);
    }

    public RueckmeldungVorabstimmung getRuemVa() {
        return ruemVa;
    }

    public UebernahmeRessourceMeldung getAkmTr() {
        return akmTr;
    }

}
